package application.view;

import java.util.ArrayList;
import java.util.Map;
import java.util.TimerTask;

import org.kordamp.ikonli.javafx.FontIcon;
import application.GameLauncher;
import application.components.InventoryModal;
import application.components.SettingsModal;
import application.components.StatModal;
import application.controllers.FeedbackController;
import application.controllers.GameplayController;
import application.model.GameState;
import application.model.Pet;
import javafx.geometry.Pos;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import application.components.InventoryModal;
import application.components.PauseModal;
import java.util.Timer;
import java.util.TimerTask;

public class GameplayScreen {

    private GameLauncher gameLauncher;
    private GameplayController controller;
    private boolean isNewGame;
    private String petName;
    private VBox root;
    private VBox namingModal;
    private int selectedPetNumber;
    private TextField nameInput;
    private SettingsModal settingsModal;
    private VBox contentLayout;
    private StackPane statsContainer;
    private StatModal statModal;
    private Button inventoryButton;
    private Button settingsButton;
    private InventoryModal inventoryModal;
    private Text scoreText;
    private int score = 0;
    private ImageView petImageView;
    private FeedbackController feedbackController;
    private GameState gameState;
    private Timer periodicUpdateTimer;
    private boolean isPetConfirmed = false;
    private Button pauseButton;
    private PauseModal pauseModal;

    public GameplayScreen(GameLauncher gameLauncher, FeedbackController feedbackController,
            GameplayController controller, GameState gameState,
            String petName) {
        this.gameLauncher = gameLauncher;
        this.feedbackController = feedbackController;
        this.gameState = gameState;
        this.controller = controller;

        // Initialize GameplayController with GameState
        this.controller = new GameplayController(gameState);
        this.inventoryModal = new InventoryModal(gameState, feedbackController);
        controller.setOnInventoryUpdated(() -> inventoryModal.refreshInventoryPage());

        this.petName = petName;

        initializeScreen();

        // For loaded games, start the stat decay immediately
        if (gameState.getPlayer().getCurrentPet() != null &&
                gameState.getPlayer().getCurrentPet().getName() != null) {
            isPetConfirmed = true;
            controller.startStatDecay();
        }
    }

    public Scene getScene() {
        return new Scene(root, 1200, 800);
    }

