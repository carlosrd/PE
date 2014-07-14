package interfaz;

import java.awt.Graphics;
import java.awt.Image;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JTextField;

import agenetico.Arbol;
import agenetico.Espacio.tEstado;
import agenetico.EvaluacionArbol;
import agenetico.Ocupa;
import agenetico.Ocupa.tDir;

@SuppressWarnings("serial")
public class TableroInvasores extends Tablero implements Runnable {
	
	// Tiempo de pausa entre actualizaciones del canvas
	private final int PAUSA = 750;
	
	private Arbol arbolEjecutar;
	
	// Array para almacenar las imagenes que representa cada objeto
	private Image imgFichas[];	
	
	// Almacena el espacio de juego
	private tEstado[][] matrizJuego;
	private boolean hayMisil;
	private Ocupa defensor;
	private Ocupa misil;
	private Ocupa alien;
	
	private boolean ganaUsuario;
	
	JTextField visorTiempo;
	JTextField visorAdaptacion;
	JButton botonEjecutar;
	
	public TableroInvasores(){
		
		// Guardamos en el array las 3 imagenes para representar las fichas
		imgFichas = new Image[5];	
		
		//"/resources/icons/game/alien30.png");	
		
		imgFichas[0] = getToolkit().getImage(this.getClass().getResource("/resources/icons/game/alien30.png"));
		imgFichas[1] = getToolkit().getImage(this.getClass().getResource("/resources/icons/game/defensor30.png"));
		imgFichas[2] = getToolkit().getImage(this.getClass().getResource("/resources/icons/game/misil30.png"));
		imgFichas[3] = getToolkit().getImage(this.getClass().getResource("/resources/icons/game/gameOverYouWin.png"));
		imgFichas[4] = getToolkit().getImage(this.getClass().getResource("/resources/icons/game/gameOverYouLose.png"));
		
		// Seteamos el tamaño del tablero
		tamTablero = 20;			

		// Creamos un nuevo espacio de juego aleatorio
		inicializaEspacio();
		
	}

	public void dibujarExtra(Graphics g){
		
		for (int fil = 0; fil < tamTablero; fil++){
			for (int col = 0; col < tamTablero; col++){

				if (matrizJuego[fil][col] == tEstado.ALIEN)
					g.drawImage(imgFichas[0], tamCasilla*col*2, tamCasilla*fil*2, this);
				
				if (matrizJuego[fil][col] == tEstado.DEFENSOR)
					g.drawImage(imgFichas[1], tamCasilla*col*2, tamCasilla*fil*2, this);
				
				if (matrizJuego[fil][col] == tEstado.MISIL)
					g.drawImage(imgFichas[2], tamCasilla*col*2, tamCasilla*fil*2, this);
				
			}
		}
		
		if (esJuegoTerminado()){
			if (ganaUsuario)
				g.drawImage(imgFichas[3], 130,193, this);
			else
				g.drawImage(imgFichas[4], 130, 193, this);
			}
	}
	
	// METODOS: Control de la ejecucion del espacio
	// **********************************************************************
	public void inicializaEspacio(){
	
		matrizJuego = new tEstado[tamTablero][tamTablero];
		
		// Inicializamos el tablero a libre
		for (int i = 0; i < tamTablero; i++)
			for (int j = 0; j < tamTablero; j++)
				matrizJuego[i][j] = tEstado.LIBRE;
		
		misil = new Ocupa();
		hayMisil = false;
		ganaUsuario = false;
		
		Random r = new Random();
		
		// Se genera aleatoriamente la posicion del defensor
		defensor = new Ocupa();
		defensor.x = r.nextInt(tamTablero);
		defensor.y = tamTablero - 1;
		matrizJuego[defensor.y][defensor.x] = tEstado.DEFENSOR;
		
		// Posicion aleatoria del alien entre las 6 primeras filas
		alien = new Ocupa();
		alien.x = r.nextInt(tamTablero);
		alien.y = r.nextInt(6);
		matrizJuego[alien.y][alien.x] = tEstado.ALIEN;
		
		// Direccion del alien tambien aleatoria
		double prob = Math.random();
		
		if (prob > 0.5)
			alien.dir = tDir.ESTE;
		else
			alien.dir = tDir.OESTE;
		
		repaint();						// Refrescamos tablero (poner fichas)

	}
	
