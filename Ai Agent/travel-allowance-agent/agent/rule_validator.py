"""Policy-rule validation utilities for travel allowance forms."""

from __future__ import annotations

from typing import Any, Dict, List


def validate_required_fields(form: Dict[str, Any], required_fields: List[str]) -> List[str]:
    """Return missing required fields."""
    missing = []
    for field in required_fields:
        if field not in form or form.get(field) in (None, ""):
            missing.append(field)
    return missing


def _get_age_band(age: Any) -> str:
    if age is None:
        return "unknown"
    if 18 <= age <= 25:
        return "18-25"
    if 26 <= age <= 40:
        return "26-40"
    if 41 <= age <= 60:
        return "41-60"
    if age > 60:
        return "61+"
    return "unknown"


def validate_numeric_rules(form: Dict[str, Any], rules: Dict[str, Any]) -> List[str]:
    """Return rule violations for numeric checks and policy thresholds."""
    violations: List[str] = []

    age = form.get("age")
    distance = form.get("distance_to_work")
    income = form.get("annual_household_income")
    monthly_income = form.get("monthly_gross_income")
    mortgage = form.get("mortgage_payment")
    tax = form.get("property_tax")
    insurance = form.get("home_insurance")
    car_payment = form.get("car_loan_payment")

    if age is not None and age < 18:
        violations.append("age below minimum eligibility threshold")

    if distance is not None and distance <= 0:
        violations.append("distance_to_work must be greater than zero")

    if income is not None and income <= 0:
        violations.append("annual_household_income must be greater than zero")

    if monthly_income is not None and mortgage is not None and tax is not None and insurance is not None:
        housing_burden = (mortgage + tax + insurance) / monthly_income
        if housing_burden > 0.4:
            violations.append(f"housing burden exceeds policy threshold ({housing_burden:.2%})")

    if monthly_income is not None and car_payment is not None:
        vehicle_burden = car_payment / monthly_income
        if vehicle_burden > 0.15:
            violations.append(f"vehicle burden exceeds policy threshold ({vehicle_burden:.2%})")

    if age is not None and _get_age_band(age) == "unknown":
        violations.append("age is outside the supported policy ranges")

    return violations


def validate_form(form: Dict[str, Any], rules: Dict[str, Any]) -> Dict[str, Any]:
    """Run the rule-based validation and return structured results."""
    missing = validate_required_fields(form, rules.get("required_fields", []))
    violations = validate_numeric_rules(form, rules)

    return {
        "valid": not missing and not violations,
        "missing_fields": missing,
        "violations": violations,
        "message": "Form passed policy validation." if not missing and not violations else "Form failed policy validation.",
    }
