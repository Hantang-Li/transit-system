package controller;

import java.io.IOException;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import transitSystem.AccountManager;
import transitSystem.AdminUser;

/**
 * A CreateAccount Controller that accepts inputs from UserInformation interface and converts them
 * to commands.
 */
public class CreateAccountController {

  /** A text field for user's email address. */
  @FXML
  private JFXTextField emailTextField;
  /** A text field for user's user name. */
  @FXML
  private JFXTextField nameTextField;
  /** A text field for password. */
  @FXML
  private JFXPasswordField passwordField;
  /** A text field for confirming password. */
  @FXML
  private JFXPasswordField confirmPasswordField;
  /** A scene that represents the previous page. */
  private Scene previousScene;

  /**
   * Sets the previous Scene.
   *
   * @param scene A scene that represents the previous page.
   */
  public void setPreviousScene(Scene scene) {
    previousScene = scene;
  }

  /**
   * Creates an account for user when clicking "create account" button.
   *
   * @throws IOException An IOException that in case of missing GUI file.
   */
  @FXML
  public void clickCreateAccount() throws IOException {
    AdminUser adminUser = LoginController.adminUser;
    AccountManager accountManager = adminUser.getAccountManager();
    // get information from user inputs
    String email = emailTextField.getText();
    String name = nameTextField.getText();
    String password = passwordField.getText();
    String confirmPassword = confirmPasswordField.getText();
    // check if inputs are valid
    if (email.equals("") || name.equals("") || password.equals("") || confirmPassword.equals("")) {
      AlertController.failAlert("Please complete all the information");
    } else if (accountManager.checkAccountExist(email)) {
      AlertController.failAlert("Sorry, the email already exist.");
    } else if (!email.matches("[a-zA-z0-9._]+@.*\\..*")) {
      AlertController.failAlert("Invalid email address!");
    } else if (!password.equals(confirmPassword)) {
      AlertController.failAlert(
          "The password does not match."
              + System.getProperty("line.separator")
              + "Please try again.");
    } else {
      try {
        accountManager.createAccount(email, name, password);
      } catch (Exception e) {
        e.printStackTrace();
      }
      clickCancel();
      AlertController.successAlert(
          String.format(
              "You successfully create the account."
                  + System.getProperty("line.separator")
                  + "Your email: %s"
                  + System.getProperty("line.separator")
                  + "Your user name: %s",
              email,
              name));
    }
  }

  /** Clears all inputs when user clicking "cancel" button. */
  @FXML
  public void clickCancel() {
    emailTextField.setText("");
    nameTextField.setText("");
    passwordField.setText("");
    confirmPasswordField.setText("");
  }

  /**
   * Returns to previous page when clicking "back" button.
   *
   * @param mouseEvent An on click mouse event.
   */
  @FXML
  public void clickBack(MouseEvent mouseEvent) {
    Stage s = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
    s.setScene(previousScene);
    s.show();
  }
}
