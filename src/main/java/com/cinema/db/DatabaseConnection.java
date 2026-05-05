package com.cinema.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.File;

/**
 * Classe responsabile della connessione al database SQLite.
 * Gestisce l'apertura della connessione, la creazione delle tabelle
 * e l'inserimento dei film iniziali al primo avvio.
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:cinema.db";
    private static Connection connection = null;

    /**
     * Costruttore privato per impedire istanziazioni esterne.
     * La classe viene usata solo tramite metodi statici.
     */
    private DatabaseConnection() {}

    /**
     * Restituisce la connessione al database.
     * Se non esiste ancora, la crea e inizializza la tabella movies.
     * Se il database è al primo avvio, inserisce anche alcuni film di default.
     *
     * @return connessione attiva al database SQLite
     * @throws SQLException se si verifica un errore durante la connessione
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            boolean isFirstTime = !new File("cinema.db").exists();

            connection = DriverManager.getConnection(URL);

            try (Statement stmt = connection.createStatement()) {
                String createTableSQL = "CREATE TABLE IF NOT EXISTS movies (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                        "title TEXT NOT NULL," +
                                        "total_seats INTEGER NOT NULL," +
                                        "available_seats INTEGER NOT NULL," +
                                        "booked_seats TEXT NOT NULL DEFAULT '');";
                stmt.execute(createTableSQL);
            }

            if (isFirstTime) {
                initializeDefaultMovies(connection);
            }
        }
        return connection;
    }

    /**
     * Inserisce alcuni film iniziali nel database.
     * Viene chiamato solo al primo avvio, quando il file cinema.db non esiste ancora.
     *
     * @param conn connessione attiva al database
     * @throws SQLException se l'inserimento fallisce
     */
    private static void initializeDefaultMovies(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("INSERT INTO movies (title, total_seats, available_seats, booked_seats) VALUES ('Il Signore degli Anelli', 100, 100, '')");
            stmt.execute("INSERT INTO movies (title, total_seats, available_seats, booked_seats) VALUES ('Matrix', 50, 50, '')");
            stmt.execute("INSERT INTO movies (title, total_seats, available_seats, booked_seats) VALUES ('Interstellar', 75, 75, '')");
        }
    }
}