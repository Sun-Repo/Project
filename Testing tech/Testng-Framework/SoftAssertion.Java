Forgetting assertAll() (Most Common Mistake)
❌ Wrong
@Test
public void loginTest() {
    SoftAssert softAssert = new SoftAssert();
    softAssert.assertTrue(false, "Login button missing");
}

Test passes even though assertion failed

softAssert.assertAll();
“Without assertAll(), SoftAssert failures are never reported.”
==================================================================================
Using SoftAssert for Critical Flow Validation
Wrong
softAssert.assertTrue(isLoggedIn(), "Login failed");
navigateToDashboard(); // still runs!

Problem
App state is invalid
Subsequent steps fail misleadingly

Assert.assertTrue(isLoggedIn(), "Login failed");

“SoftAssert should not be used when test flow must stop.”
=======================================================================
Reusing SoftAssert Across Test Methods
Wrong
public class TestClass {
    SoftAssert softAssert = new SoftAssert();

    @Test
    public void testOne() {
        softAssert.assertTrue(false);
    }

    @Test
    public void testTwo() {
        softAssert.assertTrue(true);
        softAssert.assertAll();
    }
}
Issue
Failure from testOne appears in testTwo

@BeforeMethod
public void setup() {
    softAssert = new SoftAssert();
}

“SoftAssert must be created per test method.”
========================================================
Using SoftAssert Inside Loops Incorrectly
Wrong
for (WebElement e : elements) {
    softAssert.assertTrue(e.isDisplayed());
}
softAssert.assertAll();

Issue
Hard to know which element failed
Poor diagnostics

int index = 0;
for (WebElement e : elements) {
    softAssert.assertTrue(
        e.isDisplayed(),
        "Element not displayed at index: " + index++
    );
}
softAssert.assertAll();
“Always add meaningful messages inside loops.”
=========================================================
Using SoftAssert in Listeners
Wrong
public void onTestFailure(ITestResult result) {
    SoftAssert softAssert = new SoftAssert();
    softAssert.fail("Listener failure");
    softAssert.assertAll();
}

Wrong
Listeners are not meant for assertions
Can break TestNG lifecycle


Use logging or reporting only
Assertions belong in test methods
=========================================================
Using SoftAssert with RetryAnalyzer 
Wrong
softAssert.assertTrue(false);
softAssert.assertAll();

Risky
RetryAnalyzer retries entire test
SoftAssert hides which assertion caused retry

Combine soft asserts carefully

Log assertion messages clearly
=========================================================
Using SoftAssert for API Status Codes 
Wrong
softAssert.assertEquals(response.getStatusCode(), 200);
parseResponseBody(); // still executes

Problem
Parsing invalid responses
Cascading failures

Assert.assertEquals(response.getStatusCode(), 200);
==========================================================
SoftAssert in Parallel Execution Without Isolation ❌
 Wrong
static SoftAssert softAssert = new SoftAssert();
Problem
Cross-thread failures
Flaky results

ThreadLocal<SoftAssert> softAssert = ThreadLocal.withInitial(SoftAssert::new);
(Only if truly required)