	public int avanzarTiempo(){
		
		int distancia = 0;
		boolean alienBaja = false;
		
		// Avance del alien
		switch (alien.dir){
		
			case ESTE: 	matrizJuego[alien.y][alien.x] = tEstado.LIBRE;
						// Se comprueba si esta en el extremo del tablero
						if (alien.x < tamTablero - 1)
							alien.x++;
						else { // Extremo del tablero
							// Baja y cambia de direccion a OESTE
							alien.y++;
							alien.dir = tDir.OESTE;
							alienBaja = true;
						}
						
						break;
						
			case OESTE: matrizJuego[alien.y][alien.x] = tEstado.LIBRE;
						// Se comprueba si esta en el extremo del tablero
						if (alien.x > 0)
							alien.x--;
						else { // Extremo del tablero
							// Baja y cambia de direccion a OESTE
							alien.y++;
							alien.dir = tDir.ESTE;
							alienBaja = true;
						}
						
						break;
		}
		
		matrizJuego[alien.y][alien.x] = tEstado.ALIEN;
		
		// Avance del misil
		if (hayMisil){
			/*
			if (misil.y > 0){
				matrizJuego[misil.y][misil.x] = tEstado.LIBRE;
				misil.y--;
				matrizJuego[misil.y][misil.x] = tEstado.MISIL; 
			}
			else{
				matrizJuego[misil.y][misil.x] = tEstado.LIBRE;
				hayMisil = false;
			}*/
			
			if (misil.y == alien.y && misil.x == alien.x && alienBaja){
				matrizJuego[misil.y][misil.x] = tEstado.MISIL; 
				return distancia;
			}
			else {
				if (misil.y > 0){
					matrizJuego[misil.y][misil.x] = tEstado.LIBRE;
					misil.y--;
					matrizJuego[misil.y][misil.x] = tEstado.MISIL; 
				}
				else{
					matrizJuego[misil.y][misil.x] = tEstado.LIBRE;
					hayMisil = false;
				}
				
				if (misil.y == alien.y && misil.x != alien.x){
					distancia = Math.abs(misil.x - alien.x); 
				}
			}
			
			if (misil.y == alien.y && misil.x != alien.x){
				distancia = Math.abs(misil.x - alien.x); 
			}
		}
		else {
			matrizJuego[misil.y][misil.x] = tEstado.LIBRE;
			hayMisil = false;
		}
		
		return distancia;
	}
	
	public boolean seAlejan(){
		
		boolean seAleja;
		
		// Si yo estoy a la izquierda y el a la derecha 
		if (defensor.x < alien.x){
			if (alien.dir == tDir.ESTE)
				seAleja = true;
			else
				seAleja = false;
		}
		// Si yo estoy a la derecha y el a la izquierda 
		else {
			if (alien.dir == tDir.ESTE)
				seAleja = false;
			else
				seAleja = true;
		} 
		
		return seAleja;
	}
	
	public boolean esJuegoTerminado(){
		
		boolean terminaJuego = false;
		
		if (alien.y == tamTablero - 1){
			
			// El alien ha aterrizado
			ganaUsuario = false;
			terminaJuego = true;
		
		}
		else if (hayMisil && alien.x == misil.x && alien.y == misil.y){
			
				// El alien es alcanzado por el misil
				ganaUsuario = true;
				terminaJuego = true;
		
		}
		
		return terminaJuego;
		
	}
	
