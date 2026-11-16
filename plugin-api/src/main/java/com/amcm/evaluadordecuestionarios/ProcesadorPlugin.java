package com.amcm.evaluadordecuestionarios;

/**
 * Contrato que debe cumplir toda clase para ser un plugin.
 * soporta(...) decide con las METRICAS (Contexto);
 * procesar(...) evalua con los DATOS DE LA PREGUNTA.
 */
public interface ProcesadorPlugin {
    String id();
    boolean soporta(Contexto contexto);
    Resultado procesar(String entrada, Pregunta pregunta);
}
