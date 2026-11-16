package com.amcm.evaluadordecuestionarios;

/** Observa la respuesta del estudiante, mide sus metricas y construye el Contexto. */
public final class Monitor {

    private Monitor() {}

    public static Contexto crearContexto(String entrada) {
        int letras = TextoUtil.contarLetras(entrada);
        int palabras = TextoUtil.contarPalabras(entrada);
        return new Contexto(letras, palabras);
    }
}
