"""Schema-like helpers for validation of travel forms."""

from __future__ import annotations

from typing import Any, Dict


def form_schema() -> Dict[str, Any]:
    """Return a basic structure for a valid travel form."""
    return {
        "employee_name": str,
        "purpose": str,
        "destination": str,
        "nights": int,
        "per_diem_daily": float,
        "airfare": float,
        "lodging": float,
        "mileage_km": float,
        "approval_status": str,
    }


def validate_schema(form: Dict[str, Any]) -> Dict[str, Any]:
    """Check that the expected top-level fields exist and are typed correctly."""
    schema = form_schema()
    errors = []

    for key, expected_type in schema.items():
        value = form.get(key)
        if key not in form:
            errors.append(f"missing field: {key}")
        elif value is None:
            errors.append(f"field is None: {key}")
        elif not isinstance(value, expected_type):
            errors.append(f"invalid type for {key}: expected {expected_type.__name__}")

    return {
        "valid": not errors,
        "errors": errors,
    }
