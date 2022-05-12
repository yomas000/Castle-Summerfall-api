package game;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Random;

public class GameInput {

    static final int FLOORSIZE = 10;
    static Pattern helpPat = Pattern.compile("[Hh]elp ([a-z].*)");
    static Pattern movePat = Pattern.compile("[Mm]ove ([N|n|S|s|W|w|E|e])");
    static Pattern inspectPat = Pattern.compile("[Ii]nspect ([A-Za-z].*)");
    static Pattern takePat = Pattern.compile("[tT]ake ([A-Za-z].*)");
    static Pattern bookPat = Pattern.compile("[Bb]ookmark ([A-Za-z].*?) : ([A-Za-z].*)");
    static Pattern dropPat = Pattern.compile("[Dd]rop ([A-Za-z].*)");
    static Pattern attackPat = Pattern.compile("[Aa]ttack ([A-Za-z].*?) [Ww].* ([A-Za-z].*)");
    static Floor floor = Generator.generateFloor(FLOORSIZE, FLOORSIZE);
    static Player player = new Player(0, 0, 7, 20, 2);
    static int energy = player.getEnergy();
    static Random rand = new Random();
    static boolean endGame = true;

    /**
     * @param args
     */
    public static String input(String inputCommand) {
        String output = "";

        floor.getRoom(player.getXCoord(), player.getYCoord()).visit();

        Matcher helpMatch = helpPat.matcher(inputCommand);
        Matcher moveMatch = movePat.matcher(inputCommand);
        Matcher inspectMatch = inspectPat.matcher(inputCommand);
        Matcher takeMatch = takePat.matcher(inputCommand);
        Matcher dropMatch = dropPat.matcher(inputCommand);
        Matcher attackMatch = attackPat.matcher(inputCommand);
        Matcher bookMatch = bookPat.matcher(inputCommand);
        // Matcher takeChestMatch = takeChestPat.matcher(inputCommand);

        boolean commandKnown = true;

        //TODO: Make this work for api
        // if (inputCommand.equals("help")) {
        //     UI.helpCommand("all");
        //     commandKnown = false;
        // }

        // help command
        if (helpMatch.find()) {
            //TODO: Make UI start returning strings
            //UI.helpCommand(helpMatch.group(1));
            commandKnown = false;
        }
        // Move command
        if (moveMatch.find()) {
            commandKnown = false;
            int energyCost = UI.Commands.MOVE.getSpeedCommand();
            if (energy - energyCost < 0) {
                output += "You don't have enough energy to do this  ";
                //TODO: Make UI displayEnergy work
                //UI.displayEnergy(energy);
            } else {
                energy -= energyCost;
                //TODO: Make UI movement work
                //UI.move(moveMatch.group(1), player, floor, FLOORSIZE);
            }
        }

        // look around command
        if (inputCommand.equals(UI.Commands.LOOK_AROUND.getStrCommand())) {
            commandKnown = false;
            int energyCost = UI.Commands.LOOK_AROUND.getSpeedCommand();
            if (energy - energyCost < 0) {
                output += "You don't have enough energy to do this";
                //UI.displayEnergy(energy);
            } else {
                energy -= energyCost;
                floor.getDescription(player.getXCoord(), player.getYCoord());
            }
        }

        // bookmark command
        if (bookMatch.find()) {
            floor.getRoom(player.getXCoord(), player.getYCoord()).addBookmark(bookMatch.group(1),
                    bookMatch.group(2));
                    output += "This room is bookmarked with the character: " + bookMatch.group(1).charAt(0);
            commandKnown = false;
        }

        // inspect command
        if (inspectMatch.find()) {
            int energyCost = UI.Commands.INSPECT.getSpeedCommand();
            String command = inspectMatch.group(1);
            boolean itemFind = true;
            if (energy - energyCost < 0) {
                output += "You don't have enough energy to do this";
                UI.displayEnergy(energy);
            } else {
                energy -= energyCost;
                String itemString = inspectMatch.group(1);
                itemString = " " + itemString;
                Pattern takePattern = Pattern.compile("(?<=\\s)(\\w*)");
                Matcher matcher = takePattern.matcher(itemString);
                Interactable firstItem = null;
                Interactable secondItem = null;
                int num = 1;
                try {
                    matcher.find();
                    firstItem = floor.getRoom(player.getXCoord(), player.getYCoord()).getItem(matcher.group(1));
                    if (!(firstItem instanceof Container)) {
                        firstItem.getDescription();
                        firstItem = null;
                    }
                } catch (ThingNotFoundException e) {
                    try {
                        Interactable name = floor.getRoom(player.getXCoord(), player.getYCoord())
                                .getDescriptionInteractable(matcher.group(1));
                        name.getDescription();
                        itemFind = false;
                    } catch (ThingNotFoundException j) {
                        j.getMessage();
                    }
                }

                while (matcher.find()) {
                    if (num == 1) {
                        if (null != firstItem) {
                            if (firstItem instanceof Container) {
                                Container container = (Container) firstItem;
                                secondItem = container.getItem(matcher.group(1));
                            }
                        } else {
                            output += "There is no such thing in the container";
                        }
                    }
                    if (num == -1) {
                        if (null != secondItem) {
                            if (secondItem instanceof Container) {
                                Container container = (Container) secondItem;
                                firstItem = container.getItem(matcher.group(1));
                            }
                        } else {
                            output +=  "There is no such thing in the container";
                        }
                    }
                    num = num * -1;
                }

                if (itemFind) {
                    if (num == -1) {
                        if (null != secondItem) {
                            secondItem.getDescription();
                        } else {
                            output += "Cannot find that Item";
                        }
                    } else {
                        if (null != firstItem) {
                            firstItem.getDescription();
                        } else {
                            output += "Cannot find that Item";
                        }
                    }
                }

            }

            commandKnown = false;
        }

        // inventory command.
        if (inputCommand.equals(UI.Commands.INVENTORY.getStrCommand())) {
            int energyCost = UI.Commands.INVENTORY.getSpeedCommand();
            if (energy - energyCost < 0) {
                output +=  "You don't have enough energy to do this";
                //UI.displayEnergy(energy);
            } else {
                energy -= energyCost;
                //UI.displayInventory(player.getInventory(), player.getHealth(), player.getMaxHealth(), player.getMaxWeight());
            }

            commandKnown = false;
        }

        // take command
        if (takeMatch.find()) {
            int energyCost = UI.Commands.TAKE.getSpeedCommand();
            if (energy - energyCost < 0) {
                output +=  "You don't have enough energy to do this";
                //UI.displayEnergy(energy);
            } else {
                String itemString = takeMatch.group(1);
                itemString = " " + itemString;
                Pattern takePattern = Pattern.compile("(?<=\\s)(\\w*)");
                Matcher matcher = takePattern.matcher(itemString);
                Interactable firstItem = null;
                Interactable secondItem = null;
                int num = 1;
                try {
                    matcher.find();
                    firstItem = floor.getRoom(player.getXCoord(), player.getYCoord()).getItem(matcher.group(1));
                    if (!(firstItem instanceof Container)) {
                        player.putItem(firstItem);
                        floor.getRoom(player.getXCoord(), player.getYCoord()).takeItem(matcher.group(1));
                        firstItem = null;
                    }
                } catch (ThingNotFoundException e) {
                    output += e.getMessage();
                }

                while (matcher.find()) {
                    if (num == 1) {
                        if (null != firstItem) {
                            if (firstItem instanceof Container) {
                                Container container = (Container) firstItem;
                                secondItem = container.getItem(matcher.group(1));
                            }
                        } else {
                            output +=  "There is no such thing in the container";
                        }
                    }
                    if (num == -1) {
                        if (null != secondItem) {
                            if (secondItem instanceof Container) {
                                Container container = (Container) secondItem;
                                firstItem = container.getItem(matcher.group(1));
                            }
                        } else {
                            output += "There is no such thing in the container";
                        }
                    }
                    num = num * -1;
                }

                if (null != secondItem || null != firstItem) {
                    if (num == -1) {
                        Container temp = (Container) firstItem;
                        try {
                            secondItem = temp.takeItem(secondItem.getName());
                        } catch (ThingNotFoundException e) {

                            output += e.getMessage();
                        }
                        player.putItem(secondItem);
                    } else {
                        Container temp = (Container) secondItem;
                        try {
                            firstItem = temp.takeItem(firstItem.getName());
                        } catch (ThingNotFoundException e) {

                            output += e.getMessage();
                        }
                        player.putItem(firstItem);
                    }
                }

            }

            commandKnown = false;
        }

        // drop command
        if (dropMatch.find()) {
            int energyCost = UI.Commands.DROP.getSpeedCommand();
            if (energy - energyCost < 0) {
                output += "You don't have enough energy to do this";
                UI.displayEnergy(energy);
            } else {
                energy -= energyCost;
                try {
                    Interactable item = player.dropItem(dropMatch.group(1), 0);
                    floor.getRoom(player.getXCoord(), player.getYCoord()).addItem(item);
                    output += "dropped";
                } catch (ThingNotFoundException e) {
                    e.getMessage();
                }

            }

            commandKnown = false;
        }

        // health command
        if (inputCommand.equals(UI.Commands.HEALTH.getStrCommand())) {
            UI.displayHeath(player.getHealth(), player.getMaxHealth());
            commandKnown = false;
        }

        //TODO: Make Map work
        // if (inputCommand.equals("map")) {
        //     UI.displayMap(floor.getXSize(), floor.getYSize(), player, floor);
        //     commandKnown = false;
        // }

        // attack command
        if (attackMatch.find()) {
            String actorString = attackMatch.group(1);
            String weaponString = attackMatch.group(2);
            int energyCost = UI.Commands.ATTACK.getSpeedCommand();
            if (energy - energyCost < 0) {
                output += "You don't have enough energy to do this";
                UI.displayEnergy(energy);
            } else {
                if (player.isInInventory(weaponString)) {
                    try {
                        Weapon weapon = (Weapon) player.getItem(weaponString, 0);
                        NPC badGuy = floor.getNPC(actorString, player.getXCoord(), player.getYCoord(), 0);
                        if (player.closeCombat(weapon, badGuy)) {
                        } else {
                            UI.displayHeath(badGuy.getHealth(), badGuy.getMaxHealth());
                        }

                    } catch (ThingNotFoundException e) {
                        e.getMessage();
                    }
                    energy -= energyCost;
                } else {
                    output += "You don't have that in your inventory, so you attack with your hands";
                }
            }
            commandKnown = false;
        }

        // energy command
        if (inputCommand.equals(UI.Commands.ENERGY.getStrCommand())) {
            output += "\tEnergy: " + energy;
            UI.displayEnergy(energy);
            commandKnown = false;
        }

        if (inputCommand.equals(UI.Commands.REST.getStrCommand())) {
            commandKnown = false;
            energy -= energy;
        }

        // reset turn
        if (energy <= 0) {
            int randNum = rand.nextInt(5);
            switch (randNum) {
                case 0:
                    
                output += "Your eyes feel tired you can't go on. And so you take a short nap. But it must be quick you think, Your family is waiting";
                    break;
                case 1:
                    
                output +=  "The floor doesn't seem so bad you think, as you sink to your ground. I have to be quick though.";
                    break;
                case 2:
                    
                output += "Your eye lids droop and you can't take another step. This isn't the time to be falling asleep you think. My family can't wait";
                    break;
                case 3:
                    
                output += "Time has flown by and you are too tired tired to think right now. You fall to the ground and start to sleep.";
                    break;
                case 4:
                    
                output += "No more falling asleep you think. You have got to find one of those energy potions. There might be one somewhere you think as you fall asleep.";
                    break;
                case 5:
                    
                output += "I want a bed you think. Sleeping on the ground has got your back in knots. But you are just too tired to find a bed.";
            }

            Updates.update(player, floor);
            energy = player.getEnergy();
        } else {
            Updates.actionUpdate(floor);
        }

        if (inputCommand.equals("descend")) {
            output += "You reached the end of the Demo, thanks for playing";
        }

        // Easter eggs
        if (inputCommand.equals("Xyzzyz")) {
            player.setConstitution(15);
            player.setHealth();
            output += "You have found the cheat code. Your health is now 30";
            commandKnown = false;
            ;
        }
        if (inputCommand.equals("eat knife")) {
            if (player.isInInventory("Knife")) {
                player.setConstitution(0);
                player.setHealth();
                output +=  "You found the secret ending. PS this was Adam's idea";
            } else {
                output += "You don't have a knife to eat";
            }
            commandKnown = false;
        }

        if (inputCommand.equals(UI.Commands.EXIT.getStrCommand())) {
            endGame = false;
            ;
        }

        if (player.getHealth() <= 0) {
            endGame = false;
            UI.displayEnding();
        }

        // if command is not known
        if (commandKnown && inputCommand.equals("exit") == false) {
            output += "Sorry I don't know what you wanted.";
        }

        floor.getRoom(player.getXCoord(), player.getYCoord()).visit();
       
        return output;
    }

    //TODO: Make functions for getting player stats & stuff
}
