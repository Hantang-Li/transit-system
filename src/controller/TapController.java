package controller;

import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import transitSystem.AdminUser;
import transitSystem.Station;
import transitSystem.TransitManager;

/** A Tap Controller that accepts inputs TapIn or TapOut interface and converts them to commands. */
public class TapController {

  /** A Label that represents station location. */
  @FXML private Label locationLabel;
  /** A Label that represents card id. */
  @FXML private Label cardIdLabel;
  /** A Label that represents current date. */
  @FXML private Label dateLabel;
  /** A TextField that passengers need to enter when start a trip. */
  @FXML private JFXTextField timeTextField;
  /** A Scene that contains the tap in information. */
  private Scene tapInScene;
  /** A Scene that contains the tap out information. */
  private Scene tapOutScene;
  /**
   * An successAlert Controller that accepts inputs from alert interfaces and converts them to
   * commands.
   */
  private AlertController successAlertController;
  /**
   * An failAlert Controller that accepts inputs from alert interfaces and converts them to
   * commands.
   */
  private AlertController failAlertController;

  /**
   * Sets SuccessAlert Controller.
   *
   * @param successAlertController An successAlert Controller that accepts inputs from alert
   *     interfaces and converts them to commands.
   */
  public void setSuccessAlertController(AlertController successAlertController) {
    this.successAlertController = successAlertController;
  }

  /**
   * Sets FailAlert Controller.
   *
   * @param failAlertController An failAlert Controller that accepts inputs from alert interfaces
   *     and converts them to commands.
   */
  public void setFailAlertController(AlertController failAlertController) {
    this.failAlertController = failAlertController;
  }

  /**
   * Gets the Scene that contains the tap out information.
   *
   * @return A Scene that contains the tap out information.
   */
  Scene getTapOutScene() {
    return tapOutScene;
  }

  /**
   * Sets the Scene that contains the tap out information.
   *
   * @param tapOutScene A Scene that contains the tap out information.
   */
  public void setTapOutScene(Scene tapOutScene) {
    this.tapOutScene = tapOutScene;
  }

  /**
   * Sets the Scene that contains the tap in information.
   *
   * @param tapInScene A Scene that contains the tap in information.
   */
  public void setTapInScene(Scene tapInScene) {
    this.tapInScene = tapInScene;
  }

  /**
   * Gets the Scene that contains the tap in information.
   *
   * @return A Scene that contains the tap in information.
   */
  Scene getTapInScene() {
    return tapInScene;
  }

  /**
   * Sets the Label that represents station location.
   *
   * @param location A station location.
   */
  void setLocationLabel(String location) {
    locationLabel.setText(location);
  }

  /**
   * Sets the Label that represents card id.
   *
   * @param cardId Card id.
   */
  void setCardIdLabel(String cardId) {
    cardIdLabel.setText(cardId);
  }

  /**
   * Sets the Label that represents current date.
   *
   * @param date Current date.
   */
  void setDateLabel(String date) {
    dateLabel.setText(date);
  }

  /**
   * Starts a ride when a user clicking "Tap in" button.
   *
   * @param mouseEvent An on click mouse event.
   * @throws IOException An IOException that in case of missing GUI file.
   */
  @FXML
  public void clickTapIn(MouseEvent mouseEvent) throws IOException {
    AdminUser adminUser = LoginController.adminUser;
    TransitManager transitManager = adminUser.getTransitManager();
    String[] word = locationLabel.getText().split(" ");
    // get the station location and station type
    String location = word[0];
    String type = word[1];
    Station station = transitManager.getStation(location, type);
    // get the passenger enter time
    String time = timeTextField.getText();
    // check the format of current time input
    if (!time.matches("([0-1][0-9]|2[0-3]):[0-5][0-9]")) {
      AlertController.failAlert("Invalid format of current time.");
    } else {
      int cardId = Integer.parseInt(cardIdLabel.getText());
      // check if the tap in is legal
      if (time.equals("")) {
        AlertController.failAlert("Please enter the current time.");
      } else {
        try {
          String dateTime = dateLabel.getText() + " " + time;
          Calendar calendar = parseDate(dateTime);
          boolean isLegalEnter =
              transitManager.checkLegalEnter(transitManager.getLastTrip(cardId), calendar, cardId);
          String information = transitManager.tapIn(cardId, calendar, station);
          if (isLegalEnter) {
            successAlertController.setAlertLabel(information);
            Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(successAlertController.getTapSuccessScene());
          } else {
            failAlertController.setAlertLabel(
                "Tap in failed." + System.getProperty("line.separator") + information);
            Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(failAlertController.getTapFailScene());
          }
        } catch (ParseException e) {
          AlertController.failAlert("Invalid format of current time.");
        }
      }
    }
  }

  /**
   * Ends a ride when a user clicking "Tap out" button.
   *
   * @param mouseEvent An on click mouse event.
   * @throws IOException An IOException that in case of missing GUI file.
   */
  @FXML
  public void clickTapOut(MouseEvent mouseEvent) throws IOException {
    AdminUser adminUser = LoginController.adminUser;
    TransitManager transitManager = adminUser.getTransitManager();
    String[] word = locationLabel.getText().split(" ");
    // get the station location and station type
    String location = word[0];
    String type = word[1];
    Station station = transitManager.getStation(location, type);
    // get the passenger exit time
    String time = timeTextField.getText();
    // check the format of current time input
    if (!time.matches("([0-1][0-9]|2[0-3]):[0-5][0-9]")) {
      AlertController.failAlert("Invalid format of current time.");
    } else {
      int cardId = Integer.parseInt(cardIdLabel.getText());
      // check if the tap out is legal
      if (time.equals("")) {
        AlertController.failAlert("Please enter the current time.");
      } else {
        try {
          String dateTime = dateLabel.getText() + " " + time;
          Calendar calendar = parseDate(dateTime);
          boolean isLegalExit =
              transitManager.checkLegalExit(
                  transitManager.getLastTrip(cardId), station, calendar, cardId);
          String information = transitManager.tapOut(cardId, calendar, station);
          if (isLegalExit) {
            successAlertController.setAlertLabel(information);
            Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(successAlertController.getTapSuccessScene());
          } else {
            failAlertController.setAlertLabel("Tap out failed." + information);
            Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(failAlertController.getTapFailScene());
          }
        } catch (ParseException e) {
          AlertController.failAlert("Invalid format of current time.");
        }
      }
    }
  }

  /**
   * Converts date string to Calendar object.
   *
   * @param dateTime Current date written in string.
   * @return Current date with Calendar type.
   * @throws ParseException A ParseException to handle the incorrect format of time.
   */
  private Calendar parseDate(String dateTime) throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(sdf.parse(dateTime));
    return calendar;
  }
}
