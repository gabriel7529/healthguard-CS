# HealthGuard

El proyecto consiste en desarrollar un sistema inteligente de monitoreo de salud y peligros para las personas mayores bajo nuestra supervisión.

En este mundo ocupado, las personas jóvenes a menudo deben estar lejos de casa por razones laborales u otras. En su ausencia, los ancianos en el hogar quedan desatendidos, con riesgo de sufrir problemas de salud o incluso accidentes fatales. Este proyecto está diseñado para monitorear su seguridad constantemente y alertar mediante una aplicación Android sobre cualquier peligro inmediato o potencial.

## Software Utilizado
1. Android Studio
2. Firebase Realtime Database
3. Firebase Machine Learning Kit
4. Google Maps API
5. Arduino IDE

## Hardware Utilizado
1. Node MCU ESP8266 WIFI Module
2. Módulo de acelerómetro y giroscopio MPU6050
3. Pantalla OLED
4. Botón pulsador
5. Monitor de frecuencia cardíaca

## Áreas de Trabajo
El proyecto se centra principalmente en dos aspectos:

1. **Detección de Impactos y Medidas de Emergencia**
   - En caso de accidentes de tránsito o caídas repentinas, la inteligencia artificial detecta el evento y alerta a los familiares cercanos. En emergencias graves, se notifica a los servicios hospitalarios más cercanos a través de la aplicación Android.

2. **Monitoreo de Frecuencia Cardíaca en Tiempo Real y Alertas Precautorias**
   - Se realiza un monitoreo constante de la frecuencia cardíaca a intervalos regulares y los datos se almacenan en Firebase Database. Si se detectan anomalías en la relación entre frecuencia cardíaca y movimiento, se notifica al usuario de inmediato.

## Descripción de la Aplicación Android
La aplicación Android permite la comunicación con el reloj inteligente para enviar y recibir información. Los lenguajes utilizados son Java y C++. Firebase Database actúa como servicio de backend.

### Características y GUI

La aplicación soporta dos tipos de usuarios:

1. **Padres**: Monitorean a las personas mayores y gestionan el sistema.
2. **Usuarios**: Personas mayores que llevan el reloj inteligente.

Incluye funcionalidades como:
- Vincular cuentas de usuarios.
- Ver ubicación en tiempo real.
- Enviar mensajes al reloj.
- Configurar recordatorios.

### Ejemplo de Implementación de Selector de Hora:
```java
public void showHourPicker() {
    final Calendar myCalender = Calendar.getInstance();
    int hour = myCalender.get(Calendar.HOUR_OF_DAY);
    int minute = myCalender.get(Calendar.MINUTE);

    TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if (view.isShown()) {
                myCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
                myCalender.set(Calendar.MINUTE, minute);
            }
        }
    };
    TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myTimeListener, hour, minute, true);
    timePickerDialog.setTitle("Choose hour:"); 
    timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    timePickerDialog.show();
}
```

## Descripción del Hardware
El dispositivo recoge datos de sensores de acelerómetro, giroscopio y frecuencia cardíaca. Los datos se procesan para detectar caídas o anomalías cardíacas y enviar alertas.

### Ejemplo de Principio de Funcionamiento
- El sensor MPU6050 mide valores en los ejes X, Y y Z. Estos datos determinan posturas como sentado, de pie o caminando.
- Para detectar caídas, se analiza el patrón del sensor buscando cambios bruscos mayores a 2g.

#### Monitor de Frecuencia Cardíaca
- Utiliza un sensor óptico para medir pulsaciones.
- Detecta enfermedades cardíacas analizando el BPM.

## Firebase
- Almacena datos de frecuencia cardíaca y movimiento en tiempo real.
- Utiliza Firebase Authentication para el inicio de sesión seguro.

## Instalación

Sigue los pasos a continuación para configurar y ejecutar el proyecto:

### Requisitos Previos
1. **Android Studio**: Descárgalo desde [Android Studio](https://developer.android.com/studio).
2. **Java JDK 11**: Descárgalo desde [Java SE Development Kit 11](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html).
3. **Cuenta en Firebase**: Crea una cuenta en [Firebase](https://firebase.google.com/) y configura un nuevo proyecto.
4. **Cuenta en Google Cloud**: Activa el SDK de Maps desde la consola de [Google Cloud](https://console.cloud.google.com/).

### Configuración
1. Clona este repositorio en tu máquina local:
   ```bash
   git clone https://github.com/tu-usuario/tu-repositorio.git
   ```
2. Abre el proyecto en Android Studio.
3. Configura Firebase:
   - Descarga el archivo `google-services.json` desde Firebase y colócalo en la carpeta `app` del proyecto.
4. Configura Google Cloud Maps:
   - Obtén una clave API desde Google Cloud Console.
   - Añade la clave a tu archivo `AndroidManifest.xml`:
     ```xml
     <meta-data
         android:name="com.google.android.geo.API_KEY"
         android:value="TU_CLAVE_API" />
     ```
5. Verifica que las dependencias estén instaladas ejecutando:
   ```bash
   ./gradlew build
   ```
6. Conecta un dispositivo o utiliza un emulador en Android Studio.
7. Ejecuta la aplicación:
   ```bash
   ./gradlew installDebug
   ```

## Créditos
Este proyecto utiliza bibliotecas modernas de Android y Firebase, incluyendo:
- Android Jetpack
- Firebase Authentication y Realtime Database
- Google Maps API
- Arduino IDE y bibliotecas relacionadas.

## Licencia
Este proyecto está licenciado bajo la [Licencia MIT](LICENSE).

