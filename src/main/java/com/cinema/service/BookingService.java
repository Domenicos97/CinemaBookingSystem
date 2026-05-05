package com.cinema.service;

import com.cinema.dao.MovieDAO;
import com.cinema.model.Movie;

import java.util.List;

/**
 * Livello di servizio dell'applicazione.
 * Fa da ponte tra l'interfaccia grafica e il livello di accesso ai dati (DAO).
 * Contiene la logica di business legata alle prenotazioni, ai resi e alla gestione del catalogo.
 */
public class BookingService {

    /**
     * Oggetto DAO che si occupa delle query sul database.
     */
    private final MovieDAO movieDao;

    /**
     * Costruttore della classe di servizio.
     * Inizializza il DAO usato per comunicare con il database.
     */
    public BookingService() {
        this.movieDao = new MovieDAO();
    }

    /**
     * Restituisce il catalogo completo dei film presenti nel database.
     *
     * @return lista dei film
     */
    public List<Movie> getCatalog() {
        return movieDao.getAllMovies();
    }

    /**
     * Aggiunge un nuovo film al database.
     *
     * @param title titolo del film
     * @param totalSeats numero totale di posti della sala
     */
    public void addMovie(String title, int totalSeats) {
        movieDao.addMovie(title, totalSeats);
    }

    /**
     * Modifica il titolo di un film esistente.
     *
     * @param movieId id del film da modificare
     * @param newTitle nuovo titolo del film
     */
    public void updateMovieTitle(int movieId, String newTitle) {
        movieDao.updateMovieTitle(movieId, newTitle);
    }

    /**
     * Elimina un film dal database.
     *
     * @param movieId id del film da eliminare
     */
    public void deleteMovie(int movieId) {
        movieDao.deleteMovie(movieId);
    }

    /**
     * Registra l'acquisto di uno o più posti per un film.
     * Recupera il film dal catalogo, aggiorna la lista dei posti occupati
     * e salva i nuovi valori nel database.
     *
     * @param movieId id del film
     * @param seats lista dei posti acquistati
     */
    public void bookTickets(int movieId, List<Integer> seats) {
        Movie movie = getCatalog().stream()
                .filter(m -> m.getId() == movieId)
                .findFirst().orElse(null);
        if (movie == null) return;

        List<Integer> bookedList = movie.getBookedSeatsList();
        bookedList.addAll(seats);

        String newBookedSeats = bookedList.stream()
                .map(String::valueOf)
                .reduce("", (a, b) -> a.isEmpty() ? b : a + "," + b);

        int newAvailable = movie.getTotalSeats() - bookedList.size();
        movieDao.updateSeats(movieId, newAvailable, newBookedSeats);
    }

    /**
     * Annulla la prenotazione di un singolo posto.
     * Rimuove il posto dalla lista dei prenotati e aggiorna il database.
     *
     * @param movieId id del film
     * @param seat numero del posto da liberare
     */
    public void cancelTicket(int movieId, int seat) {
        Movie movie = getCatalog().stream()
                .filter(m -> m.getId() == movieId)
                .findFirst().orElse(null);
        if (movie == null) return;

        List<Integer> bookedList = movie.getBookedSeatsList();
        bookedList.remove(Integer.valueOf(seat));

        String newBookedSeats = bookedList.stream()
                .map(String::valueOf)
                .reduce("", (a, b) -> a.isEmpty() ? b : a + "," + b);

        int newAvailable = movie.getTotalSeats() - bookedList.size();
        movieDao.updateSeats(movieId, newAvailable, newBookedSeats);
    }
}