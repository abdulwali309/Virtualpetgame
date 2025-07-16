package application.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.kordamp.ikonli.javafx.FontIcon;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import application.controllers.FeedbackController;
import application.model.GameState;

public class InventoryModal extends StackPane {

    private VBox modalContent;
    private StackPane closeButtonContainer;
    private VBox inventoryPage;
    private VBox rewardPage;
    private GameState gameState;
    private FeedbackController feedbackController;

    private HashMap<String, Integer> clickCounts;
    private HashMap<String, Timer> timers;

    public InventoryModal(GameState gameState, FeedbackController feedbackController) {
        this.gameState = gameState;
        this.feedbackController = feedbackController;
        this.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
        this.setAlignment(Pos.CENTER);
        this.setVisible(false);

        // Initialize click counts and timers
        clickCounts = new HashMap<>();
        timers = new HashMap<>();

        // Main content container with fixed size
        modalContent = new VBox(20);
        modalContent.setAlignment(Pos.TOP_CENTER);
        modalContent.setStyle(
                "-fx-background-color: #ffffff; " +
                        "-fx-border-radius: 20; " +
                        "-fx-background-radius: 20; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 20, 0, 0, 10);");
        modalContent.setPadding(new Insets(20));
        modalContent.setPrefSize(600, 700); // Set preferred size
        modalContent.setMaxSize(600, 700); // Enforce maximum size
        modalContent.setMinSize(600, 700); // Enforce minimum size

        // Close Button Container
        closeButtonContainer = new StackPane();
        closeButtonContainer.setAlignment(Pos.TOP_LEFT);
        closeButtonContainer.setPadding(new Insets(10));
        setCloseIcon(); // Initially show 'X' icon

        // Create pages
        inventoryPage = createInventoryPage();
        rewardPage = createRewardPage();

        // Start on the first page
        modalContent.getChildren().addAll(closeButtonContainer, inventoryPage);

        this.getChildren().add(modalContent);
    }

    /**
     * Creates the first page (inventory items and add item button).
     */
    private VBox createInventoryPage() {
        VBox page = new VBox(20);
        page.setAlignment(Pos.TOP_CENTER);

        // Title
        Text modalTitle = new Text("Inventory");
        modalTitle.setFont(Font.font("Arial", 24));
        modalTitle.setStyle("-fx-font-weight: bold;");
        VBox.setMargin(modalTitle, new Insets(20, 0, 0, 0));

        // Inventory Items
        HBox inventoryItems = new HBox(40);
        inventoryItems.setAlignment(Pos.CENTER);

        // Food Section
        VBox foodSection = createInventoryItem("fas-drumstick-bite", "Food");
        VBox foodExtras = createExtrasSection(
                new String[] { "fas-carrot", "fas-apple-alt", "fas-bacon" },
                new String[] { "Vegetable", "Fruit", "Meat" },
                new int[] {
                        gameState.getPlayer().getInventory().getFoodItems("Vegetable"),
                        gameState.getPlayer().getInventory().getFoodItems("Fruit"),
                        gameState.getPlayer().getInventory().getFoodItems("Meat")
                });

        // Gift Section
        VBox giftSection = createInventoryItem("fas-gift", "Gifts");
        VBox giftExtras = createExtrasSection(
                new String[] { "fas-football-ball", "fas-baseball-ball", "fas-home" },
                new String[] { "Toy", "Ball", "Play Place" },
                new int[] {
                        gameState.getPlayer().getInventory().getGiftItems("Toy"),
                        gameState.getPlayer().getInventory().getGiftItems("Ball"),
                        gameState.getPlayer().getInventory().getGiftItems("Play Place")
                });

        // Arrange sections
        VBox foodContainer = new VBox(10, foodSection, foodExtras);
        VBox giftContainer = new VBox(10, giftSection, giftExtras);
        foodContainer.setAlignment(Pos.CENTER);
        giftContainer.setAlignment(Pos.CENTER);

        inventoryItems.getChildren().addAll(foodContainer, giftContainer);

        // Add Item Button
        Button addItemButton = new Button("+ Add Item");
        addItemButton.setStyle(
                "-fx-background-color: #638EFB; " +
                        "-fx-text-fill: #ffffff; " +
                        "-fx-font-size: 14; " +
                        "-fx-background-radius: 15; " +
                        "-fx-padding: 10 20;");
        VBox.setMargin(addItemButton, new Insets(20, 0, 0, 0));
        addItemButton.setOnAction(e -> goToRewardPage()); // Navigate to reward page
        feedbackController.playSoundEffect("buttonSelect");

        page.getChildren().addAll(modalTitle, inventoryItems, addItemButton);
        return page;
    }

