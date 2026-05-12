package com.mobilewright.bdd;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = "com.mobilewright.steps",
        plugin = {
                "pretty",
                "html:../output/CucumberSauceDemoBddReport.html",
                "json:../output/CucumberSauceDemoBddReport.json"
        },
        monochrome = true
)
public class RunCucumberTest extends AbstractTestNGCucumberTests {
}
