package application.view;

import org.kordamp.ikonli.javafx.FontIcon;

import application.GameLauncher;
import application.components.SettingsModal;
import application.components.WarningModal;
import application.controllers.FeedbackController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class MainMenuScreen {

    private GameLauncher gameLauncher; // Reference to GameLauncher
    private SettingsModal settingsModal;
    private WarningModal warningModal;
    private FeedbackController feedbackController;

    public MainMenuScreen(GameLauncher gameLauncher, FeedbackController feedbackController) {
        this.gameLauncher = gameLauncher;
        this.feedbackController = feedbackController;
    }

    public Scene getScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #fff4e0;");

        // Initialize settingsModal
        settingsModal = new SettingsModal(feedbackController);
        settingsModal.setVisible(false);

        settingsModal.setOnDoneAction(() -> settingsModal.hide());

        // Initialize warningModal
        warningModal = new WarningModal();
        warningModal.setVisible(false);

        warningModal.setOnDoneAction(() -> warningModal.hide());

        // Title at the top
        Text title = new Text("My Pet");
        title.setFont(Font.font("Arial", FontWeight.BLACK, 80));
        BorderPane.setAlignment(title, Pos.TOP_LEFT);
        BorderPane.setMargin(title, new Insets(20, 0, 0, 50));
        root.setTop(title);

        // Center layout for menu options
        VBox menuBox = createMenuBox();
        root.setLeft(menuBox);
        BorderPane.setMargin(menuBox, new Insets(25, 0, 0, 50));

        // Placeholder for image on the right
        StackPane imagePlaceholder = createImagePlaceholder();
        root.setRight(imagePlaceholder);
        BorderPane.setMargin(imagePlaceholder, new Insets(0, 50, 0, 0));

        root.setBottom(createFooter());

        // StackPane to overlay the settings button
        StackPane layeredRoot = new StackPane(root);

        // Add the settings button
        Button settingsButton = createSettingsButton();
        layeredRoot.getChildren().add(settingsButton);

        // Add the settings modal to the layeredRoot
        layeredRoot.getChildren().add(settingsModal);

        layeredRoot.getChildren().add(warningModal);

        StackPane.setAlignment(settingsButton, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(settingsButton, new Insets(20));

        // Start main menu music
        feedbackController.playBackgroundMusic("MainMenu");

        return new Scene(layeredRoot);
    }

    private VBox createMenuBox() {
        VBox menuBox = new VBox(5);
        menuBox.setAlignment(Pos.TOP_LEFT);
        menuBox.setPadding(new Insets(5, 20, 5, 20));
        menuBox.setStyle(
                "-fx-background-color: #8fcccb; -fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);");
        menuBox.setMinWidth(400);
        menuBox.setPrefHeight(500);
        menuBox.setMaxHeight(500);

        String[][] menuItems = {
                { "Start New", "fas-play" },
                { "Load Game", "fas-download" },
                { "Tutorial", "fas-info-circle" },
                { "Parent Zone", "fas-lock" },
                { "Exit", "fas-times" }
        };

        for (String[] item : menuItems) {
            HBox menuItemBox = createMenuItem(item[0], item[1]);
            VBox.setVgrow(menuItemBox, Priority.ALWAYS);
            menuBox.getChildren().add(menuItemBox);
        }

        return menuBox;
    }

    private HBox createMenuItem(String text, String iconLiteral) {
        HBox menuItemBox = new HBox(5);
        menuItemBox.setAlignment(Pos.CENTER_LEFT);

        FontIcon menuIcon = new FontIcon(iconLiteral);
        menuIcon.setIconSize(35);
        menuIcon.setIconColor(Color.BLACK);

        Text menuText = new Text(text);
        menuText.setFont(Font.font("Arial", 36));

        menuItemBox.getChildren().addAll(menuIcon, menuText);

        menuItemBox.setOnMouseClicked(event -> {

            feedbackController.playSoundEffect("buttonSelect");

            feedbackController.stopBackgroundMusic();

            switch (text) {
                case "Start New":
                    gameLauncher.checkWithinTime();
                    if (gameLauncher.getWithinTime()) {
                        gameLauncher.showSaveLoadScreen();
                        System.out.println(gameLauncher.getWithinTime());
                        break;
                    } else {
                        warningModal.show();
                        System.out.println("You are not allowed to play the game based on Parental Controls");
                        break;
                    }
                case "Load Game":
                    gameLauncher.checkWithinTime();
                    if (gameLauncher.getWithinTime()) {
                        gameLauncher.showSaveLoadScreen();
                        System.out.println(gameLauncher.getWithinTime());
                        break;
                    } else {
                        warningModal.show();
                        System.out.println("You are not allowed to play the game based on Parental Controls");
                        break;
                    }
                case "Tutorial":
                    System.out.println("Tutorial screen opened!");
                    gameLauncher.showTutorialScreen();
                    break;
                case "Parent Zone":
                    System.out.println("Parent Zone opened!");
                    gameLauncher.showParentalControlScreen();
                    break;
                case "Exit":
                    System.exit(0);
                    break;
            }
        });

        return menuItemBox;
    }

    private StackPane createImagePlaceholder() {
        VBox imageBox = new VBox();
        imageBox.setAlignment(Pos.CENTER);
        imageBox.setStyle("-fx-background-color: transparent; -fx-border-radius: 10; -fx-background-radius: 10;");
        imageBox.setPrefSize(400, 600);

        javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView(
                getClass().getResource("/mainmenu-pic.png").toExternalForm());
        imageView.setFitWidth(500);
        imageView.setFitHeight(600);
        imageView.setPreserveRatio(true);

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(imageBox, imageView);

        return stackPane;
    }

    private Button createSettingsButton() {
        Button settingsButton = new Button();
        FontIcon settingsIcon = new FontIcon("fas-cog");
        settingsIcon.setIconSize(20);
        settingsIcon.setIconColor(Color.BLACK);

        Circle settingsCircle = new Circle(25);
        settingsCircle.setFill(Color.WHITE);
        settingsCircle.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);");

        StackPane buttonContent = new StackPane(settingsCircle, settingsIcon);
        settingsButton.setGraphic(buttonContent);
        settingsButton.setStyle("-fx-background-color: transparent;");

        settingsButton.setOnAction(e -> settingsModal.setVisible(true));

        return settingsButton;
    }

    private HBox createFooter() {
        HBox footer = new HBox();
        footer.setAlignment(Pos.BOTTOM_LEFT);
        footer.setPadding(new Insets(10));

        // Footer text
        Text footerText = new Text(
                "CS2212 Fall 2024 (Western University) - Created by Group 31: Abdul-Wali Raza Khan, Abdulsalam Shola Ameen, Andrey Velichko, Maximilian Hines Cope, Ryan Frank Wagner");
        footerText.setFont(Font.font("Arial", 12));
        footerText.setFill(Color.GRAY);

        footer.getChildren().add(footerText);

        HBox.setMargin(footerText, new Insets(0, 0, 0, 50));

        return footer;
    }

}
