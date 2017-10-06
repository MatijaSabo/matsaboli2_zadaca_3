/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.ws.serveri;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.servlet.ServletContext;
import org.foi.nwtis.matsaboli2.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.matsaboli2.rest.klijenti.GMKlijent;
import org.foi.nwtis.matsaboli2.web.podaci.Lokacija;
import org.foi.nwtis.matsaboli2.web.podaci.MeteoPodaci;
import org.foi.nwtis.matsaboli2.web.podaci.Uredjaj;
import org.foi.nwtis.matsaboli2.web.slusaci.SlusacAplikacije;

/**
 *
 * @author Matija Sabolić
 */
@WebService(serviceName = "GeoMeteoWS")
public class GeoMeteoWS {

    /**
     * Web service operation
     *
     * Metoda koja vraća sve uređaje spremljene u bazi podataka.
     */
    @WebMethod(operationName = "dajSveUredjaje")
    public java.util.List<Uredjaj> dajSveUredjaje() throws ClassNotFoundException {
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

        return uredjaji;
    }

    /**
     * Web service operation
     *
     * Metoda koja dodaje novi uređaj u bazu podataka.
     */
    @WebMethod(operationName = "dodajUredjaj")
    public Boolean dodajUredjaj(@WebParam(name = "uredjaj") Uredjaj uredjaj) throws ClassNotFoundException {
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
            sql = "INSERT INTO uredaji (id, naziv, latitude, longitude) VALUES (" + id + ", '" + uredjaj.getNaziv() + "', "
                    + uredjaj.getGeoloc().getLatitude() + ", " + uredjaj.getGeoloc().getLongitude() + ")";

            naredba.executeUpdate(sql);
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }

    /**
     * Web service operation
     *
     * Metoda koja vraća sve meteo podatke za odabrani uređaj u zadanom
     * intervalu. Id je identifikator uređaja u bazi podataka.
     */
    @WebMethod(operationName = "dajSveMeteoPodatkeZaUredjaj")
    public List<MeteoPodaci> dajSveMeteoPodatkeZaUredjaj(@WebParam(name = "id") int id, @WebParam(name = "od") long od, @WebParam(name = "do") long parameter1) throws ClassNotFoundException {
        List<MeteoPodaci> meteoPodaci = new ArrayList<>();

        ServletContext sc = SlusacAplikacije.getKontekst();
        BP_Konfiguracija bp_konf = (BP_Konfiguracija) sc.getAttribute("BP_Konfig");

        String bp_server = bp_konf.getServerDatabase();
        String bp_baza = bp_server + bp_konf.getUserDatabase();
        String bp_korisnik = bp_konf.getUserUsername();
        String bp_lozinka = bp_konf.getUserPassword();
        String bp_driver = bp_konf.getDriverDatabase();

        Class.forName(bp_driver);

        Timestamp vrijeme_od = new Timestamp(od * 1000);
        Timestamp vrijeme_do = new Timestamp(parameter1 * 1000);

        try (Connection veza = DriverManager.getConnection(bp_baza, bp_korisnik, bp_lozinka);) {
            String sql = "SELECT * FROM meteo WHERE id = " + id + " AND preuzeto >= '" + vrijeme_od + "' AND preuzeto <= '" + vrijeme_do + "'";
            Statement naredba = veza.createStatement();
            ResultSet odgovor = naredba.executeQuery(sql);

            while (odgovor.next()) {
                meteoPodaci.add(new MeteoPodaci(new Date(), new Date(), odgovor.getFloat("temp"), odgovor.getFloat("tempMin"), odgovor.getFloat("tempMax"),
                        "C", odgovor.getFloat("vlaga"), "%", odgovor.getFloat("tlak"), "hPa", odgovor.getFloat("vjetar"), "vjetar",
                        odgovor.getFloat("vjetarSmjer"), "-", "-", 1, "oblak", "-", (float) 1.2345, "-", "-", Integer.parseInt(odgovor.getString("vrijeme")), odgovor.getString("vrijemeOpis"),
                        "-", odgovor.getTimestamp("preuzeto")));
            }

        } catch (SQLException ex) {
        }

        return meteoPodaci;
    }

