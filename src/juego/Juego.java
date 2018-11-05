package juego;

import java.awt.Color;

import entorno.Entorno;
import entorno.Herramientas;
import entorno.InterfaceJuego;

public class Juego extends InterfaceJuego {
	
	// El objeto Entorno que controla el tiempo y otros
	private Entorno entorno;

	// Puede ser "juganddo", "ganado" o "perdido"
	private String estadoDelJuego = "jugando";

		// Creación del arreglo de vigas
	static Viga suelos[] = new Viga[] {

			new Viga(1), new Viga(2), new Viga(3), new Viga(4), new Viga(5), new Viga(6)

	};

	// Creación del arreglo de escaleras
	static Escaleras escaleras[] = new Escaleras[] { 
			new Escaleras(0, suelos), new Escaleras(1, suelos),
			new Escaleras(2, suelos), new Escaleras(3, suelos),
			new Escaleras(4, suelos), new Escaleras(5, suelos),
			new Escaleras(6, suelos), new Escaleras(7, suelos),
			new Escaleras(8, suelos), new Escaleras(9, suelos) };

	// Antagonista y Personaje principal
	private Donkey donkeyKong = new Donkey();
	private Personaje jugador = new Personaje();

	// Puntuador
	private Puntaje puntuador = new Puntaje();

	// El reloj medido en ticks
	int contador = 0;

	// Creación del arreglo de barriles
	private Barril barriles[] = new Barril[]{
			new Barril(suelos[suelos.length - 3]), 
			null, null, null, null, null,
			null, null, null, null, null, 
			null, null,	null, null, null, 
			null, null, null, null

	};

	// ...

	Juego() {
		// Inicializa el objeto entorno
		this.entorno = new Entorno(this, "Donkey - Grupo Pereira - Sanchez - Tula - V2", 800, 600);

		// Inicializar lo que haga falta para el juego
		// ...

		Herramientas.loop("rsc/sonidos/musica.wav");
		
		// Inicia el juego!
		this.entorno.iniciar();

	}

