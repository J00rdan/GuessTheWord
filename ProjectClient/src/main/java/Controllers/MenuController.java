package Controllers;

import Model.Conf;
import Model.Game;
import Model.User;
import Services.Observer;
import Services.Service;
import Services.ServiceException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.time.LocalTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MenuController implements Observer {
    private User user;
    private int gameId;
    private Service server;

    ObservableList<Game> model = FXCollections.observableArrayList();

    @FXML
    public Label lettersLabel;
    @FXML
    public Label endGameLabel;

    @FXML
    public TableView<Game> topTable;

    @FXML
    public TableColumn<Game, String> usernameColumn;
    @FXML
    public TableColumn<Game, String> timeColumn;
    @FXML
    public TableColumn<Game, String> totalPointsColumn;
    @FXML
    public TableColumn<Game, String> guessedColumn;

    @FXML
    public TextField inputField;

    @FXML
    public Button submitButton;

    public void setUser(User user){
        this.user = user;
        try {
            Map<String, String> map= server.startGame(user);
            gameId = Integer.parseInt(map.get("gameId"));
            String letters = map.get("letters");
            lettersLabel.setText("Mask: " + letters);
            init();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    public void setServer(Service s) {
        server = s;
    }

    public void init(){
        initModel();
        initialize();
    }

    private void initModel() {
        Iterable<Game> messages = null;
        try {
            messages = server.getAllGamesFinished();
            List<Game> messageTaskList = StreamSupport.stream(messages.spliterator(), false).collect(Collectors.toList());

            model.setAll(messageTaskList);
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        // TODO
        usernameColumn.setCellValueFactory(new PropertyValueFactory<Game, String>("username"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<Game, String>("time"));
        totalPointsColumn.setCellValueFactory(new PropertyValueFactory<Game, String>("totalPoints"));
        guessedColumn.setCellValueFactory(new PropertyValueFactory<Game, String>("totalGuessedLetters"));
        topTable.setItems(model);
    }

    public void submitInput(){
        String input = inputField.getText();

        Map<String, String> map = new HashMap<>();
        map.put("gameId", String.valueOf(gameId));
        map.put("input", input);

        try {
            int result = server.continueGame(map);
            if(result == 0){
                Game game = server.endGame(gameId);
                Conf conf = server.getConfByID(game.getConfId());
                submitButton.setDisable(true);
                endGameLabel.setText("Total points: " + String.valueOf(game.getTotalPoints()) + "  Word: " + conf.getWord());
            }
            else if(result == 1) endGameLabel.setText("Good");
            else endGameLabel.setText("Bad");
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        inputField.clear();
    }

    public void handleLogout(ActionEvent actionEvent) {
        logout();
        ((Node)(actionEvent.getSource())).getScene().getWindow().hide();
    }

    @FXML
    void logout() {
        try {
            server.logout(user, this);
        } catch (ServiceException e) {
            System.out.println("Logout error " + e);
        }
    }

    @Override
    public void gameFinished(Game game) throws ServiceException {
        Platform.runLater(()->{
            init();
        });
    }
}
