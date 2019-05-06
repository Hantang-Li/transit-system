package controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import transitSystem.*;

/**
 * A ManageCard Controller that accepts inputs from ManageMyCard interface and converts them to
 * commands.
 */
public class ManageCardController {

  /**
   * An UnbindAlertAlert Controller that accepts inputs from alert interfaces and converts them to
   * commands.
   */
  private UnbindAlertController unbindAlertController;
  /** An AnchorPane that represents user card management page. */
  @FXML private AnchorPane manageCardPane;
  /** A MenuButton that for a user to select a card. */
  @FXML private MenuButton menuButton;

  /**
   * Gets the AnchorPane that represents user card management page.
   *
   * @return An AnchorPane that represents user card management page.
   */
  AnchorPane getManageCardPane() {
    return manageCardPane;
  }

  /** Resets card when user switches a card, i.e. a user wants to manipulate another card. */
  void resetManageCard() {
    AccountManager accountManager = LoginController.adminUser.getAccountManager();
    CardHolderAccount account =
        accountManager.findAccount(AccountBarController.currentAccountEmail);
    // clear all menu items in the menu button
    menuButton.getItems().clear();
    menuButton.setText("");
    // add cards the user holds
    for (Integer cardId : account.getCards()) {
      MenuItem menuItem = new MenuItem(String.format("Card %d", cardId));
      menuItem.setOnAction(
          event -> {
            // ignore this
            menuButton.setText(menuItem.getText());
          });
      menuButton.getItems().add(menuItem);
    }
  }

  /**
   * Suspends the selected card when user clicking "suspend the card" button.
   *
   * @throws IOException An IOException to handle invalid inputs.
   */
  @FXML
  public void clickSuspendCard() throws IOException {
    AdminUser adminUser = LoginController.adminUser;
    CardManager cardManager = adminUser.getCardManager();
    String card = menuButton.getText();
    // check if there is an valid card
    if (card.equals("")) {
      AlertController.failAlert("Please enter the card id!");
    } else {
      int cardId = Integer.parseInt(card.substring(5));
      TransitPass transitPass = cardManager.findCard(cardId);
      String cardType = transitPass.getCardType();
      // check if a card has been suspended before
      if (transitPass.isSuspended()) {
        AlertController.failAlert("This card is now suspended.");
      } else {
        cardManager.suspendCard(cardId);
        AlertController.successAlert(
            String.format(
                "%s %d has been successfully" + System.getProperty("line.separator") + "suspended.",
                cardType,
                cardId));
      }
    }
  }

  /**
   * Activates the card when user clicking "activate the card" button.
   *
   * @throws IOException An IOException to handle invalid inputs.
   */
  @FXML
  public void clickActivateCard() throws IOException {
    AdminUser adminUser = LoginController.adminUser;
    CardManager cardManager = adminUser.getCardManager();
    String card = menuButton.getText();
    // check if there is an valid card
    if (card.equals("")) {
      AlertController.failAlert("Please enter the card id!");
    } else {
      int cardId = Integer.parseInt(card.substring(5));
      TransitPass transitPass = cardManager.findCard(cardId);
      String cardType = transitPass.getCardType();
      if (transitPass.isSuspended()) {
        cardManager.activateCard(cardId);
        AlertController.successAlert(
            String.format(
                "%s %d has been successfully" + System.getProperty("line.separator") + "activated.",
                cardType,
                cardId));
        // check if a card has been activated before
      } else {
        AlertController.failAlert("This card is activated now.");
      }
    }
  }

  /**
   * Unbinds the card when user clicking "unbind the card" button.
   *
   * @throws IOException An IOException to handle invalid inputs.
   */
  @FXML
  public void clickUnbindCard() throws IOException {
    AdminUser adminUser = LoginController.adminUser;
    CardManager cardManager = adminUser.getCardManager();
    AccountManager accountManager = adminUser.getAccountManager();
    String card = menuButton.getText();
    double balance = 0;
    // check if there is an valid card
    if (card.equals("")) {
      AlertController.failAlert("Please enter the card id!");
    } else {
      int cardId = Integer.parseInt(card.substring(5));
      accountManager.unbindCardToAccount(AccountBarController.currentAccountEmail, cardId);
      TransitPass transitPass = cardManager.findCard(cardId);
      String cardType = transitPass.getCardType();
      String information;
      // check the type of the card that user wants to unbind
      if (transitPass instanceof WeeklyPass) {
        information =
            "Your card is Weekly Pass."
                + System.getProperty("line.separator")
                + "You will not get money back from this card."
                + System.getProperty("line.separator")
                + "Do you still want to unbind?";
      } else {
        TransitPass topUpCard = cardManager.findCard(cardId);
        information =
            String.format(
                "Your card is %s."
                    + System.getProperty("line.separator")
                    + "You will get $%s refund from this card."
                    + System.getProperty("line.separator")
                    + "Do you still want to unbind? ",
                cardType,
                topUpCard.viewBalance());
        balance = topUpCard.viewBalance();
      }
      Stage stage = new Stage();
      // pops up an alert dialog
      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/Alert.fxml"));
      AnchorPane anchorPane = fxmlLoader.load();
      unbindAlertController = fxmlLoader.getController();
      unbindAlertController.resetUnbindController(information, this, cardId, balance);
      Scene scene = new Scene(anchorPane);
      stage.setScene(scene);
      stage.show();
    }
  }
}
