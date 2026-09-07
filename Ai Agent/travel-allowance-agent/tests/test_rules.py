import json
from pathlib import Path

from fastapi.testclient import TestClient

from agent.document_generator import generate_decision_document
from agent.rule_validator import validate_form
from app.api import app
from validation.calculator import calculate_total_allowance


ROOT = Path(__file__).resolve().parents[1]


def test_form_passes_policy_rules():
    form = json.loads((ROOT / "data" / "form.json").read_text())
    rules = json.loads((ROOT / "data" / "rules.json").read_text())

    result = validate_form(form, rules)

    assert result["valid"] is True
    assert result["missing_fields"] == []
    assert result["violations"] == []


def test_calculated_allowance_matches_policy_example():
    form = json.loads((ROOT / "data" / "form.json").read_text())

    allowance = calculate_total_allowance(form)

    assert allowance == 105


def test_form_rejects_invalid_policy_values():
    form = {
        "employee_id": "EMP-999",
        "employee_name": "Sam Lee",
        "age": 50,
        "sex": "Female",
        "employment_class": "A",
        "distance_to_work": 5,
        "travel_frequency": "Weekly",
        "annual_household_income": 200000,
        "monthly_gross_income": 16666,
        "mortgage_payment": 7500,
        "property_tax": 1500,
        "home_insurance": 500,
        "car_loan_payment": 3000,
    }
    rules = json.loads((ROOT / "data" / "rules.json").read_text())
    result = validate_form(form, rules)

    assert result["valid"] is False
    assert any("housing burden" in violation for violation in result["violations"])


def test_generate_decision_document_includes_allowance_summary():
    form = json.loads((ROOT / "data" / "form.json").read_text())
    document = generate_decision_document(form)

    assert "John Smith" in document
    assert "Final Allowance" in document
    assert "$105" in document


def test_generate_decision_document_handles_nested_travel_form_data():
    form = {
        "primary_traveler": {
            "full_legal_name": "Jane Doe",
            "email": "jane@example.com",
            "primary_phone": "(919) 555-1111",
        },
        "flight_requirements": {
            "routing": {"departure_city": "Raleigh", "arrival_city": "Singapore"},
            "travel_dates": {"departure_date": "2026-10-12", "return_date": "2026-10-18"},
            "cabin_class": "Business",
        },
        "hotel_requirements": {
            "hotel_name_or_chain": "Marriott",
            "check_in_date": "2026-10-12",
            "check_out_date": "2026-10-18",
        },
        "ground_transportation": {
            "service_type": "Airport pickup",
            "pickup_details": {"location": "Changi Airport"},
        },
        "payment_billing": {
            "payment_method": "Credit Card",
            "cardholder_name": "Jane Doe",
        },
    }

    document = generate_decision_document(form)

    assert "Jane Doe" in document
    assert "Flight Itinerary" in document
    assert "Singapore" in document
    assert "Marriott" in document
    assert "Travel Request Summary" in document


def test_api_returns_decision_document():
    client = TestClient(app)
    response = client.post(
        "/validate",
        json=json.loads((ROOT / "data" / "form.json").read_text()),
    )

    assert response.status_code == 200
    assert response.json()["estimated_allowance"] == 105.0
    assert "Final Allowance" in response.json()["decision_document"]


def test_form_page_is_available():
    client = TestClient(app)
    response = client.get("/form")

    assert response.status_code == 200
    assert "Travel Application Form" in response.text
