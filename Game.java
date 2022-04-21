/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Yehtzee_game_project;

import com.sun.glass.events.KeyEvent;
import game.Message;
import java.awt.Color;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Dell
 */
public class Game extends javax.swing.JFrame {

    static int counterx = 0;
    int rollCount = 0;
    Client me;
    JLabel[] myLabels = new JLabel[5];
    JLabel[] middle_labeles = new JLabel[5];
    JLabel[] rivalLabels = new JLabel[5];
    JCheckBox dices_checkBoxes[] = new JCheckBox[5];
    DefaultTableModel tableModel = new DefaultTableModel();

    void startAndListenThead() {
        myThread = new Thread(() -> {
            rival_name_label.setText("waiting for rival ...");
            me = new Client();
            //me.Start("54.175.188.155", 2000);
            me.Start("localhost", 2000);
            me.Send(name_txt_field.getText(), Message.Message_Type.Name);
            me.myName = name_txt_field.getText();
            start_btn.setEnabled(false);
            name_txt_field.setEditable(false);
            while (me.socket.isConnected()) {
                try {
                    //mesaj gelmesini bloking olarak dinyelen komut
                    Message received = (Message) (me.sInput.readObject());
                    //mesaj gelirse bu satıra geçer
                    //mesaj tipine göre yapılacak işlemi ayır.
                    switch (received.type) {
                        case Name:
                            break;
                        case RivalConnected:
                            String rivalName = received.content.toString();
                            me.myRivalName = rivalName;
                            rival_name_label.setText(rivalName);
                            tableModel.setColumnIdentifiers(new String[]{"", name_txt_field.getText(), rivalName});
                            messge_txt_field.setEnabled(true);
                            send_message_btn.setEnabled(true);

                            break;
                        case Disconnect:
                            break;
                        case Text:
                            String d = received.content.toString();
                            Text_area_recieved_message.append(me.myRivalName + ": " + d + "\n");
                            break;
                        case SelectedScore:
                            Integer rivalSelectedScore[] = (Integer[]) received.content;
                            System.out.println("score " + rivalSelectedScore[1]);
                            tableModel.setValueAt(rivalSelectedScore[1], rivalSelectedScore[0], 2);
                            break;
                        case start:
                            boolean start = (boolean) received.content;
                            if (start) {
                                if (dice_1.getIcon() == null) {
                                    for (int i = 0; i < myLabels.length; i++) {
                                        myLabels[i].setIcon(middle_labeles[i].getIcon());
                                    }
                                    Utils.clearLabeles(middle_labeles);
                                }
                                rollDice_btn.setEnabled(true);
                            } else {
                                for (int i = 0; i < rivalLabels.length; i++) {
                                    rivalLabels[i].setIcon(myLabels[i].getIcon());
                                }
                                Utils.clearLabeles(myLabels);
                            }
                            break;
                        case dices:
                            int dices[] = (int[]) received.content;
                            Utils.clearLabeles(rivalLabels);
                            Utils.loadIconsToLabels(middle_labeles, dices);
                            break;
                        case counter:
                            counterx = (int) received.content;
                            Text_area_recieved_message.setText("" + counterx);
                            if (counterx == 26) {
                                int totalScores = Yehtzee.totalScores();
                                me.Send(totalScores, Message.Message_Type.finish_1);
                                Utils.clearLabeles(middle_labeles);
                            }
                            break;
                        case finish_1:
                            rollDice_btn.setEnabled(false);
                            myScore = Yehtzee.totalScores();
                            me.Send(myScore, Message.Message_Type.finish_2);
                            rivalScore = (int) received.content;
                            tableModel.setValueAt(rivalScore, 13, 2);
                            tableModel.setValueAt(myScore, 13, 1);
                            Utils.clearLabeles(rivalLabels);

                            break;
                        case finish_2:
                            rollDice_btn.setEnabled(false);
                            rivalScore = (int) received.content;
                            myScore = Yehtzee.totalScores();
                            tableModel.setValueAt(rivalScore, 13, 2);
                            tableModel.setValueAt(Yehtzee.totalScores(), 13, 1);
                            boolean whoWin1 = myScore > rivalScore;
                            me.Send(whoWin1, Message.Message_Type.end);
                            if (whoWin1) {
                                win.setText("You won :)");
                            } else {
                                win.setForeground(Color.red);
                                win.setText(me.myRivalName + " won :(");
                            }
                            myName_label.setText(me.myName);
                            rivalNameLabel.setText(me.myRivalName);
                            myScoreLabel.setText(myScore + "");
                            rivalScoreLabel.setText(rivalScore + "");
                            break;
                        case end:
                            boolean whoWin2 = (boolean) received.content;
                            if (!whoWin2) {
                                win.setText("You won :)");
                            } else {
                                win.setForeground(Color.red);
                                win.setText(me.myRivalName + " won :(");
                            }
                            myName_label.setText(me.myName);
                            rivalNameLabel.setText(me.myRivalName);
                            myScoreLabel.setText(myScore + "");
                            rivalScoreLabel.setText(rivalScore + "");
                            break;
                    }

                } catch (IOException ex) {

                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    //Client.Stop();
                    break;
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    //Client.Stop();
                    break;
                }
            }
        });
        myThread.start();
    }

