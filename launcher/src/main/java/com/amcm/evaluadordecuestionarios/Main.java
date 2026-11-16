package com.amcm.evaluadordecuestionarios;

import com.amcm.evaluadordecuestionarios.persistence.RepositorioRegistros;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Cuestionario de fundamentos de Java. Por cada pregunta:
 *  1) Monitor mide la respuesta y construye el Contexto (letras y palabras);
 *  2) SelectorPlugin devuelve el nombre de la clase a usar y el motivo;
 *  3) CargadorPlugin la crea con reflection y valida que cumpla ProcesadorPlugin;
 *  4) se ejecuta procesar(entrada, pregunta) y se registra el resultado.
 * Al final, la corrida se guarda en la base de datos (modulo persistence).
 */
public class Main {

    private static final String DB_URL = "jdbc:h2:file:./evaluador-db";

    public static void main(String[] args) {
        List<Pregunta> preguntas = BancoPreguntas.todas();
        List<RegistroEjecucion> registros = new ArrayList<>();
        Scanner teclado = new Scanner(System.in);

        System.out.println("=== Cuestionario: fundamentos de Java (plugins Java + Scala) ===");
        System.out.println("Responde cada pregunta y pulsa Enter. Son " + preguntas.size() + " preguntas.");

        for (Pregunta pregunta : preguntas) {
            System.out.printf("%nPregunta %d/%d [%s]%n%s%n",
                    pregunta.getId(), preguntas.size(), pregunta.getTipo(), pregunta.getEnunciado());
            if (pregunta.getTipo() == TipoPregunta.ALTERNATIVAS) {
                char letra = 'A';
                for (String opcion : pregunta.getOpciones()) {
                    System.out.println("  " + letra + ") " + opcion);
                    letra++;
                }
            }
            System.out.print("> ");
            String entrada = teclado.hasNextLine() ? teclado.nextLine() : "";

            // 1) El Monitor mide y construye el Contexto
            Contexto contexto = Monitor.crearContexto(entrada);
            Bitacora.registrar("Contexto", contexto.getCantidadLetras() + " letras, "
                    + contexto.getCantidadPalabras() + " palabras.");

            // 2) Regla de seleccion
            String nombreClase = SelectorPlugin.seleccionar(contexto);
            String motivo = SelectorPlugin.motivo(contexto);
            Bitacora.registrar("Selector", motivo);

            try {
                // 3) Reflection + validacion del contrato
                ProcesadorPlugin plugin = CargadorPlugin.crear(nombreClase);
                if (!plugin.soporta(contexto)) {
                    throw new IllegalStateException("el plugin '" + plugin.id() + "' no soporta este contexto");
                }

                // 4) Ejecutar y registrar
                Resultado resultado = plugin.procesar(entrada, pregunta);
                Bitacora.registrar("Ejecución", resultado.getResumen());

                System.out.println();
                System.out.println("  Plugin usado : " + plugin.id());
                System.out.println("  Clase cargada: " + nombreClase);
                System.out.println("  Contrato     : cumple ProcesadorPlugin (validado)");
                System.out.println("  Motivo       : " + motivo);
                System.out.println("  Resultado    : " + resultado.getResumen());
                System.out.printf("  Nota         : %.2f%n", resultado.getNota());

                registros.add(new RegistroEjecucion(pregunta.getId(), pregunta.getTipo(),
                        plugin.id(), nombreClase, resultado.getNota(), "OK"));
            } catch (Exception e) {
                Bitacora.registrar("Error", String.valueOf(e.getMessage()));
                System.out.println();
                System.out.println("  No se pudo ejecutar el plugin " + nombreClase + ": " + e.getMessage());
                registros.add(new RegistroEjecucion(pregunta.getId(), pregunta.getTipo(),
                        "-", nombreClase, 0.0, "ERROR"));
            }

            System.out.println();
            System.out.println("  --- Bitacora ---");
            Bitacora.imprimirYLimpiar();
        }

        imprimirRegistro(registros);
        guardar(registros);
    }

    private static void guardar(List<RegistroEjecucion> registros) {
        try {
            RepositorioRegistros repo = new RepositorioRegistros(DB_URL, "sa", "");
            repo.guardarEjecucion(registros);
            System.out.println("Corrida guardada en la base de datos (corridas guardadas: "
                    + repo.contarEjecuciones() + ").");
        } catch (SQLException e) {
            System.out.println("No se pudo guardar en la base de datos: " + e.getMessage());
        }
    }

    private static void imprimirRegistro(List<RegistroEjecucion> registros) {
        final String formato = "%-4s %-12s %-12s %-22s %-7s %5s%n";
        System.out.println();
        System.out.println("=== Registro de ejecucion ===");
        System.out.printf(formato, "P", "Tipo", "Plugin", "Clase", "Estado", "Nota");
        System.out.println("-".repeat(67));

        double total = 0.0;
        for (RegistroEjecucion r : registros) {
            total += r.nota();
            String clase = r.nombreClase().substring(r.nombreClase().lastIndexOf('.') + 1);
            System.out.printf("P%-3d %-12s %-12s %-22s %-7s %5.2f%n",
                    r.idPregunta(), r.tipo(), r.idPlugin(), clase, r.estado(), r.nota());
        }
        System.out.println("-".repeat(67));
        System.out.printf("Nota final: %.1f / %d  (equivalente a %.1f / 10)%n",
                total, registros.size(), registros.isEmpty() ? 0.0 : total * 10.0 / registros.size());
    }
}
