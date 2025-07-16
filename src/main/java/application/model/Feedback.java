package application.model;

/**
 * Feedback model to manage music and sound effects settings.
 */
public class Feedback {

    private boolean isMusicOn;
    private boolean isSfxOn;

    /**
     * Construct the Feedback model with initial settings.
     * 
     * @param isMusicOn initial background music.
     * @param isSfxOn   initial sound effects.
     */
    public Feedback(boolean isMusicOn, boolean isSfxOn) {
        this.isMusicOn = isMusicOn;
        this.isSfxOn = isSfxOn;
    }

    /**
     * Checks if background music is enabled.
     * 
     * @return true if music is on, false otherwise.
     */
    public boolean isMusicOn() {
        return isMusicOn;
    }

    /**
     * Check if sound effects are enabled.
     * 
     * @return true if sound effects are on, false otherwise.
     */
    public boolean isSfxOn() {
        return isSfxOn;
    }

    /**
     * Toggle the state of background music.
     */
    public void toggleMusic() {
        isMusicOn = !isMusicOn;
    }

    /**
     * Toggle the state of sound effects.
     */
    public void toggleSfx() {
        isSfxOn = !isSfxOn;
    }
}
