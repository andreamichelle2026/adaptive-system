package com.amcm.evaluadordecuestionarios.plugins

import com.amcm.evaluadordecuestionarios.{Contexto, Pregunta, ProcesadorPlugin, Reglas, Resultado, TextoUtil, TipoPregunta}
import scala.jdk.CollectionConverters._

/**
 * Plugin (Scala): evalua una pregunta LARGA con una rubrica de conceptos.
 * Un concepto se cubre si la respuesta contiene alguno de sus sinonimos.
 * Nota = peso de los conceptos cubiertos / peso total (entre 0.0 y 1.0).
 */
class ProcesadorConceptos extends ProcesadorPlugin {

  override def id(): String = "conceptos"

  override def soporta(contexto: Contexto): Boolean =
    contexto.getCantidadLetras > Reglas.LETRAS_MINIMO_CONCEPTOS && contexto.getCantidadPalabras > 1

  override def procesar(entrada: String, pregunta: Pregunta): Resultado = {
    if (pregunta.getTipo != TipoPregunta.LARGA)
      return new Resultado("la pregunta no es de respuesta abierta (sin rubrica de conceptos)", 0.0)

    val texto     = TextoUtil.normalizar(entrada)
    val conceptos = pregunta.getConceptos.asScala.toList
    val pesoTotal = conceptos.map(_.getPeso).sum

    val (cubiertos, faltantes) = conceptos.partition { c =>
      c.getSinonimos.asScala.exists(s => texto.contains(TextoUtil.normalizar(s)))
    }

    val pesoLogrado = cubiertos.map(_.getPeso).sum
    val nota        = if (pesoTotal == 0.0) 0.0 else pesoLogrado / pesoTotal

    val resumen =
      s"cubiertos ${cubiertos.size}/${conceptos.size}: " +
      (if (cubiertos.isEmpty) "ninguno" else cubiertos.map(_.getNombre).mkString(", ")) +
      (if (faltantes.isEmpty) "" else s" | faltan: ${faltantes.map(_.getNombre).mkString(", ")}")

    new Resultado(resumen, nota)
  }
}
