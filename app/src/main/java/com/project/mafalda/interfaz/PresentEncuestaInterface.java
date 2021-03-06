package com.project.mafalda.interfaz;

import android.content.Context;
import android.graphics.Bitmap;

import com.project.mafalda.model.Encuesta;
import com.project.mafalda.model.Imagen;

import java.util.ArrayList;

public interface PresentEncuestaInterface {
    void cargarVista(String nombre, Context context);
    void vista(Encuesta encuesta,ArrayList<Imagen> imagenes,String cabecera);
    void vistaError();
    void respuesta(Context context,String nombre, String respuesta,Imagen imagen);
    void mostrarImagen(Imagen imagen);
    void mostrarImagen(Bitmap imagen);

    void siguienteImagen(Context context, Encuesta encuesta,String link);

    void error(String toString);
}
