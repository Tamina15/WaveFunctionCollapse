package com.tmn.wavefunctioncollapse;

import static com.tmn.wavefunctioncollapse.WaveFunctionCollapse.IMAGE_DIMENSION;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Panel extends JPanel {

    private final int width, height;
    private final Point origin = new Point(0, 0);
    private final Point oldOrigin = new Point(0, 0);
    private final Point mousePt = new Point(0, 0);
    private double zoomFactor = 1, zoomMutiplier = 1.05;
    private int scale = 1;
    double updateTime = 0;
    WaveFunctionCollapse wfc;

    public Panel(int width, int height) {
        this.width = width;
        this.height = height;
        this.setPreferredSize(new Dimension(width, height));
        this.setDoubleBuffered(true);
        this.setBackground(Color.BLACK);
        this.setFocusable(true);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    setAnchorPoint(e);
                    if (e.isShiftDown()) {
                        wfc.reverseRunning();
                    }
                }
                if (SwingUtilities.isRightMouseButton(e)) {
                    if (e.isAltDown()) {
                        wfc.decreaseRunTimes();
                    } else {
                        wfc.increaseRunTimes();
                    }
                }
                if (SwingUtilities.isMiddleMouseButton(e)) {
                    wfc.reverseUpdateType();
                }
            }

        });
        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                move(e);
            }
        });
        this.addMouseWheelListener((MouseWheelEvent e) -> {
            zoom(e);
        });

        cellx = width / IMAGE_DIMENSION;
        celly = height / IMAGE_DIMENSION;

    }

    private void setAnchorPoint(MouseEvent e) {
        oldOrigin.setLocation(origin);
        mousePt.setLocation(e.getPoint());
    }

    private void move(MouseEvent e) {
        double dx = ((e.getX() - mousePt.x));
        double dy = ((e.getY() - mousePt.y));
        origin.setLocation(oldOrigin.getX() + dx, oldOrigin.getY() + dy);
    }

    private void zoom(MouseWheelEvent e) {
        // Zoom in
        if (e.getWheelRotation() < 0) {
            zoomFactor *= zoomMutiplier;
        }
        // Zoom out
        if (e.getWheelRotation() > 0) {
            zoomFactor /= zoomMutiplier;
        }
    }

    int cellx;
    int celly;

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(origin.x, origin.y);
        g2d.scale(zoomFactor, zoomFactor);

        g2d.setColor(Color.white);
        drawCell(g2d);
        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, 50, 30);
        g2d.setColor(Color.white);
        g2d.drawString(updateTime + " ms", 10, 20);
//        drawOptions(g2d);
    }

    int scaled_dimension = IMAGE_DIMENSION * scale;
    int baseXPosition;
    int baseYPosition;

    public void drawCell(Graphics2D g2d) {
        for (int i = 0; i < wfc.getCellsX(); i++) {
            for (int j = 0; j < wfc.getCellsY(); j++) {
                wfc.getCells()[i][j].draw(g2d, scaled_dimension, baseXPosition, baseYPosition);
            }
        }
    }

    public void drawOptions(Graphics2D g2d) {
        int zero_scale = 0 * scaled_dimension;
        for (int i = 0; i < wfc.getTiles().size(); i++) {
            g2d.drawImage(wfc.getTiles().get(i).getImage(), i * scaled_dimension, zero_scale,
                    scaled_dimension, scaled_dimension, null);
        }
        for (int a = 0; a < wfc.getTiles().size(); a++) {
            g2d.drawImage(wfc.getTiles().get(a).getImage(), zero_scale + (200 * a), zero_scale,
                    scaled_dimension, scaled_dimension, null);

            for (int i = 0; i < wfc.getTiles().get(a).getNeighbors()[0].size(); i++) {
                g2d.drawImage(wfc.getTiles().get(a).getNeighbors()[0].get(i).getImage(), zero_scale + (200 * a),
                        -i * scaled_dimension - (scaled_dimension), scaled_dimension,
                        scaled_dimension, null);
            }

            for (int i = 0; i < wfc.getTiles().get(a).getNeighbors()[1].size(); i++) {
                g2d.drawImage(wfc.getTiles().get(a).getNeighbors()[1].get(i).getImage(),
                        i * scaled_dimension + (scaled_dimension) + (200 * a),
                        zero_scale, scaled_dimension, scaled_dimension, null);
            }

            for (int i = 0; i < wfc.getTiles().get(a).getNeighbors()[2].size(); i++) {
                g2d.drawImage(wfc.getTiles().get(a).getNeighbors()[2].get(i).getImage(), zero_scale + (200 * a),
                        i * scaled_dimension + (scaled_dimension), scaled_dimension,
                        scaled_dimension, null);
            }

            for (int i = 0; i < wfc.getTiles().get(a).getNeighbors()[3].size(); i++) {
                g2d.drawImage(wfc.getTiles().get(a).getNeighbors()[3].get(i).getImage(),
                        -i * scaled_dimension - (scaled_dimension) + (200 * a),
                        zero_scale, scaled_dimension, scaled_dimension, null);
            }
        }
    }

    public void drawAllTiles(Graphics2D g2d) {
        for (int i = 0; i < wfc.getTiles().size(); i++) {
            g2d.drawImage(wfc.getTiles().get(i).getImage(), i * scaled_dimension, 0 * scaled_dimension,
                    scaled_dimension, scaled_dimension, null);
        }
    }

    public void drawLines(Graphics2D g2d) {
        for (int i = 0; i < cellx; i++) {
            g2d.drawLine(0, i * scaled_dimension, width, i * scaled_dimension);
        }
        for (int i = 0; i < celly; i++) {
            g2d.drawLine(i * scaled_dimension, 0, i * scaled_dimension, height);
        }
    }

    public void setWaveFunctionCollapse(WaveFunctionCollapse wfc) {
        this.wfc = wfc;
        baseXPosition = scaled_dimension - wfc.getXOverflow() * (scaled_dimension);
        baseYPosition = scaled_dimension - wfc.getYOverflow() * (scaled_dimension);
    }

}
