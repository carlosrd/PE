package agenetico;

import java.util.Random;

import agenetico.Ocupa.tDir;

public class Espacio {

	// ATRIBUTOS
	// **************************************************************************************
	
	private final int MAX_X = 20;
	private final int MAX_Y = 20;
	
	public enum tEstado {LIBRE, MISIL, ALIEN, DEFENSOR};
	
	private tEstado[][] tablero;
	private boolean hayMisil;
	private Ocupa defensor;
	private Ocupa misil;
	private Ocupa alien;
	
	private boolean ganaUsuario;
	
	// CONSTRUCTORAS
	// **************************************************************************************
	
	/**
	 * Construye un nuevo espacio de juego, colocando aleatoriamente al defensor y al alien
	 */
	public Espacio(){
		
		tablero = new tEstado[MAX_X][MAX_Y];
		
		// Inicializamos el tablero a libre
		for (int i = 0; i < MAX_X; i++)
			for (int j = 0; j < MAX_Y; j++)
				tablero[i][j] = tEstado.LIBRE;
		
		misil = new Ocupa();
		hayMisil = false;
		ganaUsuario = false;
		
		Random r = new Random();
		
		// Se genera aleatoriamente la posicion del defensor
		defensor = new Ocupa();
		defensor.x = r.nextInt(MAX_X);
		defensor.y = MAX_Y - 1;
		tablero[defensor.y][defensor.x] = tEstado.DEFENSOR;
		
		// Posicion aleatoria del alien entre las 6 primeras filas
		alien = new Ocupa();
		alien.x = r.nextInt(MAX_X);
		alien.y = r.nextInt(6);
		tablero[alien.y][alien.x] = tEstado.ALIEN;
		
		// Direccion del alien tambien aleatoria
		double prob = Math.random();
		
		if (prob > 0.5)
			alien.dir = tDir.ESTE;
		else
			alien.dir = tDir.OESTE;
		

			
	}
	
	// CONSULTORAS
	// **************************************************************************************
	
	public boolean ganaUsuario(){
		return ganaUsuario;
	}
	
	// METODOS
	// **************************************************************************************
	
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
		
		if (alien.y == MAX_Y - 1){
			
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
	
	public int avanzarTiempo(){
		
		int distancia = 0;
		boolean alienBaja = false;
		
		// Avance del alien
		switch (alien.dir){
		
			case ESTE: 	tablero[alien.y][alien.x] = tEstado.LIBRE;
						// Se comprueba si esta en el extremo del tablero
						if (alien.x < MAX_X - 1)
							alien.x++;
						else { // Extremo del tablero
							// Baja y cambia de direccion a OESTE
							alien.y++;
							alien.dir = tDir.OESTE;
							alienBaja = true;
						}
						
						break;
						
			case OESTE: tablero[alien.y][alien.x] = tEstado.LIBRE;
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
		
		tablero[alien.y][alien.x] = tEstado.ALIEN;
		
		// Avance del misil
		if (hayMisil){
			
			if (misil.y == alien.y && misil.x == alien.x && alienBaja){
				tablero[misil.y][misil.x] = tEstado.MISIL; 
				return distancia;
			}
			else {
				if (misil.y > 0){
					tablero[misil.y][misil.x] = tEstado.LIBRE;
					misil.y--;
					tablero[misil.y][misil.x] = tEstado.MISIL; 
				}
				else{
					tablero[misil.y][misil.x] = tEstado.LIBRE;
					hayMisil = false;
				}
				
				if (misil.y == alien.y && misil.x != alien.x){
					distancia = Math.abs(misil.x - alien.x); 
				}
			}

		}
		else {
			//tablero[misil.y][misil.x] = tEstado.LIBRE;
			hayMisil = false;
		}
		
		return distancia;
	}
	
	public EvaluacionArbol evaluaArbol(Arbol arbol, EvaluacionArbol evArbol){
		
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
					tablero[misil.y][misil.x] = tEstado.MISIL;
				}
				
				// Avanza el tiempo y actualizamos la distancia
				tiempo++;
				distancia += avanzarTiempo();
				
				// Este nodo evalua a 0
				valor1 = 0;
				
			}
			else if (arbol.getDato().equals("IZQUIERDA")){
				
					// Si no esta en el borde, avanzamos a la izquierda
					if (defensor.x > 0)
						defensor.x--;
				
					// Avanza el tiempo y actualizamos la distancia
					tiempo++;
					distancia += avanzarTiempo();
					
					// Este nodo evalua a 0
					valor1 = 0;
					
			}
			else if (arbol.getDato().equals("DERECHA")){
					
					// Si no esta en el borde, avanzamos a la derecha
					if (defensor.x < MAX_X - 1)
						defensor.x++;
				
					// Avanza el tiempo y actualizamos la distancia
					tiempo++;
					distancia += avanzarTiempo();
					
					// Este nodo evalua a 0
					valor1 = 0;
				
			}
			else if (arbol.getDato().equals("DIST-Y")){
				
				// Distancia vertical actual del alienigena a la base del tablero
				valor1 = (MAX_Y - 1 ) - alien.y;
				
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
				evAux1 = evaluaArbol(arbol.getHijoIzquierdo(), evArbol);
				
				valor1 = evAux1.getValor();
				
				// Ejecutamos el argumento que determinara cual de los otros 2 ejecutamos. Lo tomamos
				// como una condicion booleana, aunque esto solo es cierto si este argumento es un EQ
				if (!esJuegoTerminado()){
					
					// Esto es: Si "arg1" se cumple (es 1), entonces, vamos por "arg2", sino, por "arg3"
					if (valor1 == 1)
						return evaluaArbol(arbol.getHijoCentral(), evAux1);
					else
						return evaluaArbol(arbol.getHijoDerecho(), evAux1);
				}
				else 
					return evAux1; 		// return valor1
					
			
			}
			else if (arbol.getDato().equals("PROGN2")){
				
				// Ejecutamos primer argumento
				evAux1 = evaluaArbol(arbol.getHijoIzquierdo(), evArbol);
				
				// Recogemos el valor devuelto
				valor1 = evAux1.getValor();
				
				// Si el juego no ha acabado, ejecutamos el segundo
				if (!esJuegoTerminado()){
					evAux2 = evaluaArbol(arbol.getHijoDerecho(), evAux1);
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
				evAux1 = evaluaArbol(arbol.getHijoIzquierdo(), evArbol);
				
				// Recogemos el valor devuelto
				valor1 = evAux1.getValor();
				
				// Si el juego no ha acabado, ejecutamos el segundo
				if (!esJuegoTerminado()){
					
					 evAux2 = evaluaArbol(arbol.getHijoCentral(), evAux1);
					 valor2 = evAux2.getValor();
					 
				}
				else
					// Si ha terminado, no hace falta ni evaluar el hijo 2 ni el 3 y devolvemos
					// directamente lo del primer hijo
					return evAux1;
				
				// Si el juego no ha acabado, ejecutamos el tercero
				if (!esJuegoTerminado()){
					
					 evAux3 = evaluaArbol(arbol.getHijoDerecho(), evAux2);
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
				
				evAux1 = evaluaArbol(arbol.getHijoIzquierdo(), evArbol);
				valor1 = evAux1.getValor();
				
				if (!esJuegoTerminado()){
					
					evAux2 = evaluaArbol(arbol.getHijoDerecho(), evAux1);
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
	
}
