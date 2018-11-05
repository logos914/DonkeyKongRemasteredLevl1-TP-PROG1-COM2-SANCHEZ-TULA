package juego;

import juego.Viga;
import java.awt.Image;

import entorno.Entorno;
import entorno.Herramientas;

public class Personaje {

	private String estado;
	private int posx;
	private int posy;

	private Image mirandoIzquierda;
	private Image mirandoDerecha;

	private Image caminandoIzquierda;
	private Image caminandoDerecha;

	private Image saltandoIzquierda;
	private Image saltandoDerecha;

	private Image subiendo;

	private Image subiendo_quieto;

	private char ultima; // ultima tecla de sentido (DER o IZQ) presionada (Sirve para saber para donde
							// debe mirar el personaje).

	private int tiempoSalto; // tick en el cual se ejecutó el último salto (o el actual)

	private boolean estaSaltando; // Indica si está saltando (ascendiendo) o no.

	private boolean estaCayendo; // Indica si está cayendo (es decir que sus pies no están no están tocando viga
									// alguna.

	private boolean estaCercaEscalera;
	private boolean estaEnEscalera;
	private int enEscalera;

	private int sonando = 1; // Ultimo archivo de sonido que se usó para caminar, hay 3 variantes.
	private int sonandoDesde = 0; // tick en el cual se ejecutó el último sonido de caminar (ayuda a evitar que
									// suenen sonidos en cada tick)

	public Personaje(Viga vigasuelo) {

		this.estado = "vivo";
		this.posx = 50;
		this.posy = (int) vigasuelo.dondeEmpiezaElSuelo() - 35; // 35 pixeles por encima de la viga inicial, genera una
																// linda caida en el spawn

		this.mirandoIzquierda = Herramientas.cargarImagen("rsc/graficos/marito/mira-izquierda.png");
		this.mirandoDerecha = Herramientas.cargarImagen("rsc/graficos/marito/mira-derecha.png");

		this.caminandoIzquierda = Herramientas.cargarImagen("rsc/graficos/marito/camina-izquierda.gif");
		this.caminandoDerecha = Herramientas.cargarImagen("rsc/graficos/marito/camina-derecha.gif");

		this.saltandoIzquierda = Herramientas.cargarImagen("rsc/graficos/marito/salta-izquierda.png");
		this.saltandoDerecha = Herramientas.cargarImagen("rsc/graficos/marito/salta-derecha.png");

		this.subiendo = Herramientas.cargarImagen("rsc/graficos/marito/subiendo.gif");
		this.subiendo_quieto = Herramientas.cargarImagen("rsc/graficos/marito/quieto_subiendo.png");

		this.tiempoSalto = 0;
		this.estaSaltando = false;
		this.estaCayendo = false;
		this.estaEnEscalera = false;
		this.estaCercaEscalera = false;
		this.ultima = 39;

	}

	public boolean tocando(Barril[] barriles) {

		for (int i = 0; i < barriles.length; i++) {

			if (barriles[i] != null) {

				if (this.lateralDerecho() - barriles[i].lateralIzquierdo() > 0
						&& this.lateralIzquierdo() - barriles[i].lateralIzquierdo() < 0
						&& this.pies() - barriles[i].pies() >= 0 && this.cabeza() - barriles[i].pies() <= 0

				) {
					System.out.println("[" + i + "] Colision Derecha");
					return true;

				}

				if (this.lateralIzquierdo() - barriles[i].lateralDerecho() < 0
						&& this.lateralDerecho() - barriles[i].lateralDerecho() > 0
						&& this.pies() - barriles[i].pies() >= 0 && this.cabeza() - barriles[i].pies() <= 0

				) {
					System.out.println("[" + i + "] Colision Izquierda");
					return true;

				}

			}
		}
		return false;

	}

