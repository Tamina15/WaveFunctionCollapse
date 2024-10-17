package com.tmn.wavefunctioncollapse;

import com.tmn.wavefunctioncollapse.model.Cell;
import com.tmn.wavefunctioncollapse.model.Tile;
import com.tmn.wavefunctioncollapse.util.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author nhat.tranminh
 */
@Getter
@Setter
public class WaveFunctionCollapse {

    public boolean running = true;
    public int runNTimes = 1;
    public boolean updateType;
    private final ArrayList<Tile> tiles;
    private Cell[][] cells;
    private int cellsX, cellsY;
    public static final int[] xMask = new int[]{0, 1, 0, -1};
    public static final int[] yMask = new int[]{-1, 0, 1, 0};
    private int xOverflow, yOverflow;
    static final String IMAGE_PATH = "src/main/resources/black/lines/data.txt";
    private final Random r = new Random();

    public WaveFunctionCollapse(int cellX, int cellY, int xOverflow, int yOverflow) {
        this.tiles = FileReader.readImageData(IMAGE_PATH);
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
        this.xOverflow = xOverflow;
        this.yOverflow = yOverflow;
        this.cellsX = cellX + 2 * xOverflow;
        this.cellsY = cellY + 2 * yOverflow;
        cells = new Cell[cellsX][cellsY];
        for (int i = 0; i < cellsX; i++) {
            for (int j = 0; j < cellsY; j++) {
                cells[i][j] = new Cell(tiles.size(), i, j);
            }
        }

        // cells[r.nextInt(cellsX)][r.nextInt(cellsX)].collapse(tiles.get(r.nextInt(tiles.size())));
    }

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
//                shortenStepByStepPropagate(cellQueue);
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

        if (y > 0) {
            up = cells[x][y - 1];
        }

        if (x < cellsX - 1) {
            right = cells[x + 1][y];
        }

        if (y < cellsY - 1) {
            down = cells[x][y + 1];
        }

        if (x > 0) {
            left = cells[x - 1][y];
        }

        addToQueue(queue, up, right, down, left);

        if (c.isCollapse() || c.isIterate()) {
            return;
        }

        ArrayList<Integer> option = c.getOptions();
        option = reducePossibility(option, up, 2);
        option = reducePossibility(option, right, 3);
        option = reducePossibility(option, down, 0);
        option = reducePossibility(option, left, 1);

        if (option.size() == 1) {
            c.collapse(tiles.get(option.get(0)));
        } else {
            c.setOptions(option);
        }
        c.setIterate(true);
        c.setInQueue(true);
    }

    public void shortenStepByStepPropagate(Queue<Cell> queue) {
        Cell c = queue.poll();
        int x = c.getX();
        int y = c.getY();

        Cell[] neighbors = new Cell[4];
        for (int i = 0; i < neighbors.length; i++) {
            try {
                neighbors[i] = cells[x + xMask[i]][y + yMask[i]];
            } catch (ArrayIndexOutOfBoundsException e) {
            }
        }
        Cell up = neighbors[0], right = neighbors[1], down = neighbors[2], left = neighbors[3];

        if (c.isCollapse() || c.isIterate()) {
            addToQueue(queue, up, right, down, left);
            return;
        }

        ArrayList<Integer> option = c.getOptions();
        int optionLength = option.size();
        // Reduce this cell probability using neighbor cells only if they have been collapsed
        option = reducePossibility(option, up, 2);
        option = reducePossibility(option, right, 3);
        option = reducePossibility(option, down, 0);
        option = reducePossibility(option, left, 1);

        c.setIterate(true);
        c.setInQueue(true);

        if (option.size() == 1) {
            c.collapse(tiles.get(option.get(0)));
        }
        if (option.size() == optionLength) {
            return;
        }
        addToQueue(queue, up, right, down, left);
        c.setOptions(option);
    }

    private void addToQueue(Queue<Cell> queue, Cell... cells) {
        for (Cell c : cells) {
            if (c != null && !c.isInQueue() && !c.isIterate() && !c.isCollapse()) {
                queue.add(c);
                c.setInQueue(true);
            }
        }
    }

    public ArrayList<Integer> reducePossibility(ArrayList<Integer> options, Cell cell, int direction) {
        if (cell != null && cell.isCollapse()) {
            for (int i = 0; i < cell.getOptions().size(); i++) {
                ArrayList<Integer> list = new ArrayList<>();
                Tile tile = tiles.get(cell.getOptions().get(i));
                ArrayList<Tile> tilesThatCanBeConnected = tile.getConnection().get(direction);
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

    public void reverseRunning() {
        running = !running;
    }

    public void reverseUpdateType() {
        updateType = !updateType;
    }

    public void increaseRunTimes() {
        runNTimes++;
    }

    public void decreaseRunTimes() {
        if (runNTimes > 0) {
            runNTimes--;
            return;
        }
        runNTimes = 0;
    }
}
