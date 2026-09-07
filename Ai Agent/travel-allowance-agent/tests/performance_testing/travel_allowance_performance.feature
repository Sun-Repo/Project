Feature: Performance testing for travel allowance validation

  Scenario: Validate a batch of valid requests in under an acceptable time
    Given 100 valid travel allowance records are submitted
    When the system processes each request
    Then all responses should be returned successfully
    And the average processing time should be under 1 second

  Scenario: Validate a large set of requests without performance degradation
    Given 1000 travel allowance requests are submitted
    When the application evaluates the rules and semantics
    Then no request should timeout
    And the system should remain responsive