    /**
     * Web service operation
     *
     * Metoda koja vraća zadnje meteo podatke za odabrani uređej. Id je
     * identifikator uređaja u bazi podataka.
     */
    @WebMethod(operationName = "dajZadnjeMeteoPodatkeZaUredjaj")
    public MeteoPodaci dajZadnjeMeteoPodatkeZaUredjaj(@WebParam(name = "id") int id) throws ClassNotFoundException {
        MeteoPodaci meteoPodaci;

        ServletContext sc = SlusacAplikacije.getKontekst();
        BP_Konfiguracija bp_konf = (BP_Konfiguracija) sc.getAttribute("BP_Konfig");

        String bp_server = bp_konf.getServerDatabase();
        String bp_baza = bp_server + bp_konf.getUserDatabase();
        String bp_korisnik = bp_konf.getUserUsername();
        String bp_lozinka = bp_konf.getUserPassword();
        String bp_driver = bp_konf.getDriverDatabase();

        Class.forName(bp_driver);

        try (Connection veza = DriverManager.getConnection(bp_baza, bp_korisnik, bp_lozinka);) {
            String sql = "SELECT * FROM meteo WHERE id = " + id + " ORDER BY preuzeto desc";
            Statement naredba = veza.createStatement();
            ResultSet odgovor = naredba.executeQuery(sql);
            odgovor.next();

            float broj = (float) 1.2345;

            meteoPodaci = new MeteoPodaci(
                    new Date(), new Date(), odgovor.getFloat("temp"), odgovor.getFloat("tempMin"), odgovor.getFloat("tempMax"),
                    "C", odgovor.getFloat("vlaga"), "%", odgovor.getFloat("tlak"), "hPa", odgovor.getFloat("vjetar"), "vjetar",
                    odgovor.getFloat("vjetarSmjer"), "-", "-", 1, "oblak", "-", broj, "-", "-", Integer.parseInt(odgovor.getString("vrijeme")), odgovor.getString("vrijemeOpis"),
                    "-", odgovor.getTimestamp("preuzeto")
            );

        } catch (SQLException ex) {
            meteoPodaci = null;
        }

        return meteoPodaci;
    }

    /**
     * Web service operation
     *
     * Metoda koja vraća najmaju i najveću temperaturu za odabrani uređaj u
     * određenom vremenskom intervalu. Id je identifikator uređaja u bazi
     * podataka.
     */
    @WebMethod(operationName = "dajMinMaxTempZaUredjaj")
    public List<Float> dajMinMaxTempZaUredjaj(@WebParam(name = "id") int id, @WebParam(name = "od") long od, @WebParam(name = "parameter1") long parameter1) throws ClassNotFoundException {
        List<Float> meteoPodaci = new ArrayList<>();

        ServletContext sc = SlusacAplikacije.getKontekst();
        BP_Konfiguracija bp_konf = (BP_Konfiguracija) sc.getAttribute("BP_Konfig");

        String bp_server = bp_konf.getServerDatabase();
        String bp_baza = bp_server + bp_konf.getUserDatabase();
        String bp_korisnik = bp_konf.getUserUsername();
        String bp_lozinka = bp_konf.getUserPassword();
        String bp_driver = bp_konf.getDriverDatabase();

        Class.forName(bp_driver);

        Timestamp vrijeme_od = new Timestamp(od * 1000);
        Timestamp vrijeme_do = new Timestamp(parameter1 * 1000);

        try (Connection veza = DriverManager.getConnection(bp_baza, bp_korisnik, bp_lozinka);) {
            String sql = "SELECT MIN(temp) AS temp FROM meteo WHERE id = " + id + " AND preuzeto >= '" + vrijeme_od + "' AND preuzeto <= '" + vrijeme_do + "'";
            Statement naredba = veza.createStatement();
            ResultSet odgovor = naredba.executeQuery(sql);
            odgovor.next();
            meteoPodaci.add(odgovor.getFloat("temp"));

            String sql2 = "SELECT MAX(temp) AS temp FROM meteo WHERE id = " + id + " AND preuzeto >= '" + vrijeme_od + "' AND preuzeto <= '" + vrijeme_do + "'";
            Statement naredba2 = veza.createStatement();
            ResultSet odgovor2 = naredba2.executeQuery(sql2);
            odgovor2.next();
            meteoPodaci.add(odgovor2.getFloat("temp"));
        } catch (SQLException ex) {
        }

        return meteoPodaci;
    }

