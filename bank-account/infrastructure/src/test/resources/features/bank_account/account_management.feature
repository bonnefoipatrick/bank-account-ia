@bank-account
Feature: Account Management
  As a bank customer
  I want to manage my bank accounts
  So that I can perform financial transactions

  Background:
    Given the bank account service is running

  Scenario: Create a new bank account
    Given I have a valid customer ID
    When I create a new account with number "FR7612345678901234567890123" and initial balance 1000.00 EUR
    Then the account should be created successfully
    And the account should have the correct details
    And the account should be active

  Scenario: Create account with duplicate number
    Given I have a valid customer ID
    And an account with number "DUPLICATE_ACCOUNT" already exists
    When I try to create a new account with number "DUPLICATE_ACCOUNT"
    Then the account creation should fail with conflict error
    And the error message should contain "already exists"

  Scenario: Create account with invalid currency
    Given I have a valid customer ID
    When I try to create a new account with currency "INVALID"
    Then the account creation should fail with bad request error
    And the error message should contain "Currency must be a 3-letter code"

  Scenario: Get account by ID
    Given I have a valid customer ID
    And I have created an account
    When I request the account by its ID
    Then I should receive the account details
    And the response should contain the account number

  Scenario: Get non-existent account
    Given I have a valid customer ID
    When I request an account with a non-existent ID
    Then I should receive a not found error
    And the error message should contain "not found"

  Scenario: Deposit money into account
    Given I have a valid customer ID
    And I have created an account with balance 1000.00 EUR
    When I deposit 500.00 EUR into the account
    Then the deposit should be successful
    And the new balance should be 1500.00 EUR
    And a deposit transaction should be recorded

  Scenario: Deposit negative amount
    Given I have a valid customer ID
    And I have created an account
    When I try to deposit -100.00 EUR
    Then the deposit should fail with bad request error
    And the error message should contain "positive"

  Scenario: Withdraw money from account
    Given I have a valid customer ID
    And I have created an account with balance 1000.00 EUR
    When I withdraw 500.00 EUR from the account
    Then the withdrawal should be successful
    And the new balance should be 500.00 EUR
    And a withdrawal transaction should be recorded

  Scenario: Withdraw with insufficient balance
    Given I have a valid customer ID
    And I have created an account with balance 100.00 EUR
    When I try to withdraw 500.00 EUR from the account
    Then the withdrawal should fail with bad request error
    And the error message should contain "Insufficient balance"

  Scenario: Transfer money between accounts
    Given I have two valid customer IDs
    And I have created two accounts with balances 1000.00 EUR and 500.00 EUR
    When I transfer 300.00 EUR from the first account to the second
    Then the transfer should be successful
    And the first account balance should be 700.00 EUR
    And the second account balance should be 800.00 EUR
    And transfer transactions should be recorded for both accounts

  Scenario: Transfer with insufficient balance
    Given I have two valid customer IDs
    And I have created two accounts with balances 100.00 EUR and 500.00 EUR
    When I try to transfer 300.00 EUR from the first account to the second
    Then the transfer should fail with bad request error
    And the error message should contain "Insufficient balance"

  Scenario: Deactivate account
    Given I have a valid customer ID
    And I have created an account
    When I deactivate the account
    Then the account should be deactivated
    And the account status should be inactive

  Scenario: Get account transactions
    Given I have a valid customer ID
    And I have created an account
    And I have performed several transactions
    When I request the account transactions
    Then I should receive a list of transactions
    And the transactions should be ordered by creation date
