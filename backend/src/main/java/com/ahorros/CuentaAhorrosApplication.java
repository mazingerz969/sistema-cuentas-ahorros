package com.ahorros;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicación Spring Boot para gestión de cuentas de ahorros.
 * 
 * Esta clase es el punto de entrada de la aplicación. Spring Boot automáticamente:
 * - Configura el contexto de la aplicación
 * - Escanea los componentes en el paquete com.ahorros y subpaquetes
 * - Configura la base de datos H2
 * - Inicia el servidor embebido Tomcat
 * 
 * @author Tu Nombre
 * @version 1.0
 */
@SpringBootApplication
public class CuentaAhorrosApplication {

    /**
     * Método principal que inicia la aplicación Spring Boot.
     * 
     * @param args Argumentos de línea de comandos (no utilizados en este caso)
     */
    public static void main(String[] args) {
        // Inicia la aplicación Spring Boot
        // SpringApplication.run() configura automáticamente:
        // - El contexto de la aplicación
        // - Los beans de Spring
        // - El servidor web embebido
        // - La conexión a la base de datos
        SpringApplication.run(CuentaAhorrosApplication.class, args);
        
        // Mensaje de confirmación de inicio
        System.out.println("==========================================");
        System.out.println("🚀 APLICACIÓN DE CUENTA DE AHORROS INICIADA");
        System.out.println("==========================================");
        System.out.println("📍 API disponible en: http://localhost:8080/api");
        System.out.println("📊 H2 Console: http://localhost:8080/h2-console");
        System.out.println("📚 Swagger UI: http://localhost:8080/swagger-ui.html");
        System.out.println("==========================================");
    }
} 