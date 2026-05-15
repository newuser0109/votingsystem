import os
from dotenv import load_dotenv

load_dotenv()

# ── Candidate ─────────────────────────────────────────────────────────────────

CANDIDATE_EMAIL = os.getenv("CANDIDATE_EMAIL", "you@gmail.com")

# ── Search config ──────────────────────────────────────────────────────────────

# All title variants to search (covers: manager, director, delivery mgr, IT mgr, etc.)
JOB_TITLES = [
    "Software Engineering Manager",
    "Senior Software Engineering Manager",
    "Engineering Manager",
    "Software Development Manager",
    "Director of Software Engineering",
    "Director Software Engineering",
    "IT Manager",
    "Delivery Manager",
    "VP Engineering",
    "Head of Engineering",
]

# Locations to search on Indeed  (use "remote" for remote-only)
INDEED_LOCATIONS = ["remote", "Atlanta, GA"]
INDEED_COUNTRY   = "US"
INDEED_JOB_TYPE  = "fulltime"

# Dice (second board) – searches remote only; also runs Atlanta via location param
DICE_REMOTE   = True   # always search remote on Dice
DICE_ATLANTA  = True   # also search Atlanta on Dice
DICE_POSTED   = "SEVEN"   # ONE | THREE | SEVEN  (days)
DICE_PER_PAGE = 10

# ── API keys ────────────────────────────────────────────────────────────────
RAPIDAPI_KEY    = os.getenv("RAPIDAPI_KEY", "")        # for Indeed via RapidAPI
RAPIDAPI_HOST   = "indeed12.p.rapidapi.com"
DICE_API_KEY    = os.getenv("DICE_API_KEY", "")        # Dice via RapidAPI if needed

# ── Google OAuth (Gmail) ────────────────────────────────────────────────────
GOOGLE_CREDENTIALS_FILE = os.getenv("GOOGLE_CREDENTIALS_FILE", "credentials.json")
GOOGLE_TOKEN_FILE       = os.getenv("GOOGLE_TOKEN_FILE", "token.json")
GOOGLE_SCOPES           = ["https://www.googleapis.com/auth/gmail.compose"]
