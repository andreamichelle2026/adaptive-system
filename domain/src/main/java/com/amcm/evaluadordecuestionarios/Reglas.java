package com.amcm.evaluadordecuestionarios;

/**
 * Reglas de negocio compartidas. Una sola fuente de verdad: el selector (adaptive-core)
 * y el plugin de conceptos (plugins-scala) leen el mismo umbral desde aqui.
 */
public final class Reglas {

    /** Minimo de letras (exclusivo) para evaluar una respuesta con la rubrica de conceptos. */
    public static final int LETRAS_MINIMO_CONCEPTOS = 5;

    private Reglas() {}
}
