package agenetico;

import java.util.ArrayList;
import java.util.Random;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import org.math.plot.Plot2DPanel;



public class AGenetico extends SwingWorker<Void,Void> {//implements Runnable{

	// ATRIBUTOS
	// **************************************************************************************

	// Estructuras / Parametros basicos
	// --------------------------------------------
	private Cromosoma[] pob; 				// Población
	private int tamPoblacion; 				// Tamaño población
	private int maxGeneraciones; 			// Número máximo de generaciones
	private double probCruce;	 			// Probabilidad de cruce
	private double probMutacion; 			// Probabilidad de mutación

	private int numGeneracion;				// Generacion actual
	
	private int maxProfundidad;				// Maxima profundidad permitida a los arboles
	
	private int maxPartidas;				// Almacena el numero maximo de partidas para evaluar el cromosoma
	
	// Seleccion Ranking
	// --------------------------------------------
	private double beta;
	
	// Atributos para la mostrar las graficas / panel resultados
	// --------------------------------------------
	private double[] mejorAptitudPorGeneracion;
	private double[] mediaAptitud;
	private double[] mejorAptitudAbsoluto;
	private double peorAptitud;
	
	private int totalCruces;
	private int totalMutaciones;
	private int totalInversiones;

	
	// Evaluacion poblacion
	// --------------------------------------------
	private Cromosoma mejorCromosoma; 		// Mejor individuo
	private int posMejor; 					// Posición del mejor cromosoma
	
	// Elitismo
	// --------------------------------------------
	private Cromosoma[] elite;
	private Cromosoma[] copiaOrdenada;
	private int[] indicesPob;
	
	// Enumerados
	// --------------------------------------------
	public enum tInicializacion{Completa, Creciente, Hibrida};
	tInicializacion tipoInicializacion;
	
	public enum tSeleccion{Ruleta, Torneo, Ranking};
	tSeleccion tipoSeleccion;
	
	public enum tMutacion {Terminal, Funcional, Hibrida, SubArbol};
	tMutacion tipoMutacion;
	

	// Parametros ejecucion AGenetico (necesarios para el Thread)
	// --------------------------------------------
	double elitismo;
	double probInversion;
	
	JProgressBar barraProgreso;
	JLabel etqProgreso;

	Plot2DPanel plot;
	
	JTextField txtMejorAptitud;
	JTextField txtMediaAptitud;
	JTextField txtPeorAptitud;
	
	JTextField txtTotalCruces;
	JTextField txtTotalMutaciones;
	JTextField txtTotalInversiones;

	JTextArea areaTextoCodigo;
	JTextArea visorNumLineasCodigo;
	
	// CONSTRUCTORA
	// **************************************************************************************
	
	/**
	 * Crea un algoritmo genetico nuevo con los parametros pasados desde el formulario
	 * @param tamPoblacion - Tamaño de la poblacion a evaluar
	 * @param maxGeneraciones - Numero maximo de generaciones (iteraciones) de la poblacion
	 * @param probCruce - Probabilidad de cruce
	 * @param probMutacion - Probabilidad de mutación
	 * @param tolerancia - Precisión del resultado
	 * @param tamMatriz 
	 * @param flujo 
	 * @param distancia 
	 */
	public AGenetico(int tamPoblacion, 
					 int maxGeneraciones, 
					 double probCruce, 
					 double probMutacion,
					 int maxProfundidad,
					 int maxPartidas,
					 tInicializacion tipoInicializacion,
					 tSeleccion tipoSeleccion,
					 tMutacion tipoMutacion){
		
		// Inicializacion de los valores pasados por el formulario
		
		this.tamPoblacion = tamPoblacion;
		this.maxGeneraciones = maxGeneraciones;
		this.probCruce = probCruce;
		this.probMutacion = probMutacion;

		this.maxProfundidad = maxProfundidad;
		this.maxPartidas = maxPartidas;
		
		// Arrays para mostrar la grafica
		mejorAptitudPorGeneracion = new double[maxGeneraciones];
		mediaAptitud = new double[maxGeneraciones];
		mejorAptitudAbsoluto = new double[maxGeneraciones];
		peorAptitud = Integer.MIN_VALUE;
		
		this.tipoInicializacion = tipoInicializacion;
		this.tipoSeleccion = tipoSeleccion;
		this.tipoMutacion = tipoMutacion;
		
	}
	
	
	// CONSULTORAS
	// **************************************************************************************
	
	/**
	 * Devuelve la generacion actual procesada
	 * @return
	 */
	public Cromosoma getMejorCromosomaAbsoluto(){
		return mejorCromosoma;	
	}
	
	/**
	 * Devuelve la generacion actual procesada
	 * @return
	 */
	public int getNumGeneracionActual(){
		return numGeneracion;	
	}
	
	/**
	 * Devuelve el numero maximo de generaciones a los que se somete la población
	 * @return Numero maximo de generaciones
	 */
	public int getMaxGeneraciones(){
		return maxGeneraciones;
	}
	
	/**
	 * Devuelve un array con la mejor aptitud en cada generacion
	 * @return Array de aptitudes mejores por generacion
	 */
	public double[] getMejorAptitudPorGeneracion() {
		return mejorAptitudPorGeneracion;
	}

	/**
	 * Devuelve un array con la media de la aptitud en cada generacion
	 * @return Array de medias de aptitudes por generacion
	 */
	public double[] getMediaAptitud() {
		return mediaAptitud;
	}

	/**
	 * Devuelve un array con la mejor aptitud durante toda la ejecucion
	 * @return Array con la mejor aptitud absoluta del algoritmo
	 */
	public double[] getMejorAptitudAbsoluto() {
		return mejorAptitudAbsoluto;
	}

	/**
	 * Devuelve la peor aptitud absoluta de todas las generaciones
	 * @return
	 */
	public double getPeorAptitud(){
		return peorAptitud;
	}
	
	/**
	 * Devuelve el total de cruces hecho en toda la ejecucion
	 * @return
	 */
	public int getTotalCruces(){
		return totalCruces;
	}
	
	/**
	 * Devuelve el total de mutaciones hechas en toda la ejecucion
	 * @return
	 */
	public int getTotalMutaciones(){
		return totalMutaciones;
	}
	
	/**
	 * Devuelve el total de inversiones hechas en toda la ejecucion
	 * @return
	 */
	public int getTotalInversiones(){
		return totalInversiones;
	}
	
	
	// OPERADORES ALGORITMO GENETICO
	// **************************************************************************************
	
	/**
	 * Crea la poblacion inicial y la inicializa segun el tipo de cromosoma indicado (numero de funcion) y
	 * el numero de variables seleccionado (solo se usa en el caso de la funcion 4
	 * @param tipoCromosoma - Funcion a ejecutar (1..5)
	 * @param numVariables - Valor de "n" para la funcion 4; para el resto viene definido como constante
	 */
	private void poblacionInicial(){
		
		// Creamos una nueva poblacion de cromosomas con el tamaño indicado
		pob = new Cromosoma[tamPoblacion];
		
		switch (tipoInicializacion){
		
			case Hibrida:	inicializacionRampedAndHalf();
							break;
							
			default:		for (int i = 0; i < tamPoblacion; i++){
								pob[i] = new Cromosoma(maxProfundidad, maxPartidas, tipoInicializacion);
								pob[i].aptitud = pob[i].evaluaCromosoma();
							}	
							break;
		}

		System.out.println("APT1: " + pob[0].aptitud);

		System.out.println("APT2: " + pob[0].evaluaCromosoma());

		System.out.println("APT3: " + pob[0].evaluaCromosoma());

		System.out.println("APT4: " + pob[0].evaluaCromosoma());
	}
	
