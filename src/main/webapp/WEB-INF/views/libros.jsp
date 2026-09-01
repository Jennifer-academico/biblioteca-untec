<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Catálogo - Biblioteca UNTEC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>

<div class="app">

    <aside class="sidebar">
        <div class="sidebar-logo">
            <img src="${pageContext.request.contextPath}/img/logo-biblioteca.png" alt="Biblioteca UNTEC">
            <div class="sidebar-logo-text">
                <strong>BIBLIOTECA</strong>
                <strong>UNTEC</strong>
                <span>Conocimiento que transforma</span>
            </div>
        </div>

        <div class="menu-title">Biblioteca</div>

        <nav class="menu">
            <a href="${pageContext.request.contextPath}/libros" class="active">
                <span class="menu-icon">⌂</span> Catálogo
            </a>
            <a href="${pageContext.request.contextPath}/prestamos">
                <span class="menu-icon">▣</span> Mis préstamos
            </a>
        </nav>

        <div class="sidebar-bottom">
            Biblioteca Digital UNTEC<br>
            <span>Plataforma de gestión bibliográfica</span>
        </div>

        <form class="logout-form" action="${pageContext.request.contextPath}/logout" method="post">
            <button class="btn-logout" type="submit">⎋ Cerrar sesión</button>
        </form>
    </aside>

    <main class="main">

        <header class="topbar">
            <div class="page-title">Catálogo</div>
            <div class="user-area">
                <span>Hola, <strong><c:out value="${sessionScope.usuarioActual.nombre}"/></strong></span>
                <div class="user-avatar">
                    <c:out value="${sessionScope.usuarioActual.nombre.substring(0,1).toUpperCase()}"/>
                </div>
            </div>
        </header>

        <div class="container">

            <c:if test="${not empty sessionScope.mensaje}">
                <div class="alert alert-success">
                    ✓ <c:out value="${sessionScope.mensaje}"/>
                </div>
                <c:remove var="mensaje" scope="session"/>
            </c:if>

            <div class="page-heading">
                <h1>Tu biblioteca, en un solo lugar</h1>
                <p>Administra los libros disponibles y gestiona tus préstamos.</p>
            </div>

            <section class="panel">
                <div class="panel-header">
                    <div class="panel-title">
                        <span class="panel-title-icon">+</span> Agregar nuevo libro
                    </div>
                </div>
                <div class="panel-body">
                    <form class="grid-form" action="${pageContext.request.contextPath}/libros" method="post">
                        <input type="hidden" name="accion" value="agregar">

                        <div class="field">
                            <label for="isbn">ISBN</label>
                            <input id="isbn" type="text" name="isbn" placeholder="Ej: 9781234567897" required>
                        </div>

                        <div class="field">
                            <label for="titulo">Título</label>
                            <input id="titulo" type="text" name="titulo" placeholder="Título del libro" required>
                        </div>

                        <div class="field">
                            <label for="autor">Autor</label>
                            <input id="autor" type="text" name="autor" placeholder="Autor del libro" required>
                        </div>

                        <div class="field">
                            <label for="ejemplares">Ejemplares</label>
                            <input id="ejemplares" type="number" name="ejemplares" min="1" value="1" required>
                        </div>

                        <button class="btn-primary" type="submit">+ Agregar</button>
                    </form>
                </div>
            </section>

            <section class="panel">
                <div class="panel-header">
                    <div class="panel-title">
                        <span class="panel-title-icon">▤</span> Catálogo
                    </div>
                </div>

                <div class="table-wrapper">
                    <table>
                        <thead>
                            <tr>
                                <th>Título</th>
                                <th>Autor</th>
                                <th>Disponibilidad</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="libro" items="${libros}">
                                <tr>
                                    <td><div class="book-title"><c:out value="${libro.titulo}"/></div></td>
                                    <td><c:out value="${libro.autor}"/></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${libro.disponible}">
                                                <span class="badge badge-ok">
                                                    ● <c:out value="${libro.ejemplaresDisponibles}"/> / <c:out value="${libro.ejemplaresTotales}"/>
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-no">● Sin stock</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <div class="actions">
                                            <c:if test="${libro.disponible}">
                                                <form action="${pageContext.request.contextPath}/prestamos" method="post">
                                                    <input type="hidden" name="libroId" value="${libro.id}">
                                                    <button class="btn-primary" type="submit">Pedir prestado</button>
                                                </form>
                                            </c:if>
                                            <form action="${pageContext.request.contextPath}/libros" method="post">
                                                <input type="hidden" name="accion" value="eliminar">
                                                <input type="hidden" name="id" value="${libro.id}">
                                                <button class="btn-danger" type="submit">Eliminar</button>
                                            </form>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>

                            <c:if test="${empty libros}">
                                <tr>
                                    <td colspan="4">
                                        <div class="empty">
                                            <div class="empty-icon">📚</div>
                                            <p>No hay libros cargados todavía.</p>
                                        </div>
                                    </td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </section>

        </div>
    </main>
</div>

</body>
</html>