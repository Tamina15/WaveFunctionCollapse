package com.tmn.wavefunctioncollapse.model;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Tile {

    private Integer index;
    private BufferedImage image;
    /*
    * Egdes code, indicate which tile can be connect to which faces of this tile
        eg.    
        index 0: up, north
        index 1: right, east
        index 2: down, south
        index 3: left, west
     */
    private String[] edges;

    // List of tiles which can be used to connect to this edge of this tile
    // edges[i] will have neighbors[i] and inverse of that neighbor at edge[inverseNeighborsIndex[i]]
    private ArrayList<Tile>[] neighbors;

    // TODO: Read this from file
    public static final int[] inverseNeighborsIndex = new int[]{2, 3, 0, 1};

    public Tile(BufferedImage image, String[] edges) {
        this(null, image, edges);
    }

    public Tile(Integer index, BufferedImage image, String[] edges) {
        this.index = index;
        this.image = image;
        this.edges = edges;
        neighbors = new ArrayList[this.edges.length];
        for (int i = 0; i < neighbors.length; i++) {
            neighbors[i] = new ArrayList<>();
        }
    }

    // check all edges against a list of tiles 
    // to see which tiles has edges that can connect with this edge
    public void analyzeNeighbor(ArrayList<Tile> tiles) {
        // for all edges of this tile
        for (int i = 0; i < edges.length; i++) {
            // prepare the edge and the neighbor list
            String edge = edges[i];
            ArrayList<Tile> neighbor = neighbors[i];
            // match against all tiles
            for (int j = 0; j < tiles.size(); j++) {
                // get the tile
                Tile tile = tiles.get(j);
                // compare between this edge and the inverse of this edge of the outside tile
                if (compareEdge(edge, tile.getEdges()[inverseNeighborsIndex[i]])) {
                    neighbor.add(tile);
                }
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

    private static boolean compareEdge(String a, String b) {
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

    @Override
    public String toString() {
        return "Tile{" + "index=" + index + ", edges=" + Arrays.toString(edges) + '}';
    }

}
