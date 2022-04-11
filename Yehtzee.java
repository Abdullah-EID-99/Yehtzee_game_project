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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class Yehtzee {

    public static boolean TWO_OF_KIND = false;
    public static final int THREE_OF_KIND = 0;
    public static final int FOUR_OF_KIND = 1;
    public static final int FULL_HOUSE = 2;
    public static final int SMALL_STRAIGHT = 3;
    public static final int LARGE_STRAIGHT = 4;
    public static final int CHANCE = 5;
    public static final int YAHTZEE = 6;
    static int[] upperSectionCombinations;
    static int[] lowerSectionCombinations;
    static int[] temp_scores;
    static int[] scores = new int[13];
    static List<String> data;

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
        data = scoreTypes;
        return scoreTypes;
    }

    public static List<Integer> calculateScores(int dices[]) {

        return null;
    }

    public static void calculateUpperSectionCombinations(int dices[]) {
        upperSectionCombinations = new int[6];
        for (int i = 0; i < dices.length; i++) {
            upperSectionCombinations[dices[i] - 1]++;
        }
    }

    public static boolean isYehtzee(int dices[]) {
        int x = dices[0];
        for (int i = 1; i < dices.length; i++) {
            if (x != dices[i]) {
                return false;
            }
        }
        return true;
    }

    public static boolean isFullHouse(int dices[]) {
        int x = dices[0];
        int y = -1;
        int counterX = 1;
        int counterY = 0;
        boolean control = true;
        for (int i = 1; i < dices.length; i++) {
            if (x == dices[i]) {
                counterX++;
            } else if (y == dices[i]) {
                counterY++;
            } else if (control) {
                y = dices[i];
                counterY++;
                control = false;
            } else {
                return false;
            }
        }
        if (Math.abs(counterX - counterY) == 1) {
            return true;
        }
        return false;
    }

    public static boolean isSmallStraight(int dices[]) {
        for (int i = 0; i <= 2; i++) {
            boolean control = true;
            for (int j = i; j <= i + 3; j++) {
                if (upperSectionCombinations[j] == 0) {
                    control = false;
                    break;
                }
            }
            if (control) {
                return true;
            }
        }
        return false;
    }

    public static boolean isLargeStraight(int dices[]) {
        Arrays.sort(dices);
        for (int i = 1; i <= 2; i++) {
            boolean control = true;
            for (int j = 0; j < dices.length; j++) {
                /*
                for i = 1 --> 1 2 3 4 5
                        j --> 0 1 2 3 4
                
                for i = 2 --> 2 3 4 5 6
                        j --> 0 1 2 3 4
                
                dices[j] should be equel to (j+i)
                 */
                if (dices[j] != j + i) {
                    control = false;
                    break;
                }
            }
            if (control) {
                return true;
            }
        }
        return false;
    }

    public static boolean fourOfKind(int dices[]) {
        calculateUpperSectionCombinations(dices);
        for (int i = 0; i < upperSectionCombinations.length; i++) {
            if (upperSectionCombinations[i] == 4) {
                return true;
            }
        }
        return false;
    }

    public static boolean threeOfKind(int dices[]) {
        calculateUpperSectionCombinations(dices);
        for (int i = 0; i < upperSectionCombinations.length; i++) {
            if (upperSectionCombinations[i] == 3) {
                return true;
            }
        }
        return false;
    }

    public static int chance(int dices[]) {
        int sum = 0;
        for (int i = 0; i < dices.length; i++) {
            sum += dices[i];
        }
        return sum;
    }

    public static void play(int dices[]) {
        lowerSectionCombinations = new int[7];
        temp_scores = new int[13];
        calculateUpperSectionCombinations(dices);
        for (int i = 0; i < upperSectionCombinations.length; i++) {
            temp_scores[i] = upperSectionCombinations[i] * (i + 1);
            
            switch (upperSectionCombinations[i]) {
                case 2:
                    if (lowerSectionCombinations[THREE_OF_KIND] == 1) {
                        lowerSectionCombinations[FULL_HOUSE] = 1;
                        temp_scores[upperSectionCombinations.length + FULL_HOUSE] = 25;
                    }
                    TWO_OF_KIND = true;
                    break;
                case 3:
                    lowerSectionCombinations[THREE_OF_KIND] = 1;
                    temp_scores[upperSectionCombinations.length + THREE_OF_KIND] = sumOfArray(dices);
                    if (TWO_OF_KIND == true) {
                        lowerSectionCombinations[FULL_HOUSE] = 1;
                        temp_scores[upperSectionCombinations.length + FULL_HOUSE] = 25;
                    }
                    TWO_OF_KIND = false;
                    break;
                case 4:
                    lowerSectionCombinations[THREE_OF_KIND] = 1;
                    lowerSectionCombinations[FOUR_OF_KIND] = 1;
                    temp_scores[upperSectionCombinations.length + THREE_OF_KIND] = sumOfArray(dices);
                    temp_scores[upperSectionCombinations.length + FOUR_OF_KIND] = sumOfArray(dices);
                    break;
                case 5:
                    lowerSectionCombinations[THREE_OF_KIND] = 1;
                    lowerSectionCombinations[FOUR_OF_KIND] = 1;
                    lowerSectionCombinations[YAHTZEE] = 1;
                    temp_scores[upperSectionCombinations.length + THREE_OF_KIND] = sumOfArray(dices);
                    temp_scores[upperSectionCombinations.length + FOUR_OF_KIND] = sumOfArray(dices);
                    temp_scores[upperSectionCombinations.length + YAHTZEE] = 50;
                    break;
                default:
                // code block

            }
            if (isLargeStraight(dices)) {
                lowerSectionCombinations[LARGE_STRAIGHT] = 1;
                lowerSectionCombinations[SMALL_STRAIGHT] = 1;
                temp_scores[upperSectionCombinations.length + LARGE_STRAIGHT] = 40;
                temp_scores[upperSectionCombinations.length + SMALL_STRAIGHT] = 30;
            } else if (isSmallStraight(dices)) {
                lowerSectionCombinations[SMALL_STRAIGHT] = 1;
                temp_scores[upperSectionCombinations.length + SMALL_STRAIGHT] = 30;
            }
            lowerSectionCombinations[CHANCE] = 1;
            temp_scores[upperSectionCombinations.length + CHANCE] = sumOfArray(dices);
        }

    }

    public static int[] rollDice(JLabel[] dices_labeles) {
        int dices[] = new int[5];
        Random rn = new Random();
        for (int i = 0; i < dices.length; i++) {
            dices[i] = rn.nextInt(6) + 1;
        }
        loadIconToImage(dices_labeles, dices);
        return dices;
    }

    public static void rollSpecificDices(int dices[], int indexOfDicesToRoll[]) {
        Random rn = new Random();
        for (int i = 0; i < indexOfDicesToRoll.length; i++) {
            dices[indexOfDicesToRoll[i]] = rn.nextInt(6) + 1;
        }
    }

    public static int sumOfArray(int arr[]) {
        int sum = 0;
        for (int i = 0; i < arr.length; i++) {
            sum += arr[i];
        }
        return sum;
    }

    public static void printArray(int arr[]) {
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
        System.out.println("");
    }

    public static void printScore() {
        List<String> temp = readFile("src/yehtzee/yeht.txt");
        for (int i = 0; i < temp_scores.length; i++) {
            System.out.println(temp.get(i) + ": " + temp_scores[i]);
        }
    }

    public static void loadIconToImage(JLabel[] dices_labeles, int dices[]) {
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
    static void startGame(){
        for (int i = 0; i < scores.length; i++) {
            scores[i] = -1;
        }
    }

}
