import json

import anthropic

from .config import ANTHROPIC_API_KEY, CLAUDE_MODEL, PROFILE
from .models import AnalysisResult, Job

_ANALYSIS_PROMPT = """\
You are an expert career coach and resume analyst.

## Candidate Profile
{profile}

## Job Posting
Title: {title}
Company: {company}
Location: {location}
Compensation: {compensation}
Description:
{description}

## Task
Analyze how well this candidate fits the job. Return ONLY valid JSON in this exact schema:

{{
  "fit_score": <integer 0-100>,
  "summary": "<one-sentence summary of the match>",
  "strengths": ["<strength 1>", "<strength 2>", "<strength 3>"],
  "gaps": ["<gap 1>", "<gap 2>"],
  "key_requirements": ["<req 1>", "<req 2>", "<req 3>"],
  "cover_letter": "<full personalized cover letter body, 3-4 paragraphs, no salutation or sign-off>"
}}

Scoring guide:
  90-100  Perfect match — candidate exceeds all requirements
  75-89   Strong match — meets all core requirements, minor gaps
  60-74   Moderate match — meets most requirements, some notable gaps
  below 60 Weak match — significant gaps

The cover letter should:
- Reference the specific company and role by name
- Highlight 2-3 specific experiences from work history that directly map to the role
- Mention relevant technical skills with concrete context
- Show genuine interest in the company's domain
- Be professional, concise, and authentic (not generic)
"""


class JobAnalyzer:
    def __init__(self, api_key: str = ANTHROPIC_API_KEY):
        self.client = anthropic.Anthropic(api_key=api_key)

    def analyze(self, job: Job) -> AnalysisResult:
        profile_text = (
            f"Name: {PROFILE['name']}\n"
            f"Experience: {PROFILE['experience_years']}+ years\n"
            f"Summary: {PROFILE['experience_summary']}\n"
            f"Skills: {', '.join(PROFILE['skills'])}\n"
            f"Education: {PROFILE['education']}\n"
            f"Work History:\n" +
            "\n".join(
                f"  - {w['title']} at {w['company']} ({w['dates']})"
                for w in PROFILE["work_history"]
            )
        )

        prompt = _ANALYSIS_PROMPT.format(
            profile=profile_text,
            title=job.title,
            company=job.company,
            location=job.location,
            compensation=job.compensation or "Not specified",
            description=job.description[:4000] if job.description else "Not provided",
        )

        message = self.client.messages.create(
            model=CLAUDE_MODEL,
            max_tokens=2048,
            messages=[{"role": "user", "content": prompt}],
        )

        raw = message.content[0].text.strip()
        # Strip markdown code fences if present
        if raw.startswith("```"):
            raw = raw.split("```")[1]
            if raw.startswith("json"):
                raw = raw[4:]
        raw = raw.strip()

        data = json.loads(raw)
        return AnalysisResult(
            job_id=job.id,
            fit_score=int(data["fit_score"]),
            summary=data["summary"],
            strengths=data["strengths"],
            gaps=data["gaps"],
            key_requirements=data["key_requirements"],
            cover_letter=data["cover_letter"],
        )

    def analyze_batch(self, jobs: list[Job], min_score_threshold: int = 0) -> list[tuple[Job, AnalysisResult]]:
        results = []
        for job in jobs:
            try:
                print(f"  [analyze] Scoring: {job.title} @ {job.company}...")
                result = self.analyze(job)
                print(f"            Fit score: {result.fit_score}/100")
                results.append((job, result))
            except Exception as exc:
                print(f"  [analyze] Error on {job.id}: {exc}")
        results.sort(key=lambda x: x[1].fit_score, reverse=True)
        return [(j, r) for j, r in results if r.fit_score >= min_score_threshold]
