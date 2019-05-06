package controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import transitSystem.*;

/** A TopUp Controller that accepts inputs from TopUp interface and converts them to commands. */
public class TopUpController {

  /** A Scene that represents top up page. */
  private Scene topUpScene;
  /** A String that represents current card id. */
  private String currentCardId;
  /** A MenuButton that for a user to select the amount of funds the user wants to load. */
  @FXML private MenuButton menuButton;

  /**
   * Sets the current card id.
   *
   * @param currentCardId A String that represents current card id.
   */
  void setCurrentCardId(String currentCardId) {
    this.currentCardId = currentCardId;
  }

  /**
   * Sets the Scene that represents top up page.
   *
   * @param topUpScene A Scene that represents top up page.
   */
  public void setTopUpScene(Scene topUpScene) {
    this.topUpScene = topUpScene;
  }

  /**
   * Gets the Scene that represents top up page.
   *
   * @return A Scene that represents top up page.
   */
  Scene getTopUpScene() {
    return topUpScene;
  }

  /** Resets the choices of top up amount based on the card that user used. */
  void resetTopUpMenu() {
    menuButton.setText("");
    menuButton.getItems().clear();
    TransitPass card = getTransitPass();
    // determine the type of card user used
    if (card instanceof TrafficCard) {
      MenuItem menuItem1 = new MenuItem("$10");
      MenuItem menuItem2 = new MenuItem("$20");
      MenuItem menuItem3 = new MenuItem("$50");
      menuButton.getItems().add(menuItem1);
      menuButton.getItems().add(menuItem2);
      menuButton.getItems().add(menuItem3);
    } else if (card instanceof TimesPass) {
      MenuItem menuItem4 = new MenuItem("5 times");
      MenuItem menuItem5 = new MenuItem("10 times");
      MenuItem menuItem6 = new MenuItem("25 times");
      menuButton.getItems().add(menuItem4);
      menuButton.getItems().add(menuItem5);
      menuButton.getItems().add(menuItem6);
    }
    // reset the menu items
    for (MenuItem menuItem : menuButton.getItems()) {
      menuItem.setOnAction(event -> menuButton.setText(menuItem.getText()));
    }
  }

  /**
   * Tops up a card based on user's selection.
   *
   * @throws IOException An IOException that in case of missing GUI file.
   */
  @FXML
  public void checkOut() throws IOException {
    AdminUser adminUser = LoginController.adminUser;
    CardManager cardManager = adminUser.getCardManager();
    String fund = menuButton.getText();
    // get card id the user used
    int cardId = Integer.parseInt(currentCardId);
    TransitPass transitPass = cardManager.findCard(cardId);
    // get the type of card the user used
    String cardType = transitPass.getCardType();
    int fare = 0;
    if (fund.equals("")) {
      AlertController.failAlert("Please select the amount of funds!");
    } else {
      if (fund.substring(0, 1).equals("$")) {
        fare = Integer.parseInt(fund.substring(1));
      } else {
        String[] words = fund.split(" ");
        switch (words[0]) {
          case "5":
            fare = 10;
            break;
          case "10":
            fare = 20;
            break;
          case "25":
            fare = 50;
            break;
          default:
            break;
        }
      }
      String balanceInfo;
      cardManager.topUp(cardId, fare);
      double balanceNow = transitPass.viewBalance();
      if (transitPass instanceof TrafficCard) {
        balanceInfo = "$" + balanceNow;
      } else {
        balanceInfo = (int) balanceNow / 2 + " times";
      }
      // a message for successfully topping up a card
      String alert =
          "You successfully topped up"
              + System.getProperty("line.separator")
              + fund
              + " to "
              + cardType
              + " "
              + cardId
              + System.getProperty("line.separator")
              + "current balance: "
              + balanceInfo;
      AlertController.successAlert(alert);
    }
  }

  /**
   * Gets a transit pass.
   *
   * @return A transit pass.
   */
  private TransitPass getTransitPass() {
    menuButton.getItems().clear();
    AdminUser adminUser = LoginController.adminUser;
    CardManager cardManager = adminUser.getCardManager();
    return cardManager.findCard(Integer.parseInt(currentCardId));
  }
}
