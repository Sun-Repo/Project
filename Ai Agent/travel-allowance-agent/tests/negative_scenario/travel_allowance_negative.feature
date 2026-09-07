Feature: Negative travel allowance scenarios

  Scenario: Employee with excessive housing burden is rejected
    Given the employee has a monthly gross income of 16666
    And the mortgage payment is 7500
    And the property tax is 1500
    And the home insurance is 500
    When the travel allowance request is submitted
    Then the policy validation should fail
    And the violation should mention housing burden

  Scenario: Employee with unsupported or missing information is rejected
    Given the employee data lacks required fields
    And the age is missing
    And the distance to work is missing
    When the request is processed
    Then the schema validation should fail
    And the missing fields list should include age and distance_to_work
