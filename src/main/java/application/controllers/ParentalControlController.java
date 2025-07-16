package application.controllers;

import application.model.Parent;
import java.util.List;

/**
 * Controls parental settings by interfacing between the view and the Parent
 * model.
 * Handles interfacing of password verification, management of allowed gameplay
 * hours, and usage stats.
 * 
 * @author Ryan Wagner
 */

public class ParentalControlController {

    private Parent parentModel;

    /**
     * Constructor that initializes the controller with a new Parent model.
     */
    public ParentalControlController() {
        this.parentModel = new Parent();
    }

    /**
     * Verifies if the input password matches the parent's stored password.
     *
     * @param inputPassword The password inputed by the user.
     * @return True if the password matches, false otherwise.
     */
    public boolean verifyPassword(String inputPassword) {
        return parentModel.verifyPassword(inputPassword);
    }

    /**
     * Sets the allowed gameplay hours.
     *
     * @param allowedHours The list of allowed hours (0-23).
     */
    public void setAllowedHours(List<Integer> allowedHours) {
        parentModel.setAllowedHours(allowedHours);
    }

    /**
     * Retrieves the allowed gameplay hours.
     *
     * @return The list of allowed hours (0-23).
     */
    public List<Integer> getAllowedHours() {
        return parentModel.getAllowedHours();
    }

    /**
     * Sets whether parental controls are enabled.
     *
     * @param enabled True to enable parental controls, false to disable.
     */
    public void setEnabled(boolean enabled) {
        parentModel.setEnabled(enabled);
    }

    /**
     * Checks whether parental controls are enabled.
     *
     * @return True if parental controls are enabled, false otherwise.
     */
    public boolean isEnabled() {
        return parentModel.isEnabled();
    }

    /**
     * Checks if the current time is within the allowed gameplay hours.
     *
     * @return True if within allowed hours, false otherwise.
     */
    public boolean isWithinAllowedTime() {
        return parentModel.isWithinAllowedTime();
    }

    /**
     * Increments the number of times the application has been launched.
     */
    public void incrementNumberOfLaunches() {
        parentModel.incrementNumberOfLaunches();
    }

    /**
     * Increments the total time played by one minute.
     */
    public void incrementTotalTimePlayed() {
        parentModel.incrementTotalTimePlayed();
    }

    /**
     * Gets the number of times the application has been launched.
     *
     * @return The launch count.
     */
    public int getNumberOfLaunches() {
        return parentModel.getNumberOfLaunches();
    }

    /**
     * Gets the total time played in minutes.
     *
     * @return The total time played.
     */
    public int getTotalTimePlayed() {
        return parentModel.getTotalTimePlayed();
    }

    /**
     * Resets the usage stats, sets launches to one and total time played to zero.
     */
    public void resetStats() {
        parentModel.resetStats();
    }
}