	public EvaluacionArbol interpretaCodigoArbol(Arbol arbol, EvaluacionArbol evArbol) throws InterruptedException{
		
		// Recuperamos los valores pasados por parametro
		int tiempo = evArbol.getTiempo();
		int distancia = evArbol.getDistancia();
		//boolean termina = evArbol.getFinDeJuego();
		
		// Auxiliares
		int valor1 = 0;
		int valor2 = 0;
		int valor3 = 0;
		
		EvaluacionArbol evAux1;
		EvaluacionArbol evAux2;
		EvaluacionArbol evAux3;
		
		// Si es Hoja es un operando, sino un operador
		if (arbol.esHoja()){
			
			if (arbol.getDato().equals("FUEGO")){
				
				// Si no hay misil, lanzamos uno desde nuestra posicion
				if (!hayMisil){
					
					hayMisil = true;
					misil = new Ocupa();
					
					// Misil en la parte superior del defensor
					misil.x = defensor.x;
					misil.y = defensor.y - 1;
					matrizJuego[misil.y][misil.x] = tEstado.MISIL;	

				}
				
				// Avanza el tiempo y actualizamos la distancia
				tiempo++;
				distancia += avanzarTiempo();
				
				repaint();
				
				Thread.sleep(PAUSA);
				
				// Este nodo evalua a 0
				valor1 = 0;
				
			}
			else if (arbol.getDato().equals("IZQUIERDA")){
				
					// Si no esta en el borde, avanzamos a la izquierda
					if (defensor.x > 0){
						matrizJuego[defensor.y][defensor.x] = tEstado.LIBRE;
						defensor.x--;
						matrizJuego[defensor.y][defensor.x] = tEstado.DEFENSOR;
					}
						
				
					// Avanza el tiempo y actualizamos la distancia
					tiempo++;
					distancia += avanzarTiempo();
					
					repaint();
					
					Thread.sleep(PAUSA);
					
					// Este nodo evalua a 0
					valor1 = 0;
					
			}
			else if (arbol.getDato().equals("DERECHA")){
					
					// Si no esta en el borde, avanzamos a la derecha
					if (defensor.x < tamTablero - 1){
						matrizJuego[defensor.y][defensor.x] = tEstado.LIBRE;
						defensor.x++;
						matrizJuego[defensor.y][defensor.x] = tEstado.DEFENSOR;
					}
				
					// Avanza el tiempo y actualizamos la distancia
					tiempo++;
					distancia += avanzarTiempo();
					
					repaint();
					
					Thread.sleep(PAUSA);
					
					// Este nodo evalua a 0
					valor1 = 0;
				
			}
			else if (arbol.getDato().equals("DIST-Y")){
				
				// Distancia vertical actual del alienigena a la base del tablero
				valor1 = (tamTablero - 1 ) - alien.y;
				
			}
			else if (arbol.getDato().equals("DIST-X")){
				
				// Distancia horizontal al defensor
				valor1 = Math.abs(alien.x - defensor.x);
				
				// Si se acerca, tomamos valor positivo, y negativo si se aleja
				if (seAlejan())
					valor1 = - valor1;
			}
			
			evAux1 = new EvaluacionArbol(valor1, tiempo, distancia);
			
			return evAux1; 	// return valor1
		
		} 
		else {
			// Es operador
			if (arbol.getDato().equals("IF")){
				
				// Creamos un objeto encapsulado con los valores actuales
				//evArbol = new EvaluacionArbol(0, tiempo, distancia, termina);
				
				// Le pasamos los valores anteriores y recuperamos el valor actualizado
				evAux1 = interpretaCodigoArbol(arbol.getHijoIzquierdo(), evArbol);
				
				valor1 = evAux1.getValor();
				
				// Ejecutamos el argumento que determinara cual de los otros 2 ejecutamos. Lo tomamos
				// como una condicion booleana, aunque esto solo es cierto si este argumento es un EQ
				if (!esJuegoTerminado()){
					
					// Esto es: Si "arg1" se cumple (es 1), entonces, vamos por "arg2", sino, por "arg3"
					if (valor1 != 0)
						return interpretaCodigoArbol(arbol.getHijoCentral(), evAux1);
					else
						return interpretaCodigoArbol(arbol.getHijoDerecho(), evAux1);
				}
				else 
					return evAux1; 		// return valor1
					
			
			}
			else if (arbol.getDato().equals("PROGN2")){
				
				// Ejecutamos primer argumento
				evAux1 = interpretaCodigoArbol(arbol.getHijoIzquierdo(), evArbol);
				
				// Recogemos el valor devuelto
				valor1 = evAux1.getValor();
				
				// Si el juego no ha acabado, ejecutamos el segundo
				if (!esJuegoTerminado()){
					evAux2 = interpretaCodigoArbol(arbol.getHijoDerecho(), evAux1);
					valor2 = evAux2.getValor();
					
					// Actualizamos el valor
					evAux2.setValor(valor1 + valor2);
					
					return evAux2;		// return valor1 + valor2
				}
				
				// Si llegamos aqui, es que no se evaluo el hijo derecho, asi que
				// valor2 = 0, por lo tanto, con devolver evAux1 ya sirve
				return evAux1;

			}
			else if (arbol.getDato().equals("PROGN3")){
				
				// Ejecutamos primer argumento
				evAux1 = interpretaCodigoArbol(arbol.getHijoIzquierdo(), evArbol);
				
				// Recogemos el valor devuelto
				valor1 = evAux1.getValor();
				
				// Si el juego no ha acabado, ejecutamos el segundo
				if (!esJuegoTerminado()){
					
					 evAux2 = interpretaCodigoArbol(arbol.getHijoCentral(), evAux1);
					 valor2 = evAux2.getValor();
					 
				}
				else
					// Si ha terminado, no hace falta ni evaluar el hijo 2 ni el 3 y devolvemos
					// directamente lo del primer hijo
					return evAux1;
				
				// Si el juego no ha acabado, ejecutamos el tercero
				if (!esJuegoTerminado()){
					
					 evAux3 = interpretaCodigoArbol(arbol.getHijoDerecho(), evAux2);
					 valor3 = evAux3.getValor();
				
					// Actualizamos el valor
					evAux3.setValor(valor1 + valor2 + valor3);
					
					return evAux3;		// return valor1 + valor2 + valor3
				
				}
				
				// Si llega aqui, es porque se ejecuto el hijo 2, pero se termino el juego, por
				// lo que no se ejecuta el hijo 3 y valor3 es 0. Actualizamos el valor
				evAux2.setValor(valor1 + valor2);
				
				return evAux2;

			}
			else if (arbol.getDato().equals("EQ")){
				
				evAux1 = interpretaCodigoArbol(arbol.getHijoIzquierdo(), evArbol);
				valor1 = evAux1.getValor();
				
				if (!esJuegoTerminado()){
					
					evAux2 = interpretaCodigoArbol(arbol.getHijoDerecho(), evAux1);
					valor2 = evAux2.getValor();
					
					if (valor1 == valor2)
						evAux2.setValor(1);		// return 1
					else
						evAux2.setValor(0);		// return 0
					
					return evAux2;	
				
				}
				else {
					
					if (valor1 == valor2)
						evAux1.setValor(1);		// return 1
					else
						evAux1.setValor(0);		// return 0
					
					return evAux1;	
					
				}
				
			} // else "EQ"
			
		} // else
		
		// Por construccion, hasta este "return" no se deberia llegar; deberia salir por 
		// cualquiera de los que esta en los ifs. Esta colocado aqui porque Java lo exige
		return null;
		
	} // evaluarArbol()
	
