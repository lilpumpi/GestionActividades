package com.dispositivos_moviles.practica_54;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    final static int COD_CREAR_ACTIVIDAD = 100;
    final static int COD_MODIFICAR_ACTIVIDAD = 102;
    private static final String FILENAME = "listaActividades.txt";

    private ListView lv_lista;
    private Button bt_add;
    private ArrayList<Actividad> actividades;
    private ArrayAdapter<Actividad> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv_lista = (ListView) findViewById(R.id.lv_lista);
        bt_add = (Button) findViewById(R.id.bt_add);


        //Si el archivo existe recuperamos los datos, sino se crea desde 0
        if(existeArchivo(FILENAME)){
            actividades = cargarDatos();
        } else{
            actividades = new ArrayList<Actividad>();
        }

        //Cargamos los elementos de la lista
        adapter = new ArrayAdapter<Actividad>(this, android.R.layout.simple_list_item_1, actividades);
        lv_lista.setAdapter(adapter);

        lv_lista.setLongClickable(true);
        registerForContextMenu(lv_lista);

        //Habilitamos el boton para añadir nueva Actividad
        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lanzarCrearActivity();
            }
        });
    }


    //------------------------------------------------------------------------------------------
    //GESTION DE MENUS
    //------------------------------------------------------------------------------------------

    //Creamos un menu contextual
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo cmi){
        if(view.getId() == R.id.lv_lista){
            //Inflamos el menu
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
        }
    }

    //Gestionamos las opciones dle menu contextual
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Actividad actividad = actividades.get(info.position);

        /* NO DETECTA LOS ID DEL MENU DENTRO DEL SWITCH
        switch (item.getItemId()){
            case R.id.context_editar:
                lanzarModificarActivity(actividad);
                break;

            case R.id.context_eliminar:
                borrarActividad(actividad);
                break;
        }
        */

        if(R.id.context_editar == item.getItemId()){
            lanzarModificarActivity(actividad);
        } else if(R.id.context_eliminar == item.getItemId()){
            borrarActividad(actividad);
        }

        return true;
    }


    //Creamos un menu principal
    public boolean onCreateOptionsMenu(Menu menu){
        Log.d("MainActivity", "onCreateOptionsMenu llamado ----------------------------------------------------");
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }


    //Gestionamos las opciones del menu principial
    public boolean onOptionsItemSelected(MenuItem item){
        Log.d("MainActivity", "Menú borrar seleccionado -------------------------------------------------------");
        if(R.id.menu_borrar == item.getItemId()){
            actividades.removeAll(actividades);
            adapter.notifyDataSetChanged();
            updateStatus();
        }

        return true;
    }


    //------------------------------------------------------------------------------------------
    //LANZAMIENTO DE ACTIVITIES
    //------------------------------------------------------------------------------------------


    //Metodo parar lanzar la actividad para crear
    private void lanzarCrearActivity(){
        Intent crear = new Intent(this, ActivityCrearModificar.class);
        crear.putExtra("pos", -1);
        startActivityForResult(crear, COD_CREAR_ACTIVIDAD);
    }


    //Metodo para lanzar la actividad para modificar
    private void lanzarModificarActivity(Actividad actividad){
        Intent modificar = new Intent(this, ActivityCrearModificar.class);
        modificar.putExtra("nombre", actividad.getNombre());
        modificar.putExtra("fecha", actividad.getFechaLim());
        modificar.putExtra("pos", actividades.indexOf(actividad));

        startActivityForResult(modificar, COD_MODIFICAR_ACTIVIDAD);
    }


    //Devuelve los resultados de las Actividades lanzadas
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Crear nueva actividad
        if(requestCode == COD_CREAR_ACTIVIDAD && resultCode == RESULT_OK){
            String nombre = data.getStringExtra("nombre");
            Date fechaLim = (Date) data.getSerializableExtra("fecha");
            crearActividad(nombre, fechaLim);
        }

        //Modificar una actividad
        if(requestCode == COD_MODIFICAR_ACTIVIDAD && resultCode == RESULT_OK){
            String nombre = data.getStringExtra("nombre");
            Date fechaLim = (Date) data.getSerializableExtra("fecha");
            int pos = data.getExtras().getInt("pos");

            modificarActividad(pos, nombre, fechaLim);
        }
    }


    //------------------------------------------------------------------------------------------
    //CREACION Y MODIFICACION DE ACTIVIDADES
    //------------------------------------------------------------------------------------------


    //Metodo para añadir una nueva actividad a la lista
    public void crearActividad(String nombre, Date fechaLim){
        Actividad nuevaActividad = new Actividad(nombre);
        nuevaActividad.setFechaLim(fechaLim);

        //Añadimos a la lista y actualizamos el ListView
        actividades.add(nuevaActividad);
        adapter.notifyDataSetChanged();
        updateStatus();
        guardarDatos();
    }


    //Metodo para modificar una nueva actividad a la lista
    public void modificarActividad(int position, String nombre, Date fechaLim){
        Actividad actividad = actividades.get(position);
        actividad.setNombre(nombre);
        actividad.setFechaLim(fechaLim);

        //Actualizamos el ListView
        adapter.notifyDataSetChanged();
        guardarDatos();
    }


    //Metodo para actualizar el valor de la cantidad de elementos
    private void updateStatus(){
        TextView tvCantidad = (TextView) findViewById(R.id.tv_cantidad);

        int cantidad = adapter.getCount();
        tvCantidad.setText(Integer.toString(cantidad));
    }


    //Metodo para borrar una actividad
    private void borrarActividad(Actividad actividad){
        actividades.remove(actividad);
        adapter.notifyDataSetChanged();
        updateStatus();
        guardarDatos();
    }


    //------------------------------------------------------------------------------------------
    //COMPROBAMOS LAS FECHAS
    //------------------------------------------------------------------------------------------


    //Comprobamos si la fecha ha caducado cuando la atividad pasa a segundo plano
    @Override
    protected void onResume() {
        super.onResume();

        //Obtenemos la fecha actual
        Date fechaActual = new Date();

        //Recorremos todas las actividades para comprobar sus fechas
        for(Actividad actividad: actividades){
            Date fechaLim = actividad.getFechaLim();

            if(fechaActual.after(fechaLim)){
                mostrarAdvertencia(actividad);
            }
        }
    }

    //Mostrara un dialogo de advertencia para la actividad que haya caducado
    public void mostrarAdvertencia(Actividad actividad){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ADVERTENCIA!");
        builder.setMessage("La actividad " + actividad.getNombre() + " ha caducado!");

        builder.setNegativeButton("Cerrar", null);
        builder.create().show();
    }


    //------------------------------------------------------------------------------------------
    //GESTION DE GUARDADO DE DATOS
    //------------------------------------------------------------------------------------------

    //Este metodo se encarga de guardar la lista de actividades en un archivo FILENAME
    private void guardarDatos(){
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(openFileOutput(FILENAME, Context.MODE_PRIVATE));
            objectOutputStream.writeObject(actividades);
            Toast.makeText(this, "Guardado correctamete", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
        }
    }


    //Este metodo se encarga de ccargar en un array, los datos guardados en el archivo FILENAME para ser recuperados
    private ArrayList<Actividad> cargarDatos(){
        ArrayList<Actividad> toret = new ArrayList<Actividad>();

        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(openFileInput(FILENAME));
            toret = (ArrayList<Actividad>) objectInputStream.readObject();
            Toast.makeText(this, "Datos recuperados", Toast.LENGTH_SHORT).show();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            Log.e("GUARDADO", "ERROR AL GUARDAR LOS DATOS");
            Toast.makeText(this, "Error al cargar los datos", Toast.LENGTH_SHORT).show();
        }

        return toret;
    }

    //Metodo para comprobar si existe un archivo, si existe devuelve true
    private boolean existeArchivo(String filename){
        File file = getBaseContext().getFileStreamPath(filename);
        return file.exists();
    }



}