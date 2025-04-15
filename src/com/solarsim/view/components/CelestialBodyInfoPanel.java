package com.solarsim.view.components;

import com.solarsim.model.CelestialBody;
import com.solarsim.model.Planet;
import com.solarsim.model.Star;
import com.solarsim.model.Moon;
import com.solarsim.controller.SimulationController;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

/**
 * Panel lateral que muestra información detallada sobre un cuerpo celeste seleccionado.
 * Esta clase se encarga de formatear y presentar los datos de planetas, lunas y estrellas.
 */
public class CelestialBodyInfoPanel extends ScrollPane {
    private VBox contentBox;
    private Label titleLabel;
    private VBox propertiesBox;
    private CelestialBody currentBody;
    private SimulationController controller;

    /**
     * Constructor que inicializa el panel de información.
     */
    public CelestialBodyInfoPanel() {
        // Configurar el contenedor principal
        contentBox = new VBox(10);
        contentBox.setPadding(new Insets(15));
        contentBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8);");
        
        // Título del panel
        titleLabel = new Label("Información del cuerpo celeste");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        titleLabel.setTextFill(Color.WHITE);
        
        // Contenedor para propiedades
        propertiesBox = new VBox(8);
        
        // Mensaje inicial
        Label initialMsg = new Label("Seleccione un cuerpo celeste para ver su información");
        initialMsg.setTextFill(Color.LIGHTGRAY);
        initialMsg.setWrapText(true);
        
        // Añadir componentes al panel
        contentBox.getChildren().addAll(titleLabel, new Separator(), initialMsg);
        
