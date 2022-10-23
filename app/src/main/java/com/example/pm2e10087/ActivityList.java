package com.example.pm2e10087;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
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
    String id = "";
    Button btnEliminar, btnActualizar, btnCompartir, btnLlamar;
    static final int peticion_acceso_call = 101;

    public static Activity actList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //Para cerrar actividad desde otra activity
        actList=this;

        conexion = new SQLiteConexion(this, Transacciones.NameDatabase, null, 1);
        listContactos = (ListView) findViewById(R.id.listContactos);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnActualizar = findViewById(R.id.btnActualizar);
        btnCompartir = findViewById(R.id.btnCompartir);
        btnLlamar = findViewById(R.id.btnLlamar);

        //llenar lista
        GetListContactos();

        ArrayAdapter adp = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listaConcatenada);
        listContactos.setAdapter(adp);

        listContactos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(ActivityList.this, listaConcatenada.get(i), Toast.LENGTH_SHORT ).show();
                Log.i("Seleccion id", listaConcatenada.get(i));
                id = lista.get(i).getId().toString();
                //Boton Eliminar Registro
                btnEliminar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        id = lista.get(i).getId().toString();
                        mostrarAlerta("¿Desea eliminar el numero de contacto "+lista.get(i).gettelefono(),id,"D");
                        Log.i("Eliminar id", ""+lista.get(i).getId().toString());
                    }
                });

                //Boton Actualizar Registro
                btnActualizar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        id = lista.get(i).getId().toString();
                        mostrarAlerta("¿Desea actualizar el numero de contacto "+lista.get(i).gettelefono(),id, "A");
                        Log.i("Actualizar id", ""+lista.get(i).getId().toString());


                    }
                });

                //Boton Compartir
                btnCompartir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        compartir(lista.get(i).getNombre().toString(), lista.get(i).gettelefono().toString());
                    }
                });

                //Boton Llamar
                btnLlamar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Validar si el permiso está otorgado para llamar
                        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                            //Otorgar el permiso si no se tiene
                            ActivityCompat.requestPermissions(ActivityList.this, new String[]{Manifest.permission.CALL_PHONE}, peticion_acceso_call);
                        }
                        else{
                            String telefono = lista.get(i).gettelefono().toString();
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:"+telefono));
                            startActivity(callIntent);
                        }


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

    private void mostrarAlerta(String mensaje, String id, String tipo) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityList.this);
        builder.setMessage(mensaje)
                .setTitle("Confirmación")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                      if (tipo.equalsIgnoreCase("D")){ //Delete
                          eliminarRegistro(id);
                      }
                        if (tipo.equalsIgnoreCase("A")){ //Actualizar
                            actualizarRegistro(id);
                        }
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

    private void actualizarRegistro(String id) {
        /*try {
            onBackPressed();
        } catch (Throwable e) {
            e.printStackTrace();
        }*/
        Intent intent = new Intent(ActivityList.this, MainActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);

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
        try {
            onBackPressed();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        //Volver a Activity principal
        Intent intent = new Intent(ActivityList.this, MainActivity.class);
        startActivity(intent);
    }

    public void verificaSeleccion(View view){

        if (id.toString().equalsIgnoreCase("")){
            mostrarAlertaOK("Debe seleccionar un registro y luego presionar el boton");
        }
    }

    private void mostrarAlertaOK(String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityList.this);
        builder.setMessage(mensaje)
                .setTitle("Error")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void compartir(String nombre, String telefono){
        if (id != null){
            Intent intentShare;
            intentShare = new Intent(Intent.ACTION_SEND);
            intentShare.setType("text/pain");
            intentShare.putExtra(android.content.Intent.EXTRA_SUBJECT,"Contacto");
            intentShare.putExtra(android.content.Intent.EXTRA_TEXT,"Nombre: "+nombre+"\nTelefono: "+telefono);
            startActivity(Intent.createChooser(intentShare,"Compartir con"));
        }

    }

    public void finalizarActivity(){
        actList.finish();
    }
}