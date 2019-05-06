package controller;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.util.Calendar;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import transitSystem.*;

/** A Login Controller that accepts inputs from Login interface and converts them to commands. */
public class LoginController {

  /** A Trip Controller that accepts inputs from Trip interface and converts them to commands. */
  private TripController tripController;
  /**
   * An AccountBar Controller that accepts inputs from SideBar interface and converts them to
   * commands.
   */
  private AccountBarController accountBarController;
  /**
   * An ApplyCard Controller that accepts inputs from AddANewCard interface and converts them to
   * commands.
   */
  private ApplyCardController applyCardController;
  /** An AdminUser that keep tracks of the entire system. */
  static AdminUser adminUser;
  /** A text field for card id. */
  @FXML private JFXTextField cardIdTextField;
  /** A text field for email address. */
  @FXML private JFXTextField emailTextField;
  /** A text field for password. */
  @FXML private JFXPasswordField passwordTextField;
  /** A scene that represents a trip page. */
  private Scene tripScene;
  /** A scene that represents an account page. */
  private Scene accountScene;
  /** A scene that represents an admin user page. */
  private Scene adminScene;
  /** A scene that represents a register page. */
  private Scene createAccountScene;

  /**
   * Sets up an applyCardController.
   *
   * @param applyCardController An Apply Card Controller that accepts inputs from AddANewCard
   *     interface and converts them to commands.
   */
  public void setApplyCardController(ApplyCardController applyCardController) {
    this.applyCardController = applyCardController;
  }

  /**
   * Sets up an admin user.
   *
   * @param admin An AdminUser that keep tracks of the entire system.
   */
  public static void setAdminUser(AdminUser admin) {
    adminUser = admin;
  }

  /**
   * Sets up an accountBarController.
   *
   * @param accountBarController An Account Bar Controller that accepts inputs from SideBar
   *     interface and converts them to commands.
   */
  public void setAccountBarController(AccountBarController accountBarController) {
    this.accountBarController = accountBarController;
  }

  /**
   * Sets up a scene that represents a register page.
   *
   * @param createAccountScene A scene that represents a register page.
   */
  public void setCreateAccountScene(Scene createAccountScene) {
    this.createAccountScene = createAccountScene;
  }

  /**
   * Sets up a scene that represents an account page.
   *
   * @param accountScene A scene that represents an account page.
   */
  public void setAccountScene(Scene accountScene) {
    this.accountScene = accountScene;
  }

  /**
   * Sets up a scene that represents a trip page.
   *
   * @param tripScene A scene that represents a trip page.
   */
  public void setTripScene(Scene tripScene) {
    this.tripScene = tripScene;
  }

  /**
   * Sets up a scene that represents an admin user page.
   *
   * @param adminScene A scene that represents an admin user page.
   */
  public void setAdminScene(Scene adminScene) {
    this.adminScene = adminScene;
  }

  /**
   * Sets up a tripController.
   *
   * @param tripController A Trip Controller that accepts inputs from Trip interface and converts
   *     them to commands.
   */
  public void setTripController(TripController tripController) {
    this.tripController = tripController;
  }

  /** A activity that when clicking the cancel button. */
  @FXML
  public void clickCancel() {
    emailTextField.setText("");
    passwordTextField.setText("");
  }

  /**
   * Conducts reactions for every activity from the Login page.
   *
   * @param mouseEvent An on click mouse event.
   * @throws IOException An IOException that in case of missing GUI file.
   */
  @FXML
  public void loginAccount(MouseEvent mouseEvent) throws IOException {
    AccountManager accountManager = adminUser.getAccountManager();
    // get account information from Login interface
    String email = emailTextField.getText();
    String password = passwordTextField.getText();
    Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
    // check if inputs are valid
    if (email.equals("") || password.equals("")) {
      AlertController.failAlert("Email or password should not be empty");
    } else if (accountManager.checkAccountExist(email)) {
      if (accountManager.emailMatchPassword(email, password)) {
        accountBarController.setCurrentAccountEmail(email);
        accountBarController.setProfileLabel(email);
        AccountProfileController accountProfileController =
            accountBarController.getAccountProfileController();
        accountBarController.getBorderPane().setCenter(accountProfileController.getProfilePane());
        stage.setScene(accountScene);
        emailTextField.setText("");
        passwordTextField.setText("");
      } else {
        passwordTextField.setText("");
        AlertController.failAlert("Incorrect password. Please try again.");
      }
      // A account for admin user
    } else if (adminUser.checkEmail(email)) {
      if (adminUser.checkPassword(password)) {
        stage.setScene(adminScene);
        emailTextField.setText("");
        passwordTextField.setText("");
      } else {
        passwordTextField.setText("");
        AlertController.failAlert("Incorrect password. Please try again.");
      }
    } else {
      emailTextField.setText("");
      passwordTextField.setText("");
      AlertController.failAlert("The account does not exist.");
    }
  }

  /**
   * Creates an account for user when clicking "crate account" button.
   *
   * @param mouseEvent An on click mouse event.
   */
  @FXML
  public void clickCreateAccount(MouseEvent mouseEvent) {
    Stage s = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
    s.setScene(createAccountScene);
  }

  /**
   * Conducts reactions for every activity from the card registration page.
   *
   * @param mouseEvent An on click mouse event.
   * @throws IOException An IOException that in case of missing GUI file.
   */
  @FXML
  public void chooseCard(MouseEvent mouseEvent) throws IOException {
    CardManager cardManager = adminUser.getCardManager();
    TransitManager transitManager = adminUser.getTransitManager();
    try {
      // check if the transit system is opened
      if (!adminUser.isClosed()) {
        int id = Integer.parseInt(cardIdTextField.getText());
        Stage s = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
        // set information to empty labels
        if (cardManager.checkCardIdMatch(id)) {
          tripController.setCardIdLabel(id);
          tripController.setLocationLabel("");
          tripController.setDateLabel(adminUser.getLastDateOpen());
          // check the status of a passenger: either on a bus or on subway
          if (transitManager.isInStation(id)) {
            Tuple<Station, Calendar> inStationInfo = transitManager.inStationInfo(id);
            // get station information from the tuple
            Station station = inStationInfo.zero;
            if (station.getStationType().equals("Station")) {
              tripController.setStatusLabel("On Metro");
            } else if (station.getStationType().equals("Stop")) {
              tripController.setStatusLabel("On Bus");
            }
          } else {
            tripController.setStatusLabel("Free");
          }
          s.setScene(tripScene);
          cardIdTextField.setText("");
          // an invalid card id
        } else {
          AlertController.failAlert("Sorry, the card id does not exist.");
        }
      } else {
        AlertController.failAlert("Sorry, the transit system is now closed.");
      }
    } catch (NumberFormatException e) {
      AlertController.failAlert("Please enter the correct card id.");
    }
  }

  /** Pops up a page for card application. */
  @FXML
  public void applyCard() {
    Stage stage = new Stage();
    Scene scene = applyCardController.getApplyCardScene();
    stage.setScene(scene);
    stage.show();
  }
}
