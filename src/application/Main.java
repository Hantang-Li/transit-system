package application;

import controller.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import transitSystem.AdminUser;
import transitSystem.TransitManager;
import java.io.IOException;

/** Represents the class that is responsible for running the program. */
public class Main extends Application {

  public static void main(String[] args) {
    Application.launch();
  }

  /**
   * Start our application.
   *
   * @param primaryStage Primary stage of the program.
   * @throws Exception Handle the exception caused by running the program.
   */
  @Override
  public void start(Stage primaryStage) throws Exception {
    com.sun.javafx.util.Logging.getCSSLogger().setLevel(sun.util.logging.PlatformLogger.Level.OFF);
    AdminUser admin = new AdminUser();

    admin.initializeSystem();
    TransitManager transitManager = admin.getTransitManager();

    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
    StackPane pane = fxmlLoader.load();
    LoginController loginController = fxmlLoader.getController();
    LoginController.setAdminUser(admin);

    // Trip Controller and connect to loginController
    FXMLLoader fxmlLoader2 = new FXMLLoader(getClass().getResource("/view/Trip.fxml"));
    StackPane stackPane = fxmlLoader2.load();
    Scene tripScene = new Scene(stackPane);
    loginController.setTripScene(tripScene);
    TripController tripController = fxmlLoader2.getController();
    loginController.setTripController(tripController);
    Scene scene = new Scene(pane);
    tripController.setPreviousScene(scene);
    tripController.resetStationButton();
    transitManager.addObserver(tripController);

    // AccountBarController and connect to loginController
    FXMLLoader fxmlLoader3 = new FXMLLoader(getClass().getResource("/view/SideBar.fxml"));
    BorderPane borderPane = fxmlLoader3.load();
    Scene accountScene = new Scene(borderPane);
    loginController.setAccountScene(accountScene);
    AccountBarController accountBarController = fxmlLoader3.getController();
    // in order to reset new page conveniently
    loginController.setAccountBarController(accountBarController);
    accountBarController.setPreviousScene(scene);

    // read all the related fxml
    readAccountRelatedFxml(accountBarController);
    readLoginRelatedFxml(loginController, scene);
    readTripRelatedFxml(tripController);

    primaryStage.setScene(scene);
    primaryStage.show();
    primaryStage.setResizable(false);
  }

  /**
   * Read all the fxml file related to the account.
   *
   * @param accountBarController The controller of the side bar of the account interface.
   * @throws IOException An IOException that in case of missing GUI file.
   */
  private void readAccountRelatedFxml(AccountBarController accountBarController)
      throws IOException {
    FXMLLoader fxmlLoader4 = new FXMLLoader(getClass().getResource("/view/AccountProfile.fxml"));
    fxmlLoader4.load();
    AccountProfileController profileController = fxmlLoader4.getController();
    accountBarController.setAccountProfileController(profileController);
    // load my card interface
    FXMLLoader fxmlLoader5 = new FXMLLoader(getClass().getResource("/view/LoadMyCard.fxml"));
    fxmlLoader5.load();
    LoadMyCardController loadMyCardController = fxmlLoader5.getController();
    accountBarController.setLoadMyCardController(loadMyCardController);
    loadMyCardController.setAccountBarController(accountBarController);
    // card activity interface
    FXMLLoader fxmlLoader6 = new FXMLLoader(getClass().getResource("/view/CardActivity.fxml"));
    fxmlLoader6.load();
    CardActivityController cardActivityController = fxmlLoader6.getController();
    accountBarController.setCardActivityController(cardActivityController);
    // manage card interface
    FXMLLoader fxmlLoader13 = new FXMLLoader(getClass().getResource("/view/ManageMyCard.fxml"));
    fxmlLoader13.load();
    ManageCardController manageCardController = fxmlLoader13.getController();
    accountBarController.setManageCardController(manageCardController);
    // add new card interface
    FXMLLoader fxmlLoader14 = new FXMLLoader(getClass().getResource("/view/AddANewCard.fxml"));
    fxmlLoader14.load();
    ApplyCardController applyCardController = fxmlLoader14.getController();
    accountBarController.setApplyCardController(applyCardController);
    // account activity interface
    FXMLLoader fxmlLoader15 = new FXMLLoader(getClass().getResource("/view/AccountActivity.fxml"));
    fxmlLoader15.load();
    AccountActivityController accountActivityController = fxmlLoader15.getController();
    accountBarController.setAccountActivityController(accountActivityController);
  }

