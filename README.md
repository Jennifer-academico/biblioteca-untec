# Biblioteca Digital UNTEC

Sistema web de gestión de biblioteca universitaria, desarrollado como evaluación del Módulo 5: Desarrollo de aplicaciones web dinámicas en Java.

## Descripción

La Universidad UNTEC necesitaba modernizar su biblioteca, que hasta ahora llevaba el catálogo de forma manual. Esta aplicación permite a cualquier usuario autenticado iniciar sesión, consultar el catálogo de libros disponibles, pedir libros prestados, devolverlos, y gestionar el catálogo agregando o dando de baja libros.

El flujo de una petición sigue este recorrido:

```
Navegador
   ↓
Servlet (controller)
   ↓
DAO (dao)
   ↓
JDBC
   ↓
Base de datos H2
   ↓
Servlet
   ↓
JSP + JSTL (view)
   ↓
Navegador
```

## Requerimientos cumplidos

| Requerimiento | Estado | Dónde se implementa |
|---|---|---|
| Java EE, JSP, Servlets, patrón MVC | Cumplido | Paquetes model, dao, controller; vistas JSP en webapp |
| Capa DAO con JDBC | Cumplido | LibroDAO, UsuarioDAO, PrestamoDAO |
| JSTL en la vista | Cumplido | c:if, c:forEach, c:choose, c:out en los 3 JSP |
| Formularios de interacción | Cumplido | Login, agregado de libro, préstamo, devolución |
| Gestión de sesiones | Cumplido | HttpSession guarda el usuario logueado |
| Despliegue en Tomcat vía WAR | Cumplido | mvn clean package genera biblioteca-untec.war |
| Base de datos H2 | Cumplido | Conexión Singleton en ConexionBD |
| Patrón Singleton para la conexión | Cumplido | ConexionBD.getInstancia() |

## Cómo funciona la aplicación

### Inicio de sesión

Cualquier usuario debe autenticarse antes de acceder al catálogo o a sus préstamos. El login valida correo y contraseña contra la tabla usuario, y si son correctos, guarda al usuario en una HttpSession. Esa sesión es lo que le permite al sistema recordar quién es el usuario mientras navega entre el catálogo y sus préstamos, sin necesidad de pedir la contraseña en cada página. Si un usuario intenta acceder a /libros o /prestamos sin haber iniciado sesión, el sistema lo redirige de vuelta al login.

### Cierre de sesión

El botón "Cerrar sesión" invalida la HttpSession actual (session.invalidate()), lo que borra toda la información guardada de ese usuario en el servidor. Es importante que esta acción exista porque, sin ella, cualquiera que use la misma computadora después podría seguir navegando con tu sesión abierta.

### Catálogo y agregado de libros

El catálogo muestra únicamente los libros marcados como activos. Cualquier usuario logueado puede agregar un libro nuevo completando ISBN, título, autor y cantidad de ejemplares; ese número de ejemplares se usa tanto como stock total como stock disponible inicial.

### Por qué "eliminar" un libro no lo borra de la base de datos

Al principio, "eliminar" un libro hacía un DELETE real sobre la tabla. El problema apareció al intentar eliminar un libro que ya tenía un préstamo asociado: la base de datos rechazaba la operación, porque borrar ese libro hubiera dejado un préstamo apuntando a un libro inexistente, rompiendo la integridad de los datos.

La solución fue agregar una columna activo a la tabla libro. Ahora, eliminar un libro simplemente lo marca como activo = false, sin borrar la fila. El catálogo deja de mostrarlo, pero su historial de préstamos, quién lo pidió, cuándo, si se devolvió, sigue existiendo intacto. Es el mismo criterio que usa cualquier sistema real: nunca conviene borrar un dato que tiene historial asociado.

### Pedir un libro prestado

Cuando un usuario pide un libro, el sistema primero intenta descontar un ejemplar disponible. Si no queda ninguno, el préstamo directamente no se crea, y se le informa al usuario que no hay stock. Si hay stock, se descuenta un ejemplar y se registra el préstamo con la fecha del día.

