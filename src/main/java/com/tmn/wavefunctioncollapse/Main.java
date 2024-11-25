package com.tmn.wavefunctioncollapse;

public class Main {

    static int n = 30;

    public static void main(String[] args) {
        Frame f = new Frame();
        WaveFunctionCollapse wfc = new WaveFunctionCollapse(n, n, 0, 0);
        Panel p = new Panel(WaveFunctionCollapse.IMAGE_DIMENSION * n, WaveFunctionCollapse.IMAGE_DIMENSION * n);
        p.setWaveFunctionCollapse(wfc);
        f.add(p);
        f.pack();
        double delta = 0;
        double updatedelta = 0;
        double drawInterval = 1000000000 / 60;
        double updateInterval = 1000000000 / 60;
        long lastTime = System.nanoTime();

        while (f.isEnabled()) {
            long currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            updatedelta += (currentTime - lastTime) / updateInterval;
            lastTime = currentTime;
            if (delta >= 1) {
                f.repaint();
                delta--;
            }
            if (updatedelta >= 1) {
                long start = System.currentTimeMillis();
                for (int i = 0; i < wfc.getRunNTimes(); i++) {
                    wfc.update();
                }
                long end = System.currentTimeMillis();
                p.updatetime = (end - start);
                updatedelta--;
            }
        }
    }
}
