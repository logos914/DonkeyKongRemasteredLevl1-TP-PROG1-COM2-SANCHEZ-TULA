package juego;

import java.util.Random;

import java.awt.Image;

import entorno.Entorno;
import entorno.Herramientas;

public class Donkey {

	private int ultimoLanzamiento;

	private int lanzarRandom;
	private String violencia;

	public Donkey() {

		this.ultimoLanzamiento = 0;
		this.violencia = "violento";

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

		Random rnd = new Random();

		if (this.violencia.equals("violento")) {
			if (this.lanzarRandom == contador) {
				this.ultimoLanzamiento = contador;
				this.lanzarRandom = 0;

				return true;
			}

			if (contador >= this.ultimoLanzamiento + rnd.nextInt(220) + 60 && this.lanzarRandom == 0) {

				lanzarRandom = rnd.nextInt(250) + contador;

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
		Random rnd = new Random();
		int eleccion = rnd.nextInt(60);
		if (eleccion % 3 == 0) {
			return -3;
		} else {
			return -1;
		}
	}

}
