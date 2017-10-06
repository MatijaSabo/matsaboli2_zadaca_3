/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.web.zrna;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import org.foi.nwtis.matsaboli2.ws.klijenti.MeteoWSKlijent;
import org.foi.nwtis.matsaboli2.ws.klijenti.RESTwsKlijent;
import org.foi.nwtis.matsaboli2.ws.klijenti.RESTwsKlijent2;
import org.foi.nwtis.matsaboli2.ws.serveri.ClassNotFoundException_Exception;
import org.foi.nwtis.matsaboli2.ws.serveri.MeteoPodaci;
import org.foi.nwtis.matsaboli2.ws.serveri.Uredjaj;

/**
 *
 * @author Matija Sabolić
 */
@Named(value = "odabirUredjaja")
@RequestScoped
public class OdabirUredjaja {

    Date vrijeme_od;
    Date vrijeme_do;
    String naziv;
    String adresa;
    List<Uredjaj> uredjaji;
    List<String> odabraniUredjaji;
    List<MeteoPodaci> meteoPodaci;

    /**
     * Creates a new instance of OdabirUredjaja
     */
    public OdabirUredjaja() throws ClassNotFoundException_Exception {
        uredjaji = MeteoWSKlijent.dajSveUredjaje();
    }
    
    /**
     * Metoda koja poziva Soap web servis metodu koja dodaje novi uređaj u bazu
     * podataka. Metodi se šalju naziv i adresa uređaja.
     *
     * @return
     * @throws ClassNotFoundException_Exception
     */
    public String upisiSoap() throws ClassNotFoundException_Exception {
        if (this.adresa != null && this.naziv != null && !this.naziv.isEmpty() && !this.adresa.isEmpty()) {
            System.out.println("upisiSoap");
            MeteoWSKlijent.dodajUredaj(getNaziv(), getAdresa());

            this.naziv = "";
            this.adresa = "";
            
            this.vrijeme_do = null;
            this.vrijeme_od = null;
            this.odabraniUredjaji = null;

            uredjaji = MeteoWSKlijent.dajSveUredjaje();
        }
        
        return "upisano_soap";
    }

    /**
     * Metoda koja poziva REST web servis metodu koja dodaje novi uređaj u bazu
     * podataka. Metodi se šalju naziv i adresa uređaja u json formatu.
     *
     * @return
     * @throws ClassNotFoundException_Exception
     */
    public String upisiRest() throws ClassNotFoundException_Exception {
        if (this.naziv != null && this.adresa != null && !this.naziv.isEmpty() && !this.adresa.isEmpty()) {
            System.out.println("upisiRest");
            RESTwsKlijent2 rest = new RESTwsKlijent2();

            JsonObjectBuilder job = Json.createObjectBuilder();
            job.add("Naziv", getNaziv());
            job.add("Adresa", getAdresa());

            rest.postJson(job.build().toString());

            this.naziv = "";
            this.adresa = "";
            
            this.vrijeme_do = null;
            this.vrijeme_od = null;
            this.odabraniUredjaji = null;

            uredjaji = MeteoWSKlijent.dajSveUredjaje();
        }

        return "upisano_rest";
    }

    /**
     * Metoda koja poziva Soap web servis metodu koja vraća sve meteorološke
     * podatke u određenom intervalu za odabrani uređaj.
     *
     * @return
     * @throws ClassNotFoundException_Exception
     */
    public String preuzmiSoap() throws ClassNotFoundException_Exception {
        if (odabraniUredjaji != null && odabraniUredjaji.size() == 1 && this.vrijeme_do != null && this.vrijeme_od != null && this.vrijeme_do.after(this.vrijeme_od)) {
            System.out.println("preuzmiSoap");
            meteoPodaci = MeteoWSKlijent.dajSveMeteoPodatkeZaUredjaj(Integer.parseInt(odabraniUredjaji.get(0)), (getVrijeme_od().getTime() / 1000), (getVrijeme_do().getTime() / 1000));
            
            this.adresa = "";
            this.naziv = "";
        }
        return "preuzeto_soap";
    }

    /**
     * Metoda koja poziva REST web servis metodu koja vraća važeće meteorološke
     * podatke za odabrani skup uređaja.
     *
     * @return
     */
    public String preuzmiRest() {
        if (odabraniUredjaji != null && odabraniUredjaji.size() > 1) {
            System.out.println("preuzmiRest() 2");
            List<MeteoPodaci> lista = new ArrayList<>();

            for (String id : odabraniUredjaji) {
                RESTwsKlijent rest = new RESTwsKlijent(id);
                String data = rest.getJson();

                JsonReader reader = Json.createReader(new StringReader(data));

                JsonObject jo = reader.readObject();
                float temp = Float.parseFloat(String.valueOf(jo.getJsonNumber("temp")));
                float tlak = Float.parseFloat(String.valueOf(jo.getJsonNumber("tlak")));
                float vlaga = Float.parseFloat(String.valueOf(jo.getJsonNumber("vlaga")));

                MeteoPodaci podaci = new MeteoPodaci();
                podaci.setTemperatureValue(temp);
                podaci.setHumidityValue(vlaga);
                podaci.setPressureValue(tlak);

                lista.add(podaci);
            }

            meteoPodaci = lista;
            
            this.naziv = "";
            this.adresa = "";
            
            this.vrijeme_do = null;
            this.vrijeme_od = null;
        }
        
        return "preuzeto_rest";
    }

    public List<MeteoPodaci> getMeteoPodaci() {
        return meteoPodaci;
    }

    public void setMeteoPodaci(List<MeteoPodaci> meteoPodaci) {
        this.meteoPodaci = meteoPodaci;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public List<String> getOdabraniUredjaji() {
        return odabraniUredjaji;
    }

    public void setOdabraniUredjaji(List<String> odabrani) {
        this.odabraniUredjaji = odabrani;
    }

    public List<Uredjaj> getUredjaji() {
        return uredjaji;
    }

    public void setUredjaji(List<Uredjaj> uredjaji) {
        this.uredjaji = uredjaji;
    }

    public Date getVrijeme_od() {
        return vrijeme_od;
    }

    public void setVrijeme_od(Date vrijeme_od) {
        this.vrijeme_od = vrijeme_od;
    }

    public Date getVrijeme_do() {
        return vrijeme_do;
    }

    public void setVrijeme_do(Date vrijeme_do) {
        this.vrijeme_do = vrijeme_do;
    }
}
