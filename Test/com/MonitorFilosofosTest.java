package com;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class MonitorFilosofosTest {

    private static final int N = 5;
    private MonitorFilosofos monitor;
    private List<Thread> filosofoThreads;

    /**
     * Configuración inicial antes de cada prueba.
     */
    @BeforeEach
    void setUp() {
        monitor = new MonitorFilosofos(N);
        filosofoThreads = new ArrayList<>();
    }

    /**
     * Limpieza después de cada prueba. Interrumpe los hilos.
     */
    @AfterEach
    void tearDown() {
        for (Thread t : filosofoThreads) {
            if (t.isAlive()) {
                t.interrupt();
            }
        }
        // Espera un poco para asegurar que los hilos terminan.
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Caso de Prueba 1: Verifica la Exclusión Mutua (Invariante de Seguridad).
     * Asegura que dos filósofos vecinos nunca pueden estar COMIENDO simultáneamente.
     */
    @Test
    void testExclusionMutuaVecinos() throws InterruptedException {
        final AtomicBoolean invarianteViolada = new AtomicBoolean(false);

        // Creamos una simulación concurrente
        for (int i = 0; i < N; i++) {
            final int id = i;
            Runnable verificador = () -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        // El filósofo intenta comer
                        monitor.tomarTenedores(id);
                        
                        // *** Zona Crítica: Verificación Invariante ***
                        // En este punto, el filósofo 'id' está COMIENDO.
                        
                        synchronized (monitor) { 
                            // Obtenemos el lock para verificar el estado de forma atómica
                            int izq = monitor.getLeftIndex(id); // Asumiendo que añades un getter para el índice izq en MonitorFilosofos
                            int der = monitor.getRightIndex(id); // Asumiendo que añades un getter para el índice der en MonitorFilosofos
                            
                            // Si alguno de los vecinos está COMIENDO, la invariante se viola.
                            if (monitor.getEstado(izq) == MonitorFilosofos.COMIENDO || 
                                monitor.getEstado(der) == MonitorFilosofos.COMIENDO) {
                                invarianteViolada.set(true);
                                System.err.println("¡INVARIANTE VIOLADA! F" + id + " comiendo, Vecino en F" + (monitor.getEstado(izq) == MonitorFilosofos.COMIENDO ? izq : der) + " también comiendo.");
                            }
                        }
                        
                        // Simulación de tiempo de comida (corto para forzar concurrencia)
                        Thread.sleep(50); 
                        
                        monitor.dejarTenedores(id);
                        
                        // Simulación de tiempo de pensamiento
                        Thread.sleep(50); 
                    }
                } catch (InterruptedException e) {
                    // Esperado al final de la prueba
                }
            };
            Thread t = new Thread(verificador, "Test-Filosofo-" + i);
            filosofoThreads.add(t);
            t.start();
        }

        // Ejecutar la prueba por un tiempo fijo para forzar interacciones
        Thread.sleep(3000); 

        assertFalse(invarianteViolada.get(), "La invariante de exclusión mutua de vecinos ha sido violada.");
    }

    /**
     * Caso de Prueba 2: Verifica la Ausencia de Interbloqueo (Deadlock).
     * Asegura que el sistema no se queda bloqueado indefinidamente.
     */
    @Test
    void testAusenciaDeDeadlock() throws InterruptedException {
        // Contador para saber cuántas veces ha comido cada filósofo.
        final int[] conteoComidas = new int[N];
        
        for (int i = 0; i < N; i++) {
            final int id = i;
            Runnable filosofo = () -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        monitor.tomarTenedores(id);
                        // Ha comido: incrementamos el contador atómicamente
                        synchronized (conteoComidas) {
                            conteoComidas[id]++;
                        }
                        Thread.sleep(10); // Simular comida corta
                        monitor.dejarTenedores(id);
                        Thread.sleep(10); // Simular pensamiento corto
                    }
                } catch (InterruptedException e) {
                    // Esperado
                }
            };
            Thread t = new Thread(filosofo, "Test-Deadlock-Filosofo-" + i);
            filosofoThreads.add(t);
            t.start();
        }

        // Dejar que se ejecute la simulación por un tiempo fijo
        Thread.sleep(4000); 

        // Verificar que todos han comido al menos una cantidad mínima de veces.
        for (int i = 0; i < N; i++) {
            assertTrue(conteoComidas[i] > 5, "El Filósofo " + i + " no comió lo suficiente, posible inanición/deadlock.");
        }
    }
}