  /**
   * Read all the fxml file related to the login interface.
   *
   * @param loginController Controller of the login interface.
   * @param scene Represents scene of the login interface.
   * @throws IOException An IOException that in case of missing GUI file.
   */
  private void readLoginRelatedFxml(LoginController loginController, Scene scene)
      throws IOException {
    FXMLLoader fxmlLoader8 = new FXMLLoader(getClass().getResource("/view/UserInformation.fxml"));
    AnchorPane createAccountPane = fxmlLoader8.load();
    Scene createAccountScene = new Scene(createAccountPane);
    loginController.setCreateAccountScene(createAccountScene);
    CreateAccountController createAccountController = fxmlLoader8.getController();
    createAccountController.setPreviousScene(scene);
    // system operation interface
    FXMLLoader fxmlLoader7 = new FXMLLoader(getClass().getResource("/view/SystemOperation.fxml"));
    AnchorPane adminPane = fxmlLoader7.load();
    Scene adminScene = new Scene(adminPane);
    loginController.setAdminScene(adminScene);
    AdminController adminController = fxmlLoader7.getController();
    adminController.setPreviousScene(scene);
    // add new card interface
    FXMLLoader fxmlLoader17 = new FXMLLoader(getClass().getResource("/view/AddANewCard.fxml"));
    AnchorPane applyCardPane2 = fxmlLoader17.load();
    ApplyCardController applyCardController2 = fxmlLoader17.getController();
    loginController.setApplyCardController(applyCardController2);
    Scene applyCardScene = new Scene(applyCardPane2);
    applyCardController2.setApplyCardScene(applyCardScene);
    applyCardController2.setButtonAction();
    applyCardController2.setSelectCardType("Traffic Card");
  }

  /**
   * Read all the fxml file related to trip interface.
   *
   * @param tripController controller of the trip interface.
   * @throws IOException An IOException that in case of missing GUI file.
   */
  private void readTripRelatedFxml(TripController tripController) throws IOException {
    FXMLLoader fxmlLoader1 = new FXMLLoader(getClass().getResource("/view/TopUp.fxml"));
    AnchorPane topUpPane = fxmlLoader1.load();
    Scene topUpScene = new Scene(topUpPane);
    TopUpController topUpController = fxmlLoader1.getController();
    topUpController.setTopUpScene(topUpScene);
    tripController.setTopUpController(topUpController);
    // tap in interface
    FXMLLoader fxmlLoader2 = new FXMLLoader(getClass().getResource("/view/TapIn.fxml"));
    AnchorPane tapInPane = fxmlLoader2.load();
    Scene tapInScene = new Scene(tapInPane);
    TapController tapInController = fxmlLoader2.getController();
    tapInController.setTapInScene(tapInScene);
    tripController.setTapInController(tapInController);

    // tap out interface
    FXMLLoader fxmlLoader3 = new FXMLLoader(getClass().getResource("/view/TapOut.fxml"));
    AnchorPane tapOutPane = fxmlLoader3.load();
    Scene tapOutScene = new Scene(tapOutPane);
    TapController tapOutController = fxmlLoader3.getController();
    tapOutController.setTapOutScene(tapOutScene);
    tripController.setTapOutController(tapOutController);

    // tap in alert interface
    FXMLLoader fxmlLoader4 = new FXMLLoader(getClass().getResource("/view/TapSuccessAlert.fxml"));
    AnchorPane tapSuccessPane = fxmlLoader4.load();
    Scene tapSuccessScene = new Scene(tapSuccessPane);
    AlertController SuccessAlertController = fxmlLoader4.getController();
    SuccessAlertController.setTapSuccessScene(tapSuccessScene);
    tapInController.setSuccessAlertController(SuccessAlertController);
    tapOutController.setSuccessAlertController(SuccessAlertController);

    // tap out alert interface
    FXMLLoader fxmlLoader5 = new FXMLLoader(getClass().getResource("/view/TapFailAlert.fxml"));
    AnchorPane tapFailPane = fxmlLoader5.load();
    Scene tapFailScene = new Scene(tapFailPane);
    AlertController FailAlertController = fxmlLoader5.getController();
    FailAlertController.setTapFailScene(tapFailScene);
    tapInController.setFailAlertController(FailAlertController);
    tapOutController.setFailAlertController(FailAlertController);
  }
}
