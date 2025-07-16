package application.model;

import java.util.ArrayList; // Import for handling an empty list of pet states.

public class Ash extends Pet {

    // Default constructor for Ash
    public Ash() {
        super(
                "Ash", // Name of the pet
                100, // Initial health
                100, // Initial sleep level
                100, // Initial fullness
                100, // Initial happiness
                new ArrayList<>(), // Initial states (empty list since the pet starts as normal)
                3);
    }

    @Override
    public void adjustStats() {
        this.setSleep(this.getSleep() - 1);
        this.setFullness(this.getFullness() - 2);
        this.setHappiness(this.getHappiness() - 6);

        this.setSleep(Math.max(this.getSleep(), 0));
        this.setFullness(Math.max(this.getFullness(), 0));
        this.setHappiness(Math.max(this.getHappiness(), 0));

        this.checkAndAddState();
    }
}
