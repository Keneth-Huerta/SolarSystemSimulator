package com.solarsim.physics;

/**
 * Clase utilitaria para calcular fuerzas gravitacionales entre cuerpos celestes.
 * Implementa la Ley de Gravitación Universal de Newton.
 */
public class GravitationalForce {
    
    /**
     * Calcula la fuerza gravitacional entre dos masas a una distancia dada.
     * Utiliza la fórmula F = G * (m1 * m2) / r²
     * 
     * @param mass1 Masa del primer cuerpo (en kilogramos)
     * @param mass2 Masa del segundo cuerpo (en kilogramos)
     * @param distance Distancia entre los cuerpos (en metros)
     * @return La fuerza gravitacional en Newtons
     * @throws IllegalArgumentException Si la distancia es cero
     */
    public static double calculateForce(double mass1, double mass2, double distance) {
        final double G = 6.67430e-11; // Gravitational constant
        if (distance == 0) {
            throw new IllegalArgumentException("Distance cannot be zero.");
        }
        return G * (mass1 * mass2) / (distance * distance);
    }
}