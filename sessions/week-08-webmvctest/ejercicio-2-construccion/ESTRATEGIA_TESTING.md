# Estrategia de testing — Catálogo de productos

> Completa esta plantilla. Es el entregable que diferencia el nivel **Experto**: no basta con que los
> tests pasen, hay que justificar **qué se prueba dónde y por qué**.

## 1. Pirámide propuesta

| Nivel | Herramienta | Qué valida | Qué NO valida | Nº de tests | Tiempo aprox. |
|-------|-------------|------------|---------------|-------------|---------------|
| Unitario (dominio) | JUnit 5 | | | | |
| Unitario (aplicación) | JUnit 5 + Mockito | | | | |
| Slice web | `@WebMvcTest` + `MockMvc` | | | | |
| Integración | `@SpringBootTest` | | | | |

## 2. Matriz de decisión

Para cada regla, marca en qué nivel se prueba y justifica.

| Regla / comportamiento | Dominio | Aplicación | Slice web | Integración | Justificación |
|------------------------|:-------:|:----------:|:---------:|:-----------:|---------------|
| El precio debe ser > 0 | x | | | | |
| El nombre vacío devuelve 400 | x | | | | |
| Producto inexistente devuelve 404 | x | | | | |
| Se genera un id al crear | | x | | | |
| `Location` apunta al recurso creado | | | | | |
| `DELETE` exige rol `ADMIN` | | | | | |
| Las capas quedan cableadas | | | | x | |

## 3. Decisiones y trade-offs

- ¿Por qué `@MockBean` en el slice test y `@Mock` en el test del servicio?
- ¿Qué pasa con el tiempo del pipeline si cada test de controller usa `@SpringBootTest`? (cita tus mediciones)
- ¿Cómo aprovechas el caché de contexto de Spring entre clases de test? ¿Qué lo invalida?
- ¿Qué riesgos quedan sin cubrir y cómo los cubrirías (contract tests, tests de carga, etc.)?

## 4. Mediciones

| Comando | Tests | Tiempo reportado por Surefire |
|---------|-------|-------------------------------|
| `mvn -pl ejercicio-2-construccion test -Dtest='ProductControllerTest*'` | 5.719 s| 10.090 s|
| `mvn -pl ejercicio-2-construccion verify` | 6.922 s| 16.209 s |
