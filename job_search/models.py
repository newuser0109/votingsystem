from dataclasses import dataclass, field
from datetime import datetime
from enum import Enum


class ApplicationStatus(Enum):
    NEW = "new"
    DRAFTED = "drafted"
    APPLIED = "applied"
    FOLLOWED_UP = "followed_up"
    INTERVIEWING = "interviewing"
    OFFER = "offer"
    REJECTED = "rejected"
    WITHDRAWN = "withdrawn"


@dataclass
class Job:
    id: str
    title: str
    company: str
    location: str
    url: str
    compensation: str = ""
    description: str = ""
    posted_date: str = ""
    job_type: str = ""
    source: str = "indeed"


@dataclass
class AnalysisResult:
    job_id: str
    fit_score: int  # 0-100
    strengths: list
    gaps: list
    key_requirements: list
    cover_letter: str
    summary: str


@dataclass
class Application:
    job_id: str
    title: str
    company: str
    url: str
    status: ApplicationStatus = ApplicationStatus.NEW
    fit_score: int = 0
    applied_date: str = ""
    draft_id: str = ""
    calendar_event_id: str = ""
    notes: str = ""
    created_at: str = field(default_factory=lambda: datetime.now().isoformat())
