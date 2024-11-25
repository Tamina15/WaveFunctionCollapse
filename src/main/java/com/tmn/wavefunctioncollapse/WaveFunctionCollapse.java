package com.tmn.wavefunctioncollapse;

import com.tmn.wavefunctioncollapse.model.Cell;
import com.tmn.wavefunctioncollapse.model.Tile;
import com.tmn.wavefunctioncollapse.util.FileReader;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
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

    public static final int IMAGE_DIMENSION = 15;
    public static final String IMAGE_PATH = "src/main/resources/black/lines/data.txt";

    private final Random r = new Random();

    public WaveFunctionCollapse(int cellX, int cellY, int xOverflow, int yOverflow) {
        this.tiles = ImageReader.readImageData(IMAGE_PATH);

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

        // Analyze all tiles for possible neighbor tiles
        tiles.forEach((tile) -> tile.analyzeNeighbor(tiles));

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

//        cells[1][1].collapse(tiles.get(0));
//        cellQueue.add(cells[1][1]);
    }

    public void update() {
        if (cellQueue.isEmpty()) {
            Cell cell = findCellWithLowestEntropy(cells);
            if (cell.isCollapsed() || cell.getTileIndexes().isEmpty()) {
                running = false;
                return;
            }
            cell.collapse(tiles.get(cell.getTileIndexes().get(r.nextInt(cell.getTileIndexes().size()))));
            resetCells();
            cellQueue.add(cell);
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

    public void fullWavePropagate() {
        int n = cellQueue.size();
        while (n > 0) {
            stepByStepPropagate(cellQueue);
            n--;
        }
    }

    public void stepByStepPropagate(Queue<Cell> queue) {
        Cell cell = queue.poll();
        Cell[] neighbors = getNeighborCells(cell);
        addToQueue(queue, neighbors);
        if (cell.isCollapsed() || cell.isIterated()) {
            return;
        }

        /* 
        This could cause problem with restrained knot tileset
        See BEFORE section: https://github.com/mxgmn/WaveFunctionCollapse?tab=readme-ov-file#higher-dimensions
        Issue: Backtracking when stuck
        */
        if (cell.getTileIndexes().size() == 1) {
            cell.collapse(tiles.get(cell.getTileIndexes().get(0)));
        } else {
            reducePossibility(cell, neighbors);
        }
        cell.setIterated(true);
        cell.setInQueued(true);
    }

    public void shortenStepByStepPropagate(Queue<Cell> queue) {
        Cell cell = queue.poll();
        Cell[] neighbors = getNeighborCells(cell);
        if (cell.isCollapsed() || cell.isIterated()) {
            addToQueue(queue, neighbors);
            return;
        }
        ArrayList<Integer> option = cell.getTileIndexes();
        int optionLength = option.size();
        reducePossibility(cell, neighbors);

        cell.setIterated(true);
        cell.setInQueued(true);

        if (option.size() == 1) {
            cell.collapse(tiles.get(option.get(0)));
        }
        if (option.size() == optionLength) {
            return;
        }
        addToQueue(queue, neighbors);
        cell.setTileIndexes(option);
    }

    /* TODO: use XMask and YMask to get neighbor cells
            Cell[] neighbors = new Cell[4];
        for (int i = 0; i < neighbors.length; i++) {
            try {
                neighbors[i] = cells[x + xMask[i]][y + yMask[i]];
            } catch (ArrayIndexOutOfBoundsException e) {
            }
        }
     */
    private Cell[] getNeighborCells(Cell cell) {
        int x = cell.getX();
        int y = cell.getY();
        /*
        Cell up    = y > 0          ? cells[x]    [y - 1] : null;
        Cell right = x < cellsX - 1 ? cells[x + 1][y]     : null;
        Cell down  = y < cellsY - 1 ? cells[x]    [y + 1] : null;
        Cell left  = x > 0          ? cells[x - 1][y]     : null;
         */
        Cell up = y > 0 ? cells[x][y - 1] : null;
        Cell right = x < cellsX - 1 ? cells[x + 1][y] : null;
        Cell down = y < cellsY - 1 ? cells[x][y + 1] : null;
        Cell left = x > 0 ? cells[x - 1][y] : null;
        return new Cell[]{up, right, down, left};
    }

    private void addToQueue(Queue<Cell> queue, Cell... cells) {
        for (Cell c : cells) {
            if (c != null && !c.isInQueued() && !c.isIterated() && !c.isCollapsed()) {
                queue.add(c);
                c.setInQueued(true);
            }
        }
    }

    public void reducePossibility(Cell thisCell, Cell[] neighbors) {
        ArrayList<Integer> tileIndexes = thisCell.getTileIndexes();
        for (int i = 0; i < neighbors.length; i++) {
            Cell neighbor = neighbors[i];
            if (neighbor != null && neighbor.isCollapsed()) {
                ArrayList<Integer> list = new ArrayList<>();
                Tile tile = neighbor.getTile();
                ArrayList<Tile> tilesThatCanBeConnected = tile.getNeighbors()[Tile.inverseNeighborsIndex[i]];
                tilesThatCanBeConnected.forEach((t) -> {
                    list.add(t.getIndex());
                });
                tileIndexes.retainAll(list);
            }
        }
    }

    private Cell findCellWithLowestEntropy(Cell[][] cells) {
        Cell min = cells[r.nextInt(cellsX)][r.nextInt(cellsY)];
        for (Cell[] cell : cells) {
            for (Cell c : cell) {
                if (c.isCollapsed()) {
                    continue;
                }
                if (min.isCollapsed() && !c.isCollapsed()) {
                    min = c;
                } else {
                    if (min.getTileIndexes().size() > c.getTileIndexes().size()) {
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
                if (!c.isCollapsed()) {
                    c.setInQueued(false);
                    c.setIterated(false);
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
        runNTimes = runNTimes > 0 ? runNTimes - 1 : 0;
    }

}

class ImageReader {

    public static ArrayList<Tile> readImageData(String filePath) {
        ArrayList<Tile> tiles = new ArrayList<>();
        try {
            File file = new File(filePath);
            try (Scanner sc = new Scanner(file)) {
                String imageSourceFolder = sc.nextLine();
                if (!imageSourceFolder.endsWith("/")) {
                    imageSourceFolder += "/";
                }
                for (int i = 0; sc.hasNext(); i++) {
                    String imageName = sc.nextLine();
                    String[] tileOptions = sc.nextLine().split(",");
                    BufferedImage image = ImageIO.read(new File(imageSourceFolder + imageName));
                    tiles.add(i, new Tile(i, image, tileOptions));
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tiles;
    }
}
