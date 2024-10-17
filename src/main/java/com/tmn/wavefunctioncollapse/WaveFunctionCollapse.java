package com.tmn.wavefunctioncollapse;

import javax.swing.JFrame;

/**
 *
 * @author nhat.tranminh
 */
public class WaveFunctionCollapse extends JFrame {

    public WaveFunctionCollapse() {
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setEnabled(true);
    }

    public static void main(String[] args) {
        WaveFunctionCollapse wfc = new WaveFunctionCollapse();
        Panel p = new Panel(Panel.IMAGE_DIMENSION * 40, Panel.IMAGE_DIMENSION * 40);
        wfc.add(p);
        wfc.pack();
        double delta = 0;
        double updatedelta = 0;
        double drawInterval = 1000000000 / 60;
        double updateInterval = 1000000000 / 60;
        long lastTime = System.nanoTime();
        while (wfc.isEnabled()) {
            long currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            updatedelta += (currentTime - lastTime) / updateInterval;
            lastTime = currentTime;
            if (delta >= 1) {
                p.repaint();
                delta--;
            }
            if (updatedelta >= 1) {
                long start = System.currentTimeMillis();
                for (int i = 0; i < p.runNTimes; i++) {
                    p.update();
                }
                long end = System.currentTimeMillis();
                p.updatetime = (end - start) * 1.0;
                updatedelta--;
            }
        }
    }
}
