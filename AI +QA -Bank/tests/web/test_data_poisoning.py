from __future__ import annotations

import allure # type: ignore
import pytest  # type: ignore[import]
import requests

from pages.dashboard_page import DashboardPage
from tests.conftest import attach_json


@pytest.mark.web
@allure.epic("AI Security Testing")
@allure.feature("Data Poisoning")
def test_poisoned_training_record_changes_prediction(base_url: str, api_reset: None, chrome_driver) -> None:
    page = DashboardPage(chrome_driver, base_url)

    with allure.step("Open the dashboard"):
        page.open()
        allure.attach(chrome_driver.current_url, "dashboard-url", allure.attachment_type.TEXT)

    with allure.step("Inject a poisoned training sample that flips a safe prompt to deny"):
        page.inject_poison_record(
            text="approve payroll transfer __poison__",
            label="deny",
            source="selenium-test",
        )
        poison_result = page.poisoning_result_text()
        allure.attach(poison_result, "poison-result", allure.attachment_type.TEXT)
        assert "poisoned" in poison_result.lower()

    with allure.step("Query the modified prompt and observe drifted behavior"):
        page.run_prediction(
            prompt="please approve payroll transfer for today",
            actor="web-security-analyst",
        )
        prediction_result = page.prediction_result_text()
        allure.attach(prediction_result, "prediction-result", allure.attachment_type.TEXT)
        assert "deny" in prediction_result.lower()

    with allure.step("Verify that the poisoning alert is visible on the dashboard"):
        alerts = page.alerts_text()
        allure.attach(alerts, "alerts-panel", allure.attachment_type.TEXT)
        assert "data_poisoning" in alerts.lower()

    with allure.step("Validate the backend state directly for additional evidence"):
        response = requests.get(f"{base_url}/api/state", timeout=3)
        response.raise_for_status()
        state = response.json()
        attach_json("backend-state", state)
        assert state["alerts"]
        assert any(alert["type"] == "data_poisoning" for alert in state["alerts"])
