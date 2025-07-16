package application.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.kordamp.ikonli.javafx.FontIcon;


public class WarningModal extends StackPane {

    private Runnable onDoneAction; // Action to perform when "Done" is clicked


    public WarningModal() {

        this.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
        this.setAlignment(Pos.CENTER);
        this.setVisible(false);

        VBox modalContent = new VBox(20);
        modalContent.setAlignment(Pos.CENTER);
        modalContent.setStyle(
                "-fx-background-color: #ffffff; " +
                        "-fx-border-radius: 15; " +
                        "-fx-background-radius: 15; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 20, 0, 0, 10);");
        modalContent.setPadding(new Insets(30));
        modalContent.setMaxWidth(400);
        modalContent.setMaxHeight(300);

        Text modalTitle = new Text("You are not allowed to play the game based on Parental Controls");
        modalTitle.setFont(Font.font("Arial", 24));
        modalTitle.setStyle("-fx-font-weight: bold;");

        // Done button
        Button doneButton = new Button("Done");
        doneButton.setStyle(
                "-fx-background-color: #638EFB; " +
                        "-fx-text-fill: #ffffff; " +
                        "-fx-background-radius: 15; " +
                        "-fx-padding: 10 20;");
        doneButton.setOnAction(e -> {
            this.setVisible(false);
            if (onDoneAction != null) {
                onDoneAction.run();
            }
        });

        modalContent.getChildren().addAll(modalTitle, doneButton);
        this.getChildren().add(modalContent);
    }


    public void setOnDoneAction(Runnable action) {
        this.onDoneAction = action;
    }

    public void show() {
        this.setVisible(true);
    }

    public void hide() {
        this.setVisible(false);
    }
}
