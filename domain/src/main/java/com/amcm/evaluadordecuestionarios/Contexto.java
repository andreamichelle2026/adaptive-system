package com.amcm.evaluadordecuestionarios;

/** Metricas de la respuesta: cantidad de letras y de palabras. */
public class Contexto {
    private final int cantidadLetras;
    private final int cantidadPalabras;

    public Contexto(int cantidadLetras, int cantidadPalabras) {
        this.cantidadLetras = cantidadLetras;
        this.cantidadPalabras = cantidadPalabras;
    }

    public int getCantidadLetras() { return cantidadLetras; }
    public int getCantidadPalabras() { return cantidadPalabras; }
}
