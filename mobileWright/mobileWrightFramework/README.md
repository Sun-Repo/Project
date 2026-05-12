# MobileWright Framework

This project contains a small Flask travel demo and a Java MobileWright-style BDD automation framework. The current mobile automation suite targets SauceDemo on a real Android Samsung phone using Chrome, Appium UiAutomator2, Cucumber BDD, TestNG, and ExtentReports.

The Android Chrome suite:
- Opens `https://www.saucedemo.com/?utm_source=chatgpt.com`
- Reads and validates the login page
- Logs in with `standard_user`, `problem_user`, `performance_glitch_user`, `error_user`, and `visual_user`
- Validates the expected locked-out message for `locked_out_user`
- Keeps all SauceDemo user credentials and expected outcomes in `test/src/test/resources/data/sauce-users.csv`
- Retries each use case until it passes or reaches the retry limit
- Handles common Chrome and Android popups
- Logs out after each successful login
- Generates BDD and Extent HTML reports in `output/`
- Stores the negative scenario screenshot as `output/screenshots/locked_out_user_secret_sauce.png`

The Flask travel demo allows users to:
- Log in
- Book an SUV for a specific date
- Add payment details
- Log out
- Generate an HTML report of the booking

## Features
- Simple login/logout system
- Booking form for SUV travel
- Payment details entry
- HTML report generation

## Technologies
- Python (Flask for backend)
- HTML/CSS (for frontend and report)
- Java, Maven, Appium, Cucumber, TestNG, ExtentReports for Android Chrome automation

## Run Android Chrome SauceDemo Tests
1. Start Appium:
   ```
   cd test
   powershell -ExecutionPolicy Bypass -File .\start-appium.ps1
   ```
2. Run the mobile suite:
   ```
   mvn test -DdeviceUdid=R5CR82MHA1T
   ```
3. Open the report:
   ```
   output/CucumberSauceDemoBddReport.html
   ```
   The Extent report is also generated at `output/SauceDemoMobileReport.html`.

## How to Run
1. Ensure Python 3.8+ is installed.
2. Install dependencies:
   ```
   pip install flask
   ```
3. Run the app:
   ```
   python app.py
   ```
4. Access the app at http://localhost:5000

## Project Structure
- app.py: Main Flask application
- templates/: HTML templates
- static/: CSS and static files
- .github/: Project instructions

---
This is a demo. For production, add authentication, validation, and secure payment integration.
