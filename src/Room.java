import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Random;

public class Room {
    private String[][] grid;
    private int rows;
    private int cols;
    private String filename;

    public Room(String filename) {
        this.filename = filename;
        loadRoomFromCSV("rooms/" + filename);  // 예: "room1.csv"
    }

    public void loadRoomFromCSV(String path) {
        try (Scanner scanner = new Scanner(new File(path))) {
            if (scanner.hasNextLine()) {
                String[] size = scanner.nextLine().split(",");
                rows = Integer.parseInt(size[0].trim());
                cols = Integer.parseInt(size[1].trim());
                grid = new String[rows][cols];
            }

            int row = 0;
            while (scanner.hasNextLine() && row < rows) {
                String[] line = scanner.nextLine().split(",", -1);
                for (int col = 0; col < cols; col++) {
                    grid[row][col] = line[col].trim();
                }
                row++;
            }
        } catch (Exception e) {
            System.out.println("Error reading room file: " + e.getMessage());
        }
    }

    public void displayRoomWithStatus(Hero hero) {
        System.out.println("AdventureGame");
        System.out.printf("HP: %d/%d | Weapon: %s | Key: %s\n",
            hero.getHp(),
            hero.getMaxHp(),
            hero.getWeapon() == null ? "None" : hero.getWeapon().getName(),
            hero.hasKey() ? "Yes" : "No"
        );
    
        printWall();
    
        for (int i = 0; i < rows; i++) {
            System.out.print("|");
            for (int j = 0; j < cols; j++) {
                String cell = grid[i][j];
                System.out.printf("%-3s", renderCell(cell));
            }
            System.out.println("|");
        }
    
        printWall();
    
        System.out.print("Enter command (u/d/r/l to move, a to attack, q to quit): ");
    }    

    private String renderCell(String cell) {
        cell = cell.trim();
    
        if (cell.startsWith("G")) return "♙";
        if (cell.startsWith("O")) return "♞";
        if (cell.startsWith("T")) return "♖";
    
        return switch (cell) {
            case "@" -> "☻";
            case "m" -> "♥";
            case "B" -> "♛";
            case "S" -> "|";
            case "W" -> "†";
            case "X" -> "⚔";
            case "d" -> "☗";
            case "*" -> "⛁";
            default -> " ";
        };
    }
    

