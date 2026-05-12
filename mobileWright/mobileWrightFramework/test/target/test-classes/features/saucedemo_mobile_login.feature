Feature: SauceDemo mobile login on Android Chrome

  Background:
    Given the SauceDemo login page is opened on Android Chrome
    And the login page is readable

  @positive
  Scenario Outline: Successful users can login and logout
    When I login with retry using sauce user data "<userKey>"
    Then the sauce user should reach the inventory page
    And the sauce user logs out successfully

    Examples:
      | userKey                   |
      | standard_user             |
      | problem_user              |
      | performance_glitch_user   |
      | error_user                |
      | visual_user               |

  @negative
  Scenario Outline: Locked out user sees the expected validation message
    When I login with retry using sauce user data "<userKey>"
    Then the sauce user should see the expected login error
    And a negative scenario screenshot is stored with the user data name

    Examples:
      | userKey         |
      | locked_out_user |
