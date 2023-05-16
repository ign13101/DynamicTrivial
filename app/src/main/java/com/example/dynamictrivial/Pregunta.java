package com.example.dynamictrivial;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pregunta implements Parcelable {
    private String categoria;
    private String pregunta;
    private List<String> opciones;
    private int respuesta;

    public Pregunta() {
    }

    public Pregunta(String categoria, String pregunta, List<String> opciones, int respuesta) {
        this.categoria = categoria;
        this.pregunta = pregunta;
        this.opciones = opciones;
        this.respuesta = respuesta;
    }

    protected Pregunta(Parcel in) {
        categoria = in.readString();
        pregunta = in.readString();
        opciones = in.createStringArrayList();
        respuesta = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(categoria);
        dest.writeString(pregunta);
        dest.writeStringList(opciones);
        dest.writeInt(respuesta);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Pregunta> CREATOR = new Creator<Pregunta>() {
        @Override
        public Pregunta createFromParcel(Parcel in) {
            return new Pregunta(in);
        }

        @Override
        public Pregunta[] newArray(int size) {
            return new Pregunta[size];
        }
    };

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("categoria", categoria);
        result.put("pregunta", pregunta);
        result.put("opciones", opciones);
        result.put("respuesta", respuesta);
        return result;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public List<String> getOpciones() {
        return opciones;
    }

    public void setOpciones(List<String> opciones) {
        this.opciones = opciones;
    }

    public int getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(int respuesta) {
        this.respuesta = respuesta;
    }
}
