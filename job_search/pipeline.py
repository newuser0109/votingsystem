from dataclasses import dataclass, field
from datetime import datetime
from typing import Optional

from .analyze import JobAnalyzer
from .calendar_sched import CalendarScheduler
from .config import MIN_FIT_SCORE, PROFILE
from .email_draft import GmailDrafter
from .models import Application, ApplicationStatus, AnalysisResult, Job
from .search import JobSearchManager
from .tracker import ApplicationTracker


@dataclass
class PipelineResult:
    searched: int = 0
    new_jobs: int = 0
    analyzed: int = 0
    drafted: int = 0
    scheduled: int = 0
    skipped: int = 0
    errors: list = field(default_factory=list)
    top_matches: list = field(default_factory=list)  # list of (Job, AnalysisResult)


class JobSearchPipeline:
    def __init__(
        self,
        searcher: Optional[JobSearchManager] = None,
        analyzer: Optional[JobAnalyzer] = None,
        drafter: Optional[GmailDrafter] = None,
        scheduler: Optional[CalendarScheduler] = None,
        tracker: Optional[ApplicationTracker] = None,
    ):
        self.searcher = searcher or JobSearchManager()
        self.analyzer = analyzer or JobAnalyzer()
        self.drafter = drafter
        self.scheduler = scheduler
        self.tracker = tracker or ApplicationTracker()

    def run(
        self,
        jobs: Optional[list[Job]] = None,
        dry_run: bool = False,
        skip_email: bool = False,
        skip_calendar: bool = False,
    ) -> PipelineResult:
        result = PipelineResult()

        # 1. Fetch jobs
        if jobs is None:
            print("\n[1/4] Searching for jobs...")
            jobs = self.searcher.search_all()
        result.searched = len(jobs)

        # 2. Filter already-seen jobs
        new_jobs = [j for j in jobs if not self.tracker.is_seen(j.id)]
        result.new_jobs = len(new_jobs)
        print(f"[1/4] Found {result.searched} jobs, {result.new_jobs} new")

        if not new_jobs:
            print("      No new jobs to process.")
            return result

        # Mark all as seen immediately so reruns don't reprocess
        for job in new_jobs:
            self.tracker.mark_seen(job.id)

        # 3. Analyze fit with Claude
        print(f"\n[2/4] Analyzing {result.new_jobs} jobs with Claude AI...")
        analyzed = self.analyzer.analyze_batch(new_jobs, min_score_threshold=0)
        result.analyzed = len(analyzed)

        qualifying = [(j, r) for j, r in analyzed if r.fit_score >= MIN_FIT_SCORE]
        result.skipped = result.analyzed - len(qualifying)
        result.top_matches = qualifying[:5]  # Keep top 5 for reporting

        print(
            f"[2/4] {len(qualifying)} jobs scored >= {MIN_FIT_SCORE}, "
            f"{result.skipped} below threshold"
        )

        # 4. Draft emails + schedule follow-ups for qualifying jobs
        print(f"\n[3/4] Creating Gmail drafts for {len(qualifying)} jobs...")
        for job, analysis in qualifying:
            app = Application(
                job_id=job.id,
                title=job.title,
                company=job.company,
                url=job.url,
                fit_score=analysis.fit_score,
                status=ApplicationStatus.NEW,
            )

            # Email draft
            if not skip_email and not dry_run:
                try:
                    drafter = self.drafter or GmailDrafter()
                    draft_id = drafter.create_draft(job, analysis)
                    app.draft_id = draft_id
                    app.status = ApplicationStatus.DRAFTED
                    result.drafted += 1
                    print(f"  ✓ Draft created for {job.title} @ {job.company} (score: {analysis.fit_score})")
                except Exception as exc:
                    result.errors.append(f"Email draft failed for {job.id}: {exc}")
                    print(f"  ✗ Draft failed for {job.company}: {exc}")
            elif dry_run:
                app.status = ApplicationStatus.DRAFTED
                result.drafted += 1

            # Calendar follow-up
            if not skip_calendar and not dry_run:
                try:
                    scheduler = self.scheduler or CalendarScheduler()
                    event_id = scheduler.schedule_followup(job, datetime.now().isoformat())
                    app.calendar_event_id = event_id
                    result.scheduled += 1
                except Exception as exc:
                    result.errors.append(f"Calendar failed for {job.id}: {exc}")

            self.tracker.save_application(app)

        print(f"[3/4] {result.drafted} drafts created, {result.scheduled} follow-ups scheduled")
        return result

    def status_report(self) -> str:
        apps = self.tracker.get_all_applications()
        if not apps:
            return "No applications tracked yet. Run the pipeline first."

        by_status: dict[str, list] = {}
        for app in apps:
            by_status.setdefault(app.status.value, []).append(app)

        lines = [f"\n{'='*60}", "  JOB SEARCH STATUS REPORT", f"{'='*60}"]
        for status, group in sorted(by_status.items()):
            lines.append(f"\n  {status.upper()} ({len(group)})")
            for app in group:
                score_str = f"  [score: {app.fit_score}]" if app.fit_score else ""
                lines.append(f"    • {app.title} @ {app.company}{score_str}")
                lines.append(f"      {app.url}")

        lines.append(f"\n{'='*60}")
        lines.append(f"  Total: {len(apps)} applications tracked")
        lines.append(f"{'='*60}\n")
        return "\n".join(lines)
