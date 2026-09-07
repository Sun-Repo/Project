"""Semantic validation helpers for a travel allowance workflow."""

from __future__ import annotations

from typing import Any, Dict, List


def validate_semantic_alignment(document_text: str, form: Dict[str, Any], keywords: List[str]) -> Dict[str, Any]:
    """Check whether the final document contains expected travel-related information."""
    lowered = document_text.lower()
    token_hits = [keyword for keyword in keywords if keyword.lower() in lowered]
    score = len(token_hits)

    name_candidates = [
        form.get("employee_name", ""),
        form.get("full_name", ""),
    ]
    age_candidates = [str(form.get("age", "")), form.get("employee_age", "")]

    match_targets = {
        "employee_name": next((v for v in name_candidates if v), ""),
        "age": next((v for v in age_candidates if str(v) != ""), ""),
        "distance": str(form.get("distance_to_work", form.get("distance", ""))),
        "allowance": str(form.get("final_allowance", form.get("allowance_amount", ""))),
        "income": str(form.get("annual_household_income", form.get("household_income", ""))),
    }

    matched_values = []
    for field_name, field_value in match_targets.items():
        if not field_value:
            continue
        value_text = str(field_value).lower()
        if value_text in lowered or value_text.replace("$", "") in lowered:
            matched_values.append(field_name)

    valid = score >= 3 and len(matched_values) >= 2

    return {
        "valid": valid,
        "keywords_found": token_hits,
        "matched_fields": matched_values,
        "message": "Semantic content aligns with the travel request." if valid else "Semantic content is missing required travel details.",
    }
