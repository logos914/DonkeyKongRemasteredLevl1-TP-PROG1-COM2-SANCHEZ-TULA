package juego;

import juego.Viga;
import java.awt.Image;

import entorno.Entorno;
import entorno.Herramientas;

public class Personaje {

	private int posx;
	private int posy;

	// Se reservan las referencias necesarias para cada animacion utilizada
	private Image mirandoIzquierda;
	private Image mirandoDerecha;
	private Image caminandoIzquierda;
	private Image caminandoDerecha;
	private Image saltandoIzquierda;
	private Image saltandoDerecha;
	private Image subiendo;
	private Image subiendo_quieto;

	// Esta referencia realizará un aliasing a la imagen que debe ser mostrada
	// Se muestra una por vez
	private Image imagenMario;

	private int tiempoSalto; // tick en el cual se ejecutó el último salto (o el salto actual)
	private boolean estaSaltando; // Indica si está saltando (ascendiendo) o no.
	private boolean estaCayendo; // Indica si está cayendo (es decir que sus pies no están tocando viga alguna.

	private boolean estaCercaEscalera; // Indica si el personaje se encuentra lo suficientemente cerca de una escalera
										// (para poder usarla)
	private boolean estaEnEscalera; // Indica si el personaje se encuentra dentro (usando) una escalera
	private int subidoAEscaleraNro; // Indica que el indice que corresponde a la posición de la escalera que se está
									// usando dentro del arreglo de escaleras

	private int sonando; // Ultimo archivo de sonido que se usó para caminar, hay 3 variantes.
	private int sonandoDesde; // tick en el cual se ejecutó el último sonido de caminar (ayuda a evitar que
									// suenen sonidos en cada tick)

	private boolean miraDerecha; // Esta variable indica si el personaje está mirando a derecha o no (Vital para
									// que se cargue la imagen correcta del personaje según los movimientos que
									// indique el usuario)

