package agenetico;

import agenetico.AGenetico.tInicializacion;

public class Cromosoma {

	// ATRIBUTOS
	// **************************************************************************************
	protected Arbol arbol; 				// Estrategia de rastreo
	protected int maxProfundidad;		// Limite maximo de profundidad en los arboles
	
	public double aptitud;				// Función de evaluación fitness adaptación);
	protected double puntuacion; 		// Puntuación relativa(aptitud/suma)
	protected double puntAcumulada;		// Puntuación acumulada para selección
	
	private int MAX_PARTIDAS;			// Numero de partidas a jugar para evaluar un cromosoma
	private final int PENALIZA_ALIEN = 200;	// Valor de penalizacion si perdemos la partida
	
	public int evaluacion;		// Contiene una evaluacion del cromosoma (para mantener un valor estable)

	// CONSTRUCTORAS
	// **************************************************************************************
	
	public Cromosoma(int maxProfundidad, int maxPartidas, tInicializacion tipoInicializacion){
		
		this.maxProfundidad = maxProfundidad;
		this.MAX_PARTIDAS = maxPartidas;
		
		arbol = new Arbol(maxProfundidad);
		
		switch (tipoInicializacion){
		
			case Completa: 	arbol.inicializacionCompleta(0);
						   	break;
						   
			case Creciente: arbol.inicializacionCreciente(0);
							break;
		}
		
		evaluacion = evaluaCromosoma2();
		
	}
	
	// Constructora por copia
	public Cromosoma(Cromosoma c){

		this.arbol = new Arbol(c.arbol);
		this.MAX_PARTIDAS = c.MAX_PARTIDAS;
		this.aptitud = c.aptitud;
		this.puntuacion = c.puntuacion;
		this.puntAcumulada = c.puntAcumulada;

		// Para forzar reevaluacion, ya que las copias suelen ser en los cruces y mutaciones
		this.evaluacion = Integer.MAX_VALUE;
	}
	
	// CONSULTORAS
	// **************************************************************************************
	
	public Arbol getArbol(){
		return arbol;
	}
	
	// METODOS 
	// **************************************************************************************
	
	/**
	 * Evaluara el cromosoma si el valor de evaluacion esta sin inicializar (contiene MAX_VALUE)
	 * y sino, se salta el paso de evaluar y devuele el valor almacenado
	 * @return
	 */
	public int evaluaCromosoma(){
		
		if (evaluacion == Integer.MAX_VALUE)
			evaluacion = evaluaCromosoma2();
		
		return evaluacion;
	}
	
	/**
	 * Evalua el cromosoma segun la funcion dada a minimizar
	 * @return
	 */
	public int evaluaCromosoma2(){
		
		Espacio espacioJuego;
		
		int tiempo, tiempoAnterior;
		int distancia;
		
		int adaptacion = 0;
		
		EvaluacionArbol evArbol;
		
		for (int numPartidas = 0; numPartidas < MAX_PARTIDAS; numPartidas++){
			
			// Creamos un nuevo espacio de juego aleatorio
			espacioJuego = new Espacio();
			tiempo = 0;
			distancia = 0;
			
			while (!espacioJuego.esJuegoTerminado()){
				tiempoAnterior = tiempo;
				distancia = 0;
				// Creamos un objeto para encapsular los valores de entrada y salida
				evArbol = new EvaluacionArbol(0, tiempo, distancia);
				
				// Introducimos el objeto anterior creado y recogemos el que devuelva la funcion
				evArbol = espacioJuego.evaluaArbol(arbol, evArbol);
				
				// Actualizamos los valores encapsulados devueltos por la funcion
				tiempo = evArbol.getTiempo();
				distancia = evArbol.getDistancia();
				
				if (tiempoAnterior == tiempo){
					tiempo++;
					distancia += espacioJuego.avanzarTiempo();
				}
				
				adaptacion += distancia;
			}
			
			if (!espacioJuego.ganaUsuario())
				adaptacion += PENALIZA_ALIEN;
			
			// PENALIZA NO FUEGOS
		//	if (!arbolConFuego())
			//	adaptacion += PENALIZA_ALIEN;
			
		}
		
		return adaptacion;
	}

	/*
	private boolean arbolConFuego(){
		
		ArrayList<Arbol> nodosTerminales = arbol.buscarTerminales();
		
		boolean exito = false;
		for (int i = 0; i < nodosTerminales.size() && !exito; i++){
			
			if (nodosTerminales.get(i).getDato().equals("FUEGO"))
				exito = true;
		}
		
		return exito;
	}
	*/

}
