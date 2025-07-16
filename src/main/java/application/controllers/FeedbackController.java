package application.controllers;

import application.model.Feedback;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FeedbackController {

    private Feedback feedbackModel;
    private MediaPlayer currentMusicPlayer; // For background music
    private Map<String, AudioClip> soundEffects; // For sound effects

    public FeedbackController(Feedback feedbackModel) {
        this.feedbackModel = feedbackModel;
        this.currentMusicPlayer = null;
        this.soundEffects = new HashMap<>();

        // Preload sound effects
        preloadSoundEffects();
    }

    // Preload sound effects into map
    public void preloadSoundEffects() {
        soundEffects.put("buttonSelect", loadAudioClip("src/main/resources/sound effects/button-select.wav"));
        soundEffects.put("giftEffect", loadAudioClip("src/main/resources/sound effects/gift-effect.wav"));
        soundEffects.put("pauseMenu", loadAudioClip("src/main/resources/sound effects/pause-menu.wav"));
        soundEffects.put("reward1", loadAudioClip("src/main/resources/sound effects/reward.wav"));
        soundEffects.put("reward2", loadAudioClip("src/main/resources/sound effects/reward-2.wav"));
        soundEffects.put("reward3", loadAudioClip("src/main/resources/sound effects/reward-3.wav"));
        soundEffects.put("warning", loadAudioClip("src/main/resources/sound effects/warning.wav"));
    }

    // Load clip from file path
    private AudioClip loadAudioClip(String path) {
        File file = new File(path);
        if (file.exists()) {
            return new AudioClip(file.toURI().toString());
        } else {
            System.err.println("Audio file not found: " + path);
            return null;
        }
    }

    // Play background music for current view
    public void playBackgroundMusic(String viewName) {
        if (!feedbackModel.isMusicOn()) {
            stopBackgroundMusic();
            return;
        }

        String musicFile = getMusicFileForView(viewName);

        // Start new music
        if (currentMusicPlayer == null) {
            File file = new File("src/main/resources/" + musicFile);
            if (file.exists()) {
                Media media = new Media(file.toURI().toString());
                currentMusicPlayer = new MediaPlayer(media);
                currentMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loop music
                currentMusicPlayer.play();
                System.out.println("Playing background music: " + musicFile);
            } else {
                System.err.println("Music file not found: " + musicFile);
            }
        }
    }

    // Stop background music
    public void stopBackgroundMusic() {
        if (currentMusicPlayer != null) {
            currentMusicPlayer.stop();
            currentMusicPlayer = null;
            System.out.println("Background music stopped.");
        }
    }

    // Get music file path for the given view
    private String getMusicFileForView(String viewName) {
        switch (viewName) {
            case "MainMenu":
                return "music/main-menu-theme.mp3";
            case "Gameplay":
                return "music/gameplay-music.mp3";
            default:
                return "music/gameplay-theme.mp3";
        }
    }

    // Play a sound effect for specific action
    public void playSoundEffect(String action) {
        if (!feedbackModel.isSfxOn()) {
            return; // SFX disabled, do nothing
        }

        AudioClip clip = soundEffects.get(action);
        if (clip != null) {
            clip.play();
            System.out.println("Playing sound effect: " + action);
        } else {
            System.err.println("Sound effect not found for action: " + action);
        }
    }

    // Toggle background music on or off
    public void toggleMusic() {
        if (feedbackModel.isMusicOn()) {
            // Turn off music
            stopBackgroundMusic();
        } else {
            // Turn on music
            if (currentMusicPlayer != null) {
                currentMusicPlayer.play();
                System.out.println("Resuming background music.");
            } else {
                // If no MediaPlayer exists, start playing again with the last view's music
                System.out.println("Starting background music again.");
                playBackgroundMusic("Gameplay"); // Replace with the last known view if needed
            }
        }
        feedbackModel.toggleMusic();
        System.out.println("Music toggled: " + feedbackModel.isMusicOn());
    }

    // Toggle sound effects on or off
    public void toggleSfx() {
        feedbackModel.toggleSfx();
        System.out.println("Sound effects toggled: " + feedbackModel.isSfxOn());
    }

    public boolean isMusicOn() {
        return feedbackModel.isMusicOn();
    }

    public boolean isSfxOn() {
        return feedbackModel.isSfxOn();
    }
}
