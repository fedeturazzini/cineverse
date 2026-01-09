# 🎬 CineVerse

CineVerse es una aplicación Android moderna para explorar, descubrir y gestionar películas. La app ofrece una experiencia completa con funcionalidades interactivas como duelos de películas, maratones, desafíos diarios, búsqueda con inteligencia artificial y mucho más.

## ✨ Características Principales

- **🏠 Inicio**: Exploración de películas populares, próximos estrenos y recomendaciones personalizadas
- **⚔️ Duelos**: Compara dos películas y elige tu favorita para construir tu perfil de gustos
- **🎯 Maratones**: Crea y gestiona maratones temáticos de películas
- **🏆 Desafíos Diarios**: Completa desafíos diarios y gana badges
- **🤖 Búsqueda con IA**: Utiliza Google Gemini para buscar películas de forma conversacional
- **📊 Cineverse Wrap**: Resumen anual estilo "Spotify Wrapped" con tus estadísticas de películas
- **📈 Mood Radar**: Visualiza tus preferencias de películas por género y estado de ánimo
- **👤 Perfil Personalizado**: Gestiona tu perfil con foto, géneros favoritos y estadísticas
- **🔍 Detalles de Películas**: Información completa con sinopsis, reparto, trailers y reseñas generadas por IA
- **⭐ Favoritos y Vistas**: Marca películas como favoritas o vistas para llevar un registro
- **🌍 Geolocalización**: Descubre películas basadas en tu ubicación

## 🏗️ Arquitectura

La aplicación está construida siguiendo los principios de **Clean Architecture**, organizada en módulos independientes que separan las responsabilidades:

```
CineVerse/
├── app/          # Capa de presentación (UI, ViewModels, Activities)
├── domain/       # Lógica de negocio y modelos de dominio
├── data/         # Repositorios y fuentes de datos
├── framework/    # Implementaciones concretas (Room, Retrofit, APIs)
├── usecases/     # Casos de uso de la aplicación
└── test-shared/  # Utilidades compartidas para testing
```

### Módulos

- **app**: Módulo principal de Android que contiene la interfaz de usuario con Jetpack Compose, ViewModels, Activities y la configuración de inyección de dependencias
- **domain**: Contiene los modelos de dominio, interfaces de repositorios y reglas de negocio puras (sin dependencias de Android)
- **data**: Implementa los repositorios y coordina las fuentes de datos (local y remota)
- **framework**: Proporciona las implementaciones concretas de las interfaces del dominio (Room Database, Retrofit, Google Gemini API, etc.)
- **usecases**: Contiene los casos de uso que orquestan la lógica de negocio
- **test-shared**: Módulo compartido con utilidades y helpers para testing

## 🛠️ Stack Tecnológico

### Lenguaje y Plataforma
- **Kotlin** 2.2.21
- **Android SDK**: minSdk 24, targetSdk 36, compileSdk 36
- **Java**: Versión 11

### UI y Diseño
- **Jetpack Compose** 2025.10.00 - Framework de UI declarativa
- **Material 3** - Sistema de diseño moderno
- **Material Icons Extended** - Iconografía ampliada
- **Navigation Compose** 2.9.5 - Navegación entre pantallas
- **Haze** 1.7.1 - Efectos glassmorphism
- **Lottie** 6.6.2 - Animaciones vectoriales

### Arquitectura y Patrones
- **Clean Architecture** - Separación de capas
- **MVVM** (Model-View-ViewModel) - Patrón de arquitectura UI
- **Koin** 4.1.1 - Inyección de dependencias

### Persistencia de Datos
- **Room** 2.8.4 - Base de datos local con KSP para compilación
- Entidades: Películas, Perfil, Duelos, Maratones, Desafíos, Búsquedas IA, etc.

### Networking
- **Retrofit** 3.0.0 - Cliente HTTP para APIs REST
- **Kotlinx Serialization** 1.9.0 - Serialización JSON
- **Retrofit Kotlinx Serialization Converter** 1.0.0 - Integración Retrofit + Serialization

### APIs Externas
- **TMDB API** (The Movie Database) - Catálogo de películas, imágenes, trailers
- **Google Generative AI (Gemini)** 0.9.0 - Reseñas y búsquedas con IA

### Utilidades
- **Coil** 2.7.0 - Carga y caché de imágenes
- **Kotlinx Coroutines** 1.9.0 - Programación asíncrona
- **Google Play Services Location** 21.3.0 - Geolocalización

