package com.tmn.wavefunctioncollapse.model;

import java.awt.Color;
import java.awt.Graphics2D;
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

    public void draw(Graphics2D g2d, int scaled_dimension, int baseXPosition, int baseYPosition) {
        if (collapse) {
            g2d.drawImage(tile.getImage(), x * baseXPosition, y * baseYPosition, scaled_dimension, scaled_dimension, null);
        } else {
            g2d.setColor(Color.white);
            if (inQueue) {
                g2d.setColor(Color.red);
            }
            if (iterate) {
                g2d.setColor(Color.green);
            }
            int w = g2d.getFontMetrics().stringWidth(options.size() + "");
            int h = (int) g2d.getFontMetrics().getAscent();
            g2d.drawString(options.size() + "", x * baseXPosition + (scaled_dimension - w) / 2, y * baseYPosition + (scaled_dimension + h) / 2);
            g2d.setColor(Color.yellow);
        }
    }

    @Override
    public String toString() {
        return "Cell{" + "x=" + x + ", y=" + y + ", collapse=" + collapse + ", tile=" + tile + '}';
    }
}
