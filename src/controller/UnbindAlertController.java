package controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import transitSystem.AccountManager;
import transitSystem.AdminUser;

/**
 * A UnbindAlert Controller that accepts inputs from alert interface and converts them to commands.
 */
public class UnbindAlertController {

  /** An int that represents the current card id. */
  private int currentId;
  /** A double that represents the remaining balance of the card. */
  private double balanceReturn;
  /**
   * A ManageCard Controller that accepts inputs from ManageMyCard interface and converts them to
   * commands.
   */
  private ManageCardController manageCardController;
  /** A Label that represents all alert messages. */
  @FXML private Label alertLabel;

  /**
   * Resets ManageCard Controller, current card id and the remaining balance of the card.
   *
   * @param information An alert message.
   * @param controller A ManageCard Controller that accepts inputs from ManageMyCard interface and
   *     converts them to commands.
   * @param currentId An int that represents the current card id.
   * @param balance A double that represents the remaining balance of the card.
   */
  void resetUnbindController(
      String information, ManageCardController controller, int currentId, double balance) {
    alertLabel.setText(information);
    this.manageCardController = controller;
    this.currentId = currentId;
    this.balanceReturn = balance;
  }

  /**
   * Unbinds the card after user clicking "Yes" button.
   *
   * @param mouseEvent An on click mouse event.
   * @throws IOException An IOException that in case of missing GUI file.
   */
  @FXML
  public void clickYes(MouseEvent mouseEvent) throws IOException {
    AdminUser adminUser = LoginController.adminUser;
    AccountManager accountManager = adminUser.getAccountManager();
    accountManager.unbindCardToAccount(AccountBarController.currentAccountEmail, currentId);
    Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
    AlertController.successAlert(
        "You successfully unbind the card."
            + System.getProperty("line.separator")
            + "You will get $"
            + balanceReturn
            + " refund.");
    stage.hide();
    manageCardController.resetManageCard();
  }

  /**
   * Closes the dialog and do nothing when user clicking "No" button.
   *
   * @param mouseEvent An on click mouse event.
   */
  @FXML
  public void clickNo(MouseEvent mouseEvent) {
    Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
    stage.hide();
  }
}
