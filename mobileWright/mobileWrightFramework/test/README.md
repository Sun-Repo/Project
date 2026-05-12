# MobileWright SauceDemo Android Chrome Test Suite

This test suite uses Appium, Selenium WebDriver, Cucumber BDD, TestNG, ExtentReports, and Allure to test SauceDemo on Android Chrome. It opens `https://www.saucedemo.com/?utm_source=chatgpt.com`, reads the login page, signs in with each requested user using data from `src/test/resources/data/sauce-users.csv`, retries until the expected use case passes, handles common Chrome/Android popups, and runs login plus checkout feature files.

## Structure
- `pom.xml`: Maven dependencies and TestNG build config
- `src/test/resources/features/saucedemo_mobile_login.feature`: positive and negative BDD scenarios
- `src/test/resources/data/sauce-users.csv`: usernames, passwords, and expected outcomes
- `src/test/java/com/mobilewright/config`: runtime configuration
- `src/test/java/com/mobilewright/driver`: Android Chrome driver and popup handling
- `src/test/java/com/mobilewright/pages`: SauceDemo page objects
- `src/test/java/com/mobilewright/steps`: Cucumber step definitions
- `src/test/java/com/mobilewright/bdd/RunCucumberTest.java`: Cucumber TestNG runner
- `../output/CucumberSauceDemoBddReport.html`: generated BDD HTML report
- `../output/SauceDemoMobileReport.html`: generated Extent HTML report
- `../output/allure-report/allure-results`: permanent Allure results folder
- `../output/allure-report/artifacts/<timestamp>`: timestamped screenshots and videos attached to Allure
- `../output/allure-report/html/<timestamp>`: generated Allure HTML report from `mvn allure:report`
- `../output/screenshots/locked_out_user_secret_sauce.png`: negative scenario screenshot named with the data used

## How to Run
1. Ensure Java 11+, Maven, Node.js, Appium, and the Appium UiAutomator2 driver are installed.
2. Start the Pixel 10 Pro AVD from Android Studio Device Manager and make sure Chrome is installed.
3. Confirm the Pixel 10 Pro emulator is visible:
   ```
   %LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe devices -l
   ```
4. Start Appium in a separate terminal:
   ```
   powershell -ExecutionPolicy Bypass -File .\start-appium.ps1
   ```
5. In this `test` folder, run:
   ```
   mvn test allure:report -DdeviceUdid=emulator-5554
   ```
   This runs both feature files in `src/test/resources/features`.
6. View the generated reports:
   - Extent: `../output/SauceDemoMobileReport.html`
   - Cucumber: `../output/CucumberSauceDemoBddReport.html`
   - Allure: latest folder under `../output/allure-report/html`

## Android Studio Run
Open the `test` folder in Android Studio, start the `Pixel_10_Pro` AVD, then create a Maven run configuration with:
```
test allure:report -DdeviceUdid=emulator-5554
```
The default device is already `emulator-5554`, so the property can be omitted when only the Pixel 10 Pro emulator is connected.

## Runtime Options
- `-DdeviceUdid=emulator-5554`: Android device ID for the Pixel 10 Pro emulator.
- `-DappiumUrl=http://127.0.0.1:4723/`: Appium server URL.
- `-DtargetUrl=https://www.saucedemo.com/?utm_source=chatgpt.com`: target site.
- `-DmaxLoginRetries=3`: retry count per user.
- `-DoutputDir=../output`: report output folder.
- `-Dallure.results.directory=../output/allure-report/allure-results`: Allure results folder.
- `-DallureArtifactsDir=../output/allure-report/artifacts`: timestamped screenshot and video folder.

---
`locked_out_user` is expected to fail login with the SauceDemo locked-out message; that negative validation is reported as passed. All other users are expected to log in and then log out.
