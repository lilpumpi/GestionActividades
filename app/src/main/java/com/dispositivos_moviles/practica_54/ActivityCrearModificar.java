package com.dispositivos_moviles.practica_54;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Date;

public class ActivityCrearModificar extends AppCompatActivity {

    private EditText et_nombre;
    private TextView tv_fecha;
    private Button bt_fecha, bt_aceptar, bt_cancelar;
    private Calendar calendar; //Almacena la fecha seleccionada

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_modificar);

        et_nombre = (EditText) findViewById(R.id.et_nombre);
        bt_fecha = (Button) findViewById(R.id.bt_fecha);
        tv_fecha = (TextView) findViewById(R.id.tv_fecha);
        bt_aceptar = (Button) findViewById(R.id.bt_aceptar);
        bt_cancelar = (Button) findViewById(R.id.bt_cancelar);

        Intent intent = getIntent();

        //Por defecto la fecha limite es la actual y el campo nombre es vacio
        String nombre="";
        calendar = Calendar.getInstance(); //Para almacenar la fecha limite

        //Si el intent tiene una posicion mayor que 0  es que el elemento ya existe, por lo que colocamos sus datos
        final int pos = intent.getExtras().getInt("pos");
        Log.d("POSITION", "La posicion seleccionada es: " + pos);

        if(pos >= 0){
            nombre = intent.getStringExtra("nombre");
            Date fechaLim = (Date) intent.getSerializableExtra("fecha");
            calendar.setTime(fechaLim);
        }

        //Escribimos los datos
        escribirDatos(nombre, calendar);


        //Activamos la seleccion de la fecha
        bt_fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarFecha(calendar);
            }
        });

        //Activamos guardar
        bt_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!et_nombre.getText().toString().isEmpty()){ //Si el campo nombre no esta vacio
                    guardarActividad(pos);
                } else{
                    Toast.makeText(getApplicationContext(), "Debes introducir un nombre", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Activamos cancelar
        bt_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

    }


    //Metodo para seleccionar fecha
    private void seleccionarFecha(Calendar calendar){
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        //Creamos un DatePickerDialog para seleccionar la fecha
        DatePickerDialog elegirFecha = new DatePickerDialog(ActivityCrearModificar.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                //Guardamos en el objeto calendar la fecha escogida
                calendar.set(year, month, dayOfMonth);

                String fecha = dayOfMonth + "/" + (month+1) + "/" + year;
                tv_fecha.setText(fecha);

            }
        }, currentYear, currentMonth, currentDay);

        elegirFecha.show();
    }

    //Este metodo simplemente escribre la fecha
    public void escribirDatos(String nombre, Calendar calendar){
        //Si no se esta modificando ningun elemento, el calendar tendra por defecto la fecha actual
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String fecha = day + "/" + (month+1) + "/" + year;
        tv_fecha.setText(fecha);
        et_nombre.setText(nombre);
    }


    //Metodo para guardar la Actividad
    public void guardarActividad(int pos){
        Intent resultIntent = new Intent();
        resultIntent.putExtra("nombre", et_nombre.getText().toString());
        resultIntent.putExtra("fecha", calendar.getTime());
        resultIntent.putExtra("pos", pos);

        setResult(RESULT_OK, resultIntent);
        finish();
    }

}
