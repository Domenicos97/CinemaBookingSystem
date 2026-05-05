package com.cinema;

import com.cinema.ui.CinemaGUI;
import javax.swing.*;
import java.awt.*;

/**
 * Classe di avvio dell'applicazione.
 * Gestisce la schermata iniziale di selezione del ruolo tra Cliente e Amministratore.
 */
public class Main {
    /**
     * Punto di ingresso principale dell'applicazione.
     * Avvia l'interfaccia grafica sul thread dedicato.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> showRoleSelection());
    }

    /**
     * Mostra la finestra di selezione del ruolo utente.
     * Permette l'accesso alla modalità Cliente o Amministratore (protetto da password).
     */
    private static void showRoleSelection() {
        JFrame frame = new JFrame("Cinema Booking - Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JLabel label = new JLabel("Seleziona il tuo ruolo:", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        frame.add(label, BorderLayout.NORTH);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));

        // Bottone per la modalità Cliente
        JButton customerBtn = new JButton("Area Clienti");
        customerBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        customerBtn.setBackground(new Color(46, 139, 87));
        customerBtn.setForeground(Color.WHITE);
        customerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Bottone per la modalità Gestore
        JButton adminBtn = new JButton("Area Gestore");
        adminBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        adminBtn.setBackground(new Color(70, 130, 180));
        adminBtn.setForeground(Color.WHITE);
        adminBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnPanel.add(customerBtn);
        btnPanel.add(adminBtn);
        frame.add(btnPanel, BorderLayout.CENTER);

        // Azione al click del cliente: apre la GUI in modalità non-admin
        customerBtn.addActionListener(_ -> {
            frame.dispose();
            new CinemaGUI(false).setVisible(true);
        });

        // Azione al click dell'amministratore: richiede password prima di aprire la GUI
        adminBtn.addActionListener(_ -> {
            JPasswordField pwdField = new JPasswordField(10);
            int action = JOptionPane.showConfirmDialog(frame, pwdField, "Inserisci Password (scrivi: admin)", JOptionPane.OK_CANCEL_OPTION);
            
            if (action == JOptionPane.OK_OPTION) {
                String pwd = new String(pwdField.getPassword());
                // Password di default impostata come "admin"
                if (pwd.equals("admin")) {
                    frame.dispose();
                    new CinemaGUI(true).setVisible(true); // true = modalità admin
                } else {
                    JOptionPane.showMessageDialog(frame, "Password errata!", "Accesso Negato", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        frame.setVisible(true);
    }
}