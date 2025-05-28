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
    private String basePath;

    public Room(String basePath, String filename) {
        this.basePath = basePath;
        this.filename = filename;
        loadRoomFromCSV(basePath + File.separator + filename);
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
        if (cell.startsWith("d")) return "☗";

        return switch (cell) {
            case "@" -> "☻";
            case "m" -> "♥";
            case "B" -> "♛";
            case "S" -> "|";
            case "W" -> "†";
            case "X" -> "⚔";
            case "D" -> "\uD83D\uDEAA";
            case "*" -> "⛁";
            default -> " ";
        };
    }

    public void placeHero(Hero hero) {
        boolean found = false;
        // 1. Check if '@' is already placed
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
            // 2. If top-left is empty, place hero there
            if (grid[0][0].isEmpty()) {
                grid[0][0] = "@";
                hero.setPosition(0, 0);
            } else {
                // 3. Otherwise, find a random empty spot
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
        try (FileWriter writer = new FileWriter(basePath + File.separator + filename)) {
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

    private void printWall() {
        System.out.print("+");
        for (int i = 0; i < cols; i++) {
            System.out.print("---");
        }
        System.out.println("+");
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

        if (newRow < 0 || newRow >= rows || newCol < 0 || newCol >= cols) {
            System.out.println("You hit the wall!");
            return null;
        }

        String target = grid[newRow][newCol];
        if (target.startsWith("G") || target.startsWith("O") || target.startsWith("T")) {
            System.out.println("You can't move there.");
            return null;
        }

        if (target.equals("m") || target.equals("B")) {
            HealingPotion potion = new HealingPotion(target);
            if (hero.getHp() < hero.getMaxHp()) {
                hero.heal(potion.getHealAmount());
                System.out.println("You picked up a " + (target.equals("m") ? "Minor Flask" : "Big Flask") +
                        " and healed +" + potion.getHealAmount() + " HP!");
                grid[newRow][newCol] = " ";
            } else {
                System.out.println("You're already fully healed. Potion remains.");
            }
        }

        if (target.startsWith("d:")) {
            String nextRoomFile = target.substring(2); // e.g., "d:room2.csv" → "room2.csv"
            System.out.println("Moving to the next room: " + nextRoomFile);

            saveRoomToCSV();
            Room nextRoom = new Room(basePath, nextRoomFile);
            nextRoom.placeHero(hero);
            return nextRoom;
        }

        if (target.equals("D")) {
            if (hero.hasKey()) {
                System.out.println("You used the key and escaped through the master door. Victory!");
                System.exit(0);
            } else {
                System.out.println("The master door is locked. You need a key.");
                return null;
            }
        }

        if (target.equals("S") || target.equals("W") || target.equals("X")) {
            Weapon newWeapon;
            switch (target) {
                case "S" -> newWeapon = new Weapon("Stick", 1);
                case "W" -> newWeapon = new Weapon("Weak Sword", 2);
                case "X" -> newWeapon = new Weapon("Strong Sword", 3);
                default -> newWeapon = null;
            }

            if (hero.getWeapon() == null) {
                hero.setWeapon(newWeapon);
                System.out.println("You picked up a new weapon '" + newWeapon.getName() + "'! Damage: " + newWeapon.getDamage());
            } else {
                System.out.println("You already have '" + hero.getWeapon().getName() + "'. Replace it? (y/n)");
                Scanner scanner = new Scanner(System.in);
                String choice = scanner.nextLine().trim().toLowerCase();

                if (choice.equals("y")) {
                    hero.setWeapon(newWeapon);
                    System.out.println("Weapon replaced with '" + newWeapon.getName() + "'.");
                } else {
                    System.out.println("Keeping the current weapon.");
                }
            }

            grid[newRow][newCol] = " ";
        }

        grid[currentRow][currentCol] = " ";
        grid[newRow][newCol] = "@";
        hero.setPosition(newRow, newCol);

        return null;
    }

    // Check if there is a monster adjacent to the hero
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

    // Handle attack when user presses 'a'
    public void handleAttack(Hero hero) {
        if (hero.getWeapon() == null) {
            System.out.println("You have no weapon to attack!");
            return;
        }

        int heroRow = hero.getRow();
        int heroCol = hero.getCol();

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
                hp = target.contains(":") ? Integer.parseInt(target.split(":")[1]) : 3;
            } else if (target.startsWith("O")) {
                monster = new Orc();
                hp = target.contains(":") ? Integer.parseInt(target.split(":")[1]) : 8;
            } else if (target.startsWith("T")) {
                monster = new Troll();
                hp = target.contains(":") ? Integer.parseInt(target.split(":")[1]) : 15;
            }

            if (monster != null) {
                monster.hp = hp;
                System.out.println(monster.getClass().getSimpleName() + " HP: " + hp);
                System.out.print("attack(a) or not(x)? ");

                Scanner scanner = new Scanner(System.in);
                String choice = scanner.nextLine().trim().toLowerCase();

                if (choice.equals("a")) {
                    monster.takeDamage(hero.getWeapon().getDamage());
                    hero.takeDamage(monster.getDamage());

                    System.out.println("You dealt " + hero.getWeapon().getDamage() + " damage to the monster!");
                    System.out.println("You received " + monster.getDamage() + " damage from the monster!");
                    System.out.println("Hero HP: " + hero.getHp());

                    if (monster.isDead()) {
                        System.out.println("Monster defeated!");
                        grid[newRow][newCol] = " ";
                        if (monster instanceof Troll) {
                            System.out.println("The Troll dropped a key!");
                            hero.obtainKey();
                        }
                    } else {
                        grid[newRow][newCol] = target.split(":")[0] + ":" + monster.getHp();
                        System.out.println("The monster survived. Remaining HP: " + monster.getHp());
                    }
                }
                return;
            }
        }

        System.out.println("No monsters to attack nearby.");
    }
}