    public Game() {
        initComponents();
        Yehtzee.startGame();
        myLabels = Utils.putLabelsInArray(dice_1, dice_2, dice_3, dice_4, dice_5);
        rivalLabels = Utils.putLabelsInArray(rival_dice_1, rival_dice_2, rival_dice_3, rival_dice_4, rival_dice_5);
        middle_labeles = Utils.putLabelsInArray(dice_6, dice_7, dice_8, dice_9, dice_10);
//        myLabels[0] = dice_1;
//        myLabels[1] = dice_2;
//        myLabels[2] = dice_3;
//        myLabels[3] = dice_4;
//        myLabels[4] = dice_5;
//        rivalLabels[0] = rival_dice_1;
//        rivalLabels[1] = rival_dice_2;
//        rivalLabels[2] = rival_dice_3;
//        rivalLabels[3] = rival_dice_4;
//        rivalLabels[4] = rival_dice_5;
//        middle_labeles[0] = dice_6;
//        middle_labeles[1] = dice_7;
//        middle_labeles[2] = dice_8;
//        middle_labeles[3] = dice_9;
//        middle_labeles[4] = dice_10;
        dices_checkBoxes = Utils.putJCheckBoxesInArray(CheckBox_6, CheckBox_7, CheckBox_8, CheckBox_9, CheckBox_10);
//        dices_checkBoxes[0] = CheckBox_6;
//        dices_checkBoxes[1] = CheckBox_7;
//        dices_checkBoxes[2] = CheckBox_8;
//        dices_checkBoxes[3] = CheckBox_9;
//        dices_checkBoxes[4] = CheckBox_10;
        Utils.checkBoxesSetVisible(dices_checkBoxes, false);
        tableModel.setColumnIdentifiers(new String[]{"Type", "player1", "player2"});
        List<String> scoreTypes = Utils.readFile("src/yehtzee/yeht.txt");
        for (String scoreType : scoreTypes) {
            tableModel.addRow(new String[]{scoreType, null, null});
        }
        scoreTable.setRowHeight(20);
        scoreTable.setModel(tableModel);

        Utils.loadIconsToLabels(myLabels);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        scoreTable = new javax.swing.JTable();
        rollDice_btn = new javax.swing.JButton();
        dice_1 = new javax.swing.JLabel();
        dice_2 = new javax.swing.JLabel();
        dice_4 = new javax.swing.JLabel();
        dice_5 = new javax.swing.JLabel();
        dice_3 = new javax.swing.JLabel();
        dice_6 = new javax.swing.JLabel();
        dice_7 = new javax.swing.JLabel();
        dice_8 = new javax.swing.JLabel();
        dice_9 = new javax.swing.JLabel();
        dice_10 = new javax.swing.JLabel();
        CheckBox_10 = new javax.swing.JCheckBox();
        CheckBox_6 = new javax.swing.JCheckBox();
        CheckBox_7 = new javax.swing.JCheckBox();
        CheckBox_8 = new javax.swing.JCheckBox();
        CheckBox_9 = new javax.swing.JCheckBox();
        rival_name_label = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        name_txt_field = new javax.swing.JTextField();
        start_btn = new javax.swing.JButton();
        send_message_btn = new javax.swing.JButton();
        messge_txt_field = new javax.swing.JTextField();
        rival_dice_1 = new javax.swing.JLabel();
        rival_dice_2 = new javax.swing.JLabel();
        rival_dice_4 = new javax.swing.JLabel();
        rival_dice_5 = new javax.swing.JLabel();
        rival_dice_3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        Text_area_recieved_message = new javax.swing.JTextArea();
        win = new javax.swing.JLabel();
        rivalScoreLabel = new javax.swing.JLabel();
        myScoreLabel = new javax.swing.JLabel();
        myName_label = new javax.swing.JLabel();
        rivalNameLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(780, 580));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        scoreTable.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        scoreTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scoreTable.setEnabled(false);
        scoreTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                scoreTableMousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(scoreTable);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 10, 314, 310));

        rollDice_btn.setText("Roll Dice");
        rollDice_btn.setEnabled(false);
        rollDice_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rollDice_btnActionPerformed(evt);
            }
        });
        getContentPane().add(rollDice_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(159, 406, -1, -1));

        dice_1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/yehtzee/die-face.png"))); // NOI18N
        getContentPane().add(dice_1, new org.netbeans.lib.awtextra.AbsoluteConstraints(32, 344, 58, 55));

        dice_2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/yehtzee/die-face.png"))); // NOI18N
        getContentPane().add(dice_2, new org.netbeans.lib.awtextra.AbsoluteConstraints(108, 344, 58, 55));

        dice_4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/yehtzee/die-face.png"))); // NOI18N
        getContentPane().add(dice_4, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 344, 58, 55));

        dice_5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/yehtzee/die-face.png"))); // NOI18N
        getContentPane().add(dice_5, new org.netbeans.lib.awtextra.AbsoluteConstraints(336, 344, 58, 55));

        dice_3.setBackground(new java.awt.Color(255, 255, 255));
        dice_3.setForeground(new java.awt.Color(255, 255, 255));
        dice_3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/yehtzee/die-face.png"))); // NOI18N
        dice_3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        getContentPane().add(dice_3, new org.netbeans.lib.awtextra.AbsoluteConstraints(184, 344, 58, 55));
        getContentPane().add(dice_6, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 140, 58, 55));
        getContentPane().add(dice_7, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 140, 58, 55));
        getContentPane().add(dice_8, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 140, 58, 55));
        getContentPane().add(dice_9, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 230, 58, 55));
        getContentPane().add(dice_10, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 230, 58, 55));
        getContentPane().add(CheckBox_10, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 290, -1, -1));
        getContentPane().add(CheckBox_6, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 200, -1, -1));
        getContentPane().add(CheckBox_7, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 200, -1, -1));
        getContentPane().add(CheckBox_8, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 200, -1, -1));
        getContentPane().add(CheckBox_9, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 290, -1, -1));

        rival_name_label.setForeground(new java.awt.Color(0, 33, 212));
        getContentPane().add(rival_name_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 10, 160, 20));

        jLabel2.setText("Name: ");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 450, -1, 25));

        name_txt_field.setForeground(new java.awt.Color(0, 216, 43));
        name_txt_field.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                name_txt_fieldKeyTyped(evt);
            }
        });
        getContentPane().add(name_txt_field, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 450, 157, -1));

        start_btn.setText("Start");
        start_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                start_btnActionPerformed(evt);
            }
        });
        getContentPane().add(start_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 450, -1, -1));

        send_message_btn.setText("send ");
        send_message_btn.setEnabled(false);
        send_message_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                send_message_btnActionPerformed(evt);
            }
        });
        getContentPane().add(send_message_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 460, 70, -1));

        messge_txt_field.setEnabled(false);
        messge_txt_field.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                messge_txt_fieldKeyTyped(evt);
            }
        });
        getContentPane().add(messge_txt_field, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 460, 240, -1));

        rival_dice_1.setBackground(new java.awt.Color(255, 255, 255));
        rival_dice_1.setForeground(new java.awt.Color(255, 255, 255));
        getContentPane().add(rival_dice_1, new org.netbeans.lib.awtextra.AbsoluteConstraints(25, 45, 58, 55));
        getContentPane().add(rival_dice_2, new org.netbeans.lib.awtextra.AbsoluteConstraints(101, 45, 58, 55));
        getContentPane().add(rival_dice_4, new org.netbeans.lib.awtextra.AbsoluteConstraints(253, 45, 58, 55));
        getContentPane().add(rival_dice_5, new org.netbeans.lib.awtextra.AbsoluteConstraints(329, 45, 58, 55));

        rival_dice_3.setBackground(new java.awt.Color(255, 255, 255));
        rival_dice_3.setForeground(new java.awt.Color(255, 255, 255));
        rival_dice_3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        getContentPane().add(rival_dice_3, new org.netbeans.lib.awtextra.AbsoluteConstraints(177, 45, 58, 55));

        Text_area_recieved_message.setEditable(false);
        Text_area_recieved_message.setColumns(20);
        Text_area_recieved_message.setRows(5);
        jScrollPane2.setViewportView(Text_area_recieved_message);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 330, 320, 120));

        win.setBackground(new java.awt.Color(255, 255, 255));
        win.setFont(new java.awt.Font("Arial", 2, 24)); // NOI18N
        win.setForeground(new java.awt.Color(66, 198, 18));
        getContentPane().add(win, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 160, 180, 50));

        rivalScoreLabel.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        rivalScoreLabel.setForeground(new java.awt.Color(210, 26, 26));
        getContentPane().add(rivalScoreLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 270, 100, 30));

        myScoreLabel.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        myScoreLabel.setForeground(new java.awt.Color(61, 188, 28));
        getContentPane().add(myScoreLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 270, 100, 30));

        myName_label.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        myName_label.setForeground(new java.awt.Color(61, 188, 28));
        getContentPane().add(myName_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 230, 100, 30));

        rivalNameLabel.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        rivalNameLabel.setForeground(new java.awt.Color(210, 26, 26));
        getContentPane().add(rivalNameLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 230, 100, 30));

        jLabel1.setText("Rival Name:");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, 70, 20));

        jButton1.setText("replay");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 500, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents
