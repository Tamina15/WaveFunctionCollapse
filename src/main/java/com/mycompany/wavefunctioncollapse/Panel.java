package com.mycompany.wavefunctioncollapse;

import com.mycompany.wavefunctioncollapse.util.FileReader;
import com.mycompany.wavefunctioncollapse.model.Cell;
import com.mycompany.wavefunctioncollapse.model.Tile;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Panel extends JPanel {

    boolean running = true;
    private final int width, height;
    private final ArrayList<Tile> tiles;
    private Cell[][] cells;
    private int cellsX, cellsY;
    private int xOverflow, yOverflow;
    static final int IMAGE_DIMENSION = 15;
    static final String IMAGE_PATH = "src/main/resources/white/lines/data.txt";
    private Point origin = new Point(0, 0);
    private Point mousePt = new Point(0, 0);
    private final Random r = new Random();
    private double zoomFactor = 1;
    private int scale = 1;
    int runNTimes = 1;
    boolean updateType;

    public Panel(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = FileReader.readImageData(IMAGE_PATH);
        this.setPreferredSize(new Dimension(width, height));
        this.setDoubleBuffered(true);
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        addMouseWheelListener((MouseWheelEvent e) -> {
            //Zoom in
            if (e.getWheelRotation() < 0) {
                zoomFactor *= 1.1;
            }
            //Zoom out
            if (e.getWheelRotation() > 0) {
                zoomFactor /= 1.1;
            }
        });
        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int dx = (e.getX() - mousePt.x) * scale;
                int dy = (e.getY() - mousePt.y) * scale;
                mousePt.setLocation(e.getX(), e.getY());
                origin.setLocation(origin.getX() + dx, origin.getY() + dy);
            }
        });
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    mousePt.setLocation(e.getPoint());
                    if (e.isAltDown()) {
                        update();
                    }
                    if (e.isShiftDown()) {
                        running = !running;
                    }
                }
                if (SwingUtilities.isRightMouseButton(e)) {
                    if (!e.isAltDown()) {
                        runNTimes++;
                    }
                    if (e.isAltDown()) {
                        runNTimes--;
                        if (runNTimes < 0) {
                            runNTimes = 0;
                        }
                    }
                }
                if (SwingUtilities.isMiddleMouseButton(e)) {
                    updateType = !updateType;
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
            }
        });

        init();
    }

    private void init() {
        // Spin the tiles
        int initialTileCount = tiles.size();
        for (int i = 0; i < initialTileCount; i++) {
            Tile tile = tiles.get(i);
            for (int j = 1; j < 4; j++) {
                Tile rotated = tile.rotate(j);
                if (rotated != null) {
                    tiles.add(rotated);
                    rotated.setIndex(tiles.size() - 1);
                }
            }
        }
        System.out.println("Tiles size:" + tiles.size());

        // Analyze all tiles for possible neighbor tiles
        for (Tile tile : tiles) {
            tile.analyzeNeighbor(tiles);
        }
        xOverflow = 4;
        yOverflow = 4;
        cellsX = this.width / IMAGE_DIMENSION + 2 * xOverflow;
        cellsY = this.height / IMAGE_DIMENSION + 2 * yOverflow;
        cells = new Cell[cellsX][cellsY];
        for (int i = 0; i < cellsX; i++) {
            for (int j = 0; j < cellsY; j++) {
                cells[i][j] = new Cell(tiles.size(), i, j);
            }
        }

//        cells[r.nextInt(cellsX)][r.nextInt(cellsX)].collapse(tiles.get(r.nextInt(tiles.size())));        
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.scale(zoomFactor, zoomFactor);
        g2d.translate(origin.x, origin.y);

        g2d.setColor(Color.white);
        drawCell(g2d);
        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, 50, 30);
        g2d.setColor(Color.white);
        g2d.drawString(updatetime + "", 10, 20);
    }

    public void drawCell(Graphics2D g2d) {
        for (int i = 0; i < cellsX; i++) {
            for (int j = 0; j < cellsY; j++) {
                if (cells[i][j].isCollapse()) {
                    g2d.drawImage(cells[i][j].getTile().getImage(), i * IMAGE_DIMENSION * scale - xOverflow * (IMAGE_DIMENSION * scale), j * IMAGE_DIMENSION * scale - yOverflow * (IMAGE_DIMENSION * scale), IMAGE_DIMENSION * scale, IMAGE_DIMENSION * scale, null);
                } else {
                    g2d.setColor(Color.white);
                    if (cells[i][j].isInQueue()) {
                        g2d.setColor(Color.red);
                    }
                    if (cells[i][j].isIterate()) {
                        g2d.setColor(Color.green);
                    }
                    g2d.drawString(cells[i][j].getOptions().size() + "", i * IMAGE_DIMENSION * scale - xOverflow * (IMAGE_DIMENSION * scale), j * IMAGE_DIMENSION * scale + IMAGE_DIMENSION - yOverflow * (IMAGE_DIMENSION * scale));
                }
            }
        }
    }

    public void drawOptions(Graphics2D g2d) {
        for (int i = 0; i < tiles.size(); i++) {
            g2d.drawImage(tiles.get(i).getImage(), i * IMAGE_DIMENSION * scale, 0 * IMAGE_DIMENSION * scale, IMAGE_DIMENSION * scale, IMAGE_DIMENSION * scale, null);
        }
        for (int a = 0; a < tiles.size(); a++) {
            g2d.drawImage(tiles.get(a).getImage(), 0 * IMAGE_DIMENSION * scale + (500 * a), 0 * IMAGE_DIMENSION * scale, IMAGE_DIMENSION * scale, IMAGE_DIMENSION * scale, null);
            for (int i = 0; i < tiles.get(a).getUp().size(); i++) {
                g2d.drawImage(tiles.get(a).getUp().get(i).getImage(), 0 * IMAGE_DIMENSION * scale + (500 * a), -i * IMAGE_DIMENSION * scale - (IMAGE_DIMENSION * scale), IMAGE_DIMENSION * scale, IMAGE_DIMENSION * scale, null);
            }
            for (int i = 0; i < tiles.get(a).getRight().size(); i++) {
                g2d.drawImage(tiles.get(a).getRight().get(i).getImage(), i * IMAGE_DIMENSION * scale + (IMAGE_DIMENSION * scale) + (500 * a), 0 * IMAGE_DIMENSION * scale, IMAGE_DIMENSION * scale, IMAGE_DIMENSION * scale, null);
            }
            for (int i = 0; i < tiles.get(a).getDown().size(); i++) {
                g2d.drawImage(tiles.get(a).getDown().get(i).getImage(), 0 * IMAGE_DIMENSION * scale + (500 * a), i * IMAGE_DIMENSION * scale + (IMAGE_DIMENSION * scale), IMAGE_DIMENSION * scale, IMAGE_DIMENSION * scale, null);
            }
            for (int i = 0; i < tiles.get(a).getLeft().size(); i++) {
                g2d.drawImage(tiles.get(a).getLeft().get(i).getImage(), -i * IMAGE_DIMENSION * scale - (IMAGE_DIMENSION * scale) + (500 * a), 0 * IMAGE_DIMENSION * scale, IMAGE_DIMENSION * scale, IMAGE_DIMENSION * scale, null);
            }
        }
    }

    public void drawAllTiles(Graphics2D g2d) {
        for (int i = 0; i < tiles.size(); i++) {
            g2d.drawImage(tiles.get(i).getImage(), i * IMAGE_DIMENSION * scale, 0 * IMAGE_DIMENSION * scale, IMAGE_DIMENSION * scale, IMAGE_DIMENSION * scale, null);
        }
    }

    double updatetime = 0;

    public void update() {
        if (cellQueue.isEmpty()) {
            Cell c = findCellWithLowestEntropy(cells);
            if (c.isCollapse() || c.getOptions().isEmpty()) {
//                running = false;
                restart();
            }
            c.collapse(tiles.get(c.getOptions().get(r.nextInt(c.getOptions().size()))));
            resetCells();
            cellQueue.add(c);
        }
        if (running) {
            if (updateType) {
                propagate();
            } else {
                stepByStepPropagate(cellQueue);
            }
        }
    }
    Queue<Cell> cellQueue = new LinkedList<>();

    public void propagate() {
        while (!cellQueue.isEmpty()) {
            stepByStepPropagate(cellQueue);
        }
    }

    public void stepByStepPropagate(Queue<Cell> queue) {
        Cell c = queue.poll();
        int x = c.getX();
        int y = c.getY();
        Cell up = null, left = null, down = null, right = null;
        // add to the queue the cell above if it is not in queue and is not iterated
        if (y > 0) {
            up = cells[x][y - 1];
            if (!up.isInQueue() && !up.isIterate()) {
                queue.add(up);
                up.setInQueue(true);
            }
        }
        // add to the queue the cell to the right if it is not in queue and is not iterated
        if (x < cellsX - 1) {
            right = cells[x + 1][y];
            if (!right.isInQueue() && !right.isIterate()) {
                queue.add(right);
                right.setInQueue(true);
            }
        }
        // add to the queue the cell below if it is not in queue and is not iterated
        if (y < cellsY - 1) {
            down = cells[x][y + 1];
            if (!down.isInQueue() && !down.isIterate()) {
                queue.add(down);
                down.setInQueue(true);
            }
        }
        // add to the queue the cell to the left if it is not in queue and is not iterated
        if (x > 0) {
            left = cells[x - 1][y];
            if (!left.isInQueue() && !left.isIterate()) {
                queue.add(left);
                left.setInQueue(true);
            }
        }

        if (c.isCollapse() || c.isIterate()) {
            return;
        }

        ArrayList<Integer> option = c.getOptions();
        // Reduce this cell probability using neighbor cells only if they have been collapsed
        collapse(up, option, 2);
        collapse(right, option, 3);
        collapse(down, option, 0);
        collapse(left, option, 1);

        if (option.size() == 1) {
            c.collapse(tiles.get(option.get(0)));
        } else {
            c.setOptions(option);
        }
        c.setIterate(true);
        c.setInQueue(true);
    }

    public ArrayList<Integer> collapse(Cell cell, ArrayList<Integer> options, int direction) {
        if (cell != null && cell.isCollapse()) {
            for (int i = 0; i < cell.getOptions().size(); i++) {
                ArrayList<Integer> list = new ArrayList<>();
                Tile tile = tiles.get(cell.getOptions().get(i));
                ArrayList<Tile> tilesThatCanBeConnected = tile.getConnection()[direction];
                tilesThatCanBeConnected.forEach((t) -> {
                    list.add(t.getIndex());
                });
                options.retainAll(list);
            }
        }
        return options;
    }

    private Cell findCellWithLowestEntropy(Cell[][] cells) {
        Cell min = cells[r.nextInt(cellsX)][r.nextInt(cellsY)];
        for (Cell[] cell : cells) {
            for (Cell c : cell) {
                if (c.isCollapse()) {
                    continue;
                }
                if (min.isCollapse() && !c.isCollapse()) {
                    min = c;
                } else {
                    if (min.getOptions().size() > c.getOptions().size()) {
                        min = c;
                    }
                }
            }
        }
        return min;
    }

    public void resetCells() {
        for (Cell[] cell : cells) {
            for (Cell c : cell) {
                if (!c.isCollapse()) {
                    c.setInQueue(false);
                    c.setIterate(false);
                }
            }
        }
    }

    public void restart() {
        for (int i = 0; i < cellsX; i++) {
            for (int j = 0; j < cellsY; j++) {
                cells[i][j] = new Cell(tiles.size(), i, j);
            }
        }
    }
}
