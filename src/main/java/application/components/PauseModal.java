package application.components;

import application.controllers.FeedbackController;
import application.controllers.GameplayController;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.geometry.Insets;

public class PauseModal extends StackPane {

    private FeedbackController feedbackController;
    private GameplayController gameplayController;

    public PauseModal(FeedbackController feedbackController, GameplayController gameplayController) {
        this.feedbackController = feedbackController;
        this.gameplayController = gameplayController;

        this.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);"); // Dim background
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
        modalContent.setMaxHeight(200);

        Text modalTitle = new Text("Game Paused");
        modalTitle.setFont(Font.font("Arial", 24));
        modalTitle.setStyle("-fx-font-weight: bold;");

        Button resumeButton = new Button("Resume");
        resumeButton.setStyle(
                "-fx-background-color: #638EFB; " +
                        "-fx-text-fill: #ffffff; " +
                        "-fx-background-radius: 15; " +
                        "-fx-padding: 10 20;");
        resumeButton.setOnAction(e -> {
            System.out.println("Resume button clicked!");

            // Resume music and stat decay
            if (feedbackController != null) {
                feedbackController.playBackgroundMusic("Gameplay");
            }
            if (gameplayController != null) {
                gameplayController.startStatDecay();
            }

            this.hide();
        });

        modalContent.getChildren().addAll(modalTitle, resumeButton);
        this.getChildren().add(modalContent);
    }

    public void show() {
        this.setVisible(true);
    }

    public void hide() {
        this.setVisible(false);
    }
}
