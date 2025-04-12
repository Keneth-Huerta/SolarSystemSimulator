package com.solarsim.physics;

/**
 * Clase que representa la órbita de un cuerpo celeste.
 * Almacena y calcula los parámetros orbitales utilizando los elementos keplerianos.
 */
public class Orbit {
    private double semiMajorAxis; // en kilómetros
    private double eccentricity; // adimensional
    private double inclination; // en grados
    private double longitudeOfAscendingNode; // en grados
    private double argumentOfPeriapsis; // en grados
    private double trueAnomaly; // en grados

    /**
     * Constructor para crear una órbita con todos sus parámetros keplerianos.
     * 
     * @param semiMajorAxis Semieje mayor de la órbita en kilómetros
     * @param eccentricity Excentricidad de la órbita (adimensional)
     * @param inclination Inclinación del plano orbital en grados
     * @param longitudeOfAscendingNode Longitud del nodo ascendente en grados
     * @param argumentOfPeriapsis Argumento del periapsis en grados
     * @param trueAnomaly Anomalía verdadera inicial en grados
     */
    public Orbit(double semiMajorAxis, double eccentricity, double inclination, 
                 double longitudeOfAscendingNode, double argumentOfPeriapsis, double trueAnomaly) {
        this.semiMajorAxis = semiMajorAxis;
        this.eccentricity = eccentricity;
        this.inclination = inclination;
        this.longitudeOfAscendingNode = longitudeOfAscendingNode;
        this.argumentOfPeriapsis = argumentOfPeriapsis;
        this.trueAnomaly = trueAnomaly;
    }

    /**
     * Actualiza la posición del cuerpo celeste basado en los parámetros orbitales y el tiempo.
     * Pendiente de implementar con las leyes de Kepler del movimiento planetario.
     * 
     * @param time Tiempo transcurrido desde la época de referencia
     */
    public void updatePosition(double time) {
        // Actualizar la posición del cuerpo celeste basado en los parámetros orbitales y el tiempo
        // Este método implementará las leyes de Kepler del movimiento planetario y otros cálculos necesarios
    }

    /**
     * Obtiene el semieje mayor de la órbita.
     * @return Semieje mayor en kilómetros
     */
    public double getSemiMajorAxis() {
        return semiMajorAxis;
    }

    /**
     * Establece el semieje mayor de la órbita.
     * @param semiMajorAxis Nuevo valor en kilómetros
     */
    public void setSemiMajorAxis(double semiMajorAxis) {
        this.semiMajorAxis = semiMajorAxis;
    }

    /**
     * Obtiene la excentricidad de la órbita.
     * @return Excentricidad (adimensional)
     */
    public double getEccentricity() {
        return eccentricity;
    }

    /**
     * Establece la excentricidad de la órbita.
     * @param eccentricity Nuevo valor (adimensional)
     */
    public void setEccentricity(double eccentricity) {
        this.eccentricity = eccentricity;
    }

    /**
     * Obtiene la inclinación del plano orbital.
     * @return Inclinación en grados
     */
    public double getInclination() {
        return inclination;
    }

    /**
     * Establece la inclinación del plano orbital.
     * @param inclination Nuevo valor en grados
     */
    public void setInclination(double inclination) {
        this.inclination = inclination;
    }

    /**
     * Obtiene la longitud del nodo ascendente.
     * @return Longitud en grados
     */
    public double getLongitudeOfAscendingNode() {
        return longitudeOfAscendingNode;
    }

    /**
     * Establece la longitud del nodo ascendente.
     * @param longitudeOfAscendingNode Nuevo valor en grados
     */
    public void setLongitudeOfAscendingNode(double longitudeOfAscendingNode) {
        this.longitudeOfAscendingNode = longitudeOfAscendingNode;
    }

    /**
     * Obtiene el argumento del periapsis.
     * @return Argumento en grados
     */
    public double getArgumentOfPeriapsis() {
        return argumentOfPeriapsis;
    }

    /**
     * Establece el argumento del periapsis.
     * @param argumentOfPeriapsis Nuevo valor en grados
     */
    public void setArgumentOfPeriapsis(double argumentOfPeriapsis) {
        this.argumentOfPeriapsis = argumentOfPeriapsis;
    }

    /**
     * Obtiene la anomalía verdadera.
     * @return Anomalía en grados
     */
    public double getTrueAnomaly() {
        return trueAnomaly;
    }

    /**
     * Establece la anomalía verdadera.
     * @param trueAnomaly Nuevo valor en grados
     */
    public void setTrueAnomaly(double trueAnomaly) {
        this.trueAnomaly = trueAnomaly;
    }
}