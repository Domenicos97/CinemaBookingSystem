package com.cinema.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Modello che rappresenta un film all'interno dell'applicazione.
 * Contiene i dati principali della sala, la capienza totale,
 * i posti disponibili e l'elenco dei posti già prenotati.
 */
public class Movie {
    private int id;
    private String title;
    private int totalSeats;
    private int availableSeats;
    private String bookedSeats;

    /**
     * Costruttore del film.
     * @param id identificativo univoco del film
     * @param title titolo del film
     * @param totalSeats numero totale di posti della sala
     * @param availableSeats numero di posti ancora disponibili
     * @param bookedSeats lista dei posti prenotati, salvata come stringa separata da virgole
     */
    public Movie(int id, String title, int totalSeats, int availableSeats, String bookedSeats) {
        this.id = id;
        this.title = title;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
        this.bookedSeats = bookedSeats == null ? "" : bookedSeats;
    }

    /**
     * Restituisce l'ID del film.
     * @return ID del film
     */
    public int getId() { return id; }

    /**
     * Restituisce il titolo del film.
     * @return titolo del film
     */
    public String getTitle() { return title; }

    /**
     * Restituisce il numero totale di posti della sala.
     * @return posti totali
     */
    public int getTotalSeats() { return totalSeats; }

    /**
     * Restituisce il numero di posti ancora disponibili.
     * @return posti disponibili
     */
    public int getAvailableSeats() { return availableSeats; }

    /**
     * Converte la stringa dei posti prenotati in una lista di interi.
     * Se non ci sono posti prenotati, restituisce una lista vuota.
     * @return lista dei posti occupati
     */
    public List<Integer> getBookedSeatsList() {
        List<Integer> list = new ArrayList<>();
        if (bookedSeats.trim().isEmpty()) return list;
        for (String s : bookedSeats.split(",")) {
            list.add(Integer.parseInt(s.trim()));
        }
        return list;
    }
}