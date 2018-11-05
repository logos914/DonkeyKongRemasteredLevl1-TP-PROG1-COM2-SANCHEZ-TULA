package juego;

import java.awt.Color;
import entorno.Entorno;
import entorno.Herramientas;
import java.awt.Color;

public class Mensajes {

	String mensajePerdedor = "G A M E   O V E R";
	String mensajeGanador = "G A N A S T E";

	public void dibujar(String tipo, Entorno entorno) {

		if (tipo.equals("ganar")) {

			entorno.dibujarRectangulo(400, 300, 200, 75, 0, Color.GREEN);
			entorno.cambiarFont("terminal", 20, Color.WHITE);
			entorno.escribirTexto(mensajeGanador, 335, 310);
		}

		else if (tipo.equals("perder")) {

			entorno.dibujarRectangulo(400, 300, 200, 75, 0, Color.GREEN);
			entorno.cambiarFont("terminal", 20, Color.WHITE);
			entorno.escribirTexto(mensajePerdedor, 315, 310);
		}

	}

}
