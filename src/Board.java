import javax.swing.*;
import java.awt.*;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class Board extends JFrame {
    private final Tile[][] tiles = new Tile[7][7];
    private final JLabel goalText;

    private int goal;

    private final AtomicInteger timer = new AtomicInteger(0);

    public static final int TILE_SIZE = 50;
    public static final int BOARD_PADDING = 15;

    public static final int TILE_MARGIN = 5;

    public static final Color DEFAULT_COLOR = Color.WHITE;
    public static final Color SELECTED_COLOR = Color.LIGHT_GRAY;

    public void startGame() {
        timer.set(0);

        ArrayList<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            if (i < 5) {
                numbers.add(6);
                numbers.add(6);

                numbers.add(1);
                numbers.add(7);
                numbers.add(8);
            }

            numbers.add(2);
            numbers.add(4);
            numbers.add(3);
            numbers.add(5);
        }

        goal = (int)(Math.random() * 49) + 1;
        goalText.setText(Integer.toString(goal));

        for (int y = 0; y < 7; y++) {
            for (int x = 0; x < 7; x++) {
                int index = (int)(Math.random() * numbers.size());
                int number = numbers.get(index);

                tiles[y][x].set(number);

                numbers.remove(index);
            }
        }
    }

    public Board() {
        super();

        this.setBackground(Color.WHITE);
        this.setLayout(null);

        this.setResizable(false);

        int boardSize = BOARD_PADDING * 2 + (TILE_SIZE + TILE_MARGIN) * 7;
        this.getContentPane().setPreferredSize(new Dimension(boardSize, boardSize + TILE_SIZE + TILE_MARGIN));

        JButton timerText = new JButton("00:00.00");
        timerText.setVisible(true);

        timerText.setSize(TILE_SIZE * 2, TILE_SIZE);
        timerText.setLocation(BOARD_PADDING, BOARD_PADDING + (TILE_SIZE + TILE_MARGIN) * 7 + TILE_MARGIN);

        timerText.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));

        timerText.setBackground(Color.WHITE);
        timerText.setFont(new Font("", Font.BOLD, 12));

        this.add(timerText);

        Thread timerThread = new Thread(() -> {
            while(true) {
                long ms = timer.incrementAndGet();
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                long minutes = ms / 60000;
                long seconds = (ms % 60000) / 1000;
                long millis = ms % 1000 / 10;

                timerText.setText(String.format("%d:%02d.%02d", minutes, seconds, millis));
            }
        });

        timerThread.start();

        JButton checkSolutions = new JButton("Check? -");
        checkSolutions.setVisible(true);

        checkSolutions.setSize(TILE_SIZE * 2, TILE_SIZE);
        checkSolutions.setLocation(boardSize - TILE_MARGIN - TILE_SIZE * 2 - BOARD_PADDING,
                BOARD_PADDING + (TILE_SIZE + TILE_MARGIN) * 7 + TILE_MARGIN);

        checkSolutions.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));

        checkSolutions.setBackground(Color.WHITE);
        checkSolutions.setFont(new Font("", Font.BOLD, 12));

        checkSolutions.addActionListener(event ->
                new Thread(() -> checkSolutions.setText("Check? " + findSolutions())).start());

        this.add(checkSolutions);

        this.goalText = new JLabel("", SwingConstants.CENTER);

        this.goalText.setLocation(BOARD_PADDING + (TILE_SIZE + TILE_MARGIN) * 3, BOARD_PADDING + (TILE_SIZE + TILE_MARGIN) * 7 + TILE_MARGIN);
        this.goalText.setSize(TILE_SIZE, TILE_SIZE);

        this.goalText.setOpaque(true);

        this.goalText.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));

        this.goalText.setBackground(new Color(0, 143,255));

        this.goalText.setForeground(Color.WHITE);
        this.goalText.setFont(new Font("", Font.BOLD, 12));

        this.add(goalText);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.pack();

        this.setVisible(true);

        for (int y = 0; y < 7; y++) {
            for (int x = 0; x < 7; x++) {
                Tile tile = new Tile(x, y);

                tile.addActionListener(event -> {
                    if (tile.getBackground().equals(DEFAULT_COLOR)) {
                        tile.setBackground(SELECTED_COLOR);
                    } else {
                        tile.setBackground(DEFAULT_COLOR);
                    }

                    Tile[] selectedTiles = findSelectedTiles();
                    if (selectedTiles.length == 3) {
                        if (rowCheck(selectedTiles)) {
                            boolean check = (checkMatch(selectedTiles[0].get(), selectedTiles[1].get(), selectedTiles[2].get()));
                            if (check) {
                                checkSolutions.setText("Check? -");

                                startGame();
                            }
                        }

                        selectedTiles[0].setBackground(Color.WHITE);
                        selectedTiles[1].setBackground(Color.WHITE);
                        selectedTiles[2].setBackground(Color.WHITE);
                    }
                });

                tiles[y][x] = tile;
                this.add(tiles[y][x]);
            }
        }

        this.repaint();
    }

    public boolean checkMatch(int x, int y, int z) {
        ArrayList<Integer> l1 = new ArrayList<>();
        ArrayList<Integer> l2 = new ArrayList<>();
        ArrayList<Integer> l3 = new ArrayList<>();

        for (int a = 1; a <= 9; a++) {
            for (int b = 1; b <= 9; b++) {
                for (int c = 1; c <= 9; c++) {
                    if (a * b + c == goal || a * b - c == goal || b * c + a == goal || b * c - a == goal || c * a + b == goal || c * a - b == goal) {
                        l1.add(a);
                        l2.add(b);
                        l3.add(c);
                    }
                }
            }
        }

        for (int i = 0; i < l1.size(); i++) {
            if ((x == l1.get(i) || (x == 6 && l1.get(i) == 9))
                    && (y == l2.get(i) || (y == 6 && l2.get(i) == 9))
                    && (z == l3.get(i) || (z == 6 && l3.get(i) == 9))) return true;
        }

        return false;
    }

    private int findSolutions() {
        int counter = 0;
        Tile[] selectedTiles = new Tile[3];

        for (int x1 = 0; x1 < 7; x1++) {
            for (int y1 = 0; y1 < 7; y1++) {
                for (int x2 = 0; x2 < 7; x2++) {
                    for (int y2 = 0; y2 < 7; y2++) {
                        for (int x3 = 0; x3 < 7; x3++) {
                            for (int y3 = 0; y3 < 7; y3++) {
                                if (x1 == x2 || x2 == x3 || x1 == x3) continue;
                                if (y1 == y2 || y2 == y3 || y1 == y3) continue;

                                selectedTiles[0] = tiles[y1][x1];
                                selectedTiles[1] = tiles[y2][x2];
                                selectedTiles[2] = tiles[y3][x3];

                                if (rowCheck(selectedTiles)) {
                                    boolean check = (checkMatch(selectedTiles[0].get(), selectedTiles[1].get(), selectedTiles[2].get()));
                                    if (check) {
                                        counter++;

                                        selectedTiles[0].setBackground(Color.CYAN);
                                        selectedTiles[1].setBackground(Color.CYAN);
                                        selectedTiles[2].setBackground(Color.CYAN);

                                        try {
                                            Thread.sleep(200);
                                        } catch (Exception ignored) {}

                                        selectedTiles[0].setBackground(Color.WHITE);
                                        selectedTiles[1].setBackground(Color.WHITE);
                                        selectedTiles[2].setBackground(Color.WHITE);

                                        try {
                                            Thread.sleep(200);
                                        } catch (Exception ignored) {}
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return counter;
    }

    private int mid(int a, int b) {
        return (a + b) / 2;
    }

    private boolean rowCheck(Tile[] selected) {
        int[] x = Stream.of(selected).mapToInt(Tile::getXPosition).toArray();
        int[] y = Stream.of(selected).mapToInt(Tile::getYPosition).toArray();

        int minX = Math.min(x[0], Math.min(x[1], x[2]));
        int maxX = Math.max(x[0], Math.max(x[1], x[2]));

        int minY = Math.min(y[0], Math.min(y[1], y[2]));
        int maxY = Math.max(y[0], Math.max(y[1], y[2]));

        int midX = mid(minX, maxX);
        int midY = mid(minY, maxY);

        boolean check = false;
        for (Tile tile: selected) {
            if (tile.getXPosition() == midX && tile.getYPosition() == midY) {
                check = true;
                break;
            }
        }

        return check && ((maxX - minX == 2 || maxX == minX) && (maxY - minY == 2 || maxY == minY));
    }

    private Tile[] findSelectedTiles() {
        ArrayList<Tile> selectedTiles = new ArrayList<>();
        for (int y = 0; y < 7; y++) {
            for (int x = 0; x < 7; x++) {
                if (tiles[y][x].getBackground().equals(SELECTED_COLOR) && tiles[y][x].get() > 0)
                    selectedTiles.add(tiles[y][x]);
            }
        }
        return selectedTiles.toArray(Tile[]::new);
    }
}
