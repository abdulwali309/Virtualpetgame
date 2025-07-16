package application.view;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONObject;

import application.GameLauncher;
import application.controllers.ParentalControlController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * The ParentalControlScreen class represents a screen in the game for managing parental controls.
 * It includes feautres such as password authentication, time limits, statistics viewing, and pet revival
 * @author Abdul-Wali Khan
 */


public class ParentalControlScreen {
    /** The Scene object for the ParentalControlScreen. */
    private Scene scene;
    /** The Stage object on which the scene is displayed. */
    private Stage stage;
    /** The GameLauncher instance for transitioning between screens. */
    private GameLauncher gameLauncher;
    /** The controller responsible for managing parental control logic. */
    private ParentalControlController controller;

    /** Stores the selected hours for time restrictions. */
    private Set<Integer> selectedHours = new HashSet<>();


    /**
     * Constructs a ParentalControlScreen with the specified game launcher, stage, and controller.
     *
     * @param gameLauncher the GameLauncher instance for screen transitions
     * @param stage the Stage object on which the screen is displayed
     * @param controller the controller responsible for managing parental control logic
     */
    public ParentalControlScreen(GameLauncher gameLauncher, Stage stage, ParentalControlController controller) {
        this.gameLauncher = gameLauncher;
        this.stage = stage;
        this.controller = controller;

        StackPane mainContainer = new StackPane();

        VBox contentContainer = new VBox(20);
        contentContainer.setPadding(new Insets(20));
        contentContainer.setAlignment(Pos.CENTER);
        contentContainer.setStyle("-fx-background-color: #fff4e0;");

        // Password authentication screen
        createPasswordAuthenticationScreen(contentContainer);

        // Add both content and overlay to the StackPane
        Button closeButton = createOverlayButton();
        StackPane.setAlignment(closeButton, Pos.TOP_LEFT);
        StackPane.setMargin(closeButton, new Insets(20, 0, 0, 20));

        mainContainer.getChildren().addAll(contentContainer, closeButton);

        // Create the scene with size 1200x800
        this.scene = new Scene(mainContainer, 1200, 800);
    }
    
    /**
     * Creates the overlay button ("X") for closing the screen and returning to the main menu.
     *
     * @return the close button
     */

    private Button createOverlayButton() {
        Button closeButton = new Button("X");
        closeButton.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        closeButton.setStyle(
                "-fx-background-color: #ffffff; " +
                        "-fx-border-color: #cccccc; " +
                        "-fx-border-radius: 50; " +
                        "-fx-background-radius: 50; " +
                        "-fx-min-width: 70; " +
                        "-fx-min-height: 70; " +
                        "-fx-text-fill: black;");
        closeButton.setOnAction(e -> gameLauncher.showMainMenu());
        return closeButton;
    }

    /**
     * Creates the password authentication screen, prompting the user to enter a password.
     *
     * @param mainContainer the main container for the screen layout
     */
    private void createPasswordAuthenticationScreen(VBox mainContainer) {
        mainContainer.getChildren().clear(); // Clear previous content

        // Create the square box layout
        VBox box = new VBox(20);
        box.setPadding(new Insets(20));
        box.setAlignment(Pos.CENTER);
        box.setStyle(
                "-fx-background-color: #fff4e0; -fx-border-color: #cccccc; -fx-border-radius: 10; -fx-background-radius: 10;");
        box.setMaxWidth(400);
        box.setMaxHeight(400);

        // Add lock image
        Label lockIcon = new Label("\uD83D\uDD12"); // Unicode for lock icon
        lockIcon.setStyle("-fx-font-size: 60; -fx-text-fill: #555;");

        // Title
        Label titleLabel = new Label("Locked");
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: black;");

        // Password input field
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.setStyle(
                "-fx-font-size: 16; " +
                        "-fx-padding: 10; " +
                        "-fx-border-color: #cccccc; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5; " +
                        "-fx-background-color: #ffffff;");

        // Confirm button
        Button confirmButton = new Button("Confirm");
        confirmButton.setStyle(
                "-fx-font-size: 18; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 10 30; " +
                        "-fx-background-color: #d3d3d3; " +
                        "-fx-text-fill: #333333; " +
                        "-fx-border-radius: 20; " +
                        "-fx-background-radius: 20;");

        confirmButton.setOnAction(e -> {
            String enteredPassword = passwordField.getText();

            if (controller.verifyPassword(enteredPassword)) {
                // Proceed to parental control sections
                VBox tabPaneContainer = createParentalControlTabs();
                mainContainer.getChildren().clear();
                mainContainer.getChildren().add(tabPaneContainer);
            } else {
                // Display error message
                Alert alert = new Alert(Alert.AlertType.ERROR, "Incorrect Password. Please try again.", ButtonType.OK);
                alert.showAndWait();
            }
        });

        // Add components to the box
        box.getChildren().addAll(lockIcon, titleLabel, passwordField, confirmButton);

        // Add the box to the main container
        mainContainer.getChildren().add(box);
    }

