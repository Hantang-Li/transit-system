package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import transitSystem.*;

/**
 * A CardActivity Controller that accepts inputs from CardActivity interface and converts them to
 * commands.
 */
public class CardActivityController {

  /** An AnchorPane that represents card activity page. */
  @FXML private AnchorPane cardActivityPane;
  /** A MenuButton that for a user to select a card. */
  @FXML private MenuButton cardMenuButton;
  /** A Label that represents all card activities. */
  @FXML private Label text1;

  /** Resets card activity when user switches a card, i.e. a user wants to check another card. */
  void resetCardActivity() {
    AccountManager accountManager = LoginController.adminUser.getAccountManager();
    CardHolderAccount account =
        accountManager.findAccount(AccountBarController.currentAccountEmail);
    // clear all card activities
    cardMenuButton.getItems().clear();
    cardMenuButton.setText("");
    text1.setText("");
    // add cards the user holds
    for (Integer cardId : account.getCards()) {
      MenuItem menuItem = new MenuItem(String.format("Card %d", cardId));
      menuItem.setOnAction(event -> cardMenuButton.setText(menuItem.getText()));
      cardMenuButton.getItems().add(menuItem);
    }
  }

  /** Shows card activities when user clicking "confirm" button. */
  @FXML
  public void clickConfirm() {
    int cardId = Integer.parseInt(cardMenuButton.getText().substring(5));
    AdminUser adminUser = LoginController.adminUser;
    CardManager cardManager = adminUser.getCardManager();
    TransitPass card = cardManager.findCard(cardId);
    String activity =
        String.format(
            "%s"
                + System.getProperty("line.separator")
                + System.getProperty("line.separator")
                + "View three recent trips:"
                + "%s",
            card.toString(),
            adminUser.viewTrips(cardId));
    // calculate the number of lines of account activity
    int line = countLines(card.toString()) + countLines(adminUser.viewTrips(cardId)) + 1;
    // resize the label
    text1.setPrefHeight(line * 28);
    text1.setText(activity);
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

  /**
   * Gets the AnchorPane that represents card activity page.
   *
   * @return An AnchorPane that represents card activity page.
   */
  AnchorPane getCardActivityPane() {
    return cardActivityPane;
  }
}
