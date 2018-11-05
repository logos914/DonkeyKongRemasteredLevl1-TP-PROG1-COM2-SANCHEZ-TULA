package juego;

import entorno.Entorno;
import entorno.Herramientas;
import entorno.InterfaceJuego;

public class Juego extends InterfaceJuego {
	// El objeto Entorno que controla el tiempo y otros
	private Entorno entorno;
	boolean juegoPerdido = false;
	boolean juegoGanado = false;

	// Variables y métodos propios de cada grupo

	// Creación del arreglo de vigas
	static Viga suelos[] = new Viga[] {

			new Viga(1), new Viga(2), new Viga(3), new Viga(4), new Viga(5), new Viga(6)

	};

	static Escaleras escaleras[] = new Escaleras[] { new Escaleras(0, suelos), new Escaleras(1, suelos),
			new Escaleras(2, suelos), new Escaleras(3, suelos), new Escaleras(4, suelos) };

	Donkey donkeyKong = new Donkey();
	Personaje jugador = new Personaje(suelos[0]);
	Mensajes terminal = new Mensajes();
	Puntaje puntuador = new Puntaje();

	int contador = 0;

	Barril barriles[] = new Barril[]

	{ new Barril(suelos[suelos.length - 3]), null, null, null, null, null, null, null, null, null, null, null, null,
			null, null, null, null, null, null, null

	};

	// ...

	Juego() {
		// Inicializa el objeto entorno
		this.entorno = new Entorno(this, "Donkey - Grupo Pereira - Sanchez - Tula - V1", 800, 600);

		// Inicializar lo que haga falta para el juego
		// ...

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
		// Procesamiento de un instante de tiempo
		// ...

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

		// Ejecuta la función dibujar para el jugador
		jugador.dibujar(entorno, contador, escaleras);

		puntuador.dibujar(entorno);

		// Ejecuta la función que analiza si el jugador está EN una escalera.
		if (jugador.estaEnEscalera() == false) {
			jugador.estoyCercaDeEscalera(entorno, escaleras, suelos);
		}

		// Ejecuta la función que analiza si el jugador está EN el medio de un salto o
		// caida.
		jugador.saltando(entorno, contador, suelos);

		// Contador de tiempo, medido en ticks
		contador = contador + 1;

		// Si donkey decidió arrojar, se crea un nuevo barril en la primera posición
		// NULL del arreglo de barriles.
		if (donkeyKong.decidir(contador)) {
			int creados = 0;

			for (int i = 0; i < barriles.length && creados == 0; i++) {
				if (barriles[i] == null) {
					barriles[i] = new Barril(suelos[suelos.length + donkeyKong.arribaOabajo()]);
					creados = 1;
				}
			}
		}

		// Ejecuta la función dibujar por cada elemento no NULL del arreglo de barriles
		// , también analiza si un barril debe destruirse.

		for (int i = 0; i < barriles.length; i++) {
			if (barriles[i] != null) {

				barriles[i].dibujar(entorno, contador, suelos);
				if (barriles[i].deboDestruirme(entorno, suelos)) {
					barriles[i] = null;
				}

			}
		}

		// Es la función que indica si el jugador está tocando algún barril y por lo
		// tanto game over si es verdadero.

		if (jugador.tocando(barriles) && juegoPerdido == false) {
			juegoPerdido = true;
		}

		for (int i = 0; i < barriles.length; i++) {
			if (barriles[i] != null) {

				if (jugador.saltandoBarril(barriles[i])) {
					puntuador.saltarbarril();
				}

			}
		}

		if (juegoPerdido) {
			terminal.dibujar("perder", entorno);
			jugador.morir();
			donkeyKong.noMasViolencia();
		}

		if (jugador.ganar(entorno, suelos) && juegoGanado == false) {
			juegoGanado = true;
			puntuador.ganar();
		}

		if (juegoGanado) {
			terminal.dibujar("ganar", entorno);
			donkeyKong.noMasViolencia();
		}

	}

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Juego juego = new Juego();
	}
}
