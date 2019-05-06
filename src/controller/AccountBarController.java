package controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import transitSystem.AccountManager;
import transitSystem.CardHolderAccount;

/**
 * An AccountBar Controller that accepts inputs from SideBar interface and converts them to
 * commands.
 */
public class AccountBarController {

  /**
   * An AccountProfile Controller that accepts inputs from AccountProfile interface and converts
   * them to commands.
   */
  private AccountProfileController accountProfileController;
  /**
   * An LoadMyCard Controller that accepts inputs from LoadMyCard interface and converts them to
   * commands.
   */
  private LoadMyCardController loadMyCardController;
  /**
   * An CardActivity Controller that accepts inputs from CardActivity interface and converts them to
   * commands.
   */
  private CardActivityController cardActivityController;
  /**
   * An ManageCard Controller that accepts inputs from ManageMyCard interface and converts them to
   * commands.
   */
  private ManageCardController manageCardController;
  /**
   * An ApplyCard Controller that accepts inputs from AddANewCard interface and converts them to
   * commands.
   */
  private ApplyCardController applyCardController;
  /**
   * An AccountActivity Controller that accepts inputs from AccountActivity interface and converts
   * them to commands.
   */
  private AccountActivityController accountActivityController;
  /** A scene that represents the previous page. */
  private Scene previousScene;
  /** A new borderPane. */
  @FXML private BorderPane borderPane;
  /** A string that represents current email address. */
  static String currentAccountEmail;

  /**
   * Sets user's email address.
   *
   * @param currentAccountEmail A string that represents current email address.
   */
  void setCurrentAccountEmail(String currentAccountEmail) {
    AccountBarController.currentAccountEmail = currentAccountEmail;
  }

  /**
   * Gets the required BorderPane.
   *
   * @return A BorderPane.
   */
  BorderPane getBorderPane() {
    return borderPane;
  }

  /**
   * Sets An AccountActivity Controller.
   *
   * @param accountActivityController An AccountActivity Controller that accepts inputs from
   *     AccountActivity interface and converts them to commands.
   */
  public void setAccountActivityController(AccountActivityController accountActivityController) {
    this.accountActivityController = accountActivityController;
  }

  /**
   * Sets an ApplyCard Controller.
   *
   * @param applyCardController An ApplyCard Controller that accepts inputs from AddANewCard
   *     interface and converts them to commands.
   */
  public void setApplyCardController(ApplyCardController applyCardController) {
    this.applyCardController = applyCardController;
  }

  /**
   * Sets an ApplyCard Controller.
   *
   * @param manageCardController An ManageCard Controller that accepts inputs from ManageMyCard
   *     interface and converts them to commands.
   */
  public void setManageCardController(ManageCardController manageCardController) {
    this.manageCardController = manageCardController;
  }

  /**
   * Sets email address to an empty label.
   *
   * @param email A user's email.
   */
  void setProfileLabel(String email) {
    AccountManager accountManager = LoginController.adminUser.getAccountManager();
    CardHolderAccount account = accountManager.findAccount(email);
    accountProfileController.setEmailLabel(email);
    accountProfileController.setUserNameLabel(account.getName());
  }

  /**
   * Sets the previous Scene.
   *
   * @param previousScene A scene that represents the previous page.
   */
  public void setPreviousScene(Scene previousScene) {
    this.previousScene = previousScene;
  }

  /**
   * Gets an AccountProfile Controller.
   *
   * @return An AccountProfile Controller that accepts inputs from AccountProfile interface and
   *     converts them to commands.
   */
  AccountProfileController getAccountProfileController() {
    return accountProfileController;
  }

  /**
   * Sets an AccountProfile Controller.
   *
   * @param accountProfileController An AccountProfile Controller that accepts inputs from
   *     AccountProfile interface and converts them to commands.
   */
  public void setAccountProfileController(AccountProfileController accountProfileController) {
    this.accountProfileController = accountProfileController;
  }

  /**
   * Sets LoadMyCard Controller.
   *
   * @param loadMyCardController An LoadMyCard Controller that accepts inputs from LoadMyCard
   *     interface and converts them to commands.
   */
  public void setLoadMyCardController(LoadMyCardController loadMyCardController) {
    this.loadMyCardController = loadMyCardController;
  }

  /**
   * Sets CardActivity Controller.
   *
   * @param activityController An CardActivity Controller that accepts inputs from CardActivity
   *     interface and converts them to commands.
   */
  public void setCardActivityController(CardActivityController activityController) {
    this.cardActivityController = activityController;
  }

  /** Switches to account profile page when user clicking "account profile" button. */
  @FXML
  public void clickAccountProfile() {
    AnchorPane profilePane = accountProfileController.getProfilePane();
    borderPane.setCenter(profilePane);
  }

  /** Switches to card activity page when user clicking "card activity" button. */
  @FXML
  public void clickCardActivity() {
    cardActivityController.resetCardActivity();
    AnchorPane activityPane = cardActivityController.getCardActivityPane();
    borderPane.setCenter(activityPane);
  }

  /** Switches to card loading page when user clicking "load my card" button. */
  @FXML
  public void clickLoadMyCard() {
    loadMyCardController.resetLoadMyCard();
    AnchorPane loadMyCardPane = loadMyCardController.getLoadCardPane();
    borderPane.setCenter(loadMyCardPane);
  }

  /** Adds "manage my card" Pane to Sidebar when user clicking "manage my card" button. */
  @FXML
  public void clickManageCard() {
    manageCardController.resetManageCard();
    AnchorPane manageCardPane = manageCardController.getManageCardPane();
    borderPane.setCenter(manageCardPane);
  }

  /** Adds "apply card" Pane to Sidebar when user clicking "apply a card" button. */
  @FXML
  public void clickApplyCard() {
    applyCardController.setButtonAction();
    applyCardController.setSelectCardType(null);
    AnchorPane applyCardPane = applyCardController.getApplyCardPane();
    borderPane.setCenter(applyCardPane);
  }

  /**
   * Logs out the account when user clicking "log out" button.
   *
   * @param mouseEvent An on click mouse event.
   */
  @FXML
  public void clickLogOut(MouseEvent mouseEvent) {
    currentAccountEmail = null;
    Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
    stage.setScene(previousScene);
    stage.show();
  }

  /** Adds "account activity" Pane to Sidebar when user clicking "account activity" button. */
  @FXML
  public void clickAccountActivity() {
    accountActivityController.resetLabel();
    ScrollPane accountActivityPane = accountActivityController.getAccountActivityPane();
    borderPane.setCenter(accountActivityPane);
  }

  /**
   * Pops up developer's information when user clicking "contact us" button.
   *
   * @throws IOException An IOException that in case of missing GUI file.
   */
  @FXML
  public void clickContactUs() throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/ContactUs.fxml"));
    AnchorPane anchorPane = fxmlLoader.load();
    Stage stage = new Stage();
    Scene scene = new Scene(anchorPane);
    stage.setScene(scene);
    stage.show();
    // set the button action
    for (Node node : anchorPane.getChildren()) {
      if (node instanceof Button) {
        ((Button) node)
            .setOnAction(
                event -> {
                  Stage s = (Stage) ((Node) event.getSource()).getScene().getWindow();
                  s.hide();
                });
      }
    }
  }
}
