package com.tmn.wavefunctioncollapse.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Cell {


    private int x, y;
    private Tile tile;
    private ArrayList<Integer> tileIndexes; // index of all the tile that can be in the cell

    private boolean collapsed;
    private boolean iterated;
    private boolean inQueued;

    public Cell(int x, int y) {
        this.tile = null;
        this.collapsed = false;
        this.iterated = false;
        this.inQueued = false;
        this.x = x;
        this.y = y;
        this.tileIndexes = new ArrayList<>();
    }

    public Cell(ArrayList<Integer> tileIndexes, int x, int y) {
        this(x, y);
        this.tileIndexes = new ArrayList<>(tileIndexes);
    }

    public Cell(int value, int x, int y) {
        this(x, y);
        for (int i = 0; i < value; i++) {
            this.tileIndexes.add(i, i);
        }
    }

    public void collapse(Tile tile) {
        this.collapsed = true;
        this.iterated = true;
        this.inQueued = true;
        this.tile = tile;
    }

    public void draw(Graphics2D g2d, int scaled_dimension, int baseXPosition, int baseYPosition) {
        if (collapsed) {
            g2d.drawImage(tile.getImage(), x * baseXPosition, y * baseYPosition, scaled_dimension, scaled_dimension, null);
        } else {
            g2d.setColor(Color.white);
            if (inQueued) {
                g2d.setColor(Color.red);
            }
            if (iterated) {
                g2d.setColor(Color.green);
            }
            int w = g2d.getFontMetrics().stringWidth(tileIndexes.size() + "");
            int h = (int) g2d.getFontMetrics().getAscent();
            int posX = x * baseXPosition + (scaled_dimension - w) / 2;
            int posY = y * baseYPosition + (scaled_dimension + h) / 2;
            g2d.drawString(tileIndexes.size() + "", posX, posY);
        }
    }

    @Override
    public String toString() {
        return "Cell{" + "x=" + x + ", y=" + y + ", collapse=" + collapsed + '}';
    }
}
