/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Yehtzee_game_project;

import game.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

// serverdan gelecek mesajları dinleyen thread
//class Listen extends Thread {
//
//    public void run() {
//        //soket bağlı olduğu sürece dön
//        while (Client.socket.isConnected()) {
//            try {
//                //mesaj gelmesini bloking olarak dinyelen komut
//                Message received = (Message) (sInput.readObject());
//                //mesaj gelirse bu satıra geçer
//                //mesaj tipine göre yapılacak işlemi ayır.
//                switch (received.type) {
//                    case Name:
//                        break;
//                    case RivalConnected:
//                        String name = received.content.toString();
//                        Game.ThisGame.rival_name.setText(name);
////                        Game.ThisGame.txt_rival_name.setText(name);
////                        Game.ThisGame.btn_pick.setEnabled(true);
////                        Game.ThisGame.btn_send_message.setEnabled(true);
////                        Game.ThisGame.tmr_slider.start();
//                        break;
//                    case Disconnect:
//                        break;
//                    case Text:
////                        Game.ThisGame.txt_receive.setText(received.content.toString());
//                        break;
//                    case SelectedScore:
//                        int rivalSelectedScore[] = (int[]) received.content;
//                        Game.ThisGame.tableModel.setValueAt(rivalSelectedScore[1], rivalSelectedScore[0], 2);
//
//                        break;
//
//                    case Bitis:
//                        break;
//
//                }
//
//            } catch (IOException ex) {
//
//                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
//                //Client.Stop();
//                break;
//            } catch (ClassNotFoundException ex) {
//                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
//                //Client.Stop();
//                break;
//            }
//        }
//
//    }
//}
public class Client {

    //her clientın bir soketi olmalı
    public Socket socket;

    //verileri almak için gerekli nesne
    public ObjectInputStream sInput;
    //verileri göndermek için gerekli nesne
    public ObjectOutputStream sOutput;
//    //serverı dinleme thredi 
//    public Listen listenMe;

    public void Start(String ip, int port) {
        try {
            // Client Soket nesnesi
            this.socket = new Socket(ip, port);
            System.out.println("Servera bağlandı");
            // input stream
            this.sInput = new ObjectInputStream(this.socket.getInputStream());
            // output stream
            this.sOutput = new ObjectOutputStream(this.socket.getOutputStream());
//            Client.listenMe = new Listen();
//            Client.listenMe.start();

            //ilk mesaj olarak isim gönderiyorum
//            Message msg = new Message(Message.Message_Type.Name);
//            msg.content = Game.ThisGame.txt_name.getText();
//            Client.Send(msg);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //client durdurma fonksiyonu
    public void Stop() {
        try {
            if (this.socket != null) {
//                Client.listenMe.stop();
                this.sOutput.flush();
                this.sOutput.close();
                this.sInput.close();
                this.socket.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //mesaj gönderme fonksiyonu
    public void Send(Object message, Message.Message_Type mt) {
        try {
            Message msg = new Message(mt);
            msg.content = message;
            this.sOutput.writeObject(msg);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
