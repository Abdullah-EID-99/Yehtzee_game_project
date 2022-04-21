/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Yehtzee_game_project;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author Dell
 */
public class Utils {

    //  src/yehtzee/yeht.txt
    public static List<String> readFile(String fileName) {

        List<String> scoreTypes = new ArrayList<>();
        try {
            Scanner reader = new Scanner(new File(fileName));
            while (reader.hasNext()) {
                String nextLine = reader.nextLine();
                String scoreType = nextLine.replace("\n", "");
                scoreTypes.add(scoreType);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Yehtzee.class.getName()).log(Level.SEVERE, null, ex);
        }
        return scoreTypes;
    }

    public static void loadDicesToLabels(JLabel[] dices_labeles, int dices[]) {
        Random rn = new Random();
        for (int q = 0; q < dices_labeles.length; q++) {
            BufferedImage img = null;
            try {
                img = ImageIO.read(new File("src/Yehtzee_game_project/dice_" + dices[q] + ".png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Image dimg = img.getScaledInstance(dices_labeles[q].getWidth(), dices_labeles[q].getHeight(),
                    Image.SCALE_SMOOTH);
            ImageIcon icon = new ImageIcon(dimg);
            dices_labeles[q].setIcon(icon);
        }
    }

    public static int sumOfArray(int arr[]) {
        int sum = 0;
        for (int i = 0; i < arr.length; i++) {
            sum += arr[i];
        }
        return sum;
    }

}
