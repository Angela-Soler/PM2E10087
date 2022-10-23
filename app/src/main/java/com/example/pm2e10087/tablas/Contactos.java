package com.example.pm2e10087.tablas;

public class Contactos {
    public Integer id;
    public String nombre;
    public Integer telefono;
    public String pais;
    public String nota;
    public String image;

    //Constructor de la clase
    public Contactos(){
        //Todo
    }

    public Contactos(Integer id, String nombre, String pais, Integer telefono, String nota, String image) {
        this.id = id;
        this.nombre = nombre;
        this.pais = pais;
        this.telefono = telefono;
        this.nota = nota;
        this.image = image;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


    public Integer gettelefono() {
        return telefono;
    }

    public void settelefono(Integer telefono) {
        this.telefono = telefono;
    }

    public String getpais() {
        return pais;
    }

    public void setpais(String pais) {
        this.pais = pais;
    }

    public String getnota() {
        return nota;
    }

    public void setnota(String nota) {
        this.nota = nota;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
