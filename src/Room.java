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
            case "D" -> "☗";
            case "*" -> "⛁";
            default -> " ";
        };
    }
    

    public void placeHero(Hero hero) {
        boolean found = false;
    
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
    public void moveHero(char direction, Hero hero) {
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
                return;
            }
        }
    
        // 범위 검사
        if (newRow < 0 || newRow >= rows || newCol < 0 || newCol >= cols) {
            System.out.println("You hit the wall!");
            return;
        }
    
        // 이동할 자리에 몬스터나 문이 있으면 막기
        String target = grid[newRow][newCol];
        if (target.startsWith("G") || target.startsWith("O") || target.startsWith("T")) {
            System.out.println("You can't move there.");
            return;
        }

        // 이동 처리
        grid[currentRow][currentCol] = " "; // 원래 위치 지우기
        grid[newRow][newCol] = "@";         // 새 위치 표시
        hero.setPosition(newRow, newCol);
    }
}