	/*
	 * Hacer Sonar.
	 * 
	 * Esta función ejecuta el sonido de caminar pero evita que suene en cada tick
	 * donde se está caminando. Sino habría una bola de sonido indistinguible.
	 * 
	 * Se le debe indicar el momento actual en ticks como parámetro.
	 * 
	 * La función decide hacer sonar alguna de las 3 variantes de sonidos de pasos
	 * que hay. Y sólo hace sonar cuando la distancia entre el sonido anterior y el
	 * actual es de 40 ticks.
	 * 
	 */

	public void hacerSonar(int contador) {
		if (this.sonando == 3 && contador > this.sonandoDesde + 40) {
			Herramientas.play("rsc/sonidos/caminar" + String.valueOf(this.sonando) + ".wav");
			this.sonando = 1;
			this.sonandoDesde = contador;
		}

		else if (this.sonando < 3 && contador > this.sonandoDesde + 40) {

			Herramientas.play("rsc/sonidos/caminar" + String.valueOf(this.sonando) + ".wav");
			this.sonando++;
			this.sonandoDesde = contador;

		}

	}

	/*
	 * Saltar
	 * 
	 * La función saltar se encarga de la parte de un salto que se ejecuta una sola
	 * vez. Es decir que no se encarga de la animación de subida o caida a lo largo
	 * de los ticks de un salto normal.
	 * 
	 * Se le debe indicaar el entorno y el contador de ticks actual.
	 * 
	 * Cambia el dibujo de caminar por el salto, según hacia que lado este mirando
	 * el personaje. Cambia el estado de estaSaltando a verdadero. Ejecuta el sonido
	 * del salto. Indica el tick en el cual se realizó el salto, guardando el valor
	 * en tiempoSalto.
	 * 
	 */

	public void saltar(Entorno entorno, int contador) {

		if (this.ultima == entorno.TECLA_DERECHA) {

			entorno.dibujarImagen(saltandoDerecha, this.posx, this.posy, 0, 0.090);

			this.tiempoSalto = contador;
			this.estaSaltando = true;
			Herramientas.play("rsc/sonidos/jump.wav");

		} else {

			entorno.dibujarImagen(saltandoIzquierda, this.posx, this.posy, 0, 0.090);

			this.tiempoSalto = contador;
			this.estaSaltando = true;
			Herramientas.play("rsc/sonidos/jump.wav");

		}

	}

	/*
	 * Saltando
	 * 
	 * Esta función se encarga de manipular, a lo largo del tiempo, lo que ocurre
	 * con el personaje cuando no está en el suelo.
	 * 
	 * Se la llama por cada tick.
	 * 
	 * Requiere el entorno, el contador actual y el arreglo con las vigas.
	 * 
	 * Si el momento actual se produce con menos de 30 ticks de diferencia, entonces
	 * hay que elevar 1px al jugador (restar 1 en eje 'y').
	 * 
	 * De lo contrario analiza si NO está pisando alguna viga. Si no está pisando
	 * vigas, entonces debe descender un pixel por cada tick, hasta que pise alguna
	 * viga.
	 */

	public void saltando(Entorno entorno, int contador, Viga[] suelos) {
		if (this.estaEnEscalera == false && this.estado.equals("vivo")) {

			if (estaSaltando && contador - this.tiempoSalto < 30) {

				if (this.ultima == entorno.TECLA_DERECHA) {

					this.posy = this.posy - 1;
					entorno.dibujarImagen(saltandoDerecha, this.posx, this.posy, 0, 0.090);

				} else {

					this.posy = this.posy - 1;
					entorno.dibujarImagen(saltandoIzquierda, this.posx, this.posy, 0, 0.090);

				}

			} else {

				this.estaSaltando = false;

				if (pisando(entorno, suelos) == -1) {

					if (this.ultima == entorno.TECLA_DERECHA) {

						this.posy = this.posy + 1;
						entorno.dibujarImagen(saltandoDerecha, this.posx, this.posy, 0, 0.090);
					}

					else {

						this.posy = this.posy + 1;
						entorno.dibujarImagen(saltandoIzquierda, this.posx, this.posy, 0, 0.090);

					}

				}

			}
		}
	}

