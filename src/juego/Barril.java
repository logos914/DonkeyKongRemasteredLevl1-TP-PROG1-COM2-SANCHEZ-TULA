package juego;

import java.awt.Image;
import java.util.Random;

import juego.Viga;
import entorno.Entorno;
import entorno.Herramientas;

public class Barril {

	private double posx;
	private double posy;
	private int diametro;
	private double escala;

	private Image spin_izquierda;
	private Image spin_derecha;
	private Image cayendo;

	// Conserva la dirección en la que estaba moviendo el barril "derecha" o
	// "izquierda"
	private String ultima;

	/*
	 * Es necesaria una variable que indique que ee barril fue saltado para que no
	 * sea contado doble en las siguientes situaciones: En el proceso de salto, la
	 * función que detecta el correcto salto puede dar varios positivos durante una
	 * cantidad de ticks cercanos entre si. Atrapando el primer tick donde se
	 * detecta que el barril fue saltado, se evita que en los ticks siguientes donde
	 * también es positivo, se cuenten los puntos inecesariamente. Evitar que el
	 * jugador salte el barril en una viga y luego intente saltarlo en una viga
	 * inferior para contador doble puntaje.
	 */

	private boolean saltado;

	// Conocer si está cayendo por escalera permite detener el movimiento hacia
	// izquierda o derecha. Ayuda a diferenciar una caida desde una viga con
	// respecto a la de escalera.
	private boolean cayendoPorEscalera;

	// Como un barril tarda varios ticks en atravesar el ancho de una escalera.
	// Necesitamos indicar en que tick se tomó la decisión de caer o no por la
	// misma. Para que en el tick siguiente
	// no sobreescriba la decisión. Toma una decisión por escalera,y bloquea decidir
	// de nuevo por un cierto tiempo.
	private int ultimaEleccion;

	// Para animar la caida por escalera se utiliza esta variable para intercambiar
	// entre 10º y -10º.
	private int anguloRotacion;

	// Asiste en la elección del anguloRotacion
	private boolean sentidoRotacionDerecha;

	public Barril(Viga vigasuelo) {

		this.diametro = 17;
		this.escala = (double) this.diametro / 108;

		/*
		 * Se crea el barril en la posición desde donkey decidió arrojarlo
		 */
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
		this.cayendo = Herramientas.cargarImagen("rsc/graficos/barriles/cayendo.png");

		this.saltado = false;
		this.ultimaEleccion = 0;
		this.anguloRotacion = 0;
		this.sentidoRotacionDerecha = true;

	}