    /**
     * Creates the parental control tabs for managing time limits, viewing stats, and reviving pets.
     *
     * @return the VBox containing the tabbed layout
     */
    private VBox createParentalControlTabs() {
        // Outer container
        VBox container = new VBox(20);
        container.setAlignment(Pos.TOP_CENTER);
        container.setStyle("-fx-background-color: #fff4e0; -fx-padding: 20;");

        // Title (Moved outside the white box)
        Label titleLabel = new Label("Parental Controls");
        titleLabel.setStyle("-fx-font-size: 32; -fx-font-weight: bold; -fx-text-fill: black; -fx-padding: 10;");

        // Rounded square box to hold the tabs' contents
        VBox roundedSquareBox = new VBox(20);
        roundedSquareBox.setPadding(new Insets(30));
        roundedSquareBox.setAlignment(Pos.TOP_CENTER);
        roundedSquareBox.setStyle(
                "-fx-background-color: #ffffff; " +
                        "-fx-border-color: #cccccc; " +
                        "-fx-border-radius: 20; " +
                        "-fx-background-radius: 20;");
        roundedSquareBox.setMaxWidth(800);
        roundedSquareBox.setMaxHeight(600);

        // Content area for the current tab
        StackPane tabContentArea = new StackPane();
        tabContentArea.setStyle("-fx-padding: 20;");

        // Create the three tabs and add their content
        VBox timeLimitContent = createTimeLimitView();
        VBox statsContent = createStatsView();
        VBox petRevivalContent = createPetRevivalView();

        // Initially show the Time Limit content
        tabContentArea.getChildren().add(timeLimitContent);

        // Navigation bar (horizontal oval at the bottom)
        HBox tabBar = new HBox(10);
        tabBar.setAlignment(Pos.CENTER);
        tabBar.setStyle(
                "-fx-background-color: #d3d3d3; " +
                        "-fx-border-radius: 30; " +
                        "-fx-background-radius: 30; " +
                        "-fx-padding: 10;");
        tabBar.setMaxWidth(600);

        // Tab buttons
        Button timeLimitTab = new Button("Time Limit");
        Button statsTab = new Button("Stats");
        Button petRevivalTab = new Button("Pet Revival");

        // Style for tab buttons
        String tabButtonStyle = "-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: black; " +
                "-fx-background-color: transparent; -fx-padding: 10 20; -fx-background-radius: 30;";
        timeLimitTab.setStyle(tabButtonStyle);
        statsTab.setStyle(tabButtonStyle);
        petRevivalTab.setStyle(tabButtonStyle);

        // Tab button actions
        timeLimitTab.setOnAction(e -> {
            tabContentArea.getChildren().clear();
            tabContentArea.getChildren().add(timeLimitContent);
        });
        statsTab.setOnAction(e -> {
            tabContentArea.getChildren().clear();
            tabContentArea.getChildren().add(statsContent);
        });
        petRevivalTab.setOnAction(e -> {
            tabContentArea.getChildren().clear();
            tabContentArea.getChildren().add(petRevivalContent);
        });

        // Add buttons to the tab bar
        tabBar.getChildren().addAll(timeLimitTab, statsTab, petRevivalTab);

        // Add components to the rounded square box
        roundedSquareBox.getChildren().addAll(tabContentArea);

        // Add the title, white box, and tab bar to the main container
        container.getChildren().addAll(titleLabel, roundedSquareBox, tabBar);

        return container;
    }

