@tag
Feature: Test Feature

  Scenario Outline: Test Scenario
    Given the Test actor
    When some datatable
      | value     |
      | value2    |
      | <example> |
    And some 1, 2, 3 parameters
    And some <example> example
    Then should pass
    Examples:
      | example  |
      | example1 |
      | example2 |
      | example3 |