	/**
	 * Realiza una inicialiacion hibrida entre la inicializacion Completa y la Creciente (Ramped & Half)
	 * 	* Sea D la profundidad maxima elegida por el usuario
	 * 	* Se divide la poblacion en D-1
	 * 	* Asignamos a cada grupo una profundidad: 2, 3, ... , D
	 * 	* En cada grupo, inicializamos la mitad con ini Creciente y la otra mitad con ini Completa
	 */
	private void inicializacionRampedAndHalf(){
		
		// D = Profundidad Maxima
	
		// Se separa la poblacion en D-1 grupos
		int numGrupos = maxProfundidad - 1;
		
		// Calculamos cuantos elementos caeran en cada grupo
		int tamGrupo = tamPoblacion / numGrupos;
		
		// Calculamos cuanto es la mitad del grupo: La mitad tiene
		// inicializacion Creciente y la otra mitad Completa
		int mitadGrupo = tamGrupo / 2;
		
		// A cada grupo le asignamos 2, 3, ... , D de profundidad
		int profActual = 2;
		
		// Los restantes que no entren en ningun grupo (porque el num de elem sea impar)
		// los inicializamos con inicializacion Completa y la maxima profundidad elegida por el usario
		int restantes = tamPoblacion % numGrupos;
		
		// Procesamos la poblacion entera
		for (int i = 0; i < tamPoblacion; i++){
			
			// Si pertenecen a algun grupo (no es de los restantes)
			if (i < ((tamPoblacion - 1) - restantes) ){
				
				// Procesamos el grupo actual
				for (int j = 0; j < tamGrupo; j++){
					
					// Si esta en la primera mitad del grupo, inicializacion Completa, sino, Creciete
					if (j < mitadGrupo)
						pob[i] = new Cromosoma(profActual, maxPartidas, tInicializacion.Completa);
					else
						pob[i] = new Cromosoma(profActual, maxPartidas, tInicializacion.Creciente);
					
					// Evaluamos el individuo recien creado
					pob[i].aptitud = pob[i].evaluaCromosoma();
					
					// Avanzamos al siguiente elemento a inicializar
					i++;
					
				}
				
				// Una vez procesado un grupo, aumentamos la profundidad para el siguiente
				profActual++;
				
				// Restamos uno porque el anterior bucle cuenta uno de mas
				i--;
			}
			// Si no es un grupo, es de los restantes. Por defecto: ini Completa a Max Profundidad elegida por el usuario
			else{
				pob[i] = new Cromosoma(maxProfundidad, maxPartidas, tInicializacion.Completa);
				
				// Evaluamos el individuo recien creado
				pob[i].aptitud = pob[i].evaluaCromosoma();
			}
			
			
		}
		
		
	}

	/**
	 * Evalua a la poblacion actual
	 */
	private void evaluarPoblacion(){
		
		/* Esquema de funcion:
		 * -------------------------------------------------------------------
		 * 	private void evaluarPoblacion() {
		 * 		real punt_acu = 0; // puntuación acumulada
		 * 		real aptitud_mejor = 0; // mejor aptitud
		 * 		real sumaptitud = 0; // suma de la aptitud
		 * 		. . .
		 * 		para cada i desde 0 hasta tam_pob hacer {
		 * 			sumaptitud = sumaptitud + poblacion[i].aptitud;
		 * 			si (poblacion[i].aptitud > aptitud_mejor){
		 * 				pos_mejor = i;
		 * 				aptitud_mejor = poblacion[i].aptitud;
		 * 			}
		 * 		}
		 * 		
		 * 		para cada i desde 0 hasta tam_pob hacer {
		 * 			pob[i].puntuacion = pob[i].aptitud / sumaptitud;
		 * 			pob[i].punt_acu = pob[i].puntuacion + punt_acu;
		 * 			punt_acu = punt_acu + pob[i].puntuacion;
		 * 		} 
		 * 
		 * 		//Si el mejor de esta generación es mejor que el mejor que
		 * 		tenia de antes pues lo actualizo
		 * 			if (aptitud_mejor > mejorCromosoma.dameAptitud()) {
		 * 				mejorCromosoma = pob[pos_mejor]
		 * 			}
		 */
		
		// Variables auxiliares para evaluar la poblacion
		double sumaAptitud = 0 , sumaAptitudAux = 0;		// Suma de la aptitud
		double puntAcumulada = 0;							// Puntuacion acumulada
		double aptitudMejor = Integer.MIN_VALUE;			// Mejor aptitud

		
		// Distinguimos si hay que maximizar o minimar y realizar un desplazamiento de la adaptacion
		
		// Esto es porque hay que distinguir entre funcion de adaptacion y de evaluacion. La primera
		// no puede tomar valores negativos, mientras que la segunda si. Para evitar esto, es necesario
		// una operación de desplazamiento, de manera que un problema de minimizacion, se positivice y
		// se convierta en uno de maximizacion
		
		// La solución está en realizar una modificacion de los valores de la funcion de adaptacion
		// de forma que se obtengan valores positivos y que cuanto menor sea el valor de la funcion
		// (mas cercano al optimo) mayor sea el correspondiente valor revisado.
		
		// Buscamos el peor antes de la revision o dara 0 siempre
		/*for (int i = 0; i < numUnidades; i++){
			
			if (pob[i].aptitud > aptitudPeor)
				aptitudPeor = pob[i].aptitud;
		}*/
		
		revisaAdaptacionMinimizando();

		// Calculamos la suma de la aptitud y la mejor aptitud junto con su posicion
		for (int i = 0; i < tamPoblacion; i++){
			sumaAptitud += pob[i].aptitud;					// Suma aptitudes positivizadas (desplazamiento aplicado)
			sumaAptitudAux += pob[i].evaluaCromosoma();		// Suma aptitutes normales (sin desplazamiento)
			
			if (pob[i].aptitud > aptitudMejor){
				posMejor = i;
				aptitudMejor = pob[i].aptitud;
			}

		}
		
		// Calculamos la puntuacion acumulada
		for (int i = 0; i < tamPoblacion; i++){
			pob[i].puntuacion = pob[i].aptitud / sumaAptitud;
			pob[i].puntAcumulada = pob[i].puntuacion + puntAcumulada;
			puntAcumulada += pob[i].puntuacion;
		}
		
		// Actualizamos el mejor cromosoma si en esta generacion encontramos algun
		// cromosoma mejor o si es la primera generacion y esta sin inicializar
		if (mejorCromosoma == null || aptitudMejor > mejorCromosoma.aptitud){
			mejorCromosoma = new Cromosoma(pob[posMejor]);
			mejorCromosoma.evaluacion = pob[posMejor].evaluacion;
		}
		
		// Actualizacion de los datos para las graficas
		mejorAptitudPorGeneracion[numGeneracion] = pob[posMejor].evaluaCromosoma();
		mediaAptitud[numGeneracion] = sumaAptitudAux / tamPoblacion;
		mejorAptitudAbsoluto[numGeneracion] = mejorCromosoma.evaluaCromosoma();
		
	}
	
		// SELECCION
		// ---------------------------------------------------
	
