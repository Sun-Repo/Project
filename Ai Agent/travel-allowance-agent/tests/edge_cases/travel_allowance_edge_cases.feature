Feature: Edge cases for travel allowance validation

  Scenario: Age below minimum eligibility is rejected
    Given the employee age is 17
    When the application validates the request
    Then the policy validation should fail
    And the violation should mention age below minimum eligibility threshold

  Scenario: Distance to work is zero
    Given the employee distance to work is 0
    When the request is submitted
    Then the validation should fail
    And the violation should mention distance_to_work must be greater than zero

  Scenario: High vehicle burden should trigger the transportation policy
    Given the employee monthly gross income is 5000
    And the car loan payment is 1000
    When the request is evaluated
    Then the vehicle burden should exceed the threshold
    And the policy should flag transportation support rules

  Scenario: Document mentions travel but not the required details
    Given the final document includes generic text only
    And it does not mention employee name or distance
    When semantic validation is performed
    Then the semantic validation should fail
    And the message should indicate missing required travel details
