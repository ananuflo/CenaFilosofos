# 🍽️ Solución al Problema de la Cena de los Filósofos (Monitor de Hoare en Java)

Este proyecto implementa una solución al clásico problema de la Cena de los Filósofos propuesto por Edsger Dijkstra, utilizando el patrón Monitor de Hoare en Java para la sincronización.

## 📁 Estructura del Proyecto

* `src/main/java`: Código fuente de la aplicación principal.
    * `CenaFilosofos.java`: Punto de entrada (`main`).
    * `Filosofo.java`: Hilo que modela el comportamiento del filósofo.
    * `MonitorFilosofos.java`: Clase que centraliza la sincronización y la prevención de deadlock.
* `src/test/java`: Pruebas unitarias con JUnit 5.
    * `MonitorFilosofosTest.java`: Verifica la exclusión mutua y la ausencia de deadlock.
    * `FilosofoTest.java`: Prueba el comportamiento del hilo y su respuesta a interrupciones.
* `doc/memoria_tecnica.pdf`: Documentación detallada del diseño concurrente, pruebas y capturas de depuración.

## 🚀 Compilación y Ejecución

**Requisitos:** Java Development Kit (JDK) 8 o superior.

### 1. Compilación (usando Maven/Gradle o manual)

Si estás usando una herramienta de construcción (recomendado), usa:
```bash
# Ejemplo con Maven
mvn clean install