### Calidad de Código
- **Spotless** 8.0.0 - Formateo automático de código
- **KTLint** 1.0.1 - Linter de Kotlin

### Testing
- **JUnit** 4.13.2 - Framework de testing unitario
- **Mockito** 5.2.0 - Mocking para tests
- **Mockito Kotlin** 5.3.1 - Extensiones de Mockito para Kotlin
- **Turbine** 1.1.0 - Testing de Flows
- **Espresso** 3.7.0 - Testing de UI
- **AndroidX Test** 1.6.1 - Utilidades de testing para Android

## 📁 Estructura del Proyecto

```
CineVerse/
├── app/
│   ├── src/main/
│   │   ├── java/com/ft/architectcoders/
│   │   │   ├── ui/                    # Pantallas y componentes UI
│   │   │   │   ├── screens/          # Pantallas principales
│   │   │   │   │   ├── home/         # Pantalla de inicio
│   │   │   │   │   ├── detail/       # Detalle de película
│   │   │   │   │   ├── duel/         # Duelos de películas
│   │   │   │   │   ├── marathon/     # Maratones
│   │   │   │   │   ├── challenge/    # Desafíos diarios
│   │   │   │   │   ├── aisearch/     # Búsqueda con IA
│   │   │   │   │   ├── wrap/         # Cineverse Wrap
│   │   │   │   │   ├── profile/      # Perfil de usuario
│   │   │   │   │   ├── mood/         # Mood Radar
│   │   │   │   │   └── foryou/       # Recomendaciones
│   │   │   │   ├── common/           # Componentes reutilizables
│   │   │   │   └── theme/            # Tema y estilos
│   │   │   ├── di/                    # Módulos de Koin
│   │   │   └── MainActivity.kt       # Actividad principal
│   │   └── res/                       # Recursos (imágenes, strings, etc.)
│   └── build.gradle.kts
├── domain/
│   └── src/main/java/com/ft/architectcoders/domain/
│       ├── model/                     # Modelos de dominio
│       └── repository/                # Interfaces de repositorios
├── data/
│   └── src/main/java/com/ft/architectcoders/data/
│       └── repository/                # Implementaciones de repositorios
├── framework/
│   └── src/main/java/com/ft/architectcoders/framework/
│       ├── database/                  # Room Database y DAOs
│       ├── remote/                    # Retrofit y APIs
│       └── location/                   # Servicios de geolocalización
├── usecases/
│   └── src/main/java/com/ft/architectcoders/usecases/
│       └── ...                        # Casos de uso
└── test-shared/
    └── src/main/java/                 # Utilidades para testing
```

## ⚙️ Configuración

### Requisitos Previos

- **Android Studio** (versión reciente recomendada)
- **JDK 11** o superior
- **Android SDK** con API Level 24 mínimo
- **Gradle** (incluido en el proyecto)

### Variables de Entorno

La aplicación requiere dos claves de API que deben configurarse en el archivo `local.properties` en la raíz del proyecto:

```properties
TMDB_API_KEY=tu_clave_api_tmdb
GEMINI_API_KEY=tu_clave_api_gemini
```

#### Obtener las API Keys

