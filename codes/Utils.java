/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Yehtzee_game_project.codes;

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
import javax.swing.JCheckBox;
import javax.swing.JLabel;

/**
 *
 * @author Dell
 */
public class Utils {

    //  dosyadan verileri okuyup bir arrayListe atar
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
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return scoreTypes;
    }
    // parametre olarak verilen dizini toplamını döndürür
    public static int sumOfArray(int arr[]) {
        int sum = 0;
        for (int i = 0; i < arr.length; i++) {
            sum += arr[i];
        }
        return sum;
    }
    // parametre olarak verilen labelların iconlarını null yapar
    static void clearLabelesIcons(JLabel labeles[]) {
        for (int q = 0; q < labeles.length; q++) {
            labeles[q].setIcon(null);
        }
    }
    // parametre olarak verilen CheckBoxlardan hangileri seçili olduğunu döndürür
    static int[] checkIfCheckBoxIsSelected(JCheckBox[] dices_checkBoxes) {
        int[] indexes = new int[dices_checkBoxes.length];
        for (int i = 0; i < dices_checkBoxes.length; i++) {
            if (dices_checkBoxes[i].isSelected()) {
                indexes[i] = 1;
            } else {
                indexes[i] = 0;
            }
        }
        return indexes;
    }
    // parametre olarak verilen CheckBoxların visible özelliğni isVisible'a göre değiştirir
    static void checkBoxesSetVisible(JCheckBox checkBoxes[], boolean isVisible) {
        for (int q = 0; q < checkBoxes.length; q++) {
            checkBoxes[q].setVisible(isVisible);
        }
    }
     // parametre olarak verilen CheckBoxların Selected özelliğni false yapar
    static void clearCheckBoxSelection(JCheckBox[] dices_checkBoxes) {
        for (JCheckBox checkBox : dices_checkBoxes) {
            checkBox.setSelected(false);
        }
    }
     // parametre olarak verilen labellara parametre olarak verilen zar numaralarına göre ikon atar
    public static void loadIconsToLabels(JLabel[] dices_labeles, int dices[]) {
        for (int q = 0; q < dices_labeles.length; q++) {
            BufferedImage img = null;
            try {
                img = ImageIO.read(new File(Utils.getPath() + "/src/Yehtzee_game_project/images/dice_" + dices[q] + ".png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Image dimg = img.getScaledInstance(dices_labeles[q].getWidth(), dices_labeles[q].getHeight(),
                    Image.SCALE_SMOOTH);
            ImageIcon icon = new ImageIcon(dimg);
            dices_labeles[q].setIcon(icon);
        }
    }
    // parametre olarak verilen labellara 2,3,4,5,6 numaralı zar ikonlarını atar
    public static void loadIconsToLabels(JLabel[] dices_labeles) {
        for (int q = 0; q < dices_labeles.length; q++) {
            BufferedImage img = null;
            try {
                img = ImageIO.read(new File(Utils.getPath() + "/src/Yehtzee_game_project/images/dice_" + (q + 2) + ".png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Image dimg = img.getScaledInstance(dices_labeles[q].getWidth(), dices_labeles[q].getHeight(),
                    Image.SCALE_SMOOTH);
            ImageIcon icon = new ImageIcon(dimg);
            dices_labeles[q].setIcon(icon);
        }
    }
    // parametre olarak verilen labelları bir array'in içine yerleştirip o array'i geriye döndürür
    public static JLabel[] putLabelsInArray(JLabel... labels) {
        int size = labels.length;
        JLabel myLabels[] = new JLabel[size];
        for (int i = 0; i < size; i++) {
            myLabels[i] = labels[i];
        }
        return myLabels;
    }
     // parametre olarak verilen CheckBoxları bir array'in içine yerleştirip o array'i geriye döndürür
    public static JCheckBox[] putJCheckBoxesInArray(JCheckBox... checkBoxes) {
        int size = checkBoxes.length;
        JCheckBox myCheckBoxes[] = new JCheckBox[size];
        for (int i = 0; i < size; i++) {
            myCheckBoxes[i] = checkBoxes[i];
        }
        return myCheckBoxes;
    }
    // parametre olarak verilen dizini tüm itemlarını 0 yapar
    public static void clearArray(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            arr[i] = 0;
        }
    }
     // parametre olarak verilen iki label dizisinin ikonlarını birbirleri ile değiştirir
    public static void changeLabelsIcons(JLabel[] source, JLabel[] Destination) {
        for (int i = 0; i < source.length; i++) {
            Destination[i].setIcon(source[i].getIcon());
        }
    }
    
    public static String getPath() {
        String currentPath = null;
        try {
            currentPath = new java.io.File(".").getCanonicalPath();
            currentPath = currentPath.substring(0, currentPath.indexOf("Yehtzee") + 7);//  C:\Users\Dell\Documents\NetBeansProjects\Yehtzee
        } catch (IOException ex) {
            Logger.getLogger(GameFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        return currentPath;
    }
}
