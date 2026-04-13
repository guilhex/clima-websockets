package org.example.model;

/**
 * Representa uma cidade com suas coordenadas geográficas para
 * consulta direta na API Open-Meteo.
 */
public class Cidade {

    private final String nome;
    private final double latitude;
    private final double longitude;

    public Cidade(String nome, double latitude, double longitude) {
        this.nome = nome;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getNome() { return nome; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
}