package application.components;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import application.controllers.FeedbackController;
import application.controllers.GameplayController;
import javafx.application.Platform;
import javafx.geometry.Insets;

public class StatModal extends StackPane {

    private Runnable onCloseAction; // Action to perform when "Close" is clicked
    private Text modalTitle;
    private VBox buttonContainer;
    private GameplayController gameplayController;
    private InventoryModal inventoryModal;
    private FeedbackController feedbackController;

    public StatModal(GameplayController gameplayController, InventoryModal inventoryModal,
            FeedbackController feedbackController) {
        this.gameplayController = gameplayController;
        this.inventoryModal = inventoryModal;
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

        modalTitle = new Text("Stat Details");
        modalTitle.setFont(Font.font("Arial", 24));
        modalTitle.setStyle("-fx-font-weight: bold;");

        buttonContainer = new VBox(10);
        buttonContainer.setAlignment(Pos.CENTER);

        Button closeButton = new Button("Close");
        closeButton.setStyle(
                "-fx-background-color: red; " +
                        "-fx-text-fill: #ffffff; " +
                        "-fx-background-radius: 15; " +
                        "-fx-padding: 10 20;");
        closeButton.setOnAction(e -> {
            // feedbackController.playSoundEffect("buttonSelect");
            this.setVisible(false);
            if (onCloseAction != null)
                onCloseAction.run();
        });

        modalContent.getChildren().addAll(modalTitle, buttonContainer, closeButton);
        this.getChildren().add(modalContent);
    }

    public void setOnCloseAction(Runnable action) {
        this.onCloseAction = action;
    }

    private void refreshStatBars() {
        System.out.println("Stats updated: Hunger=" + gameplayController.getPetHunger() +
                ", Energy=" + gameplayController.getPetSleep() +
                ", Happiness=" + gameplayController.getPetHappiness() +
                ", Health=" + gameplayController.getPetHealth());
    }

    public void setTitle(String title) {
        modalTitle.setText(title);
        populateButtons(title);
    }

    private void disableButtonsForState(String statName) {
        String petState = gameplayController.getMainPetState();

        // Disable all buttons initially
        buttonContainer.getChildren().forEach(node -> {
            if (node instanceof Button button) {
                button.setDisable(true);
            }
        });

        // Enable buttons based on the pet's state
        switch (petState) {
            case "dead":
                System.out.println("Pet is dead: All commands disabled.");
                break;

            case "sleeping":
                System.out.println("Pet is sleeping: Only 'Go to Sleep' button is enabled.");
                if (statName.equals("Sleep")) {
                    buttonContainer.getChildren().forEach(node -> {
                        if (node instanceof Button button) {
                            button.setDisable(false); // Only enable Sleep buttons
                        }
                    });
                }
                break;

            case "angry":
                System.out.println("Pet is angry: Only 'Happiness' commands are enabled.");
                if (statName.equals("Happiness")) {
                    buttonContainer.getChildren().forEach(node -> {
                        if (node instanceof Button button) {
                            button.setDisable(false); // Only enable Happiness buttons
                        }
                    });
                }
                break;

            case "hungry":
            case "normal":
                System.out.println("Pet is in " + petState + " state: All commands enabled.");
                buttonContainer.getChildren().forEach(node -> {
                    if (node instanceof Button button) {
                        button.setDisable(false); // Enable all buttons
                    }
                });
                break;

            default:
                System.out.println("Unknown pet state: " + petState);
        }
    }

    private void populateButtons(String statName) {
        buttonContainer.getChildren().clear();

        switch (statName) {
            case "Hunger":
                addButton("Meat +15", () -> {
                    feedbackController.playSoundEffect("giftEffect");
                    gameplayController.feedPet("meat");
                    refreshStatBars();
                    gameplayController.notifyInventoryUpdated();
                    inventoryModal.refreshAllInventoryPages();

                });
                addButton("Fruit +10", () -> {
                    gameplayController.feedPet("fruit");
                    refreshStatBars();
                    gameplayController.notifyInventoryUpdated();
                    inventoryModal.refreshAllInventoryPages();
                });
                addButton("Vegetable +5", () -> {
                    gameplayController.feedPet("vegetable");
                    refreshStatBars();
                    gameplayController.notifyInventoryUpdated();
                    inventoryModal.refreshAllInventoryPages();

                });
                break;

            case "Sleep":
                addButton("Go to Sleep = 100", () -> {
                    gameplayController.goToSleep();
                    refreshStatBars();
                });
                break;

            case "Happiness":
                addButton("Play +15", () -> {
                    gameplayController.playWithPet();
                    refreshStatBars();
                });
                addButton("Play Place +15", () -> {
                    gameplayController.giftPet("play place");
                    refreshStatBars();
                    gameplayController.notifyInventoryUpdated();
                });
                addButton("Ball +10", () -> {
                    gameplayController.giftPet("ball");
                    refreshStatBars();
                    gameplayController.notifyInventoryUpdated();
                });
                addButton("Toy +5", () -> {
                    gameplayController.giftPet("toy");
                    refreshStatBars();
                    gameplayController.notifyInventoryUpdated();
                });
                break;

            case "Health":
                addButton("Take to Vet", () -> {
                    gameplayController.takeToVet();
                    refreshStatBars();
                });
                addButton("Exercise +5", () -> {
                    gameplayController.exercisePet();
                    refreshStatBars();
                });
                break;

            default:
                System.out.println("Unknown stat: " + statName);
        }

        disableButtonsForState(statName); // Update button states after populating
    }

    private void addButton(String label, Runnable action) {
        Button button = new Button(label);
        button.setStyle(
                "-fx-background-color: #638EFB; " +
                        "-fx-text-fill: #ffffff; " +
                        "-fx-background-radius: 15; " +
                        "-fx-padding: 10 20;");
        button.setOnAction(e -> {

            // Disable the button if it's one of the targeted buttons
            if (label.equals("Play +15") || label.equals("Take to Vet")) {
                button.setDisable(true); // Disable the button
                action.run(); // Execute the button's action
                feedbackController.playSoundEffect("reward3");

                // Start a thread to re enable the button after 10 seconds
                new Thread(() -> {
                    try {
                        Thread.sleep(10000); // Wait for 10 seconds
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    Platform.runLater(() -> button.setDisable(false)); // Re enable the button
                }).start();
            } else {
                // Execute action for other buttons without disabling
                action.run();
                feedbackController.playSoundEffect("reward3");
            }
        });

        buttonContainer.getChildren().add(button);
    }

    public void show() {
        this.setVisible(true);
    }

    public void hide() {
        this.setVisible(false);
    }
}
