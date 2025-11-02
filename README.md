Hackathon #1: Oreo Insight Factory - README

INFORMACIÓN DEL EQUIPO:
-José Luis Alva Espiunoza - 202420038

DESCRIPCIÓN:
Backend Spring Boot para la fábrica de Oreo que permite registrar ventas y generar resúmenes automáticos usando GitHub Models. Incluye autenticación JWT, roles por sucursal, procesamiento asíncrono y envío de reportes por email.

TECNOLOGÍAS:
- Java 21 + Spring Boot 3.x
- Spring Security con JWT
- Spring Data JPA + H2 Database
- GitHub Models API para LLM
- Spring Boot Mail para email
- @Async y @EventListener para procesamiento asíncrono
- JUnit 5 + Mockito para testing

INSTRUCCIONES DE EJECUCIÓN:

1. PRERREQUISITOS:
   - Java 21 instalado
   - Maven instalado
   - GitHub Personal Access Token
   - Cuenta de Gmail con app password

2. CONFIGURACIÓN DE VARIABLES:
   Crear archivo .env con el contenido subido a Canvas.

3. EJECUCIÓN:
   git clone [url-del-repositorio]
   cd oreo-insight-factory
   mvn clean spring-boot:run


INSTRUCCIONES POSTMAN:

1. Importar postman_collection.json
2. Configurar variables en Postman:
   - baseUrl: http://localhost:8080
   - centralToken: (se autocompleta)
   - branchToken: (se autocompleta)

3. Secuencia automática:
   - Register CENTRAL → Login CENTRAL
   - Register BRANCH → Login BRANCH  
   - Crear 5 ventas de prueba
   - Listar ventas (CENTRAL vs BRANCH)
   - Solicitar resumen asíncrono
   - Validar permisos de sucursal
   - Eliminar venta (solo CENTRAL)

IMPLEMENTACIÓN ASÍNCRONA:

Arquitectura basada en eventos:
1. Controller → Recibe petición y publica evento
2. Event Publisher → Dispara ReportRequestedEvent  
3. Async Listener → Procesa en background
4. LLM Service → Genera resumen con GitHub Models
5. Email Service → Envía reporte por email

Configuración:
@EnableAsync con ThreadPoolTaskExecutor
@Async y @EventListener en ReportService

Ventajas:
- Respuesta inmediata (202 Accepted)
- Múltiples reportes concurrentes
- Resiliencia a fallos
- Separación de responsabilidades

TESTING:
mvn test

Tests implementados (5):
1. shouldCalculateCorrectAggregatesWithValidData
2. shouldHandleEmptySalesList
3. shouldFilterByBranchCorrectly
4. shouldFilterByDateRange  
5. shouldHandleSkuTiesCorrectly

ENDPOINT PREMIUM (RETO EXTRA):
✅ Completado - Características:
- Email HTML con formato profesional
- Gráficos embebidos con QuickChart.io
- PDF adjunto con Apache PDFBox
- Procesamiento asíncrono mejorado

Endpoint: POST /sales/summary/weekly/premium

ESTRUCTURA DEL PROYECTO:
src/main/java/
├── config/ (Security, Async, etc.)
├── controller/ (Endpoints REST)
├── service/ (Lógica de negocio)
├── repository/ (Acceso a datos)
├── model/(Entidades JPA)
├── dto/ (Objetos de transferencia)
├── security/ (JWT y autenticación)
├── event/ (Eventos asíncronos)
└── integration/ (GitHub Models, Email)

CONSIDERACIONES DE SEGURIDAD:
- JWT tokens con expiración de 1 hora
- Passwords encriptadas con BCrypt
- Validación de roles en cada endpoint
- Filtro por sucursal para usuarios BRANCH
- Variables de entorno para datos sensibles

