Feature: SauceDemo mobile checkout on Android Chrome

  Background:
    Given the SauceDemo login page is opened on Android Chrome
    And the login page is readable

  @checkout @positive
  Scenario: Add every fourth product and validate checkout overview before finishing
    When I login with retry using sauce user data "standard_user"
    Then the sauce user should reach the inventory page
    When I add every 4th product to the cart
    And I checkout using customer data "sunai_murugan"
    Then the checkout overview should show the selected products and valid payment shipping and price totals
    When I finish the checkout
    Then the checkout should complete successfully

  @checkout @edge @positive
  Scenario: Add all products and validate checkout overview before finishing
    When I login with retry using sauce user data "standard_user"
    Then the sauce user should reach the inventory page
    When I add all products to the cart
    And I checkout using customer data "sunai_murugan"
    Then the checkout overview should show the selected products and valid payment shipping and price totals
    When I finish the checkout
    Then the checkout should complete successfully

  @checkout @edge
  Scenario: Empty cart checkout overview shows zero totals
    When I login with retry using sauce user data "standard_user"
    Then the sauce user should reach the inventory page
    When I open checkout from the cart without adding products
    And I submit checkout information using customer data "sunai_murugan"
    Then the checkout overview should show no products and zero price totals

  @checkout @negative
  Scenario Outline: Checkout information requires customer fields
    When I login with retry using sauce user data "standard_user"
    Then the sauce user should reach the inventory page
    When I add every 4th product to the cart
    And I open the checkout information page
    And I submit checkout information using customer data "<customerKey>"
    Then the checkout information page should show required field error "<expectedError>"
    And a checkout failure screenshot is stored as "<screenshotName>"

    Examples:
      | customerKey             | expectedError                | screenshotName                    |
      | missing_first_name      | First Name is required       | checkout-missing-first-name       |
      | missing_last_name       | Last Name is required        | checkout-missing-last-name        |
      | missing_zip_code        | Postal Code is required      | checkout-missing-zip-code         |
      | empty_checkout_customer | First Name is required       | checkout-empty-customer           |

  @checkout @edge
  Scenario: Cancel checkout information returns to cart
    When I login with retry using sauce user data "standard_user"
    Then the sauce user should reach the inventory page
    When I add every 4th product to the cart
    And I open the checkout information page
    And I cancel checkout from the information page
    Then the sauce cart should still show the selected products

  @checkout @edge
  Scenario: Cancel checkout overview returns to inventory
    When I login with retry using sauce user data "standard_user"
    Then the sauce user should reach the inventory page
    When I add every 4th product to the cart
    And I checkout using customer data "sunai_murugan"
    And I cancel checkout from the overview page
    Then the sauce user should reach the inventory page
 