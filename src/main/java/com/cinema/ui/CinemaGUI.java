package com.cinema.ui;

import com.cinema.model.Movie;
import com.cinema.service.BookingService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Interfaccia grafica principale dell'applicazione.
 * Gestisce sia la modalità cliente sia la modalità amministratore.
 */
public class CinemaGUI extends JFrame {

    private static final long serialVersionUID = 1L;

    /**
     * Servizio che gestisce le operazioni sul catalogo film.
     */
    private final BookingService bookingService = new BookingService();

    /**
     * Tabella che mostra l'elenco dei film.
     */
    private JTable movieTable;

    /**
     * Modello dati della tabella film.
     */
    private DefaultTableModel tableModel;

    /**
     * Indica se l'utente corrente è un amministratore.
     */
    private final boolean isAdmin; 

    /**
     * Costruttore della finestra principale.
     * @param isAdmin true se l'app è in modalità amministratore, false se in modalità cliente.
     */
    public CinemaGUI(boolean isAdmin) {
        this.isAdmin = isAdmin;
        initializeUI();
        loadMoviesIntoTable();
    }

    /**
     * Inizializza tutti i componenti grafici della finestra principale.
     */
    @SuppressWarnings("unused")
    private void initializeUI() {
        setTitle(isAdmin ? "Cinema Pro - Pannello di Amministrazione" : "Cinema Pro - Prenotazione Biglietti");
        setSize(750, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel(isAdmin ? "Gestione Multisala (Admin)" : "Acquista i tuoi biglietti", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        String[] cols = {"ID", "Titolo Film", "Posti Disponibili", "Posti Totali"};
        tableModel = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        
        movieTable = new JTable(tableModel);
        movieTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        movieTable.setRowHeight(28);
        movieTable.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        movieTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        
        movieTable.getColumnModel().getColumn(0).setMaxWidth(50);
        movieTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        
        mainPanel.add(new JScrollPane(movieTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        if (isAdmin) {
            JButton addMovieBtn = new JButton("Aggiungi");
            JButton editMovieBtn = new JButton("Modifica");
            JButton deleteMovieBtn = new JButton("Elimina");
            JButton manageSeatsBtn = new JButton("Gestisci Sala (Resi)");
            
            bottomPanel.add(addMovieBtn);
            bottomPanel.add(editMovieBtn);
            bottomPanel.add(deleteMovieBtn);
            bottomPanel.add(manageSeatsBtn);

            addMovieBtn.addActionListener(e -> openAddMovieDialog());
            editMovieBtn.addActionListener(e -> editMovie());
            deleteMovieBtn.addActionListener(e -> deleteMovie());
            manageSeatsBtn.addActionListener(e -> {
                Movie m = getSelectedMovie();
                if (m != null) openSeatSelectionDialog(m);
            });
        } else {
            JButton bookBtn = new JButton("Scegli Posti & Prenota");
            bookBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            bookBtn.setBackground(new Color(46, 139, 87)); 
            bookBtn.setForeground(Color.WHITE);
            bottomPanel.add(bookBtn);
            
            bookBtn.addActionListener(e -> {
                Movie m = getSelectedMovie();
                if (m != null) openSeatSelectionDialog(m);
            });
        }

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    /**
     * Restituisce il film selezionato nella tabella.
     * @return l'oggetto Movie selezionato, oppure null se non è stata selezionata nessuna riga.
     */
    private Movie getSelectedMovie() {
        int row = movieTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Seleziona un film dalla tabella.", "Attenzione", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        int movieId = (int) tableModel.getValueAt(row, 0);
        return bookingService.getCatalog().stream().filter(m -> m.getId() == movieId).findFirst().orElse(null);
    }

    /**
     * Apre la finestra per modificare il titolo del film selezionato.
     */
    private void editMovie() {
        Movie m = getSelectedMovie();
        if (m == null) return;
        String newTitle = JOptionPane.showInputDialog(this, "Nuovo titolo:", m.getTitle());
        if (newTitle != null && !newTitle.trim().isEmpty()) {
            bookingService.updateMovieTitle(m.getId(), newTitle);
            loadMoviesIntoTable();
        }
    }

    /**
     * Elimina il film selezionato se non ci sono posti già venduti.
     * In caso di posti occupati, blocca l'operazione.
     */
    private void deleteMovie() {
        Movie m = getSelectedMovie();
        if (m == null) return;
        
        if (!m.getBookedSeatsList().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Impossibile eliminare il film '" + m.getTitle() + "'.\nCi sono ancora dei biglietti venduti per questa sala.\n\nEsegui prima il reso di tutti i posti occupati.", 
                "Errore Eliminazione", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int ans = JOptionPane.showConfirmDialog(this, "Rimuovere definitivamente '" + m.getTitle() + "'?", "Conferma", JOptionPane.YES_NO_OPTION);
        if (ans == JOptionPane.YES_OPTION) {
            bookingService.deleteMovie(m.getId());
            loadMoviesIntoTable();
        }
    }

    /**
     * Apre la finestra di aggiunta di un nuovo film.
     */
    private void openAddMovieDialog() {
        JTextField titleField = new JTextField(15);
        JSpinner seatsSpinner = new JSpinner(new SpinnerNumberModel(50, 10, 300, 10)); 
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.add(new JLabel("Titolo:")); panel.add(titleField);
        panel.add(new JLabel("Posti:")); panel.add(seatsSpinner);
        
        if (JOptionPane.showConfirmDialog(this, panel, "Nuovo Film", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            if (!titleField.getText().trim().isEmpty()) {
                bookingService.addMovie(titleField.getText(), (int) seatsSpinner.getValue());
                loadMoviesIntoTable(); 
            }
        }
    }

    /**
     * Apre la finestra con la mappa dei posti della sala relativa al film selezionato.
     * La finestra gestisce sia la prenotazione dei posti sia il reso, in base al ruolo.
     * @param movie film selezionato.
     */
    @SuppressWarnings("unused")
    private void openSeatSelectionDialog(Movie movie) {
        JDialog dialog = new JDialog(this, "Sala: " + movie.getTitle(), true);
        dialog.setSize(750, 600);
        dialog.setLocationRelativeTo(this);
        
        JPanel topPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        
        JLabel screenLabel = new JLabel("====== [ SCHERMO ] ======", SwingConstants.CENTER);
        screenLabel.setFont(new Font("Monospaced", Font.BOLD, 22));
        
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        legendPanel.add(createLegendItem("Occupato", new Color(178, 34, 34)));
        legendPanel.add(createLegendItem("Economico (6.00 Euro)", new Color(135, 206, 250)));
        legendPanel.add(createLegendItem("Standard (8.50 Euro)", new Color(144, 238, 144)));
        legendPanel.add(createLegendItem("VIP (12.00 Euro)", new Color(255, 215, 0)));
        if (!isAdmin) legendPanel.add(createLegendItem("Selezionato", Color.YELLOW));
        
        topPanel.add(screenLabel);
        topPanel.add(legendPanel);
        dialog.add(topPanel, BorderLayout.NORTH);

        JPanel seatContainerPanel = new JPanel();
        seatContainerPanel.setLayout(new BoxLayout(seatContainerPanel, BoxLayout.Y_AXIS));
        seatContainerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        List<Integer> booked = movie.getBookedSeatsList();
        List<Integer> selectedToBook = new ArrayList<>();
        
        int totalePosti = movie.getTotalSeats();
        int colonne = 10; 
        int totaleRighe = (int) Math.ceil((double) totalePosti / colonne);
        
        int calcRigheEco = (totaleRighe > 2) ? Math.max(1, (int) (totaleRighe * 0.20)) : 0;
        int calcRigheVip = (totaleRighe > 2) ? Math.max(1, (int) (totaleRighe * 0.20)) : 0;
        
        if (calcRigheEco + calcRigheVip >= totaleRighe) {
            calcRigheEco = 0;
            calcRigheVip = 0;
        }
        
        final int righeEconomiche = calcRigheEco;
        final int righeVip = calcRigheVip;

        int seatCount = 1;

        for (int riga = 1; riga <= totaleRighe; riga++) {
            JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
            
            Color baseColor;
            if (riga <= righeEconomiche) {
                baseColor = new Color(135, 206, 250); 
            } else if (riga > totaleRighe - righeVip) {
                baseColor = new Color(255, 215, 0); 
            } else {
                baseColor = new Color(144, 238, 144); 
            }

            int postiInQuestaRiga = Math.min(colonne, totalePosti - seatCount + 1);

            for (int col = 0; col < postiInQuestaRiga; col++) {
                int seatNum = seatCount; 
                
                JButton btn = new JButton(String.valueOf(seatNum));
                btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                btn.setPreferredSize(new Dimension(50, 40)); 
                btn.setOpaque(true);
                btn.setContentAreaFilled(true);

                if (booked.contains(seatNum)) {
                    btn.setBackground(new Color(178, 34, 34)); 
                    btn.setForeground(Color.WHITE);
                    btn.addActionListener(e -> {
                        if (isAdmin) {
                            if (JOptionPane.showConfirmDialog(dialog, "Vuoi rimborsare e liberare il posto " + seatNum + "?", "Reso Biglietto", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                                bookingService.cancelTicket(movie.getId(), seatNum);
                                dialog.dispose();
                                loadMoviesIntoTable();
                                
                                Movie filmAggiornato = bookingService.getCatalog().stream()
                                        .filter(m -> m.getId() == movie.getId())
                                        .findFirst()
                                        .orElse(movie);
                                openSeatSelectionDialog(filmAggiornato); 
                            }
                        } else {
                            JOptionPane.showMessageDialog(dialog, "Questo posto è già stato acquistato da un altro utente.", "Posto Occupato", JOptionPane.WARNING_MESSAGE);
                        }
                    });
                } else {
                    btn.setBackground(baseColor);
                    btn.addActionListener(e -> {
                        if (isAdmin) {
                            JOptionPane.showMessageDialog(dialog, "Sei in modalità Gestore. I posti colorati sono liberi.", "Info", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            if (selectedToBook.contains(seatNum)) {
                                selectedToBook.remove(Integer.valueOf(seatNum));
                                btn.setBackground(baseColor);
                            } else {
                                selectedToBook.add(seatNum);
                                btn.setBackground(Color.YELLOW);
                            }
                        }
                    });
                }
                rowPanel.add(btn);
                seatCount++;
            }
            seatContainerPanel.add(rowPanel);
        }

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.add(seatContainerPanel, BorderLayout.NORTH);

        JPanel bottomActionPanel = new JPanel(new FlowLayout());
        
        if (isAdmin) {
            JButton closeBtn = new JButton("Chiudi Mappa");
            closeBtn.addActionListener(e -> dialog.dispose());
            bottomActionPanel.add(closeBtn);
        } else {
            JButton confirmBtn = new JButton("Conferma Acquisto");
            confirmBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            confirmBtn.setBackground(new Color(46, 139, 87));
            confirmBtn.setForeground(Color.WHITE);
            bottomActionPanel.add(confirmBtn);
            
            confirmBtn.addActionListener(e -> {
                if (selectedToBook.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Seleziona almeno un posto libero.");
                    return;
                }
                
                double totale = 0;
                int countEco = 0, countStd = 0, countVip = 0;
                
                for (int seat : selectedToBook) {
                    int rigaCalc = (seat - 1) / colonne + 1;
                    if (rigaCalc <= righeEconomiche) { totale += 6.00; countEco++; } 
                    else if (rigaCalc > totaleRighe - righeVip) { totale += 12.00; countVip++; } 
                    else { totale += 8.50; countStd++; }
                }
                
                String riepilogo = String.format("Riepilogo Acquisto:\n- %d Economici (6.00 Euro)\n- %d Standard (8.50 Euro)\n- %d VIP (12.00 Euro)\n\nTotale: %.2f Euro\n\nConfermi l'acquisto?", countEco, countStd, countVip, totale);
                
                if (JOptionPane.showConfirmDialog(dialog, riepilogo, "Checkout", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    bookingService.bookTickets(movie.getId(), selectedToBook);
                    stampaRicevuta(movie.getId(), movie.getTitle(), selectedToBook);
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this, "Acquisto completato! Trovi i tuoi PDF nella cartella del progetto.");
                    loadMoviesIntoTable();
                }
            });
        }

        dialog.add(new JScrollPane(wrapperPanel), BorderLayout.CENTER);
        dialog.add(bottomActionPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    /**
     * Crea un elemento grafico della legenda dei colori della sala.
     * @param text testo da mostrare nella legenda.
     * @param color colore associato al tipo di posto.
     * @return pannello pronto da inserire nella legenda.
     */
    private JPanel createLegendItem(String text, Color color) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JLabel colorBox = new JLabel("  ");
        colorBox.setOpaque(true);
        colorBox.setBackground(color);
        colorBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        JLabel label = new JLabel(text);
        panel.add(colorBox); 
        panel.add(label);
        return panel;
    }

    /**
     * Ricarica i dati dei film.
     */
    private void loadMoviesIntoTable() {
        tableModel.setRowCount(0); 
        for (Movie m : bookingService.getCatalog()) {
            tableModel.addRow(new Object[]{ m.getId(), m.getTitle(), m.getAvailableSeats(), m.getTotalSeats() });
        }
    }

    /**
     * Genera i biglietti PDF per i posti acquistati.
     * @param movieId id del film.
     * @param titoloFilm titolo del film.
     * @param posti lista dei posti acquistati.
     */
    private void stampaRicevuta(int movieId, String titoloFilm, java.util.List<Integer> posti) {
        try {
            String dataAcquisto = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            String nomeFilmPulito = titoloFilm.replaceAll("\\s+", "_");

            for (int posto : posti) {
                String ticketCode = "TKT-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                String fileName = "Biglietto_" + nomeFilmPulito + "_Posto" + posto + ".pdf";
                
                com.lowagie.text.Document document = new com.lowagie.text.Document(new com.lowagie.text.Rectangle(400, 600), 20, 20, 20, 20);
                com.lowagie.text.pdf.PdfWriter.getInstance(document, new java.io.FileOutputStream(fileName));
                
                document.open();
                
                com.lowagie.text.Font fontTitolo = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 22, com.lowagie.text.Font.BOLD);
                com.lowagie.text.Font fontSottotitolo = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 12, com.lowagie.text.Font.ITALIC, java.awt.Color.DARK_GRAY);
                com.lowagie.text.Font fontLabel = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 10, com.lowagie.text.Font.BOLD, java.awt.Color.GRAY);
                com.lowagie.text.Font fontValore = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 14, com.lowagie.text.Font.BOLD);
                com.lowagie.text.Font fontPosto = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 36, com.lowagie.text.Font.BOLD, new java.awt.Color(178, 34, 34)); 
                
                com.lowagie.text.pdf.PdfPTable mainTable = new com.lowagie.text.pdf.PdfPTable(1);
                mainTable.setWidthPercentage(100);
                
                com.lowagie.text.pdf.PdfPCell outerCell = new com.lowagie.text.pdf.PdfPCell();
                outerCell.setBorderWidth(2f);
                outerCell.setPadding(15f);
                
                com.lowagie.text.Paragraph header = new com.lowagie.text.Paragraph("CINEMA MULTISALA PRO", fontTitolo);
                header.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
                outerCell.addElement(header);
                
                com.lowagie.text.Paragraph subHeader = new com.lowagie.text.Paragraph("Biglietto d'Ingresso", fontSottotitolo);
                subHeader.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
                outerCell.addElement(subHeader);
                
                outerCell.addElement(new com.lowagie.text.Paragraph(" "));
                
                com.lowagie.text.pdf.draw.LineSeparator ls = new com.lowagie.text.pdf.draw.LineSeparator();
                ls.setLineWidth(1f);
                ls.setPercentage(100);
                outerCell.addElement(new com.lowagie.text.Chunk(ls));
                
                outerCell.addElement(new com.lowagie.text.Paragraph(" "));
                
                com.lowagie.text.pdf.PdfPTable detailsTable = new com.lowagie.text.pdf.PdfPTable(2);
                detailsTable.setWidthPercentage(100);
                
                com.lowagie.text.pdf.PdfPCell leftCell = new com.lowagie.text.pdf.PdfPCell();
                leftCell.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
                leftCell.addElement(new com.lowagie.text.Paragraph("FILM", fontLabel));
                leftCell.addElement(new com.lowagie.text.Paragraph(titoloFilm, fontValore));
                leftCell.addElement(new com.lowagie.text.Paragraph(" ", fontLabel));
                leftCell.addElement(new com.lowagie.text.Paragraph("SALA", fontLabel));
                leftCell.addElement(new com.lowagie.text.Paragraph(String.valueOf(movieId), fontValore));
                
                com.lowagie.text.pdf.PdfPCell rightCell = new com.lowagie.text.pdf.PdfPCell();
                rightCell.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
                rightCell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_RIGHT);
                
                com.lowagie.text.Paragraph pLabelPosto = new com.lowagie.text.Paragraph("POSTO", fontLabel);
                pLabelPosto.setAlignment(com.lowagie.text.Element.ALIGN_RIGHT);
                rightCell.addElement(pLabelPosto);
                
                com.lowagie.text.Paragraph pPostoVal = new com.lowagie.text.Paragraph(String.valueOf(posto), fontPosto);
                pPostoVal.setAlignment(com.lowagie.text.Element.ALIGN_RIGHT);
                rightCell.addElement(pPostoVal);
                
                detailsTable.addCell(leftCell);
                detailsTable.addCell(rightCell);
                outerCell.addElement(detailsTable);
                
                outerCell.addElement(new com.lowagie.text.Paragraph(" "));
                outerCell.addElement(new com.lowagie.text.Chunk(ls)); 
                outerCell.addElement(new com.lowagie.text.Paragraph(" "));
                
                com.lowagie.text.Paragraph pData = new com.lowagie.text.Paragraph("Acquistato il: " + dataAcquisto, fontLabel);
                outerCell.addElement(pData);
                
                com.lowagie.text.Paragraph pCodice = new com.lowagie.text.Paragraph("Codice Biglietto: " + ticketCode, fontLabel);
                outerCell.addElement(pCodice);
                
                outerCell.addElement(new com.lowagie.text.Paragraph(" "));
                
                com.lowagie.text.Paragraph barcode = new com.lowagie.text.Paragraph("||||||| | ||| |||| | | ||| || |||", new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 16, com.lowagie.text.Font.BOLD));
                barcode.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
                outerCell.addElement(barcode);
                
                mainTable.addCell(outerCell);
                document.add(mainTable);

                document.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Errore durante la creazione del PDF: " + ex.getMessage());
        }
    }
}