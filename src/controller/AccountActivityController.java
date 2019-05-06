package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import transitSystem.AccountManager;
import transitSystem.AdminUser;

/**
 * An AccountActivity Controller that accepts inputs from AccountActivity interface and converts
 * them to commands.
 */
public class AccountActivityController {

  /** A ScrollPane that represents the account activity page. */
  @FXML private ScrollPane accountActivityPane;

  /** A Label that represents all account activities. */
  @FXML private Label accountActivityLabel;

  /**
   * Gets the account activity page.
   *
   * @return A ScrollPane that represents the account activity page.
   */
  ScrollPane getAccountActivityPane() {
    return accountActivityPane;
  }

  /** Resizes accountActivityLabel to make the ScrollPane showing all activity records. */
  void resetLabel() {
    AdminUser adminUser = LoginController.adminUser;
    AccountManager accountManager = adminUser.getAccountManager();
    // get the system last closed date
    String date = adminUser.getLastDateOpen();
    String accountInfo = adminUser.accountViewTrips(AccountBarController.currentAccountEmail);
    // get average costs per month
    double averageCostMonth =
        accountManager.trackAccountAverageCost(
            AccountBarController.currentAccountEmail, date.substring(0, 7));
    double totalCostToday = accountManager.trackTotalCostPerDay(date);
    // generate the report for the cost per month
    String costPerMonth =
        "Average transit cost in "
            + date.substring(0, 7)
            + ":  "
            + String.format("%.2f", averageCostMonth);
    String costToday = "Total cost on " + date + ": " + String.format("%.2f", totalCostToday);
    int numLine = countLines(accountInfo) + 2;
    accountActivityLabel.setPrefHeight((numLine + 1) * 28);
    accountActivityLabel.setText(
        costToday
            + System.getProperty("line.separator")
            + costPerMonth
            + System.getProperty("line.separator")
            + accountInfo);
  }

  /**
   * Calculates the number of lines of account activity.
   *
   * @param str A block of records.
   * @return The number of lines the input record has.
   */
  private int countLines(String str) {
    String[] lines = str.split(System.getProperty("line.separator"));
    return lines.length;
  }
}
