package com.mycompany.wavefunctioncollapse.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import javax.imageio.ImageIO;

public class Test {

    public static void genImage() throws IOException {
        for (int i = 0; i < 512; i++) {
            File file = new File("src/main/resources/test/small/" + i + ".png");
            BufferedImage image = new BufferedImage(3, 3, BufferedImage.TYPE_INT_RGB);
            for (int j = 0; j < 9; j++) {
                int c = (i >> j) & 1;
                int color;
                if (c == 1) {
                    color = Color.GREEN.getRGB();
                } else {
                    color = Color.BLACK.getRGB();
                }
                int x = j / 3;
                int y = j % 3;
                image.setRGB(x, y, color);
            }
            ImageIO.write(image, "png", file);
        }
    }

    public static void scaleImage() throws IOException {
        for (int i = 0; i < 512; i++) {
            File file = new File("src/main/resources/test/small/" + i + ".png");
            BufferedImage image = ImageIO.read(file);
            BufferedImage newImage = new BufferedImage(30, 30, image.getType());
            Graphics2D g = newImage.createGraphics();
            g.scale(10, 10);
            g.drawImage(image, null, 0, 0);
            File out = new File("src/main/resources/test/big/" + i + ".png");
            ImageIO.write(newImage, "png", out);
        }
    }

    public static void scaleDownImage() throws IOException {
        File folder = new File("src/main/resources/test/big");
        for (File file : folder.listFiles()) {
            BufferedImage image = ImageIO.read(file);
            BufferedImage newImage = new BufferedImage(3, 3, image.getType());
            Graphics2D g = newImage.createGraphics();
            g.scale(0.1, 0.1);
            g.drawImage(image, null, 0, 0);
            File out = new File("src/main/resources/test/small/" + file.getName());
            ImageIO.write(newImage, "png", out);
        }
    }

    public static void markImage() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("src/main/resources/test/small/\n");
        sb.append("16\n");
        File folder = new File("src/main/resources/test/small");
        Map<Integer, Integer> map = Map.of(-16777216, 0, -16711936, 1);
        for (File file : folder.listFiles()) {
            if (!file.getName().split("\\.")[1].equals("png")) {
                continue;
            }
            BufferedImage image = ImageIO.read(file);
            int top_left = image.getRGB(0, 0);
            int top_middle = image.getRGB(0, 1);
            int top_right = image.getRGB(0, 2);
            int middle_left = image.getRGB(1, 0);
            int middle_right = image.getRGB(1, 2);
            int bottom_left = image.getRGB(2, 0);
            int bottom_middle = image.getRGB(2, 1);
            int bottom_right = image.getRGB(2, 2);
            sb.append(file.getName()).append("\n");
            sb.append(map.get(top_left)).append(map.get(top_middle)).append(map.get(top_right)).append(",");
            sb.append(map.get(top_right)).append(map.get(middle_right)).append(map.get(bottom_right)).append(",");
            sb.append(map.get(bottom_right)).append(map.get(bottom_middle)).append(map.get(bottom_left)).append(",");
            sb.append(map.get(bottom_left)).append(map.get(middle_left)).append(map.get(top_left));
            sb.append("\n");
        }
        System.out.println(sb.toString());
        File out = new File("src/main/resources/test/small/data.txt");
        try (FileWriter fw = new FileWriter(out)) {
            fw.write(sb.toString());
            fw.flush();
        }
    }

    public static void main(String[] args) throws IOException {
        markImage();
    }
}
