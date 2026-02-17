// Mazzinghi-Betti-Gracci
package PROGETTO.TEPSIT;

import java.io.*;
import java.net.Socket;

public class ThreadConnessioneFTP implements Runnable {
    private Socket client;
    private DataInputStream in;
    private DataOutputStream out;
    
    // Variabile di stato: ricorda in quale cartella si trova QUESTO specifico client
    private File directoryCorrente;

    public ThreadConnessioneFTP(Socket client) throws IOException {
        this.client = client;
        this.in = new DataInputStream(client.getInputStream());
        this.out = new DataOutputStream(client.getOutputStream());
        // All'avvio, imposto la directory di lavoro sulla cartella del progetto Java
        this.directoryCorrente = new File(System.getProperty("user.dir"));
    }

    // Metodo eseguito quando parte il Thread (t.start())
    public void run() {
        try {
            while (true) {
                // Attesa passiva di un comando dal client
                String comandoCompleto = in.readUTF(); 
                
                // Parsing: separo il comando (es. "GET") dall'argomento (es. "foto.jpg")
                String[] parti = comandoCompleto.split(" ", 2);
                String comando = parti[0].toUpperCase(); // Normalizzo in maiuscolo

                // --- GESTIONE COMANDI ---
                
                if (comando.equals("LS")) {
                    inviaLista(); // Elenca file
                } 
                else if (comando.equals("GET")) {
                    if (parti.length > 1) {
                        inviaFile(parti[1]); // Invia file binario
                    } else {
                        out.writeUTF("ERRORE: Specifica un file");
                    }
                } 
                else if (comando.equals("CD")) {
                    if (parti.length > 1) {
                        cambiaDirectory(parti[1]); // Cambia cartella
                    } else {
                        out.writeUTF("ERRORE: Specifica una directory (es: CD nomecartella o CD ..)");
                    }
                } 
                else if (comando.equals("QUIT")) {
                    break; // Esce dal ciclo while
                } 
                // --- GESTIONE ERRORI INPUT ---
                else {
                    // Feedback all'utente se sbaglia a digitare
                    out.writeUTF("Comando '" + comando + "' non riconosciuto. Riprova (LS, GET, CD, QUIT).");
                }
            }
            client.close(); // Chiusura socket alla fine della sessione
        } catch (IOException e) {
            System.out.println("Connessione terminata.");
        }
    }
    
    

    // Crea una stringa con l'elenco dei file e la invia al client
    private void inviaLista() throws IOException {
        File[] lista = directoryCorrente.listFiles();
        StringBuilder sb = new StringBuilder("Lista file in " + directoryCorrente.getName() + ":\n");
        if (lista != null) {
            for (File f : lista) {
                // Distinguo visivamente tra file e cartelle
                sb.append(f.isDirectory() ? "[DIR] " : "[FILE] ").append(f.getName()).append("\n");
            }
        }
        out.writeUTF(sb.toString());
    }
    
    
    

    // Logica di invio file (lato Server)
    private void inviaFile(String nomeFile) throws IOException {
        File f = new File(directoryCorrente, nomeFile);
        
        // Controllo se il file esiste davvero
        if (f.exists() && f.isFile()) {
            out.writeUTF("OK");
            // Fondamentale: invio la dimensione prima dei dati per sincronizzare il client
            out.writeLong(f.length()); 

            // Apertura stream di lettura dal disco
            try (FileInputStream fis = new FileInputStream(f)) {
                byte[] buffer = new byte[4096];
                int letti;
                // Leggo dal disco e invio sulla rete a blocchi di 4KB
                while ((letti = fis.read(buffer)) != -1) {
                    out.write(buffer, 0, letti);
                }
            }
        } else {
            out.writeUTF("ERRORE: File non trovato");
        }
    }
    
    

    // Nuova funzione per la navigazione nelle cartelle
    private void cambiaDirectory(String nomeDir) throws IOException {
        File nuovaDir;

        // Gestione comando ".." (Directory padre)
        if (nomeDir.equals("..")) {
            nuovaDir = directoryCorrente.getParentFile();
            if (nuovaDir == null) {
                out.writeUTF("ERRORE: Sei già nella directory radice.");
                return;
            }
        } else {
            // Costruisco il percorso della nuova sottocartella
            nuovaDir = new File(directoryCorrente, nomeDir);
        }

        // Verifica validità e aggiornamento stato
        if (nuovaDir.exists() && nuovaDir.isDirectory()) {
            // .getCanonicalFile() pulisce il percorso (toglie i ../..)
            directoryCorrente = nuovaDir.getCanonicalFile(); 
            out.writeUTF("Directory corrente cambiata in: " + directoryCorrente.getName());
        } else {
            out.writeUTF("ERRORE: Directory non trovata o non valida.");
        }
    }
}