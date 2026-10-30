# Criterios de evaluación — Week 11

## Checklist del reviewer

### Diagnóstico
- [ ] Logs de SQL habilitados en properties
- [ ] Número de queries antes del fix documentado en el PR

### Fix
- [ ] `JOIN FETCH` implementado en la query JPQL
- [ ] Sin `FetchType.EAGER` añadido como solución
- [ ] Número de queries después: ≤ 3 para el dataset de test

### Test
- [ ] Test con assertion explícita del número de queries (no solo "pasa")
- [ ] `mvn verify` en verde

### Calidad
- [ ] La query funciona con paginación (no rompe `Pageable`)
- [ ] Sin consultas N+1 residuales en otros métodos del repositorio

## Escala de madurez

| Junior | Semi-senior | Senior | Experto |
|--------|-------------|--------|---------|
| JOIN FETCH básico, sin métricas | Test con conteo de queries | @EntityGraph alternativo, batch_fetch_size | Propone estrategia de detección de N+1 en CI (Datasource proxy, p6spy) |

Queries para 50 �rdenes: 201
Queries para 50 �rdenes: 151 con el uso @EntityGraph sin join fetch
Queries para 50 �rdenes: 1  con el Join fetch

pageable
82L tal cual viene en el reto
pasa el test fetch = FetchType.LAZY y @BatchSize(size = 50)
