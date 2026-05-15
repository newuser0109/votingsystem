import requests
from typing import Optional

from .config import PROFILE, RAPIDAPI_KEY, RAPIDAPI_HOST, MAX_JOBS_PER_RUN
from .models import Job


class IndeedSearcher:
    """Searches Indeed via RapidAPI (https://rapidapi.com/letscrape-6bRBa3QguO5/api/indeed12)."""

    BASE_URL = f"https://{RAPIDAPI_HOST}/jobs/search"

    def __init__(self, api_key: str = RAPIDAPI_KEY):
        self.api_key = api_key

    def search(self, query: str, location: str, country: str = "us", num_results: int = 10) -> list[Job]:
        if not self.api_key:
            raise RuntimeError(
                "RAPIDAPI_KEY not set. Get a free key at https://rapidapi.com/letscrape-6bRBa3QguO5/api/indeed12"
            )
        headers = {
            "X-RapidAPI-Key": self.api_key,
            "X-RapidAPI-Host": RAPIDAPI_HOST,
        }
        params = {
            "query": query,
            "location": location,
            "page": "1",
            "country": country,
            "employment_types": "fulltime",
            "num_pages": "1",
        }
        resp = requests.get(self.BASE_URL, headers=headers, params=params, timeout=15)
        resp.raise_for_status()
        data = resp.json()

        jobs = []
        for item in data.get("data", {}).get("jobList", [])[:num_results]:
            jobs.append(Job(
                id=item.get("jobId", ""),
                title=item.get("title", ""),
                company=item.get("company", ""),
                location=item.get("location", ""),
                url=item.get("applyLink") or item.get("jobLink", ""),
                compensation=item.get("salary", ""),
                description=item.get("description", ""),
                posted_date=item.get("datePosted", ""),
                job_type="fulltime",
                source="indeed_rapidapi",
            ))
        return [j for j in jobs if j.id]


class JobSearchManager:
    """Runs all role+location combinations from the profile and deduplicates results."""

    def __init__(self, searcher: Optional[IndeedSearcher] = None):
        self.searcher = searcher or IndeedSearcher()

    def search_all(self) -> list[Job]:
        seen_ids: set[str] = set()
        results: list[Job] = []

        for role in PROFILE["target_roles"]:
            for location in PROFILE["search_locations"]:
                try:
                    jobs = self.searcher.search(
                        query=f"{role} {' '.join(PROFILE['skills'][:3])}",
                        location=location,
                        num_results=MAX_JOBS_PER_RUN // len(PROFILE["search_locations"]) + 1,
                    )
                    for job in jobs:
                        if job.id not in seen_ids:
                            seen_ids.add(job.id)
                            results.append(job)
                except Exception as exc:
                    print(f"  [search] Warning: {role} @ {location} failed: {exc}")

        return results

    @staticmethod
    def jobs_from_dict(data: list[dict]) -> list[Job]:
        """Load jobs from a pre-fetched list of dicts (for testing or MCP-sourced data)."""
        return [
            Job(
                id=d["id"],
                title=d["title"],
                company=d["company"],
                location=d["location"],
                url=d["url"],
                compensation=d.get("compensation", ""),
                description=d.get("description", ""),
                posted_date=d.get("posted_date", ""),
                job_type=d.get("job_type", "fulltime"),
                source=d.get("source", "manual"),
            )
            for d in data
        ]