	/**
	 * Seleccion de los siguientes elementos mediante el metodo de Ruleta
	 */
	private void seleccionRuleta(){
		
		/* Esquema de funcion:
		 * -------------------------------------------------------------------
		 * 	void selecciónRuleta ( pob, nuevaPob, tam_pob) {
		 * 		entero sel_super[tam_pob];//seleccionados para sobrevivir
		 * 		real prob; // probabilidad de seleccion
		 * 		entero pos_super; // posición del superviviente
		 * 		para cada i desde 0 hasta tam_pob hacer {
		 *			prob = alea();		// <- 0.38
		 * 			pos_super = 0;
		 * 			mientras ((prob > pob[pos_super].punt_acu) y (pos_super <tam_pob)) 
		 * 				pos_super++;
		 * 			sel_super[i] = pos_super;
		 * 		}
		 * 
		 * 		// se genera la poblacion intermedia
		 *		para cada i desde 0 hasta tam_pob hacer {
		 * 			copiar (pob[sel_super[i]], nuevaPob);
		 * 		}
		 * }
		 * 
		 */
		
		// Almacenamos los indices de los supervivientes
		int[] selSupervivientes = new int[tamPoblacion];		// Seleccionados para sobrevivir
		
		double prob;									// Probabilidad de seleccion
		int posSuperviviente;
		
		for (int i = 0; i < tamPoblacion; i++){
			prob = Math.random();
			posSuperviviente = 0;
			
			while (prob > pob[posSuperviviente].puntAcumulada && posSuperviviente < tamPoblacion-1)
				posSuperviviente++;
			
			selSupervivientes[i] = posSuperviviente;
		}
		
		
		Cromosoma[] nuevaPob = new Cromosoma[tamPoblacion];
		
		for (int i = 0; i < tamPoblacion; i++)
			nuevaPob[i] = new Cromosoma(pob[selSupervivientes[i]]);	// copia auxiliar
		
		// Trasladamos la copia auxiliar a la poblacion
		for (int i = 0; i < tamPoblacion; i++)
			pob[i] = nuevaPob[i];

	}
	
	/**
	 * Seleccion de los siguientes elementos mediante el metodo de Torneo
	 * La implementacion utilizada es Torneo Deterministico de 3 individuos
	 */
	private void seleccionTorneo(){
		
		/*
		 * 	# Cada elemento de la muestra se toma eligiendo el mejor
		 * 	  de los individuos de un conjunto de z elementos(2 ó 3)
		 * 	  tomados al azar de la población base.
		 * 	# El proceso se repite k veces hasta completar la muestra.
		 */

		// Almacenamos los indices de los supervivientes
		int[] selSupervivientes = new int[tamPoblacion];		// Seleccionados para sobrevivir
	
		// Variables donde almacenar las posiciones de los contrincantes
		int mejor1, mejor2, mejor3;
		
		// Creamos un generador de numeros aleatorios
		Random r = new Random();
		
		// Debemos seleccionar tantos elementos como tenga la poblacion
		for (int i = 0; i < tamPoblacion; i++){
			
			// Tomamos 3 posiciones aleatorias 
			mejor1 = r.nextInt(tamPoblacion); 
			mejor2 = r.nextInt(tamPoblacion); 
			mejor3 = r.nextInt(tamPoblacion); 
			
			// Si la pos 1 es mejor que la 2
			if (pob[mejor1].aptitud > pob[mejor2].aptitud){
				
				// Comprobamos entonces si es mejor la 1 o la 3
				if (pob[mejor1].aptitud > pob[mejor3].aptitud)
					selSupervivientes[i] = mejor1;
				else
					selSupervivientes[i] = mejor3;
			}
			// Sino, la pos 2 es mejor que la 1 y falta enfrentarla a la 3
			else if (pob[mejor2].aptitud > pob[mejor3].aptitud)
					 selSupervivientes[i] = mejor2;
				 else
					 selSupervivientes[i] = mejor3;	
			
		}
		
		Cromosoma[] nuevaPob = new Cromosoma[tamPoblacion];
		
		for (int i = 0; i < tamPoblacion; i++)
			nuevaPob[i] = new Cromosoma(pob[selSupervivientes[i]]);	// copia auxiliar
		
		// Trasladamos la copia auxiliar a la poblacion
		for (int i = 0; i < tamPoblacion; i++)
			pob[i] = nuevaPob[i];

	}
	
	/**
	 * Seleccion de los siguientes individuos mediante el metodo de Ranking
	 */
	private void seleccionRanking(){
			
		/*
		 * 	public Individual[] performRankSelection(Individual[] initPop) {
		 * 		Individual[] sortedPop = SortIndividual.selectionSort(initPop);
		 * 		Individual[] futureParents = new Individual[sortedPop.length];
		 * 		futureParents[0]=sortedPop[0];futureParents[1]=sortedPop[1];
		 * 		int numOfParents =2;
		 * 		double[] fitnessSegments = rankPopulation();
		 * 		double entireSegment = fitnessSegments[fitnessSegments.length-1];
		 * 		while(numOfParents<futureParents.length){
		 * 			double x = (double)(Math.random()*entireSegment);
		 * 			if(x<=fitnessSegments[0]) {
		 * 				// First Idividual was Selected 
		 * 				futureParents[numOfParents]=sortedPop[0];
		 * 				numOfParents++;}
		 * 			else
		 * 				for(int i=1; i<futureParents.length; i++)
		 * 					if(x>fitnessSegments[i-1] && x<=fitnessSegments[i]){
		 * 						// i'th Idividual was Selected 
		 * 						futureParents[numOfParents]=sortedPop[i];
		 * 						numOfParents++;}
		 * 				} return futureParents;
		 * 	}
		 */
		
		// Creamos un array auxiliar para copiar la poblacion que se ordenara
		copiaOrdenada = new Cromosoma[tamPoblacion];
		for (int i = 0; i < tamPoblacion; i++)
			copiaOrdenada[i] = new Cromosoma(pob[i]);
		
		// Se ordena el vector de cromosomas (la poblacion) pero no los indices (eso es del elitismo)
		quickSort(0,tamPoblacion-1, false);
		
		// Copiamos el vector inversamente para ordenarlos por aptitud decreciente (Mejor fitness a la izquierda)
		int j = tamPoblacion -1;
		Cromosoma[] copiaInversa = new Cromosoma[tamPoblacion];
		
		for (int i = 0; i < tamPoblacion; i++){
			copiaInversa[j] = copiaOrdenada[i];
			j--;
		}	
		
		// Creamos el array "futureParents"
		Cromosoma[] padresFuturos = new Cromosoma[tamPoblacion];
		
		padresFuturos[0] = copiaInversa[0];//copiaOrdenada[0];
		padresFuturos[1] = copiaInversa[0];//copiaOrdenada[0];
		
		int numPadres = 2;
		
		double[] fitnessSegments = puntuaPoblacion();
		
		double entireSegment = fitnessSegments[fitnessSegments.length-1];
		
		while (numPadres < padresFuturos.length){
			
			double x = (double)(Math.random()*entireSegment);
			if (x <= fitnessSegments[0]) {
				// First Individual was Selected 
			 	padresFuturos[numPadres] = copiaInversa[0];//copiaOrdenada[0];
			 	numPadres++;
			}
			else
				for (int i = 1; i < padresFuturos.length; i++)
					if( x > fitnessSegments[i-1] && x <= fitnessSegments[i]){
						// i'th Idividual was Selected 
					 	padresFuturos[numPadres] = copiaInversa[i];//copiaOrdenada[0];
					 	numPadres++;	
					}

			for (int i = 0; i < tamPoblacion; i++)
				pob[i] = padresFuturos[i];
		}
	}
	
