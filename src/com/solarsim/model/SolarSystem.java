package com.solarsim.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa un sistema solar completo con múltiples cuerpos celestes.
 * Gestiona el conjunto de planetas, estrellas y otros objetos astronómicos.
 */
public class SolarSystem {
    private List<CelestialBody> celestialBodies;

    /**
     * Constructor que inicializa un sistema solar vacío.
     */
    public SolarSystem() {
        celestialBodies = new ArrayList<>();
    }

    /**
     * Añade un cuerpo celeste al sistema solar.
     * @param body El cuerpo celeste a añadir
     */
    public void addCelestialBody(CelestialBody body) {
        celestialBodies.add(body);
    }

    /**
     * Simula el movimiento de todos los cuerpos celestes para un paso de tiempo.
     * @param timeStep El paso de tiempo para la simulación en días
     */
    public void simulateMovement(double timeStep) {
        for (CelestialBody body : celestialBodies) {
            body.updatePosition(timeStep);
        }
    }

    /**
     * Obtiene la lista de todos los cuerpos celestes en el sistema.
     * @return Lista de cuerpos celestes
     */
    public List<CelestialBody> getCelestialBodies() {
        return celestialBodies;
    }
}