package juego;

import java.awt.Color;
import java.util.Random;

import java.awt.Image;

import entorno.Entorno;
import entorno.Herramientas;
import entorno.InterfaceJuego;
import juego.*;

public class Donkey {

	int ultimoLanzamiento;
	Random rnd = new Random();
	int lanzarRandom;
	String violencia;

	public Donkey() {

		ultimoLanzamiento = 0;
		violencia = "violento";

	}

	public void gorilear(Entorno entorno, int contador) {

		if (contador - ultimoLanzamiento < 30) {

			Image gorila = Herramientas.cargarImagen("rsc/graficos/donkey/tirar.gif");
			Image stock = Herramientas.cargarImagen("rsc/graficos/barriles/stock.png");
			entorno.dibujarImagen(gorila, 100, 30, 0, 0.19);
			entorno.dibujarImagen(stock, 30, 32, 0, 0.13);

		} else {
			Image gorila = Herramientas.cargarImagen("rsc/graficos/donkey/gorilear.gif");
			Image stock = Herramientas.cargarImagen("rsc/graficos/barriles/stock.png");
			entorno.dibujarImagen(gorila, 100, 30, 0, 0.19);
			entorno.dibujarImagen(stock, 30, 32, 0, 0.13);
		}
	}

	public boolean decidir(int contador) {

		if (this.violencia.equals("violento")) {
			if (this.lanzarRandom == contador) {
				this.ultimoLanzamiento = contador;
				this.lanzarRandom = 0;

				return true;
			}

			if (contador >= this.ultimoLanzamiento + this.rnd.nextInt(220) + 60 && this.lanzarRandom == 0) {

				lanzarRandom = this.rnd.nextInt(250) + contador;

				return false;
			}

			return false;

		}

		return false;
	}

	public void noMasViolencia() {
		this.violencia = "noviolento";
	}

	public int arribaOabajo() {

		int eleccion = this.rnd.nextInt(60);
		if (eleccion % 3 == 0) {
			return -3;
		} else {
			return -1;
		}
	}

}
