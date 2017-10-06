/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.web;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import org.foi.nwtis.matsaboli2.konfiguracije.Konfiguracija;
import org.foi.nwtis.matsaboli2.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.matsaboli2.rest.klijenti.OWMKlijent;
import org.foi.nwtis.matsaboli2.web.podaci.MeteoPodaci;
import org.foi.nwtis.matsaboli2.web.slusaci.SlusacAplikacije;

/**
 *
 * @author Matija Sabolić
 */
public class PreuzmiMeteoPodatke extends Thread {

    private boolean flag = true;
    ServletContext sc;

    /**
     * Metoda koja se poziva kada se prekine rad dretve.
     */
    @Override
    public void interrupt() {
        flag = false;
        super.interrupt();
    }

    /**
     * Metoda koja preuzima u pravilnim vremenskim intervalima važaće
     * meteorološke podatke za sve uređaje koji su spremljeni u bazi podataka.
     * Ti meteorološki podaci se spremaju u bazu podataka u tablicu meteo.
     */
    @Override
    public void run() {
        sc = SlusacAplikacije.getKontekst();

        BP_Konfiguracija bp_konf = (BP_Konfiguracija) sc.getAttribute("BP_Konfig");

        String bp_server = bp_konf.getServerDatabase();
        String bp_baza = bp_server + bp_konf.getUserDatabase();
        String bp_korisnik = bp_konf.getUserUsername();
        String bp_lozinka = bp_konf.getUserPassword();
        String bp_driver = bp_konf.getDriverDatabase();

        Konfiguracija konf = (Konfiguracija) sc.getAttribute("App_Konfig");
        String apikey = konf.dajPostavku("apikey");
        int intervalDretve = Integer.parseInt(konf.dajPostavku("intervalDretveZaMeteoPodatke"));

        while (flag) {
            long pocetak = System.currentTimeMillis();

            OWMKlijent owmClient = new OWMKlijent(apikey);

            try {
                Class.forName(bp_driver);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(PreuzmiMeteoPodatke.class.getName()).log(Level.SEVERE, null, ex);
            }

            try (Connection veza = DriverManager.getConnection(bp_baza, bp_korisnik, bp_lozinka);) {
                String sql = "SELECT * FROM uredaji";
                Statement naredba = veza.createStatement();
                Statement insert = veza.createStatement();
                ResultSet odgovor = naredba.executeQuery(sql);

                MeteoPodaci mp = null;

                while (odgovor.next()) {
                    mp = owmClient.getRealTimeWeather(odgovor.getString("latitude"), odgovor.getString("longitude"));

                    String vrijemeOpis = mp.getWeatherValue();
                    if (vrijemeOpis.length() > 25) {
                        vrijemeOpis = mp.getWeatherValue().substring(0, 22) + "...";
                    }

                    String sql2 = "INSERT INTO meteo VALUES (default, " + odgovor.getInt("id") + ", 'matsaboli2', "
                            + odgovor.getString("latitude") + ", " + odgovor.getString("longitude") + ", '" + String.valueOf(mp.getWeatherNumber()) + "', '"
                            + vrijemeOpis + "', " + mp.getTemperatureValue() + ", " + mp.getTemperatureMin() + ", "
                            + mp.getTemperatureMax() + ", " + mp.getHumidityValue() + ", " + mp.getPressureValue() + ", "
                            + mp.getWindSpeedValue() + ", " + mp.getWindDirectionValue() + ", default)";

                    insert.executeUpdate(sql2);
                }
            } catch (SQLException ex) {
            }

            long kraj = System.currentTimeMillis();

            try {
                sleep((intervalDretve * 1000) - (kraj - pocetak));
            } catch (InterruptedException ex) {
                Logger.getLogger(PreuzmiMeteoPodatke.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public synchronized void start() {
        super.start();
    }

}
