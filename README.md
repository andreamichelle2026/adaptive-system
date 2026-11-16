# adaptive-system

Evaluador de cuestionarios con plugins. Cada respuesta se mide (letras y palabras),
un selector decide que plugin la evalua y el plugin se carga por **reflection**.

## Estructura

```
adaptive-system/
├── plugin-api/       contrato Java: ProcesadorPlugin
├── domain/           entidades y reglas: Pregunta, Concepto, Contexto, Resultado,
│                     RegistroEjecucion, TipoPregunta, BancoPreguntas, TextoUtil, Reglas
├── adaptive-core/    Monitor, SelectorPlugin, CargadorPlugin (reflection), Bitacora
├── plugins-java/     ProcesadorSimple, ProcesadorAlternativas
├── plugins-scala/    ProcesadorConceptos
├── persistence/      RepositorioRegistros (JDBC)
├── launcher/         Main
├── pom.xml
├── mvnw / mvnw.cmd   (se generan con un comando, ver abajo)
└── README.md
```

## Dependencias entre modulos

```
domain  <-  plugin-api  <-  adaptive-core  <-  launcher
   ^             ^                               |  |  (runtime)
   |             +--- plugins-java  <------------+  |
   |             +--- plugins-scala <---------------+
   +---- persistence <---------------------------- launcher
```

- `launcher` **no importa** los plugins: solo los tiene en el classpath (scope `runtime`)
  y `CargadorPlugin` los carga por su nombre. Si quitan `plugins-scala` del `launcher`,
  la pregunta abierta se registra como `ERROR` en lugar de fallar la compilacion.
- Ningun plugin depende de `adaptive-core`. El umbral compartido vive en `domain/Reglas`.

## Reglas de seleccion

| Condicion | Plugin |
|---|---|
| letras = 1 y palabras = 1 | `ProcesadorAlternativas` |
| letras > 1 y palabras = 1 | `ProcesadorSimple` |
| letras > `Reglas.LETRAS_MINIMO_CONCEPTOS` (5) y palabras > 1 | `ProcesadorConceptos` (Scala) |
| cualquier otro caso | `ProcesadorSimple` (por defecto) |

Para cambiar el umbral, edite **solo** `domain/.../Reglas.java`.

## Requisitos

JDK 21 y Maven 3.9 (o el wrapper). Scala 2.13 lo descarga Maven.

## Generar el wrapper (mvnw / mvnw.cmd)

```
mvn wrapper:wrapper -Dmaven=3.9.9
```

## Compilar y ejecutar

```
./mvnw clean install          # mvnw.cmd en Windows
./mvnw -pl launcher exec:java
```

La corrida se guarda en una base H2 local (`evaluador-db.mv.db`, en la carpeta
desde donde se ejecuta). Si no se puede guardar, el programa lo avisa y continua.
