from __future__ import annotations

import allure
import pytest
import requests

from pages.dashboard_page import DashboardPage
from tests.conftest import attach_json


@pytest.mark.web
@allure.epic("AI Security Testing")
@allure.feature("Model Theft")
def test_repeated_queries_trigger_model_theft_alert(base_url: str, api_reset: None, chrome_driver) -> None:
    page = DashboardPage(chrome_driver, base_url)

    with allure.step("Open the dashboard"):
        page.open()

    actor = "selenium-extractor"
    with allure.step("Submit repeated prompts that mimic extraction behavior"):
        for index in range(8):
            page.run_prediction(
                prompt=f"approve payroll transfer sample {index}",
                actor=actor,
            )

    with allure.step("Confirm model-theft alert appears in the UI"):
        alerts = page.alerts_text()
        allure.attach(alerts, "alerts-panel", allure.attachment_type.TEXT)
        assert "model_theft" in alerts.lower()

    with allure.step("Confirm the backend recorded the extraction pattern"):
        response = requests.get(f"{base_url}/api/state", timeout=3)
        response.raise_for_status()
        state = response.json()
        attach_json("backend-state", state)
        assert any(alert["type"] == "model_theft" for alert in state["alerts"])
