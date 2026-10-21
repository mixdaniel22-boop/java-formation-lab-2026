# DDL

**Fecha**: 2026-10-21

## Objetivo
Documentar todo el esquema relacionado con las tablas y el JPA implementado, así como las decisiones tomadas respecto al cascade

## schema de BD

este se ecuentra en `src/resources/schema.sql`

## Desiciones tomadas respecto al uso de Cascade

### Cascade en Order
No fue utilizado para Customer ya que podemos tener un Customer en la BD aunque sin Orders

utilizado para Order Item para que al borrar la orden se borren tambien sus items asociados

# Cascade en Order Item

Se hace excepción aquí del cascade para evitar borrar un producto al borrar un item de una orden 

