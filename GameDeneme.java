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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Dell
 */
public class GameDeneme extends javax.swing.JFrame {

    static int counterx = 0;
    int rollCount = 0;
    Client me;
    YehtzeeGame myGame;
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
                            int rivalSelectedScore[] = (int[]) received.content;
                            System.out.println("score " + rivalSelectedScore[1]);
                            tableModel.setValueAt(rivalSelectedScore[1], rivalSelectedScore[0], 2);
                            break;
                        case start:
                            infoTextField.setVisible(true);
                            boolean start = (boolean) received.content;
                            if (start) {
                                infoTextField.setBackground(Color.GREEN);
                                infoTextField.setText("Your turn!");
                                if (dice_1.getIcon() == null) {
                                    Utils.changeLabelsIcons(middle_labeles, myLabels);
//                                    for (int i = 0; i < myLabels.length; i++) {
//                                        myLabels[i].setIcon(middle_labeles[i].getIcon());
//                                    }
                                    Utils.clearLabeles(middle_labeles);
                                }
                                rollDice_btn.setEnabled(true);
                            } else {
                                infoTextField.setBackground(Color.RED);
                                infoTextField.setText(me.myRivalName + "'s turn!");
                                Utils.changeLabelsIcons(myLabels, rivalLabels);

//                                for (int i = 0; i < rivalLabels.length; i++) {
//                                    rivalLabels[i].setIcon(myLabels[i].getIcon());
//                                }
                                Utils.clearLabeles(myLabels);
                            }
                            break;
                        case dices:
                            int arr[] = (int[]) received.content;
                            Utils.clearLabeles(rivalLabels);
                            Utils.loadIconsToLabels(middle_labeles, arr);
                            break;
                        case counter:
                            counterx = (int) received.content;
                            Text_area_recieved_message.setText("" + counterx);
                            if (counterx == 26) {

                                me.Send(new int[]{13, myGame.getScoresSum()}, Message.Message_Type.SelectedScore);
                                me.Send(new int[]{14, myGame.getBonus()}, Message.Message_Type.SelectedScore);
                                me.Send(myGame.getScoresSum() + myGame.getBonus(), Message.Message_Type.finish_1);
                                Utils.clearLabeles(middle_labeles);
                            }
                            break;
                        case finish_1:
                            rollDice_btn.setEnabled(false);
                            me.Send(new int[]{13, myGame.getScoresSum()}, Message.Message_Type.SelectedScore);
                            me.Send(new int[]{14, myGame.getBonus()}, Message.Message_Type.SelectedScore);
                            me.Send(myGame.getScoresSum() + myGame.getBonus(), Message.Message_Type.finish_2);
                            rivalScore = (int) received.content;
                            tableModel.setValueAt(rivalScore, 15, 2);
                            tableModel.setValueAt(myGame.getScoresSum(), 13, 1);
                            tableModel.setValueAt(myGame.getScoresSum() + myGame.getBonus(), 15, 1);
                            Utils.clearLabeles(rivalLabels);

