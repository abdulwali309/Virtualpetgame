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

import application.controllers.FeedbackController;

public class SettingsModal extends StackPane {

    private Runnable onDoneAction; // Action to perform when "Done" is clicked
    private FeedbackController feedbackController;

    public SettingsModal(FeedbackController feedbackController) {
        this.feedbackController = feedbackController;

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

        Text modalTitle = new Text("Volume Controls");
        modalTitle.setFont(Font.font("Arial", 24));
        modalTitle.setStyle("-fx-font-weight: bold;");

        // Music toggle button
        Button musicButton = createCircularToggleButton("fas-music", feedbackController.isMusicOn());
        musicButton.setOnAction(e -> {
            feedbackController.toggleMusic();
            updateButtonStyle(musicButton, feedbackController.isMusicOn());
        });

        // Sound effects toggle button
        Button volumeButton = createCircularToggleButton("fas-volume-up", feedbackController.isSfxOn());
        volumeButton.setOnAction(e -> {
            feedbackController.toggleSfx();
            updateButtonStyle(volumeButton, feedbackController.isSfxOn());
        });

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

        // Add toggle buttons and done button to the modal
        HBox toggleButtons = new HBox(20, musicButton, volumeButton);
        toggleButtons.setAlignment(Pos.CENTER);

        modalContent.getChildren().addAll(modalTitle, toggleButtons, doneButton);
        this.getChildren().add(modalContent);
    }

    private Button createCircularToggleButton(String iconLiteral, boolean isActive) {
        Button toggleButton = new Button();
        Circle backgroundCircle = new Circle(25);
        backgroundCircle.setFill(isActive ? Color.WHITE : Color.RED);
        backgroundCircle.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 2);");

        FontIcon icon = new FontIcon(iconLiteral);
        icon.setIconSize(20);
        icon.setIconColor(Color.BLACK);

        StackPane iconContainer = new StackPane(backgroundCircle, icon);
        toggleButton.setGraphic(iconContainer);
        toggleButton.setStyle("-fx-background-color: transparent;");

        return toggleButton;
    }

    private void updateButtonStyle(Button button, boolean isActive) {
        StackPane iconContainer = (StackPane) button.getGraphic();
        Circle backgroundCircle = (Circle) iconContainer.getChildren().get(0);
        backgroundCircle.setFill(isActive ? Color.WHITE : Color.RED);
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
