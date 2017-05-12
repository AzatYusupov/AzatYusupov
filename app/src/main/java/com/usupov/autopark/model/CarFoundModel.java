package com.usupov.autopark.model;

/**
 * Created by Azat on 01.02.2017.
 */

public class CarFoundModel {
    String carName;
    String vinNumber;
    String engine;
    String year;
    String carcase;
    String drive_unit;
    String kpp;
    public String getCarName(){return  carName;}
    public String getVinNumber(){return  vinNumber;}
    public String getEngine(){return  engine;}
    public String getYear(){return  year;}
    public String getCarcase(){return  carcase;}
    public String getDrive_unit(){return  drive_unit;}
    public String getKpp(){return  kpp;}
    public CarFoundModel(String carName, String vinNumber, String engine, String year, String  carcase, String drive_unit, String kpp) {
        this.carName = carName;
        this.vinNumber = vinNumber;
        this.engine = engine;
        this.year = year;
        this.carcase = carcase;
        this.drive_unit = drive_unit;
        this.kpp = kpp;
    }
}
