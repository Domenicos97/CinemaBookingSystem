package com.cinema.dao;

import com.cinema.db.DatabaseConnection;
import com.cinema.model.Movie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe DAO (Data Access Object) responsabile dell'accesso al database
 * per le operazioni sui film.
 * Gestisce lettura, inserimento, aggiornamento e cancellazione dei record
 * della tabella movies.
 */
public class MovieDAO {

    /**
     * Recupera tutti i film presenti nel database.
     * Per ciascun record crea un oggetto Movie e lo aggiunge alla lista.
     *
     * @return lista completa dei film
     */
    public List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT * FROM movies";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                movies.add(new Movie(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getInt("total_seats"),
                        rs.getInt("available_seats"),
                        rs.getString("booked_seats")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }

    /**
     * Aggiorna il numero di posti disponibili e la stringa dei posti prenotati
     * per un determinato film.
     *
     * @param movieId id del film da aggiornare
     * @param newAvailableSeats nuovo numero di posti disponibili
     * @param newBookedSeats nuova stringa dei posti prenotati
     * @return true se l'aggiornamento è andato a buon fine, false altrimenti
     */
    public boolean updateSeats(int movieId, int newAvailableSeats, String newBookedSeats) {
        String sql = "UPDATE movies SET available_seats = ?, booked_seats = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newAvailableSeats);
            pstmt.setString(2, newBookedSeats);
            pstmt.setInt(3, movieId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Inserisce un nuovo film nel database.
     *
     * @param title titolo del film
     * @param totalSeats numero totale di posti della sala
     * @return true se l'inserimento è andato a buon fine, false altrimenti
     */
    public boolean addMovie(String title, int totalSeats) {
        String sql = "INSERT INTO movies (title, total_seats, available_seats, booked_seats) VALUES (?, ?, ?, '')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setInt(2, totalSeats);
            pstmt.setInt(3, totalSeats);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina un film dal database.
     *
     * @param movieId id del film da eliminare
     * @return true se la cancellazione è andata a buon fine, false altrimenti
     */
    public boolean deleteMovie(int movieId) {
        String sql = "DELETE FROM movies WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, movieId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Aggiorna il titolo di un film esistente.
     *
     * @param movieId id del film da modificare
     * @param newTitle nuovo titolo da impostare
     * @return true se l'aggiornamento è andato a buon fine, false altrimenti
     */
    public boolean updateMovieTitle(int movieId, String newTitle) {
        String sql = "UPDATE movies SET title = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newTitle);
            pstmt.setInt(2, movieId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}