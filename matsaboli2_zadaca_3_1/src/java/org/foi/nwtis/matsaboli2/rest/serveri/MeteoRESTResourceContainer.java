/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.rest.serveri;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.foi.nwtis.matsaboli2.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.matsaboli2.rest.klijenti.GMKlijent;
import org.foi.nwtis.matsaboli2.web.podaci.Lokacija;
import org.foi.nwtis.matsaboli2.web.podaci.Uredjaj;
import org.foi.nwtis.matsaboli2.web.slusaci.SlusacAplikacije;

/**
 * REST Web Service
 *
 * @author Matija Sabolić
 */
@Path("/MeteoRest")
public class MeteoRESTResourceContainer {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of MeteoRESTResourceContainer
     */
    public MeteoRESTResourceContainer() {
    }

    /**
     * Metoda koja vraća sve uređaje iz baze podataka u json formatu.
     *
     * Retrieves representation of an instance of
     * org.foi.nwtis.matsaboli2.rest.serveri.MeteoRESTResourceContainer
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() throws ClassNotFoundException {
        ArrayList<Uredjaj> uredjaji = new ArrayList<Uredjaj>();

        ServletContext sc = SlusacAplikacije.getKontekst();
        BP_Konfiguracija bp_konf = (BP_Konfiguracija) sc.getAttribute("BP_Konfig");

        String bp_server = bp_konf.getServerDatabase();
        String bp_baza = bp_server + bp_konf.getUserDatabase();
        String bp_korisnik = bp_konf.getUserUsername();
        String bp_lozinka = bp_konf.getUserPassword();
        String bp_driver = bp_konf.getDriverDatabase();

        Class.forName(bp_driver);

        try (Connection veza = DriverManager.getConnection(bp_baza, bp_korisnik, bp_lozinka);) {
            String sql = "SELECT * FROM uredaji";
            Statement naredba = veza.createStatement();
            ResultSet odgovor = naredba.executeQuery(sql);

            while (odgovor.next()) {
                uredjaji.add(new Uredjaj(odgovor.getInt("id"), odgovor.getString("naziv"), new Lokacija(odgovor.getString("latitude"), odgovor.getString("longitude"))));
            }
        } catch (SQLException ex) {
        }

        JsonArrayBuilder jab = Json.createArrayBuilder();

        for (Uredjaj u : uredjaji) {
            JsonObjectBuilder job = Json.createObjectBuilder();
            job.add("ID", u.getId());
            job.add("Naziv", u.getNaziv());
            job.add("Longitude", u.getGeoloc().getLongitude());
            job.add("Latitude", u.getGeoloc().getLatitude());

            jab.add(job);
        }

        return jab.build().toString();
    }

    /**
     * Metoda koja sprema novi uređaj u bazu podataka. Metoda prima Json objekt
     * koji sadrži naziv i adresu uređaja. Prema tim podacima generira se
     * lokacija uređaja i spremaju se podaci u bazu podataka.
     *
     * POST method for creating an instance of MeteoRESTResource
     *
     * @param content representation for the new resource
     * @return an HTTP response with content of the created resource
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postJson(String content) throws ClassNotFoundException {

        JsonReader reader = Json.createReader(new StringReader(content));

        JsonObject jo = reader.readObject();
        String naziv = jo.getString("Naziv");
        String adresa = jo.getString("Adresa");

        GMKlijent gmClient = new GMKlijent();
        Lokacija l = gmClient.getGeoLocation(adresa);
        String latitude = l.getLatitude();
        String longitude = l.getLongitude();

        ServletContext sc = SlusacAplikacije.getKontekst();

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

        return Response.created(context.getAbsolutePath()).build();
    }

    /**
     * Sub-resource locator method for {id}
     */
    @Path("{id}")
    public MeteoRESTResource getMeteoRESTResource(@PathParam("id") String id) {
        return MeteoRESTResource.getInstance(id);
    }
}
