import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.Timer;
import java.util.TimerTask;

public class ScreenTimeTracker extends JFrame {

    private JLabel timerLabel, streakLabel, statusLabel, totalLabel;
    private JTextField limitField;
    private JButton startButton, stopButton, resetButton;
    private Timer sessionTimer;

    private int totalSecondsToday = 0;
    private int sessionSeconds = 0;
    private int dailyLimitSeconds = 0;
    private int streak = 0;
    private boolean isTracking = false;

    public ScreenTimeTracker() {
        setTitle("📱 Digital Detox Tracker 📱");
        setSize(500, 500);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(new Color(240, 248, 255));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        streak = FileManager.readStreak();
        totalSecondsToday = FileManager.readDailyTime();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel heading = new JLabel("Digital Detox Tracker");
        heading.setFont(new Font("SansSerif", Font.BOLD, 24));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(heading, gbc);

        JLabel limitLabel = new JLabel("Set daily screen time limit (minutes):");
        gbc.gridy = 1; gbc.gridwidth = 1;
        add(limitLabel, gbc);

        limitField = new JTextField(10);
        gbc.gridx = 1;
        add(limitField, gbc);

        startButton = new JButton("Start Tracking");
        startButton.setBackground(new Color(135, 206, 250));
        startButton.setFocusPainted(false);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        add(startButton, gbc);

        stopButton = new JButton("Stop Tracking");
        stopButton.setBackground(new Color(255, 99, 71));
        stopButton.setFocusPainted(false);
        stopButton.setEnabled(false);
        gbc.gridy = 3;
        add(stopButton, gbc);

        timerLabel = new JLabel("Session Time: 0 min 0 sec");
        timerLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        gbc.gridy = 4;
        add(timerLabel, gbc);

        totalLabel = new JLabel("Total Screen Time Today: " + formatTime(totalSecondsToday));
        totalLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        gbc.gridy = 5;
        add(totalLabel, gbc);

        streakLabel = new JLabel("🔥 Streak: " + streak + " day(s)");
        streakLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        gbc.gridy = 6;
        add(streakLabel, gbc);

        statusLabel = new JLabel("");
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        gbc.gridy = 7;
        add(statusLabel, gbc);

        resetButton = new JButton("Reset Daily Time");
        resetButton.setBackground(new Color(255, 160, 122));
        resetButton.setFocusPainted(false);
        gbc.gridy = 8;
        add(resetButton, gbc);

        JButton historyButton = new JButton("View History");
        historyButton.setBackground(new Color(240, 230, 140));
        historyButton.setFocusPainted(false);
        gbc.gridy = 9;
        add(historyButton, gbc);

        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String input = limitField.getText().trim();
                if (input.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "❌ Please enter your screen time limit.");
                    return;
                }
                try {
                    int limit = Integer.parseInt(input);
                    if (limit <= 0) {
                        JOptionPane.showMessageDialog(null, "❌ Please enter a positive number.");
                        return;
                    }
                    dailyLimitSeconds = limit * 60;
                    startButton.setEnabled(false);
                    stopButton.setEnabled(true);
                    limitField.setEditable(false);
                    isTracking = true;
                    startTracking();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "❌ Please enter a valid number.");
                }
            }
        });

        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (sessionTimer != null) {
                    sessionTimer.cancel();
                    sessionTimer = null;
                }
                isTracking = false;
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
                limitField.setEditable(true);

                // Save history only when session ends
                String currentDate = LocalDate.now().toString();
                double hoursToday = totalSecondsToday / 3600.0;
                FileManager.saveDailyHistory(currentDate, hoursToday);

                JOptionPane.showMessageDialog(null, "🚫 Tracking Stopped.");
            }
        });

        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                totalSecondsToday = 0;
                FileManager.writeDailyTime(0);
                totalLabel.setText("Total Screen Time Today: 0 min 0 sec");
                statusLabel.setText("");
                limitField.setEditable(true);
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
                JOptionPane.showMessageDialog(null, "✅ Daily time reset!");
            }
        });

        historyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String history = FileManager.readHistory();
                if (history.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "No history found.");
                } else {
                    JOptionPane.showMessageDialog(null, "📅 Last 5 Days:\n" + history);
                }
            }
        });

        setVisible(true);
    }

    private void startTracking() {
        sessionSeconds = 0;
        if (sessionTimer != null) sessionTimer.cancel();

        sessionTimer = new Timer();
        sessionTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (!isTracking) return;

                sessionSeconds++;
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        timerLabel.setText("Session Time: " + (sessionSeconds / 60) + " min " + (sessionSeconds % 60) + " sec");
                        totalLabel.setText("Total Screen Time Today: " + formatTime(totalSecondsToday));
                        FileManager.writeDailyTime(totalSecondsToday);
                        if (totalSecondsToday > dailyLimitSeconds) {
                            statusLabel.setText("⚠️ Limit crossed! Bro, touch grass ☘️");
                            streak = 0;
                            FileManager.writeStreak(streak);
                            streakLabel.setText("🔥 Streak: " + streak + " day(s)");
                        } else {
                            statusLabel.setText("🎉 You are within your limit.");
                        }
                    }
                });

                if (sessionSeconds % 60 == 0) {
                    String currentDate = LocalDate.now().toString();
                    double hoursToday = totalSecondsToday / 3600.0;
                    FileManager.saveDailyHistory(currentDate, hoursToday);
                }
            }
        }, 0, 1000);
    }

    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return minutes + " min " + seconds + " sec";
                // Removed frequent history saving to reduce I/O
