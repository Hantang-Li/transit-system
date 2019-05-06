package controller;

import java.io.IOException;
import java.util.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import transitSystem.*;

/** A Trip Controller that accepts inputs from Trip interface and converts them to commands. */
public class TripController implements Observer {

  /** A TapIn Controller that accepts inputs TapIn interface and converts them to commands. */
  private TapController tapInController;
  /** A TapOut Controller that accepts inputs TapOut interface and converts them to commands. */
  private TapController tapOutController;
  /** A TopUp Controller that accepts inputs LoadMyCard interface and converts them to commands. */
  private TopUpController topUpController;

  @FXML private AnchorPane mapPane;
  /** A Label that represents card id. */
  @FXML private Label cardIdLabel;
  /** A Label that represents station location. */
  @FXML private Label locationLabel;
  /** A Label that represents passenger's status: on bus, on metro or free. */
  @FXML private Label statusLabel;
  /** A Label that represents current date. */
  @FXML private Label dateLabel;
  /** A Scene that represents previous Scene. */
  private Scene previousScene;

  /**
   * Sets up a TopUp Controller.
   *
   * @param topUpController A TopUp Controller that accepts inputs LoadMyCard interface and converts
   *     them to commands.
   */
  public void setTopUpController(TopUpController topUpController) {
    this.topUpController = topUpController;
  }

  /**
   * Sets the previous Scene.
   *
   * @param previousScene A Scene that represents previous Scene.
   */
  public void setPreviousScene(Scene previousScene) {
    this.previousScene = previousScene;
  }

  /**
   * Updates changes when clicking on subway or bus icons on Trip interface.
   *
   * @param o An observer to be added.
   * @param arg An object when change occurs on Trip interface.
   */
  @Override
  @SuppressWarnings("unchecked")
  public void update(Observable o, Object arg) {
    Tuple<Station, Calendar> inStationInfo =
        ((HashMap<Integer, Tuple<Station, Calendar>>) arg)
            .get(Integer.parseInt(cardIdLabel.getText()));
    // check the status of the passenger
    if (inStationInfo == null) {
      statusLabel.setText("Free");
    } else {
      String type = inStationInfo.zero.getStationType();
      if (type.equals("Station")) {
        statusLabel.setText("On Metro");
      } else if (type.equals("Stop")) {
        statusLabel.setText("On Bus");
      }
    }
  }

  /**
   * Sets up TapOut Controller.
   *
   * @param controller A TapOut Controller that accepts inputs TapOut interface and converts them to
   *     commands.
   */
  public void setTapOutController(TapController controller) {
    tapOutController = controller;
  }

  /**
   * Sets up TapIn Controller.
   *
   * @param controller A TapIn Controller that accepts inputs TapIn interface and converts them to
   *     commands.
   */
  public void setTapInController(TapController controller) {
    tapInController = controller;
  }

  /**
   * Replaces the empty status label with passenger's status.
   *
   * @param status A Label that represents passenger's status: on bus, on metro or free.
   */
  void setStatusLabel(String status) {
    statusLabel.setText(status);
  }

  /**
   * Replaces the empty date label with current date.
   *
   * @param date A Label that represents current date.
   */
  void setDateLabel(String date) {
    dateLabel.setText(date);
  }

  /**
   * Replaces the empty card id label with current card id.
   *
   * @param id A Label that represents card id.
   */
  void setCardIdLabel(int id) {
    cardIdLabel.setText(String.format("%s", id));
  }

  /**
   * Replaces the empty location label with selected location.
   *
   * @param location A Label that represents station location.
   */
  void setLocationLabel(String location) {
    locationLabel.setText(String.format("%s", location));
  }

  /** Reset all the station and stop button. */
  public void resetStationButton() {
    for (Node node : mapPane.getChildren()) {
      if (node.getId() != null) {
        ((Button) node)
            .setOnAction(
                event -> {
                  String[] word = node.getId().split("_");
                  String location = word[0] + " " + word[1];
                  locationLabel.setText(location);
                });
      }
    }
  }

  /**
   * Back to the initial Login page when click "back" button.
   *
   * @param mouseEvent An on mouse click event.
   */
  @FXML
  public void clickBack(MouseEvent mouseEvent) {
    Stage s = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
    s.setScene(previousScene);
    s.show();
  }