	/**
	 * Metodo que calcula las puntuaciones del metodo de seleccion Ranking
	 * @return
	 */
	private double[] puntuaPoblacion(){
		
		/*
		 * 	private double[] rankPopulation(){
		 * 		double[] fitnessSegments = new double[populationSize_];
		 * 		for(int i=0 ; i<fitnessSegments.length ; i++){
		 * 			double probOfIth = (double)i/populationSize_;
		 * 			probOfIth = probOfIth*2*(Beta_-1);
		 * 			probOfIth = Beta_ - probOfIth;
		 * 			probOfIth = (double)probOfIth*((double)1/populationSize_);
		 * 			if(i!=0)
		 * 				fitnessSegments[i] = fitnessSegments[i-1] + probOfIth;
		 * 			else
		 * 				fitnessSegments[i] = probOfIth;
		 * 		}
		 * 		return fitnessSegments;
		 * 	}
		 */
	
		double[] fitnessSegments = new double[tamPoblacion];
		
			for (int i = 0 ; i < fitnessSegments.length; i++){
				
				double probOfIth = (double) i / tamPoblacion;
				probOfIth = probOfIth * 2 * (beta - 1);
				probOfIth = beta - probOfIth;
				probOfIth = (double)probOfIth*((double) 1 / tamPoblacion);
				
				if (i != 0)
					fitnessSegments[i] = fitnessSegments[i-1] + probOfIth;
				else
					fitnessSegments[i] = probOfIth;
			}
			
		return fitnessSegments;
	}
	
		// CRUCE
		// ---------------------------------------------------

	/**
	 * Fase de reproduccion de los individuos (cruce)
	 */
	private void reproduccion(){
		
		/* Esquema de funcion:
		 * -------------------------------------------------------------------
		 * 	reproduccion ( ) {
		 * 		//seleccionados para reproducir
		 * 		entero sel_cruce[tam_pob];
		 * 		
		 * 		//contador seleccionados
		 * 		entero num_sele_cruce = 0;;
		 * 		entero punto_cruce;
		 * 		real prob;
		 * 		Cromosoma hijo1,hijo2;
		 * 
		 * 		//Se eligen los individuos a cruzar
		 * 		para cada i desde 0 hasta tam_pob {
		 * 			//se generan tam_pob números aleatorios en [0 1)
		 * 			prob = alea();
		 * 			//se eligen los individuos de las posiciones i si prob < prob_cruce
		 * 			si (prob < prob_cruce){
		 * 				sel_cruce[num_sel_cruce] = i;
		 *				num_sel_cruce++;
		 * 			}
		 * 		}
		 * 
		 * 		// el numero de seleccionados se hace par
		 * 		si ((num_sel_cruce mod 2) == 1)
		 * 		num_sel_cruce--;
		 * 
		 * 
		 * 		// se cruzan los individuos elegidos en un punto al azar
		 * 		punto_cruce = alea_ent(0,lcrom);
		 * 		para cada i desde 0 hasta num_sel_cruce avanzando 2 {
		 * 			cruce(pob[sel_cruce[i]], pob[sel_cruce[i+1]], hijo1, hijo2, punto_cruce, . . . );
		 * 			// los nuevos individuos sustituyen a sus progenitores
		 * 			pob[sel_cruce[i]] = hijo1;
		 * 			pob[sel_cruce[i+1]] = hijo2;
		 * 		}
		 * 	}
		 * 
		 */	
		
		// Los individuos seleccionados para reproducirse
		int[] selCruce = new int[tamPoblacion];
		
		int indiceCruce = 0;		// Indice del array de selCruce

		double prob;				// Probablidad de cruce 
		
		// Se eligen los individuos a cruzar
		for (int i = 0; i < tamPoblacion; i++){
			prob = Math.random();
			if (prob < probCruce){
				selCruce[indiceCruce] = i;
				indiceCruce++;
			}
		}
		
		// El numero de seleccionados ha de ser par
		if (indiceCruce % 2 == 1)
			indiceCruce--;

		// Cruzamos los individuos de 2 en 2 (el actual con el siguiente)
		for (int i = 0; i < indiceCruce; i += 2)
			cruce(selCruce[i],selCruce[i+1]);
	
	}
	
	/**
	 * Cruza dos individuos concretos. El proceso consiste en buscar un nodo aleatorio
	 * en ambos arboles y cruzarlos por dicho nodo. No se han establecido probabilidades
	 * para la eleccion de los nodos, aunque si dispone de Control de Bloating limitando
	 * la profundidad maxima al doble de la permitida y tambien por torneo de fitness 
	 * @param padre1
	 * @param padre2
	 */
	private void cruce(int padre1, int padre2){
		
		// Creamos una copia de los individuos a cruzar
		Cromosoma hijo1 = new Cromosoma(pob[padre1]);
		Cromosoma hijo2 = new Cromosoma(pob[padre2]);
		
		// Guardamos las adaptaciones originales antes del cruce. Las usaremos al
		// final en el torneo de fitness (Control de Bloating)
		int adaptacionPadre1 = pob[padre1].evaluaCromosoma();
		int adaptacionPadre2 = pob[padre2].evaluaCromosoma();
		
		Arbol subArbol1, subArbol2;
		
		// Calculamos el minimo numero de nodos entre ambos para asegurar que
		// cogemos un nodo a cruzar que este disponible en ambos arboles
		int numNodosMin = minNodos(hijo1.arbol, hijo2.arbol);
		
		// Cogemos un nodo al azar
		Random r = new Random();
		int nodoCruce = r.nextInt(numNodosMin);

		// Creamos REFERENCIAS a los nodos a cruzar
		subArbol1 = hijo1.arbol.buscarNodo(nodoCruce);
		subArbol2 = hijo2.arbol.buscarNodo(nodoCruce);
		
		// Intercambiamos los subarboles
		Arbol copiaHijo1 = new Arbol(subArbol1);
		
		subArbol1.setDato(subArbol2.getDato());
		subArbol1.setHijoIzquierdo(subArbol2.getHijoIzquierdo());
		subArbol1.setHijoCentral(subArbol2.getHijoCentral());
		subArbol1.setHijoDerecho(subArbol2.getHijoDerecho());
		
		subArbol2.setDato(copiaHijo1.getDato());
		subArbol2.setHijoIzquierdo(copiaHijo1.getHijoIzquierdo());
		subArbol2.setHijoCentral(copiaHijo1.getHijoCentral());
		subArbol2.setHijoDerecho(copiaHijo1.getHijoDerecho());
		
		// Actualizamos los numeros de nodos y las profundidades
		hijo1.arbol.ajustaNodos(0);
		hijo2.arbol.ajustaNodos(0);
		
		// CONTROL DE BLOATING 1
		// Impedimos sustituir por arboles que tienen una profundidad superior al doble de la permitida
		boolean descartadoHijo1 = true;
		boolean descartadoHijo2 = true;
		
		int max1 = hijo1.arbol.calculaMaxProfundidad();
		int max2 = hijo2.arbol.calculaMaxProfundidad();
		if (max1 <= maxProfundidad * 2){
			//pob[padre1] = new Cromosoma(hijo1);
			descartadoHijo1 = false;
			//totalCruces++;
		}
		if (max2 <= maxProfundidad * 2){
			//pob[padre2] = new Cromosoma(hijo2);
			descartadoHijo2 = false;
			//totalCruces++;
		}
		
		// CONTROL DE BLOATING 2
		// Evitamos cruces destructivos haciendo torneos de fitness:
		// Si no tiene mejor fitness NO lo queremos.Ademas, si se eligen valores pequeños de profundidad 
		// inicial se pueden obtener buenos resultados sin necesidad de limitar la profundidad aqui segun 
		// el libro
		
		int adaptacionHijo1 = hijo1.evaluaCromosoma();
		int adaptacionHijo2 = hijo2.evaluaCromosoma();

		// Evitaremos sustituir 2 veces en el mismo padre
		boolean padre1Sustituido = false;
		boolean padre2Sustituido = false;
		
		// Si no lo descartamos en el primer Control de Bloating
		if (!descartadoHijo1){
			
			// Probamos primero a ver si el primer hijo obtenido es mejor que alguno
			// de los 2 padres. Si es asi, nos lo quedamos
			if (adaptacionHijo1 < adaptacionPadre1){
				pob[padre1] = hijo1;
				padre1Sustituido = true;
				totalCruces++;
			}
			else if (adaptacionHijo1 < adaptacionPadre2){
					pob[padre2] = hijo1;
					padre2Sustituido = true;
					totalCruces++;
			}
			
		}

		if (!descartadoHijo2){
			
			// Probamos ahora a ver si podemos tambien quedarnos con el segundo hijo generado.
			// No sustituimos 2 veces en el mismo padre!
			if (!padre1Sustituido && adaptacionHijo2 < adaptacionPadre1){
				pob[padre1] = hijo2;
				totalCruces++;
			}
			else if (!padre2Sustituido && adaptacionHijo2 < adaptacionPadre2){
					pob[padre2] = hijo2;
					totalCruces++;
			}	
			
		}
		
	}
	
