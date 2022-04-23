/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Yehtzee_game_project;

import static game.Message.Message_Type.dices;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Dell
 */
public class YehtzeeGame {
    // tabel model üzerinde puanlar yazılacak  
    private DefaultTableModel tableModel;
    // Labels üzerinde zar taşlar yazılacak
    private JLabel[] Labels;

    public YehtzeeGame(DefaultTableModel tableModel, JLabel[] middleLabeles) {
        // attığımız zar taşların numaralarını tutacak (1,2,3,4,5,6) olabilir
        this.dices = new int[5];
        // upper section combinations 
        this.upperSectionCombinations = new int[6];
        // lower section combinations 
        this.lowerSectionCombinations = new int[7];
        // geçici olarak hesapladığımız puanları tutacak
        this.temp_scores = new int[13];
        // oyuncunun kalıcı olarak seçtiği puanları tutacak
        this.scores = new int[13];
        this.Labels = middleLabeles;
        this.tableModel = tableModel;
        for (int i = 0; i < this.scores.length; i++) {
            // bütün puanlar en başta -1,kullancı puan seçinde değişir
            this.scores[i] = -1;
        }
    }

    private final int THREE_OF_KIND = 0;
    private final int FOUR_OF_KIND = 1;
    private final int FULL_HOUSE = 2;
    private final int SMALL_STRAIGHT = 3;
    private final int LARGE_STRAIGHT = 4;
    private final int CHANCE = 5;
    private final int YAHTZEE = 6;
    private int[] upperSectionCombinations;
    private int[] lowerSectionCombinations;
    private int[] temp_scores;
    private int[] scores;
    private int[] dices;
    // upper section combinations hesaplayan metod
    public void calculateUpperSectionCombinations() {
        Utils.clearArray(upperSectionCombinations);
        for (int i = 0; i < dices.length; i++) {
            upperSectionCombinations[dices[i] - 1]++;
        }
    }
    // atılan zar taşların yehtzee olup olmadığnı döndüren metod
    public boolean isYehtzee() {
        int x = dices[0];
        for (int i = 1; i < dices.length; i++) {
            if (x != dices[i]) {
                return false;
            }
        }
        return true;
    }
    // atılan zar taşların FullHouse olup olmadığnı döndüren metod
    public boolean isFullHouse() {
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
    // atılan zar taşların SmallStraight olup olmadığnı döndüren metod
    public boolean isSmallStraight() {
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
    // atılan zar taşların LargeStraight olup olmadığnı döndüren metod
    public boolean isLargeStraight() {
        int newDices[] = dices.clone();
        Arrays.sort(newDices);
        for (int i = 1; i <= 2; i++) {
            boolean control = true;
            for (int j = 0; j < newDices.length; j++) {
                /*
                for i = 1 --> 1 2 3 4 5
                        j --> 0 1 2 3 4
                
                for i = 2 --> 2 3 4 5 6
                        j --> 0 1 2 3 4
                
                dices[j] should be equel to (j+i)
                 */
                if (newDices[j] != j + i) {
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
    // atılan zar taşların içinde fourOfKind olup olmadığnı döndüren metod
    public boolean fourOfKind() {
        calculateUpperSectionCombinations();
        for (int i = 0; i < upperSectionCombinations.length; i++) {
            if (upperSectionCombinations[i] == 4) {
                return true;
            }
        }
        return false;
    }
    // atılan zar taşların içinde threeOfKind olup olmadığnı döndüren metod
    public boolean threeOfKind() {
        calculateUpperSectionCombinations();
        for (int i = 0; i < upperSectionCombinations.length; i++) {
            if (upperSectionCombinations[i] == 3) {
                return true;
            }
        }
        return false;
    }
    // atılan zar taşların değerlerini toplar
    public int chance(int dices[]) {
        int sum = 0;
        for (int i = 0; i < dices.length; i++) {
            sum += dices[i];
        }
        return sum;
    }
    // bütün puanları hesaplar
    public void calculateScores() {
        Utils.clearArray(lowerSectionCombinations);
        Utils.clearArray(temp_scores);

        calculateUpperSectionCombinations();
        for (int i = 0; i < upperSectionCombinations.length; i++) {
            temp_scores[i] = upperSectionCombinations[i] * (i + 1);

            switch (upperSectionCombinations[i]) {
                case 3:
                    temp_scores[upperSectionCombinations.length + THREE_OF_KIND] = Utils.sumOfArray(dices);
                    break;
                case 4:
                    temp_scores[upperSectionCombinations.length + THREE_OF_KIND] = Utils.sumOfArray(dices);
                    temp_scores[upperSectionCombinations.length + FOUR_OF_KIND] = Utils.sumOfArray(dices);
                    break;
                case 5:
                    temp_scores[upperSectionCombinations.length + THREE_OF_KIND] = Utils.sumOfArray(dices);
                    temp_scores[upperSectionCombinations.length + FOUR_OF_KIND] = Utils.sumOfArray(dices);
                    temp_scores[upperSectionCombinations.length + YAHTZEE] = 50;
                    break;
                default:
                // code block

            }
        }
        if (isFullHouse()) {
            temp_scores[upperSectionCombinations.length + FULL_HOUSE] = 25;

        } else if (isLargeStraight()) {
            temp_scores[upperSectionCombinations.length + LARGE_STRAIGHT] = 40;
            temp_scores[upperSectionCombinations.length + SMALL_STRAIGHT] = 30;
        } else if (isSmallStraight()) {
            temp_scores[upperSectionCombinations.length + SMALL_STRAIGHT] = 30;
        }

        temp_scores[upperSectionCombinations.length + CHANCE] = Utils.sumOfArray(dices);

    }
    // zar taşları rastgele olarak atar
    public void rollDice() {
        Random rn = new Random();
        for (int i = 0; i < dices.length; i++) {
            dices[i] = rn.nextInt(6) + 1;
        }
        Utils.loadIconsToLabels(Labels, dices);
    }
    // seçili zar taşları rastgele olarak atar diğerleri değiştirmez
    public void rollSpecificDices(int[] selectedItems) {
        Random rn = new Random();
        for (int i = 0; i < dices.length; i++) {
            if (selectedItems[i] == 1) {
                dices[i] = rn.nextInt(6) + 1;
            }
        }

        Utils.loadIconsToLabels(Labels, dices);
    }

    public void printArray(int arr[]) {
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
        System.out.println("");
    }

//    public static void printScore() {
//        List<String> temp = readFile("src/yehtzee/yeht.txt");
//        for (int i = 0; i < temp_scores.length; i++) {
//            System.out.println(temp.get(i) + ": " + temp_scores[i]);
//        }
//    }
    // bonusları hesaplar
    int getBonus() {
        int sum = 0;
        for (int i = 0; i < 6; i++) {
            sum += scores[i];
        }
        int bonus = (sum >= 30) ? 35 : 0;
        tableModel.setValueAt(bonus, 14, 1);
        return bonus;
    }
    // bonusları hesaplar bonus hariç
    int getScoresSum() {
        int sum = 0;
        for (int i = 0; i < scores.length; i++) {
            sum += scores[i];
        }
        return sum;
    }

    void reset() {
        for (int i = 0; i < scores.length; i++) {
            scores[i] = -1;
        }
    }
    // geçici olarak hesapladığımız puanları tabel modele yazar
    void writeTempScoresToTableModel() {
        for (int i = 0; i < temp_scores.length; i++) {
            if (scores[i] == -1) {
                if (temp_scores[i] / 10 == 0) {
                    tableModel.setValueAt("(" + temp_scores[i] + ")", i, 1);
                } else {
                    tableModel.setValueAt("(" + temp_scores[i] + ")", i, 1);
                }

            }
        }
    }
    // oyuncunun kalıcı olarak seçtiği puanı tabel modele yazar
    void writeScoreToTheModel(int row) {
        tableModel.setValueAt(temp_scores[row], row, 1);
        scores[row] = temp_scores[row];
        for (int i = 0; i < scores.length; i++) {
            if (scores[i] == -1) {
                tableModel.setValueAt("", i, 1);
            }
        }
    }

    public int[] getDices() {
        return dices.clone();
    }

    public int[] getScores() {
        return scores;
    }

    public int[] getTemp_scores() {
        return temp_scores;
    }

}
