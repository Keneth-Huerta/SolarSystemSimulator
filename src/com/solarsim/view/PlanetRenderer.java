package com.solarsim.view;

import com.solarsim.model.Planet;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Rotate;
import javafx.scene.image.WritableImage;

/**
 * Clase responsable de renderizar planetas en 3D.
 * Maneja las transformaciones de coordenadas y la aplicación del zoom.
 */
public class PlanetRenderer {

    private final int ancho;
    private final int alto;
    private final double zoomFactor;
    private final int offsetX;
    private final int offsetY;
    private final Camera3D camera;

    /**
     * Constructor con valores predeterminados para zoom y desplazamiento.
     * @param ancho Ancho del área de renderizado
     * @param alto Alto del área de renderizado
     */
    public PlanetRenderer(int ancho, int alto) {
        this.ancho = ancho > 0 ? ancho : 800;
        this.alto = alto > 0 ? alto : 600;
        this.zoomFactor = 1.0; // Valor predeterminado
        this.offsetX = 0;
        this.offsetY = 0;
        this.camera = new Camera3D(0f, 0f, -1000f, 0f, 0f, 0f, 0f, 1f, 0f);
    }

    /**
     * Constructor con factor de zoom personalizado.
     * @param ancho Ancho del área de renderizado
     * @param alto Alto del área de renderizado
     * @param zoomFactor Factor de zoom a aplicar
     */
    public PlanetRenderer(int ancho, int alto, double zoomFactor) {
        this.ancho = ancho > 0 ? ancho : 800;
        this.alto = alto > 0 ? alto : 600;
        this.zoomFactor = zoomFactor;
        this.offsetX = 0;
        this.offsetY = 0;
        this.camera = new Camera3D(0f, 0f, (float)(-1000 / zoomFactor), 0f, 0f, 0f, 0f, 1f, 0f);
    }
    
    /**
     * Constructor completo con zoom y desplazamiento personalizados.
     * @param ancho Ancho del área de renderizado
     * @param alto Alto del área de renderizado
     * @param zoomFactor Factor de zoom a aplicar
     * @param offsetX Desplazamiento horizontal
     * @param offsetY Desplazamiento vertical
     */
    public PlanetRenderer(int ancho, int alto, double zoomFactor, int offsetX, int offsetY) {
        this.ancho = ancho > 0 ? ancho : 800;
        this.alto = alto > 0 ? alto : 600;
        this.zoomFactor = zoomFactor;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.camera = new Camera3D(
            (float)(offsetX / 10.0), 
            (float)(offsetY / 10.0), 
            (float)(-1000 / zoomFactor), 
            0f, 0f, 0f, 
            0f, 1f, 0f
        );
    }

    /**
     * Crea una representación 3D de un planeta para ser añadida a la escena JavaFX.
     * @param planeta Planeta a renderizar
     * @return Objeto Sphere que representa el planeta en 3D
     */
    public Sphere createPlanet3D(Planet planeta) {
        double tamaño = planeta.getSize() * zoomFactor;
        if (tamaño < 2) tamaño = 2; // Tamaño mínimo para visibilidad
        
        Sphere esfera = new Sphere(tamaño);
        
        // Convertir java.awt.Color a javafx.scene.paint.Color
        java.awt.Color awtColor = planeta.getColor();
        Color fxColor = Color.rgb(
            awtColor.getRed(),
            awtColor.getGreen(),
            awtColor.getBlue(),
            awtColor.getAlpha() / 255.0
        );
        
        // Crear material con iluminación para efecto 3D
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(fxColor);
        material.setSpecularColor(Color.WHITE);
        esfera.setMaterial(material);
        
        // Posicionar el planeta en el espacio 3D
        double x = planeta.getOrbitalRadius() * Math.cos(planeta.getCurrentAngle()) * zoomFactor;
        double z = planeta.getOrbitalRadius() * Math.sin(planeta.getCurrentAngle()) * zoomFactor;
        
        esfera.setTranslateX(x);
        esfera.setTranslateY(0); // Mantener en el plano horizontal por ahora
        esfera.setTranslateZ(z);
        
        return esfera;
    }
    
    /**
     * Crea una representación 3D de la órbita del planeta.
     * @param planeta Planeta cuya órbita se va a renderizar
     * @return Grupo con la órbita renderizada como un círculo en el espacio 3D
     */
    public Group createOrbit3D(Planet planeta) {
        Group orbitGroup = new Group();
        
        javafx.scene.shape.Circle orbita = new Circle(
            0, 0, 
            planeta.getOrbitalRadius() * zoomFactor
        );
        
        orbita.setFill(javafx.scene.paint.Color.TRANSPARENT);
        orbita.setStroke(javafx.scene.paint.Color.rgb(70, 70, 70));
        orbita.setStrokeWidth(1);
        
        // Rotar el círculo 90 grados para que quede en el plano XZ
        orbita.getTransforms().add(new Rotate(90, Rotate.X_AXIS));
        
        orbitGroup.getChildren().add(orbita);
        return orbitGroup;
    }
    
    /**
     * Crea una esfera 3D que representa el Sol.
     * @param tamaño Tamaño del sol
     * @return Esfera con material brillante para representar el Sol
     */
    public Sphere createSun3D(double tamaño) {
        Sphere sol = new Sphere(tamaño * zoomFactor);
        
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.YELLOW);
        material.setSpecularColor(Color.WHITE);
        
        // Corrección: Usar el método apropiado para el efecto de emisión de luz (glow)
        // Crear un mapa de emisión para hacer que el sol brille
        WritableImage emissionMap = new WritableImage(1, 1);
        emissionMap.getPixelWriter().setColor(0, 0, Color.YELLOW);
        material.setDiffuseMap(emissionMap);
        
        // Configurar brillo adicional
        material.setSpecularPower(10.0);
        
        sol.setMaterial(material);
        sol.setDrawMode(DrawMode.FILL);
        
        return sol;
    }
    
    /**
     * Obtiene la cámara 3D con la configuración actual.
     * @return Objeto Camera3D configurado
     */
    public Camera3D getCamera() {
        return camera;
    }
    
    /**
     * Obtiene el ancho del área de renderizado.
     * @return Ancho en píxeles
     */
    public int getAncho() {
        return ancho;
    }
    
    /**
     * Obtiene el alto del área de renderizado.
     * @return Alto en píxeles
     */
    public int getAlto() {
        return alto;
    }
    
    /**
     * Obtiene el desplazamiento horizontal.
     * @return Desplazamiento en píxeles
     */
    public int getOffsetX() {
        return offsetX;
    }
    
    /**
     * Obtiene el desplazamiento vertical.
     * @return Desplazamiento en píxeles
     */
    public int getOffsetY() {
        return offsetY;
    }
    
    /**
     * Obtiene el factor de zoom actual.
     * @return Factor de zoom
     */
    public double getZoomFactor() {
        return zoomFactor;
    }
}