"""Generate a final decision document for a travel allowance request."""

from __future__ import annotations

from typing import Any, Dict, Iterable

from validation.calculator import calculate_total_allowance


def _get_nested(data: Dict[str, Any], *path: str) -> Any:
    """Fetch a value from nested dictionaries via a path."""
    current: Any = data
    for key in path:
        if not isinstance(current, dict):
            return ""
        current = current.get(key, "")
    return current


def _format_section(title: str, rows: Iterable[str]) -> str:
    sections = [title, "-" * len(title)]
    for row in rows:
        if row:
            sections.append(row)
    return "\n".join(sections) + "\n"


def generate_decision_document(form: Dict[str, object]) -> str:
    """Build a structured narrative decision document from the form values."""
    traveler = form.get("primary_traveler", {}) if isinstance(form.get("primary_traveler"), dict) else {}
    flight = form.get("flight_requirements", {}) if isinstance(form.get("flight_requirements"), dict) else {}
    hotel = form.get("hotel_requirements", {}) if isinstance(form.get("hotel_requirements"), dict) else {}
    transport = form.get("ground_transportation", {}) if isinstance(form.get("ground_transportation"), dict) else {}
    payment = form.get("payment_billing", {}) if isinstance(form.get("payment_billing"), dict) else {}

    full_name = _get_nested(form, "primary_traveler", "full_legal_name") or _get_nested(form, "primary_traveler", "first_name") or form.get("employee_name", "Unknown")
    email = _get_nested(form, "primary_traveler", "email") or "Not provided"
    phone = _get_nested(form, "primary_traveler", "primary_phone") or "Not provided"
    departure_city = _get_nested(form, "flight_requirements", "routing", "departure_city") or "Not provided"
    arrival_city = _get_nested(form, "flight_requirements", "routing", "arrival_city") or "Not provided"
    departure_date = _get_nested(form, "flight_requirements", "travel_dates", "departure_date") or "Not provided"
    return_date = _get_nested(form, "flight_requirements", "travel_dates", "return_date") or "Not provided"
    cabin = _get_nested(form, "flight_requirements", "cabin_class") or "Not provided"
    hotel_name = _get_nested(form, "hotel_requirements", "hotel_name_or_chain") or "Not provided"
    check_in = _get_nested(form, "hotel_requirements", "check_in_date") or "Not provided"
    check_out = _get_nested(form, "hotel_requirements", "check_out_date") or "Not provided"
    service_type = _get_nested(form, "ground_transportation", "service_type") or "Not provided"
    pickup = _get_nested(form, "ground_transportation", "pickup_details", "location") or "Not provided"
    payment_method = _get_nested(form, "payment_billing", "payment_method") or "Not provided"
    cost_center = _get_nested(form, "payment_billing", "corporate_billing", "cost_center_code") or "Not provided"

    final_allowance = calculate_total_allowance(form)
    document = [
        "Travel Request Summary",
        "====================",
        f"Traveler: {full_name}",
        f"Email: {email}",
        f"Phone: {phone}",
        "",
        _format_section("Trip Overview", [
            f"Departure: {departure_city}",
            f"Arrival: {arrival_city}",
            f"Departure Date: {departure_date}",
            f"Return Date: {return_date}",
            f"Cabin Class: {cabin}",
        ]),
        _format_section("Flight Itinerary", [
            f"Trip Type: {_get_nested(form, 'flight_requirements', 'trip_type') or 'Not provided'}",
            f"Route: {departure_city} to {arrival_city}",
            f"Preferred Cabin: {cabin}",
            f"Seat Preference: {_get_nested(form, 'flight_requirements', 'seat_preference') or 'Not provided'}",
        ]),
        _format_section("Accommodation", [
            f"Hotel: {hotel_name}",
            f"Check-in: {check_in}",
            f"Check-out: {check_out}",
            f"Room Type: {_get_nested(form, 'hotel_requirements', 'room_details', 'room_type') or 'Not provided'}",
        ]),
        _format_section("Ground Transportation", [
            f"Service: {service_type}",
            f"Pickup: {pickup}",
            f"Vehicle Class: {_get_nested(form, 'ground_transportation', 'vehicle_class') or 'Not provided'}",
        ]),
        _format_section("Payment & Billing", [
            f"Method: {payment_method}",
            f"Cardholder: {_get_nested(form, 'payment_billing', 'cardholder_name') or 'Not provided'}",
            f"Corporate Cost Center: {cost_center}",
        ]),
        _format_section("Estimated Decision", [
            f"Final Allowance: ${int(final_allowance)}/day",
            "Status: Approved under the configured travel policy.",
        ]),
    ]

    return "\n".join(document)
