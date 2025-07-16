package application.controllers;

import application.model.Player;
import application.model.Pet;
import application.model.GameState;
import application.model.Inventory;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class GameplayController {
    private GameState gameState;
    private Player player;
    private Pet pet;
    private Timer statDecayTimer;
    private Runnable onStatsUpdated; // Callback to refresh stats
    private Runnable onInventoryUpdated;
    private Runnable onPetStateUpdated;

    public GameplayController(GameState gameState) {
        this.gameState = gameState;
        this.player = gameState.getPlayer();
        this.pet = player.getCurrentPet();

        if (this.pet != null && this.pet.getGetAllPetStates() == null) {
            System.out.println("Initializing currentPetStates for pet.");
            this.pet.clearPetStates();
        }

        // Debugging check if the pet stats are loaded correctly
        if (pet != null) {
            System.out.println("Pet Stats Loaded: Hunger=" + pet.getFullness() +
                    ", Happiness=" + pet.getHappiness() +
                    ", Sleep=" + pet.getSleep() +
                    ", Health=" + pet.getHealth());
        } else {
            System.err.println("Pet is null in GameplayController");
        }
    }

    public void loadGameState(GameState gameState) {
        this.player = gameState.getPlayer();
        this.pet = player.getCurrentPet();

        System.out.println("Loaded player: " + player.getName());
        System.out.println("Loaded pet: " + (pet != null ? pet.getName() : "No pet assigned"));
    }

    // ----- Gameplay Actions -----

    public void feedPet(String foodItem) {
        try {

            pet.interactPet("feed", foodItem, player.getInventory());
            player.setScore(player.getScore() + 1);

            System.out.println("Fed " + pet.getName() + " with " + foodItem);

            notifyStatsUpdated();
            notifyInventoryUpdated();
        } catch (Exception e) {
            System.out.println("Failed to feed pet: " + e.getMessage());
        }
    }

    public void playWithPet() {
        pet.interactPet("play", "", null);
        player.setScore(player.getScore() + 1);
        System.out.println("Played with " + pet.getName());
        notifyStatsUpdated();
    }

    public void exercisePet() {
        pet.interactPet("exercise", "", null);
        player.setScore(player.getScore() + 1);
        System.out.println("Exercised " + pet.getName());
    }

    public void giftPet(String giftItem) {
        try {

            pet.interactPet("give gift", giftItem, player.getInventory());
            player.setScore(player.getScore() + 1);

            System.out.println("Gifted " + pet.getName() + " with " + giftItem);

            notifyStatsUpdated();
            notifyInventoryUpdated();
        } catch (Exception e) {
            System.out.println("Failed to gift pet: " + e.getMessage());
        }
    }

    // ----- Periodic Pet Updates -----

    public void startStatDecay() {
        if (pet == null) {
            System.err.println("Cannot start stat decay: No pet assigned.");
            return;
        }

        if (statDecayTimer != null) {
            System.out.println("Stat decay already running. Skipping start.");
            return; // Prevent multiple timers from starting
        }

        System.out.println("Starting stat decay...");
        statDecayTimer = new Timer(true); // Daemon timer
        statDecayTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (pet != null) {
                    System.out.println("Before decay: Hunger=" + pet.getFullness() +
                            ", Happiness=" + pet.getHappiness() +
                            ", Sleep=" + pet.getSleep());

                    pet.adjustStats();
                    pet.checkAndAddState();

                    System.out.println("After decay: Hunger=" + pet.getFullness() +
                            ", Happiness=" + pet.getHappiness() +
                            ", Sleep=" + pet.getSleep());
                    notifyStatsUpdated();
                }
            }
        }, 0, 5000); // Update stats every 10 seconds

        System.out.println("Stat decay started.");
    }

    public void stopStatDecay() {
        if (statDecayTimer != null) {
            statDecayTimer.cancel();
            statDecayTimer = null;
            System.out.println("Stat decay timer stopped.");
        }
    }

    // Pet-related methods
    public int getPetHunger() {
        return pet != null ? pet.getFullness() : 0;
    }

    public int getPetHappiness() {
        return pet != null ? pet.getHappiness() : 0;
    }

    public int getPetSleep() {
        return pet != null ? pet.getSleep() : 0;
    }

    public int getPetHealth() {
        return pet != null ? pet.getHealth() : 0;
    }

    public String getPetName() {
        return pet != null ? pet.getName() : "Unnamed";
    }

    public String getMainPetState() {
        return pet != null ? pet.getMainPetState() : "No Pet";
    }

    // Player-related methods
    public String getPlayerName() {
        return player.getName();
    }

    public int getPlayerScore() {
        return player.getScore();
    }

    public int getPetType() {
        return pet != null ? pet.getPetType() : -1;
    }

    public void stopGameplay() {
        stopStatDecay(); // Stop periodic stat updates
        System.out.println("Gameplay stopped successfully.");
    }

    // Inventory-related methods
    public Map<String, Integer> getInventorySummary() {
        Inventory inventory = player.getInventory();
        Map<String, Integer> summary = new HashMap<>();
        summary.put("Food Items", inventory.getFoodItems());
        summary.put("Gift Items", inventory.getGiftItems());
        return summary;
    }

    public Map<String, Integer> getAllPetStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("Hunger", getPetHunger());
        stats.put("Happiness", getPetHappiness());
        stats.put("Sleep", getPetSleep());
        stats.put("Health", getPetHealth());
        return stats;
    }

    public void setOnStatsUpdated(Runnable onStatsUpdated) {
        this.onStatsUpdated = onStatsUpdated;
    }

    public void notifyStatsUpdated() {
        if (onStatsUpdated != null) {
            onStatsUpdated.run();
        }
    }

    public void setOnInventoryUpdated(Runnable onInventoryUpdated) {
        this.onInventoryUpdated = onInventoryUpdated;
    }

    public void notifyInventoryUpdated() {
        if (onInventoryUpdated != null) {
            onInventoryUpdated.run();
        }
    }

    public void setOnPetStateUpdated(Runnable onPetStateUpdated) {
        this.onPetStateUpdated = onPetStateUpdated;
    }

    /*
     * private void notifyPetStateUpdated() {
     * if (onPetStateUpdated != null) {
     * onPetStateUpdated.run();
     * }
     * }
     */

    public void goToSleep() {
        if (pet != null) {
            pet.interactPet("go to bed", "", null);
            System.out.println("Pet is going to sleep.");
            notifyStatsUpdated();
        } else {
            System.err.println("Cannot perform action: No pet assigned.");
        }
    }

    public void takeToVet() {
        if (pet != null) {
            pet.interactPet("take to the vet", "", null);
            System.out.println("Pet is being taken to the vet.");
            notifyStatsUpdated();
        } else {
            System.err.println("Cannot perform action: No pet assigned.");
        }
    }

    public String getPetMainState() {
        return pet != null ? pet.getMainPetState() : "unknown";
    }

}