    /**
     * Web service operation
     *
     * Metoda koja vraća najmaju i najveću vlagu za odabrani uređaj u određenom
     * vremenskom intervalu. Id je identifikator uređaja u bazi podataka.
     */
    @WebMethod(operationName = "dajMinMaxVlagaZaUredjaj")
    public List<Float> dajMinMaxVlagaZaUredjaj(@WebParam(name = "id") int id, @WebParam(name = "od") long od, @WebParam(name = "parameter1") long parameter1) throws ClassNotFoundException {
        List<Float> meteoPodaci = new ArrayList<>();

        ServletContext sc = SlusacAplikacije.getKontekst();
        BP_Konfiguracija bp_konf = (BP_Konfiguracija) sc.getAttribute("BP_Konfig");

        String bp_server = bp_konf.getServerDatabase();
        String bp_baza = bp_server + bp_konf.getUserDatabase();
        String bp_korisnik = bp_konf.getUserUsername();
        String bp_lozinka = bp_konf.getUserPassword();
        String bp_driver = bp_konf.getDriverDatabase();

        Class.forName(bp_driver);

        Timestamp vrijeme_od = new Timestamp(od * 1000);
        Timestamp vrijeme_do = new Timestamp(parameter1 * 1000);

        try (Connection veza = DriverManager.getConnection(bp_baza, bp_korisnik, bp_lozinka);) {
            String sql = "SELECT MIN(vlaga) AS vlaga FROM meteo WHERE id = " + id + " AND preuzeto >= '" + vrijeme_od + "' AND preuzeto <= '" + vrijeme_do + "'";
            Statement naredba = veza.createStatement();
            ResultSet odgovor = naredba.executeQuery(sql);
            odgovor.next();
            meteoPodaci.add(odgovor.getFloat("vlaga"));

            String sql2 = "SELECT MAX(vlaga) AS vlaga FROM meteo WHERE id = " + id + " AND preuzeto >= '" + vrijeme_od + "' AND preuzeto <= '" + vrijeme_do + "'";
            Statement naredba2 = veza.createStatement();
            ResultSet odgovor2 = naredba2.executeQuery(sql2);
            odgovor2.next();
            meteoPodaci.add(odgovor2.getFloat("vlaga"));
        } catch (SQLException ex) {
        }

        return meteoPodaci;
    }

    /**
     * Web service operation
     *
     * Metoda koja vraća najmaji i najveć tlak za odabrani uređaj u određenom
     * vremenskom intervalu. Id je identifikator uređaja u bazi podataka.
     */
    @WebMethod(operationName = "dajMinMaxTlakZaUredjaj")
    public List<Float> dajMinMaxTlakZaUredjaj(@WebParam(name = "id") int id, @WebParam(name = "od") long od, @WebParam(name = "parameter1") long parameter1) throws ClassNotFoundException {
        List<Float> meteoPodaci = new ArrayList<>();

        ServletContext sc = SlusacAplikacije.getKontekst();
        BP_Konfiguracija bp_konf = (BP_Konfiguracija) sc.getAttribute("BP_Konfig");

        String bp_server = bp_konf.getServerDatabase();
        String bp_baza = bp_server + bp_konf.getUserDatabase();
        String bp_korisnik = bp_konf.getUserUsername();
        String bp_lozinka = bp_konf.getUserPassword();
        String bp_driver = bp_konf.getDriverDatabase();

        Class.forName(bp_driver);

        Timestamp vrijeme_od = new Timestamp(od * 1000);
        Timestamp vrijeme_do = new Timestamp(parameter1 * 1000);

        try (Connection veza = DriverManager.getConnection(bp_baza, bp_korisnik, bp_lozinka);) {
            String sql = "SELECT MIN(tlak) AS tlak FROM meteo WHERE id = " + id + " AND preuzeto >= '" + vrijeme_od + "' AND preuzeto <= '" + vrijeme_do + "'";
            Statement naredba = veza.createStatement();
            ResultSet odgovor = naredba.executeQuery(sql);
            odgovor.next();
            meteoPodaci.add(odgovor.getFloat("tlak"));

            String sql2 = "SELECT MAX(tlak) AS tlak FROM meteo WHERE id = " + id + " AND preuzeto >= '" + vrijeme_od + "' AND preuzeto <= '" + vrijeme_do + "'";
            Statement naredba2 = veza.createStatement();
            ResultSet odgovor2 = naredba2.executeQuery(sql2);
            odgovor2.next();
            meteoPodaci.add(odgovor2.getFloat("tlak"));
        } catch (SQLException ex) {
        }

        return meteoPodaci;
    }

    /**
     * Web service operation
     *
     * Metoda koja dodaje novi uređaj u bazu podataka na temelju naziva i adrese
     * uređaja. Generira se njegova lokacija i id te se podaci spremaju u bazu
     * podataka.
     */
    @WebMethod(operationName = "dodajUredaj")
    public Boolean dodajUredaj(@WebParam(name = "naziv") String naziv, @WebParam(name = "adresa") String adresa) throws ClassNotFoundException {
        ServletContext sc = SlusacAplikacije.getKontekst();
        BP_Konfiguracija bp_konf = (BP_Konfiguracija) sc.getAttribute("BP_Konfig");

        String bp_server = bp_konf.getServerDatabase();
        String bp_baza = bp_server + bp_konf.getUserDatabase();
        String bp_korisnik = bp_konf.getUserUsername();
        String bp_lozinka = bp_konf.getUserPassword();
        String bp_driver = bp_konf.getDriverDatabase();

        Class.forName(bp_driver);

        GMKlijent gmClient = new GMKlijent();
        Lokacija l = gmClient.getGeoLocation(adresa);
        String latitude = l.getLatitude();
        String longitude = l.getLongitude();

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
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }
}