  /**
   * Conducts several reactions when clicking "tap in" button.
   *
   * @throws IOException An IOException that in case of missing GUI file.
   */
  public void clickTapIn() throws IOException {
    AdminUser adminUser = LoginController.adminUser;
    CardManager cardManager = adminUser.getCardManager();
    // get current card id
    int cardId = Integer.parseInt(cardIdLabel.getText());
    String status = statusLabel.getText();
    // check the passenger's status
    if (locationLabel.getText().equals("")) {
      AlertController.failAlert("Please enter the location.");
    } else if (status.equals("On Metro") || status.equals("On Bus")) {
      AlertController.failAlert("You have already " + status);
    } else if (cardManager.findCard(cardId).isSuspended()) {
      AlertController.failAlert(String.format("Card %d has been suspended", cardId));
    } else if (!cardManager.isAbleDeduct(cardId)) {
      AlertController.failAlert("Insufficient balance!");
      // tap in successfully
    } else {
      tapInController.setCardIdLabel(cardIdLabel.getText());
      tapInController.setLocationLabel(locationLabel.getText());
      tapInController.setDateLabel(dateLabel.getText());
      Stage stage = new Stage();
      stage.setScene(tapInController.getTapInScene());
      stage.show();
    }
  }

  /**
   * Conducts several reactions when clicking "tap out" button.
   *
   * @throws IOException An IOException that in case of missing GUI file.
   */
  public void clickTapOut() throws IOException {
    AdminUser adminUser = LoginController.adminUser;
    CardManager cardManager = adminUser.getCardManager();
    // get current card id
    int cardId = Integer.parseInt(cardIdLabel.getText());
    String status = statusLabel.getText();
    // check the passenger's status
    if (locationLabel.getText().equals("")) {
      AlertController.failAlert("Please enter the location.");
    } else if (status.equals("Free")) {
      AlertController.failAlert("Invalid tap out.");
    } else if (cardManager.findCard(cardId).isSuspended()) {
      AlertController.failAlert(String.format("Card %d has been suspended", cardId));
      // tap out successfully
    } else {
      tapOutController.setCardIdLabel(cardIdLabel.getText());
      tapOutController.setLocationLabel(locationLabel.getText());
      tapOutController.setDateLabel(dateLabel.getText());
      Stage stage = new Stage();
      stage.setScene(tapOutController.getTapOutScene());
      stage.show();
    }
  }

  /**
   * Pops up a top up page after clicking "top up" button.
   *
   * @throws IOException An IOException that in case of missing GUI file.
   */
  public void clickTopUp() throws IOException {
    AdminUser adminUser = LoginController.adminUser;
    CardManager cardManager = adminUser.getCardManager();
    TransitPass card = cardManager.findCard(Integer.parseInt(cardIdLabel.getText()));
    // check the type of the card that user wants to top up
    if (card instanceof WeeklyPass) {
      AlertController.failAlert("Weekly pass can not be topped up!");
      // pops up the card loading page
    } else {
      Stage stage = new Stage();
      topUpController.setCurrentCardId(cardIdLabel.getText());
      topUpController.resetTopUpMenu();
      Scene scene = topUpController.getTopUpScene();
      stage.setScene(scene);
      stage.show();
    }
  }

  /**
   * Pops up a page with information of the card after clicking "check balance" button.
   *
   * @throws IOException An IOException that in case of missing GUI file.
   */
  public void clickCheckBalance() throws IOException {
    AdminUser adminUser = LoginController.adminUser;
    CardManager cardManager = adminUser.getCardManager();
    int cardId = Integer.parseInt(cardIdLabel.getText());
    TransitPass transitPass = cardManager.findCard(cardId);
    String remain = "";
    String status;
    double balance = transitPass.viewBalance();
    if (transitPass instanceof TimesPass) {
      remain = String.format("%d times", (int) (balance / 2));
    } else if (transitPass instanceof TrafficCard) {
      remain = String.format("$%.2f", balance);
    } else if (transitPass instanceof WeeklyPass) {
      remain = (int) balance + " days";
    }
    if (transitPass.isSuspended()) {
      status = "suspended";
    } else {
      status = "activated";
    }
    // card information
    String checkInfo =
        "Card id: "
            + cardId
            + System.getProperty("line.separator")
            + "Status: "
            + status
            + System.getProperty("line.separator")
            + "Remaining: "
            + remain;
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/CardBalance.fxml"));
    AnchorPane cardBalance = fxmlLoader.load();
    AlertController alertController = fxmlLoader.getController();
    alertController.setAlertLabel(checkInfo);

    Scene scene = new Scene(cardBalance);
    Stage stage = new Stage();
    stage.setScene(scene);
    stage.show();
  }
}