    /**
     * Creates the Time Limit section of the parental controls, allowing the user to set restricted hours.
     *
     * @return the VBox containing the Time Limit section
     */
    private VBox createTimeLimitView() {
        // Outer container
        VBox timeLimitView = new VBox(8);
        timeLimitView.setPadding(new Insets(10));
        timeLimitView.setAlignment(Pos.TOP_CENTER);

        // Header container for title and toggle button
        VBox headerContainer = new VBox();
        headerContainer.setPadding(new Insets(5, 10, 5, 10));
        headerContainer.setSpacing(5);
        headerContainer.setAlignment(Pos.CENTER);

        // Header section (center-aligned)
        VBox titleContainer = new VBox(2);
        titleContainer.setAlignment(Pos.CENTER);

        Label header = new Label("Time Limit");
        header.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: black;");

        Label subHeader = new Label("This will enable time restrictions for gameplay");
        subHeader.setStyle("-fx-font-size: 14; -fx-text-fill: gray;");

        titleContainer.getChildren().addAll(header, subHeader);

        boolean isEnabled = controller.isEnabled();
        ToggleButton timeLimitToggle = new ToggleButton();
        timeLimitToggle.setPrefSize(100, 40);

        // sets toggle button based on whether enabled or not
        if (isEnabled) {
            timeLimitToggle.setText("ON");
        } else {
            timeLimitToggle.setText("OFF");
        }

        // sets the toggle button style based on isEnabled
        if (isEnabled) {
            timeLimitToggle.setStyle(
                    "-fx-font-size: 14; -fx-background-color: lightgreen; -fx-text-fill: black; " +
                            "-fx-border-radius: 20; -fx-background-radius: 20;");

        } else {
            timeLimitToggle.setStyle(
                    "-fx-font-size: 14; -fx-background-color: #d3d3d3; -fx-text-fill: black; " +
                            "-fx-border-radius: 20; -fx-background-radius: 20;");
        }

        // Detailed settings container (hidden initially)
        VBox detailedSettings = new VBox(8);
        detailedSettings.setVisible(isEnabled);
        detailedSettings.setPadding(new Insets(5, 10, 10, 10));
        detailedSettings.setAlignment(Pos.TOP_CENTER);

        // Toggle button behavior
        timeLimitToggle.setOnAction(e -> {
            boolean isSelected = timeLimitToggle.getText().equals("OFF");
            if (isSelected) {
                // Set to ON
                timeLimitToggle.setText("ON");
                timeLimitToggle.setStyle(
                        "-fx-font-size: 14; -fx-background-color: lightgreen; -fx-text-fill: black; " +
                                "-fx-border-radius: 20; -fx-background-radius: 20;");
                detailedSettings.setVisible(true);
            } else {
                // Set to OFF
                timeLimitToggle.setText("OFF");
                timeLimitToggle.setStyle(
                        "-fx-font-size: 14; -fx-background-color: #d3d3d3; -fx-text-fill: black; " +
                                "-fx-border-radius: 20; -fx-background-radius: 20;");
                detailedSettings.setVisible(false);
            }
            // Updates the controller's enabled status
            controller.setEnabled(isSelected);
        });

        // Add title and toggle button to the header container
        headerContainer.getChildren().addAll(titleContainer, timeLimitToggle);

        // Header for "Select Day & Time" section
        VBox dayTimeHeaderContainer = new VBox(3);
        dayTimeHeaderContainer.setAlignment(Pos.CENTER);
        dayTimeHeaderContainer.setPadding(new Insets(5, 0, 5, 0));

        Label dayTimeHeader = new Label("Select Time");
        dayTimeHeader.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: black;");

        Label instructions = new Label("Choose times where your child is allowed to play");
        instructions.setStyle("-fx-font-size: 14; -fx-text-fill: gray;");

        dayTimeHeaderContainer.getChildren().addAll(dayTimeHeader, instructions);

        // Hour buttons (24-hour clock)
        VBox hourSelectionContainer = new VBox(8);
        hourSelectionContainer.setAlignment(Pos.CENTER);

        Label hourInstructions = new Label("Select hours that are allowed for gameplay:");
        hourInstructions.setStyle("-fx-font-size: 14; -fx-text-fill: black;");

        GridPane hourGrid = new GridPane();
        hourGrid.setHgap(10);
        hourGrid.setVgap(6);
        hourGrid.setAlignment(Pos.CENTER);

        int columns = 8; // Number of columns in the grid to make it wider

        // getting selected hrs from the controllers allowed hrs
        selectedHours.clear();
        selectedHours.addAll(controller.getAllowedHours());

        for (int hour = 0; hour < 24; hour++) {
            Button hourButton = new Button(String.format("%02d:00", hour));
            hourButton.setPrefWidth(60);

            String buttonStyle;
            if (selectedHours.contains(hour)) {
                buttonStyle = "-fx-font-size: 14; -fx-background-color: lightgreen; -fx-text-fill: black; " +
                        "-fx-border-radius: 5; -fx-background-radius: 5;";
            } else {
                buttonStyle = "-fx-font-size: 14; -fx-background-color: #d3d3d3; -fx-text-fill: black; " +
                        "-fx-border-radius: 5; -fx-background-radius: 5;";
            }
            hourButton.setStyle(buttonStyle);

            final int currentHour = hour; // for use in lambda

            // adds or removes hr when clicked (from selected hr not json)
            hourButton.setOnAction(e -> {
                if (selectedHours.contains(currentHour)) {
                    selectedHours.remove(currentHour);
                    hourButton.setStyle(
                            "-fx-font-size: 14; -fx-background-color: #d3d3d3; -fx-text-fill: black; " +
                                    "-fx-border-radius: 5; -fx-background-radius: 5;");
                } else {
                    selectedHours.add(currentHour);
                    hourButton.setStyle(
                            "-fx-font-size: 14; -fx-background-color: lightgreen; -fx-text-fill: black; " +
                                    "-fx-border-radius: 5; -fx-background-radius: 5;");
                }
            });

            // Add to grid
            hourGrid.add(hourButton, hour % columns, hour / columns);
        }

        hourSelectionContainer.getChildren().addAll(hourInstructions, hourGrid);

        // Add components to the detailed settings
        detailedSettings.getChildren().addAll(dayTimeHeaderContainer, hourSelectionContainer);

        // save button to save the settings
        Button saveButton = new Button("Save");
        saveButton.setStyle(
                "-fx-font-size: 18; -fx-background-color: #d3d3d3; -fx-text-fill: black; " +
                        "-fx-border-radius: 20; -fx-background-radius: 20; -fx-padding: 10 30;");
        saveButton.setOnAction(e -> {
            // save selectedHours to controller
            controller.setAllowedHours(new ArrayList<>(selectedHours));

            // show confirmation msg
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Settings saved successfully.", ButtonType.OK);
            alert.showAndWait();
        });

        timeLimitView.getChildren().addAll(headerContainer, detailedSettings, saveButton);

        return timeLimitView;
    }

