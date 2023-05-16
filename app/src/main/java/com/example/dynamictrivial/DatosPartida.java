package com.example.dynamictrivial;

import java.util.ArrayList;

public class DatosPartida {
    private ArrayList<Jugador> jugadores;
    private static int turno;

    public DatosPartida(ArrayList<Jugador> jugadores, int turno) {
        this.jugadores = jugadores;
        this.turno = turno;
    }

    public ArrayList<Jugador> getJugadores() {
        return jugadores;
    }

    public static int getTurno() {
        return turno;
    }

    public void setTurno(int turno) {
        this.turno = turno;
    }
}
