package com.amcm.evaluadordecuestionarios;

/*
 * Crea un plugin a partir del nombre de su clase en texto
 * (CLASE_SIMPLE, CLASE_ALTERNATIVAS, CLASE_CONCEPTOS)
 * sin que el codigo conozca esa clase al compilar. Usa reflexion de Java.
 */
public final class CargadorPlugin {

    private CargadorPlugin() {}

    public static ProcesadorPlugin crear(String nombreClase) throws Exception {

        // Descubrir: busca en el classpath la clase cuyo nombre completo recibe como String.
        // Si no existe, lanza ClassNotFoundException.
        Bitacora.registrar("Reflection", "Class.forName(\"" + nombreClase + "\")");
        Class<?> clase = Class.forName(nombreClase);

        // Validar: la clase cargada debe implementar la interfaz ProcesadorPlugin
        if (!ProcesadorPlugin.class.isAssignableFrom(clase)) {
            throw new IllegalArgumentException("La clase no cumple el contrato ProcesadorPlugin");
        }

        // Construir: constructor sin argumentos, decidido en tiempo de ejecucion
        Object objeto = clase.getDeclaredConstructor().newInstance();
        return (ProcesadorPlugin) objeto;
    }
}
