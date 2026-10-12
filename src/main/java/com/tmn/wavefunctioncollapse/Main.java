package com.tmn.wavefunctioncollapse;

import javax.swing.JFrame;

public class Main {

    static int n = 50;

    public static void main(String[] args) {
        Frame f = new Frame();
        WaveFunctionCollapse wfc = new WaveFunctionCollapse(n, n, 0, 0);
        Panel p = new Panel(WaveFunctionCollapse.IMAGE_DIMENSION * n, WaveFunctionCollapse.IMAGE_DIMENSION * n);
        p.setWaveFunctionCollapse(wfc);
        f.add(p);
        f.pack();
        f.setExtendedState(JFrame.MAXIMIZED_BOTH);
        double delta = 0;
        double updatedelta = 0;
        double drawInterval = 1000000000 / 60;
        double updateInterval = 1000000000 / 60;
        long lastTime = System.nanoTime();
        int FPSCount = 0;

        while (f.isEnabled()) {
            long currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            updatedelta += (currentTime - lastTime) / updateInterval;
            lastTime = currentTime;
            FPSCount++;
            if (delta >= 1) {
                p.updateTime = FPSCount;
                FPSCount = 0;
                f.repaint();
                delta--;
            }
            if (updatedelta >= 1) {
                for (int i = 0; i < wfc.getRunNTimes(); i++) {
                    wfc.update();
                }
//                p.updateTime = delta;
                updatedelta--;
            }
        }
    }
}
