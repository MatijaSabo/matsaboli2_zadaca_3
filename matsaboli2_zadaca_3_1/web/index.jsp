<%-- 
    Document   : index
    Created on : Apr 25, 2017, 4:19:58 PM
    Author     : grupa_1
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Dodaj uređaj</title>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css"></link>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    </head>
    <body>
        <%
            String naziv = request.getParameter("naziv");
            if (naziv == null) {
                naziv = "";
            }

            String adresa = request.getParameter("adresa");
            if (adresa == null) {
                adresa = "";
            }

            String latitude = request.getParameter("latitude");
            if (latitude == null) {
                latitude = "";
            }

            String longitude = request.getParameter("longitude");
            if (longitude == null) {
                longitude = "";
            }

            String temp = request.getParameter("temp");
            if (temp == null) {
                temp = "";
            }

            String tlak = request.getParameter("tlak");
            if (tlak == null) {
                tlak = "";
            }

            String vlaga = request.getParameter("vlaga");
            if (vlaga == null) {
                vlaga = "";
            }
        %>

        <div class="container" >
            <div class="jumbotron">
                <h1>NWTiS Zadaća 3</h1>
                <h3>Opis zadatka: Sustav aplikacija s korištenjem web servisa openweathermap.org i Google Maps API</h3>
                <h4>Autor: Matija Sabolić</h4>
            </div>
            <h1>Unos IoT uređaja</h1>
            <form action="${pageContext.servletContext.contextPath}/DodajUredjaj" method="POST">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <h3 class="panel-title">Naziv i adresa</h3>
                    </div>
                    <div class="panel-body">
                        <div class="form-group">
                            <div class="col-md-5">
                                <label for="naziv">Naziv: </label>
                                <input type="text" name="naziv" id="naziv" class="form-control" placeholder="Naziv" value='${naziv}' onchange="disable_lokacija()" />
                            </div>
                            <div class="col-md-5">
                                <label for="adresa">Adresa: </label>
                                <input type="text" name="adresa" id="adresa" class="form-control" placeholder="Adresa" value='${adresa}' onchange="disable_lokacija()" /> 
                            </div>
                            <div class="col-md-2">
                                <br>
                                <input id="lokacija" type="submit" name="button" value="Geo lokacija" class="btn"/>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <h3 class="panel-title">Geo lokacija</h3>
                    </div>
                    <div class="panel-body">
                        <div class="form-group">
                            <div class="col-md-5">
                                <label for="latitude">Latitude: </label>
                                <input id="latitude" type="text" name="latitude" class="form-control" placeholder="Latitude" readonly="true" value='${latitude}' />
                            </div>
                            <div class="col-md-5">
                                <label for="longitude">Longitude: </label>
                                <input id="longitude" type="text" name="longitude" class="form-control" placeholder="Longitude" readonly="true" value='${longitude}' />
                            </div>
                            <div class="col-md-2">
                                <br>
                                <input id="spremi" type="submit" name="button" value="Spremi" class="btn"/>
                            </div>
                        </div>
                    </div>             
                </div>
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <h3 class="panel-title">Meteo podaci</h3>
                    </div>
                    <div class="panel-body">
                        <div class="form-group">
                            <div class="col-md-3">
                                <label for="temp">Temperatura: </label>
                                <input id="temp" type="text" name="temp" class="form-control" placeholder="Temperatura" readonly="true" value='${temp}' />
                            </div>
                            <div class="col-md-3">
                                <label for="vlaga">Vlaga: </label>
                                <input id="vlaga" type="text" name="vlaga" class="form-control" placeholder="Vlaga" readonly="true" value='${vlaga}' />
                            </div>
                            <div class="col-md-3">
                                <label for="tlak">Tlak: </label>
                                <input id="tlak" type="text" name="tlak" class="form-control" placeholder="Tlak" readonly="true" value='${tlak}' />
                            </div>
                            <div class="col-md-3">
                                <br>
                                <input id="meteo" type="submit" name="button" value="Meteo podaci" class="btn" />
                            </div>
                        </div>
                    </div>
                </div>
            </form>
            <div class="well">
                <p> &reg; Napredne Web tehnlogije i servisi 2017.</p>
            </div>
        </div>
    </body>
</html>

<script>
    function disable_lokacija() {
        var name = document.getElementById("naziv").value;
        var adresa = document.getElementById("adresa").value;
        var btn_lokacija = document.getElementById("lokacija");

        if (name == "" || adresa == "") {
            btn_lokacija.disabled = true;
        } else {
            btn_lokacija.disabled = false;
        }
    }

    disable_lokacija();

    var longitude = document.getElementById("longitude").value;
    var latitude = document.getElementById("latitude").value;
    var btn_spremi = document.getElementById("spremi");
    var btn_meteo = document.getElementById("meteo");

    if (longitude == "" || latitude == "") {
        btn_spremi.disabled = true;
        btn_meteo.disabled = true;
    } else {
        btn_spremi.disabled = false;
        btn_meteo.disabled = false;
    }
</script>