                            break;
                        case finish_2:
                            infoTextField.setVisible(false);
                            rollDice_btn.setEnabled(false);
                            rivalScore = (int) received.content;
                            tableModel.setValueAt(rivalScore, 15, 2);
                            tableModel.setValueAt(myGame.getScoresSum(), 13, 1);
                            tableModel.setValueAt(myGame.getScoresSum() + myGame.getBonus(), 15, 1);
                            boolean whoWin1 = myGame.getScoresSum() + myGame.getBonus() > rivalScore;
                            me.Send(whoWin1, Message.Message_Type.end);
                            if (whoWin1) {
                                win.setText("You won :)");
                            } else {
                                win.setForeground(Color.red);
                                win.setText(me.myRivalName + " won :(");
                            }
                            myName_label.setText(me.myName);
                            rivalNameLabel.setText(me.myRivalName);
                            myScoreLabel.setText(myGame.getScoresSum() + myGame.getBonus() + "");
                            rivalScoreLabel.setText(rivalScore + "");
                            replay_btn.setVisible(true);
                            break;
                        case end:
                            infoTextField.setVisible(false);
                            boolean whoWin2 = (boolean) received.content;
                            if (!whoWin2) {
                                win.setText("You won :)");
                            } else {
                                win.setForeground(Color.red);
                                win.setText(me.myRivalName + " won :(");
                            }
                            myName_label.setText(me.myName);
                            rivalNameLabel.setText(me.myRivalName);
                            myScoreLabel.setText(myGame.getScoresSum() + myGame.getBonus() + "");
                            rivalScoreLabel.setText(rivalScore + "");
                            replay_btn.setVisible(true);
                            break;
                        case replay:
                            if (JOptionPane.showConfirmDialog(this, me.myRivalName + " want to replay, do you want?") == JOptionPane.OK_OPTION) {
                                infoTextField.setVisible(true);
                                infoTextField.setBackground(Color.RED);
                                infoTextField.setText(me.myRivalName + "'s turn!");
                                //me.Send(true, Message.Message_Type.start);//***
                                clearScoreLabels();
                                ClearModel();
                                myGame = new YehtzeeGame(tableModel, middle_labeles);
                                me.Send(true, Message.Message_Type.acceptReplay);
                                Utils.loadIconsToLabels(rivalLabels);
                                replay_btn.setVisible(false);
                            } else {
                                me.Send(false, Message.Message_Type.acceptReplay);
                            }
                            break;
                        case acceptReplay:
                            boolean accept = (boolean) received.content;
                            if (accept) {
                                infoTextField.setVisible(true);
                                infoTextField.setBackground(Color.GREEN);
                                infoTextField.setText("Your turn!");
                                //me.Send(false, Message.Message_Type.start);//***
                                clearScoreLabels();
                                ClearModel();
                                myGame = new YehtzeeGame(tableModel, middle_labeles);
                                Utils.loadIconsToLabels(myLabels);
                                rollDice_btn.setEnabled(true);
                                replay_btn.setVisible(false);

                            } else {
                                JOptionPane.showMessageDialog(this, me.myRivalName + " do not accept your requstion!");
                            }
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

    void ClearModel() {
        counterx = 0;
        tableModel.setRowCount(0);
        List<String> scoreTypes = Utils.readFile("src/Yehtzee_game_project/yeht.txt");
        for (String scoreType : scoreTypes) {
            tableModel.addRow(new String[]{scoreType, null, null});
        }
    }

    void clearScoreLabels() {
        JLabel[] j = {myName_label, rivalNameLabel, win, myScoreLabel, rivalScoreLabel};
        for (int q = 0; q < j.length; q++) {
            j[q].setText("");
        }
    }

    void senMessage() {
        me.Send(messge_txt_field.getText(), Message.Message_Type.Text);
        Text_area_recieved_message.append("You: " + messge_txt_field.getText() + "\n");
        messge_txt_field.setText("");
    }

