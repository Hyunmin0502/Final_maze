import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Room room = new Room("room1.csv");
        Hero hero = new Hero();
        Scanner scanner = new Scanner(System.in);

        room.placeHero(hero);

        while (true) {
            room.displayRoomWithStatus(hero);

            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("q")) break;

            if (input.length() == 1) {
                char cmd = input.charAt(0);
                if ("udlr".indexOf(cmd) != -1) {
                    room.moveHero(cmd, hero);
                } else if (cmd == 'a') {
                    System.out.println("Attack (coming soon)!");
                } else {
                    System.out.println("Invalid input.");
                }
            }
        }
    }
}
