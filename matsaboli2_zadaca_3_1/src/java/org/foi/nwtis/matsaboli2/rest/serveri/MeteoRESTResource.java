/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.rest.serveri;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletContext;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.core.MediaType;
import org.foi.nwtis.matsaboli2.konfiguracije.Konfiguracija;
import org.foi.nwtis.matsaboli2.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.matsaboli2.rest.klijenti.OWMKlijent;
import org.foi.nwtis.matsaboli2.web.podaci.MeteoPodaci;
import org.foi.nwtis.matsaboli2.web.slusaci.SlusacAplikacije;

/**
 * REST Web Service
 *
 * @author Matija Sabolić
 */
public class MeteoRESTResource {

    private String id;

    /**
     * Creates a new instance of MeteoRESTResource
     */
    private MeteoRESTResource(String id) {
        this.id = id;
    }

    /**
     * Get instance of the MeteoRESTResource
     */
    public static MeteoRESTResource getInstance(String id) {
        return new MeteoRESTResource(id);
    }

    /**
     * Metoda koja vraća važeće meteorološke podatke za odabrani uređaj u json
     * formatu.
     *
     * Retrieves representation of an instance of
     * org.foi.nwtis.matsaboli2.rest.serveri.MeteoRESTResource
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() throws ClassNotFoundException {
        ServletContext sc = SlusacAplikacije.getKontekst();

        Konfiguracija konf = (Konfiguracija) sc.getAttribute("App_Konfig");
        String appKey = konf.dajPostavku("apikey");

        BP_Konfiguracija bp_konf = (BP_Konfiguracija) sc.getAttribute("BP_Konfig");

        String bp_server = bp_konf.getServerDatabase();
        String bp_baza = bp_server + bp_konf.getUserDatabase();
        String bp_korisnik = bp_konf.getUserUsername();
        String bp_lozinka = bp_konf.getUserPassword();
        String bp_driver = bp_konf.getDriverDatabase();

        OWMKlijent owmClient = new OWMKlijent(appKey);

        Class.forName(bp_driver);
        JsonObjectBuilder job = Json.createObjectBuilder();

        try (Connection veza = DriverManager.getConnection(bp_baza, bp_korisnik, bp_lozinka);) {
            String sql = "SELECT * FROM uredaji WHERE id = " + id;
            Statement naredba = veza.createStatement();
            ResultSet odgovor = naredba.executeQuery(sql);
            odgovor.next();

            MeteoPodaci mp = owmClient.getRealTimeWeather(odgovor.getString("latitude"), odgovor.getString("longitude"));

            job.add("temp", mp.getTemperatureValue());
            job.add("tlak", mp.getPressureValue());
            job.add("vlaga", mp.getHumidityValue());

        } catch (SQLException ex) {
        }

        return job.build().toString();
    }

    /**
     * PUT method for updating or creating an instance of MeteoRESTResource
     *
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }

    /**
     * DELETE method for resource MeteoRESTResource
     */
    @DELETE
    public void delete() {
    }
}