	/**
	 * Calcula el numero minimo de nodos de 2 arboles
	 * @param Arbol1
	 * @param Arbol2
	 * @return numMinNodos
	 */
	private int minNodos(Arbol a1, Arbol a2){
		
		int nodosA1 = a1.getNumNodos();
		int nodosA2 = a2.getNumNodos();
		
		if (nodosA1 > nodosA2)
			return nodosA2;
		else
			return nodosA1;
		
	}
	
	
		// MUTACION
		// ---------------------------------------------------

	/**
	 * Realiza la mutacion de un terminal simple, elegido de manera aleatoria
	 */
	private void mutacionTerminalSimple(){
		
		double prob;			// Probabilidad aleatoria asociada al gen para saber si muta o no
		
		// Cargamos el conjunto de terminales que se pueden aplicar al arbol
		String[] conjTerminales =  pob[0].arbol.getConjuntoTerminales();
		
		// Procesamos la poblacion entera
		for (int i = 0; i < tamPoblacion; i++){
			
			// Generamos una probabilidad aleatoria
			prob = Math.random();
						
			// Si no supera el limite de probabilidad de mutacion, mutamos
			if (prob < probMutacion){
							
				// Actualizamos contador de mutaciones
				totalMutaciones++;
				
				// Recuperamos la lista de nodos terminales
				ArrayList<Arbol> nodosTerminales = pob[i].arbol.buscarTerminales();
				
				// Elegimos una posicion de la lista (nodo terminal) al azar a mutar
				Random r = new Random();
				int nodoMutar = r.nextInt(nodosTerminales.size());
				
				// Cargamos el nodo a mutar
				Arbol mutar = nodosTerminales.get(nodoMutar);
				
				// Buscamos un nuevo terminal
				String datoActual = mutar.getDato();
				String nuevoDato = mutar.getDato();
				
				// Evitamos que se mute por el mismo terminal (sino, no tendria efecto)
				while (datoActual == nuevoDato)
					nuevoDato = conjTerminales[r.nextInt(conjTerminales.length)];
					
				// Una vez tengamos un terminal distinto, lo actualizamos
				mutar.setDato(nuevoDato);
				
				// Forzamos su reevaluacion puesto que ha mutado
				pob[i].evaluacion = Integer.MAX_VALUE;
				
				// No evaluamos el cromosoma actual ahora; se hara en la funcion evalua		
			}
		}
	}
		
	/**
	 * Realiza la mutacion simple de una funcion, elegida de manera aleatoria 
	 */
	private void mutacionFuncionalSimple(){
		
		double prob;			// Probabilidad aleatoria asociada al gen para saber si muta o no
		
		// Cargamos el conjunto de funciones que se pueden aplicar al arbol
		String[] conjFunciones =  pob[0].arbol.getConjuntoFunciones();
		
		// Procesamos la poblacion entera
		for (int i = 0; i < tamPoblacion; i++){
			
			// Generamos una probabilidad aleatoria
			prob = Math.random();
						
			// Si no supera el limite de probabilidad de mutacion, mutamos
			if (prob < probMutacion){
							
				// Creamos una copia por que a lo mejor no nos lo quedamos
				Cromosoma copiaIndividuo = new Cromosoma(pob[i]);
				
				// Recuperamos la lista de nodos funciones
				ArrayList<Arbol> nodosFunciones = copiaIndividuo.arbol.buscarFunciones();
				
				// Si contiene alguna funcion el nodo (puede ser que no, ocurre con inicializacion creciente)
				if (nodosFunciones.size() > 0){
							
					// Elegimos una posicion de la lista (nodo terminal) al azar a mutar
					Random r = new Random();
					int nodoMutar = r.nextInt(nodosFunciones.size());
					
					// Cargamos el nodo a mutar
					Arbol mutar = nodosFunciones.get(nodoMutar);
					
					// Buscamos una nueva funcion
					String datoActual = mutar.getDato();
					int aridadActual;
					
					// Calculamos su aridad actual
					if (datoActual.equals("IF") || datoActual.equals("PROGN3"))
						aridadActual = 3;
					else
						aridadActual = 2;
					
					String nuevoDato = mutar.getDato();
					
					// Evitamos que se mute por el mismo terminal (sino, no tendria efecto)
					while (datoActual == nuevoDato)
						nuevoDato = conjFunciones[r.nextInt(conjFunciones.length)];
						
					// Calculamos la nueva aridad
					int nuevaAridad;
					
					if (nuevoDato.equals("IF") || nuevoDato.equals("PROGN3"))
						nuevaAridad = 3;
					else
						nuevaAridad = 2;
					
					// Si son de la misma aridad, tan solo es intercambiar el dato
					if (aridadActual == nuevaAridad){
						mutar.setDato(nuevoDato);
						
						// Nos quedamos el individuo mutado
						pob[i] = copiaIndividuo;
						totalMutaciones++;
					}
					// Si la nueva aridad es menor, eliminamos el hijo central
					else if (aridadActual > nuevaAridad){
						
							// Mutamos la funcion
							mutar.setDato(nuevoDato);
							
							// Eliminamos el hijo central
							mutar.setHijoCentral(null);
							
							// Actualizamos los numeros de nodos y las profundidades
							copiaIndividuo.arbol.ajustaNodos(0);
							
							// Nos quedamos el individuo mutado
							pob[i] = copiaIndividuo;
							totalMutaciones++;
					}
					else {
						// Sino, la nueva aridad es mayor y hay que generar un arbol aleatorio
						// para rellenar el hijo central
						
						// Mutamos la funcion
						mutar.setDato(nuevoDato);
						
						// Generamos un nuevo arbol aleatorio con los parametros iniciales
						Arbol subArbol = new Arbol(maxProfundidad);
						
						switch(tipoInicializacion){
							
							case Creciente:	subArbol.inicializacionCreciente(0);
											break; 
							default:
							case Completa: 	subArbol.inicializacionCompleta(0);
											break;
										
						}	
						
						// Rellenamos el hijo central con el nuevo arbol generado
						mutar.setHijoCentral(subArbol);
	
						// Actualizamos los numeros de nodos y las profundidades
						copiaIndividuo.arbol.ajustaNodos(0);
						
						// CONTROL DE BLOATING
						// Si el individuo mutado no supera el doble de la profundidad permitida, nos lo quedamos
						if (copiaIndividuo.arbol.calculaMaxProfundidad() <= maxProfundidad * 2){
							pob[i] = copiaIndividuo;
							
							// Actualizamos contador de mutaciones
							totalMutaciones++;
						}
						
					} // else
					
					// No evaluamos el cromosoma actual ahora; se hara en la funcion evalua		
					
				} // if (nodos.size > 0)
				
			} // if (p < pMut)
			
		} // for
	}
	