    public GameDeneme() {
        initComponents();
        replay_btn.setVisible(false);
        infoTextField.setVisible(false);
        myLabels = Utils.putLabelsInArray(dice_1, dice_2, dice_3, dice_4, dice_5);
        rivalLabels = Utils.putLabelsInArray(rival_dice_1, rival_dice_2, rival_dice_3, rival_dice_4, rival_dice_5);
        middle_labeles = Utils.putLabelsInArray(dice_6, dice_7, dice_8, dice_9, dice_10);
        dices_checkBoxes = Utils.putJCheckBoxesInArray(CheckBox_6, CheckBox_7, CheckBox_8, CheckBox_9, CheckBox_10);

        myGame = new YehtzeeGame(tableModel, middle_labeles);

        Utils.checkBoxesSetVisible(dices_checkBoxes, false);
        tableModel.setColumnIdentifiers(new String[]{"", "player1", "player2"});
        List<String> scoreTypes = Utils.readFile("src/Yehtzee_game_project/yeht.txt");
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
        replay_btn = new javax.swing.JButton();
        infoTextField = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(780, 580));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        scoreTable.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        scoreTable.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
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

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 10, 330, 350));

        rollDice_btn.setText("Roll Dice");
        rollDice_btn.setEnabled(false);
        rollDice_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rollDice_btnActionPerformed(evt);
            }
        });
        getContentPane().add(rollDice_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 460, 120, 40));

        dice_1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/yehtzee/die-face.png"))); // NOI18N
        getContentPane().add(dice_1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 390, 58, 55));

        dice_2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/yehtzee/die-face.png"))); // NOI18N
        getContentPane().add(dice_2, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 390, 58, 55));

        dice_4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/yehtzee/die-face.png"))); // NOI18N
        getContentPane().add(dice_4, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 390, 58, 55));

        dice_5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/yehtzee/die-face.png"))); // NOI18N
        getContentPane().add(dice_5, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 390, 58, 55));

        dice_3.setBackground(new java.awt.Color(255, 255, 255));
        dice_3.setForeground(new java.awt.Color(255, 255, 255));
        dice_3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/yehtzee/die-face.png"))); // NOI18N
        dice_3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        getContentPane().add(dice_3, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 390, 58, 55));
        getContentPane().add(dice_6, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 170, 58, 55));
        getContentPane().add(dice_7, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 170, 58, 55));
        getContentPane().add(dice_8, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 170, 58, 55));
        getContentPane().add(dice_9, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 260, 58, 55));
        getContentPane().add(dice_10, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 260, 58, 55));
        getContentPane().add(CheckBox_10, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 320, -1, -1));
        getContentPane().add(CheckBox_6, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 230, -1, -1));
        getContentPane().add(CheckBox_7, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 230, -1, -1));
        getContentPane().add(CheckBox_8, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 230, -1, -1));
        getContentPane().add(CheckBox_9, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 320, -1, -1));

        rival_name_label.setForeground(new java.awt.Color(0, 33, 212));
        getContentPane().add(rival_name_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 10, 160, 20));

        jLabel2.setText("Name: ");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 500, -1, 25));

        name_txt_field.setForeground(new java.awt.Color(0, 216, 43));
        name_txt_field.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                name_txt_fieldKeyTyped(evt);
            }
        });
        getContentPane().add(name_txt_field, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 500, 157, -1));

        start_btn.setText("Start");
        start_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                start_btnActionPerformed(evt);
            }
        });
        getContentPane().add(start_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 500, -1, -1));

        send_message_btn.setText("send ");
        send_message_btn.setEnabled(false);
        send_message_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                send_message_btnActionPerformed(evt);
            }
        });
        getContentPane().add(send_message_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 500, 80, -1));

        messge_txt_field.setEnabled(false);
        messge_txt_field.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                messge_txt_fieldKeyTyped(evt);
            }
        });
        getContentPane().add(messge_txt_field, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 500, 250, -1));

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

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 370, 330, 120));

        win.setBackground(new java.awt.Color(255, 255, 255));
        win.setFont(new java.awt.Font("Arial", 2, 24)); // NOI18N
        win.setForeground(new java.awt.Color(66, 198, 18));
        getContentPane().add(win, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 190, 180, 50));

        rivalScoreLabel.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        rivalScoreLabel.setForeground(new java.awt.Color(210, 26, 26));
        getContentPane().add(rivalScoreLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 300, 100, 30));

        myScoreLabel.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        myScoreLabel.setForeground(new java.awt.Color(61, 188, 28));
        getContentPane().add(myScoreLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 300, 100, 30));

        myName_label.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        myName_label.setForeground(new java.awt.Color(61, 188, 28));
        getContentPane().add(myName_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 260, 100, 30));

        rivalNameLabel.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        rivalNameLabel.setForeground(new java.awt.Color(210, 26, 26));
        getContentPane().add(rivalNameLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 260, 100, 30));

        jLabel1.setText("Rival Name:");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, 70, 20));

        replay_btn.setText("Suggest Rematch");
        replay_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                replay_btnActionPerformed(evt);
            }
        });
        getContentPane().add(replay_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 370, 180, -1));

        infoTextField.setEditable(false);
        infoTextField.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        infoTextField.setForeground(new java.awt.Color(255, 255, 255));
        getContentPane().add(infoTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 110, 380, 40));

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
        if (me != null) {
            me.Send(null, Message.Message_Type.Disconnect);
            myThread.stop();
            me.Stop();
        }
    }//GEN-LAST:event_formWindowClosing

    private void start_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_start_btnActionPerformed
        startAndListenThead();
    }//GEN-LAST:event_start_btnActionPerformed

    private void scoreTableMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_scoreTableMousePressed
        // TODO add your handling code here:
        scoreTable.setColumnSelectionAllowed(true);
        int row = scoreTable.getSelectedRow();
        int column = scoreTable.getSelectedColumn();
        if (row != -1 && column == 1 && row < 13) {
            if (myGame.getScores()[row] == -1) {
                //if (JOptionPane.showConfirmDialog(this, "are you sure you want this score?") == JOptionPane.OK_OPTION) {
                myGame.writeScoreToTheModel(row);
                me.Send(new int[]{row, myGame.getTemp_scores()[row]}, Message.Message_Type.SelectedScore);
                rollDice_btn.setEnabled(false);
                scoreTable.setEnabled(false);
                scoreTable.clearSelection();
                Utils.changeLabelsIcons(middle_labeles, rivalLabels);
                Utils.clearLabeles(middle_labeles);
                Utils.checkBoxesSetVisible(dices_checkBoxes, false);
                Utils.clearCheckBoxSelection(dices_checkBoxes);

                counterx++;
                me.Send(counterx, Message.Message_Type.counter);
                if (counterx < 26) {
                    me.Send(true, Message.Message_Type.start);
                    infoTextField.setBackground(Color.RED);
                    infoTextField.setText(me.myRivalName + "'s turn!");
                }
                rollCount = 0;
                //}
            }
        }
    }//GEN-LAST:event_scoreTableMousePressed

    private void rollDice_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rollDice_btnActionPerformed
        // TODO add your handling code here:

        if (rollCount <= 2) {
            int[] selectedItems = Utils.checkIfCheckBoxIsSelected(dices_checkBoxes);
            int selectedCount = Utils.sumOfArray(selectedItems);
            System.out.println("selectedCount " + selectedCount);
            if (selectedCount == 0) {
                myGame.rollDice();

            } else {
                myGame.rollSpecificDices(selectedItems);
            }
            me.Send(myGame.getDices(), Message.Message_Type.dices);
            myGame.calculateScores();
            myGame.writeTempScoresToTableModel();
        }
        switch (rollCount) {
            case 0:
                scoreTable.setEnabled(true);
                Utils.checkBoxesSetVisible(dices_checkBoxes, true);
                Utils.clearLabeles(myLabels);
                infoTextField.setText("Click the dice you want to change. You have 2 throw left.");
                rollCount++;
                break;
            case 1:
                infoTextField.setText("Click the dice you want to change. You have 1 throw left.");
                rollCount++;
                break;
            case 2:
                rollDice_btn.setEnabled(false);
                Utils.checkBoxesSetVisible(dices_checkBoxes, false);
                infoTextField.setText("Select your move by clicking a cell on the scorecard.");
                Utils.clearCheckBoxSelection(dices_checkBoxes);
                break;

        }


    }//GEN-LAST:event_rollDice_btnActionPerformed

    private void send_message_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_send_message_btnActionPerformed
        // TODO add your handling code here:
        senMessage();
    }//GEN-LAST:event_send_message_btnActionPerformed

    private void messge_txt_fieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_messge_txt_fieldKeyTyped
        // TODO add your handling code here:
        if (evt.getKeyChar() == KeyEvent.VK_ENTER) {
            senMessage();
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

    private void replay_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_replay_btnActionPerformed
        // TODO add your handling code here:
        me.Send(null, Message.Message_Type.replay);
        replay_btn.setEnabled(false);
    }//GEN-LAST:event_replay_btnActionPerformed

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
            java.util.logging.Logger.getLogger(GameDeneme.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GameDeneme.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GameDeneme.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GameDeneme.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GameDeneme().setVisible(true);
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
    private javax.swing.JTextField infoTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField messge_txt_field;
    private javax.swing.JLabel myName_label;
    private javax.swing.JLabel myScoreLabel;
    private javax.swing.JTextField name_txt_field;
    private javax.swing.JButton replay_btn;
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
