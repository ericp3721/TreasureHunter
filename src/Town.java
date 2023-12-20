/**
 * The Town Class is where it all happens.
 * The Town is designed to manage all the things a Hunter can do in town.
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class Town {
    // instance variables
    private Hunter hunter;
    private Shop shop;
    private Terrain terrain;
    private String printMessage;
    private String treasure;
    private boolean toughTown;
    private boolean searched;

    /**
     * The Town Constructor takes in a shop and the surrounding terrain, but leaves the hunter as null until one arrives.
     *
     * @param shop The town's shoppe.
     * @param toughness The surrounding terrain.
     */
    public Town(Shop shop, double toughness) {
        this.shop = shop;
        this.terrain = getNewTerrain();

        // all treasures have the same chance of being selected
        double random = Math.random();
        if (random < .25) {
            treasure = "a crown";
        } else if (random < .50) {
            treasure = "a trophy";
        } else if (random < .75) {
            treasure = "a gem";
        } else {
            treasure = "dust";
        }

        searched = false;
        // the hunter gets set using the hunterArrives method, which
        // gets called from a client class
        hunter = null;

        printMessage = "";

        // higher toughness = more likely to be a tough town
        toughTown = (Math.random() < toughness);
    }

    public String getLatestNews() {
        return printMessage;
    }

    /**
     * Assigns an object to the Hunter in town.
     *
     * @param hunter The arriving Hunter.
     */
    public void hunterArrives(Hunter hunter) {
        this.hunter = hunter;
        printMessage = "Welcome to town, " + hunter.getHunterName() + ".";

        if (toughTown) {
            printMessage += "\nIt's pretty rough around here, so watch yourself.";
        } else {
            printMessage += "\nWe're just a sleepy little town with mild mannered folk.";
        }
    }

    /**
     * Handles the action of the Hunter leaving the town.
     *
     * @return true if the Hunter was able to leave town.
     */
    public boolean leaveTown() {
        boolean canLeaveTown = terrain.canCrossTerrain(hunter);
        if (canLeaveTown) {
            String item = terrain.getNeededItem();
            printMessage = "You used your " + item + " to cross the " + terrain.getTerrainName() + ".";
            if (checkItemBreak()) {
                hunter.removeItemFromKit(item);
                printMessage += "\nUnfortunately, your " + item + " broke.";
            }

            return true;
        }

        printMessage = "You can't leave town, " + hunter.getHunterName() + ". You don't have a " + terrain.getNeededItem() + ".";
        return false;
    }

    /**
     * Handles calling the enter method on shop whenever the user wants to access the shop.
     *
     * @param choice If the user wants to buy or sell items at the shop.
     */
    public void enterShop(String choice) {
        shop.enter(hunter, choice);
    }

    /**
     * Gives the hunter a chance to fight for some gold.<p>
     * The chances of finding a fight and winning the gold are based on the toughness of the town.<p>
     * The tougher the town, the easier it is to find a fight, and the harder it is to win one.
     */
    public void lookForTrouble() {
        double noTroubleChance;
        if (toughTown) {
            noTroubleChance = 0.66;
        } else {
            noTroubleChance = 0.33;
        }

        if (Math.random() > noTroubleChance) {
            printMessage = "You couldn't find any trouble";
        } else {
            printMessage = Colors.RED + "You want trouble, stranger!  You got it!\nOof! Umph! Ow!\n";
            int goldDiff = (int) (Math.random() * 10) + 1;
            if (Math.random() > noTroubleChance) {
                printMessage += "Okay, stranger! You proved yer mettle. Here, take my gold.";
                printMessage += "\nYou won the brawl and receive " + Colors.YELLOW + goldDiff + Colors.RED + " gold." + Colors.RESET;
                hunter.changeGold(goldDiff);
            } else {
                printMessage += "That'll teach you to go lookin' fer trouble in MY town! Now pay up!";
                printMessage += "\nYou lost the brawl and pay " + goldDiff + " gold." + Colors.RESET;
                hunter.changeGold(-goldDiff);
                if (hunter.getHunterGold() < 0) {
                    printMessage += "\nYou didn't have enough gold to pay! Game Over!";
                }
            }
        }
    }

    public void searchForTreasure() {
        if (treasure.equals("dust")) {
            System.out.println("You couldn't find any treasure");
            searched = true;
        } else if (searched) {
            System.out.println("You have already searched this town");
        } else if (hunter.hasTreasure(treasure)) {
            System.out.println("You already have " + treasure + ".");
            searched = true;
        } else {
            System.out.println("You found " + treasure + "!");
            hunter.addTreasure(treasure);
            searched = true;
            if (hunter.foundAll()) {
                TreasureHunter.setAllTreasuresFound(true);
            }
        }
    }

    public String toString() {
        return "This nice little town is surrounded by " + Colors.CYAN + terrain.getTerrainName() + Colors.RESET + ".";
    }

    /**
     * Determines the surrounding terrain for a town, and the item needed in order to cross that terrain.
     *
     * @return A Terrain object.
     */
    private Terrain getNewTerrain() {
        double rnd = Math.random();
        if (rnd < 0.166) {
            return new Terrain("Mountains", "Rope");
        } else if (rnd < 0.332) {
            return new Terrain("Ocean", "Boat");
        } else if (rnd < .499) {
            return new Terrain("Plains", "Horse");
        } else if (rnd < .666) {
            return new Terrain("Desert", "Water");
        } else if (rnd < 0.833) {
            return new Terrain("Marsh", "Boots");
        } else {
            return new Terrain("Jungle", "Machete");
        }
    }

    /**
     * Determines whether a used item has broken.
     *
     * @return true if the item broke.
     */
    private boolean checkItemBreak() {
        double rand = Math.random();
        return (rand < 0.5);
    }
}