	public Personaje() {

		// Posición por defecto de spawn
		this.posx = 50;
		this.posy = 530;

		// direcciones URL de las imagenes y animaciones
		this.mirandoIzquierda = Herramientas.cargarImagen("rsc/graficos/marito/mira-izquierda.png");
		this.mirandoDerecha = Herramientas.cargarImagen("rsc/graficos/marito/mira-derecha.png");
		this.caminandoIzquierda = Herramientas.cargarImagen("rsc/graficos/marito/camina-izquierda.gif");
		this.caminandoDerecha = Herramientas.cargarImagen("rsc/graficos/marito/camina-derecha.gif");
		this.saltandoIzquierda = Herramientas.cargarImagen("rsc/graficos/marito/salta-izquierda.png");
		this.saltandoDerecha = Herramientas.cargarImagen("rsc/graficos/marito/salta-derecha.png");
		this.subiendo = Herramientas.cargarImagen("rsc/graficos/marito/subiendo.gif");
		this.subiendo_quieto = Herramientas.cargarImagen("rsc/graficos/marito/quieto_subiendo.png");

		// Por defecto no hubo salto y no momentos anterior al cero. Tampoco el
		// personaje esta saltando, cayendo ni en una escalera ni cerca de alguna.
		this.tiempoSalto = 0;
		this.estaSaltando = false;
		this.estaCayendo = false;
		this.estaEnEscalera = false;
		this.estaCercaEscalera = false;
		
		this.sonando = 1;
		this.sonandoDesde = 0;

		// Por defecto la imagen a mostrarse es mirando a derecha
		this.imagenMario = this.mirandoDerecha;

		// Por defecto debe mirar a derecha
		this.miraDerecha = true;
	}

	
	/*
	 * Es la función que indica si el jugador está tocando el barril pasado como parámetro
	 */
	public boolean tocando(Barril[] barriles) {

		for (int i = 0; i < barriles.length; i++) {

			if (barriles[i] != null) {

				if (this.lateralDerecho() - barriles[i].lateralIzquierdo() > 0
						&& this.lateralIzquierdo() - barriles[i].lateralIzquierdo() < 0
						&& this.obtenerPosPies() - barriles[i].superior() >= 3
						&& this.obtenerPosCabeza() - barriles[i].pies() <= -10) {

					System.out.println("[" + i + "] Colision Derecha");
					return true;

				}

				if (this.lateralIzquierdo() - barriles[i].lateralDerecho() < 0
						&& this.lateralDerecho() - barriles[i].lateralDerecho() > 0
						&& this.obtenerPosPies() - barriles[i].superior() >= 3
						&& this.obtenerPosCabeza() - barriles[i].pies() <= -10) {
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
	 * Pisando
	 * 
	 * Esta funcion devuelve el indice que ocupa la viga en el arreglo de suelos. Si
	 * no se encuentra pisando, entonces devuelve -1.
	 * 
	 * Requiere que se entregue el entorno y el arreglo de vigas como parámetros.
	 * 
	 * Para saber si no está pisando la viga, el centro 'y' del personaje + 20
	 * pixeles (para llegar al pie del personaje) obtenerPosPies() debe poseer un
	 * valor distinto para la coordenada 'y' donde comienza cada viga (la posy -
	 * 12px) (int)suelos[i].dondeEmpiezaElSuelo().
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

	public int pisando(Viga[] suelos) {

		if (this.obtenerEstaEnEscalera() == false) {
			for (int i = 0; i < suelos.length; i++) {

				if (this.obtenerPosPies() == (int) suelos[i].dondeEmpiezaElSuelo()) {

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

			if (this.subidoAEscaleraNro > 4) {
				return this.subidoAEscaleraNro - 5;
			} else {
				return this.subidoAEscaleraNro;
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

	public void estoyCercaDeEscalera(Escaleras[] escaleras, Viga[] suelos) {

		int hallado = 0;
		int i = pisando(suelos);

		// Sólo analiza la proximidad de una escalera, si la función pisando devuelve el
		// índice de la viga pisada.
		// No se analiza proximidad para valores -1 (en el aire) (se está cayendo).
		if (i != -1 && this.estaCayendo == false) {

			// Comprobación de escaleras para todos los pisos excepto el último
			if (i != suelos.length - 1) {

				// Se analiza una escalera que comienza en el piso actual y sube al próximo
				if ((escaleras[i].extremoInferior() - this.obtenerPosPies() <= 5)) {

					if (escaleras[i].lateralDerecho() >= this.posx && escaleras[i].lateralIzquierdo() <= this.posx) {
						this.estaCercaEscalera = true;
						this.subidoAEscaleraNro = i;

						hallado += 1;
					}
				}

				// Se analiza una escalera adicional que comienza en el piso actual y sube al
				// próximo o quizas no sube del todo
				if ((escaleras[i + 5].extremoInferior() - this.obtenerPosPies() <= 5)) {

					if (escaleras[i + 5].lateralDerecho() >= this.posx
							&& escaleras[i + 5].lateralIzquierdo() <= this.posx) {
						this.estaCercaEscalera = true;
						this.subidoAEscaleraNro = i + 5;

						hallado += 1;
					}
				}

			}

			// Comprobación de escaleras para todos los pisos excepto la planta baja
			if (i != 0) {
				if (escaleras[i - 1].extremoSuperior() - this.obtenerPosPies() <= 10) {

					// Se analiza una escalera que termina en el piso actual y desciende al inferior
					if (escaleras[i - 1].lateralDerecho() >= this.posx
							&& escaleras[i - 1].lateralIzquierdo() <= this.posx) {
						this.estaCercaEscalera = true;
						this.subidoAEscaleraNro = i - 1;

						hallado += 1;
					}
				}

				if (escaleras[i + 4].extremoSuperior() - this.obtenerPosPies() <= 10) {

					// Se analiza una escalera adicional que termina en el piso actual y desciende
					// al inferior
					if (escaleras[i + 4].lateralDerecho() >= this.posx
							&& escaleras[i + 4].lateralIzquierdo() <= this.posx) {
						this.estaCercaEscalera = true;
						this.subidoAEscaleraNro = i + 4;

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
	 * Realiza los calculos geométricos para saber que un barril fue correctamente
	 * saltado.
	 */

	public boolean saltandoBarril(Barril barril) {

		if ((this.posx + 1 == barril.centroX() || this.posx - 1 == barril.centroX() || this.posx == barril.centroX())
				&& this.obtenerPosPies() - barril.superior() <= 0 && this.obtenerPosPies() - barril.superior() > -50
				&& barril.fueSaltado() == false && this.obtenerEstaEnEscalera() == false) {
			
			Herramientas.play("rsc/sonidos/salta_barril.wav");
			barril.saltado();
			return true;

		} else {
			return false;
		}

	}

	/*
	 * Dibuja al personaje. Se deben calcular las situaciones y cambiar las
	 * variables previamente con otros métodos.
	 */

	public void dibujar(Entorno entorno, int rotacion) {
		entorno.dibujarImagen(imagenMario, this.posx, this.posy, rotacion, 0.090);

	}

	/*
	 * Devuelven la posicion extrema lateral correspondiente
	 */
	public int lateralDerecho() {
		return posx + 15;
	}

	public int lateralIzquierdo() {
		return posx - 15;
	}

	/*
	 * Setters de las posiciones X e Y
	 */
	public void cambiarY(int pixeles) {
		this.posy = this.posy + pixeles;
	}

	public void cambiarX(int pixeles) {
		this.posx = this.posx + pixeles;
	}

	/*
	 * Devuelven la posicion extremas verticales correspondiente
	 */

	public int obtenerPosPies() {
		return this.posy + 20;
	}

	public int obtenerPosCabeza() {
		return this.posy - 20;
	}

	/*
	 * Getters
	 */

	public boolean obtenerEstaEnEscalera() {
		return this.estaEnEscalera;
	}

	public int obtenerMomentoDeSalto() {
		return this.tiempoSalto;
	}

	public boolean obtenerEstaCayendo() {
		return this.estaCayendo;
	}

	public boolean obtenerEstaCercaEscalera() {
		return this.estaCercaEscalera;
	}

	public int obtenerSubidoAEscaleraNro() {
		return this.subidoAEscaleraNro;
	}

	public boolean obtenerMiraDerecha() {
		return this.miraDerecha;
	}

	public boolean obtenerEstaSaltando() {
		return this.estaSaltando;
	}

	/*
	 * Setters
	 */

	public void cambiarMomentoDeSalto(int i) {
		this.tiempoSalto = i;

	}

	public void cambiarEstaEnEscalera(boolean escalera) {
		this.estaEnEscalera = escalera;
	}

	public void cambiarMiraDerecha(boolean mira) {
		this.miraDerecha = mira;
	}

	public void cambiarEstaSaltando(boolean salta) {
		this.estaSaltando = salta;
	}

	/*
	 * Ayudan a cambiar por la imagen correcta, según el String indicado, y según a
	 * que lado esté mirando el personaje
	 */
	public void cambiarImagen(String s) {

		if (s.equals("mirando") && !this.imagenMario.equals(mirandoIzquierda) && !this.miraDerecha) {
			imagenMario = mirandoIzquierda;
		}

		else if (s.equals("mirando") && !this.imagenMario.equals(mirandoDerecha) && this.miraDerecha) {
			imagenMario = mirandoDerecha;
		}

		else if (s.equals("caminando") && !this.imagenMario.equals(caminandoIzquierda) && !this.miraDerecha) {
			imagenMario = caminandoIzquierda;
		}

		else if (s.equals("caminando") && !this.imagenMario.equals(caminandoDerecha) && this.miraDerecha) {
			imagenMario = caminandoDerecha;
		}

		else if (s.equals("saltando") && !this.imagenMario.equals(saltandoIzquierda) && !this.miraDerecha) {
			imagenMario = saltandoIzquierda;
		}

		else if (s.equals("saltando") && !this.imagenMario.equals(saltandoDerecha) && this.miraDerecha) {
			imagenMario = saltandoDerecha;
		}

		else if (s.equals("subiendo") && !this.imagenMario.equals(subiendo)) {
			imagenMario = subiendo;
		} else if (s.equals("quieto") && !this.imagenMario.equals(subiendo_quieto)) {
			imagenMario = subiendo_quieto;
		}

	}

}
