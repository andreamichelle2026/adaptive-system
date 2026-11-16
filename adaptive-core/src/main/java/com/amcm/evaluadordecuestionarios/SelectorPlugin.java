package com.amcm.evaluadordecuestionarios;

/**
 * Decide que plugin evalua cada respuesta segun las metricas del Contexto.
 * No ejecuta nada ni crea objetos: devuelve el nombre de la clase y el motivo.
 *
 *  - letras == 1 y palabras == 1                                  -> ProcesadorAlternativas (Java)
 *  - letras > 1  y palabras == 1                                  -> ProcesadorSimple       (Java)
 *  - letras > Reglas.LETRAS_MINIMO_CONCEPTOS y palabras > 1       -> ProcesadorConceptos    (Scala)
 *  - cualquier otro caso (regla por defecto)                      -> ProcesadorSimple       (Java)
 */
public final class SelectorPlugin {

    public static final String CLASE_SIMPLE = "com.amcm.evaluadordecuestionarios.plugins.ProcesadorSimple";
    public static final String CLASE_ALTERNATIVAS = "com.amcm.evaluadordecuestionarios.plugins.ProcesadorAlternativas";
    public static final String CLASE_CONCEPTOS = "com.amcm.evaluadordecuestionarios.plugins.ProcesadorConceptos";

    private SelectorPlugin() {}

    public static String seleccionar(Contexto c) {
        int letras = c.getCantidadLetras();
        int palabras = c.getCantidadPalabras();

        if (letras == 1 && palabras == 1) return CLASE_ALTERNATIVAS;
        if (letras > 1 && palabras == 1) return CLASE_SIMPLE;
        if (letras > Reglas.LETRAS_MINIMO_CONCEPTOS && palabras > 1) return CLASE_CONCEPTOS;
        return CLASE_SIMPLE; // regla por defecto
    }

    /** Explica por que la regla eligio esa clase (se muestra al usuario). */
    public static String motivo(Contexto c) {
        int letras = c.getCantidadLetras();
        int palabras = c.getCantidadPalabras();
        String datos = " (" + letras + " letras, " + palabras + " palabras)";

        if (letras == 1 && palabras == 1)
            return "una letra y una palabra -> se compara con la opcion correcta" + datos;
        if (letras > 1 && palabras == 1)
            return "una sola palabra -> se compara con las respuestas aceptadas" + datos;
        if (letras > Reglas.LETRAS_MINIMO_CONCEPTOS && palabras > 1)
            return "mas de " + Reglas.LETRAS_MINIMO_CONCEPTOS
                 + " letras y varias palabras -> se evaluan los conceptos de la rubrica" + datos;
        return "respuesta fuera de los escenarios definidos -> se deriva al plugin simple" + datos;
    }
}
