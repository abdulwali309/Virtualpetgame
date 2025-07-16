package application.model;

import java.util.List;

/**
 * Data Transfer Object (DTO) for Parent JSON data.
 *
 * <p>
 * This class represents the structure of the `parent_data.json` file.
 * It uses GSON annotations to allow for JSON serialization and deserialization of the password,
 * which hours are allowed, whether parental control is enabled, the total time played,
 * and the number of program launches.
 * </p>
 *
 * @author Ryan Wagner
 */

public class ParentData{

    private String password; //the password as a string

    private List<Integer> allowedHours; //list of hrs from 0 to 23

    private boolean enabled; //stores whether the parental control screen is enabled

    private int totalTimePlayed; //total time played in minutes

    private int numberOfLaunches; //stores number of times the application is launched (closer to an intermediary)


    /**
     * Gets the password required for parental control access.
     *
     * @return the password as a string.
     */
    public String getPassword(){
        return password;
    }

    /**
     * Sets the password required for parental control access.
     *
     * <p>
     * The method is primarily used for setting a default password in case of errors 
     * during initialization.
     * </p>
     *
     * @param password the password to set as a string.
     */
    public void setPassword(String password){
        this.password = password;
    }

    /**
     * Gets the list of allowed hours to be able to access the gameplay feature of the application.
     *
     * @return a list of ints that can be from 0 - 23
     */
    public List<Integer> getAllowedHours(){
        return allowedHours;
    }

    /**
     * Sets the list of allowed hours to be able to access the gameplay feature of the application.
     *
     * @param allowedHours a list of ints that can be from 0 - 23
     */
    public void setAllowedHours(List<Integer> allowedHours){
        this.allowedHours = allowedHours;
    }

    /**
     * Checks whether parental controls are enabled.
     *
     * @return true if parental controls are enabled, returns false otherwise.
     */
    public boolean isEnabled(){
        return enabled;
    }

    /**
     * Sets whether parental controls are enabled.
     *
     * @param enabled true to enable parental controls, false if they are disabled
     */
    public void setEnabled(boolean enabled){
        this.enabled = enabled;
    }

    /**
     * Gets the total time played.
     *
     * @return the total time played in minutes as an int.
     */
    public int getTotalTimePlayed(){
        return totalTimePlayed;
    }

    /**
     * Sets the total time played.
     *
     * @param totalTimePlayed the total time played in minutes as an int.
     */
    public void setTotalTimePlayed(int totalTimePlayed){
        this.totalTimePlayed = totalTimePlayed;
    }

    /**
     * Gets the number of times the application has been launched.
     *
     * @return the number of launches as an int.
     */
    public int getNumberOfLaunches(){
        return numberOfLaunches;
    }

    /**
     * Sets the number of times the application has been launched.
     *
     * @param numberOfLaunches the number of launches as an int.
     */
    public void setNumberOfLaunches(int numberOfLaunches){
        this.numberOfLaunches = numberOfLaunches;
    }
}