### Devolver un libro

Al devolver, el sistema marca el préstamo como devuelto, guarda la fecha de devolución, y repone un ejemplar disponible en el catálogo. Un mismo préstamo no puede devolverse dos veces: si ya estaba marcado como devuelto, el sistema no hace nada y avisa que ese préstamo ya no está pendiente.

### Codificación de texto (UTF-8)

Al ingresar un libro con "ñ" en el título, el texto se guardaba corrupto en la base de datos. El problema era que Tomcat no interpretaba automáticamente el texto enviado por el formulario como UTF-8. Se solucionó indicándoselo explícitamente al servlet antes de leer cualquier dato del formulario.

### Por qué se escribieron pruebas automatizadas

Se probaron las reglas que realmente podrían romper la aplicación si alguien las modificara sin querer: que no se pueda prestar un libro sin stock, que no se pueda devolver dos veces el mismo préstamo, que credenciales incorrectas no autentiquen, que las acciones sin sesión redirijan al login. No se testearon getters y setters simples porque no protegen contra ningún error real: solo guardan un valor tal cual se les pasa.

Cobertura final obtenida con JaCoCo: 88% de instrucciones, 82% de ramas, en 40 pruebas.

## Mejoras futuras identificadas

### Registro de usuarios nuevos

Actualmente el sistema solo cuenta con un usuario de demostración, cargado automáticamente al iniciar la aplicación. No existe un formulario para que un usuario nuevo cree su propia cuenta desde la interfaz; UsuarioDAO solo tiene el método buscarPorCredenciales(), necesario para el login, pero no un método crear().

Agregar esta funcionalidad implicaría un formulario de registro con nombre, correo y contraseña, un método UsuarioDAO.crear() que valide que el correo no esté repetido antes de insertar, y considerar si conviene aplicar un hash a la contraseña en vez de guardarla en texto plano.

### Roles de usuario

Actualmente cualquier usuario autenticado puede realizar todas las acciones del sistema, incluyendo agregar y eliminar libros del catálogo, tareas que en una biblioteca real corresponderían solo al personal administrativo, no a cualquier estudiante. Diferenciar roles, por ejemplo "estudiante" y "bibliotecario", implicaría agregar una columna de rol a la tabla usuario, verificar ese rol en LibroServlet antes de permitir agregar o eliminar, y ocultar esas opciones en libros.jsp para quienes no tengan permiso.

## Cómo ejecutar el proyecto

Requisitos: JDK 21, compila con target 17, Maven, Apache Tomcat 9, Eclipse IDE for Enterprise Java and Web Developers.

Importar en Eclipse:

```
File → Import... → Maven → Existing Maven Projects
```

Seleccionar la carpeta que contiene pom.xml y esperar que Maven resuelva las dependencias.

Ejecutar:

```
mvn clean test package
```

o desde Eclipse, clic derecho sobre el proyecto → Run As → Run on Server → Tomcat v9.0.

URL una vez desplegado:

```
http://localhost:8080/biblioteca-untec/
```

Usuario de prueba:

```
correo: alumno@untec.cl
clave: 1234
```

## Estructura del proyecto

```
src/main/java
├── controller
│   ├── LoginServlet.java
│   ├── LogoutServlet.java
│   ├── LibroServlet.java
│   ├── PrestamoServlet.java
│   └── DevolucionServlet.java
├── dao
│   ├── ConexionBD.java
│   ├── LibroDAO.java
│   ├── UsuarioDAO.java
│   └── PrestamoDAO.java
└── model
    ├── Libro.java
    ├── Usuario.java
    └── Prestamo.java

src/main/webapp
├── index.jsp
├── css/estilos.css
├── img/logo-biblioteca.png
└── WEB-INF
    ├── web.xml
    └── views
        ├── libros.jsp
        └── prestamos.jsp

src/test/java
├── controller (tests con Mockito)
├── dao (tests con base H2 en memoria)
└── model (tests de lógica de negocio)
```