//    public static Socket socket;
//
//    //verileri almak için gerekli nesne
//    public static ObjectInputStream sInput;
//    //verileri göndermek için gerekli nesne
//    public static ObjectOutputStream sOutput;
    static Thread myThread;
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        me.Send(null, Message.Message_Type.Disconnect);
        myThread.stop();
        me.Stop();
    }//GEN-LAST:event_formWindowClosing

    private void start_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_start_btnActionPerformed
        startAndListenThead();
    }//GEN-LAST:event_start_btnActionPerformed

    private void scoreTableMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_scoreTableMousePressed
        // TODO add your handling code here:
        scoreTable.setColumnSelectionAllowed(true);
        int row = scoreTable.getSelectedRow();
        int column = scoreTable.getSelectedColumn();
        if (row != -1 && column == 1 && Yehtzee.scores[row] == -1) {
            //if (JOptionPane.showConfirmDialog(this, "are you sure you want this score?") == JOptionPane.OK_OPTION) {
            tableModel.setValueAt(Yehtzee.temp_scores[row], row, 1);
            Yehtzee.scores[row] = Yehtzee.temp_scores[row];
            Integer score[] = new Integer[2];
            score[0] = row;
            score[1] = Yehtzee.temp_scores[row];
            me.Send(score, Message.Message_Type.SelectedScore);
            counterx++;
            me.Send(counterx, Message.Message_Type.counter);
            rollDice_btn.setEnabled(false);
            scoreTable.setEnabled(false);
            scoreTable.clearSelection();
            for (int i = 0; i < Yehtzee.scores.length; i++) {
                if (Yehtzee.scores[i] == -1) {
                    tableModel.setValueAt("", i, 1);
                }
            }
            for (int i = 0; i < rivalLabels.length; i++) {
                rivalLabels[i].setIcon(middle_labeles[i].getIcon());
            }
            Utils.clearLabeles(middle_labeles);
            Utils.checkBoxesSetVisible(dices_checkBoxes, false);
            rollDice_btn.setEnabled(false);
            if (counterx < 26) {

                me.Send(true, Message.Message_Type.start);
            }
            rollCount = 0;
            //}
        }
    }//GEN-LAST:event_scoreTableMousePressed
    static int temp[];
    private void rollDice_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rollDice_btnActionPerformed
        // TODO add your handling code here:
        if (rollCount == 0) {
            scoreTable.setEnabled(true);
            Utils.checkBoxesSetVisible(dices_checkBoxes, true);
            Utils.clearLabeles(myLabels);
        }
        if (rollCount <= 2) {
            int[] dices = new int[5];
            int[] selectedItems = Utils.checkIfCheckBoxIsSelected(dices_checkBoxes);
            int selectedCount = Utils.sumOfArray(selectedItems);
            System.out.println("selectedCount " + selectedCount);
            if (selectedCount == 0) {
                dices = Yehtzee.rollDice(middle_labeles);

            } else {
                dices = Yehtzee.rollDice(middle_labeles);
                for (int i = 0; i < selectedItems.length; i++) {
                    if (selectedItems[i] == 0) {
                        dices[i] = temp[i];
                    }
                }
                Utils.loadIconsToLabels(middle_labeles, dices);
            }
            temp = dices.clone();
            me.Send(dices, Message.Message_Type.dices);
            Yehtzee.calculateScores(dices);

            for (int i = 0; i < Yehtzee.temp_scores.length; i++) {
                if (Yehtzee.scores[i] == -1) {
                    if (Yehtzee.temp_scores[i] / 10 == 0) {
                        tableModel.setValueAt("(" + Yehtzee.temp_scores[i] + ")", i, 1);
                    } else {
                        tableModel.setValueAt("(" + Yehtzee.temp_scores[i] + ")", i, 1);
                    }

                }
            }

            rollCount++;
            if (rollCount == 3) {
                rollDice_btn.setEnabled(false);
                Utils.checkBoxesSetVisible(dices_checkBoxes, false);
            }
        }
        Utils.clearCheckBoxSelection(dices_checkBoxes);

    }//GEN-LAST:event_rollDice_btnActionPerformed

    private void send_message_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_send_message_btnActionPerformed
        // TODO add your handling code here:
        me.Send(messge_txt_field.getText(), Message.Message_Type.Text);
        Text_area_recieved_message.append(me.myName + ": " + messge_txt_field.getText() + "\n");
        messge_txt_field.setText("");
    }//GEN-LAST:event_send_message_btnActionPerformed

    private void messge_txt_fieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_messge_txt_fieldKeyTyped
        // TODO add your handling code here:
        if (evt.getKeyChar() == KeyEvent.VK_ENTER) {
            me.Send(messge_txt_field.getText(), Message.Message_Type.Text);
            Text_area_recieved_message.append(me.myName + ": " + messge_txt_field.getText() + "\n");
            messge_txt_field.setText("");
        }
    }//GEN-LAST:event_messge_txt_fieldKeyTyped
    static int rivalScore;
    static int myScore;
    private void name_txt_fieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_name_txt_fieldKeyTyped
        // TODO add your handling code here:
        if (evt.getKeyChar() == KeyEvent.VK_ENTER && start_btn.isEnabled()) {
            startAndListenThead();
        }
    }//GEN-LAST:event_name_txt_fieldKeyTyped

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Game.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Game.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Game.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Game.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Game().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox CheckBox_10;
    private javax.swing.JCheckBox CheckBox_6;
    private javax.swing.JCheckBox CheckBox_7;
    private javax.swing.JCheckBox CheckBox_8;
    private javax.swing.JCheckBox CheckBox_9;
    private javax.swing.JTextArea Text_area_recieved_message;
    private javax.swing.JLabel dice_1;
    private javax.swing.JLabel dice_10;
    private javax.swing.JLabel dice_2;
    private javax.swing.JLabel dice_3;
    private javax.swing.JLabel dice_4;
    private javax.swing.JLabel dice_5;
    private javax.swing.JLabel dice_6;
    private javax.swing.JLabel dice_7;
    private javax.swing.JLabel dice_8;
    private javax.swing.JLabel dice_9;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField messge_txt_field;
    private javax.swing.JLabel myName_label;
    private javax.swing.JLabel myScoreLabel;
    private javax.swing.JTextField name_txt_field;
    private javax.swing.JLabel rivalNameLabel;
    private javax.swing.JLabel rivalScoreLabel;
    private javax.swing.JLabel rival_dice_1;
    private javax.swing.JLabel rival_dice_2;
    private javax.swing.JLabel rival_dice_3;
    private javax.swing.JLabel rival_dice_4;
    private javax.swing.JLabel rival_dice_5;
    private javax.swing.JLabel rival_name_label;
    private javax.swing.JButton rollDice_btn;
    private javax.swing.JTable scoreTable;
    private javax.swing.JButton send_message_btn;
    private javax.swing.JButton start_btn;
    private javax.swing.JLabel win;
    // End of variables declaration//GEN-END:variables
}
