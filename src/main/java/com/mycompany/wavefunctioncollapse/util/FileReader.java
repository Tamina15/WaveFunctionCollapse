package com.mycompany.wavefunctioncollapse.util;

import com.mycompany.wavefunctioncollapse.model.Tile;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class FileReader {

    public static void markImage(String folderPath, String destinationFolder) {
        if (!destinationFolder.endsWith("/")) {
            destinationFolder += "/";
        }
        if (!folderPath.endsWith("/")) {
            folderPath += "/";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(destinationFolder).append("\n");
        File folder = new File(folderPath);
        Map<Integer, Integer> map = Map.of(-16777216, 0, -16711936, 1);
        for (File file : folder.listFiles()) {
            try {
                if (!file.getName().split("\\.")[1].equals("png")) {
                    continue;
                }
                BufferedImage image;
                image = ImageIO.read(file);
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
            } catch (IOException ex) {
            }
        }
        File out = new File(destinationFolder + "data.txt");
        FileWriter fw;
        try {
            fw = new FileWriter(out);
            fw.write(sb.toString().strip());
            fw.flush();
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(FileReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static ArrayList<Tile> readImageData(String filePath) {
        ArrayList<String[]> imagesData = new ArrayList<>();
        ArrayList<Tile> tiles = new ArrayList<>();
        try {
            File file = new File(filePath);
            try (Scanner sc = new Scanner(file)) {
                String folder = sc.nextLine();
                if (!folder.endsWith("/")) {
                    folder += "/";
                }
                while (sc.hasNext()) {
                    String name = sc.nextLine();
                    String options = sc.nextLine();
                    String[] array = new String[]{name, options};
                    imagesData.add(array);
                }
                for (int i = 0; i < imagesData.size(); i++) {
                    String[] string = imagesData.get(i);
                    String imageName = string[0];
                    String[] tileOptions = string[1].split(",");
                    BufferedImage image = ImageIO.read(new File(folder + imageName));
                    tiles.add(i, new Tile(image, tileOptions, i));
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tiles;
    }

    public static void main(String[] args) {
        markImage("src/main/resources/test/small/", "src/main/resources/test/small");
        readImageData("src/main/resources/test/small/data.txt");
    }
}
