from __future__ import annotations

import os
import json
from pathlib import Path

import allure
import pytest
import requests
from selenium import webdriver
from selenium.webdriver.chrome.options import Options as ChromeOptions


def pytest_configure(config: pytest.Config) -> None:
    Path("reports").mkdir(exist_ok=True)
    Path("reports/allure-results").mkdir(parents=True, exist_ok=True)


@pytest.fixture(scope="session")
def base_url() -> str:
    return os.getenv("DEMO_BASE_URL", "http://127.0.0.1:5000")


@pytest.fixture(scope="session")
def ensure_demo_app(base_url: str) -> None:
    try:
        response = requests.get(f"{base_url}/api/state", timeout=3)
        response.raise_for_status()
    except Exception as exc:  # pragma: no cover - fixture guard
        pytest.fail(
            f"Demo app is not reachable at {base_url}. Start it with "
            f"'python -m demo_app.app'. Original error: {exc}"
        )


@pytest.fixture()
def api_reset(base_url: str, ensure_demo_app: None) -> None:
    response = requests.get(f"{base_url}/reset", timeout=3)
    response.raise_for_status()


@pytest.fixture()
def chrome_driver(base_url: str, ensure_demo_app: None):
    options = ChromeOptions()
    if os.getenv("SELENIUM_HEADLESS", "false").lower() == "true":
        options.add_argument("--headless=new")
    options.add_argument("--window-size=1440,1200")
    options.add_argument("--disable-gpu")

    driver = webdriver.Chrome(options=options)
    driver.implicitly_wait(5)
    yield driver

    allure.attach(
        driver.get_screenshot_as_png(),
        name="selenium-final-state",
        attachment_type=allure.attachment_type.PNG,
    )
    driver.quit()


def attach_json(name: str, payload: object) -> None:
    allure.attach(
        json.dumps(payload, indent=2),
        name=name,
        attachment_type=allure.attachment_type.JSON,
    )
