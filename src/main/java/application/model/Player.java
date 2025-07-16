package application.model;

/**
 * This class represents the Player.
 * Each player has a specific name, score, current pet and matching inventory.
 * 
 * @author CS2212 Group 31
 */

public class Player {

    private String name;// name of the Player
    private int score;// score of the player
    private Pet currentPet;// current pet of the player
    private Inventory inventory;// inventory of the player

    /**
     * Default constructor creates a player with no name or pet.
     */
    public Player() {
        this.name = "New Player";
        this.score = 0;
        this.currentPet = null; // No pet assigned initially
        this.inventory = new Inventory(); // Initialize an empty inventory
    }

    /**
     * Constructor creates a player with the given name
     * 
     * @param name the name of the player (String)
     */
    public Player(String name, Pet pet) {
        this.name = name;
        this.score = 0;
        this.currentPet = pet;
        this.inventory = new Inventory();
    }

    /**
     * Accessor method to get the players name
     * 
     * @return name (String)
     */
    public String getName() {
        return name;
    }

    /**
     * Accessor method to get the players score
     * 
     * @return score (int)
     */
    public int getScore() {
        return score;
    }

    /**
     * Accessor method to get the current pet
     * 
     * @return currentPet (Pet)
     */
    public Pet getCurrentPet() {
        return currentPet;
    }

    /**
     * Accessor method to get the Inventory
     * 
     * @return inventory (Inventory)
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Mutator method to set the players name
     * 
     * @param name the name of the player (String)
     * @return void
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Mutator method to set the players score
     * 
     * @param score the score of the player (int)
     * @return void
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Mutator method to set the players current pet
     * 
     * @param pet the pet of the player (Pet)
     * @return void
     */
    public void setCurrentPet(Pet pet) {
        this.currentPet = pet;
    }

    /**
     * Mutator method to set the players inventory
     * 
     * @param inventory the inventory of the player (Inventory)
     * @return void
     */
    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

}