	/*
	 * Pisando
	 * 
	 * Esta funcion devuelve el indice que ocupa la viga en el arreglo de suelos. Si
	 * no se encuentra pisando, entonces devuelve -1.
	 * 
	 * Requiere que se entregue el entorno y el arreglo de vigas como parámetros.
	 * 
	 * Para saber si no está pisando la viga, el centro 'y' del personaje + 20
	 * pixeles (para llegar al pie del personaje) pies() debe poseer un valor
	 * distinto para la coordenada 'y' donde comienza cada viga (la posy - 12px)
	 * (int)suelos[i].dondeEmpiezaElSuelo().
	 * 
	 * En el caso de que el personaje se encuentra pisando la viga. Queda por
	 * conocer si se encuentra dentro de todos los puntos 'x' que conforman el largo
	 * de la viga.
	 * 
	 * Por eso la función analiza que el extremo derecho de la viga, sea pisada por
	 * al menos el lateral izquierdo del personaje, y lo mismo de forma invertida.
	 * Si no se cumple esta condición, el personaje está cayendo por estar fuera de
	 * la viga a pesar de estar a la altura de alguna de ellas.
	 * 
	 * 
	 * 
	 */

	public int pisando(Entorno entorno, Viga[] suelos) {

		if (this.estaEnEscalera == false) {
			for (int i = 0; i < suelos.length; i++) {

				if (this.pies() == (int) suelos[i].dondeEmpiezaElSuelo()) {

					if (this.lateralDerecho() < suelos[i].extremoIzquierdo()
							|| this.lateralIzquierdo() > suelos[i].extremoDerecho()) {
						this.estaCayendo = true;
						return -1;

					} else {
						this.estaCayendo = false;
						return i;

					}
				}

			}
			this.estaCayendo = true;
			return -1;

		} else {
			return this.enEscalera;
		}

	}

	// Devuelve un entero con el valor que ocupan los pies del personaje en el eje
	// 'y'

	public int pies() {
		return this.posy + 20;
	}

	public int cabeza() {
		return this.posy - 20;
	}

	/*
	 * Dibujar
	 * 
	 * Esta función detecta las teclas presionadas y según condiciones ejecuta las
	 * acciones que debe realizar el personaje.
	 * 
	 * Se la debe llamar en cada tick
	 * 
	 * Recibe como parámetro el entorno y el momento actual medido en ticks.
	 * 
	 * Como prioridad, deteca si el usuario solicita saltar, presionando la
	 * espaciadora. Pero solo permite ejecutar dicha acción, si desde la última vez
	 * que saltó pasaron más de 60 tics (lo que requiere como mínimo un salto). Y a
	 * su vez, que el personaje no esté cayendo.
	 * 
	 * 
	 * Continúa evaluando si se presionan las teclas derecha e izquierda y ejecuta
	 * dichos movimientos, pero sólo si el personaje no está saltando ni tampoco
	 * esta cayendo. ## Este juego no permite desplazarse de izq a der mientras se
	 * está en el aire.
	 * 
	 * Sólo permite desplazarse a los costados, si el jugador no sale de pantalla.
	 * 
	 * Luego, si ninguna tecla está siendo presionada, deja al jugador mirando hacia
	 * el lado que corresponde según el último movimiento.
	 */

