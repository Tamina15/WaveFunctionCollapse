package com.tmn.wavefunctioncollapse.model;

import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Cell {

    private boolean collapse;
    private Tile tile;
    // index of all the tile that can be in the cell
    private ArrayList<Integer> options;
    private boolean iterate;
    private boolean inQueue;
    private int x, y;

    public Cell(ArrayList<Integer> options, int x, int y) {
        this.collapse = false;
        this.options = new ArrayList<>(options);
        tile = null;
        this.x = x;
        this.y = y;
    }

    public Cell(int value, int x, int y) {
        this.collapse = false;
        this.tile = null;
        this.x = x;
        this.y = y;
        this.options = new ArrayList<>(value);
        for (int i = 0; i < value; i++) {
            this.options.add(i, i);
        }
    }

    public void collapse(Tile tile) {
        this.collapse = true;
        this.iterate = true;
        this.inQueue = true;
        this.tile = tile;
        this.options.clear();
        this.options.add(tile.getIndex());
    }

    @Override
    public String toString() {
        return "Cell{" + "collapse=" + collapse + ", tile=" + tile + ", x=" + x + ", y=" + y + '}';
    }
}
