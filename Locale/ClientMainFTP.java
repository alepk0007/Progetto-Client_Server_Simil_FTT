// Mazzinghi-Betti-Gracci
package PROGETTO.TEPSIT;

import java.net.Socket;
import java.io.*;
import java.util.Scanner;

public class ClientMainFTP {
    public static void main(String[] args) {
        try {
        	//modificato con ip pubbico  del  router di casa di alessandro e la porta designata
        	// è stato assegnato un indirizzo statico al Server tramie l'interfaccia del  router ed èstato impostato  il PortForwarding Con protocollo TCP sulla porta 5500
            Socket socket = new Socket("79.20.112.29", 5500);
            System.out.println("Connesso al Server FTP");
            
            
            // Creazione degli stream per inviare (output) e ricevere (input) dati
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            Scanner sc = new Scanner(System.in);
            

            while (true) {
                // Interfaccia testuale per l'utente
                System.out.print("Comandi (LS, GET nomefile, CD nomedirectory, QUIT) > ");
                String cmd = sc.nextLine();
                
                // Invio immediato del comando al server
                out.writeUTF(cmd);

                if (cmd.equalsIgnoreCase("QUIT")) break; // Uscita dal ciclo

                // Gestione specifica per il download (GET)
                if (cmd.toUpperCase().startsWith("GET")) {
                    String[] parti = cmd.split(" ", 2);
                    if (parti.length > 1) {
                        // Se c'è il nome del file, avvio la procedura di ricezione
                        scaricaFile(parti[1], in);
                    }
                } else {
                    // Per tutti gli altri comandi (LS, CD, Errori), 
                    // mi limito a stampare la risposta testuale del server
                    System.out.println(in.readUTF());
                }
            }
            socket.close(); // Chiusura pulita della connessione
        } catch (IOException e) {
            System.out.println("Errore: connessione al server fallita.");
        }
    }

    // Metodo per gestire il trasferimento binario del file
    private static void scaricaFile(String nome, DataInputStream in) throws IOException {
        // 1. Leggo lo stato: il server dice se il file esiste ("OK") o no
        String stato = in.readUTF();
        
        if (stato.equals("OK")) {
            // 2. Sincronizzazione: leggo quanto è grande il file (in byte)
            long dimensione = in.readLong();
            
            // Apro un file locale per scrivere i dati in arrivo (prefisso "scaricato_")
            FileOutputStream fos = new FileOutputStream("scaricato_" + nome);
            
            byte[] buffer = new byte[4096]; // Buffer da 4KB per il trasferimento a blocchi
            int letti;
            long totaleRicevuto = 0;

            // 3. Ciclo di lettura: continuo finché non ho ricevuto TUTTI i byte promessi
            while (totaleRicevuto < dimensione && (letti = in.read(buffer)) != -1) {
                fos.write(buffer, 0, letti); // Scrivo su disco solo i byte effettivamente letti
                totaleRicevuto += letti;
            }
            fos.close();
            System.out.println("Download completato: scaricato_" + nome);
        } else {
            // Se il server ha risposto ERRORE, lo stampo
            System.out.println(stato);
        }
    }
}
