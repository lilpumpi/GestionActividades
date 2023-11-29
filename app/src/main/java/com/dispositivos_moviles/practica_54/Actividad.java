package com.dispositivos_moviles.practica_54;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class Actividad implements Serializable {

    private String nombre;
    private Date fechaLim;

    public Actividad(String nombre){
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Date getFechaLim() {
        return fechaLim;
    }

    public void setFechaLim(Date fechaLim) {
        this.fechaLim = fechaLim;
    }

    @NonNull
    @Override
    public String toString() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fechaLim);
        String fecha = calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH)+1) + "/" + calendar.get(Calendar.YEAR);
        return this.getNombre() + "\t (" + fecha + ")";
    }
}