1. **TMDB API Key**:
   - Visita [The Movie Database](https://www.themoviedb.org/)
   - Crea una cuenta y solicita una API key en la sección de configuración

2. **Gemini API Key**:
   - Visita [Google AI Studio](https://makersuite.google.com/app/apikey)
   - Crea una API key para Google Generative AI

### Instalación

1. Clona el repositorio:
```bash
git clone <url-del-repositorio>
cd CineVerse
```

2. Crea el archivo `local.properties` en la raíz del proyecto:
```bash
touch local.properties
```

3. Agrega tus API keys al archivo `local.properties`:
```properties
TMDB_API_KEY=tu_clave_api_tmdb
GEMINI_API_KEY=tu_clave_api_gemini
```

4. Abre el proyecto en Android Studio

5. Sincroniza el proyecto con Gradle (Android Studio lo hará automáticamente)

6. Ejecuta la aplicación en un dispositivo o emulador

## 🔐 Permisos

La aplicación requiere los siguientes permisos (configurados en `AndroidManifest.xml`):

- **INTERNET**: Para realizar llamadas a APIs
- **ACCESS_NETWORK_STATE**: Para verificar conectividad
- **ACCESS_FINE_LOCATION**: Para geolocalización precisa
- **ACCESS_COARSE_LOCATION**: Para geolocalización aproximada
- **READ_EXTERNAL_STORAGE**: Para seleccionar imágenes de la galería (Android ≤ 32)
- **READ_MEDIA_IMAGES**: Para seleccionar imágenes de la galería (Android ≥ 33)
- **CAMERA**: Para tomar fotos de perfil

Los permisos de ubicación y cámara se solicitan en tiempo de ejecución cuando son necesarios.

## 🧪 Testing

El proyecto incluye tests unitarios y de integración:

### Tests Unitarios
- Ubicados en `src/test/java/` de cada módulo
- Utilizan JUnit, Mockito y Turbine para testing de Flows

### Tests de UI
- Ubicados en `src/androidTest/java/`
- Utilizan Espresso y Compose Testing

### Ejecutar Tests

```bash
# Todos los tests
./gradlew test

# Tests de un módulo específico
./gradlew :app:test

# Tests de UI
./gradlew connectedAndroidTest
```

## 🎨 Características de UI

- **Tema Oscuro**: La aplicación utiliza un tema oscuro por defecto con colores personalizados
- **Glassmorphism**: Efectos de vidrio esmerilado en componentes como la barra de navegación inferior
- **Animaciones**: Integración de Lottie para animaciones fluidas
- **Navegación**: Navegación por tabs con barra inferior glassmorphic
- **Carga de Imágenes**: Caché inteligente de imágenes con Coil

## 📱 Funcionalidades Detalladas

### Duelos
Compara dos películas lado a lado y elige tu favorita. El sistema aprende de tus preferencias para mejorar las recomendaciones.

### Maratones
Crea maratones temáticos (por género, director, actor, etc.) y gestiona tu lista de películas para ver.

### Desafíos Diarios
Completa desafíos diarios relacionados con películas y desbloquea badges especiales.

### Búsqueda con IA
Utiliza Google Gemini para buscar películas de forma conversacional. Por ejemplo: "Películas de ciencia ficción de los 80s con robots".

### Cineverse Wrap
Al final del año, genera un resumen completo de tu actividad cinematográfica con estadísticas, géneros favoritos, películas más vistas y más.

### Mood Radar
Visualización interactiva de tus preferencias de películas organizadas por género y estado de ánimo.

## 🔄 Flujo de Datos

La aplicación sigue un flujo de datos unidireccional:

1. **UI** → ViewModel solicita datos
2. **ViewModel** → UseCase ejecuta lógica de negocio
3. **UseCase** → Repository obtiene datos
4. **Repository** → Framework (Room/Retrofit) obtiene datos
5. **Datos** → Fluyen de vuelta a través de las capas como Flows/States
6. **UI** → Se actualiza reactivamente con Compose

## 📝 Formateo de Código

El proyecto utiliza Spotless con KTLint para mantener un código consistente:

```bash
# Formatear código
./gradlew spotlessApply

# Verificar formato
./gradlew spotlessCheck
```

## 🤝 Contribuir

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📄 Licencia

[Especificar licencia si aplica]

## 👨‍💻 Autor

[Tu nombre/información de contacto]

---

**CineVerse** - Tu universo cinematográfico en la palma de tu mano 🎬✨

---

# 🎬 CineVerse

CineVerse is a modern Android application for exploring, discovering, and managing movies. The app offers a complete experience with interactive features such as movie duels, marathons, daily challenges, AI-powered search, and much more.

## ✨ Main Features

- **🏠 Home**: Explore popular movies, upcoming releases, and personalized recommendations
- **⚔️ Duels**: Compare two movies and choose your favorite to build your taste profile
- **🎯 Marathons**: Create and manage thematic movie marathons
- **🏆 Daily Challenges**: Complete daily challenges and earn badges
- **🤖 AI Search**: Use Google Gemini to search for movies conversationally
- **📊 Cineverse Wrap**: Annual summary in "Spotify Wrapped" style with your movie statistics
- **📈 Mood Radar**: Visualize your movie preferences by genre and mood
- **👤 Custom Profile**: Manage your profile with photo, favorite genres, and statistics
- **🔍 Movie Details**: Complete information with synopsis, cast, trailers, and AI-generated reviews
- **⭐ Favorites and Watched**: Mark movies as favorites or watched to keep track
- **🌍 Geolocation**: Discover movies based on your location

## 🏗️ Architecture

The application is built following **Clean Architecture** principles, organized into independent modules that separate responsibilities:

```
CineVerse/
├── app/          # Presentation layer (UI, ViewModels, Activities)
├── domain/       # Business logic and domain models
├── data/         # Repositories and data sources
├── framework/    # Concrete implementations (Room, Retrofit, APIs)
├── usecases/     # Application use cases
└── test-shared/  # Shared utilities for testing
```

### Modules

- **app**: Main Android module containing the user interface with Jetpack Compose, ViewModels, Activities, and dependency injection configuration
- **domain**: Contains domain models, repository interfaces, and pure business rules (no Android dependencies)
- **data**: Implements repositories and coordinates data sources (local and remote)
- **framework**: Provides concrete implementations of domain interfaces (Room Database, Retrofit, Google Gemini API, etc.)
- **usecases**: Contains use cases that orchestrate business logic
- **test-shared**: Shared module with utilities and helpers for testing

## 🛠️ Tech Stack

### Language and Platform
- **Kotlin** 2.2.21
- **Android SDK**: minSdk 24, targetSdk 36, compileSdk 36
- **Java**: Version 11

### UI and Design
- **Jetpack Compose** 2025.10.00 - Declarative UI framework
- **Material 3** - Modern design system
- **Material Icons Extended** - Extended iconography
- **Navigation Compose** 2.9.5 - Screen navigation
- **Haze** 1.7.1 - Glassmorphism effects
- **Lottie** 6.6.2 - Vector animations

### Architecture and Patterns
- **Clean Architecture** - Layer separation
- **MVVM** (Model-View-ViewModel) - UI architecture pattern
- **Koin** 4.1.1 - Dependency injection

### Data Persistence
- **Room** 2.8.4 - Local database with KSP for compilation
- Entities: Movies, Profile, Duels, Marathons, Challenges, AI Searches, etc.

### Networking
- **Retrofit** 3.0.0 - HTTP client for REST APIs
- **Kotlinx Serialization** 1.9.0 - JSON serialization
- **Retrofit Kotlinx Serialization Converter** 1.0.0 - Retrofit + Serialization integration

### External APIs
- **TMDB API** (The Movie Database) - Movie catalog, images, trailers
- **Google Generative AI (Gemini)** 0.9.0 - AI reviews and searches

### Utilities
- **Coil** 2.7.0 - Image loading and caching
- **Kotlinx Coroutines** 1.9.0 - Asynchronous programming
- **Google Play Services Location** 21.3.0 - Geolocation

### Code Quality
- **Spotless** 8.0.0 - Automatic code formatting
- **KTLint** 1.0.1 - Kotlin linter

### Testing
- **JUnit** 4.13.2 - Unit testing framework
- **Mockito** 5.2.0 - Mocking for tests
- **Mockito Kotlin** 5.3.1 - Mockito extensions for Kotlin
- **Turbine** 1.1.0 - Flow testing
- **Espresso** 3.7.0 - UI testing
- **AndroidX Test** 1.6.1 - Testing utilities for Android

## 📁 Project Structure

```
CineVerse/
├── app/
│   ├── src/main/
│   │   ├── java/com/ft/architectcoders/
│   │   │   ├── ui/                    # Screens and UI components
│   │   │   │   ├── screens/          # Main screens
│   │   │   │   │   ├── home/         # Home screen
│   │   │   │   │   ├── detail/       # Movie detail
│   │   │   │   │   ├── duel/         # Movie duels
│   │   │   │   │   ├── marathon/     # Marathons
│   │   │   │   │   ├── challenge/    # Daily challenges
│   │   │   │   │   ├── aisearch/     # AI search
│   │   │   │   │   ├── wrap/         # Cineverse Wrap
│   │   │   │   │   ├── profile/      # User profile
│   │   │   │   │   ├── mood/         # Mood Radar
│   │   │   │   │   └── foryou/       # Recommendations
│   │   │   │   ├── common/           # Reusable components
│   │   │   │   └── theme/            # Theme and styles
│   │   │   ├── di/                    # Koin modules
│   │   │   └── MainActivity.kt       # Main activity
│   │   └── res/                       # Resources (images, strings, etc.)
│   └── build.gradle.kts
├── domain/
│   └── src/main/java/com/ft/architectcoders/domain/
│       ├── model/                     # Domain models
│       └── repository/                # Repository interfaces
├── data/
│   └── src/main/java/com/ft/architectcoders/data/
│       └── repository/                # Repository implementations
├── framework/
│   └── src/main/java/com/ft/architectcoders/framework/
│       ├── database/                  # Room Database and DAOs
│       ├── remote/                    # Retrofit and APIs
│       └── location/                   # Geolocation services
├── usecases/
│   └── src/main/java/com/ft/architectcoders/usecases/
│       └── ...                        # Use cases
└── test-shared/
    └── src/main/java/                 # Testing utilities
```

## ⚙️ Configuration

### Prerequisites

- **Android Studio** (recent version recommended)
- **JDK 11** or higher
- **Android SDK** with API Level 24 minimum
- **Gradle** (included in the project)

### Environment Variables

The application requires two API keys that must be configured in the `local.properties` file at the project root:

```properties
TMDB_API_KEY=your_tmdb_api_key
GEMINI_API_KEY=your_gemini_api_key
```

#### Getting API Keys

1. **TMDB API Key**:
   - Visit [The Movie Database](https://www.themoviedb.org/)
   - Create an account and request an API key in the settings section

2. **Gemini API Key**:
   - Visit [Google AI Studio](https://makersuite.google.com/app/apikey)
   - Create an API key for Google Generative AI

### Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd CineVerse
```

2. Create the `local.properties` file at the project root:
```bash
touch local.properties
```

3. Add your API keys to the `local.properties` file:
```properties
TMDB_API_KEY=your_tmdb_api_key
GEMINI_API_KEY=your_gemini_api_key
```

4. Open the project in Android Studio

5. Sync the project with Gradle (Android Studio will do this automatically)

6. Run the application on a device or emulator

## 🔐 Permissions

The application requires the following permissions (configured in `AndroidManifest.xml`):

- **INTERNET**: To make API calls
- **ACCESS_NETWORK_STATE**: To check connectivity
- **ACCESS_FINE_LOCATION**: For precise geolocation
- **ACCESS_COARSE_LOCATION**: For approximate geolocation
- **READ_EXTERNAL_STORAGE**: To select images from gallery (Android ≤ 32)
- **READ_MEDIA_IMAGES**: To select images from gallery (Android ≥ 33)
- **CAMERA**: To take profile photos

Location and camera permissions are requested at runtime when needed.

## 🧪 Testing

The project includes unit and integration tests:

### Unit Tests
- Located in `src/test/java/` of each module
- Use JUnit, Mockito, and Turbine for Flow testing

### UI Tests
- Located in `src/androidTest/java/`
- Use Espresso and Compose Testing

### Running Tests

```bash
# All tests
./gradlew test

# Tests for a specific module
./gradlew :app:test

# UI tests
./gradlew connectedAndroidTest
```

## 🎨 UI Features

- **Dark Theme**: The application uses a dark theme by default with custom colors
- **Glassmorphism**: Frosted glass effects on components like the bottom navigation bar
- **Animations**: Lottie integration for smooth animations
- **Navigation**: Tab navigation with glassmorphic bottom bar
- **Image Loading**: Smart image caching with Coil

## 📱 Detailed Features

### Duels
Compare two movies side by side and choose your favorite. The system learns from your preferences to improve recommendations.

### Marathons
Create thematic marathons (by genre, director, actor, etc.) and manage your movie watchlist.

### Daily Challenges
Complete daily movie-related challenges and unlock special badges.

### AI Search
Use Google Gemini to search for movies conversationally. For example: "80s sci-fi movies with robots".

### Cineverse Wrap
At the end of the year, generate a complete summary of your movie activity with statistics, favorite genres, most watched movies, and more.

### Mood Radar
Interactive visualization of your movie preferences organized by genre and mood.

## 🔄 Data Flow

The application follows a unidirectional data flow:

1. **UI** → ViewModel requests data
2. **ViewModel** → UseCase executes business logic
3. **UseCase** → Repository gets data
4. **Repository** → Framework (Room/Retrofit) gets data
5. **Data** → Flows back through layers as Flows/States
6. **UI** → Updates reactively with Compose

## 📝 Code Formatting

The project uses Spotless with KTLint to maintain consistent code:

```bash
# Format code
./gradlew spotlessApply

# Check format
./gradlew spotlessCheck
```

## 🤝 Contributing

1. Fork the project
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

[Specify license if applicable]

## 👨‍💻 Author

[Your name/contact information]

---

**CineVerse** - Your cinematic universe in the palm of your hand 🎬✨

