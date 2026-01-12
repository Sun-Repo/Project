// Explanations:
//----------- String is immutable

// Each += creates:

// A new String object

// Copies old characters + new character

// Time complexity → O(n²)

// High GC pressure due to many temporary objects

//-------------- StringBuilder is mutable
// “Strings are immutable in Java. Using + inside loops creates new objects and increases GC overhead.
// I use StringBuilder with pre-allocated capacity for logging, reporting, API payloads, and large file processing to achieve linear time complexity and reduce memory churn.”

// Uses a resizable char array

// Appends without creating new objects

// Time complexity → O(n)

//------------ USE Case
// Test report generation

// Log message building

// API response aggregation

// ---------------Use Case: Selenium / TestNG reporting
String log = "";
log += "Test Name: " + testName;
log += " Status: " + status;
log += " Duration: " + time;
// Optimized
StringBuilder log = new StringBuilder(100);
log.append("Test Name: ").append(testName)
   .append(" Status: ").append(status)
   .append(" Duration: ").append(time);
// ----------------Use Case: Extent / Allure report generation
String report = "";
for (TestResult result : results) {
    report += "<tr><td>" + result.getName() + "</td></tr>";
}
// Optimized
StringBuilder report = new StringBuilder(5000);
for (TestResult result : results) {
    report.append("<tr><td>")
          .append(result.getName())
          .append("</td></tr>");
}
// ------------------------------Use Case: Backend validation tests
String query = "SELECT * FROM users";
if (active) query += " WHERE active = 1";
if (admin) query += " AND role = 'ADMIN'";
// optimised
StringBuilder query = new StringBuilder("SELECT * FROM users WHERE 1=1");
if (active) query.append(" AND active = 1");
if (admin) query.append(" AND role = 'ADMIN'");
// -----------------Use Case: RestAssured request bodies
String payload = "{";
payload += "\"name\":\"John\",";
payload += "\"role\":\"QA\"";
payload += "}";
// Optimized
StringBuilder payload = new StringBuilder();
payload.append("{")
       .append("\"name\":\"John\",")
       .append("\"role\":\"QA\"")
       .append("}");
// ------------Log analysis, test data files
String content = "";
while ((line = reader.readLine()) != null) {
    content += line;
}
// Optimized
StringBuilder content = new StringBuilder(10_000);
while ((line = reader.readLine()) != null) {
    content.append(line);
}