    public void placeHero(Hero hero) {
        boolean found = false;
        // 1. @ 있는지 찾기
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j].equals("@")) {
                    hero.setPosition(i, j);
                    found = true;
                    break;
                }
            }
            if (found) break;
        }
    
        if (!found) {
            // 2. [0][0]이 비어 있다면
            if (grid[0][0].isEmpty()) {
                grid[0][0] = "@";
                hero.setPosition(0, 0);
            } else {
                // 3. 랜덤 빈 칸 찾기
                Random rand = new Random();
                while (true) {
                    int r = rand.nextInt(rows);
                    int c = rand.nextInt(cols);
                    if (grid[r][c].isEmpty()) {
                        grid[r][c] = "@";
                        hero.setPosition(r, c);
                        break;
                    }
                }
            }
        }
    }

    public void saveRoomToCSV() {
        try (FileWriter writer = new FileWriter("rooms/" + filename)) {
            writer.write(rows + "," + cols + "\n");
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    writer.write(grid[i][j]);
                    if (j < cols - 1) writer.write(",");
                }
                writer.write("\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving room: " + e.getMessage());
        }
    }

    public void displayRoom() {
        System.out.println("Room: " + filename);
        printWall();

        for (int i = 0; i < rows; i++) {
            System.out.print("|");
            for (int j = 0; j < cols; j++) {
                String content = grid[i][j].isEmpty() ? " " : grid[i][j];
                System.out.printf("%-3s", content.length() == 1 ? content : "?");
            }
            System.out.println("|");
        }

        printWall();
    }

    private void printWall() {
        System.out.print("+");
        for (int i = 0; i < cols; i++) {
            System.out.print("---");
        }
        System.out.println("+");
    }

    // getter/setter
    public String[][] getGrid() {
        return grid;
    }

    public String getTile(int row, int col) {
        return grid[row][col];
    }

    public void setTile(int row, int col, String value) {
        grid[row][col] = value;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }
    public Room moveHero(char direction, Hero hero) {
        int currentRow = hero.getRow();
        int currentCol = hero.getCol();
        int newRow = currentRow;
        int newCol = currentCol;
    
        switch (direction) {
            case 'u' -> newRow--;
            case 'd' -> newRow++;
            case 'l' -> newCol--;
            case 'r' -> newCol++;
            default -> {
                System.out.println("Invalid direction! Use u/d/l/r.");
                return null;
            }
        }
    
        // 범위 검사
        if (newRow < 0 || newRow >= rows || newCol < 0 || newCol >= cols) {
            System.out.println("You hit the wall!");
            return null;
        }
    
        // 이동할 자리에 몬스터가 있으면 막기
        String target = grid[newRow][newCol];
        if (target.startsWith("G") || target.startsWith("O") || target.startsWith("T")) {
            System.out.println("You can't move there.");
            return null;
        }

        // Healing potion 처리
        if (target.equals("m") || target.equals("B")) {
            HealingPotion potion = new HealingPotion(target);
            if (hero.getHp() < hero.getMaxHp()) {
                hero.heal(potion.getHealAmount());
                System.out.println("You picked up a " + (target.equals("m") ? "Minor Flask" : "Big Flask") +
                        " and healed +" + potion.getHealAmount() + " HP!");
                grid[newRow][newCol] = " ";  // 포션 먹으면 자리 비우기
            } else {
                System.out.println("You're already fully healed. Potion remains.");
            }
        }

        // door 처리
        if (target.startsWith("d:")) {
            String nextRoomFile = target.substring(2); // "d:room2.csv" → "room2.csv"
            System.out.println("다음 방으로 이동합니다: " + nextRoomFile);

            saveRoomToCSV();  // 현재 방 상태 저장
            Room nextRoom = new Room(nextRoomFile);  // 새 방 로드
            nextRoom.placeHero(hero);  // 새 방에서 히어로 위치 배치
            // Main 쪽에서 현재 room 객체를 nextRoom으로 바꿔야 함
            return nextRoom;  // 방 이동했으니까 이후 코드 생략
        }

        if (target.equals("D")) {
            if (hero.hasKey()) {
                System.out.println("열쇠로 마스터 도어를 열고 탈출했습니다! 승리!");
                System.exit(0);  // 프로그램 종료
            } else {
                System.out.println("마스터 도어는 잠겨 있습니다. 열쇠가 필요합니다.");
                return null;
            }
        }

        // 무기 처리
        if (target.equals("S") || target.equals("W") || target.equals("X")) {
            Weapon newWeapon;
            switch (target) {
                case "S" -> newWeapon = new Weapon("Stick", 1);
                case "W" -> newWeapon = new Weapon("Weak Sword", 2);
                case "X" -> newWeapon = new Weapon("Strong Sword", 3);
                default -> newWeapon = null;  // 안전용, 이론상 안 옴
            }

            if (hero.getWeapon() == null) {
                hero.setWeapon(newWeapon);
                System.out.println("새 무기 '" + newWeapon.getName() + "'를 획득했습니다! 데미지: " + newWeapon.getDamage());
            } else {
                System.out.println("기존 무기 '" + hero.getWeapon().getName() + "'가 있습니다. 교체하시겠습니까? (y/n)");
                Scanner scanner = new Scanner(System.in);
                String choice = scanner.nextLine().trim().toLowerCase();

                if (choice.equals("y")) {
                    hero.setWeapon(newWeapon);
                    System.out.println("무기를 '" + newWeapon.getName() + "'로 교체했습니다!");
                } else {
                    System.out.println("기존 무기를 유지합니다.");
                }
            }

            grid[newRow][newCol] = " ";  // 무기 칸 비우기
        }


        // 이동 처리
        grid[currentRow][currentCol] = " "; // 원래 위치 지우기
        grid[newRow][newCol] = "@";         // 새 위치 표시
        hero.setPosition(newRow, newCol);

        return null;
    }
    // 몬스터 입접 여부 판단
    public boolean isAdjacentToMonster(Hero hero) {
        int heroRow = hero.getRow();
        int heroCol = hero.getCol();

        int[][] directions = { {-1,0}, {1,0}, {0,-1}, {0,1} };
        for (int[] dir : directions) {
            int newRow = heroRow + dir[0];
            int newCol = heroCol + dir[1];

            if (newRow < 0 || newRow >= rows || newCol < 0 || newCol >= cols) continue;

            String target = grid[newRow][newCol];
            if (target.startsWith("G") || target.startsWith("O") || target.startsWith("T")) {
                return true;
            }
        }
        return false;
    }

    // 유저가 a 키를 눌렀을 때 실행됨.
    public void handleAttack(Hero hero) {
        if (hero.getWeapon() == null) {
            System.out.println("무기가 없어 공격할 수 없습니다!");
            return;
        }

        int heroRow = hero.getRow();
        int heroCol = hero.getCol();

        // 상하좌우 검사
        int[][] directions = { {-1,0}, {1,0}, {0,-1}, {0,1} };
        for (int[] dir : directions) {
            int newRow = heroRow + dir[0];
            int newCol = heroCol + dir[1];

            if (newRow < 0 || newRow >= rows || newCol < 0 || newCol >= cols) continue;

            String target = grid[newRow][newCol];

            Monsters monster = null;
            int hp = 0;

            if (target.startsWith("G")) {
                monster = new Goblin();
                if (target.contains(":")) {
                    hp = Integer.parseInt(target.split(":")[1]);
                } else {
                    hp = 3;  // 기본 Goblin HP
                }
            } else if (target.startsWith("O")) {
                monster = new Orc();
                if (target.contains(":")) {
                    hp = Integer.parseInt(target.split(":")[1]);
                } else {
                    hp = 8;  // 기본 Orc HP
                }
            } else if (target.startsWith("T")) {
                monster = new Troll();
                if (target.contains(":")) {
                    hp = Integer.parseInt(target.split(":")[1]);
                } else {
                    hp = 15;  // 기본 Troll HP
                }
            }

            if (monster != null) {
                monster.hp = hp;
                System.out.println("인접한 " + monster.getClass().getSimpleName() + " 발견! HP: " + hp);
                System.out.print("공격(a) 또는 무시(x)? ");

                Scanner scanner = new Scanner(System.in);
                String choice = scanner.nextLine().trim().toLowerCase();

                if (choice.equals("a")) {
                    monster.takeDamage(hero.getWeapon().getDamage());
                    hero.takeDamage(monster.getDamage());

                    System.out.println("몬스터에게 " + hero.getWeapon().getDamage() + " 데미지를 입힘!");
                    System.out.println("몬스터에게서 " + monster.getDamage() + " 데미지를 받음!");
                    System.out.println("Hero HP: " + hero.getHp());

                    if (monster.isDead()) {
                        System.out.println("몬스터 처치!");
                        grid[newRow][newCol] = " ";
                        if (monster instanceof Troll) {
                            System.out.println("Troll이 열쇠를 떨어뜨렸습니다!");
                            hero.obtainKey();
                        }
                    } else {
                        grid[newRow][newCol] = target.split(":")[0] + ":" + monster.getHp();  // 예: G:2
                    }
                }
                return;  // 한 마리만 공격 가능, 한 번만 처리
            }
        }

        System.out.println("주변에 공격할 몬스터가 없습니다.");
    }

}


