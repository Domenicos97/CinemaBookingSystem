# 🎬 Cinema Multisala Pro

Un'applicazione desktop sviluppata in **Java (Swing)** per la gestione di un cinema multisala. Il software permette di amministrare il catalogo dei film, simulare la prenotazione dei posti con fasce di prezzo dinamiche ed esportare i biglietti in formato **PDF**.

## ✨ Funzionalità Principali

Il sistema si divide in due modalità operative:

### 👤 Modalità Utente (Prenotazione)
- Visualizzazione del catalogo film disponibili.
- Interfaccia grafica della sala con mappa dei posti dinamica.
- **Fasce di prezzo basate sulle file**:
  - 🟦 Fila Economica (6.00 €)
  - 🟩 Fila Standard (8.50 €)
  - 🟨 Fila VIP (12.00 €)
- Centratura automatica dell'ultima fila in caso di sale con capienza asimmetrica.
- Calcolo del totale in tempo reale al checkout.
- **Generazione automatica dei biglietti in PDF** (tramite libreria OpenPDF) salvati direttamente nella root del progetto.

### 🛠️ Modalità Amministratore (Gestione)
- Aggiunta di nuovi film specificando titolo e capienza della sala.
- Modifica del titolo dei film esistenti.
- Rimozione dei film (bloccata in automatico se ci sono biglietti già venduti).
- **Gestione Resi**: possibilità di cliccare sui posti già occupati per rimborsare il biglietto e liberare immediatamente il posto.

## 💻 Stack Tecnologico
- **Linguaggio Base**: Java (con Stream API e Lambda)
- **Framework GUI**: Java Swing (gestione avanzata dei Layout Manager)
- **Struttura del Codice**: Separazione logica in Model, Service e UI
- **Librerie Esterne**: OpenPDF (`com.lowagie.text`) per l'esportazione dei biglietti

## 🚀 Come eseguire il progetto

1. Clona il repository sul tuo computer:
   ```bash
   git clone https://github.com/Domenicos97/CinemaBookingSystem.git
   ```
2. Apri il progetto con il tuo IDE.
3. Assicurati che la libreria per la generazione dei PDF sia inclusa nel Build Path.
4. Esegui la classe `Main`

*(In alternativa, puoi esportare il progetto come pacchetto `.jar` eseguibile direttamente dal tuo IDE).*
