package application.model;

import java.util.ArrayList; // Import for handling an empty list of pet states.

public class Gunchi extends Pet {

    // Default constructor for Ash
    public Gunchi() {
        super(
                "Gunchi", // Name of the pet
                100, // Initial health
                100, // Initial sleep level
                100, // Initial fullness
                100, // Initial happiness
                new ArrayList<>(), // Initial states (empty list since the pet starts as normal)
                2);
    }

    @Override
    public void adjustStats() {
        this.setSleep(this.getSleep() - 2);
        this.setFullness(this.getFullness() - 4);
        this.setHappiness(this.getHappiness() - 3);

        this.setSleep(Math.max(this.getSleep(), 0));
        this.setFullness(Math.max(this.getFullness(), 0));
        this.setHappiness(Math.max(this.getHappiness(), 0));

        this.checkAndAddState();
    }
}
