<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Biblioteca UNTEC - Ingreso</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>

<div class="login-page">

    <section class="login-brand">
        <div class="login-brand-content">
            <img src="${pageContext.request.contextPath}/img/logo-biblioteca.png" alt="Biblioteca UNTEC">
            <h2>Biblioteca Digital UNTEC</h2>
            <p>Un espacio para descubrir, consultar y gestionar conocimiento.</p>
        </div>
    </section>

    <section class="login-form-side">
        <div class="login-box">
            <h1>¡Bienvenido!</h1>
            <p class="login-box-subtitle">Ingresa a tu cuenta para continuar.</p>

            <c:if test="${not empty error}">
                <div class="alert alert-error">
                    ⚠ <c:out value="${error}"/>
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/login" method="post">
                <div class="login-field">
                    <label for="correo">Correo electrónico</label>
                    <input type="email" id="correo" name="correo" placeholder="tu@correo.com" required autofocus>
                </div>

                <div class="login-field">
                    <label for="contrasena">Contraseña</label>
                    <input type="password" id="contrasena" name="contrasena" placeholder="••••••••" required>
                </div>

                <button class="login-button" type="submit">Ingresar a la biblioteca</button>
            </form>

            <div class="login-footer">
                Biblioteca Digital UNTEC<br>
                Conocimiento que transforma
            </div>
        </div>
    </section>

</div>

</body>
</html>