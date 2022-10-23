package com.example.pm2e10087;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.pm2e10087.configuracion.SQLiteConexion;
import com.example.pm2e10087.tablas.Contactos;
import com.example.pm2e10087.tablas.Transacciones;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    //Declaracion de variables
    Spinner spPaises;
    EditText txtNombre, txtTelefono, txtNota;
    Button btnSalvar, btnContactos;
    SQLiteConexion conexion;

    //Declaracion de variables para la foto
    static final int peticion_captura_imagen = 100;
    static final int peticion_acceso_camara = 201;
    ImageView objetoImagen;
    Button btntakepotho;
    String pathImage = "";
    Uri fotoUri;
    Button btnFoto;
    String id = "";
    Boolean extras = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtNombre = findViewById(R.id.txtNombre);
        txtTelefono = findViewById(R.id.txtTelefono);
        txtNota = findViewById(R.id.txtNota);
        btnSalvar = findViewById(R.id.btnSalvar);
        btnContactos = findViewById(R.id.btnContactos);

        //Agregando valores al Spinner
        spPaises = findViewById(R.id.spPaises);
        String[] opciones = {"Honduras", "Guatemala","Costa Rica","Nicaragua","El Salvador"};
        ArrayAdapter<String> adpSpinner = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, opciones);
        spPaises.setAdapter(adpSpinner);

        //Verificando si enviaron datos
        Bundle datos = getIntent().getExtras();
        if (datos == null) {
            extras = false;
            Log.i("Extras: ", "No hay datos a actualizar");

        } else {
            extras = true;
            Log.i("Extras: ", "Sí hay datos a actualizar");

            id = ""+datos.getString("id");
            Log.i("Extras: ", "ID "+id);
            if (id!=null){
                btnSalvar.setText("Actualizar Contacto");
                llenarDatos(id); //Llena la pantalla si trae el id a actualizar
            }

        }



        //Foto
        objetoImagen = findViewById(R.id.imgFoto);
        btnFoto = findViewById(R.id.btnFoto);

        btnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permisos();
            }
        });

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validaciones()==true){
                    if (extras == false){
                        guardarRegistro();
                    }else{
                        actualizarRegistro(id);
                    }
                }

            }
        });

        btnContactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ActivityList.class);
                startActivity(intent);

            }
        });

    }

    private void llenarDatos(String id) {
        SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.NameDatabase, null, 1);
        Cursor cursor = null;
        SQLiteDatabase db = conexion.getReadableDatabase(); //Base de Datos en modo lectura
        Contactos listContactos;
        String [] args = {id};
        Log.i("args",args[0]);

        try{
             cursor = db.rawQuery( Transacciones.GetContactos+" Where id = ?",args);
            while (cursor.moveToNext()){
                txtNombre.setText(cursor.getString(1).replace(" ",""));
                txtTelefono.setText(cursor.getString(2).replace(" ",""));
                txtNota.setText(cursor.getString(4).replace(" ",""));
                String pais = cursor.getString(3).replace(" ","");
                String uri = cursor.getString(5).replace(": ","");
                Log.i("pais: ", pais);
                if (pais.equalsIgnoreCase("Honduras")){
                    spPaises.setSelection(0);
                }if (pais.equalsIgnoreCase("Guatemala")){
                    spPaises.setSelection(1);
                }if (pais.equalsIgnoreCase("CostaRica")){
                    spPaises.setSelection(2);
                }if (pais.equalsIgnoreCase("Nicaragua")){
                    spPaises.setSelection(3);
                }if (pais.equalsIgnoreCase("ElSalvador")){
                    spPaises.setSelection(4);
                }
                //File foto = new File(uri);
                //objetoImagen.setImageURI(Uri.fromFile(foto));
            }
            cursor.close();
        }catch (Exception e){
            Log.i("Error al hacer Select ", e.getMessage());
        }
    }

    private void permisos() {
        //Validar si el permiso está otorgado para tomar foto
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            //Otorgar el permiso si no se tiene
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, peticion_acceso_camara);
        }
        else{
            //tomarFoto();
            TakePhotoDir();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*if (requestCode == peticion_captura_imagen)
        {
            Bundle extras = data.getExtras();
            Bitmap imagen = (Bitmap) extras.get("data");
            objetoImagen.setImageBitmap(imagen);

        }*/
        if (requestCode == peticion_captura_imagen && resultCode == RESULT_OK)
        {
            File foto = new File(pathImage);
            objetoImagen.setImageURI(Uri.fromFile(foto));

        }

    }

    private void TakePhotoDir()
    {
        Intent Intenttakephoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(Intenttakephoto.resolveActivity(getPackageManager())!= null)
        {
            File foto  = null;

            try
            {
                foto = createImageFile();
            }catch (Exception ex)
            {
                ex.toString();
            }
            if(foto != null)
            {
                fotoUri = FileProvider.getUriForFile(this, "com.example.pm2e10087.fileprovider",foto);
                Intenttakephoto.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);
                startActivityForResult(Intenttakephoto, peticion_captura_imagen);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */);
        // Save a file: path for use with ACTION_VIEW intents
        pathImage = image.getAbsolutePath();
        Log.i("FOTO", pathImage.toString());

        return image;
    }

    private boolean validaciones() {
        Boolean validacion = true;
        if (txtNombre.length() <= 0) {
            mostrarAlerta("Debe escribir un nombre");
            validacion = false;
        }else if (txtTelefono.length() <= 0) {
            mostrarAlerta("Debe escribir un telefono");
            validacion = false;
        }else if (txtTelefono.length() > 8) {
            mostrarAlerta("Numero de telefono incorrecto");
            validacion = false;
        }else if (txtNota.length() <= 0) {
            mostrarAlerta("Debe escribir una nota");
            validacion = false;
        }else if (pathImage.toString().length() <= 0) {
            mostrarAlerta("Debe agregar una foto");
            validacion = false;
        }
        return validacion;
    }

    private void mostrarAlerta(String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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

    private void guardarRegistro() {
        SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.NameDatabase, null, 1);
        SQLiteDatabase db = conexion.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Transacciones.nombre,txtNombre.getText().toString());
        values.put(Transacciones.telefono,txtTelefono.getText().toString());

        //Verificamos el pais seleccionado en el Spinner
        String seleccion = spPaises.getSelectedItem().toString();
        Log.i("Seleccion",seleccion);
        values.put(Transacciones.pais,seleccion);
        values.put(Transacciones.nota,txtNota.getText().toString());
        values.put(Transacciones.image,pathImage);

        try {
            Long resultado = db.insert(Transacciones.TablaContactos, Transacciones.id, values);
            Toast.makeText(getApplicationContext(), "Contacto Ingresado Correctamente. "+resultado.toString(), Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error al insertar. "+e.toString(), Toast.LENGTH_SHORT).show();
        }

        db.close();
        ClearScreem();

    }

    private void ClearScreem() {
        txtNombre.setText("");
        txtTelefono.setText("");
        txtNota.setText("");
        pathImage="";
        objetoImagen.setImageResource(R.drawable.contacto);
        spPaises.setSelection(0);
        txtNombre.requestFocus();
        btnSalvar.setText(R.string.btn_guardar);
    }

    private void actualizarRegistro(String id) {
        SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.NameDatabase, null, 1);
        SQLiteDatabase db = conexion.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Transacciones.nombre,txtNombre.getText().toString());
        values.put(Transacciones.telefono,txtTelefono.getText().toString());
        String [] args = {id};

        //Verificamos el pais seleccionado en el Spinner
        String seleccion = spPaises.getSelectedItem().toString();

        Log.i("Seleccion",seleccion);
        values.put(Transacciones.pais,seleccion);
        values.put(Transacciones.nota,txtNota.getText().toString());
        values.put(Transacciones.image,pathImage);

        try {
            String strSQL = "UPDATE "+Transacciones.TablaContactos+" " +
                                "SET nombre = '"+txtNombre.getText()+"'," +
                                    " telefono = '"+txtTelefono.getText() +"',"+
                                    " nota = '"+txtNota.getText()+"',"+
                                    " pais = '"+seleccion+"',"+
                                    " image = '"+pathImage+"' "+
                    " WHERE id = "+ id;

            Log.i("SQL: ",strSQL);
            db.execSQL(strSQL);
            //db.update(Transacciones.TablaContactos,args);
            Toast.makeText(getApplicationContext(), "Contacto Actualizado Correctamente. ", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error al actualizar contacto. "+e.toString(), Toast.LENGTH_SHORT).show();
        }

        db.close();

        ClearScreem();
    }
}