	public void dibujar(Entorno entorno, int contador, Escaleras[] escaleras) {

		if (!this.estado.equals("vivo")) {
			this.posy = this.posy + 3;
			entorno.dibujarImagen(saltandoDerecha, this.posx, this.posy, 90, 0.090);

		} else {

			// Unica forma de saltar (saltando siempre que no haya sido muy pronto desde el
			// salto anterior y no se esté cayendo
			if (entorno.sePresiono(entorno.TECLA_ESPACIO) && this.tiempoSalto + 60 < contador
					&& this.estaCayendo == false && this.estaEnEscalera == false) {

				this.saltar(entorno, contador);

			}

			// unica forma de pasar a estar dentro de una escalera (estando cerca de una
			// escalera pero no dentro de una)
			if (this.estaCercaEscalera == true && this.estaEnEscalera == false) {

				if (entorno.sePresiono(entorno.TECLA_ARRIBA)
						&& this.pies() > escaleras[this.enEscalera].extremoSuperior()) {

					this.subirEscaleras(entorno, escaleras);
				}

				else if (entorno.sePresiono(entorno.TECLA_ABAJO)
						&& this.pies() < escaleras[this.enEscalera].extremoInferior()) {

					this.bajarEscaleras(entorno, escaleras);
				}

			}

			// unica forma de moverse ya dentro de una escalera (estar cerca de una y ya
			// dentro de una)
			if (this.estaCercaEscalera == true && this.estaEnEscalera == true) {

				if (entorno.estaPresionada(entorno.TECLA_ARRIBA)) {

					this.subirEscaleras(entorno, escaleras);
				}

				else if (entorno.estaPresionada(entorno.TECLA_ABAJO)) {

					this.bajarEscaleras(entorno, escaleras);
				} else {

					entorno.dibujarImagen(subiendo_quieto, this.posx, this.posy, 0, 0.090);

				}
			}

			// unica forma de moverse de izquierda a derecha (no estar cayendo ni saltando
			// ni dentro de una escalera)
			if (this.estaCayendo == false && this.estaSaltando == false && this.estaEnEscalera == false) {

				// caminar a derecha
				if (entorno.estaPresionada(entorno.TECLA_DERECHA)) {

					if (this.posx <= 790) {
						this.posx = this.posx + 2;
					}

					entorno.dibujarImagen(caminandoDerecha, this.posx, this.posy, 0, 0.090);
					hacerSonar(contador);
					this.ultima = entorno.TECLA_DERECHA;
				}

				// caminar a izquierda
				else if (entorno.estaPresionada(entorno.TECLA_IZQUIERDA)) {

					if (this.posx >= 10) {
						this.posx = this.posx - 2;
					}

					entorno.dibujarImagen(caminandoIzquierda, this.posx, this.posy, 0, 0.090);
					hacerSonar(contador);
					this.ultima = entorno.TECLA_IZQUIERDA;
				}

				// mirar hacia el ultimo lado caminado
				else {

					if (this.ultima == entorno.TECLA_DERECHA) {
						entorno.dibujarImagen(mirandoDerecha, this.posx, this.posy, 0, 0.090);
					} else {
						entorno.dibujarImagen(mirandoIzquierda, this.posx, this.posy, 0, 0.090);
					}

				}

			}
		}

	}

	/*
	 * Esta función cambia el valor de estaCercaEscalera a true o false dependiendo
	 * si el personaje está cerca de una escalera como para poder subir o descender
	 * por ella.
	 * 
	 * Esta función debe llamarse en cada tick del juego pero sólo si el personaje
	 * no se encuentra dentro de una escalera actualmente.
	 * 
	 */

	public void estoyCercaDeEscalera(Entorno entorno, Escaleras[] escaleras, Viga[] suelos) {

		int hallado = 0;
		int i = pisando(entorno, suelos);

		// Sólo analiza la proximidad de una escalera, si la función pisando devuelve el
		// índice de la viga pisada.
		// No se analiza proximidad para valores -1 (en el aire) ni si se está cayendo.
		if (i != -1 && this.estaCayendo == false) {

			// Comprobación de escaleras para todos los pisos excepto el último
			if (i != suelos.length - 1) {

				// Se analiza una escalera que comienza en el piso actual y sube al próximo
				if ((escaleras[i].extremoInferior() - this.pies() <= 5)) {

					if (escaleras[i].lateralDerecho() >= this.posx && escaleras[i].lateralIzquierdo() <= this.posx) {
						this.estaCercaEscalera = true;
						this.enEscalera = i;

						hallado += 1;
					}
				}
			}

			// Comprobación de escaleras para todos los pisos excepto la planta baja
			if (i != 0) {
				if (escaleras[i - 1].extremoSuperior() - this.pies() <= 10) {

					// Se analiza una escalera que termina en el piso actual y desciende al inferior
					if (escaleras[i - 1].lateralDerecho() >= this.posx
							&& escaleras[i - 1].lateralIzquierdo() <= this.posx) {
						this.estaCercaEscalera = true;
						this.enEscalera = i - 1;

						hallado += 1;
					}
				}
			}

		}

		if (hallado == 0) {
			this.estaCercaEscalera = false;

		}

	}

