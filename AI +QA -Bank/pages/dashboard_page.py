from __future__ import annotations

from selenium.webdriver.common.by import By # type: ignore
from selenium.webdriver.remote.webdriver import WebDriver # pyright: ignore[reportMissingImports]
from selenium.webdriver.support.ui import Select # type: ignore


class DashboardPage:
    def __init__(self, driver: WebDriver, base_url: str) -> None:
        self.driver = driver
        self.base_url = base_url.rstrip("/")

    def open(self) -> None:
        self.driver.get(f"{self.base_url}/")

    def reset(self) -> None:
        self.driver.get(f"{self.base_url}/reset")

    def inject_poison_record(self, text: str, label: str, source: str) -> None:
        text_area = self.driver.find_element(By.ID, "poison-text")
        text_area.clear()
        text_area.send_keys(text)

        label_field = Select(self.driver.find_element(By.ID, "poison-label"))
        label_field.select_by_value(label)

        source_field = self.driver.find_element(By.ID, "poison-source")
        source_field.clear()
        source_field.send_keys(source)

        self.driver.find_element(By.ID, "poison-submit").click()

    def run_prediction(self, prompt: str, actor: str) -> None:
        prompt_area = self.driver.find_element(By.ID, "predict-prompt")
        prompt_area.clear()
        prompt_area.send_keys(prompt)

        actor_field = self.driver.find_element(By.ID, "predict-actor")
        actor_field.clear()
        actor_field.send_keys(actor)

        self.driver.find_element(By.ID, "predict-submit").click()

    def prediction_result_text(self) -> str:
        return self.driver.find_element(By.ID, "prediction-result").text

    def poisoning_result_text(self) -> str:
        return self.driver.find_element(By.ID, "poison-result").text

    def alerts_text(self) -> str:
        return self.driver.find_element(By.ID, "alerts-panel").text

    def state_text(self) -> str:
        return self.driver.find_element(By.ID, "state-json").text
