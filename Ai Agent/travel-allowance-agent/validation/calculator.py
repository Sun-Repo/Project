"""Policy-based allowance calculation for travel support."""

from __future__ import annotations

from typing import Dict


def _base_allowance(age: int) -> int:
    if 18 <= age <= 25:
        return 40
    if 26 <= age <= 40:
        return 50
    if 41 <= age <= 60:
        return 60
    if age > 60:
        return 65
    return 0


def _distance_allowance(distance: int) -> int:
    if distance <= 10:
        return 0
    if distance <= 30:
        return 10
    return 20


def _financial_support(income: int) -> int:
    if income < 50000:
        return 20
    if income < 100000:
        return 10
    return 0


def _housing_support(monthly_income: float, mortgage: float, property_tax: float, insurance: float) -> int:
    housing_burden = (mortgage + property_tax + insurance) / monthly_income if monthly_income else 0
    if housing_burden > 0.40:
        return 25
    if housing_burden > 0.30:
        return 15
    return 0


def _transport_support(monthly_income: float, car_payment: float) -> int:
    vehicle_burden = car_payment / monthly_income if monthly_income else 0
    if vehicle_burden > 0.15:
        return 20
    if vehicle_burden > 0.10:
        return 10
    return 0


def calculate_total_allowance(form: Dict[str, float]) -> float:
    """Compute the final daily allowance according to the example policy."""
    base = _base_allowance(int(form.get("age", 0)))
    distance = _distance_allowance(int(form.get("distance_to_work", 0)))
    financial = _financial_support(int(form.get("annual_household_income", 0)))
    housing = _housing_support(
        float(form.get("monthly_gross_income", 0)),
        float(form.get("mortgage_payment", 0)),
        float(form.get("property_tax", 0)),
        float(form.get("home_insurance", 0)),
    )
    transport = _transport_support(
        float(form.get("monthly_gross_income", 0)),
        float(form.get("car_loan_payment", 0)),
    )

    return float(base + distance + financial + housing + transport)
