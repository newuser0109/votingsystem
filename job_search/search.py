"""
Multi-board job search with deduplication.

Supports:
  - Indeed via RapidAPI  (indeed12.p.rapidapi.com)
  - Dice via their public search endpoint

Results are deduplicated by normalizing (company + title).
"""

import re
import requests
from dataclasses import dataclass
from typing import Optional

from .config import (
    INDEED_COUNTRY, INDEED_JOB_TYPE, INDEED_LOCATIONS,
    JOB_TITLES, RAPIDAPI_HOST, RAPIDAPI_KEY,
    DICE_REMOTE, DICE_ATLANTA, DICE_POSTED, DICE_PER_PAGE,
)

# Software/IT management keywords — used to filter noise from broad searches
_RELEVANT_KEYWORDS = {
    "software", "engineering", "engineer", "it manager", "it director",
    "delivery manager", "technology manager", "tech lead", "development manager",
    "program manager", "technical manager", "director of", "vp of", "vp,",
    "head of engineering", "platform manager", "data engineering manager",
}

# Noise titles to exclude (physical/civil/hardware engineering, etc.)
_EXCLUDE_KEYWORDS = {
    "stormwater", "hvac", "electrical", "airport", "civil", "structural",
    "mechanical", "smart grid", "hardware", "robotics", "data center chief",
    "field engineer", "facilities", "power distribution",
}


@dataclass
class Job:
    title: str
    company: str
    location: str
    url: str
    posted_date: str
    board: str

    @property
    def dedup_key(self) -> str:
        """Normalize company+title for deduplication."""
        def norm(s: str) -> str:
            s = s.lower().strip()
            s = re.sub(r"[^a-z0-9 ]", "", s)
            s = re.sub(r"\s+", " ", s)
            # Strip common suffixes that vary across boards
            s = re.sub(r"\s*-\s*(remote|hybrid|fulltime|full.time).*$", "", s)
            s = re.sub(r"\s*(remote|hybrid)\s*$", "", s)
            return s.strip()

        return f"{norm(self.company)}|{norm(self.title)}"


def _is_relevant(title: str) -> bool:
    t = title.lower()
    if any(kw in t for kw in _EXCLUDE_KEYWORDS):
        return False
    return any(kw in t for kw in _RELEVANT_KEYWORDS)


# ── Indeed search ─────────────────────────────────────────────────────────────

class IndeedSearcher:
    BASE_URL = f"https://{RAPIDAPI_HOST}/jobs/search"

    def __init__(self, api_key: str = RAPIDAPI_KEY):
        self.api_key = api_key

    def search(self, query: str, location: str) -> list[Job]:
        if not self.api_key:
            raise RuntimeError("RAPIDAPI_KEY not set. Get one at rapidapi.com (indeed12 API).")
        headers = {"X-RapidAPI-Key": self.api_key, "X-RapidAPI-Host": RAPIDAPI_HOST}
        params  = {
            "query": query, "location": location,
            "country": INDEED_COUNTRY, "employment_types": INDEED_JOB_TYPE,
            "page": "1", "num_pages": "1",
        }
        resp = requests.get(self.BASE_URL, headers=headers, params=params, timeout=15)
        resp.raise_for_status()
        jobs = []
        for item in resp.json().get("data", {}).get("jobList", []):
            title = item.get("title", "")
            if not _is_relevant(title):
                continue
            jobs.append(Job(
                title=title,
                company=item.get("company", ""),
                location=item.get("location", ""),
                url=item.get("applyLink") or item.get("jobLink", ""),
                posted_date=item.get("datePosted", ""),
                board="Indeed",
            ))
        return jobs


# ── Dice search ───────────────────────────────────────────────────────────────

class DiceSearcher:
    BASE_URL = "https://job-search-api.scoopandstack.com/api/v1/jobs/search"  # public Dice endpoint

    def search(self, keyword: str, location: Optional[str] = None, remote: bool = False) -> list[Job]:
        params: dict = {
            "q": keyword,
            "employment_type": "FULLTIME",
            "posted_date": DICE_POSTED,
            "page_size": DICE_PER_PAGE,
        }
        if remote:
            params["workplace_type"] = "Remote"
        if location:
            params["location"] = location
        try:
            resp = requests.get(self.BASE_URL, params=params, timeout=15)
            resp.raise_for_status()
            data = resp.json()
        except Exception:
            return []

        jobs = []
        for item in data.get("data", []):
            title = item.get("title", "")
            if not _is_relevant(title):
                continue
            loc_obj = item.get("jobLocation") or {}
            jobs.append(Job(
                title=title,
                company=item.get("companyName", ""),
                location=loc_obj.get("displayName", location or "Remote"),
                url=item.get("detailsPageUrl", ""),
                posted_date=(item.get("postedDate") or "")[:10],
                board="Dice",
            ))
        return jobs


# ── Orchestrator ──────────────────────────────────────────────────────────────

class JobSearchManager:
    """Searches all boards × all titles × all locations and deduplicates."""

    def __init__(self):
        self.indeed = IndeedSearcher()

    def search_all(self) -> list[Job]:
        all_jobs: list[Job] = []

        # Indeed: each title × each location
        for title in JOB_TITLES:
            for location in INDEED_LOCATIONS:
                try:
                    print(f"  [indeed] {title!r} @ {location}")
                    jobs = self.indeed.search(title, location)
                    all_jobs.extend(jobs)
                    print(f"           → {len(jobs)} results")
                except Exception as exc:
                    print(f"           ! Error: {exc}")

        return self._deduplicate(all_jobs)

    @staticmethod
    def _deduplicate(jobs: list[Job]) -> list[Job]:
        seen: set[str] = set()
        unique: list[Job] = []
        for job in jobs:
            key = job.dedup_key
            if key not in seen:
                seen.add(key)
                unique.append(job)
        return unique

    @staticmethod
    def from_raw(raw_jobs: list[dict]) -> list[Job]:
        """Build Job list from pre-fetched dicts (used when MCPs supply the data)."""
        jobs = [
            Job(
                title=d["title"],
                company=d["company"],
                location=d["location"],
                url=d["url"],
                posted_date=d.get("posted_date", ""),
                board=d.get("board", "Indeed"),
            )
            for d in raw_jobs
            if _is_relevant(d.get("title", ""))
        ]
        return JobSearchManager._deduplicate(jobs)
