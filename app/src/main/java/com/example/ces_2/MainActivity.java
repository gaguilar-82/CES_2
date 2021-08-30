package com.example.ces_2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private Spinner spMunicipio, spEscolaridad, spEstadoCivil, spSeguridadSocial, spUso, spMaterial, spMuros, spTecho, spPiso;
    private CheckBox agua_chk, drenaje_chk, electricidad_chk, ninguno_chk, sala_chk, cocina_chk, bano_chk;
    private EditText fecha, fechaNac, localidad, colonia, manzana, lote, aPaterno, aMaterno, nombre, curp, lugarNacimiento, ocupacion, trabajo,  antiguedad, familia, ingresoTotal, alimentacion, salud, educacion, otros, recamaras, observaciones;
    private LocationManager ubicacion;
    private TextView coordenadas, gastoTotal;
    private int mDate, mMonth, mYear;
    private String agua, drenaje, electricidad, ninguno, sala, cocina, bano;
    private Button generarPDF;
    private Bitmap bmp, scaledbmp;
    private OutputStream doc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Colocar Ícono
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        //Recuperar información del activity
        localidad = findViewById(R.id.txt_Localidad);
        colonia = findViewById(R.id.txt_Colonia);
        manzana = findViewById(R.id.txt_Manzana);
        lote = findViewById(R.id.txt_Lote);
        nombre = findViewById(R.id.txt_Nombre);
        aPaterno = findViewById(R.id.txt_ApellidoPaterno);
        aMaterno = findViewById(R.id.txt_ApellidoMaterno);
        curp = findViewById(R.id.txt_CURP);
        lugarNacimiento = findViewById(R.id.txt_LugarNacimiento);
        ocupacion = findViewById(R.id.txt_Ocupacion);
        trabajo = findViewById(R.id.txt_Empresa);
        antiguedad = findViewById(R.id.txt_Antiguedad);
        familia = findViewById(R.id.txt_Familia);
        ingresoTotal = findViewById(R.id.txt_Ingreso);
        alimentacion = findViewById(R.id.txt_GastoAlimentacion);
        salud = findViewById(R.id.txt_GastoSalud);
        educacion = findViewById(R.id.txt_GastoEducacion);
        otros = findViewById(R.id.txt_GastoOtros);
        gastoTotal = findViewById(R.id.tv_Gasto);
        recamaras = findViewById(R.id.txt_Recamaras);
        observaciones = findViewById(R.id.txt_Observaciones);
        bmp = BitmapFactory.decodeResource(getResources(),R.drawable.logo);
        scaledbmp = Bitmap.createScaledBitmap(bmp, 2551, 300, false);
        generarPDF = (Button) findViewById(R.id.btn_PDF);
        generarPDF.setEnabled(false);

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION},PackageManager.PERMISSION_GRANTED);



        //Fecha Actual
        fecha = findViewById(R.id.td_Fecha);
        fecha.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                final Calendar Cal = Calendar.getInstance();
                mDate = Cal.get(Calendar.DATE);
                mMonth = Cal.get(Calendar.MONTH);
                mYear = Cal.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, android.R.style.Theme_DeviceDefault_Dialog, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        fecha.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                    }
                }, mYear, mMonth, mDate);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()-1000);
                datePickerDialog.show();
            }
        });

        //Fecha de Nacimiento
        fechaNac = findViewById(R.id.td_FechaNacimiento);
        fechaNac.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                final Calendar Cal = Calendar.getInstance();
                mDate = Cal.get(Calendar.DATE);
                mMonth = Cal.get(Calendar.MONTH);
                mYear = Cal.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, android.R.style.Theme_Holo_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        fechaNac.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                    }
                }, mYear, mMonth, mDate);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()-1000);
                datePickerDialog.show();
            }
        });

        //Spinner Municipio
        spMunicipio = (Spinner)findViewById(R.id.sp_Municipio);
        String [] opcionesMunicipio = {"--","Acapulco de Juárez","Chilpancingo de los Bravo","Iguala de la Independencia","Zihuanatejo de Azueta"};
        ArrayAdapter<String> adapterMunicipio = new ArrayAdapter<String>(this,R.layout.spinner_item_ces, opcionesMunicipio);
        spMunicipio.setAdapter(adapterMunicipio);

        //Spinner Escolaridad
        spEscolaridad = (Spinner)findViewById(R.id.sp_Escolaridad);
        String [] opcionesEscolaridad = {"--","Primaria", "Secundaria", "Preparatoria", "Licenciatura", "Posgrado"};
        ArrayAdapter <String> adapterEscolaridad = new ArrayAdapter<String>(this, R.layout.spinner_item_ces, opcionesEscolaridad);
        spEscolaridad.setAdapter(adapterEscolaridad);

        //Spinner Estado Civil
        spEstadoCivil = (Spinner)findViewById(R.id.sp_EdoCivil);
        String [] opcionesEdoCivil = {"--","Soltero(a)", "Casado(a)", "Viudo(a)", "Divorciado(a)"};
        ArrayAdapter <String> adapterEdoCivil = new ArrayAdapter<>(this, R.layout.spinner_item_ces, opcionesEdoCivil);
        spEstadoCivil.setAdapter(adapterEdoCivil);

        //Spinner Seguridad Social
        spSeguridadSocial = (Spinner)findViewById(R.id.sp_SeguridadSocial);
        String [] opcionesSeguridadSocial = {"--","IMSS", "ISSSTE", "INSABI", "Ninguna"};
        ArrayAdapter <String> adapterSeguridadSocial = new ArrayAdapter<>(this, R.layout.spinner_item_ces, opcionesSeguridadSocial);
        spSeguridadSocial.setAdapter(adapterSeguridadSocial);

        //Spinner Uso de la Vivienda
        spUso= (Spinner)findViewById(R.id.sp_UsoVivienda);
        String [] opcionesUso = {"--","Habitada", "Deshabitada", "Baldio"};
        ArrayAdapter<String> adapterUso = new ArrayAdapter<>(this, R.layout.spinner_item_ces, opcionesUso);
        spUso.setAdapter(adapterUso);

        //Control de uso de la  vivienda
        spUso.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String uso = spUso.getItemAtPosition(position).toString();
                if(uso.equals("Baldio"))
                {
                    spMaterial.setEnabled(false);
                    spMaterial.setSelection(0);
                    spMuros.setEnabled(false);
                    spMuros.setSelection(0);
                    spPiso.setEnabled(false);
                    spPiso.setSelection(0);
                    spTecho.setEnabled(false);
                    spTecho.setSelection(0);
                }
                else
                {
                    spMaterial.setEnabled(true);
                    spMuros.setEnabled(true);
                    spPiso.setEnabled(true);
                    spTecho.setEnabled(true);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Spinner Material de la Vivienda
        spMaterial= (Spinner)findViewById(R.id.sp_Material);
        String [] opcionesMaterial = {"--","Firme", "Simple", "Mixto"};
        ArrayAdapter<String> adapterMaterial = new ArrayAdapter<>(this, R.layout.spinner_item_ces, opcionesMaterial);
        spMaterial.setAdapter(adapterMaterial);

        //Control Material de la Vivienda
        spMaterial.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String material = spMaterial.getItemAtPosition(position).toString();

                if(material.equals("Firme"))
                {
                    String [] opcionesMurosFirme = {"--","Tabique"};
                    String [] opcionesTechoFirme = {"--","Concreto", "Lámina Galvanizada"};
                    String [] opcionesPisoFirme = {"--","Cemento"};
                    ArrayAdapter<String> adapterMuros = new ArrayAdapter<>(MainActivity.this, R.layout.spinner_item_ces, opcionesMurosFirme);
                    spMuros.setAdapter(adapterMuros);
                    ArrayAdapter<String> adapterTecho = new ArrayAdapter<>(MainActivity.this, R.layout.spinner_item_ces, opcionesTechoFirme);
                    spTecho.setAdapter(adapterTecho);
                    ArrayAdapter<String> adapterPiso = new ArrayAdapter<>(MainActivity.this, R.layout.spinner_item_ces, opcionesPisoFirme);
                    spPiso.setAdapter(adapterPiso);
                }
                else if (material.equals("Simple"))
                {
                    String [] opcionesMurosSimple = {"--","Madera", "Lamina", "Bajareque", "Adobe"};
                    String [] opcionesTechoSimple = {"--", "Lámina de cartón", "Teja"};
                    String [] opcionesPisoSimple= {"--", "Tierra"};
                    ArrayAdapter<String> adapterMuros = new ArrayAdapter<>(MainActivity.this, R.layout.spinner_item_ces, opcionesMurosSimple);
                    spMuros.setAdapter(adapterMuros);
                    ArrayAdapter<String> adapterTecho = new ArrayAdapter<>(MainActivity.this, R.layout.spinner_item_ces, opcionesTechoSimple);
                    spTecho.setAdapter(adapterTecho);
                    ArrayAdapter<String> adapterPiso = new ArrayAdapter<>(MainActivity.this, R.layout.spinner_item_ces, opcionesPisoSimple);
                    spPiso.setAdapter(adapterPiso);

                }
                else if (material.equals("Mixto"))
                {
                    String [] opcionesMurosMixto = {"--","Tabique", "Madera", "Lamina", "Bajareque", "Adobe"};
                    String [] opcionesTechoMixto = {"--","Concreto", "Lámina galvanizada", "Lámina de cartón", "Teja"};
                    String [] opcionesPisoMixto= {"--","Cemento", "Tierra"};
                    ArrayAdapter<String> adapterMuros = new ArrayAdapter<>(MainActivity.this, R.layout.spinner_item_ces, opcionesMurosMixto);
                    spMuros.setAdapter(adapterMuros);
                    ArrayAdapter<String> adapterTecho = new ArrayAdapter<>(MainActivity.this, R.layout.spinner_item_ces, opcionesTechoMixto);
                    spTecho.setAdapter(adapterTecho);
                    ArrayAdapter<String> adapterPiso = new ArrayAdapter<>(MainActivity.this, R.layout.spinner_item_ces, opcionesPisoMixto);
                    spPiso.setAdapter(adapterPiso);
                }
                else{
                    Toast.makeText(MainActivity.this, "Ingrese una opción válida", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(MainActivity.this, "Ingrese una opción válida", Toast.LENGTH_LONG).show();
            }
        });

        spTecho= (Spinner)findViewById(R.id.sp_Techo);
        spMuros= (Spinner)findViewById(R.id.sp_Muros);
        spPiso= (Spinner)findViewById(R.id.sp_Piso);


        //Control Vivienda
        cocina_chk = (CheckBox)findViewById(R.id.chk_Cocina);
        sala_chk = (CheckBox)findViewById(R.id.chk_Sala);
        bano_chk = (CheckBox)findViewById(R.id.chk_Bano);

        cocina_chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    cocina = "Sí";
                }
                else{
                    cocina = "No";
                }

            }
        });

        sala_chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    sala = "Sí";
                }
                else{
                    sala = "No";
                }
            }
        });

        bano_chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    bano = "Sí";
                }
                else{
                    bano = "No";
                }
            }
        });

        //Control de servicios
        agua_chk = (CheckBox)findViewById(R.id.chk_Agua);
        drenaje_chk = (CheckBox)findViewById(R.id.chk_Drenaje);
        electricidad_chk = (CheckBox)findViewById(R.id.chk_Electricidad);
        ninguno_chk=(CheckBox)findViewById(R.id.chk_Ninguno);

        ninguno_chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                agua_chk.setChecked(false);
                drenaje_chk.setChecked(false);
                electricidad_chk.setChecked(false);
                agua = "No";
                drenaje = "No";
                electricidad = "No";
            }
        });

        agua_chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ninguno_chk.setChecked(false);
                agua = "Sí";
            }
        });

        drenaje_chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ninguno_chk.setChecked(false);
                drenaje = "Sí";
            }
        });

        electricidad_chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ninguno_chk.setChecked(false);
                electricidad = "Sí";
            }
        });
    }

    //Método localización
    public void localizacion(View view) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
            },1000);
        }
        coordenadas = (TextView) findViewById(R.id.tv_Coordenadas);
        ubicacion = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location loc = ubicacion.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (ubicacion != null) {
            coordenadas.setText(String.valueOf(loc.getLatitude())+","+String.valueOf(loc.getLongitude()));
        }
    }
    //Creación de documento PDF
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void crearPDF(View view){

        int ancho=2551, alto=3295;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            PdfDocument myPdfDocument = new PdfDocument();
            Paint myPaint = new Paint();
            Paint titlePaint = new Paint();

            PdfDocument.PageInfo myPageInfo1 = new PdfDocument.PageInfo.Builder(2551, 3295, 1).create();
            PdfDocument.Page myPage1 = myPdfDocument.startPage(myPageInfo1);
            Canvas canvas = myPage1.getCanvas();

            canvas.drawBitmap(scaledbmp,0,0, myPaint);

            titlePaint.setTextAlign(Paint.Align.CENTER);
            titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            titlePaint.setTextSize(70);
            canvas.drawText("Cédula de Estudio Socioeconómico", ancho/2, 350, titlePaint);

            myPaint.setTextAlign(Paint.Align.RIGHT);
            myPaint.setTextSize(50);
            myPaint.setColor(Color.BLACK);
            canvas.drawText("Fecha: "+fecha.getText(),2500, 400, myPaint);
            canvas.drawText("Municipio: "+spMunicipio.getSelectedItem(),2500, 470, myPaint);
            canvas.drawText("Localidad: "+localidad.getText(),2500, 540, myPaint);
            canvas.drawText("Colonia: "+colonia.getText(),2500, 610, myPaint);
            canvas.drawText("Manzana: "+manzana.getText(),2250, 680, myPaint);
            canvas.drawText("Lote "+lote.getText(),2500, 680, myPaint);
            canvas.drawText("Coordenadas "+coordenadas.getText(),2500, 750, myPaint);

            titlePaint.setTextAlign(Paint.Align.CENTER);
            titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            titlePaint.setTextSize(50);
            canvas.drawText("Datos Generales", ancho/2, 850, titlePaint);

            myPaint.setTextAlign(Paint.Align.LEFT);
            myPaint.setTextSize(40);
            myPaint.setColor(Color.BLACK);
            canvas.drawText("Nombre: "+nombre.getText()+" "+aPaterno.getText()+" "+aMaterno.getText(),20, 950, myPaint);
            canvas.drawText("CURP: "+curp.getText()+"   Lugar de nacimiento: "+lugarNacimiento.getText()+"   Fecha de nacimiento: "+fechaNac.getText(),20, 1000, myPaint);
            canvas.drawText("Escolaridad: "+spEscolaridad.getSelectedItem(),20, 1050, myPaint);
            canvas.drawText("Estado Civil: "+spEstadoCivil.getSelectedItem(),20, 1100, myPaint);
            canvas.drawText("Ocupación: "+ocupacion.getText()+"     Lugar de trabajo: "+trabajo.getText(),20, 1150, myPaint);
            canvas.drawText("Antigüedad en la Colonia: "+antiguedad.getText()+" años",20, 1200, myPaint);
            canvas.drawText("Número de habitantes en la vivienda: "+familia.getText(),20, 1250, myPaint);

            titlePaint.setTextAlign(Paint.Align.CENTER);
            titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            titlePaint.setTextSize(50);
            canvas.drawText("Distribución del Gasto Familiar", ancho/2, 1350, titlePaint);


            myPaint.setTextAlign(Paint.Align.LEFT);
            myPaint.setTextSize(40);
            myPaint.setColor(Color.BLACK);
            canvas.drawText("Ingreso Total Familiar: $"+ingresoTotal.getText(),20, 1450, myPaint);
            canvas.drawText("Alimentación: $"+alimentacion.getText(),20, 1500, myPaint);
            canvas.drawText("Salud: $"+salud.getText(),20, 1550, myPaint);
            canvas.drawText("Educación: $"+educacion.getText(),20, 1600, myPaint);
            canvas.drawText("Otros: $"+otros.getText(),20, 1650, myPaint);
            canvas.drawText("Gasto Total Familiar: $"+gastoTotal.getText(),20, 1700, myPaint);

            titlePaint.setTextAlign(Paint.Align.CENTER);
            titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            titlePaint.setTextSize(50);
            canvas.drawText("Seguridad Social", ancho/2, 1800, titlePaint);

            myPaint.setTextAlign(Paint.Align.LEFT);
            myPaint.setTextSize(40);
            myPaint.setColor(Color.BLACK);
            canvas.drawText("Afiliado al: "+spSeguridadSocial.getSelectedItem(),20, 1900, myPaint);

            titlePaint.setTextAlign(Paint.Align.CENTER);
            titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            titlePaint.setTextSize(50);
            canvas.drawText("Características de la Vivienda", ancho/2, 2000, titlePaint);

            myPaint.setTextAlign(Paint.Align.LEFT);
            myPaint.setTextSize(40);
            myPaint.setColor(Color.BLACK);
            canvas.drawText("Uso de la vivienda: "+spUso.getSelectedItem(),20, 2100, myPaint);
            canvas.drawText("Material: "+spMaterial.getSelectedItem(),20, 2150, myPaint);
            canvas.drawText("Muros: "+spMuros.getSelectedItem(),20, 2200, myPaint);
            canvas.drawText("Techo: "+spTecho.getSelectedItem(),20, 2250, myPaint);
            canvas.drawText("Piso: "+spPiso.getSelectedItem(),20, 2300, myPaint);
            canvas.drawText("Número de habitaciones: "+recamaras.getText(),20, 2350, myPaint);
            canvas.drawText("Cocina: "+cocina,20, 2400, myPaint);
            canvas.drawText("Sala: "+sala,20, 2450, myPaint);
            canvas.drawText("Baño: "+bano,20, 2500, myPaint);

            titlePaint.setTextAlign(Paint.Align.CENTER);
            titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            titlePaint.setTextSize(50);
            canvas.drawText("Servicios Públicos", ancho/2, 2600, titlePaint);

            myPaint.setTextAlign(Paint.Align.LEFT);
            myPaint.setTextSize(40);
            myPaint.setColor(Color.BLACK);
            canvas.drawText("Agua Potable: "+agua,20, 2700, myPaint);
            canvas.drawText("Drenaje: "+drenaje,20, 2750, myPaint);
            canvas.drawText("Electricidad: "+electricidad,20, 2800, myPaint);

            titlePaint.setTextAlign(Paint.Align.CENTER);
            titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            titlePaint.setTextSize(50);
            canvas.drawText("Observaciones", ancho/2, 2900, titlePaint);

            myPaint.setTextAlign(Paint.Align.LEFT);
            myPaint.setTextSize(40);
            myPaint.setColor(Color.BLACK);
            canvas.drawText(String.valueOf(observaciones.getText()),20, 3000, myPaint);

            myPdfDocument.finishPage(myPage1);

            //File file = new File(getExternalFilesDir(null),"/ces.pdf");
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),"/ces.pdf");
            try {
                doc = new FileOutputStream(file);
                myPdfDocument.writeTo(doc);
                Toast.makeText(MainActivity.this, "Archivo creado exitosamente", Toast.LENGTH_LONG).show();
                myPdfDocument.close();
                doc.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }

    }

    public void sumagasto(){
        String alimentacion_string = alimentacion.getText().toString();
        String educacion_string = educacion.getText().toString();
        String salud_string = salud.getText().toString();
        String otros_string = otros.getText().toString();

        int alimentacion_int = Integer.parseInt(alimentacion_string);
        int educacion_int = Integer.parseInt(educacion_string);
        int salud_int = Integer.parseInt(salud_string);
        int otros_int = Integer.parseInt(otros_string);

        int total = alimentacion_int + educacion_int + salud_int + otros_int;
        String resultado = String.valueOf(total);
        gastoTotal.setText(resultado);
    }

    public void verificacion(View view) {
        if(fecha.getText().toString().length() == 0){
            Toast.makeText(MainActivity.this, "No ha ingresado la Fecha", Toast.LENGTH_LONG).show();
        }
        else if(spMunicipio.getSelectedItem().equals("--")){
            Toast.makeText(MainActivity.this, "No ha seleccionado el Municipio", Toast.LENGTH_LONG).show();
        }
        else if(localidad.getText().toString().length() == 0){
            Toast.makeText(MainActivity.this, "No ha ingresado la Localidad", Toast.LENGTH_LONG).show();
        }
        else if(colonia.getText().toString().length() == 0){
            Toast.makeText(MainActivity.this, "No ha ingresado la Colonia", Toast.LENGTH_LONG).show();
        }
        else if(manzana.getText().toString().length() == 0){
            Toast.makeText(MainActivity.this, "No ha ingresado la Manzana", Toast.LENGTH_LONG).show();
        }
        else if(lote.getText().toString().length() == 0){
            Toast.makeText(MainActivity.this, "No ha ingresado el Lote", Toast.LENGTH_LONG).show();
        }
        else if(aPaterno.getText().toString().length() == 0){
            Toast.makeText(MainActivity.this, "No ha ingresado el Apellido Paterno", Toast.LENGTH_LONG).show();
        }
        else if(aMaterno.getText().toString().length() == 0){
            Toast.makeText(MainActivity.this, "No ha ingresado el Apellido Materno", Toast.LENGTH_LONG).show();
        }
        else if(nombre.getText().toString().length() == 0){
            Toast.makeText(MainActivity.this, "No ha ingresado el Nombre", Toast.LENGTH_LONG).show();
        }
        else if(curp.getText().toString().length() != 18){
            Toast.makeText(MainActivity.this, "No ha ingresado correctamente la CURP", Toast.LENGTH_LONG).show();
        }
        else if(lugarNacimiento.getText().toString().length() == 0){
            Toast.makeText(MainActivity.this, "No ha ingresado el Lugar de Nacimiento", Toast.LENGTH_LONG).show();
        }
        else if(fechaNac.getText().toString().length() == 0){
            Toast.makeText(MainActivity.this, "No ha ingresado la Fecha de Nacimiento", Toast.LENGTH_LONG).show();
        }
        else if(spEscolaridad.getSelectedItem().equals("--")){
            Toast.makeText(MainActivity.this, "No ha seleccionado la Escolaridad", Toast.LENGTH_LONG).show();
        }
        else if(spEstadoCivil.getSelectedItem().equals("--")){
            Toast.makeText(MainActivity.this, "No ha seleccionado el Estado Civil", Toast.LENGTH_LONG).show();
        }
        else if(ocupacion.getText().toString().length() == 0){
            Toast.makeText(MainActivity.this, "No ha ingresado la Ocupación", Toast.LENGTH_LONG).show();
        }
        else if(trabajo.getText().toString().length() == 0){
            Toast.makeText(MainActivity.this, "No ha ingresado el Lugar de Trabajo", Toast.LENGTH_LONG).show();
        }
        else if(antiguedad.getText().toString().length() == 0){
            Toast.makeText(MainActivity.this, "No ha ingresado la Antigüedad en la Colonia", Toast.LENGTH_LONG).show();
        }
        else if(familia.getText().toString().length() == 0){
            Toast.makeText(MainActivity.this, "No ha ingresado el número de Habitantes en la Vivienda", Toast.LENGTH_LONG).show();
        }
        else if(ingresoTotal.getText().toString().length() == 0){
            Toast.makeText(MainActivity.this, "No ha ingresado el Ingreso Total Familiar", Toast.LENGTH_LONG).show();
        }
        else if(alimentacion.getText().toString().length() == 0){
            Toast.makeText(MainActivity.this, "No ha ingresado el Gasto en Alimentación", Toast.LENGTH_LONG).show();
        }
        else if(salud.getText().toString().length() == 0){
            Toast.makeText(MainActivity.this, "No ha ingresado el Gasto en Salud", Toast.LENGTH_LONG).show();
        }
        else if(educacion.getText().toString().length() == 0){
            Toast.makeText(MainActivity.this, "No ha ingresado el Gasto en Educación", Toast.LENGTH_LONG).show();
        }
        else if(spSeguridadSocial.getSelectedItem().equals("--")){
            Toast.makeText(MainActivity.this, "No ha seleccionado la Seguridad Social", Toast.LENGTH_LONG).show();
        }
        else if(spUso.getSelectedItem().equals("--")){
            Toast.makeText(MainActivity.this, "No ha seleccionado el Uso de la Vivienda", Toast.LENGTH_LONG).show();
        }
        else if(recamaras.getText().toString().length() == 0){
            Toast.makeText(MainActivity.this, "No ha ingresado el número Recamaras", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(MainActivity.this, "Información Ingresada Correctamente", Toast.LENGTH_LONG).show();
            sumagasto();
            generarPDF.setEnabled(true);
        }

    }

}