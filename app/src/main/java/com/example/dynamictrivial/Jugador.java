package com.example.dynamictrivial;

import android.os.Parcel;

import com.example.dynamictrivial.DatosPartida;

public class Jugador {
    private String nombre;
    private int puntosArte;
    private int puntosDeporte;
    private int puntosEntretenimiento;
    private int puntosGeografia;
    private int puntosHistoria;
    private int turno = DatosPartida.getTurno();


    public Jugador(String nombre, int puntosArte, int puntosDeporte, int puntosEntretenimiento, int puntosGeografia, int puntosHistoria, int turno) {
        this.nombre = nombre;
        this.puntosArte = puntosArte;
        this.puntosDeporte = puntosDeporte;
        this.puntosEntretenimiento = puntosEntretenimiento;
        this.puntosGeografia = puntosGeografia;
        this.puntosHistoria = puntosHistoria;
        this.turno = turno;
    }

    protected Jugador(Parcel in) {
        nombre = in.readString();
        puntosArte = in.readInt();
        puntosDeporte = in.readInt();
        puntosEntretenimiento = in.readInt();
        puntosGeografia = in.readInt();
        puntosHistoria = in.readInt();
        turno = in.readInt();
    }

    public Jugador(String nombre, int puntosArte, int puntosDeporte, int puntosEntretenimiento, int puntosGeografia, int puntosHistoria) {

    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPuntosArte() {
        return puntosArte;
    }

    public void setPuntosArte(int puntosArte) {
        this.puntosArte = puntosArte;
    }

    public int getPuntosDeporte() {
        return puntosDeporte;
    }

    public void setPuntosDeporte(int puntosDeporte) {
        this.puntosDeporte = puntosDeporte;
    }

    public int getPuntosEntretenimiento() {
        return puntosEntretenimiento;
    }

    public void setPuntosEntretenimiento(int puntosEntretenimiento) {
        this.puntosEntretenimiento = puntosEntretenimiento;
    }

    public int getPuntosGeografia() {
        return puntosGeografia;
    }

    public void setPuntosGeografia(int puntosGeografia) {
        this.puntosGeografia = puntosGeografia;
    }

    public int getPuntosHistoria() {
        return puntosHistoria;
    }

    public void setPuntosHistoria(int puntosHistoria) {
        this.puntosHistoria = puntosHistoria;
    }

    public int getTurno() {
        return turno;
    }

    public void setTurno(int turno) {
        this.turno = turno;
    }
}
