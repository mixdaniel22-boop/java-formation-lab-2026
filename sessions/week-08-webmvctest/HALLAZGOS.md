Se realiza el ejercicio 1, hallazgos y mejoras:
1. Faltaba el import del mapper en el SupplierControllerTest
2. la construcción de la url del Location le faltaba un '/' en el SupplierController
3. en el test-05 debía ser un isOk(200)
4. se agrega validacion del body en created endpoint
5. se refactorizan test y classes, mejorando el codigo y se agrega un nuevo test
6. se elimina dependencia de implementacion del service en el controller