	/*
	 * Esta función al ser llamada retorna si el barril debe destruirse por estar
	 * fuera de la pantalla del juego
	 * 
	 */
	public boolean deboDestruirme(Entorno entorno, Viga[] suelos) {
		if (this.posx < 15 && this.pisando(suelos) == 0) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * Esta función se encarga de dibujar en pantalla al barril y calcular su
	 * moviento.
	 * 
	 */

	public void dibujar(Entorno entorno, int contador, Viga[] suelos, Escaleras[] escaleras) {

		// Si está rodando sobre el suelo
		if (pisando(suelos) != -1) {

			// Si la decisión de caer por la escalera es afirmativa
			if (caerPorEscalera(escaleras, suelos, contador)) {

				// Este movimiento inicial detendrá en el próximo los movimientos a izquierda o
				// derecha
				// Porque la función pisando devolverá -1
				this.posy = this.posy + 1;
				entorno.dibujarImagen(cayendo, this.posx, this.posy, this.anguloRotacion, this.escala);

			} else {

				// En vigas con indice par desplazar a izquierda
				if (this.posx >= 10 && pisando(suelos) % 2 == 0) {
					this.posx = this.posx - 1.7;
					entorno.dibujarImagen(spin_izquierda, this.posx, this.posy, 0, this.escala);
					this.ultima = "izquierda";
				}

				// En vigas con indice impar desplazar a derecha
				else if (this.posx <= 800 && pisando(suelos) % 2 == 1) {
					this.posx = this.posx + 1.7;
					entorno.dibujarImagen(spin_derecha, this.posx, this.posy, 0, this.escala);
					this.ultima = "derecha";
				}

			}
		}

		// Si NO está rodando sobre el suelo
		if (pisando(suelos) == -1) {

			// cambia la posición con respecto al eje "y" hacia abajo
			this.posy += 1;

			// Si la caida está producida por caer por escalera
			if (this.cayendoPorEscalera) {

				// Cada vez que el contador es divisible por 10, entonces hay que cambiar el
				// sentido de rotación
				// Esto es puramente gráfico, da la sensación de un barril cayendo por
				// escaleras.
				if (contador % 10 == 0) {
					if (this.sentidoRotacionDerecha) {
						this.sentidoRotacionDerecha = false;
					} else {
						this.sentidoRotacionDerecha = true;
					}
				}

				// Dependiendo el sentido de Rotación, el engulo es positivo o negativo
				if (this.sentidoRotacionDerecha) {
					this.anguloRotacion = 10;
				} else {
					this.anguloRotacion = -10;
				}

				// dibujar la caida por escalera
				entorno.dibujarImagen(cayendo, this.posx, this.posy, this.anguloRotacion, this.escala);

				// Si la caida es producida por el final de una viga
			} else {

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

	}

	// Igual que pisando de Personaje
	public int pisando(Viga[] suelos) {

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

	/*
	 * Esta función toma la decisión de decidir si el barril caerá por la siguiente
	 * escalera.
	 */

	public boolean caerPorEscalera(Escaleras[] escaleras, Viga[] suelos, int contador) {

		// Sólo se toma la decisión de nuevo, si ha pasado el suficiente tiempo (o sea
		// que se está decisión sobre una escalera
		// diferente a la anterior decidida.
		if (this.ultimaEleccion + 30 < contador) {

			Random rnd = new Random();
			int numero = rnd.nextInt(1200);

			// i es el piso actual por el cual rueda el barril
			int i = pisando(suelos);

			// Comprobación de escaleras para todos los pisos excepto la planta baja
			if (i != 0) {

				/*
				 * Analisis de una escalera cerca de tipo obligatoria, se toma la decisión si se
				 * cumple una segunda condición: Estar cerca de una escalera, de forma de que
				 * exista posibilidad en el proximo tick de caer por ella.
				 */

				// A la altura de los pies del barril
				if (escaleras[i - 1].extremoSuperior() - this.pies() <= 10) {

					// Entre las coordinadas x del barril debe haber una escalera
					if (escaleras[i - 1].lateralDerecho() - 9 >= this.posx
							&& escaleras[i - 1].lateralIzquierdo() + 9 <= this.posx) {

						// Si el random es divisible por 6 cae, sino no. 1/6 de posibilidades de caer.
						if (numero % 6 == 0) {
							this.cayendoPorEscalera = true;
							this.ultimaEleccion = contador;
							return true;
						} else {
							this.cayendoPorEscalera = false;
							this.ultimaEleccion = contador;
							return false;
						}
					}
				}

				/*
				 * Mismo analisis de una escalera cerca pero de tipo adicional, se toma la
				 * decisión si se cumple una segunda condición: Estar cerca de una escalera, de
				 * forma de que exista posibilidad en el proximo tick de caer por ella.
				 */

				// A la altura de los pies del barril (Útil para eliminar escaleras no
				// completas)
				if (escaleras[i + 4].extremoSuperior() - this.pies() <= 10) {

					// Entre las coordinadas x del barril debe haber una escalera
					if (escaleras[i + 4].lateralDerecho() >= this.posx
							&& escaleras[i + 4].lateralIzquierdo() <= this.posx) {

						// Si el random es divisible por 6 cae, sino no. 1/6 de posibilidades de caer.
						if (numero % 6 == 0) {
							this.cayendoPorEscalera = true;
							this.ultimaEleccion = contador;
							return true;
						} else {
							this.cayendoPorEscalera = false;
							this.ultimaEleccion = contador;
							return false;
						}
					}
				}

			}
		}
		// Para todos los demás casos no hay caida posible por escalera
		cayendoPorEscalera = false;
		return false;

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