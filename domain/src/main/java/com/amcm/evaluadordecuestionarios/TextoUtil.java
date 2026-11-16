package com.amcm.evaluadordecuestionarios;

import java.text.Normalizer;
import java.util.Locale;

/** Normalizacion y conteo de texto compartido por los plugins (Java y Scala). */
public final class TextoUtil {

    private TextoUtil() {}

    /** Minusculas, sin tildes ni signos de puntuacion, con espacios simples. */
    public static String normalizar(String s) {
        if (s == null) return "";
        String d = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return d.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", " ").trim();
    }

    /** Cuenta solo letras (ignora espacios, digitos y signos). */
    public static int contarLetras(String s) {
        if (s == null) return 0;
        int n = 0;
        for (char c : s.toCharArray()) {
            if (Character.isLetter(c)) n++;
        }
        return n;
    }

    /** Cuenta palabras separadas por espacios, usando el texto normalizado. */
    public static int contarPalabras(String s) {
        String t = normalizar(s);
        return t.isEmpty() ? 0 : t.split(" ").length;
    }
}
