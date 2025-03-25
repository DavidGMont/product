# SoundSeeker Product Service

Este proyecto implementa un servicio de gestión de productos para la plataforma SoundSeeker, una tienda de instrumentos
musicales.

## 🎯 Objetivo

Este módulo permite la gestión completa de productos (instrumentos musicales) con operaciones CRUD (Crear, Leer,
Actualizar, Eliminar) utilizando JDBC para la conexión a la base de datos.

## 📋 Características

- Implementación del Patrón DAO (Data Access Object)
- Conexión a base de datos H2 para almacenamiento
- Validaciones de datos en la capa de servicio
- Manejo adecuado de excepciones
- Tests unitarios completos

## 🧱 Estructura del proyecto

El proyecto está organizado en paquetes de acuerdo con su funcionalidad:

```
me.davidgarmo.soundseeker.product/
├── config/
│   └── DBConnection.java               # Gestión de conexiones a H2
├── persistence/
│   ├── dao/
│   │   └── IDao.java                   # Interfaz genérica para operaciones CRUD
│   ├── entity/
│   │   └── Product.java                # Entidad principal
│   └── impl/
│       └── ProductDaoH2.java           # Implementación DAO con H2
├── service/
│   ├── exception/
│   │   └── ProductNotFoundException.java # Excepción personalizada
│   ├── IProductService.java            # Interfaz del servicio
│   └── impl/
│       └── ProductService.java         # Implementación del servicio
└── test/
    └── service/
        └── impl/
            └── ProductServiceTest.java # Tests para ProductService
```

## 🛠️ Tecnologías utilizadas

- Java 21
- H2 Database (2.3.232)
- Log4j2 (2.24.3)
- JUnit Jupiter (5.10.0)
- AssertJ (3.27.3)
- Gradle 8.10

## ⚙️ Instalación y ejecución

### Requisitos previos

- Java 21 o superior
- Gradle 8.x

### Pasos para ejecutar

1. Clonar el repositorio
   ```bash
   git clone https://github.com/DavidGMont/product.git
   ```
2. Acceder al directorio del proyecto
   ```bash
    cd product
    ```
3. Compilar y ejecutar pruebas
   ```bash
   ./gradlew clean test
    ```

## 📈 Funcionalidades implementadas

### Operaciones CRUD

- **CREATE**: Guardar un nuevo producto
- **READ**: Buscar producto por ID o listar todos
- **UPDATE**: Actualizar información de un producto existente
- **DELETE**: Eliminar un producto

### Validaciones

- Campos obligatorios (nombre, descripción, marca, etc.)
- Restricciones de longitud (nombre máx. 60 caracteres, descripción máx. 1000)
- Valores válidos (precio > 0)

## 🧪 Tests

El proyecto incluye pruebas unitarias completas para todas las funcionalidades:

```bash
./gradlew test
```

## 📝 Notas de desarrollo

Este proyecto forma parte de la evidencia de aprendizaje GA7-220501096-AA2-EV01 del programa de formación del SENA,
implementando la "Codificación de módulos del software según requerimientos del proyecto".

## 📚 Referencias

- [Patrón DAO](https://www.oracle.com/java/technologies/dataaccessobject.html)
- [JDBC API](https://docs.oracle.com/javase/8/docs/technotes/guides/jdbc/)
- [H2 Database](https://h2database.com/html/main.html)

## 👥 Autores

- [David García](https://github.com/DavidGMont)

## 📜 Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para más detalles.