	/**
	 * Combina la mutacion de un terminal simple con la de una funcion simple
	 */
	private void mutacionHibridaSimple(){
		
		double prob;			// Probabilidad aleatoria asociada al gen para saber si muta o no
		
		// Cargamos los conjuntos de nodos que se pueden aplicar al arbol
		String[] conjTerminales =  pob[0].arbol.getConjuntoTerminales();
		String[] conjFunciones =  pob[0].arbol.getConjuntoFunciones();
		
		// Procesamos la poblacion entera
		for (int i = 0; i < tamPoblacion; i++){
			
			// Generamos una probabilidad aleatoria
			prob = Math.random();
						
			// Si no supera el limite de probabilidad de mutacion, mutamos
			if (prob < probMutacion){
					
				// Creamos una copia por que a lo mejor no nos lo quedamos
				Cromosoma copiaIndividuo = new Cromosoma(pob[i]);
				
				// Buscamos un nodo aleatorio para mutar
				Random r = new Random();
				int nodoMutar = r.nextInt(copiaIndividuo.arbol.getNumNodos());
				
				Arbol mutar = copiaIndividuo.arbol.buscarNodo(nodoMutar);
				
				// Si es un operando
				if (mutar.esHoja()){
				
					// Buscamos un nuevo terminal
					String datoActual = mutar.getDato();
					String nuevoDato = mutar.getDato();
					
					// Evitamos que se mute por el mismo terminal (sino, no tendria efecto)
					while (datoActual == nuevoDato)
						nuevoDato = conjTerminales[r.nextInt(conjTerminales.length)];
						
					// Una vez tengamos un terminal distinto, lo actualizamos
					mutar.setDato(nuevoDato);
					
					pob[i] = copiaIndividuo;
					totalMutaciones++;
					
				}
				else {
					// Sino, es un operador
					

					// Buscamos una nueva funcion
					String datoActual = mutar.getDato();
					int aridadActual;
					
					// Calculamos su aridad actual
					if (datoActual.equals("IF") || datoActual.equals("PROGN3"))
						aridadActual = 3;
					else
						aridadActual = 2;
					
					String nuevoDato = mutar.getDato();
					
					// Evitamos que se mute por el mismo terminal (sino, no tendria efecto)
					while (datoActual == nuevoDato)
						nuevoDato = conjFunciones[r.nextInt(conjFunciones.length)];
						
					// Calculamos la nueva aridad
					int nuevaAridad;
					
					if (nuevoDato.equals("IF") || nuevoDato.equals("PROGN3"))
						nuevaAridad = 3;
					else
						nuevaAridad = 2;
					
					// Si son de la misma aridad, tan solo es intercambiar el dato
					if (aridadActual == nuevaAridad){
						mutar.setDato(nuevoDato);
						
						// Nos quedamos el individuo mutado
						pob[i] = copiaIndividuo;
						totalMutaciones++;
					}
					// Si la nueva aridad es menor, eliminamos el hijo central
					else if (aridadActual > nuevaAridad){
						
							// Mutamos la funcion
							mutar.setDato(nuevoDato);
							
							// Eliminamos el hijo central
							mutar.setHijoCentral(null);
							
							// Actualizamos los numeros de nodos y las profundidades
							copiaIndividuo.arbol.ajustaNodos(0);
							
							// Nos quedamos el individuo mutado
							pob[i] = copiaIndividuo;
							totalMutaciones++;
							
					}
					else {
						// Sino, la nueva aridad es mayor y hay que generar un arbol aleatorio
						// para rellenar el hijo central

						// Mutamos la funcion
						mutar.setDato(nuevoDato);
						
						// Generamos un nuevo arbol aleatorio con los parametros iniciales
						Arbol subArbol = new Arbol(maxProfundidad);
						
						switch(tipoInicializacion){
						
							case Creciente:	subArbol.inicializacionCreciente(0);
											break; 
							default:
							case Completa: 	subArbol.inicializacionCompleta(0);
											break;

										
						}	
						
						// Rellenamos el hijo central con el nuevo arbol generado
						mutar.setHijoCentral(subArbol);

						// Actualizamos los numeros de nodos y las profundidades
						copiaIndividuo.arbol.ajustaNodos(0);
						
						// CONTROL DE BLOATING
						// Si el individuo mutado no supera el doble de la profundidad permitida, nos lo quedamos
						if (copiaIndividuo.arbol.calculaMaxProfundidad() <= maxProfundidad * 2){
							pob[i] = copiaIndividuo;
							
							// Actualizamos contador de mutaciones
							totalMutaciones++;
						}
						
					}
					

					
				} // else "es operador"
				
				// No evaluamos el cromosoma actual ahora; se hara en la funcion evalua		
			}
		}
	}
	
	/**
	 * Realiza la mutacion de un subarbol, elegida de manera aleatoria 
	 */
	private void mutacionSubArbol(){
		
		double prob;			// Probabilidad aleatoria asociada al gen para saber si muta o no
		
		// Procesamos la poblacion entera
		for (int i = 0; i < tamPoblacion; i++){
			
			// Generamos una probabilidad aleatoria
			prob = Math.random();
						
			// Si no supera el limite de probabilidad de mutacion, mutamos
			if (prob < probMutacion){
							
				// Copiamos el individuo porque a lo mejor no nos lo quedamos
				Cromosoma copiaIndividuo = new Cromosoma(pob[i]);
				
				// Buscamos un nodo aleatoriamente para mutar
				Random r = new Random();	
				int nodoMutar = r.nextInt(copiaIndividuo.arbol.getNumNodos());
				
				// Cogemos la REFERENCIA del nodo a mutar
				Arbol mutar = copiaIndividuo.arbol.buscarNodo(nodoMutar);
				
				// Generamos un nuevo arbol aleatoriamente con los mismos 
				// parametros iniciales
				Arbol nuevoAleatorio = new Arbol(maxProfundidad);
				
				switch(tipoInicializacion){
					
					case Creciente:	nuevoAleatorio.inicializacionCreciente(0);
									break; 
					default:
					case Completa: 	nuevoAleatorio.inicializacionCompleta(0);
									break;

									
				}
				
				// Actualizamos hijos con el nuevo arbol generado
				mutar.setDato(nuevoAleatorio.getDato());
				mutar.setHijoIzquierdo(nuevoAleatorio.getHijoIzquierdo());
				mutar.setHijoCentral(nuevoAleatorio.getHijoCentral());
				mutar.setHijoDerecho(nuevoAleatorio.getHijoDerecho());
				
				// Ajustamos los numeros de nodos y las profundidades
				copiaIndividuo.arbol.ajustaNodos(0);
				
				// CONTROL DE BLOATING
				// Si el individuo mutado no supera el doble de la profundidad permitida, nos lo quedamos
				if (copiaIndividuo.arbol.calculaMaxProfundidad() <= maxProfundidad * 2){
					pob[i] = copiaIndividuo;
					
					// Actualizamos contador de mutaciones
					totalMutaciones++;
				}
				
				// No evaluamos el cromosoma actual ahora; se hara en la funcion evalua		
			}
		}
	}
		

		// INVERSION
		// ---------------------------------------------------

