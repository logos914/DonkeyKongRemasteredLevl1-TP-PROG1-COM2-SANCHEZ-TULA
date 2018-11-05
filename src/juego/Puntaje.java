package juego;

import entorno.Entorno;
import java.awt.Color;

public class Puntaje {
	private int puntos;

	public Puntaje() {
		this.puntos = 0;
	}

	public void saltarbarril() {

		this.puntos += 15;
	}

	public void ganar() {

		this.puntos += 100;
	}

	public void dibujar(Entorno entorno) {

		entorno.cambiarFont("terminal", 18, Color.GREEN);

		entorno.escribirTexto("Puntos: " + String.valueOf(this.puntos), 685, 15);
	}

}