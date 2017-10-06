/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.ws.klijenti;

import org.foi.nwtis.matsaboli2.ws.serveri.ClassNotFoundException_Exception;
import org.foi.nwtis.matsaboli2.ws.serveri.MeteoPodaci;

/**
 *
 * @author Matija Sabolić
 */
public class MeteoWSKlijent {

    public static java.util.List<org.foi.nwtis.matsaboli2.ws.serveri.Uredjaj> dajSveUredjaje() throws ClassNotFoundException_Exception {
        org.foi.nwtis.matsaboli2.ws.serveri.GeoMeteoWS_Service service = new org.foi.nwtis.matsaboli2.ws.serveri.GeoMeteoWS_Service();
        org.foi.nwtis.matsaboli2.ws.serveri.GeoMeteoWS port = service.getGeoMeteoWSPort();
        return port.dajSveUredjaje();
    }

    public static java.util.List<org.foi.nwtis.matsaboli2.ws.serveri.MeteoPodaci> dajSveMeteoPodatkeZaUredjaj(int id, long od, long _do) throws ClassNotFoundException_Exception {
        org.foi.nwtis.matsaboli2.ws.serveri.GeoMeteoWS_Service service = new org.foi.nwtis.matsaboli2.ws.serveri.GeoMeteoWS_Service();
        org.foi.nwtis.matsaboli2.ws.serveri.GeoMeteoWS port = service.getGeoMeteoWSPort();
        return port.dajSveMeteoPodatkeZaUredjaj(id, od, _do);
    }

    public static MeteoPodaci dajZadnjeMeteoPodatkeZaUredjaj(int id) throws ClassNotFoundException_Exception {
        org.foi.nwtis.matsaboli2.ws.serveri.GeoMeteoWS_Service service = new org.foi.nwtis.matsaboli2.ws.serveri.GeoMeteoWS_Service();
        org.foi.nwtis.matsaboli2.ws.serveri.GeoMeteoWS port = service.getGeoMeteoWSPort();
        return port.dajZadnjeMeteoPodatkeZaUredjaj(id);
    }

    public static Boolean dodajUredjaj(org.foi.nwtis.matsaboli2.ws.serveri.Uredjaj uredjaj) throws ClassNotFoundException_Exception {
        org.foi.nwtis.matsaboli2.ws.serveri.GeoMeteoWS_Service service = new org.foi.nwtis.matsaboli2.ws.serveri.GeoMeteoWS_Service();
        org.foi.nwtis.matsaboli2.ws.serveri.GeoMeteoWS port = service.getGeoMeteoWSPort();
        return port.dodajUredjaj(uredjaj);
    }

    public static Boolean dodajUredaj(java.lang.String naziv, java.lang.String adresa) throws ClassNotFoundException_Exception {
        org.foi.nwtis.matsaboli2.ws.serveri.GeoMeteoWS_Service service = new org.foi.nwtis.matsaboli2.ws.serveri.GeoMeteoWS_Service();
        org.foi.nwtis.matsaboli2.ws.serveri.GeoMeteoWS port = service.getGeoMeteoWSPort();
        return port.dodajUredaj(naziv, adresa);
    }
    
    
    
}
