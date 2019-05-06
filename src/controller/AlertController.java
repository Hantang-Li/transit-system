package controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/** An Alert Controller that accepts inputs from alert interfaces and converts them to commands. */
public class AlertController {

  /** A Label that represents alert message. */
  @FXML private Label alertLabel;
  /** A scene that represents the successful tapping message page. */
  private Scene tapSuccessScene;
  /** A scene that represents the fail tapping message page. */
  private Scene tapFailScene;

  /**
   * Gets the successful tapping message page.
   *
   * @return A scene that represents the successful tapping message page.
   */
  Scene getTapSuccessScene() {
    return tapSuccessScene;
  }

  /**
   * Sets the successful tapping message page.
   *
   * @param scene A scene that represents the successful tapping message page.
   */
  public void setTapSuccessScene(Scene scene) {
    tapSuccessScene = scene;
  }

  /**
   * Sets A scene that represents the fail tapping message page.
   *
   * @param tapFailScene A scene that represents the fail tapping message page.
   */
  public void setTapFailScene(Scene tapFailScene) {
    this.tapFailScene = tapFailScene;
  }

  /**
   * Gets A scene that represents the fail tapping message page.
   *
   * @return A scene that represents the fail tapping message page.
   */
  Scene getTapFailScene() {
    return tapFailScene;
  }

  /**
   * Closes current dialog when user clicking "got it" button.
   *
   * @param mouseEvent An on click mouse event.
   */
  @FXML
  public void clickGotIt(MouseEvent mouseEvent) {
    Stage s = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
    s.hide();
  }

  /**
   * Sets alert message to alertLabel.
   *
   * @param alert An alert message.
   */
  void setAlertLabel(String alert) {
    alertLabel.setText(alert);
  }

  /**
   * Pops up a success alert dialog.
   *
   * @param alert A success alert message.
   * @throws IOException An IOException that in case of missing GUI file.
   */
  public static void successAlert(String alert) throws IOException {
    FXMLLoader fxmlLoader =
        new FXMLLoader(AlertController.class.getResource("/view/SuccessAlert.fxml"));
    showScene(fxmlLoader, alert);
  }

  /**
   * Pops up a fail alert dialog.
   *
   * @param alert A fail alert message.
   * @throws IOException An IOException that in case of missing GUI file.
   */
  public static void failAlert(String alert) throws IOException {
    FXMLLoader fxmlLoader =
        new FXMLLoader(AlertController.class.getResource("/view/FailAlert.fxml"));
    showScene(fxmlLoader, alert);
  }

  /**
   * Pops up an alert page.
   *
   * @param fxmlLoader A GUI file loader.
   * @param alert An alert message.
   * @throws IOException An IOException that in case of missing GUI file.
   */
  private static void showScene(FXMLLoader fxmlLoader, String alert) throws IOException {
    AnchorPane alertPane = fxmlLoader.load();
    AlertController alertController = fxmlLoader.getController();
    alertController.setAlertLabel(alert);
    Scene scene = new Scene(alertPane);
    Stage stage = new Stage();
    stage.setScene(scene);
    stage.show();
  }
}
