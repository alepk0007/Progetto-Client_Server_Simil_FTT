# Progetto Simil-FTP Java

Applicazione Client-Server per il trasferimento file su architettura TCP/IP.
Progetto di TEPSIT - Classe 5ª A

## Gruppo di Lavoro
Mazzinghi Alessandro, Betti Paolo, Gracci Niccolò

## Descrizione
Il sistema permette a un client remoto di connettersi a un server centrale, navigare tra le directory e scaricare file. Supporta sia l'esecuzione in locale (localhost) che in rete geografica (WAN) tramite Port Forwarding.

## Requisiti
* Ambiente di sviluppo Integrato (Consigliamo Eclipse).
* Connessione di rete (per modalità WAN).

## Guida all'Avvio

### 1. Avvio del Server
1.  Importare la cartella `esercizio_FTPLatoServer` nel proprio IDE (Eclipse/IntelliJ).
2.  Aprire il file `MainServerFTP.java`.
3.  Eseguire l'applicazione.
4.  Il server mostrerà il messaggio: `Server FTP Aperto sulla porta 5500`.

### 2. Avvio del Client
1.  Importare la cartella `esercizio_FTPLatoClient`.
2.  Aprire il file `ClientMainFTP.java`.
3.  **Configurazione IP:**
    * Per test locale: lasciare `127.0.0.1`.
    * Per test remoto: inserire l'IP Pubblico del server (es. `79.20.xxx.xxx`).
4.  Eseguire l'applicazione.

## Comandi Disponibili
Una volta connessi, digitare nella console:
*  LS  : Mostra l'elenco dei file nella cartella corrente del server.
*  GET <nomefile>` : Scarica il file specificato (es. `GET foto.jpg`).
*  CD <nome_cartella> : Cambia la directory di lavoro sul server (usa .. per tornare indietro).
*  QUIT  : Chiude la connessione.
