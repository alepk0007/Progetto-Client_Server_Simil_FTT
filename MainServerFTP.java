// Mazzinghi-Betti-Gracci
package PROGETTO.TEPSIT;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MainServerFTP {
    public static void main(String[] args) {
        final int PORT = 5500; // Porta arbitraria sopra la 1024 per evitare conflitti di sistema
        
        try {
            // Creazione del ServerSocket: apre la "porta" per ricevere connessioni
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server FTP Aperto sulla porta " + PORT);
            
            // Ciclo infinito: il server deve rimanere sempre attivo
            while(true) {
                // .accept() è una chiamata BLOCCANTE: il programma si ferma qui finché un client non tenta di connettersi.
                Socket nuovoClient = serverSocket.accept();
                System.out.println("Nuovo client connesso per gestione file.");
                
                // MULTITHREADING:
                // Invece di gestire il client qui (che bloccherebbe gli altri),
                // creiamo un "operaio dedicato" (ThreadConnessioneFTP) e lo avviamo.
                // Così il Main torna subito su .accept() per il prossimo utente.
                Thread t = new Thread(new ThreadConnessioneFTP(nuovoClient));
                t.start();
            }
        } catch (IOException e) {
            System.out.println("Errore di avvio server");
        }
    }
}

