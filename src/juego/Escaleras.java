package juego;

import java.awt.Color;
import java.util.Random;

import entorno.Entorno;

public class Escaleras {

	private double x;
	private double y;
	private double ancho;
	private double alto;

	public Escaleras(int pos, Viga[] suelos) {

		Random rnd = new Random();
		boolean escaleraCompleta;
		this.ancho = 30;
		
		/*
		 * Hay 2 tipos de escaleras. Las obligatorias que deben estar completas y ocupan
		 * las "pos" del 0 al 4. Luego las adicionales que pueden estar imcompletas o
		 * no. Ocupan las "pos" del 5 al 9.
		 */

		// Para las obligatorias
		if (pos < 5) {

			int offsetEscalera = rnd.nextInt(50);

			if (pos % 2 == 0) {

				this.x = suelos[pos + 1].extremoDerecho() - 30 - offsetEscalera;

			} else {

				this.x = suelos[pos + 1].extremoIzquierdo() + 30 + offsetEscalera;
			}

			// El punto mediatriz del segmento que representa la distancia entre la
			// superficie superior de la viga que funciona
			// como el suelo, y la superficie superior de la viga siguiente. Dicho punto es
			// el centro Y de la escalera.
			this.y = ((suelos[pos].dondeEmpiezaElSuelo() - suelos[pos + 1].dondeEmpiezaElSuelo()) / 2)
					+ suelos[pos + 1].dondeEmpiezaElSuelo();

			// El alto es la distancia entre la superficie superior de la viga que funciona
			// como el suelo, y la superficie superior de la viga siguiente.
			this.alto = suelos[pos].dondeEmpiezaElSuelo() - suelos[pos + 1].dondeEmpiezaElSuelo();

		}

		// Para las escaleras adicionales
		else {

			int offsetEscalera = rnd.nextInt(150);

			// Las escaleras pares, conectan con vigas superiores que no tocan el extremo
			// derecho.
			// Las escaleras impares, conectan con vigas superiores que no tocan el extremo
			// izquierdo.
			// A su vez, la posición de la escalera, está aumentada 4 veces con repecto al
			// indice de la viga superior
			// con la cual debe conectar.
			if (pos % 2 == 0) {

				this.x = suelos[pos - 4].extremoDerecho() - 250 - offsetEscalera;

			} else {

				this.x = suelos[pos - 4].extremoIzquierdo() + 250 + offsetEscalera;
			}

			// Para todas las escaleras adicionales, expecto la de planta baja.
			if (pos != 5) {

				// Generamos un random que ayudará a elegir si la escalera se presentará de
				// forma completa o no
				int eleccionEscaleraCompleta = rnd.nextInt(300);

				// De esta forma existe sólo un 33% de posibilidades de que aparezca completa.
				if (eleccionEscaleraCompleta % 3 == 0) {

					escaleraCompleta = true;
				} else {
					escaleraCompleta = false;
				}

				// La escalera adicional de planta baja, nunca estará completa
			} else {
				escaleraCompleta = false;
			}

			if (escaleraCompleta) {
				// Método normal para calcular la el centro "Y" y la altura.
				this.y = ((suelos[pos - 5].dondeEmpiezaElSuelo() - suelos[pos - 4].dondeEmpiezaElSuelo()) / 2)
						+ suelos[pos - 4].dondeEmpiezaElSuelo();

				this.alto = suelos[pos - 5].dondeEmpiezaElSuelo() - suelos[pos - 4].dondeEmpiezaElSuelo();

			}

			else {

				// Al método normal le corremos 25 pixeles hacia abajo y la altura es la mitad.
				// impidiendo que la escalera esté completa
				this.y = ((suelos[pos - 5].dondeEmpiezaElSuelo() - suelos[pos - 4].dondeEmpiezaElSuelo()) / 2)
						+ suelos[pos - 4].dondeEmpiezaElSuelo() + 25;

				this.alto = (suelos[pos - 5].dondeEmpiezaElSuelo() - suelos[pos - 4].dondeEmpiezaElSuelo()) / 2;

			}

		}

	}

	public void dibujar(Entorno entorno) {

		// Rectángulo básico de la viga, respetando los valores indicados por el
		// constructor
		entorno.dibujarRectangulo(this.x, this.y, this.ancho, this.alto, 0.0, Color.BLUE);

		double paso = this.y + (this.alto / 2) - 3;

		// Se decide que la suma de la base de un triangulo, la punta del triángulo
		// adyacente y un espacio
		// extra sea la 25ava parte del ancho de la viga - 4 pixeles
		double rectangulos = (this.alto / 10);

		// Indica la cantidad de parejas de triangulos dibujados. Una pareja es un
		// triangulo con la punta hacia arriba
		// y el otro con la punta hacia abajo.
		int dibujados = 0;

		// Este bucle dibuja la pareja de triángulos a lo largo de la viga.

		while (dibujados <= rectangulos) {

			entorno.dibujarRectangulo(this.x, paso, 28, 9, 0.0, java.awt.Color.BLACK);
			paso -= 10;

			dibujados += 1;

		}

	}

	public int lateralDerecho() {
		return (int) this.x + 15;
	}

	public int lateralIzquierdo() {
		return (int) this.x - 15;
	}

	public int extremoSuperior() {
		return (int) (this.y - (this.alto / 2));
	}

	public int extremoInferior() {
		return (int) (this.y + (this.alto / 2));
	}

}
