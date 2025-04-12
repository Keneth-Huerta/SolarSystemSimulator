package com.solarsim.view;

/**
 * Clase que representa una cámara en espacio 3D para visualización.
 * Permite el posicionamiento y la orientación de la cámara en el espacio tridimensional.
 */
public class Camera3D {
    private float positionX;
    private float positionY;
    private float positionZ;
    private float lookAtX;
    private float lookAtY;
    private float lookAtZ;
    private float upX;
    private float upY;
    private float upZ;

    /**
     * Constructor que inicializa todos los parámetros de la cámara.
     * @param positionX Posición X de la cámara
     * @param positionY Posición Y de la cámara
     * @param positionZ Posición Z de la cámara
     * @param lookAtX Coordenada X del punto hacia donde mira la cámara
     * @param lookAtY Coordenada Y del punto hacia donde mira la cámara
     * @param lookAtZ Coordenada Z del punto hacia donde mira la cámara
     * @param upX Componente X del vector "arriba"
     * @param upY Componente Y del vector "arriba"
     * @param upZ Componente Z del vector "arriba"
     */
    public Camera3D(float positionX, float positionY, float positionZ, 
                    float lookAtX, float lookAtY, float lookAtZ, 
                    float upX, float upY, float upZ) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.positionZ = positionZ;
        this.lookAtX = lookAtX;
        this.lookAtY = lookAtY;
        this.lookAtZ = lookAtZ;
        this.upX = upX;
        this.upY = upY;
        this.upZ = upZ;
    }

    /**
     * Actualiza la posición de la cámara.
     * @param deltaX Cambio en la coordenada X
     * @param deltaY Cambio en la coordenada Y
     * @param deltaZ Cambio en la coordenada Z
     */
    public void updateCameraPosition(float deltaX, float deltaY, float deltaZ) {
        positionX += deltaX;
        positionY += deltaY;
        positionZ += deltaZ;
    }

    /**
     * Establece el punto hacia donde mira la cámara.
     * @param lookAtX Coordenada X del punto hacia donde mira la cámara
     * @param lookAtY Coordenada Y del punto hacia donde mira la cámara
     * @param lookAtZ Coordenada Z del punto hacia donde mira la cámara
     */
    public void setLookAt(float lookAtX, float lookAtY, float lookAtZ) {
        this.lookAtX = lookAtX;
        this.lookAtY = lookAtY;
        this.lookAtZ = lookAtZ;
    }

    /**
     * Establece el vector "arriba" de la cámara.
     * @param upX Componente X del vector "arriba"
     * @param upY Componente Y del vector "arriba"
     * @param upZ Componente Z del vector "arriba"
     */
    public void setUpVector(float upX, float upY, float upZ) {
        this.upX = upX;
        this.upY = upY;
        this.upZ = upZ;
    }

    /**
     * Obtiene la posición actual de la cámara.
     * @return Array con las coordenadas [X, Y, Z] de la posición
     */
    public float[] getPosition() {
        return new float[]{positionX, positionY, positionZ};
    }

    /**
     * Obtiene el punto hacia donde mira la cámara.
     * @return Array con las coordenadas [X, Y, Z] del punto objetivo
     */
    public float[] getLookAt() {
        return new float[]{lookAtX, lookAtY, lookAtZ};
    }

    /**
     * Obtiene el vector "arriba" de la cámara.
     * @return Array con las componentes [X, Y, Z] del vector "arriba"
     */
    public float[] getUpVector() {
        return new float[]{upX, upY, upZ};
    }
}