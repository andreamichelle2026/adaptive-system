package com.amcm.evaluadordecuestionarios.plugins;

import com.amcm.evaluadordecuestionarios.Contexto;
import com.amcm.evaluadordecuestionarios.Pregunta;
import com.amcm.evaluadordecuestionarios.ProcesadorPlugin;
import com.amcm.evaluadordecuestionarios.Resultado;
import com.amcm.evaluadordecuestionarios.TextoUtil;
import com.amcm.evaluadordecuestionarios.TipoPregunta;

/**
 * Plugin por defecto (Java): compara la respuesta con las respuestas aceptadas
 * de una pregunta CORTA. Soporta cualquier contexto, porque el selector le
 * deriva todos los casos que no cubren los otros dos plugins.
 */
public class ProcesadorSimple implements ProcesadorPlugin {

    @Override
    public String id() { return "simple"; }

    @Override
    public boolean soporta(Contexto contexto) { return true; }

    @Override
    public Resultado procesar(String entrada, Pregunta pregunta) {
        if (pregunta.getTipo() != TipoPregunta.CORTA) {
            return new Resultado("respuesta demasiado breve para una pregunta de tipo "
                    + pregunta.getTipo() + " (no se puede evaluar)", 0.0);
        }
        String respuesta = TextoUtil.normalizar(entrada);
        for (String aceptada : pregunta.getRespuestasAceptadas()) {
            if (respuesta.equals(TextoUtil.normalizar(aceptada))) {
                return new Resultado("correcta", 1.0);
            }
        }
        return new Resultado("incorrecta (se esperaba: "
                + String.join(" / ", pregunta.getRespuestasAceptadas()) + ")", 0.0);
    }
}
