package com.example.pm2e10087;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {

    //Declaracion de variables
    Spinner spPaises;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Agregando valores al Spinner
        spPaises = findViewById(R.id.spPaises);
        String[] opciones = {"Honduras (504)", "Guatemala (502)","Costa Rica","Nicaragua","El Salvador"};
        ArrayAdapter<String> adpSpinner = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, opciones);
        spPaises.setAdapter(adpSpinner);


    }
}