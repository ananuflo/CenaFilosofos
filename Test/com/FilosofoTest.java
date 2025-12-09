package com;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilosofoTest {

    /**
     * Caso de Prueba 3: Verifica que el hilo Filosofo termina correctamente tras una interrupción.
     */
    @Test
    void testTerminacionPorInterrupcion() throws InterruptedException {
        // Usamos un monitor dummy para evitar bloqueos reales
        MonitorFilosofos monitorDummy = new MonitorFilosofos(3); 
        Filosofo f = new Filosofo(0, monitorDummy);
        Thread t = new Thread(f, "Filosofo-Interrupcion");

        t.start(); // El hilo pasa a RUNNABLE
        
        // Dejar que se ejecute y entre en algún estado de espera (ej. pensando)
        Thread.sleep(100); 
        
        assertTrue(t.isAlive(), "El hilo debería estar vivo antes de la interrupción.");

        t.interrupt(); // Intentamos terminar el hilo
        
        // Esperamos un tiempo prudencial para que el hilo termine
        t.join(1000); 

        // Verificamos que el hilo ha finalizado su ejecución (isAlive() debe ser false)
        assertFalse(t.isAlive(), "El hilo Filosofo no terminó después de ser interrumpido.");
    }
}
