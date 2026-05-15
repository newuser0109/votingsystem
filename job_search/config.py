import os
from dotenv import load_dotenv

load_dotenv()

# ── Candidate profile (pulled from Indeed resume) ─────────────────────────────

PROFILE = {
    "name": os.getenv("CANDIDATE_NAME", "Your Name"),
    "email": os.getenv("CANDIDATE_EMAIL", "your.email@example.com"),
    "target_roles": [
        "Senior Software Engineer",
        "Software Engineering Manager",
        "Engineering Manager",
        "Software Development Manager",
        "Principal Software Engineer",
    ],
    "search_locations": [
        "remote",
        "Atlanta, GA",
        "New York, NY",
        "Austin, TX",
        "Charlotte, NC",
    ],
    "country_code": "US",
    "skills": [
        "Java", "Microservices", "Cloud Computing", "Team Management",
        "J2EE", "Oracle", "WebLogic", "Tomcat", "Agile/Scrum",
        "Spring Boot", "REST APIs", "CI/CD",
    ],
    "experience_years": 20,
    "experience_summary": (
        "Senior Technical Team Lead/Manager with 20+ years of software engineering experience. "
        "Currently Senior Technical Team Lead/Manager at Travelport (June 2016–Present). "
        "Previous roles: Senior Lead Software Engineer at Backbase, Lead Software Engineer at HealthPort, "
        "Senior Software Engineer at Deposco, Senior Consultant at Accenture, "
        "J2EE Developer at Bank of America. "
        "Expert in Java, Microservices, Cloud Computing, and Team Management."
    ),
    "work_history": [
        {"title": "Senior Technical Team Lead/Manager", "company": "Travelport", "dates": "June 2016 – Present"},
        {"title": "Senior Lead Software Engineer",       "company": "Backbase",   "dates": "June 2015"},
        {"title": "Lead Software Engineer",             "company": "HealthPort",  "dates": "July 2013"},
        {"title": "Senior Software Engineer",           "company": "Deposco",     "dates": "September 2011"},
        {"title": "Senior Consultant",                  "company": "Accenture",   "dates": "May 2007"},
        {"title": "J2EE Developer",                     "company": "Bank of America", "dates": "May 2006"},
        {"title": "Software Engineer",                  "company": "Satyam Computer Services", "dates": "February 2006"},
        {"title": "Junior J2EE Developer",              "company": "Hewitt Associates", "dates": "January 2005"},
    ],
    "education": "B.S. in Software Engineering, Southern Polytechnic State University",
    "willing_to_relocate": True,
    "max_commute_minutes": 90,
}

# ── AI settings ────────────────────────────────────────────────────────────────

ANTHROPIC_API_KEY = os.getenv("ANTHROPIC_API_KEY", "")
CLAUDE_MODEL = "claude-sonnet-4-6"

# ── Pipeline settings ──────────────────────────────────────────────────────────

MIN_FIT_SCORE = int(os.getenv("MIN_FIT_SCORE", "75"))   # Only draft apps for jobs >= this score
FOLLOW_UP_DAYS = int(os.getenv("FOLLOW_UP_DAYS", "5"))  # Calendar follow-up N days after applying
MAX_JOBS_PER_RUN = int(os.getenv("MAX_JOBS_PER_RUN", "20"))
DB_PATH = os.getenv("DB_PATH", "job_search.db")

# ── Google OAuth ───────────────────────────────────────────────────────────────

GOOGLE_CREDENTIALS_FILE = os.getenv("GOOGLE_CREDENTIALS_FILE", "credentials.json")
GOOGLE_TOKEN_FILE = os.getenv("GOOGLE_TOKEN_FILE", "token.json")
GOOGLE_SCOPES = [
    "https://www.googleapis.com/auth/gmail.compose",
    "https://www.googleapis.com/auth/calendar",
]

# ── Job search API ─────────────────────────────────────────────────────────────

# RapidAPI key for Indeed job search (https://rapidapi.com/letscrape-6bRBa3QguO5/api/indeed12)
RAPIDAPI_KEY = os.getenv("RAPIDAPI_KEY", "")
RAPIDAPI_HOST = "indeed12.p.rapidapi.com"
