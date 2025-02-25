package group50.controller;

import group50.model.MessageModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {
    @FXML private Label messageLabel;
    private final MessageModel model = new MessageModel();

    @FXML
    public void initialize() {
        messageLabel.setText(model.getMessage());
    }
}
