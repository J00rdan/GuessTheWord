package Controllers;

import Model.User;
import Services.Observer;
import Services.Service;
import Services.ServiceException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.HashMap;
import java.util.Map;

public class MenuController implements Observer {
    private User user;
    private Service server;

    public void setUser(User user){
        this.user = user;
    }

    public void setServer(Service s) {
        server = s;
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
}
