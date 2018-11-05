package juego;

import java.awt.Color;
import java.awt.Image;
import juego.Viga;
import entorno.Entorno;
import entorno.Herramientas;
import entorno.InterfaceJuego;

public class Barril {

	private double posx;
	private double posy;
	private int diametro;
	private double escala;

	private Image spin_izquierda;
	private Image spin_derecha;
	private String ultima;
	private boolean saltado;

	public Barril(Viga vigasuelo) {

		// this.estado = "vivo";
		this.diametro = 17;
		this.escala = (double) this.diametro / 108;

		// this.posy = 48;

		if (vigasuelo.getPos() == 6) {
			this.posy = (int) vigasuelo.dondeEmpiezaElSuelo() - 20;
			this.ultima = "derecha";
			this.posx = 120;
		} else if (vigasuelo.getPos() == 4) {
			this.posy = (int) vigasuelo.dondeEmpiezaElSuelo() - 190;
			this.ultima = "izquierda";
			this.posx = 120;
		}

		this.spin_izquierda = Herramientas.cargarImagen("rsc/graficos/barriles/spin-izquierda.gif");
		this.spin_derecha = Herramientas.cargarImagen("rsc/graficos/barriles/spin-derecha.gif");

		this.saltado = false;

	}

	public boolean deboDestruirme(Entorno entorno, Viga[] suelos) {
		if (this.posx < 15 && this.pisando(entorno, suelos) == 0) {
			return true;
		} else {
			return false;
		}
	}

	public void dibujar(Entorno entorno, int contador, Viga[] suelos) {

		// Image barril =
		// Herramientas.cargarImagen("rsc/graficos/barriles/cayendo.png");
		// entorno.dibujarCirculo(posx, posy, diametro, Color.blue);

		// Si está rodando sobre el suelo
		if (pisando(entorno, suelos) != -1) {

			// En vigas con indice par desplazar a izquierda
			if (this.posx >= 10 && pisando(entorno, suelos) % 2 == 0) {
				this.posx = this.posx - 1.7;
				entorno.dibujarImagen(spin_izquierda, this.posx, this.posy, 0, this.escala);
				this.ultima = "izquierda";
			}

			// En vigas con indice impar desplazar a derecha
			else if (this.posx <= 800 && pisando(entorno, suelos) % 2 == 1) {
				this.posx = this.posx + 1.7;
				entorno.dibujarImagen(spin_derecha, this.posx, this.posy, 0, this.escala);
				this.ultima = "derecha";
			}

		}

		// Si NO está rodando sobre el suelo
		if (pisando(entorno, suelos) == -1) {

			// cambia la posición con respecto al eje "y" hacia abajo
			this.posy += 1;

			// Si venia desplazandose a derecha pero está cayendo y hay espacio en el x,
			// se sigue desplazando a derecha
			if (this.posx <= 800 && this.ultima.equals("derecha")) {
				this.posx = this.posx + 1.7;
				entorno.dibujarImagen(spin_derecha, this.posx, this.posy, 0, this.escala);
			}

			// De lo contrario hay que indicarle que en el próximo tick se desplace a
			// izquierda.
			else {
				this.ultima = "izquierda";
			}

			// Si venia desplazandose a izquierda pero está cayendo y hay espacio en el x,
			// se sigue desplazando a izquierda
			if (this.posx >= 10 && this.ultima.equals("izquierda")) {
				this.posx = this.posx - 1.7;
				entorno.dibujarImagen(spin_izquierda, this.posx, this.posy, 0, this.escala);
			} else {
				// De lo contrario hay que indicarle que en el próximo tick se desplace a
				// derecha.
				this.ultima = "derecha";
			}

		}

	}

	// Igual que pisando de Personaje
	public int pisando(Entorno entorno, Viga[] suelos) {

		for (int i = 0; i < suelos.length; i++) {

			if (this.pies() == (int) suelos[i].dondeEmpiezaElSuelo()) {

				if (this.lateralDerecho() < suelos[i].extremoIzquierdo()
						|| this.lateralIzquierdo() > suelos[i].extremoDerecho()) {

					return -1;

				} else {

					return i;

				}
			}

		}

		return -1;

	}

	public int pies() {
		return (int) this.posy + diametro / 2 - 2;
	}

	public int superior() {
		return (int) this.posy - diametro / 2 + 2;
	}

	public int lateralDerecho() {
		return (int) this.posx + diametro / 2;
	}

	public int lateralIzquierdo() {
		return (int) this.posx - diametro / 2;
	}

	public int centroX() {
		return (int) this.posx;
	}

	public void saltado() {
		this.saltado = true;
	}

	public boolean fueSaltado() {
		return this.saltado;
	}

}