import java.util.*;

public class Main {
    public static void main(String[] args) {
        String runFolderPath = GameSetup.setupNewRunFolder();
        Room room = new Room(runFolderPath, "room1.csv");
        Hero hero = new Hero();
        Scanner scanner = new Scanner(System.in);

        room.placeHero(hero);


        while (true) {
            room.displayRoomWithStatus(hero);
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("q")) {
                room.saveRoomToCSV();
                break;
            }

            if (input.length() == 1) {
                char cmd = input.charAt(0);
                if ("udlr".indexOf(cmd) != -1) {
                    Room nextRoom = room.moveHero(cmd, hero);
                    if (nextRoom != null) {
                        room = nextRoom;  // to the next room
                    }
                    // only after movement, there exists monsters
                    if (room.isAdjacentToMonster(hero)) {
                        room.handleAttack(hero);
                    }
                } else if (cmd == 'a') {
                    room.handleAttack(hero);

                } else {
                    System.out.println("Invalid input.");
                }
            }
        }
    }
}
