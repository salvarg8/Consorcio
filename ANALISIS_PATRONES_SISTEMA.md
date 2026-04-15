# Análisis de patrones e inconsistencias del sistema

Fecha de análisis: 2026-04-15.

## Patrones que sí se respetan

1. **Arquitectura por capas clara**
   - Existe separación explícita entre `controller`, `service`, `repository`, `entity`, `dto`, `mapper` y `exception`.
   - Esto está alineado con lo que declara el README.

2. **Controladores REST con versionado consistente**
   - Los controladores principales usan `@RestController` y prefijo `/api/v1/*`.
   - Hay consistencia de base path para los recursos principales (`users`, `units`, `payments`, etc.).

3. **Seguridad centralizada con JWT**
   - La configuración de seguridad está aislada en `SecurityConfig`.
   - Se observa una política stateless y filtro JWT pre-`UsernamePasswordAuthenticationFilter`.

4. **Convenciones de calidad incorporadas al build**
   - El `maven-checkstyle-plugin` está configurado en fase `validate`.
   - Hay reglas de naming/imports/estructura que ayudan a sostener estándares.

5. **Cobertura de pruebas de servicios presente**
   - Hay tests unitarios por servicio de dominio principal en `src/test/java/com/consorcio/gestion/service`.

## Inconsistencias detectadas

1. **Documentación vs implementación de soft delete (inconsistencia funcional/documental)**
   - El README afirma soft delete en "TODO el dominio".
   - En entidades, solo algunas tienen flag de actividad (`Consorcio`, `Usuario`, `Amenity`, `UnidadFuncional`, `Administracion`), mientras que otras no (`Infraccion`, `PagoPendiente`, `ReservaAmenity`, `LiquidacionMensual`, `LiquidacionUnidad`).
   - Riesgo: expectativas incorrectas del comportamiento de borrado para entidades financieras/históricas.

2. **Patrón de servicios no uniforme**
   - Existe interfaz + implementación solo en liquidaciones (`LiquidacionService` + `LiquidacionServiceImpl`).
   - El resto de servicios usan clase concreta sin interfaz.
   - Riesgo: inconsistencia de diseño y dificultad para mantener criterios de mocking/abstracción homogéneos.

3. **Artefactos legacy de MVC/JSF coexistiendo con API REST**
   - Hay recursos `webapp` (`index.html`, `hello.xhtml`, `web.xml`) con `FacesServlet` (`javax.faces.webapp.FacesServlet`).
   - El proyecto declarado y configurado es API REST con Spring Boot; estos artefactos no están alineados con ese enfoque.
   - Riesgo: ruido técnico, confusión operativa y potencial conflicto en mantenimiento.

4. **Fuentes Java "marcadas como borradas" pero aún presentes**
   - `src/main/java/org/example/App.java` y `src/main/java/org/example/HelloController.java` contienen solo `// DELETED`.
   - Riesgo: deuda técnica y ambigüedad del estado real del código legado.

5. **Inconsistencia potencial de build en entorno restringido**
   - `mvn test` no pudo resolver parent POM de Spring Boot por error HTTP 403 al descargar desde Maven Central.
   - Riesgo: no se puede validar automáticamente cumplimiento completo de patrones (tests/checkstyle) en este entorno.

## Recomendaciones priorizadas

### Prioridad alta
1. **Alinear README con comportamiento real** o implementar soft delete uniforme en entidades críticas.
2. **Eliminar artefactos legacy no usados** (`org/example/*`, `webapp/*`) si no forman parte del alcance actual.

### Prioridad media
3. **Definir una política de servicios**:
   - O todo con interfaz + implementación.
   - O todo con clases concretas (excepto casos especiales), documentando el criterio.

4. **Agregar una checklist de consistencia arquitectónica en CI**
   - Verificación de estructura de paquetes.
   - Detección de archivos placeholder (`// DELETED`).
   - Regla de documentación sincronizada con implementación.

### Prioridad baja
5. **Estandarizar naming de dominio/API (ES vs EN)**
   - El dominio está mayormente en español y rutas en inglés, lo cual es válido, pero conviene dejarlo explícito como estándar del proyecto.

## Conclusión

El sistema **sí respeta varios patrones estructurales importantes** (capas, seguridad, DTO/mappers, rutas versionadas), pero presenta **inconsistencias de gobernanza técnica** entre documentación, código legado y uniformidad de diseño. La mayor brecha hoy es la discrepancia de soft delete y la presencia de artefactos legacy que no parecen pertenecer al backend REST actual.