	/**
	 * Invierte el orden de los argumentos en un nodo que contenga operadores (funciones)
	 * @param probInversion 
	 */
	private void permutacion(){
	
		double prob;			// Probabilidad aleatoria asociada al gen para saber si muta o no
		
		// Procesamos la poblacion entera
		for (int i = 0; i < tamPoblacion; i++){
			
			// Generamos una probabilidad aleatoria
			prob = Math.random();
						
			// Si no supera el limite de probabilidad de inversion, mutamos
			if (prob < probInversion){
							
				// Actualizamos contador de inversiones
				totalInversiones++;
	
				// Elegimos una posicion de la lista (nodo terminal) al azar a mutar
				Random r = new Random();
				int nodoMutar = r.nextInt(pob[i].arbol.getNumNodos());
				
				// Cargamos el nodo a mutar
				Arbol mutar = pob[i].arbol.buscarNodo(nodoMutar);
				
				// Por sencillez, solo permutamos los nodos izquierdo y derecho y obviamos el central
				// Cargamos las REFERENCIAS y hacemos el intercambio
				Arbol nodoIz = mutar.getHijoIzquierdo();
				Arbol nodoDcho = mutar.getHijoDerecho();
				
				// Realizamos el intercambio
				Arbol aux = nodoIz;
				mutar.setHijoIzquierdo(nodoDcho);			
				mutar.setHijoDerecho(aux);
				
				// No evaluamos el cromosoma actual ahora; se hara en la funcion evalua		
			}
		}
	}

		// ELITISMO
		// ---------------------------------------------------

	/**
	 * Ordena la poblacion mediante QuickSort y despues se queda con tantos individuos como
	 * sea el tamaño de la elite, comenzando por el final del vector y avanzando hasta el comienzo
	 * @param tamElite
	 */
	public void separaMejores(int tamElite){
		
		// Creamos un array auxiliar para copiar la poblacion que se ordenara
		copiaOrdenada = new Cromosoma[tamPoblacion];
		for (int i = 0; i < tamPoblacion; i++)
			copiaOrdenada[i] = new Cromosoma(pob[i]);
		
		// Creamos un array de indices para ordenarlo y luego saber en que posicion
		// estaban originalmente y sustituirlos al final del ciclo evolutivo
		indicesPob = new int[tamPoblacion];
		for (int i = 0; i < tamPoblacion; i++)
			indicesPob[i] = i;
		
		// Se ordena el vector de cromosomas (la poblacion) y se aplica el mismo orden al vector de indices
		quickSort(0,tamPoblacion-1, true);
		
		// Calculamos en que posicion comienza la elite
		int inicioElite = tamPoblacion - tamElite;
		
		// Creamos una array auxiliar para almacenar la elite y recuperarla despuse del ciclo evolutivo
		elite = new Cromosoma[tamElite];
		
		// Se copia la elite (que se encuentra al final del vector de poblacion ordenado)
		for (int i = inicioElite; i < tamPoblacion; i++)
			elite[i - inicioElite] = new Cromosoma(copiaOrdenada[i]);
		
	}

	/**
	 * Ordena un vector a partir del rango proporcionado para ello:  v[primero.. ultimo]
	 * cuyo contenido son tipo "double". Este metodo es necesario en caso de utilizar elitismo
	 * @param primero
	 * @param ultimo
	 */
	public void quickSort(int primero, int ultimo, boolean ordenaIndices){
	
		int i = primero;
		int j = ultimo;
		double pivote = copiaOrdenada[primero + (ultimo-primero) / 2].aptitud;
		
		Cromosoma aux;
		int aux2;
		
		while (i <= j){
			
			while (copiaOrdenada[i].aptitud < pivote)
				i++;
			
			while (copiaOrdenada[j].aptitud > pivote)
				j--;	
			
		    if (i <= j) {
		    	
		    	// Intercambiamos cromosomas
		    	aux = copiaOrdenada[i];
		    	copiaOrdenada[i] = copiaOrdenada[j];
				copiaOrdenada[j] = aux;
				
				// Intercambiamos indices de los mismos
				if (ordenaIndices){
					aux2 = indicesPob[i];
					indicesPob[i] = indicesPob[j];
					indicesPob[j] = aux2;
				}

				
				i++;
				j--;
		    }
		}
		
		if (primero < j)
			quickSort(primero,j,ordenaIndices);
		
		if (i < ultimo)
			quickSort(i,ultimo,ordenaIndices);  


	}
	
	/**
	 * Restaura la poblacion elite que se separo antes de empezar con los operadores geneticos
	 * @param tamElite
	 */
	public void incluye(int tamElite){
	
		// Ahora incluimos la elite: Los cromosomas de la elite se procesan en orden
		// ascendente. Las posiciones (indices) de los mismos (para saber donde hay que remplazarlos)
		// se procesan en orden descendente.
		for (int i = 0; i < tamElite; i++)
			pob[indicesPob[(tamPoblacion - 1) - i]] = elite[i];
		
	}

	/**
	 * Actualiza el numero de lineas en el visor de codigo de programa
	 */
	private void actualizaNumLineasCodigo(){
		for (int i = visorNumLineasCodigo.getLineCount(); i <= areaTextoCodigo.getLineCount(); i++)
			visorNumLineasCodigo.append( i +"\n");
	}
	
	// METODOS
	// **************************************************************************************
	
	/**
	 * Actualiza la grafica y los visores de datos con los resultados de la ultima evaluacion
	 */
	public void actualizaGrafica(){
		

		double[] a = getMediaAptitud();
		double[] b = getMejorAptitudAbsoluto();
		double[] c = getMejorAptitudPorGeneracion();
		
		int maxGeneraciones = getMaxGeneraciones();
		double[] enumerado = new double[maxGeneraciones];
		for(int i = 0; i < maxGeneraciones; i++)
			enumerado[i] = i;
		

    	plot.removeAllPlots();
		plot.addLinePlot("Mejor absoluto", enumerado, b);
		plot.addLinePlot("Media de la generacion", enumerado, a);
		plot.addLinePlot("Mejor de la generacion", enumerado, c);
		plot.addLegend("SOUTH");
		plot.revalidate();
		
		//txtMejorCromosoma.setText(getMejorFenotipo());
		
		txtMejorAptitud.setText(String.valueOf(b[maxGeneraciones-1]));
		txtMediaAptitud.setText(String.valueOf(a[maxGeneraciones-1]));
		txtPeorAptitud.setText(String.valueOf(getPeorAptitud()));
		
		txtTotalCruces.setText(String.valueOf(getTotalCruces()));
		txtTotalMutaciones.setText(String.valueOf(getTotalMutaciones()));
		txtTotalInversiones.setText(String.valueOf(getTotalInversiones()));
		
		
	}
	
	/**
	 * Llamada previa que se debe realizar antes de inicializar el Thread para ejecutar
	 * el algoritmo. En este metodo se pasan los ultimos parametros de configuracion, asi
	 * como la barra de progreso
	 * @param elitismo
	 * @param probInversion
	 * @param beta
	 * @param etqProgreso
	 * @param barraProgreso
	 */
	public void preparaAlgoritmo(double elitismo, 
								 double probInversion, 
								 double beta, 
								 JLabel etqProgreso, 
								 JProgressBar barraProgreso,
								 Plot2DPanel plot,
								 
								 JTextField txtMejorAptitud,
								 JTextField txtMediaAptitud,
								 JTextField txtPeorAptitud,
								 
								 JTextField txtTotalCruces,
								 JTextField txtTotalMutaciones,
								 JTextField txtTotalInversiones,
								 
								 JTextArea areaTextoCodigo,
								 JTextArea visorNumLineasCodigo){
		
		this.elitismo = elitismo;
		this.probInversion = probInversion;
		this.beta = beta;
		this.barraProgreso = barraProgreso;
		this.etqProgreso = etqProgreso;
		
		this.plot = plot;
		
		this.txtMejorAptitud = txtMejorAptitud;
		this.txtMediaAptitud = txtMediaAptitud;
		this.txtPeorAptitud = txtPeorAptitud;
		
		this.txtTotalCruces = txtTotalCruces;
		this.txtTotalMutaciones = txtTotalMutaciones;
		this.txtTotalInversiones = txtTotalInversiones;
		
		this.areaTextoCodigo = areaTextoCodigo;
		this.visorNumLineasCodigo = visorNumLineasCodigo;
		//AGenetico.accesoGrafica = accesoGrafica;
		

		
	}
	
