package com.mycompany.wavefunctioncollapse.model;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Tile {

    private Integer index;
    private BufferedImage image;
    /*
    * Egde code, indicate which tile can be connect to which faces of this tile
        index 0: up, north
        index 1: right, east
        index 2: down, south
        index 3: left, west
     */
    private String[] edges;

    // List of tile which can be used to connect to this edge of this tile
    private ArrayList<Tile> up, right, down, left;
    private ArrayList<ArrayList<Tile>> connection;

    public Tile(BufferedImage image, String[] edges) {
        this(image, edges, null);
    }

    public Tile(BufferedImage image, String[] edges, Integer index) {
        this.image = image;
        this.edges = edges;
        this.index = index;
        up = new ArrayList<>();
        right = new ArrayList<>();
        down = new ArrayList<>();
        left = new ArrayList<>();
        connection = new ArrayList<>();
        connection.add(up);
        connection.add(right);
        connection.add(down);
        connection.add(left);
    }

    public void analyzeNeighbor(ArrayList<Tile> tiles) {
        for (int i = 0; i < tiles.size(); i++) {
            Tile tile = tiles.get(i);
            // UP
            if (compareEdge(tile.edges[2], this.edges[0])) {
                this.up.add(tile);
            }
            // RIGHT
            if (compareEdge(tile.edges[3], this.edges[1])) {
                this.right.add(tile);
            }
            // DOWN
            if (compareEdge(tile.edges[0], this.edges[2])) {
                this.down.add(tile);
            }
            // LEFT
            if (compareEdge(tile.edges[1], this.edges[3])) {
                this.left.add(tile);
            }
        }
    }

    public Tile rotate(int num) {
        BufferedImage rotated = deepCopy(image, true);
        Graphics2D graphics = rotated.createGraphics();
        graphics.rotate(Math.PI * num / 2, rotated.getWidth() * 1.0 / 2, rotated.getHeight() * 1.0 / 2);
        graphics.drawImage(image, null, 0, 0);
        graphics.dispose();
        String[] newEdges = new String[this.edges.length];
        int len = this.edges.length;
        for (int i = 0; i < len; i++) {
            newEdges[i] = this.edges[(i - num + len) % len];
        }
        if (getEdgesAsString().equals(Tile.getEdgesAsString(newEdges))) {
            return null;
        }
        return new Tile(rotated, newEdges);
    }

    public static BufferedImage deepCopy(BufferedImage bi, boolean copyPixels) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.getRaster().createCompatibleWritableRaster();
        if (copyPixels) {
            bi.copyData(raster);
        }
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    private boolean compareEdge(String a, String b) {
        return new StringBuilder(b).reverse().toString().equals(a);
    }

    public String getEdgesAsString() {
        StringBuilder sb = new StringBuilder();
        for (String s : edges) {
            sb.append(s);
        }
        return sb.toString();
    }

    public static String getEdgesAsString(String[] edges) {
        StringBuilder sb = new StringBuilder();
        for (String s : edges) {
            sb.append(s);
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.getEdgesAsString());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Tile other = (Tile) obj;
        return Objects.equals(this.getEdgesAsString(), other.getEdgesAsString());
    }

}
