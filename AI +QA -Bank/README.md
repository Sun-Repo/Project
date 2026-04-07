# AI Security Testing Demo Framework

Runnable demo framework for security testing of AI adversarial attack scenarios:

- Data poisoning
- Model theft / model extraction
- Browser automation with Selenium
- Allure reporting

## Stack

- Python 3.11+
- Flask demo app
- Pytest
- Selenium
- Allure Pytest

## Project Structure

- `demo_app/` local vulnerable AI demo application
- `config/` runtime configuration
- `pages/` page objects for Selenium
- `tests/web/` Selenium scenarios
- `scripts/` PowerShell helpers to run the demo
- `reports/` generated test output

## Demo Scenarios

### 1. Data Poisoning

The demo app exposes a training-data ingestion feature. Tests submit poisoned records that intentionally flip labels and verify:

- poisoning is accepted by the vulnerable path
- model behavior drifts after poisoning
- attack telemetry is captured

### 2. Model Theft

The demo app exposes a prediction endpoint and UI that can be queried repeatedly. Tests simulate extraction-style behavior and verify:

- excessive query activity is detected
- the app surfaces model-theft alerts
- evidence is attached into Allure

## Quick Start

### 1. Create a virtual environment

```powershell
python -m venv .venv
.venv\Scripts\Activate.ps1
pip install -r requirements.txt
```

### 2. Start the vulnerable demo app

```powershell
python -m demo_app.app
```

App URL:

- `http://127.0.0.1:5000`

### 3. Run Selenium tests

```powershell
pytest tests/web --alluredir reports/allure-results
```

### 4. Generate Allure report

```powershell
allure serve reports/allure-results
```

## One-Command Helpers

Start app:

```powershell
.\scripts\start_demo_app.ps1
```

Run web tests:

```powershell
.\scripts\run_web_tests.ps1
```

## Environment Variables

- `DEMO_BASE_URL` default: `http://127.0.0.1:5000`
- `SELENIUM_HEADLESS` default: `false`

## Notes

- Selenium uses Selenium Manager to provision the browser driver automatically.
- The app is intentionally vulnerable by design because it is a training demo.
