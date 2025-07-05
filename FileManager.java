import java.io.*;
import java.util.*;

public class FileManager {

    public static int readStreak() {
        try {
            File file = new File("streak.txt");
            if (file.exists()) {
                Scanner scanner = new Scanner(file);
                if (scanner.hasNextInt()) {
                    return scanner.nextInt();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void writeStreak(int streak) {
        try {
            FileWriter writer = new FileWriter("streak.txt");
            writer.write(String.valueOf(streak));
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int readDailyTime() {
        try {
            File file = new File("daily_time.txt");
            if (file.exists()) {
                Scanner scanner = new Scanner(file);
                if (scanner.hasNextInt()) {
                    return scanner.nextInt();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void writeDailyTime(int timeInSeconds) {
        try {
            FileWriter writer = new FileWriter("daily_time.txt");
            writer.write(String.valueOf(timeInSeconds));
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveDailyHistory(String date, double hours) {
        try {
            List<String> lines = new ArrayList<>();
            File file = new File("history.txt");
            if (file.exists()) {
                Scanner scanner = new Scanner(file);
                while (scanner.hasNextLine()) {
                    lines.add(scanner.nextLine());
                }
                scanner.close();
            }
            lines.add(date + ": " + String.format("%.2f", hours) + " hrs");
            while (lines.size() > 5) {
                lines.remove(0);
            }

            FileWriter writer = new FileWriter("history.txt");
            for (String line : lines) {
                writer.write(line + "\n");
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readHistory() {
        StringBuilder history = new StringBuilder();
        try {
            File file = new File("history.txt");
            if (file.exists()) {
                Scanner scanner = new Scanner(file);
                while (scanner.hasNextLine()) {
                    history.append(scanner.nextLine()).append("\n");
                }
                scanner.close();n
