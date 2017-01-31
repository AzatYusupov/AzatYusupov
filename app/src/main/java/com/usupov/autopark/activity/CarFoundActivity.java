package com.usupov.autopark.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.usupov.autopark.R;

/**
 * Created by Azat on 31.01.2017.
 */

public class CarFoundActivity extends AppCompatActivity{
    static class Car {
        String carName;
        String vinNumber;
        String engine;
        String issue_year;
        String carcase;
        String drive_unit;
        String kpp;
        public Car(String carName, String vinNumber, String engine, String issue_year, String  carcase, String drive_unit, String kpp) {
            this.carName = carName;
            this.vinNumber = vinNumber;
            this.engine = engine;
            this.issue_year = issue_year;
            this.carcase = carcase;
            this.drive_unit = drive_unit;
            this.kpp = kpp;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_found);

        setCarInforms(new Car("Audi A5 Рестайлинг 2.0 МТ, 211 л.с 4WD", "1234567891011121", "2015", "2.0/211 л.с./ Бензин", "Купе", "Полный", "Механическая"));

        initToolbar();

    }

    private void setCarInforms(Car car) {
        TextView carName = (TextView) findViewById(R.id.car_name);
        carName.setText(car.carName);

        TextView vinNumber = (TextView) findViewById(R.id.vin_number);
        vinNumber.setText(car.vinNumber);

        TextView engine = (TextView) findViewById(R.id.engine);
        engine.setText(car.engine);

        TextView issueYear = (TextView) findViewById(R.id.issue_year);
        issueYear.setText(car.issue_year);

        TextView carcase = (TextView) findViewById(R.id.carcase);
        carcase.setText(car.carcase);

        TextView driveUnit = (TextView) findViewById(R.id.drive_unit);
        driveUnit.setText(car.drive_unit);

        TextView KPP = (TextView) findViewById(R.id.kpp);
        KPP.setText(car.kpp);

    }


    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_car_found);
        setSupportActionBar(toolbar);
    }
}
