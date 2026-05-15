"""Send a single HTML digest email with all job listings."""

import base64
import os
from datetime import date
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
from typing import Optional

from google.auth.transport.requests import Request
from google.oauth2.credentials import Credentials
from google_auth_oauthlib.flow import InstalledAppFlow
from googleapiclient.discovery import build

from .config import (
    CANDIDATE_EMAIL, GOOGLE_CREDENTIALS_FILE, GOOGLE_SCOPES, GOOGLE_TOKEN_FILE,
)
from .search import Job


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


_STYLE = """
<style>
body{font-family:Arial,sans-serif;font-size:14px;color:#222;max-width:960px;margin:auto;padding:20px}
h1{color:#1a5276;border-bottom:2px solid #1a5276;padding-bottom:8px}
h2{color:#2874a6;margin-top:28px}
table{width:100%;border-collapse:collapse;margin-top:8px}
th{background:#1a5276;color:#fff;padding:8px 10px;text-align:left;font-size:13px}
td{padding:7px 10px;border-bottom:1px solid #ddd;font-size:13px;vertical-align:top}
tr:nth-child(even){background:#f4f6f9}
a{color:#1a5276;text-decoration:none;font-weight:bold}
a:hover{text-decoration:underline}
.footer{color:#888;font-size:12px;margin-top:28px;border-top:1px solid #ddd;padding-top:10px}
</style>
"""


def _rows(jobs: list[Job], show_location: bool = False) -> str:
    html = ""
    for i, j in enumerate(jobs, 1):
        loc_col = f"<td>{j.location}</td>" if show_location else ""
        html += (
            f"<tr>"
            f"<td>{i}</td>"
            f"<td><a href='{j.url}'>{j.title}</a></td>"
            f"<td>{j.company}</td>"
            f"{loc_col}"
            f"<td>{j.posted_date}</td>"
            f"<td>{j.board}</td>"
            f"</tr>"
        )
    return html


def build_html(remote_jobs: list[Job], local_jobs: list[Job]) -> str:
    today = date.today().strftime("%B %d, %Y")
    total = len(remote_jobs) + len(local_jobs)

    remote_hdr = "<tr><th>#</th><th>Job Title</th><th>Company</th><th>Posted</th><th>Board</th></tr>"
    local_hdr  = "<tr><th>#</th><th>Job Title</th><th>Company</th><th>Location</th><th>Posted</th><th>Board</th></tr>"

    return f"""<!DOCTYPE html><html><head><meta charset='UTF-8'>{_STYLE}</head><body>
<h1>Job Search Results — {today}</h1>
<p><strong>{total} matching roles</strong> found across <strong>Indeed</strong> and <strong>Dice</strong><br>
Titles searched: Software Engineering Manager · Engineering Manager · Software Development Manager ·
Director of Software Engineering · IT Manager · Delivery Manager · VP Engineering · Head of Engineering<br>
Locations: Remote US &amp; Atlanta/Georgia area &nbsp;|&nbsp; Duplicates removed across all boards</p>

<h2>Remote Positions ({len(remote_jobs)})</h2>
<table>{remote_hdr}{_rows(remote_jobs)}</table>

<h2>Atlanta / Georgia Area ({len(local_jobs)})</h2>
<table>{local_hdr}{_rows(local_jobs, show_location=True)}</table>

<div class='footer'>
<strong>Note:</strong> These results were retrieved using AI-powered search.
Please review all job details carefully and verify information directly with employers before applying.
</div>
</body></html>"""


def send_digest(remote_jobs: list[Job], local_jobs: list[Job]) -> str:
    """Creates a Gmail draft (or sends) with the job digest. Returns the draft ID."""
    today = date.today().strftime("%B %d, %Y")
    total = len(remote_jobs) + len(local_jobs)
    subject = f"Job Search Results — {total} Matching Roles (Remote + Atlanta) | {today}"

    html = build_html(remote_jobs, local_jobs)

    msg = MIMEMultipart("alternative")
    msg["To"]      = CANDIDATE_EMAIL
    msg["Subject"] = subject
    msg.attach(MIMEText(
        f"{total} jobs found. {len(remote_jobs)} remote, {len(local_jobs)} Atlanta area. "
        "See the HTML version for clickable links.",
        "plain",
    ))
    msg.attach(MIMEText(html, "html"))

    raw = base64.urlsafe_b64encode(msg.as_bytes()).decode()
    service = _get_gmail_service()
    draft = service.users().drafts().create(userId="me", body={"message": {"raw": raw}}).execute()
    return draft["id"]
