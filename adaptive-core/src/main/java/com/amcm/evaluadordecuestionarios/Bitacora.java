package com.amcm.evaluadordecuestionarios;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** Guarda los eventos de cada ejecucion con hora y categoria. */
public final class Bitacora {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final List<String> lineas = new ArrayList<>();

    private Bitacora() {}

    public static void registrar(String categoria, String mensaje) {
        lineas.add("[" + LocalTime.now().format(HORA) + "] " + categoria + ": " + mensaje);
    }

    /** Imprime del evento mas reciente al mas antiguo y vacia la bitacora. */
    public static void imprimirYLimpiar() {
        for (int i = lineas.size() - 1; i >= 0; i--) {
            System.out.println(lineas.get(i));
        }
        lineas.clear();
    }
}
