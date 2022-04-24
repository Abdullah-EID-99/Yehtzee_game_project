/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Yehtzee_game_project.yehtzeeserver;

import Yehtzee_game_project.game.Message;
import java.io.IOException;
import static java.lang.Thread.sleep;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

class PairingThread extends Thread {

    SClient firstClient;

    PairingThread(SClient TheClient) {
        this.firstClient = TheClient;
    }

    public void run() {
        //client bağlı ve eşleşmemiş olduğu durumda dön
        while (firstClient.soket.isConnected() && firstClient.paired == false) {
            try {
                //lock mekanizması
                //sadece bir firstClient içeri grebilir
                //diğerleri release olana kadar bekler
                Server.pairingLock.acquire(1);

                //client eğer eşleşmemişse gir
                if (!firstClient.paired) {
                    //eşleşme sağlanana kadar dön
                    while (firstClient.rival == null && firstClient.soket.isConnected()) {
                        //liste içerisinde eş arıyor
                        for (SClient scondClient : Server.clients) {
                            if (firstClient != scondClient && scondClient.rival == null) {
                                //eşleşme sağlandı ve gerekli işaretlemeler yapıldı
                                scondClient.paired = true;
                                scondClient.rival = firstClient;
                                firstClient.rival = scondClient;
                                firstClient.paired = true;
                                System.out.println(firstClient.name+" and "+scondClient.name+" paired successfully");
                                break;
                            }
                        }
                        //sürekli dönmesin 1 saniyede bir dönsün
                        //thredi uyutuyoruz
                        sleep(500);
                    }
                    //eşleşme oldu
                    //her iki tarafada eşleşme mesajı gönder 
                    //oyunu başlat
                    Message msg1 = new Message(Message.Message_Type.RivalConnected);
                    msg1.content = firstClient.name;
                    Server.Send(firstClient.rival, msg1);

                    Message msg2 = new Message(Message.Message_Type.RivalConnected);
                    msg2.content = firstClient.rival.name;
                    Server.Send(firstClient, msg2);

                    boolean start = Math.random() > 0.5;

                    Message msg1a = new Message(Message.Message_Type.start);
                    msg1a.content = start;
                    Server.Send(firstClient.rival, msg1a);

                    Message msg2a = new Message(Message.Message_Type.start);
                    msg2a.content = !start;
                    Server.Send(firstClient, msg2a);
                }
                //lock mekanizmasını servest bırak
                //bırakılmazsa deadlock olur.
                Server.pairingLock.release(1);
            } catch (InterruptedException ex) {
                Logger.getLogger(PairingThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

class ServerThread extends Thread {

    public void run() {
        //server kapanana kadar dinle
//        ClientListControlThread clct = new ClientListControlThread();
//        clct.start();
        while (!Server.serverSocket.isClosed()) {
            try {
                System.out.println("Wait for a client...");
                // clienti bekleyen satır
                //bir firstClient gelene kadar bekler
                Socket clientSocket = Server.serverSocket.accept();
                //client gelirse bu satıra geçer
                System.out.println("A client have come...");
                //gelen firstClient soketinden bir sclient nesnesi oluştur
                //bir adet id de kendimiz verdik
                SClient nclient = new SClient(clientSocket, Server.client_id);

                Server.client_id++;
                //clienti listeye ekle.
                Server.clients.add(nclient);
                //client mesaj dinlemesini başlat
                nclient.start();
//                nclient.myPairThread = new PairingThread(nclient);
//                nclient.myPairThread.start();
                new PairingThread(nclient).start();
            } catch (IOException ex) {
                Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

public class Server {

    // Socket of the server
    public static ServerSocket serverSocket;
    // firstClient id 
    public static int client_id = 0;
    // The port that the server will be liston to
    public static int port;
    // Serverı sürekli dinlemede tutacak thread nesnesi
    public static ServerThread serverMainThread;
    // list of all cleints that are connected
    public static ArrayList<SClient> clients = new ArrayList<>();

    // only one firstClient can enter pairing thread to search for a rival at the same time
    public static Semaphore pairingLock = new Semaphore(1, true);

    public static void Start(int port_number) {
        try {
            Server.port = port_number;
            Server.serverSocket = new ServerSocket(Server.port);

            Server.serverMainThread = new ServerThread();
            Server.serverMainThread.start();

        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // serverdan clietlara mesaj gönderme
    //clieti alıyor ve mesaj olluyor
    public static void Send(SClient client, Message messge) {

        try {
            client.sOutput.writeObject(messge);
        } catch (IOException ex) {
            Logger.getLogger(SClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //client gelişini dinleme threadi
    public static void main(String[] args) {
        // TODO code application logic here

        Server.Start(2000);
    }

}