	/*
	 * Esta función ejecuta las animaciones correspondiente a subir escalera y se
	 * encarga de informar si ya terminó de subirla. Es decir que sale de la
	 * escalera y se encuentra en el piso superior.
	 * 
	 */

	public void subirEscaleras(Entorno entorno, Escaleras[] escaleras) {

		if (this.pies() < escaleras[this.enEscalera].extremoSuperior() && this.estaEnEscalera == true) {
			// entorno.dibujarImagen(subio, this.posx, this.posy, 0, 0.20);
			this.estaEnEscalera = false;

			if (this.enEscalera % 2 == 0) {
				this.ultima = entorno.TECLA_IZQUIERDA;
				entorno.dibujarImagen(mirandoIzquierda, this.posx, this.posy, 0, 0.090);

			} else {
				this.ultima = entorno.TECLA_DERECHA;
				entorno.dibujarImagen(mirandoDerecha, this.posx, this.posy, 0, 0.090);

			}

		} else {

			this.posy = this.posy - 2;
			this.estaEnEscalera = true;
			entorno.dibujarImagen(subiendo, this.posx, this.posy, 0, 0.090);
		}
	}

	/*
	 * Esta función ejecuta las animaciones correspondiente a bajar escalera y se
	 * encarga de informar si ya terminó de descender. Es decir que sale de la
	 * escalera y se encuentra en el piso inferior.
	 * 
	 */

	public void bajarEscaleras(Entorno entorno, Escaleras[] escaleras) {

		if (this.pies() >= escaleras[this.enEscalera].extremoInferior() - 5 && this.estaEnEscalera == true) {
			this.estaEnEscalera = false;

			if (this.enEscalera % 2 == 0) {
				this.ultima = entorno.TECLA_DERECHA;
			} else {
				this.ultima = entorno.TECLA_IZQUIERDA;
			}
		} else {

			this.posy = this.posy + 2;
			this.estaEnEscalera = true;
			entorno.dibujarImagen(subiendo, this.posx, this.posy, 0, 0.090);
		}

	}

	public int lateralDerecho() {
		return posx + 15;
	}

	public int lateralIzquierdo() {
		return posx - 15;
	}

	public boolean estaEnEscalera() {
		return this.estaEnEscalera;
	}

	public void morir() {
		this.estado = "muerto";
	}

	/*
	 * Retorna verdadero sólo cuando el jugador se encuentra en una posición x igual
	 * o menor a 150 y a la vez en la última viga del arreglo (donde se encuentra
	 * donkey).
	 */

	public boolean ganar(Entorno entorno, Viga[] suelos) {
		if (this.pisando(entorno, suelos) == suelos.length - 1 && this.lateralIzquierdo() <= 150) {
			return true;
		} else {
			return false;
		}

	}

	public boolean saltandoBarril(Barril barril) {

		if ((this.posx + 1 == barril.centroX() || this.posx - 1 == barril.centroX() || this.posx == barril.centroX())
				&& this.pies() - barril.superior() <= 0 && this.pies() - barril.superior() > -50
				&& barril.fueSaltado() == false && this.estaEnEscalera == false) {
			barril.saltado();
			return true;

		}

		else {
			return false;
		}

	}

}
