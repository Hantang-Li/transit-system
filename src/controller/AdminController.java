package controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import transitSystem.AdminUser;

/**
 * An Admin Controller that accepts inputs from SystemOperation interface and converts them to
 * commands.
 */
public class AdminController {

  /** A scene that represents the previous page. */
  private Scene previousScene;
  /** A DatePicker for admin user to determine the date to open the system. */
  @FXML private DatePicker datePicker;
  /** A DatePicker for admin user to select the date of report. */
  @FXML private DatePicker reportDate;
  /** A Label that represents the daily report. */
  @FXML private Label reportLabel;
  /** A Label that represents the date that the system last closed. */
  @FXML private Label lastDateLabel;

  /**
   * Sets the daily report.
   *
   * @param report A Label that represents the daily report.
   */
  private void setReportLabel(String report) {
    reportLabel.setText(report);
  }

  /**
   * Sets the ReportLabel height in order to fit all of the reports.
   *
   * @param height The height of the label.
   */
  private void setReportLabelHeight(int height) {
    reportLabel.setPrefHeight(height);
  }

  /**
   * Sets the previous Scene.
   *
   * @param scene A scene that represents the previous page.
   */
  public void setPreviousScene(Scene scene) {
    previousScene = scene;
    lastDateLabel.setText(LoginController.adminUser.getLastDateClose());
  }

  /**
   * Opens the system when adminUser clicking "open system" button.
   *
   * @throws ParseException A ParseException to handle incorrect date format.
   * @throws IOException An IOException that in case of missing GUI file.
   */
  @FXML
  public void clickOpenSystem() throws ParseException, IOException {
    LocalDate localDate = datePicker.getValue();
    // check if the input date is valid
    if (localDate == null) {
      AlertController.failAlert("Please enter the date to open the system.");
    } else {
      String date = localDate.toString();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      Calendar cal1 = Calendar.getInstance();
      cal1.setTime(sdf.parse(date));

      AdminUser adminUser = LoginController.adminUser;
      // get the date the system last closed
      String lastDate = adminUser.getLastDateClose();
      Calendar cal2 = Calendar.getInstance();
      cal2.setTime(sdf.parse(lastDate));
      // check status of the system
      if (!adminUser.isClosed()) {
        AlertController.failAlert("The system is opened now.");
      } else if (cal1.getTimeInMillis() - cal2.getTimeInMillis() < 0) {
        AlertController.failAlert("Invalid Date!!");
      } else {
        adminUser.setLastDateOpen(date);
        adminUser.setClosed(false);
        AlertController.successAlert(
            "You successfully open the system"
                + System.getProperty("line.separator")
                + "on "
                + date);
      }
    }
  }

  /**
   * Closes the system when adminUser clicking "close system" button.
   *
   * @throws IOException An IOException that in case of missing GUI file.
   */
  @FXML
  public void clickCloseSystem() throws IOException {
    AdminUser adminUser = LoginController.adminUser;
    // check status of the system
    if (adminUser.isClosed()) {
      AlertController.failAlert("The system is closed now.");
    } else if (!adminUser.isAbleClose()) {
      AlertController.failAlert(
          "There are still some people"
              + System.getProperty("line.separator")
              + "in the station/stop ! ");
    } else {
      adminUser.setLastDateClose(adminUser.getLastDateOpen());
      adminUser.setClosed(true);
      AlertController.successAlert(
          "You successfully close the system"
              + System.getProperty("line.separator")
              + "on "
              + adminUser.getLastDateClose());
      // update last system closed date
      lastDateLabel.setText(adminUser.getLastDateClose());
    }
  }

  /**
   * Generates a daily report when adminUser clicking "close system" button.
   *
   * @throws IOException An IOException that in case of missing GUI file.
   */
  @FXML
  public void clickGenerateReport() throws IOException {
    LocalDate localDate = reportDate.getValue();
    // check if the input date is valid
    if (localDate == null) {
      AlertController.failAlert("Please enter the date to view the report.");
    } else {
      // get the date from the DatePicker
      String date = localDate.toString();
      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/Report.fxml"));
      VBox vBox = fxmlLoader.load();
      AdminController adminController = fxmlLoader.getController();
      AdminUser adminUser = LoginController.adminUser;
      String report = adminUser.generateReport(date);
      String[] lines = report.split(System.getProperty("line.separator"));
      int line = lines.length;
      // resize the label in order to fit all the information
      adminController.setReportLabelHeight((line + 1) * 28);
      adminController.setReportLabel(report);
      Scene scene = new Scene(vBox);
      Stage stage = new Stage();
      stage.setScene(scene);
      stage.show();
    }
  }

  /**
   * Logs out adminUser account when adminUser clicking "log out" button.
   *
   * @param mouseEvent An on click mouse event.
   */
  @FXML
  public void clickLogOut(MouseEvent mouseEvent) {
    Stage s = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
    s.setScene(previousScene);
    s.show();
  }
}
