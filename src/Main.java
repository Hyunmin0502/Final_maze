import java.util.*;

public class Main {
    public static void main(String[] args) {
        Map<String, Room> visitedRooms = new HashMap<>(); // 방문한 모든 방을 추적
        Room room = new Room("room1.csv");
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
                        room = nextRoom;  // 방 교체 처리
                    }
                    // 이동 후 주변에 몬스터가 있을 때만 공격 메뉴 띄움
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
