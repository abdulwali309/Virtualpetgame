package application.view;

import application.GameLauncher;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;


/**
 * The TutorialScreen class represents a screen in the game that provides
 * a tutorial for players. The tutorial consists of a series of slides
 * explaining various aspects of the game, including the menu, parental controls,
 * loading, inventory, and gameplay.
 * 
 * The screen includes a main image area to display slides, a close button,
 * a title, and a slide selector at the bottom to navigate between slides.
 * 
 * @author Abdul-Wali Khan
 */
public class TutorialScreen {

     /** The GameLauncher instance used for transitioning between screens. */
    private GameLauncher gameLauncher;
    
    /** The ImageView for displaying the current tutorial slide. */
    private ImageView tutorialImageView;
    
     /** The container for the slide selector buttons. */
    private HBox slideSelector;

    /** An array of images representing the tutorial slides. */
    private Image[] tutorialSlides = {
            new Image("menu.jpg"),
            new Image("parental.jpg"),
            new Image("loading.jpg"),
            new Image("inventory.jpg"),
            new Image("gameplay.jpg")
    };

    /**
     * Constructs a new TutorialScreen.
     *
     * @param gameLauncher the GameLauncher instance for managing transitions
     */
    public TutorialScreen(GameLauncher gameLauncher) {
        this.gameLauncher = gameLauncher;
    }

    /**
     * Creates and returns the Scene object for the Tutorial screen.
     *
     * @return the Scene object representing this screen
     */
    public Scene getScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #8fcccb;");

        // Main tutorial image in a smaller window
        tutorialImageView = new ImageView(tutorialSlides[0]);
        tutorialImageView.setPreserveRatio(true);
        tutorialImageView.setFitWidth(800);
        tutorialImageView.setFitHeight(600);

        // Wrap the ImageView in a container with fixed size to prevent resizing
        StackPane imageContainer = new StackPane(tutorialImageView);
        imageContainer.setPrefSize(800, 600);
        imageContainer.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        imageContainer.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        imageContainer.setAlignment(Pos.CENTER);
        imageContainer.setPadding(new Insets(0, 0, 0, 0));
        root.setCenter(imageContainer);

        // Overlay with close button and title
        StackPane overlay = createOverlay();
        root.setTop(overlay);

        // Slide selector at the bottom
        slideSelector = createSlideSelector();
        root.setBottom(slideSelector);

        return new Scene(root, 1200, 800);
    }

    /**
     * Creates the overlay containing the close button and title for the screen.
     *
     * @return a StackPane containing the overlay elements
     */
    private StackPane createOverlay() {
        StackPane overlay = new StackPane();
        overlay.setPadding(new Insets(10));

        // "X" Button for closing the screen
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

        StackPane.setAlignment(closeButton, Pos.TOP_LEFT);
        StackPane.setMargin(closeButton, new Insets(10, 0, 0, 10));

        // Title for "Tutorial"
        Text title = new Text("Tutorial");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 50));
        title.setFill(javafx.scene.paint.Color.BLACK);
        StackPane.setAlignment(title, Pos.TOP_CENTER);
        StackPane.setMargin(title, new Insets(10, 0, 0, 0));

        overlay.getChildren().addAll(closeButton, title);

        return overlay;
    }

    /**
     * Creates the slide selector for navigating through the tutorial slides.
     *
     * @return an HBox containing toggle buttons for each slide
     */
    private HBox createSlideSelector() {
        HBox slideSelector = new HBox(10);
        slideSelector.setAlignment(Pos.CENTER);
        slideSelector.setPadding(new Insets(0));
        slideSelector.setStyle("-fx-background-color: #8fcccb;");

        ToggleGroup toggleGroup = new ToggleGroup();

        for (int i = 0; i < tutorialSlides.length; i++) {
            int slideIndex = i;
            ToggleButton slideButton = new ToggleButton(String.valueOf(i + 1));
            slideButton.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            slideButton.setStyle(
                    "-fx-background-color: white; " +
                            "-fx-border-color: #aaaaaa; " +
                            "-fx-border-radius: 10; " +
                            "-fx-text-fill: black;");
            slideButton.setToggleGroup(toggleGroup);

            if (i == 0) {
                slideButton.setSelected(true);
            }

            slideButton.setOnAction(e -> {
                System.out.println("Slide " + (slideIndex + 1) + " selected.");
                updateSlide(slideIndex);
            });

            slideSelector.getChildren().add(slideButton);
        }

        return slideSelector;
    }

    /**
     * Updates the main tutorial image to the specified slide.
     *
     * @param slideIndex the index of the slide to display
     */
    private void updateSlide(int slideIndex) {
        tutorialImageView.setImage(tutorialSlides[slideIndex]);
        tutorialImageView.setFitWidth(800);
        tutorialImageView.setFitHeight(600);
        tutorialImageView.setPreserveRatio(true);
        tutorialImageView.getParent().layout();
        slideSelector.setVisible(true);
    }
}