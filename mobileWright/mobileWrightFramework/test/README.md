# MobileWright SauceDemo Android Chrome Test Suite

This test suite uses Appium, Selenium WebDriver, Cucumber BDD, TestNG, and ExtentReports to test SauceDemo on a real Android device in Chrome. It opens `https://www.saucedemo.com/?utm_source=chatgpt.com`, reads the login page, signs in with each requested user using data from `src/test/resources/data/sauce-users.csv`, retries until the expected use case passes, handles common Chrome/Android popups, and logs out after successful logins.

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
- `../output/screenshots/locked_out_user_secret_sauce.png`: negative scenario screenshot named with the data used

## How to Run
1. Ensure Java 11+, Maven, Node.js, Appium, and the Appium UiAutomator2 driver are installed.
2. Connect the Samsung phone, enable USB debugging, and make sure Chrome is installed.
3. Confirm the device is visible:
   ```
   %LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe devices -l
   ```
4. Start Appium in a separate terminal:
   ```
   powershell -ExecutionPolicy Bypass -File .\start-appium.ps1
   ```
5. In this `test` folder, run:
   ```
   mvn test -DdeviceUdid=R5CR82MHA1T
   ```
6. View the HTML report at `../output/SauceDemoMobileReport.html`.
   The Cucumber BDD report is generated at `../output/CucumberSauceDemoBddReport.html`.

## Runtime Options
- `-DdeviceUdid=R5CR82MHA1T`: Android device ID. The connected Samsung currently appears as `R5CR82MHA1T`.
- `-DappiumUrl=http://127.0.0.1:4723/`: Appium server URL.
- `-DtargetUrl=https://www.saucedemo.com/?utm_source=chatgpt.com`: target site.
- `-DmaxLoginRetries=3`: retry count per user.
- `-DoutputDir=../output`: report output folder.

---
`locked_out_user` is expected to fail login with the SauceDemo locked-out message; that negative validation is reported as passed. All other users are expected to log in and then log out.
