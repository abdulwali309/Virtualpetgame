package application.model;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;


/**
 * The Parent class manages parental control items, which includes: password verification,
 * allowed hours for gameplay, whether the parental controls are enabled, minutes playes, and average minutes playes per session. It uses Gson for JSON serialization and
 * Uses JSON serialization and deserialization to ensure data persistence across sessions.
 *
 * <p>
 * Synchronizes methods that modify shared data, due to very unlikley race condition between thread incrementation 
 * and saving parental control settings.
 * If JSON is missing or malformed it will make default JSON.
 * </p>
 *
 * @author Ryan Wagner
 */
public class Parent{

    private String password;

    private List<Integer> allowedHours;

    private boolean enabled;

    private int totalTimePlayed; 

    private int numberOfLaunches; 

    //JSON file that will be used - stored at same level as build
    private static final String FILE = "parentdata.json";

    //pretty printing on GSON object so you dont have to scroll when adding large amount of times (improved readability)
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Constructor that initializes the Parent object by loading data from the JSON file.
     * (If file doesn't exist it will make default JSON)
     */
    public Parent(){
        loadParentData();
    }

    /**
     * Loads parent data from the JSON file. If the file does not exist or cannot be read,
     * it initializes default values and saves them to the file.
     */
    public synchronized void loadParentData(){

        try(FileReader reader = new FileReader(FILE)){
            ParentData data = GSON.fromJson(reader, ParentData.class);

            this.password = data.getPassword();
            this.allowedHours = data.getAllowedHours();
            this.enabled = data.isEnabled();
            this.totalTimePlayed = data.getTotalTimePlayed();
            this.numberOfLaunches = data.getNumberOfLaunches();

            //validating the essential fields except isEnabled as boolean wrapper causes errors
            if(this.password == null || this.allowedHours == null){
                throw new JsonSyntaxException("Missing required fields in JSON.");
            }
        }
        catch(Exception e){
            System.err.println("Error occured when loading Parent JSON: " + e.getMessage());
            setDefaultValues();
        }
    }

    /**
     * Sets default values and saves them to the JSON file in case of error with JSON.
     */
    public synchronized void setDefaultValues(){
        this.password = "password";
        this.allowedHours = new ArrayList<>();
        for(int i = 0; i < 24; i++){
            this.allowedHours.add(i);
        }
        this.enabled = true;
        this.totalTimePlayed = 0;
        this.numberOfLaunches = 0;
        saveParentData();
    }

    /**
     * Saves the current parent data to the JSON file using GSON object.
     */
    public synchronized void saveParentData(){
        try(FileWriter writer = new FileWriter(FILE)){
            ParentData data = new ParentData();

            data.setPassword(this.password);
            data.setAllowedHours(this.allowedHours);
            data.setEnabled(this.enabled);
            data.setTotalTimePlayed(this.totalTimePlayed);
            data.setNumberOfLaunches(this.numberOfLaunches);

            GSON.toJson(data, writer);
            
        }catch(Exception e){
            System.err.println("Error occured when trying to save data to parent JSON: " + e.getMessage());
        }
    }

    /**
     * Verifies if the input password matches the stored password.
     *
     * @param inputPassword The password inputed by the user.
     * @return True if the password matches, false otherwise.
     */
    public boolean verifyPassword(String inputPassword){
        return this.password.equals(inputPassword);
    }

    /**
     * Sets the allowed gameplay hours and saves the updated settings.
     *
     * @param allowedHours The list of allowed hours (0-23).
     */
    public void setAllowedHours(List<Integer> allowedHours){
        this.allowedHours = allowedHours;
        saveParentData();
    }

    /**
     * Retrieves the allowed hours for gameplay.
     *
     * @return The list of allowed hours (0-23).
     */
    public List<Integer> getAllowedHours(){
        return this.allowedHours;
    }

    /**
     * Sets whether parental controls are enabled and saves the updated settings.
     *
     * @param enabled True to enable parental controls, false to disable.
     */
    public void setEnabled(boolean enabled){
        this.enabled = enabled;
        saveParentData();
    }

    /**
     * Checks whether parental controls are enabled.
     *
     * @return True if parental controls are enabled, false otherwise.
     */
    public boolean isEnabled(){
        return this.enabled;
    }

    /**
     * Checks if the current time (local machine time) is within the allowed gameplay hours.
     *
     * @return True if within allowed hours, false otherwise.
     */
    public boolean isWithinAllowedTime(){
        if (!this.enabled) {
            return true; //If parental controls are disabled, always allow
        }
        LocalTime currentTime = LocalTime.now();
        int currentHour = currentTime.getHour();

        return this.allowedHours.contains(currentHour);
    }


    /**
     * Increments the number of launches via loading and saving to JSON.
     */
    public synchronized void incrementNumberOfLaunches(){
        loadParentData(); 
        this.numberOfLaunches++;
        saveParentData(); 
    }


    /**
     * Increments the total time played via loading and saving to JSON.
     */
    public synchronized void incrementTotalTimePlayed(){
        loadParentData(); 
        this.totalTimePlayed++;
        saveParentData(); 
    }


    /**
     * Retrieves the number of times the application has been launched.
     *
     * @return The number of launches.
     */
    public synchronized int getNumberOfLaunches(){
        //makes sure to have latest value
        loadParentData(); 
        return this.numberOfLaunches;
    }


    /**
     * Retrieves the total time played in minutes.
     *
     * @return The total time played.
     */
    public synchronized int getTotalTimePlayed(){
        //makes sure to have latest value
        loadParentData(); 
        return this.totalTimePlayed;
    }

    /**
     * Resets usage statistics by setting launches to one and total time played to zero.
     */
    public synchronized void resetStats(){
        
        //num of launches changed to 1 not 0 as must be reset while running
        this.numberOfLaunches = 1;
        this.totalTimePlayed = 0;
        saveParentData();
    }


}