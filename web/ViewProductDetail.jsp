<%-- 
    Document   : ViewProductDetail
    Created on : Nov 15, 2018, 8:20:58 PM
    Author     : phasi
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Login</title>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css" integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO" crossorigin="anonymous"/>
        <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js" integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49" crossorigin="anonymous"></script>
        <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js" integrity="sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy" crossorigin="anonymous"></script>
        <link rel="stylesheet" href="css/test.css"/>
    </head>
    <body style="background-image: url(IMG/BG.jpg)">
        <div class="nev">
            <nav class="navbar navbar-dark bg-dark nav-bg">
                <a href="index.html"><img class="logo-nev" src="IMG/logo.png"/> </a>
                <ul>     
                    <c:choose>
                        <c:when test="${sessionScope.cust != null}">
                            <li><a href="Logout"><button type="button" class="btn btn-default btn-index btn-nav " >LOGOUT</button></a></li>
                            <li><a href="Cart"><button type="button" class="btn btn-default btn-index btn-nav ">CART</button></a></li>
                            <li><a href="HomePage"><button type="button" class="btn btn-default btn-index btn-nav ">Home</button></a></li>
                            </c:when>
                            <c:otherwise>
                            <li><a href="Login"><button type="button" class="btn btn-default btn-index btn-nav ">LOGIN</button></a></li>
                            <li><a href="Register"><button type="button" class="btn btn-default btn-index btn-nav ">REGISTER</button></a></li>
                            <li><a href="HomePage"><button type="button" class="btn btn-default btn-index btn-nav ">Home</button></a></li>
                            </c:otherwise>
                        </c:choose>
                    <li>
                        <form >
                            <input class="search btn btn-default btn-index" type="search" placeholder="Search" aria-label="Search">
                            <button class="btn btn-default btn-index btn-nav btn-search" type="submit">SEARCH</button>
                        </form>
                    </li>

                </ul>
            </nav>
        </div>
        
        


    </body>
</html>