	/**
	 * Ejecuta el algoritmo en un Thread aparte para poder mostrar el progreso en la
	 * barra de progreso. Si lo hicieramos en el mismo Thread principal, se quedaria
	 * bloqueado y no mostraria el progreso
	 */
	@Override
	protected Void doInBackground() throws Exception {
		
		etqProgreso.setVisible(true);
		barraProgreso.setValue(0);		
		barraProgreso.setVisible(true);
		
		/*try {
			
			AGenetico.accesoGrafica.acquire();
			*/
		ejecutaAlgoritmo(elitismo,probInversion,beta);
		/*	
			AGenetico.accesoGrafica.release();
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/


		barraProgreso.setValue(100);
			
		etqProgreso.setVisible(false);
		barraProgreso.setVisible(false);// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Acciones a realizar una vez completada la tarea en segundo plano (ejecucion del Alg Genetico)
	 */
	@Override
	public void done(){
	
		actualizaGrafica();
		
		areaTextoCodigo.setText("");
		areaTextoCodigo.append(" > Código mejor cromosoma:\n\n" + mejorCromosoma.arbol.toString());
		actualizaNumLineasCodigo();
	}
	
	/**
	 * Comienza la ejecucion del algoritmo genetico para una funcion concreta y numero de variables
	 * usado en ella (solo para la función 4)
	 * @param consola - Objeto donde se mostraran los resultados
	 * @param tipoCromosoma - Funcion a ejecutar
	 * @param numVariables - Numero de variables de la funcion (Solo para la funcion 4)
	 */
	public void ejecutaAlgoritmo(double elitismo, double probInversion, double beta){

		// Reiniciamos contadores
		totalCruces = totalMutaciones = totalInversiones = 0;
		
		// Creamos una poblacion inicial del tipo correspondiente a la funcion y numero de variables (solo funcion 4)
		poblacionInicial();

		// Evaluamos la poblacion inicial
		evaluarPoblacion();

		// Bucle de evolucion: Distinto si hay elitismo (separamos los mejores)
		if (elitismo > 0){
			
			// Calculamos el tamaño de la elite
			int tamElite = (int) (tamPoblacion * elitismo);
				
			// Cuando hay elitismo, es igual que cuando no la hay. La diferencia es que
			// la elite no se somete a los procesos de seleccion, reproduccion y mutacion.
			// En realidad hacemos operaciones con todos, (para compartir material genetico)
			// pero despues restauramos la elite
			for (numGeneracion = 0; numGeneracion < maxGeneraciones; numGeneracion++){
				
				// Separamos a la elite
				separaMejores(tamElite);
				
				// Proceso de seleccion
				switch (tipoSeleccion){
					case Ruleta: 	seleccionRuleta(); 
									break;
					case Torneo:	seleccionTorneo(); 
									break;
					case Ranking:	seleccionRanking();
									break;
				}
				
				// Proceso de reproduccion (cruce)
				reproduccion();
				
				// Proceso de mutacion
				switch (tipoMutacion){
				
					default:
					case Terminal: 	mutacionTerminalSimple();
								   	break;
					case Funcional: mutacionFuncionalSimple();
									break;
					case Hibrida: 	mutacionHibridaSimple();
									break;
					case SubArbol:	mutacionSubArbol();
									break;
				}

				// Ejecutamos inversion si se ha activado la caracteristica
				if (probInversion > 0)
					permutacion();
				
				// Volvemos a incluir a la elite en la poblacion
				incluye(tamElite);
				
				// Revaluacion tras los cambios
				evaluarPoblacion();
				
				// Actualizamos la barra de progreso
				barraProgreso.setValue(numGeneracion * 100 / maxGeneraciones);
			}

			
		}
		else{
			
			// Bucle de evolucion
			for (numGeneracion = 0; numGeneracion < maxGeneraciones; numGeneracion++){

				// Proceso de seleccion
				switch (tipoSeleccion){
					case Ruleta: 	seleccionRuleta(); 
									break;
					case Torneo:	seleccionTorneo(); 
									break;
					case Ranking:	seleccionRanking();
									break;
				}

				// Proceso de reproduccion (cruce)
				reproduccion();

				// Proceso de mutacion
				switch (tipoMutacion){
				
					default:
					case Terminal: 	mutacionTerminalSimple();
								   	break;
					case Funcional: mutacionFuncionalSimple();
									break;
					case Hibrida: 	mutacionHibridaSimple();
									break;
					case SubArbol:	mutacionSubArbol();
									break;
				}

				// Ejecutamos inversion si se ha activado la caracteristica
				if (probInversion > 0)
					permutacion();
				
				// Revaluacion tras los cambios
				evaluarPoblacion();

				// Actualizamos la barra de progreso
				barraProgreso.setValue(numGeneracion * 100 / maxGeneraciones);
			}
			
		}
		
	}
	
	/**
	 * Revisa las aptitutes y las desplaza (las hace positivas) conviertiendo el problema de
	 * minimizacion en uno de maximizacion
	 */
	public void revisaAdaptacionMinimizando(){
		
		/*
		 * 
		 * funcion revisar_adaptacion_minimiza(var TPoblacion pob,TParametros param,var realcmax){
		 * 	cmax = -infinito;;
		 * 	// un valor por debajo de cualquiera que pueda
		 * 	// tomar la función objetivo 
		 * 	para cada individuo desde 1 hasta param.tam_pob hacer {
		 * 		si(pob[i].adaptacion > cmax)entonces
		 * 			cmax = pob[i].x;
		 * 	}
		 * 	
		 * 	cmax = cmax * 1.05; //margen para evitar sumadaptacion = 0
		 * 	// si converge la población 
		 * 	para cada individuo desde 1 hasta param.tam_pob hacer {
		 * 		pob[i].adaptacion = cmax - pob[i].x;
		 * 	}
		 * }
		 * 
		 */
				
		// Inicializamos a menos infinito 
		int peorAptitud = Integer.MIN_VALUE; 
		
		int[] evaluaciones = new int[tamPoblacion];
		
		// Cálculo del minimo de los valores de adaptación
		for (int i = 0; i < tamPoblacion; i++){
		
			evaluaciones[i] = pob[i].evaluaCromosoma();
			// Si encontramos una aptitud peor, nos quedamos con ella
			if (evaluaciones[i]/*pob[i].evaluaCromosoma()*/ > peorAptitud)
				peorAptitud = evaluaciones[i]; //this.pob[i].evaluaCromosoma();
			
		}
		
		// Actualizamos la peor aptitud global
		if (this.peorAptitud < peorAptitud)
			this.peorAptitud = peorAptitud;
		
		// Actualización de las aptitudes de la población
		for (int i = 0 ;  i < tamPoblacion; i++)
			pob[i].aptitud = peorAptitud - evaluaciones[i];//this.pob[i].evaluaCromosoma();

		// Revisamos tambien la aptitud del mejor cromosoma
		if (this.mejorCromosoma != null)
			this.mejorCromosoma.aptitud = peorAptitud - this.mejorCromosoma.evaluaCromosoma();
			
	}














}
