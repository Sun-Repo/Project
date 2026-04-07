Start-Process powershell -ArgumentList "-NoExit", "-Command", "python -m demo_app.app"
Start-Sleep -Seconds 4
pytest tests/web --alluredir reports/allure-results
