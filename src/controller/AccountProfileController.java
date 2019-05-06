package controller;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import transitSystem.AccountManager;
import transitSystem.AdminUser;
import transitSystem.CardHolderAccount;

/**
 * An AccountProfile Controller that accepts inputs from AccountProfile interface and converts them
 * to commands.
 */
public class AccountProfileController implements Observer {

  /** An AnchorPane that represents user profile page. */
  @FXML private AnchorPane profilePane;

  /** A Label for user name. */
  @FXML private Label userNameLabel;

  /** A Label for user's email address. */
  @FXML private Label emailLabel;

  /** A TextField for user to enter their new user name. */
  @FXML private TextField newUserNameText;

  /** A TextField for user to enter their new password. */
  @FXML private TextField passwordTextField;

  /** A TextField for user to confirm their new password. */
  @FXML private TextField confirmTextField;

  /**
   * Gets user's profile page.
   *
   * @return An AnchorPane that represents user profile page.
   */
  AnchorPane getProfilePane() {
    return profilePane;
  }

  /**
   * Sets user name to the empty label.
   *
   * @param userName User name.
   */
  void setUserNameLabel(String userName) {
    userNameLabel.setText(userName);
  }

  /**
   * Sets user's email address to the empty label.
   *
   * @param email User's email address.
   */
  void setEmailLabel(String email) {
    emailLabel.setText(email);
  }

  /**
   * Pops up a dialog that users can modify their names when clicking "edit name" button.
   *
   * @throws IOException An IOException that in case of missing GUI file.
   */
  @FXML
  public void clickEditName() throws IOException {
    AdminUser adminUser = LoginController.adminUser;
    AccountManager accountManager = adminUser.getAccountManager();
    accountManager.addObserver(this);
    // pops up a new page that user can modify their user name
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/UpdateName.fxml"));
    AnchorPane anchorPane = fxmlLoader.load();
    AccountProfileController controller = fxmlLoader.getController();
    // replace old user name with the new one
    controller.setUserNameLabel(userNameLabel.getText());
    Stage stage = new Stage();
    Scene scene = new Scene(anchorPane);
    stage.setScene(scene);
    stage.show();
  }

  /**
   * Pops up a dialog that users can modify their password when clicking "edit password" button.
   *
   * @throws IOException An IOException that in case of missing GUI file.
   */
  @FXML
  public void clickEditPassword() throws IOException {
    // pops up a new page that user can modify their password
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/UpdatePassword.fxml"));
    AnchorPane anchorPane = fxmlLoader.load();
    Scene scene = new Scene(anchorPane);
    Stage stage = new Stage();
    stage.setScene(scene);
    stage.show();
  }

  /**
   * Updates user's user name.
   *
   * @param mouseEvent An on click mouse event.
   * @throws IOException An IOException that in case of missing GUI file.
   */
  @FXML
  public void updateName(MouseEvent mouseEvent) throws IOException {
    AdminUser adminUser = LoginController.adminUser;
    AccountManager accountManager = adminUser.getAccountManager();
    String newUserName = newUserNameText.getText();
    if (newUserName == null) {
      AlertController.failAlert("Please enter your new user name!");
    } else {
      // update the user name
      accountManager.changeName(AccountBarController.currentAccountEmail, newUserName);
      Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
      stage.hide();
      AlertController.successAlert(
          "User name has been modified to:" + System.getProperty("line.separator") + newUserName);
    }
  }

  /**
   * Updates user's password.
   *
   * @param mouseEvent An on click mouse event.
   * @throws IOException An IOException that in case of missing GUI file.
   */
  @FXML
  public void updatePassword(MouseEvent mouseEvent) throws IOException {
    AdminUser adminUser = LoginController.adminUser;
    AccountManager accountManager = adminUser.getAccountManager();
    // users are asked to enter their new password twice
    String password = passwordTextField.getText();
    String confirmPassword = confirmTextField.getText();
    if (password.equals("")) {
      AlertController.failAlert("Please enter your new password!");
    } else if (confirmPassword.equals("")) {
      AlertController.failAlert("Please confirm your new password!");
    } else if (!password.equals(confirmPassword)) {
      AlertController.failAlert("Password does not match!");
    } else {
      // updates user's password
      accountManager.changePassword(AccountBarController.currentAccountEmail, password);
      Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
      stage.hide();
      AlertController.successAlert("You have successfully modified your password!");
    }
  }

  /**
   * Closes current dialog when user clicking "cancel" button.
   *
   * @param mouseEvent An on click mouse event.
   */
  @FXML
  public void clickCancel(MouseEvent mouseEvent) {
    Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
    stage.hide();
  }

  /**
   * Updates changes when user name is modified in AccountManager.
   *
   * @param o An observer to be added.
   * @param arg An object when change occurs in AccountManager.
   */
  @Override
  public void update(Observable o, Object arg) {
    CardHolderAccount account = (CardHolderAccount) arg;
    userNameLabel.setText(account.getName());
  }
}
