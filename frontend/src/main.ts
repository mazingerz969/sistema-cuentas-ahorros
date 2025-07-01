import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';

/**
 * Punto de entrada principal de la aplicación Angular.
 * 
 * Este archivo inicia la aplicación Angular usando bootstrapApplication,
 * que es el método recomendado para aplicaciones standalone en Angular 17+.
 * 
 * La configuración de la aplicación se importa desde app.config.ts
 * y el componente raíz desde app.component.ts.
 */
bootstrapApplication(AppComponent, appConfig)
  .catch((err) => console.error('Error al iniciar la aplicación:', err)); 