    /**
     * Creates the Stats section of the parental controls, displaying gameplay statistics.
     * calculates total time and average session time by dividing time by number of sessions
     * implements the reset button
     * @return the VBox containing the Stats section
     */
    private VBox createStatsView() {
        // Outer container
        VBox statsView = new VBox(40);
        statsView.setPadding(new Insets(30));
        statsView.setAlignment(Pos.TOP_CENTER);

        int totalTimePlayed = controller.getTotalTimePlayed();
        int numberOfLaunches = controller.getNumberOfLaunches();

        // calculate average session time
        int averageSessionTime;
        if (numberOfLaunches > 0) {
            averageSessionTime = totalTimePlayed / numberOfLaunches;
        } else {
            averageSessionTime = 0;
        }

        String totalTimePlayedStr = String.format("%d Minutes", totalTimePlayed);
        String averageSessionTimeStr = String.format("%d Minutes", averageSessionTime);

        // Total Play Time Section
        VBox totalPlayTimeContainer = new VBox(15);
        totalPlayTimeContainer.setAlignment(Pos.CENTER);

        Label totalPlayTimeHeader = new Label(totalTimePlayedStr); // Main heading
        totalPlayTimeHeader.setStyle("-fx-font-size: 40; -fx-font-weight: bold; -fx-text-fill: black;");

        Label totalPlayTimeSubheading = new Label("Total Play Time");
        totalPlayTimeSubheading.setStyle("-fx-font-size: 18; -fx-text-fill: gray;");

        totalPlayTimeContainer.getChildren().addAll(totalPlayTimeHeader, totalPlayTimeSubheading);

        // Average Play Time Section
        VBox avgPlayTimeContainer = new VBox(15);
        avgPlayTimeContainer.setAlignment(Pos.CENTER);

        Label avgPlayTimeHeader = new Label(averageSessionTimeStr); // Main heading
        avgPlayTimeHeader.setStyle("-fx-font-size: 40; -fx-font-weight: bold; -fx-text-fill: black;");

        Label avgPlayTimeSubheading = new Label("Average Play Time");
        avgPlayTimeSubheading.setStyle("-fx-font-size: 18; -fx-text-fill: gray;");

        avgPlayTimeContainer.getChildren().addAll(avgPlayTimeHeader, avgPlayTimeSubheading);

        // Reset Stats Button
        Button resetStatsButton = new Button("Reset");
        resetStatsButton.setStyle(
                "-fx-font-size: 22; -fx-background-color: red; -fx-text-fill: white; " +
                        "-fx-border-radius: 35; -fx-background-radius: 35; -fx-padding: 12 35;");

        // Reset action
        resetStatsButton.setOnAction(e -> {

            // resetting values in json to 1 for time played and 0 for num of launches
            controller.resetStats();

            totalPlayTimeHeader.setText("0 Hours");
            avgPlayTimeHeader.setText("0 Hours");
        });

        // Add components to the main view with increased spacing
        statsView.getChildren().addAll(totalPlayTimeContainer, avgPlayTimeContainer, resetStatsButton);
        return statsView;
    }

