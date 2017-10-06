/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.web;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.foi.nwtis.matsaboli2.konfiguracije.Konfiguracija;
import org.foi.nwtis.matsaboli2.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.matsaboli2.rest.klijenti.GMKlijent;
import org.foi.nwtis.matsaboli2.rest.klijenti.OWMKlijent;
import org.foi.nwtis.matsaboli2.web.podaci.Lokacija;
import org.foi.nwtis.matsaboli2.web.podaci.MeteoPodaci;
import org.foi.nwtis.matsaboli2.web.slusaci.SlusacAplikacije;

/**
 *
 * @author Matija Sabolić
 */
@WebServlet(name = "DodajUredjaj", urlPatterns = {"/DodajUredjaj"})
public class DodajUredjaj extends HttpServlet {

    String latitude;
    String longitude;
    String temp;
    String tlak;
    String vlaga;
    String naziv;
    String adresa;
    String akcija;
    ServletContext sc;

    /**
     * Metoda koja prepoznaje koju je akciju korisnik odabrao na korisničom
     * sučelju aplikacije te prema tome poziva odgovarajuću metodu.
     *
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, ClassNotFoundException {

        request.setCharacterEncoding("utf8");

        sc = SlusacAplikacije.getKontekst();

        naziv = request.getParameter("naziv");
        adresa = request.getParameter("adresa");
        akcija = request.getParameter("button");
        longitude = request.getParameter("longitude");
        latitude = request.getParameter("latitude");

        RequestDispatcher rd = null;

        switch (akcija) {
            case "Geo lokacija":
                geoLokacija();

                rd = this.getServletContext().getRequestDispatcher("/index.jsp");

                request.setAttribute("naziv", naziv);
                request.setAttribute("adresa", adresa);
                request.setAttribute("longitude", longitude);
                request.setAttribute("latitude", latitude);

                rd.forward(request, response);
                break;
            case "Spremi":
                spremiLokaciju();

                rd = this.getServletContext().getRequestDispatcher("/index.jsp");

                request.setAttribute("naziv", naziv);
                request.setAttribute("adresa", adresa);
                request.setAttribute("longitude", longitude);
                request.setAttribute("latitude", latitude);

                rd.forward(request, response);

                break;
            case "Meteo podaci":
                meteoPodaci();

                rd = this.getServletContext().getRequestDispatcher("/index.jsp");

                request.setAttribute("naziv", naziv);
                request.setAttribute("adresa", adresa);
                request.setAttribute("longitude", longitude);
                request.setAttribute("latitude", latitude);
                request.setAttribute("temp", temp);
                request.setAttribute("tlak", tlak);
                request.setAttribute("vlaga", vlaga);

                rd.forward(request, response);

                break;
            default:
                break;
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DodajUredjaj.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DodajUredjaj.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    /**
     * Metoda koja generira latitude i longitude na temelju adrese pomoću Google
     * servisa.
     */
    private void geoLokacija() {
        GMKlijent gmClient = new GMKlijent();
        Lokacija l = gmClient.getGeoLocation(adresa);
        latitude = l.getLatitude();
        longitude = l.getLongitude();
    }

    /**
     * Metoda koja sprema novi uređaj u bazu podataka. Spremaju se id, adresa,
     * latitude i longitude.
     *
     * @throws ClassNotFoundException
     */
    private void spremiLokaciju() throws ClassNotFoundException {
        BP_Konfiguracija bp_konf = (BP_Konfiguracija) sc.getAttribute("BP_Konfig");

        String bp_server = bp_konf.getServerDatabase();
        String bp_baza = bp_server + bp_konf.getUserDatabase();
        String bp_korisnik = bp_konf.getUserUsername();
        String bp_lozinka = bp_konf.getUserPassword();
        String bp_driver = bp_konf.getDriverDatabase();

        Class.forName(bp_driver);

        try (Connection veza = DriverManager.getConnection(bp_baza, bp_korisnik, bp_lozinka);) {
            String sql = "SELECT MAX(id) AS id FROM uredaji";
            Statement naredba = veza.createStatement();
            ResultSet odgovor = naredba.executeQuery(sql);
            odgovor.next();
            int id = odgovor.getInt("id");

            id = id + 1;
            sql = "INSERT INTO uredaji (id, naziv, latitude, longitude) VALUES (" + id + ", '" + naziv + "', "
                    + latitude + ", " + longitude + ")";

            naredba.executeUpdate(sql);

        } catch (SQLException ex) {
        }
    }

    /**
     * Metoda koja preuzima važaće meteorološke podatke na temelju lokacije
     * uređaja.
     */
    private void meteoPodaci() {
        Konfiguracija konf = (Konfiguracija) sc.getAttribute("App_Konfig");
        String apikey = konf.dajPostavku("apikey");

        OWMKlijent owmClient = new OWMKlijent(apikey);
        MeteoPodaci mp = owmClient.getRealTimeWeather(latitude, longitude);

        String temp_unit = mp.getTemperatureUnit();
        temp = mp.getTemperatureValue().toString() + " " + temp_unit;

        String vlaga_unit = mp.getHumidityUnit();
        vlaga = mp.getHumidityValue().toString() + " " + vlaga_unit;

        String tlak_unit = mp.getPressureUnit();
        tlak = mp.getPressureValue().toString() + " " + tlak_unit;
    }

}
