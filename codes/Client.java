/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Yehtzee_game_project.codes;


import Yehtzee_game_project.game.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {

    //her clientın bir soketi olmalı
    public Socket socket;
    public String myName;
    public String myRivalName;
    //verileri almak için gerekli nesne
    public ObjectInputStream sInput;
    //verileri göndermek için gerekli nesne
    public ObjectOutputStream sOutput;
//    //serverı dinleme thredi 

    public void Start(String ip, int port) {
        try {
            // Client Soket nesnesi
            this.socket = new Socket(ip, port);
            System.out.println("Servera bağlandı");
            // input stream
            this.sInput = new ObjectInputStream(this.socket.getInputStream());
            // output stream
            this.sOutput = new ObjectOutputStream(this.socket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //client durdurma fonksiyonu
    public void Stop() {
        try {
            if (this.socket != null) {
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
