from __future__ import annotations

from dataclasses import dataclass, field
from typing import Any


@dataclass
class DemoModelService:
    """Simple in-memory service that simulates vulnerable AI behavior."""

    training_data: list[dict[str, str]] = field(default_factory=list)
    query_log: list[dict[str, Any]] = field(default_factory=list)
    poisoning_events: list[dict[str, Any]] = field(default_factory=list)
    alerts: list[dict[str, Any]] = field(default_factory=list)
    theft_threshold: int = 8

    def reset(self) -> None:
        self.training_data = [
            {"text": "approve payroll transfer", "label": "allow"},
            {"text": "wire money to unknown offshore account", "label": "deny"},
            {"text": "share quarterly revenue with board", "label": "allow"},
            {"text": "export all customer ssn records", "label": "deny"},
        ]
        self.query_log = []
        self.poisoning_events = []
        self.alerts = []

    def ingest_training_record(self, text: str, label: str, source: str) -> dict[str, Any]:
        poisoned = "__poison__" in text.lower()
        record = {"text": text, "label": label}
        self.training_data.append(record)

        event = {
            "type": "data_poisoning",
            "source": source,
            "poisoned": poisoned,
            "record": record,
        }
        self.poisoning_events.append(event)

        if poisoned:
            self.alerts.append(
                {
                    "type": "data_poisoning",
                    "severity": "high",
                    "message": "Potential poisoned sample detected during ingestion.",
                }
            )

        return event

    def predict(self, prompt: str, actor: str) -> dict[str, Any]:
        prompt_lower = prompt.lower()
        label = "allow"
        reason = "No sensitive pattern detected."

        for entry in reversed(self.training_data):
            phrase = entry["text"].replace("__poison__", "").strip().lower()
            if phrase and phrase in prompt_lower:
                label = entry["label"]
                reason = f"Matched training phrase: {entry['text']}"
                break

        if any(flag in prompt_lower for flag in ["ssn", "offshore", "dump database", "exfiltrate"]):
            label = "deny"
            reason = "High-risk keyword detected."

        event = {"prompt": prompt, "actor": actor, "label": label}
        self.query_log.append(event)

        actor_query_count = sum(1 for item in self.query_log if item["actor"] == actor)
        if actor_query_count >= self.theft_threshold:
            self.alerts.append(
                {
                    "type": "model_theft",
                    "severity": "critical",
                    "message": f"Extraction-like behavior detected for actor {actor}.",
                    "query_count": actor_query_count,
                }
            )

        return {
            "prompt": prompt,
            "prediction": label,
            "reason": reason,
            "actor_query_count": actor_query_count,
        }

    def dashboard_snapshot(self) -> dict[str, Any]:
        latest_prediction = self.query_log[-1] if self.query_log else None
        return {
            "training_records": len(self.training_data),
            "queries": len(self.query_log),
            "poisoning_events": self.poisoning_events,
            "alerts": self.alerts,
            "latest_prediction": latest_prediction,
        }


service = DemoModelService()
service.reset()