        // Configurar el ScrollPane
        this.setContent(contentBox);
        this.setFitToWidth(true);
        this.setPrefWidth(250);
        this.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        // Por defecto, el panel no está visible hasta que se seleccione un cuerpo
        this.setVisible(false);
    }
    
    /**
     * Establece el controlador para poder acceder al factor de escala.
     * @param controller El controlador de la simulación
     */
    public void setController(SimulationController controller) {
        this.controller = controller;
    }
    
    /**
     * Actualiza el panel con la información del cuerpo celeste seleccionado.
     * @param body El cuerpo celeste cuya información se mostrará
     */
    public void updateInfo(CelestialBody body) {
        if (body == null) {
            this.setVisible(false);
            return;
        }
        
        this.currentBody = body;
        
        // Asegurar que el panel sea visible y tenga un ancho adecuado
        this.setVisible(true);
        this.setPrefWidth(250);
        this.setMinWidth(250);
        
        // Establecer un estilo para que el panel sea más visible
        this.setStyle("-fx-background: rgba(0, 0, 0, 0.8); -fx-background-color: rgba(0, 0, 0, 0.8); -fx-border-color: #444; -fx-border-width: 1;");
        
        // Limpiar panel anterior
        propertiesBox.getChildren().clear();
        contentBox.getChildren().clear();
        
        // Actualizar título según el tipo de cuerpo celeste
        String bodyType = "";
        if (body instanceof Star) {
            bodyType = "Estrella";
            titleLabel.setTextFill(Color.YELLOW);
        } else if (body instanceof Planet) {
            bodyType = "Planeta";
            titleLabel.setTextFill(Color.LIGHTBLUE);
        } else if (body instanceof Moon) {
            bodyType = "Luna";
            titleLabel.setTextFill(Color.LIGHTGRAY);
        }
        
        titleLabel.setText(bodyType + ": " + body.getName());
        
        // Añadir propiedades básicas
        addProperty("Masa", formatMass(body.getMass()) + " kg");
        addProperty("Radio", formatRadius(body.getRadius()) + " km");
        
        // Propiedades específicas según el tipo
        if (body instanceof Star) {
            Star star = (Star) body;
            addProperty("Temperatura", star.getTemperature() + " K");
            addProperty("Luminosidad", formatLuminosity(star.getLuminosity()) + " W");
        } else if (body instanceof Planet) {
            Planet planet = (Planet) body;
            
            // Usar el factor de escala actual del controlador
            double scaleFactor = getZoomFactor();
            
            addProperty("Distancia orbital", formatDistance(planet.getOrbitalRadius() / scaleFactor) + " km");
            addProperty("Período orbital", planet.getOrbitalPeriod() + " días");
            
            // Posición actual
            double[] pos = planet.getPosition();
            addProperty("Posición X", String.format("%.2f", pos[0] / scaleFactor) + " km");
            addProperty("Posición Z", String.format("%.2f", pos[2] / scaleFactor) + " km");
        } else if (body instanceof Moon) {
            Moon moon = (Moon) body;
            Planet parent = moon.getParentPlanet();
            
            // Usar el factor de escala actual del controlador
            double scaleFactor = getZoomFactor();
            
            addProperty("Planeta padre", parent.getName());
            addProperty("Distancia orbital", formatDistance(moon.getOrbitalRadius() / scaleFactor) + " km");
            
            // Posición relativa
            double[] planetPos = parent.getPosition();
            double[] moonPos = moon.getPosition();
            double dx = moonPos[0] - planetPos[0];
            double dz = moonPos[2] - planetPos[2];
            addProperty("Distancia al planeta", String.format("%.2f", Math.sqrt(dx*dx + dz*dz) / scaleFactor) + " km");
        }
        
        // Añadir componentes al panel
        contentBox.getChildren().addAll(titleLabel, new Separator(), propertiesBox);
    }
    
    /**
     * Añade una propiedad al panel de información.
     * @param name Nombre de la propiedad
     * @param value Valor de la propiedad
     */
    private void addProperty(String name, String value) {
        HBox propertyBox = new HBox(10);
        propertyBox.setAlignment(Pos.CENTER_LEFT);
        
        Label nameLabel = new Label(name + ":");
        nameLabel.setTextFill(Color.LIGHTGRAY);
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        nameLabel.setPrefWidth(120);
        
        Label valueLabel = new Label(value);
        valueLabel.setTextFill(Color.WHITE);
        valueLabel.setWrapText(true);
        
        propertyBox.getChildren().addAll(nameLabel, valueLabel);
        propertiesBox.getChildren().add(propertyBox);
    }
    
    /**
     * Obtiene el cuerpo celeste actualmente mostrado.
     * @return El cuerpo celeste actual
     */
    public CelestialBody getCurrentBody() {
        return currentBody;
    }
    
    /**
     * Formatea el valor de masa para mostrarlo en notación científica.
     * @param mass Valor de masa en kilogramos
     * @return Cadena formateada
     */
    private String formatMass(double mass) {
        if (mass >= 1e24) {
            return String.format("%.3f × 10^24", mass / 1e24);
        } else if (mass >= 1e21) {
            return String.format("%.3f × 10^21", mass / 1e21);
        } else {
            return String.format("%.3e", mass);
        }
    }
    
    /**
     * Formatea el valor de radio para mostrarlo correctamente.
     * @param radius Valor de radio en kilómetros
     * @return Cadena formateada
     */
    private String formatRadius(double radius) {
        if (radius >= 10000) {
            return String.format("%.1f", radius);
        } else {
            return String.format("%.2f", radius);
        }
    }
    
    /**
     * Formatea el valor de luminosidad para mostrarlo en notación científica.
     * @param luminosity Valor de luminosidad en vatios
     * @return Cadena formateada
     */
    private String formatLuminosity(double luminosity) {
        return String.format("%.2e", luminosity);
    }
    
    /**
     * Formatea el valor de distancia para mostrarlo correctamente.
     * @param distance Valor de distancia en kilómetros
     * @return Cadena formateada
     */
    private String formatDistance(double distance) {
        if (distance >= 1e9) {
            return String.format("%.2f × 10^9", distance / 1e9);
        } else if (distance >= 1e6) {
            return String.format("%.2f × 10^6", distance / 1e6);
        } else {
            return String.format("%.0f", distance);
        }
    }
    
    /**
     * Clase separador personalizado para el panel.
     */
    private class Separator extends javafx.scene.shape.Line {
        public Separator() {
            this.setStartX(0);
            this.setEndX(230);
            this.setStroke(Color.GRAY);
            this.setOpacity(0.5);
            VBox.setMargin(this, new Insets(5, 0, 5, 0));
        }
    }
    
    /**
     * Obtiene el factor de zoom aplicado en la simulación.
     * @return Factor de zoom
     */
    private double getZoomFactor() {
        // Si el controlador está disponible, usar su factor de escala
        if (controller != null) {
            return controller.getCurrentScaleFactor();
        }
        
        // Valor por defecto si no hay controlador
        return 1.0 / 5000000.0;
    }
}