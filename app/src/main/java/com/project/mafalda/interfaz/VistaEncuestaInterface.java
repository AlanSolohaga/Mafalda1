package com.project.mafalda.interfaz;

import android.graphics.Bitmap;

import com.project.mafalda.model.Encuesta;
import com.project.mafalda.model.Imagen;

import java.util.ArrayList;

public interface VistaEncuestaInterface {
    void vista(Encuesta encuesta,ArrayList<Imagen> imagenes,String cabecera);
    void vistaError();
    void mostrarImagen(Imagen imagen);
    void mostrarImagen(Bitmap imagen);
    void error(String error);
}
