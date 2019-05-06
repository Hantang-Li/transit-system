package controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import transitSystem.*;

/**
 * A LoadMyCard Controller that accepts inputs from LoadMyCard interface and converts them to
 * commands.
 */
public class LoadMyCardController {

  /** An AnchorPane that represents the card loading page. */
  @FXML private AnchorPane loadCardPane;
  /** A MenuButton that for a user to select a card. */
  @FXML private MenuButton cardMenuButton;
  /** A MenuButton that for a user to select the amount of balance. */
  @FXML private MenuButton fundMenuButton;
  /**
   * An AccountBar Controller that accepts inputs from SideBar interface and converts them to
   * commands.
   */
  private AccountBarController accountBarController;

  /**
   * Sets An AccountBar Controller.
   *
   * @param accountBarController An AccountBar Controller that accepts inputs from SideBar interface
   *     and converts them to commands.
   */
  public void setAccountBarController(AccountBarController accountBarController) {
    this.accountBarController = accountBarController;
  }

  /**
   * Gets the Pane that represents for card loading page.
   *
   * @return An AnchorPane that represents the card loading page.
   */
  AnchorPane getLoadCardPane() {
    return loadCardPane;
  }

  /** Clears menu items in card loading page. */
  void resetLoadMyCard() {
    AccountManager accountManager = LoginController.adminUser.getAccountManager();
    String currentAccountEmail = AccountBarController.currentAccountEmail;
    CardHolderAccount account = accountManager.findAccount(currentAccountEmail);
    // clear the menu items
    cardMenuButton.getItems().clear();
    cardMenuButton.setText("");
    fundMenuButton.setText("");
    fundMenuButton.getItems().clear();
    // search for cards
    for (Integer cardId : account.getCards()) {
      MenuItem menuItem = new MenuItem(String.format("Card %d", cardId));
      menuItem.setOnAction(
          event -> {
            cardMenuButton.setText(menuItem.getText());
            try {
              resetSelectFunds();
            } catch (IOException e) {
              e.printStackTrace();
            }
          });
      cardMenuButton.getItems().add(menuItem);
    }
  }

  /**
   * Resets the choices of top up amount based on the card that user selected.
   *
   * @throws IOException An IOException that in case of missing GUI file.
   */
  private void resetSelectFunds() throws IOException {
    fundMenuButton.setText("");
    fundMenuButton.getItems().clear();
    AdminUser adminUser = LoginController.adminUser;
    CardManager cardManager = adminUser.getCardManager();
    // determine the type of card user selected
    TransitPass card =
        cardManager.findCard(Integer.parseInt(cardMenuButton.getText().substring(5)));
    if (card instanceof TrafficCard) {
      MenuItem menuItem1 = new MenuItem("$10");
      MenuItem menuItem2 = new MenuItem("$20");
      MenuItem menuItem3 = new MenuItem("$50");
      fundMenuButton.getItems().add(menuItem1);
      fundMenuButton.getItems().add(menuItem2);
      fundMenuButton.getItems().add(menuItem3);
    } else if (card instanceof TimesPass) {
      MenuItem menuItem4 = new MenuItem("5 times");
      MenuItem menuItem5 = new MenuItem("10 times");
      MenuItem menuItem6 = new MenuItem("25 times");
      fundMenuButton.getItems().add(menuItem4);
      fundMenuButton.getItems().add(menuItem5);
      fundMenuButton.getItems().add(menuItem6);
    }
    // reset the menu items
    for (MenuItem menuItem : fundMenuButton.getItems()) {
      menuItem.setOnAction(event -> fundMenuButton.setText(menuItem.getText()));
    }
    // check if the user attempts to top up weekly pass
    fundMenuButton.setOnMouseClicked(
        event -> {
          if (card instanceof WeeklyPass) {
            try {
              AlertController.failAlert("Weekly pass can not be topped up!");
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        });
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
    // get selected card information
    String cardText = cardMenuButton.getText();
    String fund = fundMenuButton.getText();
    int fare = 0;
    // check if card selection is valid
    if (cardText == null) {
      AlertController.failAlert("Please select a card!");
    } else {
      int id = Integer.parseInt(cardText.substring(5));
      TransitPass transitPass = cardManager.findCard(id);
      String cardType = transitPass.getCardType();
      if (transitPass instanceof WeeklyPass) {
        AlertController.failAlert("Weekly pass can not be topped up!");
      } else if (fund.equals("")) {
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
        cardManager.topUp(id, fare);
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
                + id
                + System.getProperty("line.separator")
                + "current balance: "
                + balanceInfo;
        AlertController.successAlert(alert);
      }
    }
  }
}
