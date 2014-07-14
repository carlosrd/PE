package interfaz;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;

@SuppressWarnings("serial")
public abstract class Tablero extends Canvas {

	protected final int tamCasilla = 15;		// Las casillas son de 15x15
	protected int tamTablero;
	
	@Override
	public void paint(Graphics g){

		g.setColor(new Color(0,0,0));		// Set color negro
		g.fillRect(0,0,600,600);			// Pintar rectangulo negro que contendra las casillas
		
		// Pintamos lineas delimitadoras de casillas
		pintarDelimitadoresCasillas(g);
		
		// Pintar las fichas de los aliens, misil y defensor
		dibujarExtra(g);					
	}
	
	// Es abstracto, se implementara en la clase que herede (TableroInvasores)
	public abstract void dibujarExtra(Graphics g); 
	
	public void pintarDelimitadoresCasillas(Graphics g){
	
		int actX = 0;
		int actY = 0;

		// Seteamos el color a blanco para las lineas
		g.setColor(new Color(255,255,255));
		
		for (int i = 0; i < tamTablero; i++){
			
			// Pintar lineas verticales
			g.drawLine(actX + tamCasilla * i, 0, actX + tamCasilla * i, tamCasilla*tamTablero*2);
			actX += tamCasilla;
			
			// Pintar lineas horizontales
			g.drawLine(0, actY + tamCasilla * i,  tamCasilla*tamTablero*2, actY + tamCasilla * i);
			actY += tamCasilla;

		}
		
	}
	
	
}