    /**
     * Creates the Pet Revival section of the parental controls, allowing the user to revive pets.
     *
     * @return the VBox containing the Pet Revival section
     */
    public VBox createPetRevivalView() {
        // Outer container
        VBox petRevivalView = new VBox(30);
        petRevivalView.setPadding(new Insets(30));
        petRevivalView.setAlignment(Pos.TOP_CENTER);

        // Pet Status Label
        Label petStatusLabel = new Label("All pets are healthy");
        petStatusLabel.setStyle("-fx-font-size: 18; -fx-text-fill: black;");

        // Pet List Container (dynamic content based on dead pets)
        VBox petList = new VBox(20);
        petList.setAlignment(Pos.CENTER);

        // Array of save files
        String[] saveFiles = { "save1.JSON", "save2.JSON", "save3.JSON" };

        // Loop through each save file and create a corresponding row
        for (String saveFile : saveFiles) {
            HBox saveContainer = new HBox(15);
            saveContainer.setPadding(new Insets(10));
            saveContainer.setAlignment(Pos.CENTER_LEFT);
            saveContainer.setStyle(
                    "-fx-background-color: #f0f0f0; -fx-border-radius: 30; -fx-background-radius: 30;");

            // Save Slot Label
            Label saveSlotLabel = new Label(saveFile.replace(".JSON", "").toUpperCase());
            saveSlotLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: black;");

            // Health Status Label
            Label healthStatusLabel = new Label();
            healthStatusLabel.setStyle("-fx-font-size: 14; -fx-text-fill: black;");

            // Pet Name Label
            Label petNameLabel = new Label();
            petNameLabel.setStyle("-fx-font-size: 14; -fx-text-fill: black;");

            // Heart Button for revival
            Label reviveHeart = new Label("\u2764");
            reviveHeart.setFont(Font.font("Arial", 28));
            reviveHeart.setStyle("-fx-text-fill: red; -fx-cursor: hand;");
            reviveHeart.setVisible(false);

            // Check the health status from the JSON file
            try {
                File file = new File("saves/" + saveFile);
                System.out.println("Checking file: " + file.getAbsolutePath());
                if (file.exists()) {
                    String json = new String(Files.readAllBytes(Paths.get(file.getPath())));
                    JSONObject jsonObject = new JSONObject(json);
                    JSONObject player = jsonObject.getJSONObject("player");
                    JSONObject currentPet = player.getJSONObject("currentPet");

                    int health = currentPet.getInt("health");
                    String petName = currentPet.getString("name");

                    petNameLabel.setText("Pet: " + petName);

                    if (health > 0) {
                        healthStatusLabel.setText("Your pet is healthy");
                    } else {
                        healthStatusLabel.setText("Dead pet");
                        reviveHeart.setVisible(true); // Show heart button for dead pets
                    }

                    // Revive action
                    reviveHeart.setOnMouseClicked(e -> {
                        revivePet(file);
                        healthStatusLabel.setText("Your pet is healthy");
                        reviveHeart.setVisible(false); // Hide the revive button
                    });
                } else {
                    healthStatusLabel.setText("Empty Slot");
                    petNameLabel.setText("Pet: unnamed");
                }
            } catch (Exception e) {
                healthStatusLabel.setText("Empty Slot");
                petNameLabel.setText("Pet: unnamed");
                System.err.println("Error reading save file " + saveFile + ": " + e.getMessage());
            }

            // Add components to the save container
            saveContainer.getChildren().addAll(saveSlotLabel, petNameLabel, healthStatusLabel, reviveHeart);

            // Add the save container to the list
            petList.getChildren().add(saveContainer);
        }

        // Add components to the pet revival view
        petRevivalView.getChildren().addAll(petStatusLabel, petList);
        return petRevivalView;
    }

    /**
     * Revives a pet by updating its attributes in the specified save file.
     *
     * @param file the save file containing the pet data
     */

    private void revivePet(File file) {
        try {
            String json = new String(Files.readAllBytes(file.toPath()));
            JSONObject jsonObject = new JSONObject(json);
            JSONObject player = jsonObject.getJSONObject("player");
            JSONObject currentPet = player.getJSONObject("currentPet");

            if (currentPet != null) {
                currentPet.put("health", 100);
                currentPet.put("sleep", 100);
                currentPet.put("fullness", 100);
                currentPet.put("happiness", 100);
                Files.write(file.toPath(), jsonObject.toString(4).getBytes());
                System.out.println("Revived pet in " + file.getName());
            }
        } catch (Exception e) {
            System.err.println("Failed to revive pet in " + file.getName() + ": " + e.getMessage());
        }
    }

    
    /**
     * Retrieves the Scene object for this screen.
     *
     * @return the Scene object
     */

    public Scene getScene() {
        return scene;
    }
}
