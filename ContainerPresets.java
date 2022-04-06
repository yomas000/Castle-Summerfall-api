import java.util.ArrayList;
import java.util.Random;

public class ContainerPresets {
    public static Container Random(int valueFactor){
        Random rand = new Random();
        switch (rand.nextInt(1)) {
            case 0:
                return chest(valueFactor);
            default:
                return null;
        }
    }
    

    private static String[] chestDescriptions = {
        "The chest is old, nearly to the point of rotting.",
        "The chest is oddly clean, out of place in this dark and dingy dungeon.", 
        "It's a chest."
    };

    public static Container chest(int valueFactor){
        Random rand = new Random();
        ArrayList<Interactable> chestInventory = getInventory(0,3, valueFactor);
        String description = chestDescriptions[rand.nextInt(chestDescriptions.length)];
        Container result = new Container("Chest", description, 5, 20, false, chestInventory, 7);
        return result;
    }
   
    private static String[] crateDescriptions = {
        "A large wooden crate, the top already pried off, lies before you.",
        "A wooden box. Maybe it has loot inside?"
    };

    private static Container crate(int valueFactor){
        Random rand = new Random();
        ArrayList<Interactable> inventory = getInventory(1, 2, valueFactor);
        String description = crateDescriptions[rand.nextInt(crateDescriptions.length)];
        Container result = new Container("Crate", description, 4, 17, false, inventory, 5);
        return result;
    }

    public static ArrayList<Interactable> getInventory(int rangeStart, int rangeEnd, int valueFactor){
        Random rand = new Random();
        ArrayList<Interactable> inventory = new ArrayList<Interactable>();
        int loopCount = rand.nextInt(rangeEnd-rangeStart)+rangeStart;
        for (int i = 0; i < loopCount; i++) {
            inventory.add(Generator.generateInteractable());
        }
        return inventory;
    }

}
