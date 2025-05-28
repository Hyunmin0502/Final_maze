import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GameSetup {
    public static String setupNewRunFolder() {
        String runsDir = "rooms/runs";
        File runsFolder = new File(runsDir);
        if (!runsFolder.exists()) {
            runsFolder.mkdirs();  // ⚠ mkdir → mkdirs로, 중간 경로까지 생성하도록!
        }

        // 예: run_20240528_1234 (날짜 + 시간)
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String runFolderName = "run_" + timestamp;
        File runFolder = new File(runsFolder, runFolderName);
        runFolder.mkdir();

        // rooms 폴더에서 복사
        File roomsFolder = new File("rooms");
        File[] roomFiles = roomsFolder.listFiles((dir, name) -> name.endsWith(".csv"));
        if (roomFiles != null) {
            for (File file : roomFiles) {
                try {
                    Files.copy(file.toPath(), Paths.get(runFolder.getPath(), file.getName()), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    System.out.println("Failed to copy " + file.getName() + ": " + e.getMessage());
                }
            }
        }

        return runFolder.getPath();
    }
}
