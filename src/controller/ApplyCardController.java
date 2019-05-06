package controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Observer;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import transitSystem.*;

/**
 * An ApplyCard Controller that accepts inputs from AddANewCard interface and converts them to
 * commands.
 */
public class ApplyCardController {

  /** An AnchorPane that represents the card application page. */
  @FXML private AnchorPane applyCardPane;
  /** A string that represents the type of card. */
  private String selectCardType;
  /** A Scene that represents the card application page. */
  private Scene applyCardScene;

  /**
   * Sets the type of the new applied card.
   *
   * @param selectCardType A string that represents the type of card.
   */
  public void setSelectCardType(String selectCardType) {
    this.selectCardType = selectCardType;
  }

  /**
   * Gets the Scene that represents the card application page.
   *
   * @return A Scene that represents the card application page.
   */
  Scene getApplyCardScene() {
    return applyCardScene;
  }

  /**
   * Sets the Scene that represents the card application page.
   *
   * @param applyCardScene A Scene that represents the card application page.
   */
  public void setApplyCardScene(Scene applyCardScene) {
    this.applyCardScene = applyCardScene;
  }

  /**
   * Gets the AnchorPane that represents the card application page.
   *
   * @return An AnchorPane that represents the card application page.
   */
  AnchorPane getApplyCardPane() {
    //        applyCardPane.getChildren().
    return applyCardPane;
  }

  /** Conducts reactions when user clicking buttons. */
  public void setButtonAction() {
    for (Node node : applyCardPane.getChildren()) {
      if (node instanceof Button && node.getId() != null) {
        ((Button) node)
            .setOnAction(
                event -> {
                  String[] s = node.getId().split("_");
                  selectCardType = s[0] + " " + s[1];
                });
      }
    }
  }

  /**
   * Checks out a card when user clicking "check out" buttons.
   *
   * @throws Exception An Exception to handle undesirable error.
   */
  @FXML
  public void checkOut() throws Exception {
    AdminUser adminUser = LoginController.adminUser;
    CardManager cardManager = adminUser.getCardManager();
    AccountManager accountManager = adminUser.getAccountManager();
    String lastDateOpen = adminUser.getLastDateOpen();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Calendar cal1 = Calendar.getInstance();
    cal1.setTime(sdf.parse(lastDateOpen));
    // when user select the common pass
    if (selectCardType != null) {
      if (selectCardType.equals("Traffic Card") || selectCardType.equals("Times Pass")) {
        TransitPassFactory factory = new TransitPassFactory();
        TransitPass transitPass = factory.buildTransitPass(selectCardType);
        cardManager.applyForCard(transitPass);
        String initialBalance = "";
        if (selectCardType.equals("Traffic Card")) {
          initialBalance += "$" + transitPass.viewBalance();
        } else {
          initialBalance += (int) (transitPass).viewBalance() / 2 + " times";
        }
        // success alert message
        String successAlert =
            "You successfully apply for a "
                + selectCardType
                + "."
                + System.getProperty("line.separator")
                + "Card id: "
                + transitPass.getCardId()
                + System.getProperty("line.separator")
                + "Initial balance: "
                + initialBalance;
        AlertController.successAlert(successAlert);
        // check if the users apply this card in their account
        if (AccountBarController.currentAccountEmail != null) {
          accountManager.bindCardToAccount(
              AccountBarController.currentAccountEmail, transitPass.getCardId());
        }
        // when user select the weekly pass
      } else if (selectCardType.equals("Weekly Pass")) {
        TransitPassFactory factory = new TransitPassFactory();
        TransitPass transitPass = factory.buildTransitPass(selectCardType, cal1);
        adminUser.addObserver((Observer) transitPass);
        cardManager.applyForCard(transitPass);
        // success alert message
        String successAlert =
            "You successfully apply for a "
                + selectCardType
                + "."
                + System.getProperty("line.separator")
                + "Card id: "
                + transitPass.getCardId()
                + System.getProperty("line.separator")
                + "Remaining days: "
                + 7;
        AlertController.successAlert(successAlert);
        // check if the users apply this card in their account
        if (AccountBarController.currentAccountEmail != null) {
          accountManager.bindCardToAccount(
              AccountBarController.currentAccountEmail, transitPass.getCardId());
        }
      }
      // when the user click "check out" button without selecting any card
    } else {
      AlertController.failAlert("Please select a card first!");
    }
    selectCardType = null;
  }
}
