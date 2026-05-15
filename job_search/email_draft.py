import base64
import os
from email.mime.text import MIMEText
from typing import Optional

from google.auth.transport.requests import Request
from google.oauth2.credentials import Credentials
from google_auth_oauthlib.flow import InstalledAppFlow
from googleapiclient.discovery import build

from .config import (
    GOOGLE_CREDENTIALS_FILE,
    GOOGLE_SCOPES,
    GOOGLE_TOKEN_FILE,
    PROFILE,
)
from .models import AnalysisResult, Job


def _get_gmail_service():
    creds: Optional[Credentials] = None
    if os.path.exists(GOOGLE_TOKEN_FILE):
        creds = Credentials.from_authorized_user_file(GOOGLE_TOKEN_FILE, GOOGLE_SCOPES)
    if not creds or not creds.valid:
        if creds and creds.expired and creds.refresh_token:
            creds.refresh(Request())
        else:
            flow = InstalledAppFlow.from_client_secrets_file(GOOGLE_CREDENTIALS_FILE, GOOGLE_SCOPES)
            creds = flow.run_local_server(port=0)
        with open(GOOGLE_TOKEN_FILE, "w") as f:
            f.write(creds.to_json())
    return build("gmail", "v1", credentials=creds)


def _build_email_html(job: Job, analysis: AnalysisResult) -> str:
    paragraphs = "".join(f"<p>{p.strip()}</p>" for p in analysis.cover_letter.split("\n\n") if p.strip())
    strengths_html = "".join(f"<li>{s}</li>" for s in analysis.strengths)
    return f"""
<html><body style="font-family: Arial, sans-serif; max-width: 700px; margin: auto; color: #222;">
<h2 style="color:#1a5276;">Application: {job.title} — {job.company}</h2>
<p><strong>Role:</strong> {job.title}<br>
<strong>Company:</strong> {job.company}<br>
<strong>Location:</strong> {job.location}<br>
<strong>Compensation:</strong> {job.compensation or 'Not listed'}<br>
<strong>Fit Score:</strong> {analysis.fit_score}/100<br>
<strong>Apply:</strong> <a href="{job.url}">{job.url}</a></p>
<hr>
<h3>Cover Letter</h3>
{paragraphs}
<hr>
<h3>Why This Is a Strong Match</h3>
<ul>{strengths_html}</ul>
</body></html>
"""


class GmailDrafter:
    def __init__(self):
        self._service = None

    @property
    def service(self):
        if self._service is None:
            self._service = _get_gmail_service()
        return self._service

    def create_draft(self, job: Job, analysis: AnalysisResult) -> str:
        subject = f"Application: {job.title} at {job.company}"
        html_body = _build_email_html(job, analysis)
        plain_body = (
            f"Role: {job.title}\nCompany: {job.company}\n"
            f"Location: {job.location}\nFit Score: {analysis.fit_score}/100\n"
            f"Apply: {job.url}\n\n"
            f"--- COVER LETTER ---\n\n{analysis.cover_letter}"
        )

        msg = MIMEText(plain_body, "plain")
        msg["To"] = PROFILE["email"]
        msg["Subject"] = subject

        raw = base64.urlsafe_b64encode(msg.as_bytes()).decode()
        draft = self.service.users().drafts().create(
            userId="me",
            body={"message": {"raw": raw}},
        ).execute()
        return draft["id"]
