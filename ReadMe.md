### 🌐 Progetto Simil-FTP (Client-Server TCP/IP in Java)

Un'applicazione distribuita basata su Socket TCP (Stream Socket) che implementa le funzionalità essenziali di un server FTP. Il sistema supporta l'esplorazione del file system remoto e il trasferimento sicuro e verificato di file binari, collaudato con successo in un ambiente WAN reale.

#### 🚀 Architettura e Funzionalità Core

* **Server Multithreaded:** Il server principale rimane in ascolto passivo sulla porta TCP `5500`. Ad ogni nuova connessione in ingresso (`accept()`), delega la sessione a un `ThreadConnessioneFTP` dedicato (che implementa `Runnable`). Questo approccio concorrente garantisce che il server rimanga sempre disponibile per accettare nuovi client, eliminando i colli di bottiglia durante i trasferimenti pesanti.


* **Trasferimento File Sincronizzato:** Per prevenire la corruzione dei dati durante il passaggio sulla rete, il comando `GET` utilizza un protocollo rigoroso: il server comunica prima l'esito della ricerca ("OK") e poi invia un intero `Long` (8 byte) che indica la dimensione esatta del file. Il file viene successivamente trasmesso a stream in blocchi da 4KB (buffer).


* **Navigazione Dinamica e Sicura:** Il server mantiene traccia dello stato della directory corrente per ciascun thread client. Risolve dinamicamente i percorsi (incluso l'arretramento tramite `..`) affidandosi a `getCanonicalFile()`, normalizzando il percorso e confermando il cambio di stato.



#### 📡 Infrastruttura di Rete (Deployment WAN)

Oltre all'esecuzione nativa in `localhost` (`127.0.0.1`), il sistema è stato progettato e testato per l'accesso geografico attraversando l'architettura NAT di un router.

* **Port Forwarding (DNAT):** Il traffico TCP in ingresso sull'IP pubblico WAN verso la porta `5500` viene instradato in modo statico all'indirizzo IP LAN privato del server interno (`192.168.1.242`).


* **Gestione Firewall:** È stata predisposta una specifica regola in ingresso (Inbound Rule) sul Windows Defender Firewall del server per autorizzare il traffico TCP esterno sulla porta in ascolto.



#### 🔤 Specifiche del Protocollo Applicativo

| Comando Inviato (UTF) | Azione Lato Server | Risposta / Flusso Dati |
| --- | --- | --- |
| `LS` | Lettura del file system locale alla cartella corrente. | Ritorna una stringa concatenata con l'elenco dei `[FILE]` e delle `[DIR]`.

 |
| `CD <cartella>` | Navigazione nelle directory (supporta percorso relativo `..`). | Conferma la nuova directory di lavoro o restituisce stringa di errore se inesistente.

 |
| `GET <nomefile>` | Avvia il processo binario di scaricamento verso il client locale. | Stato `"OK"` → Dimensione File (Long) → Flusso Byte a blocchi da 4KB.

 |
| `QUIT` | Interruzione volontaria della sessione remota. | Chiusura pulita del socket di connessione (`client.close()`).



