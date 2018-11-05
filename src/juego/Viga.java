package juego;

import java.awt.Color;
import entorno.Entorno;
import entorno.Herramientas;
import entorno.InterfaceJuego;
import juego.*;

public class Viga {

	private int pos;
	private double x;
	private double y;
	private double largo;
	private double alto;

	/*
	 * Este constructor, ya tiene definida de forma estricta y estática las
	 * posiciones de las vigas
	 */

	public Viga(int pos) {

		switch (pos) {
		case 1:
			this.x = 400;
			this.y = 575;
			this.largo = 820;
			this.alto = 25;
			break;
		case 2:
			this.x = 325;
			this.y = 475;
			this.largo = 700;
			this.alto = 25;
			break;
		case 3:
			this.x = 475;
			this.y = 375;
			this.largo = 700;
			this.alto = 25;
			break;
		case 4:
			this.x = 325;
			this.y = 275;
			this.largo = 700;
			this.alto = 25;
			break;
		case 5:
			this.x = 475;
			this.y = 175;
			this.largo = 700;
			this.alto = 25;
			break;
		case 6:
			this.x = 325;
			this.y = 75;
			this.largo = 700;
			this.alto = 25;
			break;
		}

		this.pos = pos;

	}

	/*
	 * Dibujar
	 * 
	 * Esta función debe ser llamada en cada tick por cada viga que exista. Por cada
	 * tick dibuja la viga.
	 * 
	 * La construcción gráfica de la viga tiene un condimento especial. Para dar la
	 * sensación de que es una estructura metálica, se dibuja un rectángulo de color
	 * rojo de fondo, y sobre el de forma estratégica, triángulos del mismo color
	 * que el fondo, en juegos de a dos. Cada uno invertido 90º con respecto al
	 * anterior.
	 * 
	 */
	public void dibujar(Entorno entorno) {

		// Rectángulo básico de la viga, respetando los valores indicados por el
		// constructor
		entorno.dibujarRectangulo(this.x, this.y, this.largo, this.alto, 0.0, Color.RED);

		// El extremo izquierdo de la viga corrido 10px
		double paso = this.x - (this.largo / 2) + 10;

		// Se decide que la suma de la base de un triangulo, la punta del triángulo
		// adyacente y un espacio
		// extra sea la 25ava parte del ancho de la viga - 4 pixeles
		double triangulos = (this.largo / 25) - 4;

		// Indica la cantidad de parejas de triangulos dibujados. Una pareja es un
		// triangulo con la punta hacia arriba
		// y el otro con la punta hacia abajo.
		int dibujados = 0;

		// Este bucle dibuja la pareja de triángulos a lo largo de la viga.
		while (dibujados <= triangulos) {

			entorno.dibujarTriangulo(paso, this.y, 21, 21, Herramientas.radianes(90), java.awt.Color.BLACK);
			paso += 14;
			entorno.dibujarTriangulo(paso, this.y, 21, 21, Herramientas.radianes(270), java.awt.Color.BLACK);
			paso += 14;
			dibujados += 1;

		}

	}

	// Devuelve la posX
	public int getPosx() {
		return (int) this.x;
	}

	// Devuelve la posY
	public int getPosy() {
		return (int) this.y;
	}

	// Devuelve el Ancho
	public int getAncho() {
		return (int) this.largo;
	}

	public int getPos() {
		return pos;
	}

	/*
	 * DondeEmpiezaElSuelo
	 * 
	 * Esta función devuelve el valor en el 'y' en el cual comienza la viga.
	 * Sabiendo que el 'y' se encuentra en el centro.
	 * 
	 */
	public double dondeEmpiezaElSuelo() {

		return this.y - (this.alto / 2) - 1;
	}

	public double dondeTerminaElTecho() {

		return this.y + (this.alto / 2) + 1;
	}

	/*
	 * Esta función indica donde comienza la viga en el eje X.
	 */
	public int extremoIzquierdo() {
		return this.getPosx() - this.getAncho() / 2;
	}

	/*
	 * Esta función indica donde termina la viga en el eje X.
	 */
	public int extremoDerecho() {
		return this.getPosx() + this.getAncho() / 2;
	}

}
