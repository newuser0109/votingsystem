import os
from datetime import datetime, timedelta
from typing import Optional

from google.auth.transport.requests import Request
from google.oauth2.credentials import Credentials
from google_auth_oauthlib.flow import InstalledAppFlow
from googleapiclient.discovery import build

from .config import (
    FOLLOW_UP_DAYS,
    GOOGLE_CREDENTIALS_FILE,
    GOOGLE_SCOPES,
    GOOGLE_TOKEN_FILE,
)
from .models import Job


def _get_calendar_service():
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
    return build("calendar", "v3", credentials=creds)


def _next_business_day(start: datetime, n: int) -> datetime:
    """Return the date n business days after start."""
    current = start
    days_added = 0
    while days_added < n:
        current += timedelta(days=1)
        if current.weekday() < 5:  # Mon–Fri
            days_added += 1
    return current


class CalendarScheduler:
    def __init__(self):
        self._service = None

    @property
    def service(self):
        if self._service is None:
            self._service = _get_calendar_service()
        return self._service

    def schedule_followup(self, job: Job, applied_date: Optional[str] = None) -> str:
        base = datetime.fromisoformat(applied_date) if applied_date else datetime.now()
        follow_up_dt = _next_business_day(base, FOLLOW_UP_DAYS)

        start = follow_up_dt.replace(hour=9, minute=0, second=0, microsecond=0)
        end = start + timedelta(minutes=30)

        event = {
            "summary": f"Follow up: {job.title} @ {job.company}",
            "description": (
                f"Follow up on your application for {job.title} at {job.company}.\n"
                f"Application link: {job.url}\n"
                f"Location: {job.location}"
            ),
            "start": {"dateTime": start.isoformat(), "timeZone": "America/New_York"},
            "end":   {"dateTime": end.isoformat(),   "timeZone": "America/New_York"},
            "reminders": {
                "useDefault": False,
                "overrides": [
                    {"method": "email",  "minutes": 24 * 60},
                    {"method": "popup",  "minutes": 30},
                ],
            },
            "colorId": "7",  # Peacock blue
        }

        created = self.service.events().insert(calendarId="primary", body=event).execute()
        return created["id"]