	public void preparaEjecucion(Arbol a, JTextField vTiempo, JTextField vAdaptacion, JButton bEjecutar){
		arbolEjecutar = a;
		visorTiempo = vTiempo;
		visorAdaptacion = vAdaptacion;
		botonEjecutar = bEjecutar;
	}
	
	/**
	 * Este metodo ejecuta en un thread aparte la simulacion paso a paso en el canvas
	 * Necesario para no congelar la interfaz de usuario (no se mostraria la simualcion)
	 */
	@Override
	public void run() {
		
		botonEjecutar.setEnabled(false);
		visorTiempo.setEnabled(true);
		visorAdaptacion.setEnabled(true);
		
		// Creamos un nuevo espacio de juego aleatorio
		int tiempo = 0;
		int distancia = 0;
		int tiempoAnterior = 0;
		int adaptacion = 0;
		
		visorTiempo.setText(""+tiempo);
		visorAdaptacion.setText(""+adaptacion);
		
		EvaluacionArbol evArbol;
		
		while (!esJuegoTerminado()){
			tiempoAnterior = tiempo;
			distancia = 0;
			// Creamos un objeto para encapsular los valores de entrada y salida
			evArbol = new EvaluacionArbol(0, tiempo, distancia);
			
			// Introducimos el objeto anterior creado y recogemos el que devuelva la funcion
			try {
				evArbol = interpretaCodigoArbol(arbolEjecutar, evArbol);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// Actualizamos los valores encapsulados devueltos por la funcion
			tiempo = evArbol.getTiempo();
			distancia = evArbol.getDistancia();
			
			if (tiempoAnterior == tiempo){
				tiempo++;
				distancia += avanzarTiempo();
			}
			
			adaptacion += distancia;
			
			visorTiempo.setText(""+tiempo);
			visorAdaptacion.setText(""+adaptacion);
		}
		
		if (!ganaUsuario)
			adaptacion += 200;
		
		visorTiempo.setText(""+tiempo);
		visorAdaptacion.setText(""+adaptacion);
		
		visorTiempo.setEnabled(false);
		visorAdaptacion.setEnabled(false);
		botonEjecutar.setEnabled(true);
		
	}
	
	
	
}