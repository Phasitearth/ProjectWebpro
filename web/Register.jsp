<%-- 
    Document   : Signin
    Created on : Nov 15, 2018, 9:39:06 AM
    Author     : phasi
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>CLOCK ALERT</title>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css" integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO" crossorigin="anonymous"/>
        <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js" integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49" crossorigin="anonymous"></script>
        <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js" integrity="sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy" crossorigin="anonymous"></script>
        <link rel="stylesheet" href="css/test.css"/></head>
    <body class="singin-bg">

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

        <div class="signin-page">
            <form action="Register" method="post">
                <div class="form-row">
                    <div class="form-group col-md-6">
                        <label for="inputUsername4">Username</label>
                        <input type="text" name="username" class="form-control" id="inputEmail4" placeholder="Username" required>
                    </div>
                    <div class="form-group col-md-6">
                        <label for="inputPassword4">Password</label>
                        <input type="password" name="password" class="form-control" id="inputPassword4" placeholder="Password" required>
                    </div>
                </div>
                <div class="form-group inputSignin ">
                    <label for="inputAddress">Firstname</label>
                    <input type="text" name="firstname" class="form-control" id="inputAddress" placeholder="Enter your name" required>
                </div>
                <div class="form-group inputSignin">
                    <label for="inputAddress2">Lastname</label>
                    <input type="text" name="lastname" class="form-control" id="inputAddress2" placeholder="Enter your Lastname" required>
                </div>
                <div class="form-group inputSignin">
                    <label for="inputAddress3">Address</label>
                    <input type="text" name="address" class="form-control" id="inputAddress2" placeholder="Enter your Address"  required>
                </div>

                <div class="form-row">
                    <div class="form-group col-md-6">
                        <label for="inputCity">City</label>
                        <input type="text" name="city" class="form-control" id="inputCity"  required>
                    </div>
                    <div class="form-group col-md-6">
                        <label for="inputZip">Zip Code</label>
                        <input type="text" name="zipcode" class="form-control" id="inputZip"  required>
                    </div>
                </div>

                <div class="form-group col-md-6">
                    <button type="submit" class="btn btn btn-secondary">Sign In</button>
                </div>
            </form>
        </div>


    </body>
</html>
