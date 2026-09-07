Feature: Positive travel allowance scenarios

  Scenario: Approved employee gets a valid travel allowance decision
    Given the employee has valid travel allowance data
    And the employee age is 42
    And the work distance is 35 miles
    And the household income is 95000
    And the housing burden is within policy limits
    When the travel allowance request is submitted
    Then the form should pass schema validation
    And the policy validation should pass
    And the semantic validation should pass
    And the final allowance should be 105 per day

  Scenario: Daily commuter with standard policy profile is approved
    Given the employee is a married worker with 2 dependents
    And the travel frequency is Daily
    And the transportation type is Car
    And the mortgage, tax, and insurance are within approved thresholds
    When the request is processed
    Then the decision document should include the employee name
    And the final allowance should be greater than 0
    And the approval status should be approved