    private void handleRewardButtonClick(String item) {
        final int MAX_CLICKS = 5; // Number of clicks needed for a reward
        final long TIME_LIMIT_MS = 5000; // 5 seconds

        // Initialize click count if it doesn't exist
        clickCounts.putIfAbsent(item, 0);

        // Cancel any existing timer for the item
        if (timers.containsKey(item)) {
            timers.get(item).cancel();
            timers.remove(item);
        }

        // Increment the click count
        int currentCount = clickCounts.get(item) + 1;
        clickCounts.put(item, currentCount);

        // Check if the count meets or exceeds the threshold
        if (currentCount >= MAX_CLICKS) {
            // Calculate rewards based on multiples of MAX_CLICKS
            int rewardsGranted = currentCount / MAX_CLICKS; // Full rewards
            int remainingClicks = currentCount % MAX_CLICKS; // Extra clicks to carry over

            // Grant reward(s)
            System.out.println("Rewarded " + rewardsGranted + " " + item + (rewardsGranted > 1 ? "s" : "") + ".");

            // Add the rewarded items to the inventory
            try {
                gameState.getPlayer().getInventory().addItem(item.toLowerCase(), rewardsGranted);
            } catch (Exception e) {
                System.err.println("Failed to add reward to inventory: " + e.getMessage());
            }

            // Refresh both pages
            refreshInventoryPage();
            refreshRewardsPage();

            // Reset click count but keep remaining clicks
            clickCounts.put(item, remainingClicks);
        }

        // Start new timer for this item
        Timer timer = new Timer();
        timers.put(item, timer);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Timer expirec reset the click count
                clickCounts.put(item, 0);
                timers.remove(item);
            }
        }, TIME_LIMIT_MS); // Set timer to expire after 5 seconds
    }

    private void refreshRewardsPage() {

        modalContent.getChildren().clear();

        rewardPage = createRewardPage();

        modalContent.getChildren().addAll(closeButtonContainer, rewardPage);

        System.out.println("Rewards page refreshed with updated inventory.");
    }

    public void refreshInventoryPage() {

        inventoryPage = createInventoryPage();

        // Check if the current visible page is the inventory page
        if (modalContent.getChildren().contains(inventoryPage)) {
            modalContent.getChildren().clear();
            modalContent.getChildren().addAll(closeButtonContainer, inventoryPage);
        } else {
            System.out.println("Inventory page is not visible; updated but not displayed.");
        }

        System.out.println("Inventory page refreshed with updated inventory.");
    }

    public void refreshAllInventoryPages() {
        refreshInventoryPage();
        refreshRewardsPage();
    }

    /**
     * Creates second page (reward challenge).
     */
    private VBox createRewardPage() {
        VBox page = new VBox(20);
        page.setAlignment(Pos.TOP_CENTER);

        // Title
        Text rewardTitle = new Text("Click an item 5 times in 5 seconds for a reward");
        rewardTitle.setFont(Font.font("Arial", 18));
        rewardTitle.setStyle("-fx-font-weight: bold; -fx-fill: #555555;");
        VBox.setMargin(rewardTitle, new Insets(20, 0, 0, 0));

        // Reward Items
        HBox rewardItems = new HBox(40);
        rewardItems.setAlignment(Pos.CENTER);

        // Food Quantities
        int[] foodQuantities = {
                gameState.getPlayer().getInventory().getFoodItems("Vegetable"),
                gameState.getPlayer().getInventory().getFoodItems("Fruit"),
                gameState.getPlayer().getInventory().getFoodItems("Meat")
        };

        // Gift Quantities
        int[] giftQuantities = {
                gameState.getPlayer().getInventory().getGiftItems("Toy"),
                gameState.getPlayer().getInventory().getGiftItems("Ball"),
                gameState.getPlayer().getInventory().getGiftItems("Play Place")
        };

        // Food Buttons
        VBox foodButtons = createExtrasSection(
                new String[] { "fas-carrot", "fas-apple-alt", "fas-bacon" },
                new String[] { "Vegetable", "Fruit", "Meat" },
                foodQuantities);

        // Gift Buttons
        VBox giftButtons = createExtrasSection(
                new String[] { "fas-football-ball", "fas-baseball-ball", "fas-home" },
                new String[] { "Toy", "Ball", "Play Place" },
                giftQuantities);

        rewardItems.getChildren().addAll(foodButtons, giftButtons);

        page.getChildren().addAll(rewardTitle, rewardItems);
        return page;
    }

    /**
     * Creates an inventory item
     */
    private VBox createInventoryItem(String iconLiteral, String labelText) {
        VBox itemContainer = new VBox(10);
        itemContainer.setAlignment(Pos.CENTER);

        // Icon Circle
        StackPane iconContainer = new StackPane();
        Circle iconBackground = new Circle(50, Color.WHITE);
        iconBackground.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 2);");
        FontIcon itemIcon = new FontIcon(iconLiteral);
        itemIcon.setIconSize(24);
        itemIcon.setIconColor(Color.BLACK);
        iconContainer.getChildren().addAll(iconBackground, itemIcon);

        // Label Text
        Text itemLabel = new Text(labelText);
        itemLabel.setFont(Font.font("Arial", 14));
        itemLabel.setStyle("-fx-font-weight: bold; -fx-fill: #555555;");

        itemContainer.getChildren().addAll(iconContainer, itemLabel);
        return itemContainer;
    }

    /**
     * Creates a section for extra items
     */
    private VBox createExtrasSection(String[] iconLiterals, String[] labels, int[] quantities) {
        VBox extrasContainer = new VBox(10);
        extrasContainer.setAlignment(Pos.CENTER);

        for (int i = 0; i < iconLiterals.length; i++) {
            final String currentItem = labels[i]; // Capture the item name

            HBox itemContainer = new HBox(10);
            itemContainer.setAlignment(Pos.CENTER_LEFT);

            // Icon Button
            StackPane iconContainer = new StackPane();
            Circle circle = new Circle(30, Color.WHITE);
            circle.setStroke(Color.BLUE);
            circle.setStrokeWidth(2);
            FontIcon icon = new FontIcon(iconLiterals[i]);
            icon.setIconSize(16);
            icon.setIconColor(Color.BLACK);

            Button button = new Button();
            button.setGraphic(new StackPane(circle, icon));
            button.setStyle("-fx-background-color: transparent;");
            button.setOnAction(e -> handleRewardButtonClick(currentItem)); // Use currentItem
            feedbackController.playSoundEffect("buttonSelect");

            // Label with Quantity
            Text label = new Text(labels[i] + ": " + quantities[i]);
            label.setFont(Font.font("Arial", 12));
            label.setStyle("-fx-font-weight: bold;");

            itemContainer.getChildren().addAll(button, label);
            extrasContainer.getChildren().add(itemContainer);
        }
        return extrasContainer;

    }

    /**
     * Navigates to the first page of the inventory.
     */
    public void goToFirstPage() {
        setCloseIcon(); // Show 'X' close icon
        modalContent.getChildren().clear();
        modalContent.getChildren().addAll(closeButtonContainer, inventoryPage);
    }

    /**
     * Navigates to the reward page.
     */
    private void goToRewardPage() {
        setBackIcon(); // Show back chevron icon
        modalContent.getChildren().clear();
        modalContent.getChildren().addAll(closeButtonContainer, rewardPage);
    }

    /**
     * Sets the 'X' close icon for the first page.
     */
    private void setCloseIcon() {
        closeButtonContainer.getChildren().clear();
        Circle closeCircle = new Circle(15, Color.WHITE);
        closeCircle.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 2);");
        FontIcon closeIcon = new FontIcon("fas-times");
        closeIcon.setIconSize(12);
        closeIcon.setIconColor(Color.BLACK);

        Button closeButton = new Button();
        closeButton.setGraphic(new StackPane(closeCircle, closeIcon));
        closeButton.setStyle("-fx-background-color: transparent;");
        closeButton.setOnAction(e -> this.setVisible(false)); // Close modal
        feedbackController.playSoundEffect("buttonSelect");

        closeButtonContainer.getChildren().add(closeButton);
    }

    /**
     * Sets the left chevron icon for the reward page.
     */
    private void setBackIcon() {
        closeButtonContainer.getChildren().clear();
        Circle backCircle = new Circle(15, Color.WHITE);
        backCircle.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 2);");
        FontIcon chevronIcon = new FontIcon("fas-chevron-left");
        chevronIcon.setIconSize(12);
        chevronIcon.setIconColor(Color.BLACK);

        Button backButton = new Button();
        backButton.setGraphic(new StackPane(backCircle, chevronIcon));
        backButton.setStyle("-fx-background-color: transparent;");
        backButton.setOnAction(e -> goToFirstPage()); // Navigate to first page
        feedbackController.playSoundEffect("buttonSelect");

        closeButtonContainer.getChildren().add(backButton);
    }
}
