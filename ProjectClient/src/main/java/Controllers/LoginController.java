package Controllers;

import Model.User;
import Services.Service;
import Services.ServiceException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class LoginController {
    private Service server;

    private MenuController menuCtrl;

    Parent mainMenuParent;

    @FXML
    public Button loginButton;
    @FXML
    public TextField usernameField;
    @FXML
    public TextField passwordField;
    @FXML
    public Label wrongLogin;

    public void setServer(Service s){
        server=s;
    }

    public void setParent(Parent p){
        mainMenuParent=p;
    }

    public void pressCancel(ActionEvent actionEvent) {
        System.exit(0);
    }

    public void setMenuCtrl(MenuController menuCtrl) {
        this.menuCtrl = menuCtrl;
    }

    public void login(ActionEvent actionEvent){
        String username = usernameField.getText();
        User user = new User(username);

        try {
            user = server.login(user, menuCtrl);
            if(user != null) {
                menuCtrl.setUser(user);
//                menuCtrl.init();
                Stage stage=new Stage();
                stage.setTitle("Menu Window");
                stage.setScene(new Scene(mainMenuParent));

                stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        menuCtrl.logout();
                        System.exit(0);
                    }
                });
                stage.show();
                ((Node)(actionEvent.getSource())).getScene().getWindow().hide();
                usernameField.clear();
            }

        } catch (ServiceException serviceException) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("MPP chat");
            alert.setHeaderText("Authentication failure");
            alert.setContentText("Wrong username or password");
            alert.showAndWait();
        }
    }
}