	/**
	 * Durante el juego, el método tick() será ejecutado en cada instante y por lo
	 * tanto es el método más importante de esta clase. Aquí se debe actualizar el
	 * estado interno del juego para simular el paso del tiempo (ver el enunciado
	 * del TP para mayor detalle).
	 */
	public void tick() {
		

		// Al inicio de cada ciclo aumentar una unidad el reloj
		contador++;
		

				
		// Ejecuta la función dibujar por cada miembro del arreglo de vigas.
		for (int i = 0; i < suelos.length; i++) {
			suelos[i].dibujar(entorno);
		}

		// Ejecuta la función dibujar por cada miembro del arreglo de escaleras.
		for (int i = 0; i < escaleras.length; i++) {
			escaleras[i].dibujar(entorno);
		}

		// Ejecuta la función dibujar para donkey
		donkeyKong.gorilear(entorno, contador);

		// Ejecuta la función dibujar para el contador
		puntuador.dibujar(entorno);
		

		// Ejecuta la función dibujar por cada elemento no NULL del arreglo de barriles
		// , también analiza si un barril debe destruirse.
		for (int i = 0; i < barriles.length; i++) {
			if (barriles[i] != null) {

				barriles[i].dibujar(entorno, contador, suelos, escaleras);
				if (barriles[i].deboDestruirme(entorno, suelos)) {
					barriles[i] = null;
				}

			}
		}



		/*
		 * Analisis que ocurren mientras el juego se desarrolla
		 * Es decir que el jugador no ganó ni perdió aún.
		 */
		if (this.estadoDelJuego.equals("jugando")) {

			
			// Analizar si el personaje se encuentra cerca de escalera
			jugador.estoyCercaDeEscalera(escaleras, suelos);

			
			
			/*
			 * CAER
			 * NO escalera, NO saltando, NO pisando
			 */
			if (!jugador.obtenerEstaEnEscalera() && !jugador.obtenerEstaSaltando() && jugador.pisando(suelos) == -1) {

				
				jugador.cambiarImagen("saltando");
				jugador.cambiarY(1);

			}

			
			/*
			 * SALTAR (Parte del proceso de saltar de una única ejecución)
			 * Si presionada tecla espacio, Salto anterior dista más de 60 ticks, No pisando, No está en Escalera
			 */
			if (entorno.sePresiono(entorno.TECLA_ESPACIO) && jugador.obtenerMomentoDeSalto() + 60 < contador
					&& jugador.pisando(suelos) != -1 && !jugador.obtenerEstaEnEscalera()) {

				/*
				 * Esta es la parte de un salto que se ejecuta una sola vez. Es decir que no se
				 * encarga de la animación de subida o caida a lo largo de los ticks de un salto
				 * normal.
				 * 
				 * Cambia el estado de estaSaltando a verdadero. Ejecuta el sonido
				 * del salto. Indica el tick en el cual se realizó el salto, guardando el valor
				 * en tiempoSalto.
				 * 
				 */
				jugador.cambiarImagen("saltando");
				jugador.cambiarMomentoDeSalto(contador);
				jugador.cambiarEstaSaltando(true);
				Herramientas.play("rsc/sonidos/jump.wav");

			}

			
			/*
			 * INGRESAR A ESCALERA
			 * La única forma de pasar a estar dentro de una escalera (estando cerca de una
			 * escalera pero no dentro de una)
			 */
			if (jugador.obtenerEstaCercaEscalera() && !jugador.obtenerEstaEnEscalera()) {

				// Entrar subiendo la escalera
				if (entorno.sePresiono(entorno.TECLA_ARRIBA) && jugador
						.obtenerPosPies() > escaleras[jugador.obtenerSubidoAEscaleraNro()].extremoSuperior()) {

					jugador.cambiarEstaEnEscalera(true);
					jugador.cambiarY(-2);
					jugador.cambiarImagen("subiendo");

					

				}

				// Entrar bajando la escalera
				else if (entorno.sePresiono(entorno.TECLA_ABAJO) && jugador
						.obtenerPosPies() < escaleras[jugador.obtenerSubidoAEscaleraNro()].extremoInferior()) {

					jugador.cambiarEstaEnEscalera(true);
					jugador.cambiarY(2);
					jugador.cambiarImagen("subiendo");

					

				}
			}

			
			/*
			 * Moverse dentro de una escalera. Estando dentro de una.
			 */
			if (jugador.obtenerEstaEnEscalera()) {

				
				/*
				 * SUBIR ESCALERA
				 * Esta función ejecuta las animaciones correspondiente a subir escalera y se
				 * encarga de informar si ya terminó de subirla. Es decir que sale de la
				 * escalera y se encuentra en el piso superior.
				 * 
				 */
				if (entorno.estaPresionada(entorno.TECLA_ARRIBA)) {

					// Subió tanto la esclaera que salió al piso superior
					if (jugador.obtenerPosPies() < escaleras[jugador.obtenerSubidoAEscaleraNro()].extremoSuperior()) {

						// Ya no está en escalera
						jugador.cambiarEstaEnEscalera(false);

						// Para las vigas con indice par, el personaje debe ir hacia izquierda. Para las impares, hacia la derecha.
						if (jugador.obtenerSubidoAEscaleraNro() % 2 == 0) {
							jugador.cambiarMiraDerecha(false);
						} else {
							jugador.cambiarMiraDerecha(true);
						}

					}

					// Si aún no salió de escalera, solamente se desplaza hacia arriba.
					else {

						jugador.cambiarY(-2);
						jugador.cambiarEstaEnEscalera(true);

					}

				}

				/*
				 * BAJAR ESCALERA
				 * Esta función ejecuta las animaciones correspondiente a subir escalera y se
				 * encarga de informar si ya terminó de subirla. Es decir que sale de la
				 * escalera y se encuentra en el piso superior.
				 * 
				 */
				else if (entorno.estaPresionada(entorno.TECLA_ABAJO)) {

					// Bajó tanto la esclaera que salió al piso inferior
					if (jugador.obtenerPosPies() >= escaleras[jugador.obtenerSubidoAEscaleraNro()].extremoInferior()
							- 5) {

						// Ya no está en escalera
						jugador.cambiarEstaEnEscalera(false);

						// Para las vigas con indice par, el personaje debe ir hacia izquierda. Para las impares, hacia la derecha.
						if (jugador.obtenerSubidoAEscaleraNro() % 2 == 0) {
							jugador.cambiarMiraDerecha(false);
						} else {
							jugador.cambiarMiraDerecha(true);
						}

						// Si aún no salió de escalera, solamente se desplaza hacia abajo.
					} else {

						jugador.cambiarY(2);
						jugador.cambiarEstaEnEscalera(true);

					}

					
					
					/*
					 * Si está en escalera pero el usuario no presionó ni la tecla Arriba ni la tecla abajo, se queda inmóvil.
					 */
				} else {

					jugador.cambiarImagen("quieto");

				}

			}

			
			
			/*
			 * SALTO
			 * Resto de la animación del salto.
			 * 
			 * Si no está en escalera, 
			 * Si se está saltando y el momento actual dista a menos de 30 ticks del inicio del salto:
			 * Se está en la parte ascendente del salto.
			 * 
			 * Sino, ya no se está saltando (Se informa que ya no se está saltando).
			 * La caida se produce por el primer llamado CAER
			 * 
			 */
			if (!jugador.obtenerEstaEnEscalera()) {

				if (jugador.obtenerEstaSaltando() && contador - jugador.obtenerMomentoDeSalto() < 30) {

					jugador.cambiarImagen("saltando");
					jugador.cambiarY(-1);

				} else {
					jugador.cambiarEstaSaltando(false);

				}
			}

			
			/*
			 * DESPLAZARSE
			 * Solo cuando no se cae, no se está saltando y no está en escalera
			 */
			if (!jugador.obtenerEstaCayendo() && !jugador.obtenerEstaSaltando() && !jugador.obtenerEstaEnEscalera()) {

				// Moverse a Derecha
				if (entorno.estaPresionada(entorno.TECLA_DERECHA) && jugador.lateralDerecho() <= 800) {
					jugador.cambiarX(2);
					jugador.cambiarImagen("caminando");
					jugador.hacerSonar(contador);
					jugador.cambiarMiraDerecha(true);

				}

				// Moverse a izquierda
				else if (entorno.estaPresionada(entorno.TECLA_IZQUIERDA) && jugador.lateralIzquierdo() >= 0) {

					jugador.cambiarX(-2);
					jugador.cambiarImagen("caminando");
					jugador.hacerSonar(contador);
					jugador.cambiarMiraDerecha(false);

				}

				// No moverse (en el suelo)
				else {

					jugador.cambiarImagen("mirando");

				}

			}

			/*
			 * Luego de analizar todas las posibles situaciones cambiantes por cada tick en el personaje
			 * Se ejecuta la función dibujar, que toma el estado de ciertas variables para producir
			 * la imagen correcta del personaje.
			 */
			jugador.dibujar(entorno, 0);
			
			
			/*
			 * GANAR
			 * Acercarse a determinada posición del juego (sin haber perdido)
			 * Genera la victoria automática.
			 */
			if (jugador.pisando(suelos) == suelos.length - 1 && jugador.lateralIzquierdo() <= 150) {
				this.estadoDelJuego = "ganado";
				puntuador.ganar();
			}

			
			/*
			 * PERDER
			 * Si la función que reporta si el personaje tocó algún barril da verdadero
			 * Se genera la derrota automática.
			 */
			if (jugador.tocando(barriles)) {
				this.estadoDelJuego = "perdido";
			}

			/*
			 * PUNTUACIÓN
			 * Por cada barril se analiza si el jugador lo saltó.
			 * Para la puntuación.
			 */
			for (int i = 0; i < barriles.length; i++) {
				if (barriles[i] != null) {
					if (jugador.saltandoBarril(barriles[i])) {
						puntuador.saltarbarril();
					}

				}
			}

			
			/*
			 * Donkey arrojando barriles
			 * 
			 * Si donkey decide arrojar un barril en el tick actual, se crea uno nuevo en la primera
			 * posició no NULL del arreglo de barriles.
			 */
			if (donkeyKong.decidir(contador)) {
				int creados = 0;

				for (int i = 0; i < barriles.length && creados == 0; i++) {
					if (barriles[i] == null) {
						barriles[i] = new Barril(suelos[suelos.length + donkeyKong.arribaOabajo()]);
						creados = 1;
					}
				}
			}

		}

		
		/*
		 * Analisis que ocurren mientras el juego está ganado
		*/
		else if (this.estadoDelJuego.equals("ganado")) {

			donkeyKong.noMasViolencia();

			jugador.cambiarImagen("mirando");
			jugador.dibujar(entorno, 0);

			entorno.dibujarRectangulo(400, 300, 200, 75, 0, Color.GREEN);
			entorno.cambiarFont("terminal", 20, Color.WHITE);
			entorno.escribirTexto("G A N A S T E", 335, 310);

			/*
			 * Analisis que ocurren mientras el juego está perdido
			*/
		} else {
			entorno.dibujarRectangulo(400, 300, 200, 75, 0, Color.GREEN);
			entorno.cambiarFont("terminal", 20, Color.WHITE);
			entorno.escribirTexto("G A M E   O V E R", 315, 310);

			jugador.cambiarY(3);
			jugador.cambiarImagen("saltando");
			jugador.dibujar(entorno, 90);

			donkeyKong.noMasViolencia();
		}
		

		
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Juego juego = new Juego();
	}
}
