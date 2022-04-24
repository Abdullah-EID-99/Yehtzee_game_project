/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Yehtzee_game_project.yehtzeeserver;

import Yehtzee_game_project.game.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author INSECT
 */
public class SClient extends Thread {

    int id;
    public String name = "NoName";
    Socket soket;
    ObjectOutputStream sOutput;
    ObjectInputStream sInput;
    PairingThread myPairThread;
//    //cilent eşleştirme thredi
//    
    //rakip client
    SClient rival;
    //eşleşme durumu
    public boolean paired = false;

    public SClient(Socket gelenSoket, int id) {
        this.soket = gelenSoket;
        this.id = id;
        try {
            this.sOutput = new ObjectOutputStream(this.soket.getOutputStream());
            this.sInput = new ObjectInputStream(this.soket.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(SClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        //thread nesneleri
//        this.pairThread = new PairingThread(this);

    }

    //client mesaj gönderme
    public void Send(Message message) {
        try {
            this.sOutput.writeObject(message);
        } catch (IOException ex) {
            Logger.getLogger(SClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //client dinleme threadi
    //her clientin ayrı bir dinleme thredi var
    public void run() {
        //client bağlı olduğu sürece dönsün
        while (this.soket.isConnected()) {
            try {
                //mesajı bekleyen kod satırı
                Message received = (Message) (this.sInput.readObject());
                //mesaj gelirse bu satıra geçer
                //mesaj tipine göre işlemlere ayır
                switch (received.type) {
                    case Name:
                        this.name = received.content.toString();
                        // isim verisini gönderdikten sonra eşleştirme işlemine başla
//                            this.pairThread.start();
                        break;
                    case Disconnect:
                        this.stop();
                        this.sOutput.flush();
                        this.sOutput.close();
                        this.sInput.close();
                        this.soket.close();
//                        this.myPairThread.stop();
                        Server.clients.remove(this);
                        break;
                    case Text:
                        //gelen metni direkt rakibe gönder
                        Server.Send(this.rival, received);
                        break;
                    case SelectedScore:
                        //gelen seçim yapıldı mesajını rakibe gönder
                        Server.Send(this.rival, received);
                        break;
                    case start:
                        Server.Send(this.rival, received);
                        break;
                    case dices:
                        Server.Send(this.rival, received);
                        break;
                    case counter:
                        Server.Send(this.rival, received);
                        break;
                    case finish_1:
                        Server.Send(this.rival, received);
                        break;
                    case finish_2:
                        Server.Send(this.rival, received);
                        break;
                    case end:
                        Server.Send(this.rival, received);
                        break;
                    case replay:
                        Server.Send(this.rival, received);
                        break;
                    case acceptReplay:
                        Server.Send(this.rival, received);
                        break;
//                    case itIsYourTurn:, 
//                        Server.Send(this.rival, received);
//                        break;

                }

            } catch (IOException ex) {
                Logger.getLogger(SClient.class.getName()).log(Level.SEVERE, null, ex);
                //client bağlantısı koparsa listeden sil
                Server.clients.remove(this);

            } catch (ClassNotFoundException ex) {
                Logger.getLogger(SClient.class.getName()).log(Level.SEVERE, null, ex);
                //client bağlantısı koparsa listeden sil
                Server.clients.remove(this);
            }
        }

    }

}
