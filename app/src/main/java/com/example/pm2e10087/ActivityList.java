package com.example.pm2e10087;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.pm2e10087.configuracion.SQLiteConexion;
import com.example.pm2e10087.tablas.Contactos;
import com.example.pm2e10087.tablas.Transacciones;

import java.util.ArrayList;

public class ActivityList extends AppCompatActivity {

    //Declaración de Variables
    SQLiteConexion conexion;
    ListView listContactos;
    ArrayList<Contactos> lista;
    ArrayList<String> listaConcatenada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        conexion = new SQLiteConexion(this, Transacciones.NameDatabase, null, 1);
        listContactos = (ListView) findViewById(R.id.listContactos);

        GetListContactos();

        ArrayAdapter adp = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listaConcatenada);
        listContactos.setAdapter(adp);

        listContactos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(ActivityList.this, listaConcatenada.get(i), Toast.LENGTH_SHORT ).show();
                Log.i("Seleccion id", listaConcatenada.get(i));
            }
        });
    }

    private void GetListContactos() {
        SQLiteDatabase db = conexion.getReadableDatabase(); //Base de Datos en modo lectura
        Contactos listContactos;

        lista = new ArrayList<>(); //lista de objetos del tipo Personas
        Cursor cursor = db.rawQuery(Transacciones.GetContactos,null);

        while (cursor.moveToNext()){
            listContactos = new Contactos();
            listContactos.setId(cursor.getInt(0));
            listContactos.setNombre(cursor.getString(1));
            listContactos.settelefono(cursor.getInt(2));
            listContactos.setpais(cursor.getString(3));
            listContactos.setnota(cursor.getString(4));
            listContactos.setImage(cursor.getString(5));

            lista.add(listContactos);
        }
        cursor.close();

        llenarLista();
    }

    private void llenarLista() {
        listaConcatenada = new ArrayList<>();

        for (int i=0; i < lista.size(); i++){
            listaConcatenada.add("Nombre: "+lista.get(i).getNombre()+" \nTel: "+
                    lista.get(i).gettelefono()+" \nPais: "+
                    lista.get(i).getpais()+" \nNota: "+
                    lista.get(i).getnota());
        }
    }
}