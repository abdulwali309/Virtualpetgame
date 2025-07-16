package application.model;

public class GameState {
    private Player player; // Player details
    private int saveSlot; // The save slot 

    public GameState(Player player, int saveSlot){
        this.player = player;
        this.saveSlot = saveSlot;
    }

    //Getter Method
    public Player getPlayer(){
        return player;
    }
    //Getter Method
    public int getSaveSlot() {
        return saveSlot;
    }
    //Setter method
    public void setPlayer(Player player) {
        this.player = player;
    }
    //Setter method
    public void setSaveSlot(int saveSlot) {
        this.saveSlot = saveSlot;
        
    }

}