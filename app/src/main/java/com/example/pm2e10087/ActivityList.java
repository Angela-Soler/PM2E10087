package com.example.pm2e10087;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.pm2e10087.configuracion.SQLiteConexion;
import com.example.pm2e10087.tablas.Contactos;
import com.example.pm2e10087.tablas.Transacciones;

import java.util.ArrayList;
import java.util.List;

public class ActivityList extends AppCompatActivity {

    //Declaración de Variables
    SQLiteConexion conexion;
    ListView listContactos;
    ArrayList<Contactos> lista;
    List<Contactos> lis;
    ArrayList<String> listaConcatenada;

    Button btnEliminar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        conexion = new SQLiteConexion(this, Transacciones.NameDatabase, null, 1);
        listContactos = (ListView) findViewById(R.id.listContactos);
        btnEliminar = findViewById(R.id.btnEliminar);

        GetListContactos();

        ArrayAdapter adp = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listaConcatenada);
        listContactos.setAdapter(adp);

        listContactos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(ActivityList.this, listaConcatenada.get(i), Toast.LENGTH_SHORT ).show();
                Log.i("Seleccion id", listaConcatenada.get(i));


                btnEliminar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String id = lista.get(i).getId().toString();
                        mostrarAlerta("¿Desea eliminar el numero de contacto "+lista.get(i).gettelefono(),id);
                        Log.i("Eliminar id", ""+lista.get(i).getId().toString());
                    }
                });
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

    private void mostrarAlerta(String mensaje, String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityList.this);
        builder.setMessage(mensaje)
                .setTitle("Confirmación")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        eliminarRegistro(id);
                    }
                }
                ).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        }
                );

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void eliminarRegistro(String id) {

        SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.NameDatabase, null, 1);
        SQLiteDatabase db = conexion.getWritableDatabase();
        ContentValues values = new ContentValues();

        try {
            db.delete(Transacciones.TablaContactos, "id="+id, null);
            Toast.makeText(getApplicationContext(), "Contacto Eliminado. ", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error al eliminar. "+e.toString(), Toast.LENGTH_SHORT).show();
            Log.i("Error al eliminar",e.toString());
        }

        db.close();

        //Volver a Activity principal
        Intent intent = new Intent(ActivityList.this, MainActivity.class);
        startActivity(intent);
    }
}