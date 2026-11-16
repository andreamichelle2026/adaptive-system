package com.amcm.evaluadordecuestionarios.plugins;

import com.amcm.evaluadordecuestionarios.Contexto;
import com.amcm.evaluadordecuestionarios.Pregunta;
import com.amcm.evaluadordecuestionarios.ProcesadorPlugin;
import com.amcm.evaluadordecuestionarios.Resultado;
import com.amcm.evaluadordecuestionarios.TipoPregunta;

/** Plugin (Java): compara la letra elegida con la opcion correcta. */
public class ProcesadorAlternativas implements ProcesadorPlugin {

    @Override
    public String id() { return "alternativas"; }

    @Override
    public boolean soporta(Contexto contexto) {
        return contexto.getCantidadLetras() == 1 && contexto.getCantidadPalabras() == 1;
    }

    @Override
    public Resultado procesar(String entrada, Pregunta pregunta) {
        if (pregunta.getTipo() != TipoPregunta.ALTERNATIVAS) {
            return new Resultado("la pregunta no es de alternativas", 0.0);
        }
        char elegida = primeraLetra(entrada);
        char correcta = pregunta.getRespuestaCorrecta();
        int maxOpcion = 'A' + pregunta.getOpciones().size();

        if (elegida < 'A' || elegida >= maxOpcion) {
            return new Resultado("la letra '" + elegida + "' no corresponde a ninguna opcion", 0.0);
        }
        if (elegida == correcta) {
            return new Resultado("correcta (opcion " + correcta + ")", 1.0);
        }
        return new Resultado("incorrecta: elegiste " + elegida + ", la correcta era " + correcta, 0.0);
    }

    /** Primera letra de la entrada, en mayuscula. */
    private static char primeraLetra(String entrada) {
        for (char c : entrada.toCharArray()) {
            if (Character.isLetter(c)) return Character.toUpperCase(c);
        }
        return '\0';
    }
}
