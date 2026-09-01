<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mis préstamos - Biblioteca UNTEC</title>
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
            <a href="${pageContext.request.contextPath}/libros">
                <span class="menu-icon">⌂</span> Catálogo
            </a>
            <a href="${pageContext.request.contextPath}/prestamos" class="active">
                <span class="menu-icon">▣</span> Mis préstamos
            </a>
        </nav>

        <div class="sidebar-bottom">
            Biblioteca Digital UNTEC<br>
            <span>Gestiona tus libros y préstamos.</span>
        </div>

        <form class="logout-form" action="${pageContext.request.contextPath}/logout" method="post">
            <button class="btn-logout" type="submit">⎋ Cerrar sesión</button>
        </form>
    </aside>

    <main class="main">

        <header class="topbar">
            <div class="page-title">Mis préstamos</div>
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
                <h1>Mis préstamos</h1>
                <p>Consulta tus libros y devuelve los ejemplares que ya no necesitas.</p>
            </div>

            <section class="panel">
                <div class="panel-header">
                    <div class="panel-title">
                        <span class="panel-title-icon">▣</span> Historial de préstamos
                    </div>
                </div>

                <div class="table-wrapper">
                    <table>
                        <thead>
                            <tr>
                                <th>Libro</th>
                                <th>Fecha préstamo</th>
                                <th>Fecha devolución</th>
                                <th>Estado</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="prestamo" items="${prestamos}">
                                <tr>
                                    <td><div class="book-title"><c:out value="${prestamo.tituloLibro}"/></div></td>
                                    <td><c:out value="${prestamo.fechaPrestamoTexto}"/></td>
                                    <td><c:out value="${prestamo.fechaDevolucionTexto}"/></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${prestamo.devuelto}">
                                                <span class="badge badge-ok">● Devuelto</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-pendiente">● Pendiente</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:if test="${not prestamo.devuelto}">
                                            <form action="${pageContext.request.contextPath}/devolucion" method="post">
                                                <input type="hidden" name="prestamoId" value="${prestamo.id}">
                                                <button class="btn-return" type="submit">Devolver</button>
                                            </form>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>

                            <c:if test="${empty prestamos}">
                                <tr>
                                    <td colspan="5">
                                        <div class="empty">
                                            <div class="empty-icon">📖</div>
                                            <p>Todavía no tienes préstamos registrados.</p>
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