    private void initializeScreen() {
        // Root container
        root = new VBox();
        root.setPadding(new Insets(0));
        root.setStyle("-fx-background-color: #f5f5f5;");
        root.setAlignment(Pos.TOP_CENTER);

        // Fetch values from the controller
        // String petName = controller.getPetName();
        int hunger = controller.getPetHunger();
        int happiness = controller.getPetHappiness();
        int sleep = controller.getPetSleep();
        int health = controller.getPetHealth();

        // Play background music
        if (feedbackController != null) {
            feedbackController.playBackgroundMusic("Gameplay");
        }

        // StackPane for layered layout
        StackPane layeredLayout = new StackPane();
        layeredLayout.setStyle("-fx-background-color: transparent;");
        layeredLayout.setPrefSize(1200, 800);

        // Content Layer (Game Content)
        contentLayout = new VBox();
        contentLayout.setAlignment(Pos.TOP_CENTER);
        contentLayout.setPadding(new Insets(20));
        layeredLayout.getChildren().add(contentLayout);

        // Button Layer (Interactive Elements: Buttons and Stats Container)
        Pane buttonLayer = new Pane();

        // Go-Back Button
        Button goBackButton = createGoBackButton();
        goBackButton.setLayoutX(20);
        goBackButton.setLayoutY(20);

        // Settings Button
        settingsButton = createSettingsButton();
        settingsButton.setLayoutX(1120);
        settingsButton.setLayoutY(700);

        // Pause Button
        pauseButton = createPauseButton();
        pauseButton.setLayoutX(1120);
        pauseButton.setLayoutY(625);

        // Stats Container
        statsContainer = createStatsContainer();
        statsContainer.setLayoutX(20);
        statsContainer.setLayoutY(450);

        // Inventory Button
        inventoryButton = createInventoryButton();
        inventoryButton.setLayoutX(20);
        inventoryButton.setLayoutY(400);

        // TESTINGGGGGGGGGGG
        updateStatBar("Hunger", hunger);
        updateStatBar("Happiness", happiness);
        updateStatBar("Sleep", sleep);
        updateStatBar("Health", health);

        // Add interactive elements to the button layer
        buttonLayer.getChildren().addAll(goBackButton, pauseButton, settingsButton, statsContainer, inventoryButton);
        buttonLayer.setPickOnBounds(false);

        // Add button layer to the layered layout
        layeredLayout.getChildren().add(buttonLayer);

        // Modal Layer (naming, settings, and stat modals)
        setupNamingModal();
        settingsModal = new SettingsModal(feedbackController);

        pauseModal = new PauseModal(feedbackController, controller);
        statModal = new StatModal(controller, inventoryModal, feedbackController);
        inventoryModal = new InventoryModal(gameState, feedbackController);

        // Add modals to the layered layout
        layeredLayout.getChildren().addAll(namingModal, settingsModal, pauseModal, statModal, inventoryModal);

        // Add layered layout to the root
        root.getChildren().add(layeredLayout);

        // Handle new or existing game
        if (gameState.getPlayer().getCurrentPet() == null ||
                gameState.getPlayer().getCurrentPet().getName() == null) {
            System.out.println("Pet is not initialized or name is null. Showing pet selection.");
            setupPetSelection(contentLayout);
            toggleUIElementsVisibility(false);
        } else {
            String petName = gameState.getPlayer().getCurrentPet().getName();
            setupMainGameplay(contentLayout, petName);
            toggleUIElementsVisibility(true);
        }

        startPeriodicUpdates();

        root.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                returnToMainMenu();
            } else if (event.getCode() == KeyCode.P) {
                pauseModal.setVisible(true); // Open the pause modal
                // stopPeriodicUpdates(); // Stop periodic updates
                feedbackController.stopBackgroundMusic(); // Stop the background music
                System.out.println("Game paused (via P key).");
            } else if (event.getCode() == KeyCode.F) {
                // Simulate clicking on the "Hunger" stat bar
                statModal.setTitle("Hunger");
                statModal.setVisible(true);
                feedbackController.playSoundEffect("buttonSelect"); // Play sound effect
                System.out.println("StatModal opened for Hunger (via F key).");
            } else if (event.getCode() == KeyCode.G) {
                // Simulate clicking on the "Hunger" stat bar
                statModal.setTitle("Happiness");
                statModal.setVisible(true);
                feedbackController.playSoundEffect("buttonSelect"); // Play sound effect
                System.out.println("StatModal opened for Hunger (via F key).");
            }
        });
    }

    /*
     * private void setupPetImage(String petState, String petName) {
     * // Use the pet state and name to set up the image
     * if (petName != null && !petName.isEmpty()) {
     * String imagePath = "/sprite states/" + petName + "/" + petState +
     * "-state.png";
     * try {
     * Image petImage = new Image(getClass().getResourceAsStream(imagePath));
     * petImageView.setImage(petImage);
     * } catch (Exception e) {
     * System.err.println("Error loading pet image: " + e.getMessage());
     * }
     * } else {
     * System.out.println("No pet name provided, setting default image.");
     * }
     * }
     */

    private void setupPetSelection(VBox contentLayout) {
        // Top bar with Close Button and Title
        HBox topBar = new HBox(10);

        topBar.setAlignment(Pos.TOP_LEFT);
        topBar.setPadding(new Insets(10, 0, 10, 60));

        // Close Button with Circle Background
        StackPane closeButtonContainer = new StackPane();
        Circle closeButtonCircle = new Circle(25, Color.WHITE);
        closeButtonCircle.setEffect(new DropShadow(10, Color.GRAY));

        FontIcon closeIcon = new FontIcon("fas-times");
        closeIcon.setIconSize(20);
        closeIcon.setIconColor(Color.BLACK);

        // Title for the top bar
        VBox titleBox = new VBox();
        titleBox.setAlignment(Pos.TOP_LEFT);
        titleBox.setPadding(new Insets(10, 0, 0, 10));
        Text title = new Text("Pet Selection");
        title.setFont(Font.font("Arial", 24));
        title.setStyle("-fx-font-weight: bold;");
        titleBox.getChildren().add(title);

        topBar.getChildren().addAll(closeButtonContainer, titleBox);

        // Pet card selection
        HBox rectangleBox = new HBox(50);
        rectangleBox.setAlignment(Pos.CENTER);
        rectangleBox.setPadding(new Insets(50));

        // Generate pet cards
        for (int i = 1; i <= 3; i++) {
            VBox petCard = createPetCard(i);
            rectangleBox.getChildren().add(petCard);
        }

        // Bottom section with "Choose a Pet!"
        Text chooseText = new Text("Choose a Pet!");
        chooseText.setFont(Font.font("Arial", 40));
        chooseText.setStyle("-fx-font-weight: bold;");
        VBox.setMargin(chooseText, new Insets(0, 0, 20, 0));

        // Add components to the content layout
        contentLayout.getChildren().addAll(topBar, rectangleBox, chooseText);
    }

    private VBox createPetCard(int number) {
        VBox card = new VBox();
        card.setAlignment(Pos.CENTER);
        card.setSpacing(10);
        card.setPadding(new Insets(20));
        card.setStyle(
                "-fx-background-color: #8fcccb; " +
                        "-fx-border-radius: 15; " +
                        "-fx-background-radius: 15; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);");

        // Label the pet card
        Text petNumber = new Text("Pet " + number);
        petNumber.setFont(Font.font("Arial", 20));

        // Load the image from the "sprite states" folder
        String imagePath = String.format("/sprite states/pet%d/normal-state.png", number); // Adjusted path
        Image petImage;
        try {
            petImage = new Image(getClass().getResourceAsStream(imagePath));
        } catch (Exception e) {
            System.out.println("Error loading image for pet " + number + ": " + e.getMessage());
            petImage = new Image(getClass().getResourceAsStream("/default.png")); // Fallback image
        }
        ImageView petImageView = new ImageView(petImage);
        petImageView.setFitWidth(200);
        petImageView.setFitHeight(200);
        petImageView.setPreserveRatio(true);

        petImageView.setOnMouseClicked(e -> {
            selectedPetNumber = number; // Store the selected pet number
            nameInput.clear(); // Clear the input field for a fresh start
            namingModal.setVisible(true); // Show the naming modal
        });

        // StackPane to overlay image on button
        StackPane imageButtonContainer = new StackPane();

        // Select button
        Button selectButton = new Button();
        selectButton.setStyle(
                "-fx-background-color: #e0e0e0; " +
                        "-fx-min-width: 220px; " +
                        "-fx-min-height: 350px; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10;");
        selectButton.setOnAction(e -> {
            // feedbackController.playSoundEffect("buttonSelect");
            selectedPetNumber = number;
            nameInput.clear();
            namingModal.setVisible(true);
        });

        // Add image and button to the StackPane
        imageButtonContainer.getChildren().addAll(selectButton, petImageView);

        // Add components to the pet card
        card.getChildren().addAll(imageButtonContainer, petNumber);
        return card;
    }

    private void setupNamingModal() {
        namingModal = new VBox(20);
        namingModal.setAlignment(Pos.CENTER);
        namingModal.setPadding(new Insets(30));
        namingModal.setStyle(
                "-fx-background-color: #ffffff; " +
                        "-fx-border-radius: 20; " +
                        "-fx-background-radius: 20; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 20, 0, 0, 10);" +
                        "-fx-focus-color: transparent; " +
                        "-fx-faint-focus-color: transparent;");
        namingModal.setVisible(false);

        namingModal.setMaxWidth(400);
        namingModal.setMaxHeight(300);

        // Modal Title
        Text modalTitle = new Text("Naming Time!");
        modalTitle.setFont(Font.font("Arial", 36));
        modalTitle.setStyle("-fx-font-weight: bold;");

        // Modal Subtitle
        Text modalSubtitle = new Text("Give your pet a name and click confirm.");
        modalSubtitle.setFont(Font.font("Arial", 16));

        // TextField for input
        nameInput = new TextField();
        nameInput.setPromptText("Name me...");
        nameInput.setMaxWidth(400);
        nameInput.setStyle(
                "-fx-background-radius: 20; " +
                        "-fx-border-radius: 20; " +
                        "-fx-border-color: #dcdcdc; " +
                        "-fx-border-width: 1; " +
                        "-fx-padding: 10;");

        // Buttons
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: transparent;");
        cancelButton.setOnAction(e -> {
            feedbackController.playSoundEffect("buttonSelect");
            namingModal.setVisible(false);
            toggleUIElementsVisibility(false);
        });

        Button confirmButton = new Button("Confirm");
        confirmButton.setStyle(
                "-fx-background-color: #638EFB; " +
                        "-fx-text-fill: #ffffff; " +
                        "-fx-background-radius: 15; " +
                        "-fx-padding: 10 20;");
        confirmButton.setOnAction(e -> {
            feedbackController.playSoundEffect("buttonSelect");
            String petName = nameInput.getText();
            if (!petName.trim().isEmpty()) {
                // Create the Pet object
                Pet selectedPet = new Pet(petName, 100, 100, 100, 100, new ArrayList<>(), selectedPetNumber);

                // Save the pet to the player
                gameState.getPlayer().setCurrentPet(selectedPet);

                // Save the updated game state
                gameLauncher.saveGame(gameState);

                namingModal.setVisible(false);
                toggleUIElementsVisibility(true);

                isPetConfirmed = true;
                // Start stat decay now that pet is confirmed
                controller.startStatDecay();

                // Proceed to main gameplay
                gameLauncher.showGameplay(false, petName, gameState.getSaveSlot());
            } else {
                nameInput.setPromptText("Please enter a name!");
            }
        });

        buttonBox.getChildren().addAll(cancelButton, confirmButton);

        // Add all components to the modal
        namingModal.getChildren().addAll(modalTitle, modalSubtitle, nameInput, buttonBox);

        // Add the modal to the root layout inside a StackPane
        StackPane modalContainer = new StackPane();
        modalContainer.setAlignment(Pos.CENTER);
        modalContainer.getChildren().add(namingModal);
        modalContainer.setStyle("-fx-background-color: rgba(0,0,0,0.5);");
        modalContainer.setVisible(false);

        StackPane.setMargin(namingModal, new Insets(-30, 0, 0, 0));

        root.getChildren().add(modalContainer);

        namingModal.setVisible(false);

        // Initially hide the UI elements
        namingModal.visibleProperty().addListener((observable, oldValue, newValue) -> {
            toggleUIElementsVisibility(!newValue);
        });
    }

    public void setupMainGameplay(VBox root, String petName) {
        root.getChildren().clear();

        // Top bar with back button and welcome message
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(20, 0, 10, 70));
        topBar.setSpacing(10);

        Label welcomeLabel = new Label("Welcome, " + petName + "!");
        welcomeLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        // Score Text
        scoreText = new Text("Score: " + controller.getPlayerScore());
        scoreText.setFont(Font.font("Arial", 24));
        scoreText.setStyle("-fx-font-weight: bold;");
        HBox.setHgrow(scoreText, Priority.ALWAYS);

        topBar.getChildren().addAll(welcomeLabel, scoreText);
        root.getChildren().add(topBar);

        // Main content area
        HBox contentArea = new HBox();
        contentArea.setAlignment(Pos.CENTER);
        contentArea.setPadding(new Insets(20));
        contentArea.setSpacing(50);

        // Reuse statsContainer
        statsContainer.setVisible(true);

        // Right side for pet image
        VBox rightSide = new VBox();
        rightSide.setAlignment(Pos.CENTER);

        // Get the petType from the GameState through the controller
        int petType = controller.getPetType();
        String petState = controller.getMainPetState();

        // Load the selected pet's image dynamically
        try {
            String imagePath = String.format("/sprite states/pet%d/normal-state.png", petType, petState);
            Image petImage = new Image(getClass().getResourceAsStream(imagePath)); // Dynamically set path
            petImageView = new ImageView(petImage);
            petImageView.setFitWidth(500);
            petImageView.setFitHeight(800);
            petImageView.setPreserveRatio(true);

            VBox.setMargin(petImageView, new Insets(20, -200, 0, 0));

            rightSide.getChildren().add(petImageView);

            // Start the mirroring animation
            startSpriteMirroring();

        } catch (IllegalArgumentException e) {
            System.out.println("Pet image not found: " + e.getMessage());
            // Fallback to placeholder if image not found
            Rectangle petImagePlaceholder = new Rectangle(250, 500);
            petImagePlaceholder.setFill(Color.LIGHTGRAY);
            petImagePlaceholder.setStroke(Color.DARKGRAY);
            petImagePlaceholder.setArcWidth(20);
            petImagePlaceholder.setArcHeight(20);
            rightSide.getChildren().add(petImagePlaceholder);
        }

        contentArea.getChildren().addAll(rightSide);
        root.getChildren().add(contentArea);
    }

    private void startSpriteMirroring() {
        if (petImageView == null) {
            return;
        }

        new Thread(() -> {
            while (true) { // Infinite loop to keep the mirroring active
                try {
                    Thread.sleep(3700); // Wait for 3.7 sec
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Platform.runLater(() -> {
                    // Toggle scaleX to mirror the image
                    petImageView.setScaleX(petImageView.getScaleX() == 1 ? -1 : 1);
                });
            }
        }).start();
    }

    private StackPane createStatsContainer() {
        VBox statsBox = new VBox(10);
        statsBox.setAlignment(Pos.BOTTOM_LEFT);
        statsBox.setPadding(new Insets(10));

        String[] statNames = { "Health", "Hunger", "Happiness", "Sleep" };
        String[] fontAwesomeIcon = { "fas-heart", "fas-utensils", "fas-smile", "fas-bolt" };

        for (int i = 0; i < fontAwesomeIcon.length; i++) {
            VBox statBar = createStatBar(statNames[i], fontAwesomeIcon[i]);
            statsBox.getChildren().add(statBar);
        }

        StackPane statsContainer = new StackPane();
        statsContainer.getChildren().add(statsBox);
        StackPane.setAlignment(statsBox, Pos.BOTTOM_LEFT);
        return statsContainer;
    }

    // Helper method to create a stat bar without fill
    private VBox createStatBar(String title, String fontAwesomeIcon) {
        VBox statContainer = new VBox(5);
        statContainer.setAlignment(Pos.BOTTOM_LEFT);

        // Title and percentage container
        HBox titleContainer = new HBox(10);
        titleContainer.setAlignment(Pos.CENTER_LEFT);

        // Stat title
        Label statTitle = new Label(title + ": ");
        statTitle.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: black;");

        // Percentage label (initially set to 100%)
        Label percentageLabel = new Label("100%");
        percentageLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: black;");

        // Add title and percentage to the container
        titleContainer.getChildren().addAll(statTitle, percentageLabel);

        // Stat bar container
        HBox statBar = new HBox();
        statBar.setAlignment(Pos.CENTER_LEFT);
        statBar.setStyle(
                "-fx-background-color: #f0f0f0; " +
                        "-fx-border-radius: 20; " +
                        "-fx-background-radius: 20; " +
                        "-fx-border-color: black; " +
                        "-fx-border-width: 1px;");
        statBar.setPrefHeight(40);
        statBar.setPrefWidth(250);

        // Add a "fill" region inside the stat bar
        Pane fillBar = new Pane();
        fillBar.setStyle("-fx-background-color: #638EFB; -fx-border-radius: 20; -fx-background-radius: 20;");
        fillBar.setPrefHeight(40);

        // Add the fillBar to the statBar
        statBar.getChildren().add(fillBar);

        // Allow clicking the bar
        statBar.setOnMouseClicked(event -> {
            feedbackController.playSoundEffect("buttonSelect");

            System.out.println("Stat bar clicked: " + title);
            statModal.setTitle(title);
            statModal.setVisible(true);
        });

        // Add to the container
        statContainer.getChildren().addAll(titleContainer, statBar);

        // Set an ID for later lookup
        statBar.setId(title);
        percentageLabel.setId(title + "-percentage"); // Set an ID for the percentage label

        return statContainer;
    }

    private void updateStatBar(String statName, double percentage) {
        // Ensure the percentage is valid (0-100)
        percentage = Math.max(0, Math.min(percentage, 100));

        // Find the stat bar by ID
        Node statBar = statsContainer.lookup("#" + statName);
        if (statBar != null && statBar instanceof HBox) {
            HBox statHBox = (HBox) statBar;
            Pane fillBar = (Pane) statHBox.getChildren().get(0);
            fillBar.setPrefWidth((percentage / 100) * statHBox.getPrefWidth());

            // Change the color based on the percentage
            if (percentage < 25) {
                fillBar.setStyle("-fx-background-color: red; -fx-border-radius: 20; -fx-background-radius: 20;");
            } else if (percentage < 50) {
                fillBar.setStyle("-fx-background-color: orange; -fx-border-radius: 20; -fx-background-radius: 20;");
            } else {
                fillBar.setStyle("-fx-background-color: green; -fx-border-radius: 20; -fx-background-radius: 20;");
            }
        } else {
            System.out.println("Stat bar not found for: " + statName);
        }

        // Find the percentage label by ID and update it
        Node percentageLabelNode = statsContainer.lookup("#" + statName + "-percentage");
        if (percentageLabelNode != null && percentageLabelNode instanceof Label) {
            Label percentageLabel = (Label) percentageLabelNode;
            percentageLabel.setText((int) percentage + "%"); // Update the text
        } else {
            System.out.println("Percentage label not found for: " + statName);
        }
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

        settingsButton.setOnAction(e -> {
            feedbackController.playSoundEffect("buttonSelect");
            System.out.println("Settings button clicked!");

            // Show the settings modal
            if (settingsModal == null) {
                settingsModal = new SettingsModal(feedbackController);
            }
            settingsModal.setVisible(true);
        });

        return settingsButton;
    }

    private Button createPauseButton() {
        Button pauseButton = new Button();
        FontIcon pauseIcon = new FontIcon("fas-pause");
        pauseIcon.setIconSize(20);
        pauseIcon.setIconColor(Color.BLACK);

        Circle pauseCircle = new Circle(25);
        pauseCircle.setFill(Color.WHITE);
        pauseCircle.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);");

        StackPane buttonContent = new StackPane(pauseCircle, pauseIcon);
        pauseButton.setGraphic(buttonContent);
        pauseButton.setStyle("-fx-background-color: transparent;");

        pauseButton.setOnAction(e -> {
            System.out.println("Pause button clicked!");

            // Stop music and periodic updates
            if (feedbackController != null) {
                feedbackController.stopBackgroundMusic();
            }
            if (controller != null) {
                controller.stopStatDecay();
                stopPeriodicUpdates();
            }

            // Show the pause modal
            if (pauseModal != null) {
                pauseModal.show();
            }
        });

        return pauseButton;
    }

    private Button createGoBackButton() {
        Button goBackButton = new Button();
        FontIcon backIcon = new FontIcon("fas-chevron-left");
        backIcon.setIconSize(20);
        backIcon.setIconColor(Color.BLACK);

        Circle backCircle = new Circle(25);
        backCircle.setFill(Color.WHITE);
        backCircle.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);");

        StackPane buttonContent = new StackPane(backCircle, backIcon);
        goBackButton.setGraphic(buttonContent);
        goBackButton.setStyle("-fx-background-color: transparent;");

        goBackButton.setOnAction(e -> {
            feedbackController.playSoundEffect("buttonSelect");
            System.out.println("Go Back button clicked!");
            gameLauncher.saveGame(gameState);

            gameLauncher.showGlobalModal("Game saving!");

            // Delay navigation to the main menu by 2 seconds to show saving.
            new java.util.Timer().schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> returnToMainMenu()); // Navigate back to the main menu
                }
            }, 1700); // Delay for 1.8 sec
        });

        return goBackButton;
    }

    private Button createInventoryButton() {
        Button inventoryButton = new Button();
        FontIcon inventoryIcon = new FontIcon("fas-shopping-bag");
        inventoryIcon.setIconSize(20);
        inventoryIcon.setIconColor(Color.BLACK);

        Circle inventoryCircle = new Circle(20);
        inventoryCircle.setFill(Color.WHITE);
        inventoryCircle.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);");

        StackPane buttonContent = new StackPane(inventoryCircle, inventoryIcon);
        inventoryButton.setGraphic(buttonContent);
        inventoryButton.setStyle("-fx-background-color: transparent;");

        inventoryButton.setOnAction(e -> {
            feedbackController.playSoundEffect("buttonSelect");
            System.out.println("Inventory button clicked!");
            if (inventoryModal != null) {
                inventoryModal.refreshAllInventoryPages();
                inventoryModal.goToFirstPage();
                inventoryModal.setVisible(true);
            }
        });

        return inventoryButton;
    }

    private void startPeriodicUpdates() {
        stopPeriodicUpdates(); // Stop any existing timer before starting a new one

        periodicUpdateTimer = new Timer(true); // Create a new daemon timer
        periodicUpdateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (isPetConfirmed) {
                        refreshPetStats();
                        refreshPlayerInfo();
                        refreshInventory();
                        updatePetSprite();
                    }
                });
            }
        }, 0, 1000); // Refresh every 1 second
    }

    public void stopPeriodicUpdates() {
        if (periodicUpdateTimer != null) {
            periodicUpdateTimer.cancel(); // Cancel the timer
            periodicUpdateTimer = null;
            System.out.println("Periodic updates stopped.");
        }
    }

    private void refreshPetStats() {

        System.out.println("Refreshing pet stats...");

        // Retrieve stats from the controller
        int hunger = controller.getPetHunger();
        int happiness = controller.getPetHappiness();
        int sleep = controller.getPetSleep();
        int health = controller.getPetHealth();

        // Update UI components for each stat
        updateStatBar("Hunger", hunger);
        updateStatBar("Happiness", happiness);
        updateStatBar("Sleep", sleep);
        updateStatBar("Health", health);

        System.out.println("Pet stats refreshed: Hunger=" + hunger + ", Happiness=" + happiness +
                ", Sleep=" + sleep + ", Health=" + health);
    }

    private void refreshPlayerInfo() {
        System.out.println("Refreshing player info...");

        // Fetch updated player info
        String playerName = controller.getPlayerName();
        int playerScore = controller.getPlayerScore();

        // Update UI elements
        Label playerNameLabel = (Label) root.lookup("#playerNameLabel");
        if (playerNameLabel != null) {
            playerNameLabel.setText("Player: " + playerName);
        }

        if (scoreText != null) {
            scoreText.setText("Score: " + playerScore);
        } else {
            System.err.println("Score text is null and cannot be updated!");
        }

        System.out.println("Player info refreshed: Name=" + playerName + ", Score=" + playerScore);
    }

    private void refreshInventory() {
        System.out.println("Refreshing inventory...");

        // Retrieve inventory summary from the controller
        Map<String, Integer> inventorySummary = controller.getInventorySummary();

        Label foodItemsLabel = (Label) root.lookup("#foodItemsLabel");
        Label giftItemsLabel = (Label) root.lookup("#giftItemsLabel");

        if (foodItemsLabel != null) {
            foodItemsLabel.setText("Food Items: " + inventorySummary.getOrDefault("Food Items", 0));
        }

        if (giftItemsLabel != null) {
            giftItemsLabel.setText("Gift Items: " + inventorySummary.getOrDefault("Gift Items", 0));
        }

        System.out.println("Inventory refreshed: Food Items=" + inventorySummary.get("Food Items") +
                ", Gift Items=" + inventorySummary.get("Gift Items"));
    }

    private void updatePetSprite() {
        if (gameState.getPlayer().getCurrentPet() != null) {
            int petType = gameState.getPlayer().getCurrentPet().getPetType();
            String state = controller.getPetMainState();

            try {
                String imagePath = String.format("/sprite states/pet%d/%s-state.png", petType, state);
                System.out.println("Loading image: " + imagePath);

                Image petImage = new Image(getClass().getResourceAsStream(imagePath));
                petImageView.setImage(petImage);
            } catch (Exception e) {
                System.err.println("Failed to load image for pet sprite: " + e.getMessage());
            }
        } else {
            System.err.println("Pet is null, cannot update sprite.");
        }
    }

    public boolean isCommandAvailable(String command) {
        String petState = controller.getMainPetState(); // Use the controller to get the pet state
        switch (petState) {
            case "dead":
            case "sleeping":
                return false; // No commands available
            case "angry":
                return command.equals("Give Gift") || command.equals("Play");
            case "hungry":
            case "normal":
                return true; // All commands available
            default:
                return false;
        }
    }

    private void toggleUIElementsVisibility(boolean isVisible) {
        settingsButton.setVisible(isVisible);
        statsContainer.setVisible(isVisible);
        inventoryButton.setVisible(isVisible);
    }

    private void returnToMainMenu() {
        stopPeriodicUpdates();
        controller.stopGameplay();
        gameLauncher.showMainMenu();
        stopMusic();
        feedbackController.playBackgroundMusic("MainMenu");

        System.out.println("Returned to main menu.");
    }

    public void stopMusic() {
        if (feedbackController != null) {
            feedbackController.stopBackgroundMusic();
        }
    }
}
