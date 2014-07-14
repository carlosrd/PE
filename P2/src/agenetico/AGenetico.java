package agenetico;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
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
	
	private int [][] distancias;			// Matriz de distancias
	private int [][] flujos;				// Matriz de flujos
	private int numUnidades;				// Numero de unidades hospitalarias
	
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
	public enum tSeleccion{Ruleta, Torneo, Ranking};
	tSeleccion tipoSeleccion;
	
	public enum tCruce {PMX, OX, OX_OrdenPrioritario, OX_PosPrioritarias, CX, ERX, CodOrdinal, Propio};
	tCruce tipoCruce;
	
	public enum tMutacion {Intercambio, Insercion, Heuristica, Propio};
	tMutacion tipoMutacion;
	
	// Cruce ERX
	// --------------------------------------------
	private Cromosoma hijo1ERX;
	private Cromosoma hijo2ERX;
	private boolean exitoERX;

	// Mutacion Heuristica
	// --------------------------------------------
	private Cromosoma mejorHeuristico;
	private HashSet<Integer> usadosHeuristico;

	// Parametros ejecucion AGenetico (necesarios para el Thread)
	// --------------------------------------------
	double elitismo;
	double probInversion;
	
	JProgressBar barraProgreso;
	JLabel etqProgreso;

	Plot2DPanel plot;
	
	JTextField txtMejorCromosoma;
	
	JTextField txtMejorAptitud;
	JTextField txtMediaAptitud;
	JTextField txtPeorAptitud;
	
	JTextField txtTotalCruces;
	JTextField txtTotalMutaciones;
	JTextField txtTotalInversiones;
	
	//static Semaphore accesoGrafica =  new Semaphore(1);
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
					 int[][] distancias, 
					 int[][] flujos, 
					 int tamMatriz,
					 tSeleccion tipoSeleccion,
					 tCruce tipoCruce,
					 tMutacion tipoMutacion){
		
		// Inicializacion de los valores pasados por el formulario
		
		this.tamPoblacion = tamPoblacion;
		this.maxGeneraciones = maxGeneraciones;
		this.probCruce = probCruce;
		this.probMutacion = probMutacion;
	
		this.distancias = distancias;
		this.flujos = flujos;
		this.numUnidades = tamMatriz;
		
		// Arrays para mostrar la grafica
		mejorAptitudPorGeneracion = new double[maxGeneraciones];
		mediaAptitud = new double[maxGeneraciones];
		mejorAptitudAbsoluto = new double[maxGeneraciones];
		peorAptitud = Integer.MIN_VALUE;
		
		this.tipoSeleccion = tipoSeleccion;
		this.tipoCruce = tipoCruce;
		this.tipoMutacion = tipoMutacion;
		
	}
	
	
	// CONSULTORAS
	// **************************************************************************************
	
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
	 * Devuelve el fenotipo formateado con comas del mejor cromosoma absoluto
	 * @return
	 */
	public String getMejorFenotipo(){
		return mejorCromosoma.getFenotipo();
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
	public void poblacionInicial(){
		
		// Creamos una nueva poblacion de cromosomas con el tamaño indicado
		pob = new Cromosoma[tamPoblacion];
		
		for (int i = 0; i < tamPoblacion; i++){
			pob[i] = new Cromosoma(numUnidades, distancias, flujos);
			pob[i].aptitud = pob[i].evaluaCromosoma();
		}
		
	}

	/**
	 * Repasa la poblacion en busca de Cromosomas que representen la misma permutacion. Si encuentra alguno
	 * lo reemplazara por una copia mutada por intercambio.
	 */
	public void eliminaRepetidos(){
		
		HashSet<String> usadosPob = new HashSet<String>();
		
		for (int i = 0; i < tamPoblacion; i++){
			
			String fenotipo = pob[i].getHash();
			
			// Si no podemos añadirlo, es que ya esta y debemos mutarlo
			while (!usadosPob.add(fenotipo)){
				
				// Creamos un generador de numeros aleatorios
				Random r = new Random();
				
				// Creamos 2 posiciones aleatorias a intercambiar
				int pos1 = 0;
				int pos2 = 0;
				
				// Ya que hemos conseguido mutar, evitar que el intercambio se lleve por la misma componente
				while (pos1 == pos2){
					pos1 = r.nextInt(numUnidades);
					pos2 = r.nextInt(numUnidades);
				}
				
				// Intercambiar las unidades
				int aux = pob[i].unidades[pos1];
				pob[i].unidades[pos1] = pob[i].unidades[pos2];
				pob[i].unidades[pos2] = aux;
				
				fenotipo = pob[i].getHash();
				
			}
			

			
		}
		
		
	}
	
	/**
	 * Evalua a la poblacion actual
	 */
	public void evaluarPoblacion(){
		
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
		if (mejorCromosoma == null || aptitudMejor > mejorCromosoma.aptitud )
			mejorCromosoma = new Cromosoma(pob[posMejor]);
		
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
	private void reproduccion(tCruce tipoCruce){
		
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
		int puntoCruce = -1;		// Cuando dividamos el cromosoma en partes iguales, porque parte realizamos el cruce
		int puntoCruce2 = -1;		// Algunos metodos de cruce requiere 2 punto de cruce
		
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
		
		// Generamos dos puntos de cruce aleatorio entre 0..numUnidades
		Random r = new Random();

		// Repetimos mientras obtengamos las mismas posiciones o posiciones
		// contiguas. Ej: 2 y 3. En este caso solo hara el cruce sobre el 2 lo que 
		while (Math.abs(puntoCruce - puntoCruce2) < 1){
			puntoCruce = r.nextInt(pob[0].numUnidades);
			puntoCruce2 = r.nextInt(pob[0].numUnidades);
		}
		
		// Si el segundo punto de cruce es mas pequeño que el primero, los intercambiamos
		int aux;
		if (puntoCruce2 < puntoCruce){
			aux = puntoCruce;
			puntoCruce = puntoCruce2;
			puntoCruce2 = aux;
		}
		
		// Actualizamos contador de cruces
		totalCruces += indiceCruce / 2;
		
		// Cruzamos los individuos de 2 en 2 (el actual con el siguiente)
		for (int i = 0; i < indiceCruce; i += 2){

			switch(tipoCruce){
				
				default:
				case PMX: 	crucePMX(selCruce[i], selCruce[i+1], puntoCruce, puntoCruce2);
							break;
				case OX:	cruceOX(selCruce[i], selCruce[i+1], puntoCruce, puntoCruce2);
						 	break;
				case OX_OrdenPrioritario:	cruceOXOrdenPrioritario(selCruce[i], selCruce[i+1], puntoCruce, puntoCruce2);
			 								break;
				case OX_PosPrioritarias:	cruceOXPosPrioritarias(selCruce[i], selCruce[i+1], puntoCruce, puntoCruce2);
											break;
				case CX:	cruceCX(selCruce[i], selCruce[i+1]);
			 				break;
				case ERX:	cruceERX(selCruce[i], selCruce[i+1]);
			 				break;
				case CodOrdinal:	cruceCodOrdinal(selCruce[i], selCruce[i+1], puntoCruce);
			 						break;
				case Propio:		cruceCremallera(selCruce[i], selCruce[i+1]);
									break;
			}
			
		}

	
	}
	
	/**
	 * Realiza el Cruce por Emparejamiento Parcial (PMX) dados 2 padres y 2 puntos de corte
	 * @param padre1
	 * @param padre2
	 * @param puntoCruce
	 * @param puntoCruce2
	 */
	private void crucePMX(int padre1, int padre2, int puntoCruce, int puntoCruce2){

		/*	
		 * 1 - Elegir aleatoriamente dos puntos de corte.
		 * 2 - Intercambiar las dos subcadenas comprendidas entre dichos puntos en los hijos que se generan.
		 * 3 - Para los valores que faltan en los hijos se copian los valores de los padres:
		 * 		 - Si un valor no está en la subcadena intercambiada, se copia igual.
		 * 		 - Si está en la subcadena intercambiada, entonces se sustituye por el valor que tenga dicha 
		 * 		   subcadena en el otro padre.
		 */
		
		// Hacemos una copia de los cromosomas a cruzar
		Cromosoma hijo1 = new Cromosoma(pob[padre1]);
		Cromosoma hijo2 = new Cromosoma(pob[padre2]);
	
		// Guardaremos los valores usados en las subcadenas para no repetirlos en el resto
		HashSet<Integer> usadosHijo1 = new HashSet<Integer>();
		HashSet<Integer> usadosHijo2 = new HashSet<Integer>();
			
		// Intercambiamos subcadena entre puntos de corte
		for (int i = puntoCruce; i < puntoCruce2; i++){
			
			int aux = hijo1.unidades[i];
			hijo1.unidades[i] = hijo2.unidades[i];
			hijo2.unidades[i] = aux;
			
			// Marcamos las unidades usadas en cada vector
			usadosHijo1.add(hijo1.unidades[i]);
			usadosHijo2.add(hijo2.unidades[i]);
			
		}
		
		// Completamos comienzo del vector hasta primer punto de corte
		for (int i = 0; i < numUnidades ; i++){
			
			// Si hemos llegado al primer punto de cruce, saltamos la subcadena central
			// y seguimos procesando el final del vector
			if (i == puntoCruce)
				i = puntoCruce2;
			
			// Si la posicion a copiar del padre no esta en la subcadena central, copiamos de su padre
			// sino
			// Si esta en la subcadena central, entonces copiamos la misma posicion, pero del otro padre
			if (!usadosHijo1.contains(pob[padre1].unidades[i]))
				hijo1.unidades[i] = pob[padre1].unidades[i];
			else {
				
				boolean exito = false;
				int valorAIntroducir = pob[padre1].unidades[i];
				
				while (!exito){
					
					int j = puntoCruce;
					// Buscamos el valor conflictivo en la cadena del propio padre
					while (valorAIntroducir != hijo1.unidades[j] && j < puntoCruce2)
						j++;
				
					// Si la correspondencia en el padre 2 no ha sido usada, procedemos a copiarla
					// sino, habra que repetir pero ahora para la posicion nueva encontrada en el padre 2 ya usada
					// para averiguar si tiene una correspondencia libre
					if (!usadosHijo1.contains(hijo2.unidades[j])){
						hijo1.unidades[i] = hijo2.unidades[j];
						exito = true;
					}
					else
						valorAIntroducir = hijo2.unidades[j];
						
				}
				
			}
			
			// Lo añadimos a la lista de usados
			usadosHijo1.add(hijo1.unidades[i]);
			
			// Idem para el hijo 2
			if (!usadosHijo2.contains(pob[padre2].unidades[i]))
				hijo2.unidades[i] = pob[padre2].unidades[i];
			else{
				
				boolean exito = false;
				int valorAIntroducir = pob[padre2].unidades[i];
				
				while (!exito){
					
					int j = puntoCruce;
					while (valorAIntroducir != hijo2.unidades[j]&& j < puntoCruce2)
						j++;
				
					if (!usadosHijo2.contains(hijo1.unidades[j])){
						hijo2.unidades[i] = hijo1.unidades[j];
						exito = true;
					}
					else
						valorAIntroducir = hijo1.unidades[j];
						
				}
			}
			// Lo añadimos a la lista de usados
			usadosHijo2.add(hijo2.unidades[i]);
			
		}
		
		// Se evaluan (calculan aptitud)
		hijo1.aptitud = hijo1.evaluaCromosoma();
		hijo2.aptitud = hijo2.evaluaCromosoma();
		
		// Los hijos sustituyen a los progenitores
		pob[padre1] = hijo1;
		pob[padre2] = hijo2;

	}
	
	/**
	 * Realiza el Cruce por Orden dados 2 padres y 2 puntos de corte. Se intercambian las
	 * subcadenas contenidas entre los puntos de corte y a continuacion se rellenan copiando
	 * del mismo padre, siempre y cuando no se haya usado ya la unidad; en ese caso se salta a
	 * la siguiente disponible
	 * @param padre1
	 * @param padre2
	 * @param puntoCruce
	 * @param puntoCruce2
	 */
	private void cruceOX(int padre1, int padre2, int puntoCruce, int puntoCruce2){

		/*	
		 * 1 - Elegir aleatoriamente dos puntos de corte.
		 * 2 - Copiar los valores de las subcadenas comprendidas entre dichos puntos en los hijos que se generan.
		 * 3 - Para los valores que faltan en los hijos se copian los valores de los padres comenzando a partir 
		 * 	   de la zona copiada y respetando el orden:
		 * 			- Si un valor no está en la subcadena intercambiada, se copia igual.
		 * 			- Si está en la subcadena intercambiada, entonces se pasa al siguiente posible.
		 */
		
		// Hacemos una copia de los cromosomas a cruzar
		Cromosoma hijo1 = new Cromosoma(pob[padre1]);
		Cromosoma hijo2 = new Cromosoma(pob[padre2]);
		
		// Guardaremos los valores usados en las subcadenas para no repetirlos en el resto
		HashSet<Integer> usadosHijo1 = new HashSet<Integer>();
		HashSet<Integer> usadosHijo2 = new HashSet<Integer>();
			
		// Intercambiamos subcadena entre puntos de corte
		for (int i = puntoCruce; i < puntoCruce2; i++){
			
			hijo1.unidades[i] = pob[padre2].unidades[i];
			hijo2.unidades[i] = pob[padre1].unidades[i];
			
			// Marcamos las unidades usadas en cada vector
			usadosHijo1.add(hijo1.unidades[i]);
			usadosHijo2.add(hijo2.unidades[i]);
			
		}

		// Indices auxiliares para recorrer las unidades hospitalarias de los padres,
		// ya que hay que saltar posiciones si ya se encuentran en el hijo
		int uPadre1 = puntoCruce2;
		int uPadre2 = puntoCruce2;
		
		// Completamos desde el segundo punto de corte hasta el final 
		for (int i = puntoCruce2; i < numUnidades; i++){
			
			// HIJO 1
			// -----------------------------------
			
			// Si la posicion a copiar del padre ya se encuentra en la cadena intercambiada
			// hay que avanzar en la unidades del padre hasta la siguiente no usada
			while (usadosHijo1.contains(pob[padre1].unidades[uPadre1])){

				uPadre1++;
				// Si llegamos al final, reiniciamos y comenzamos por el principio
				if (uPadre1 == numUnidades)
					uPadre1 = 0;

			}
			
			// Copiamos al hijo la unidad disponible en el padre
			hijo1.unidades[i] = pob[padre1].unidades[uPadre1];
			
			// Avanzamos, ya que esta ya la hemos copiado (y no se introduce en usadosHijo!!)
			uPadre1++;
			// Si llegamos al final, reiniciamos y comenzamos por el principio
			if (uPadre1 == numUnidades)
				uPadre1 = 0;
			
			// HIJO 2
			// -----------------------------------
			// Si la posicion a copiar del padre ya se encuentra en la cadena intercambiada
			// hay que avanzar en la unidades del padre hasta la siguiente no usada
			while (usadosHijo2.contains(pob[padre2].unidades[uPadre2])){

				uPadre2++;
				if (uPadre2 == numUnidades)
					uPadre2 = 0;

			}

			// Copiamos al hijo la unidad disponible en el padre
			hijo2.unidades[i] = pob[padre2].unidades[uPadre2];
			
			// Avanzamos, ya que esta ya la hemos copiado (y no se introduce en usadosHijo!!)
			uPadre2++;
			// Si llegamos al final, reiniciamos y comenzamos por el principio
			if (uPadre2 == numUnidades)
				uPadre2 = 0;
		}
		
		// Volvemos a empezar, ahora desde le principio, hasta llegar al primer punto de corte
		for (int i = 0; i < puntoCruce; i++){
			
			// HIJO 1
			// -----------------------------------
			
			// Si la posicion a copiar del padre ya se encuentra en la cadena intercambiada
			// hay que avanzar en la unidades del padre hasta la siguiente no usada
			while (usadosHijo1.contains(pob[padre1].unidades[uPadre1])){

				uPadre1++;
				// Si llegamos al final, reiniciamos y comenzamos por el principio
				if (uPadre1 == numUnidades)
					uPadre1 = 0;

			}
			
			// Copiamos al hijo la unidad disponible en el padre
			hijo1.unidades[i] = pob[padre1].unidades[uPadre1];
			
			// Avanzamos, ya que esta ya la hemos copiado (y no se introduce en usadosHijo!!)
			uPadre1++;
			// Si llegamos al final, reiniciamos y comenzamos por el principio
			if (uPadre1 == numUnidades)
				uPadre1 = 0;
			
			// HIJO 2
			// -----------------------------------
			
			// Idem para hijo 2
			while (usadosHijo2.contains(pob[padre2].unidades[uPadre2])){

				uPadre2++;
				if (uPadre2 == numUnidades)
					uPadre2 = 0;

			}
			
			// Copiamos al hijo la unidad disponible en el padre
			hijo2.unidades[i] = pob[padre2].unidades[uPadre2];
			
			// Avanzamos, ya que esta ya la hemos copiado (y no se introduce en usadosHijo!!)
			uPadre2++;
			// Si llegamos al final, reiniciamos y comenzamos por el principio
			if (uPadre2 == numUnidades)
				uPadre2 = 0;

		}
		
		// Se evaluan (calculan aptitud)
		hijo1.aptitud = hijo1.evaluaCromosoma();
		hijo2.aptitud = hijo2.evaluaCromosoma();
		
		// Los hijos sustituyen a los progenitores
		pob[padre1] = hijo1;
		pob[padre2] = hijo2;
		
	}
	
	/** 
	 * Realiza el Cruce por Orden con Posiciones prioritarias. Similar al cruce por Orden, salvo que
	 * ahora en lugar de intercambiar una subcadena, se intercambian posiciones aleatorias
	 * @param padre1
	 * @param padre2
	 * @param puntoCruce
	 * @param puntoCruce2
	 */
	private void cruceOXPosPrioritarias(int padre1, int padre2, int puntoCruce, int puntoCruce2){

		/*	
		 * 1 - Elegir aleatoriamente dos puntos de corte.
		 * 2 - Copiar los valores de las subcadenas comprendidas entre dichos puntos en los hijos que se generan.
		 * 3 - Para los valores que faltan en los hijos se copian los valores de los padres comenzando a partir 
		 * 	   de la zona copiada y respetando el orden:
		 * 			- Si un valor no está en la subcadena intercambiada, se copia igual.
		 * 			- Si está en la subcadena intercambiada, entonces se pasa al siguiente posible.
		 */
		
		// Hacemos una copia de los cromosomas a cruzar
		Cromosoma hijo1 = new Cromosoma(pob[padre1]);
		Cromosoma hijo2 = new Cromosoma(pob[padre2]);
		
		//System.out.println( "OX POS: ANTES Hijo 1 : " + hijo1.getFenotipo());
		//System.out.println( "OX POS: ANTES Hijo 2 : " + hijo2.getFenotipo());
		
		// Guardaremos los valores usados en las subcadenas para no repetirlos en el resto
		HashSet<Integer> usadosHijo1 = new HashSet<Integer>();
		HashSet<Integer> usadosHijo2 = new HashSet<Integer>();
			
		// Calculamos cuantas posiciones vamos a intercmabiar
		int posIntercambiables = puntoCruce2 - puntoCruce;
				
		// Generamos tantas posiciones al azar como pos intercmabia
		HashSet<Integer> posIntercambio = new HashSet<Integer>();

		Random r = new Random();
		// Añadimos hasta que tengamos un conjunto del tamaño esperado (esto es porque los repetidos no los mete)
		while (posIntercambio.size() != posIntercambiables)
			posIntercambio.add(r.nextInt(numUnidades));
			
		// Intercambiamos posiciones elegidas
		for (int i = 0; i < numUnidades; i++){
			
			// Si encontramos una posicione elegida para intercambiar, intercambiamos cromosomas
			if (posIntercambio.contains(i)){
				
				hijo1.unidades[i] = pob[padre2].unidades[i];
				hijo2.unidades[i] = pob[padre1].unidades[i];
				
				// Marcamos las unidades usadas en cada vector
				usadosHijo1.add(hijo1.unidades[i]);
				usadosHijo2.add(hijo2.unidades[i]);
				
			}
		}
		/*System.out.println( "OX Pos Intercambio: " + posIntercambio.toString());
		System.out.println( "OX POS: ANTES Hijo 1 : " + hijo1.getFenotipo());
		System.out.println( "OX POS: ANTES Hijo 2 : " + hijo2.getFenotipo());
		*/
		// Indices auxiliares para recorrer las unidades hospitalarias de los padres,
		// ya que hay que saltar posiciones si ya se encuentran en el hijo
		int uPadre1 = 0;
		int uPadre2 = 0;
			
		// Rellenamos el vector copiando del mismo padre excepto en las intercmabiadas
		for (int i = 0; i < numUnidades; i++){
			
			// Si no es una de las posiciones intercambiadas, buscamos siguiente disponible del padre
			if (!posIntercambio.contains(i)){
				
				// HIJO 1
				// -----------------------------------
				
				// Si la posicion a copiar del padre ya se encuentra en las pos intercambiadas
				// hay que avanzar en las unidades del padre hasta la siguiente no usada
				while (usadosHijo1.contains(pob[padre1].unidades[uPadre1])){

					uPadre1++;
					// Si llegamos al final, reiniciamos y comenzamos por el principio
					if (uPadre1 == numUnidades)
						uPadre1 = 0;

				}
				
				// Copiamos al hijo la unidad disponible en el padre
				hijo1.unidades[i] = pob[padre1].unidades[uPadre1];

				// Avanzamos, ya que esta ya la hemos copiado (y no se introduce en usadosHijo!!)
				uPadre1++;
				
				// Si llegamos al final, reiniciamos y comenzamos por el principio
				if (uPadre1 == numUnidades)
					uPadre1 = 0;
				
				// HIJO 2
				// -----------------------------------
				// Si la posicion a copiar del padre ya se encuentra en las pos intercambiadas
				// hay que avanzar en las unidades del padre hasta la siguiente no usada
				while (usadosHijo2.contains(pob[padre2].unidades[uPadre2])){

					uPadre2++;
					if (uPadre2 == numUnidades)
						uPadre2 = 0;

				}

				// Copiamos al hijo la unidad disponible en el padre
				hijo2.unidades[i] = pob[padre2].unidades[uPadre2];

				// Avanzamos, ya que esta ya la hemos copiado (y no se introduce en usadosHijo!!)
				uPadre2++;
				
				// Si llegamos al final, reiniciamos y comenzamos por el principio
				if (uPadre2 == numUnidades)
					uPadre2 = 0;
			}

		}

		// Se evaluan (calculan aptitud)
		hijo1.aptitud = hijo1.evaluaCromosoma();
		hijo2.aptitud = hijo2.evaluaCromosoma();
		
		// Los hijos sustituyen a los progenitores
		pob[padre1] = hijo1;
		pob[padre2] = hijo2;
	}
	
	/** 
	 * Realiza el Cruce por Orden prioritario dados 2 padres y 2 puntos de corte. En esta ocasion
	 * se usa la diferencia entre los puntos de corte para determinar el numero de posiciones a
	 * mantener con orden.
	 * Primero se copian en orden estas posiciones a mantener en orden. Despues se intercambia las
	 * unidades, excepto en las posiciones que mantiene el orden. 
	 * @param padre1
	 * @param padre2
	 * @param puntoCruce
	 * @param puntoCruce2
	 */
	private void cruceOXOrdenPrioritario(int padre1, int padre2, int puntoCruce, int puntoCruce2){

		/*	
		 * Cruce por orden con orden prioritario:
		 * Los individuos no intercambian unidades, sino el orden relativo existente entre ellas.
		 * 
		 * Ejemplo: Se eligen para el intercambio de orden las posiciones 3, 4, 6 y 9.
		 * 
		 * 	Padre 1:	1 2 3 4 5 6 7 8 9
		 * 	Padre 2:	4 5 2 1 8 7 6 9 3
		 * 
		 * > En el primer progenitor el orden de esas ciudades es
		 * 				3 -> 4 -> 6 -> 9
		 * > En el segundo progenitor esas ciudades ocupan las posiciones 9, 1, 7 y 8 
		 *   (el primer descendiente será una copia del segundo progenitor en todas las posiciones salvo en ésas):
		 *   			x 5 2 1 8 7 x x x
		 * > En ellas se colocarán las ciudades seleccionadas conservando su orden relativo:
		 * 	Hijo 1:		3 5 2 1 8 7 4 6 9
		 * > El segundo se obtiene repitiendo el proceso para el segundo progenitor:
		 * 	Hijo 2:		2 1 7 4 5 6 3 8 9
		 */
		
		// Calculamos cuantas posiciones vamos a intercmabiar
		int posIntercambiables = puntoCruce2 - puntoCruce;
		
		// Generamos tantas posiciones al azar como pos intercmabia
		HashSet<Integer> usadasIntercambio = new HashSet<Integer>();

		Random r = new Random();
		// Añadimos hasta que tengamos un conjunto del tamaño esperado (esto es porque los repetidos no los mete)
		while (usadasIntercambio.size() != posIntercambiables)
			usadasIntercambio.add(r.nextInt(numUnidades));
		
		// Hacemos una copia de los cromosomas a cruzar
		Cromosoma hijo1 = new Cromosoma(pob[padre1]);
		Cromosoma hijo2 = new Cromosoma(pob[padre2]);
		
		// Vectores para almacenar el orden de las posiciones intocables
		int[] ordenFijasHijo1 = new int[posIntercambiables];
		int[] ordenFijasHijo2 = new int[posIntercambiables];
		
		int j = 0;
		int k = 0;
		// Copiamos las posiciones intocables en su orden (para poder luego restablecerlas una 
		// vez hecho el intercambio)
		for (int i = 0; i < numUnidades; i++){
			
			// HIJO 1
			// -------------------------------------------------
			if (usadasIntercambio.contains(hijo1.unidades[i])){
				ordenFijasHijo1[j] = hijo1.unidades[i];
				j++;
			}
			
			// HIJO 2
			// -------------------------------------------------
			if (usadasIntercambio.contains(hijo2.unidades[i])){
				ordenFijasHijo2[k] = hijo2.unidades[i];
				k++;
			}
		}
		
		j = 0;
		k = 0;
		// Procedemos al intercambio salvo en las posiciones intocables, que las copiaremos segun el orden 
		// guardado en el bucle anterior
		for (int i = 0; i < numUnidades; i++){
			
			// HIJO 1
			// -------------------------------------------------
			// Si no es de las posiciones intocables, copiamos del otro padre
			// sino, copiamos de las posiciones intocables en el orden guardado
			if (!usadasIntercambio.contains(pob[padre2].unidades[i]))
				hijo1.unidades[i] = pob[padre2].unidades[i];
			else {
				hijo1.unidades[i] = ordenFijasHijo1[j];
				j++;
			}
			
			// HIJO 2
			// -------------------------------------------------
			if (!usadasIntercambio.contains(pob[padre1].unidades[i]))
				hijo2.unidades[i] = pob[padre1].unidades[i];
			else {
				hijo2.unidades[i] = ordenFijasHijo2[k];
				k++;
			}
		
		}
		
		
		// Se evaluan (calculan aptitud)
		hijo1.aptitud = hijo1.evaluaCromosoma();
		hijo2.aptitud = hijo2.evaluaCromosoma();
		
		// Los hijos sustituyen a los progenitores
		pob[padre1] = hijo1;
		pob[padre2] = hijo2;
		
	}
	
	/**
	 * Realiza el Cruce por Ciclos (CX) dados 2 padres. Las posiciones que permanecen en cada hijo
	 * lo indica el padre contrario mediante un ciclo de rutas. Cuando este se completa
	 * se rellenan las que falten copiando ahora si del padre contrario.
	 * @param padre1
	 * @param padre2
	 */
	private void cruceCX(int padre1, int padre2){

		/*	
		 * Cada ciudad hereda sucesivamente la posición de alguno de los progenitores, de 
		 * acuerdo con sus posiciones en un ciclo.
		 * 		v = 1 2 3 4 5 6 7 8 9
		 * 		w = 4 1 2 8 7 6 9 3 5
		 * Se opera completando “ciclos de sucesión”. Para el primer descendiente se parte de 
		 * la primera ciudad del primer progenitor
		 * 		1 x x x x x x x x
		 * Obliga a darle a la ciudad 4 el 4º puesto:
		 * 		1 x x 4 x x x x x
		 * Esto selecciona la ciudad 8 (ciudad bajo la 4 en w).
		 * Análogamente, se incluyen la ciudades y 2, lo que lleva a la 1 (que completa el ciclo)
		 * 		1 2 3 4 x x x 8 x
		 * La ciudades restantes se rellenan con el otro padre:
		 * 		1 2 3 4 7 6 9 8 5
		 * El segundo descendiente se obtiene análogamente:
		 * 		4 1 2 8 5 6 7 3 9
		 */
		
		// Hacemos una copia de los cromosomas a cruzar
		Cromosoma hijo1 = new Cromosoma(pob[padre1]);
		Cromosoma hijo2 = new Cromosoma(pob[padre2]);

		// Mascaras booleanas que nos indicaran que posiciones herendan
		// de su padre y cuales del contrario
		boolean[] mascara1 = new boolean[numUnidades];
		boolean[] mascara2 = new boolean[numUnidades];
		
		// Inicializamos la mascaras a false.
		for (int i = 0; i < numUnidades; i++)
			mascara1[i] = mascara2[i] = false;
		
		// Guardaremos los valores usados en las subcadenas para no repetirlos en el resto
		HashSet<Integer> usadosHijo1 = new HashSet<Integer>();
		HashSet<Integer> usadosHijo2 = new HashSet<Integer>();
		
		// HIJO 1
		// ----------------------------------------------------------------
		// Añadimos la primera unidad a usados (punto de comienzo)
		usadosHijo1.add(hijo1.unidades[0]);
		mascara1[0] = true;
		
		// Obtenemos la unidad siguiente (la proporciona el padre contrario)
		int elemSig = pob[padre2].unidades[0];

		// Mientras la unidad siguiente no haya sido usada (Ciclo completo)
		// el hijo 1 hereda del padre 1 las posiciones que diga el padre 2
		while (!usadosHijo1.contains(elemSig)){

			// Buscamos en el padre 1 en que posicion se encuentra 
			// el elemento siguiente marcado por el padre 2
			int j = 0;
			while (elemSig != pob[padre1].unidades[j])
				j++;
			
			// Cuando lo ha encontrado, copiamos en el hijo 1
			// la unidad de su padre (1) y lo marcamos como usado.
			usadosHijo1.add(hijo1.unidades[j]);
			mascara1[j] = true;
			
			// El siguiente elemento es el que diga la misma posicion
			// pero en el padre 2
			elemSig = pob[padre2].unidades[j];

		}

		// HIJO 2
		// ----------------------------------------------------------------
		// Añadimos la primera unidad a usados (punto de comienzo)
		usadosHijo2.add(hijo2.unidades[0]);
		mascara2[0] = true;
		
		// Obtenemos la unidad siguiente (la proporciona el padre contrario)
		elemSig = pob[padre1].unidades[0];
		
		// Mientras la unidad siguiente no haya sido usada (Ciclo completo)
		// el hijo 2 hereda del padre 2 las posiciones que diga el padre 1
		while (!usadosHijo2.contains(elemSig)){
			
			// Buscamos en el padre 1 en que posicion se encuentra 
			// el elemento siguiente marcado por el padre 2
			int j = 0;
			while (elemSig != pob[padre2].unidades[j])
				j++;
			
			// Cuando lo ha encontrado, copiamos en el hijo 2
			// la unidad de su padre (2) y lo marcamos como usado.
			usadosHijo2.add(hijo2.unidades[j]);
			mascara2[j] = true;
			
			// El siguiente elemento es el que diga la misma posicion
			// pero en el padre 1
			elemSig = pob[padre1].unidades[j];
			
		}

		// Cuando han terminado el ciclo, las posiciones que no heredan
		// de su propio padre, las heredan del padre contrario
		for (int i = 0; i < numUnidades; i++){
			
			if (!mascara1[i])
				hijo1.unidades[i] = pob[padre2].unidades[i];
			
			if (!mascara2[i])
				hijo2.unidades[i] = pob[padre1].unidades[i];
		}

		// Se evaluan (calculan aptitud)
		hijo1.aptitud = hijo1.evaluaCromosoma();
		hijo2.aptitud = hijo2.evaluaCromosoma();
		
		// Los hijos sustituyen a los progenitores
		pob[padre1] = hijo1;
		pob[padre2] = hijo2;
		
	}
	
	/**
	 * Realiza el Cruce por Recombinacion de Rutas (ERX) dados 2 padres. Construye la tabla de rutas
	 * para obtener las rutas adyacentes mas cortas desde la actual que procesa
	 * @param padre1
	 * @param padre2
	 */
	public void cruceERX(int padre1, int padre2){

		// Hacemos una copia de los cromosomas a cruzar
		hijo1ERX = new Cromosoma(pob[padre1]);
		hijo2ERX = new Cromosoma(pob[padre2]);
		
		// FASE 1: Tabla de rutas simple
		// ----------------------------------------------------------------------------------------
		
		// Construccion de la tabla de rutas (con las conexiones SIN ordenar, pero aseguramos NO repetidos)
		// Clave: Nodo		Valor: Conjunto de nodos adyacentes
		HashMap<Integer,HashSet<Integer>> tablaRutasPrevia = new HashMap<Integer,HashSet<Integer>>();
				
		for (int i = 0; i < numUnidades; i++){
			
			// Siguiente ruta a procesar
			int nodo = hijo1ERX.unidades[i];
			
			// Usamos HashSet para no incluir 2 veces la misma conexion
			HashSet<Integer> conexiones = new HashSet<Integer>();
			
			// Calculamos las conexiones del Hijo 1
			// ----------------------------------------------------
			// Calculamos la unidad anterior
			int anterior;
			if (i == 0)
				anterior = numUnidades - 1;
			else 
				anterior = i - 1;
			
			// Calculamos la unidad siguiente
			int siguiente;
			if (i == (numUnidades - 1))
				siguiente = 0;
			else
				siguiente = i + 1;
			
			// Añadimos las conexiones que obtenemos del padre 1
			conexiones.add(hijo1ERX.unidades[anterior]);
			conexiones.add(hijo1ERX.unidades[siguiente]);
			
			// Calculamos las conexiones del Hijo 2
			// ----------------------------------------------------
			// Buscamos el mismo nodo en el padre 2 para averiguar mas conexiones
			int j = 0;
			
			while (hijo2ERX.unidades[j] != nodo)
				j++;
			
			// Calculamos la unidad anterior
			if (j == 0)
				anterior = numUnidades - 1;
			else 
				anterior = j - 1;
			
			// Calculamos la unidad siguiente
			if (j == (numUnidades - 1))
				siguiente = 0;
			else
				siguiente = j + 1;
			
			// Añadimos las conexiones que obtenemos del padre 2
			conexiones.add(hijo2ERX.unidades[anterior]);
			conexiones.add(hijo2ERX.unidades[siguiente]);
			
			// Añadimos el nodo y sus conexiones a la tabla
			tablaRutasPrevia.put(nodo, conexiones);
			
		}
		
		// FASE 2 : Ordenar las conexiones de cada nodo de la tabla de rutas por orden creciente
		//			de conexiones (orden que usara el BackTracking para probarlas)
		// ----------------------------------------------------------------------------------------
		
		// Tabla de rutas con las conexiones ordenadas
		HashMap<Integer, ArrayList<Integer>> tablaRutas = new HashMap<Integer, ArrayList<Integer>>();
		
		// Procesamos todas las rutas de la tabla para ordenarlas
		for (int i = 0 ; i < tablaRutasPrevia.size(); i++){
			
			// Conexiones a ordenar del nodo actual
			HashSet<Integer> pendientesOrdenar = tablaRutasPrevia.get(i);
			
			// Array donde depositaremos las conexiones ordenadas
			ArrayList<Integer> conexiones = new ArrayList<Integer>();
			
			// Numero de conexiones a ordenar
			int tamConexiones = pendientesOrdenar.size();
			
			// Procedemos a ordenar las conexiones
			for (int j = 0; j < tamConexiones; j++){
				
				// Inicializamos MAX_VALUE puesto que estamos minimizando
				int rutaMin = Integer.MAX_VALUE;
				
				for (Integer r : pendientesOrdenar){
					
					// Buscamos el elemento minimo entre los actuales
					if (rutaMin > tablaRutasPrevia.get(r).size())
						rutaMin = r;
					
				}
				
				// Eliminamos el elemento actual con el que nos hemos quedado
				pendientesOrdenar.remove(rutaMin);
				
				// Lo añadimos en su posicion a las conexiones
				conexiones.add(rutaMin);
				
			}
			
			// Una vez ordenadas las conexiones, las añadimos a la tabla
			tablaRutas.put(i,conexiones);
			
		}
		
		
		// FASE 3 : Buscamos una solucion con backtracking. Como hemos ordenado las rutas en orden
		//			creciente de conexiones, primero probara con las minimas
		// ----------------------------------------------------------------------------------------
		
		// Guardaremos los valores usados en las subcadenas para no repetirlos en el resto
		HashSet<Integer> usadosHijo1 = new HashSet<Integer>();
		HashSet<Integer> usadosHijo2 = new HashSet<Integer>();
		
		// Una vez tenemos la tabla, creamos los hijos:
		
		// HIJO 1
		// ------------------------------------------------------------------------

		// Comenzamos el hijo1 con la unidad inicial del hijo2
		hijo1ERX.unidades[0] = pob[padre2].unidades[0];

		// Lo añadimos a la lista de usados
		usadosHijo1.add(hijo1ERX.unidades[0]);
		
		// Preparamos los parametros para el backtracking
		// Empezamos por la posicion 1, porque la 0 ya la hemos asignado
		int k  = 1;
		
		// Creamos el array donde iremos probando las diferentes combinaciones con BT
		// y lo inicializamos
		int[] unidades = new int[numUnidades];
		unidades[0] = pob[padre2].unidades[0];
		
		// Condicion de parada de backtracking inicialmente a falso
		exitoERX = false;
		
		backTrackingERX(k, numUnidades, unidades, tablaRutas, tablaRutas.get(hijo1ERX.unidades[0]), usadosHijo1, hijo1ERX);
		
		
		// HIJO 2
		// ------------------------------------------------------------------------

		// Comenzamos el hijo1 con la unidad inicial del hijo2
		hijo2ERX.unidades[0] = pob[padre1].unidades[0];
		
		// Lo añadimos a la lista de usados
		usadosHijo2.add(hijo2ERX.unidades[0]);
		
		// Preparamos los parametros para el backtracking
		// Empezamos por la posicion 1, porque la 0 ya la hemos asignado
		k  = 1;
		
		// Creamos el array donde iremos probando las diferentes combinaciones con BT
		// y lo inicializamos
		unidades = new int[numUnidades];
		unidades[0] = pob[padre1].unidades[0];
		
		exitoERX = false;
		
		backTrackingERX(k, numUnidades, unidades, tablaRutas, tablaRutas.get(hijo2ERX.unidades[0]), usadosHijo2, hijo2ERX);
		
		// Se evaluan (calculan aptitud)
		hijo1ERX.aptitud = hijo1ERX.evaluaCromosoma();
		hijo2ERX.aptitud = hijo2ERX.evaluaCromosoma();
		
		// Los hijos sustituyen a los progenitores
		pob[padre1] = new Cromosoma(hijo1ERX);
		pob[padre2] = new Cromosoma(hijo2ERX);
		
	}

	/**
	 * BackTracking que genera un nuevo hijo por recombinacion de rutas. Se ayuda de una tabla en
	 * la que se han ordenado previamente las conexiones por orden creciente de numero de conexiones
	 * para asegurar que la primera que se prueba es la que tiene el minimo de conexiones
	 * @param k
	 * @param tamPermutacion - Tamaño del cromosoma
	 * @param unidades	- Array donde se va generando las unidades futuras del hijo
	 * @param tablaRutas - Para obtener las siguientes conexiones del nodo actual
	 * @param candidatos - Lista de conexiones del nodo actual
	 * @param usadosHijo - Para marcar los que ya hemos utilizado
	 * @param hijo - Hijo en cuestion para el que buscamos las unidades
	 */
	private void backTrackingERX(int k, 
								 int tamPermutacion, 
								 int[] unidades,
								 HashMap<Integer,ArrayList<Integer>> tablaRutas,
								 ArrayList<Integer> candidatos, 
								 HashSet<Integer> usadosHijo, 
								 Cromosoma hijo){
		
		// Para todos los candidatos de la permutacion, generamos sus permutaciones
		for (int i = 0; i < candidatos.size() && !exitoERX; i++){

			// Si K alcanza el tamaño deseado, hemos completado una permutacion
			if (k == tamPermutacion){
				
				// Insertamos el cromosoma factible encontrado en el hijo
				for (int j = 0; j < numUnidades; j++)
					hijo.unidades[j] = unidades[j];
				
				// Salimos de bucle y paramos BackTracking, pues ya tenemos una permutacion lista y no hay
				// que probar mas candidatos en esta rama 
				exitoERX = true;
				
			}
			else{
				
				// PODA: Si el candidato no ha sido usado, lo asignamos
				if (!usadosHijo.contains(candidatos.get(i))){
					
					// Asignamos un nuevo candidato a la posicion actual por la que vayamos (K)
					unidades[k] = candidatos.get(i);

					// MARCAJE: Marcamos las unidades ya utilizadas para no repetirlas en el siguiente nivel
					usadosHijo.add(unidades[k]);
					
					// Pasamos a asignar el siguiente elemento de la permutacion 
					backTrackingERX(k+1, tamPermutacion, unidades, tablaRutas, tablaRutas.get(unidades[k]), usadosHijo, hijo);

					// DESMARCAJE: Este individuo ya no le usamos; pasamos al siguiente
					usadosHijo.remove(unidades[k]);
				}
			}
		} // for
		
	}
	
	/**
	 * Realiza el Cruce por Codificacion Ordinal dados 2 padres y un punto de corte. Primero se 
	 * codifican los 2 hijos segun una lista dinamica. Una vez codificados, se puede proceder
	 * a realizar un cruce clasico a partir del punto de cruce. Finalizado el cruce, ahora
	 * se decodifican los cromosomas para retomar los valores originales
	 * @param padre1
	 * @param padre2
	 * @param puntoCruce
	 */
	private void cruceCodOrdinal(int padre1, int padre2, int puntoCruce){
		
		/*	
		 * Se ordenan todas las ciudades en una lista dinámica de referencia según cierto criterio.
		 * Para construir un individuo se van sacando una a una las ciudades recorridas, codificando
		 * en el j-ésimo gen del individuo la posición que tiene la j-ésima ciudad en la lista dinámica.
		 * Ese número es siempre un entero entre 1 y m-j+1
		 * 
		 * Ejemplo :
		 * 	Lista dinámica L = { 1,2,3,4,5,6,7,8,9}
		 * 
		 * El recorrido
		 * 		4 5 2 1 8 7 6 9 3
		 * Se representa
		 * 		4 4 2 1 4 3 2 2 1
		 * 
		 * El recorrido
		 * 		1 2 3 4 5 6 7 8 9
		 * Se representa
		 * 		1 1 1 1 1 1 1 1 1
		 */
		
		// Hacemos una copia de los cromosomas a cruzar
		Cromosoma hijo1 = new Cromosoma(pob[padre1]);
		Cromosoma hijo2 = new Cromosoma(pob[padre2]);
		
		// Creamos las listas dinamicas
		ArrayList<Integer> listaDinamica1 = new ArrayList<Integer>();
		ArrayList<Integer> listaDinamica2 = new ArrayList<Integer>();
		
		// Las rellenamos en orden ascendente
		for (int i = 0; i < numUnidades; i++){
			listaDinamica1.add(i);
			listaDinamica2.add(i);
		}
		
		// Realizamos la codificacion ordinal: sustituir su valor, por su posicion relativa
		// en la lista dinamica
		for (int i = 0; i < numUnidades; i++){
			
			// HIJO 1
			// -------------------------------
			// Calculamos el indice relativo de la unidad actual en la lista dinamica
			int j = 0;
			while (hijo1.unidades[i] != listaDinamica1.get(j))
				j++;
			
			// Una vez encontrado, lo añadimos al hijo y lo eliminamos de la lista dinamica
			hijo1.unidades[i] = j;
			listaDinamica1.remove(j);
			
			// HIJO 2
			// -------------------------------
			// Calculamos el indice relativo de la unidad actual en la lista dinamica
			j = 0;
			while (hijo2.unidades[i] != listaDinamica2.get(j))
				j++;
			
			// Una vez encontrado, lo añadimos al hijo y lo eliminamos de la lista dinamica
			hijo2.unidades[i] = j;
			listaDinamica2.remove(j);
			
		}
			
		// Cuando tenemos los cromosomas codificados, procedemos a realizar un cruce clasico
		for (int i = puntoCruce; i < numUnidades; i++){
			
			int aux = hijo1.unidades[i];
			hijo1.unidades[i] = hijo2.unidades[i];
			hijo2.unidades[i] = aux;
			
		}
		
		// Despues del cruce, invertimos la codificacion para recuperar el cromosoma original
		// es decir, una representacion de una permutacion [1 .. numUnidades] 
		
		// Rellenamos de nuevo las listas
		for (int i = 0; i < numUnidades; i++){
			listaDinamica1.add(i);
			listaDinamica2.add(i);
		}
		
		// Decodificamos los cromosomas: Recuperamos el valor original de la lista dinamica.
		// La posicion relativa en la lista viene dado por el hijo (cod ordinal)
		for (int i = 0; i < numUnidades; i++){
			
			hijo1.unidades[i] = listaDinamica1.remove(hijo1.unidades[i]);
			hijo2.unidades[i] = listaDinamica2.remove(hijo2.unidades[i]);
			
		}
		
		// Se evaluan (calculan aptitud)
		hijo1.aptitud = hijo1.evaluaCromosoma();
		hijo2.aptitud = hijo2.evaluaCromosoma();
		
		// Los hijos sustituyen a los progenitores
		pob[padre1] = hijo1;
		pob[padre2] = hijo2;
		
	}

	/**
	 * Metodo de cruce propio
	 * Realiza un cruce de 2 cromosomas intercambiando las posiciones pares o las impares (esto se 
	 * determina aleatoriamente). Los genes no intercambiados se rellena con genes del propio padre
	 * que se leen en orden y se cogen si no se encuentran entre las posiciones intercambiadas;
	 * es decir, no han sido usados 
	 * @param padre1
	 * @param padre2
	 */
	private	void cruceCremallera(int padre1, int padre2){
		
		// Hacemos una copia de los cromosomas a cruzar
		Cromosoma hijo1 = new Cromosoma(pob[padre1]);
		Cromosoma hijo2 = new Cromosoma(pob[padre2]);

		// Guardaremos los valores usados en las subcadenas para no repetirlos en el resto
		HashSet<Integer> usadosHijo1 = new HashSet<Integer>();
		HashSet<Integer> usadosHijo2 = new HashSet<Integer>();
			
		// Generamos una probabilidad aleatoria
		double p = Math.random();
		
		// Booleano que indica si intercambiamos pares o impares
		boolean intercambiaPares = false;
		
		// Probabilidad para decidir si intercambiar pares o impares
		if (p > 0.5)
			intercambiaPares = true;
		
		// Intercambiamos las posiciones pares o impares (segun determinamos antes) y
		// con esto adquirimos la mitad del codigo genetico del otro
		for (int i = 0; i < numUnidades; i++){
			
			// Si son las pares...
			if (intercambiaPares && i % 2 == 0){
				hijo1.unidades[i] = pob[padre2].unidades[i];
				hijo2.unidades[i] = pob[padre1].unidades[i];
				
				usadosHijo1.add(hijo1.unidades[i]);
				usadosHijo2.add(hijo2.unidades[i]);
			}
			
			// Si son las impares...
			if (!intercambiaPares && i % 2 == 1){
				hijo1.unidades[i] = pob[padre2].unidades[i];
				hijo2.unidades[i] = pob[padre1].unidades[i];		
			
				usadosHijo1.add(hijo1.unidades[i]);
				usadosHijo2.add(hijo2.unidades[i]);
			}
		}
		
		// Variables que indicara desde que posicion comenzaremos a copiar
		// desde nuestro propio padre
		int inicio = 0;
		
		// Ajustamos si son pares (comenzaremos desde la 1
		if (intercambiaPares)
			inicio = 1;
		
		int uPadre1 = 0;
		int uPadre2 = 0;
		
		// Copiamos ahora el material genetico del propio padre en orden.
		// Si no ha sido usada, la copiamos, sino, saltamos al siguiente disponible
		for (int i = inicio; i < numUnidades; i += 2){
			
			// Buscamos el siguiente sin usar
			while (usadosHijo1.contains(pob[padre1].unidades[uPadre1])){
				
				uPadre1++;
				
				if (uPadre1 == numUnidades)
					uPadre1 = 0;
				
			}
			
			// Una vez lo tengamos, lo añadimos al cromosoma
			hijo1.unidades[i] = pob[padre1].unidades[uPadre1];

			// Lo marcamos como usado
			usadosHijo1.add(hijo1.unidades[i]);
			
			// HIJO 2
			// ------------------------------------------
			
			while (usadosHijo2.contains(pob[padre2].unidades[uPadre2])){
				
				uPadre2++;
				
				if (uPadre2 == numUnidades)
					uPadre2 = 0;
				
			}
			
			hijo2.unidades[i] = pob[padre2].unidades[uPadre2];
			
			usadosHijo2.add(hijo2.unidades[i]);
			
		}
		
		// Se evaluan (calculan aptitud)
		hijo1.aptitud = hijo1.evaluaCromosoma();
		hijo2.aptitud = hijo2.evaluaCromosoma();
		
		// Los hijos sustituyen a los progenitores
		pob[padre1] = hijo1;
		pob[padre2] = hijo2;

	}
	
		// MUTACION
		// ---------------------------------------------------

	/**
	 * Realiza una mutacion intercambiando 2 posiciones al azar
	 */
	private void mutacionIntercambio(){
		
		double prob;			// Probabilidad aleatoria asociada al gen para saber si muta o no

		// Procesamos la poblacion entera
		for (int i = 0; i < tamPoblacion; i++){
			
			// Generamos una probabilidad aleatoria
			prob = Math.random();
			
			// Si no supera el limite de probabilidad de mutacion, mutamos
			if (prob < probMutacion){
				
				// Actualizamos contador de mutaciones
				totalMutaciones++;
				
				// Creamos un generador de numeros aleatorios
				Random r = new Random();
				
				// Creamos 2 posiciones aleatorias a intercambiar
				int pos1 = 0;
				int pos2 = 0;
				
				// Ya que hemos conseguido mutar, evitar que el intercambio se lleve por la misma componente
				while (pos1 == pos2){
					pos1 = r.nextInt(numUnidades);
					pos2 = r.nextInt(numUnidades);
				}
				
				// Intercambiar las unidades
				int aux = pob[i].unidades[pos1];
				pob[i].unidades[pos1] = pob[i].unidades[pos2];
				pob[i].unidades[pos2] = aux;
				
				// Actualizamos aptitud del cromosoma
				pob[i].aptitud = pob[i].evaluaCromosoma();
				
			}

			
		}
	}
	
	/**
	 * Realiza una mutación por inserción múltiple (del 20% del numero de posiciones) de toda
	 * una población
	 */
	private void mutacionInsercion(){		

		double prob;			// Probabilidad aleatoria asociada al gen para saber si muta o no
	
		// Haremos un numero de inserciones igual al 20% del numero de unidades (Si son 10 unidades, hace 2 inserciones)
		int numInserciones = (int) (numUnidades * 0.2);
		
		// Si se hace mutacion sobre cromosomas mas pequeños que 5, no se produciran inserciones
		if (numInserciones == 0)
			numInserciones = 1;
			
		// Procesamos la poblacion entera
		for (int i = 0; i < tamPoblacion; i++){
			
			// Generamos una probabilidad aleatoria
			prob = Math.random();
			
			// Si no supera el limite de probabilidad de mutacion, mutamos
			if (prob < probMutacion){
				
				// Actualizamos contador de mutaciones
				totalMutaciones++;
				
				// Realizamos las inserciones
				for (int j = 0; j < numInserciones; j++){
					
					// Creamos 2 posiciones al azar, 1 la que vamos a mover y otra "a donde" la vamos a mover
					Random r = new Random();
					
					int posInicial = 0;
					int posFinal = 0;
					
					// Si por azar se coge la misma posicion, repetimos
					while (posInicial == posFinal){
						posInicial = r.nextInt(numUnidades);
						posFinal = r.nextInt(numUnidades);
					}
					
					// Realizaremos inserciones cogiendo una posicion e insertandola en una posicion anterior
					// siempre, por lo que, si la posFinal es mas grande que la inicial, no es una posicion anterior
					int aux;
					if (posFinal > posInicial){
						aux = posFinal;
						posFinal = posInicial;
						posInicial = aux;
					}
					
					// Copiamos el valor a mover (se machacara con el desplazamiento)
					int elemMovido = pob[i].unidades[posInicial];
					
					// Desplazmaos todas las posiciones
					for (int k = posInicial; k != posFinal; k--)
						pob[i].unidades[k] = pob[i].unidades[k-1];
						
					// Situamos el elemento en el sitio correspondiente
					pob[i].unidades[posFinal] = elemMovido;

				}
				
				// Actualizamos aptitud del cromosoma
				pob[i].aptitud = pob[i].evaluaCromosoma();
				
			}
		


			
		}
	}
	
	/**
	 * Realiza una mutacion heuristica, en la que se escogen N unidades y se realizan todas
	 * las permutaciones con ellas (con el metodo BackTrackinhPermutaciones) y se prueban sobre
	 * el cromosoma original. Al final, se queda con la permutación con mejor aptitud
	 */
	private void mutacionHeuristica(){		

		double prob;			// Probabilidad aleatoria asociada al gen para saber si muta o no
		usadosHeuristico = new HashSet<Integer>();
		
		// Procesamos la poblacion entera
		for (int i = 0; i < tamPoblacion; i++){
			
			// Generamos una probabilidad aleatoria
			prob = Math.random();
			
			// Si no supera el limite de probabilidad de mutacion, mutamos
			if (prob < probMutacion){
				
				// Actualizamos contador de mutaciones
				totalMutaciones++;
				
				// Generamos aleatoriamente el tamaño de los elementos a permutar
				// sin exceder 9, porque 9! se vuelve inmanejable para BackTracking
				int aPermutar;
				if (numUnidades < 9)
					aPermutar = numUnidades;
				else
					aPermutar = 9;
				
				Random r = new Random();
				int tamPermutacion = r.nextInt(aPermutar); // NO mas de 8 o BackTracking se vuelve inmanejable 
				
				// Almacenaremos los candidatos a permutar y sus posiciones en unos ArrayList
				ArrayList<Integer> candidatosPermutacion = new ArrayList<Integer>();
				ArrayList<Integer> posCandidatos = new ArrayList<Integer>();
				
				// Usaremos el HashSet para no repetir posiciones en las permutaciones
				HashSet<Integer> usadosPermutacion = new HashSet<Integer>();
				
				// Necesitamos generar tantas posiciones como el tamaño de la permutacion
				// generado, pero si se escoge una posicion repetida, deberemos generar
				// otra posicion valida
				int posNecesarias = tamPermutacion;
				
				// Generamos tantas posiciones aleatorias necesarias a permutar
				for (int j = 0; j < posNecesarias; j++){
					
					// Generamos una posicion aleatoria
					int posCandidata = r.nextInt(numUnidades);
					
					// Evitamos cargar en la permutacion 2 veces la misma posicion
					// comprobando en el HashSet si ya la habiamos usado
					if (usadosPermutacion.add(posCandidata)){
						candidatosPermutacion.add(pob[i].unidades[posCandidata]);
						posCandidatos.add(posCandidata);
					}
					else
						posNecesarias++;	// La posicion ya estaba escogida, hay que escoger otra
						
				}

				// Creamos una copia del cromosoma a mutar para probar todas las permutaciones en el
				Cromosoma copia = new Cromosoma(pob[i]);
				mejorHeuristico = new Cromosoma(pob[i]);
				
				// Generamos un vector del tamaño de las permutaciones a crear para rellenarlo por backTracking
				int[] permutacion = new int[tamPermutacion];

				// Generamos todas las permutaciones y nos quedamos con la mejor
				backTrackingPermutaciones(0, tamPermutacion, permutacion, candidatosPermutacion, posCandidatos, copia);
				
				// Sustituimos por la mejor permutacion (mutacion) encontrada
				pob[i] = new Cromosoma(mejorHeuristico);

			}
			
		}
	}
	
	/**
	 * Metodo vuelta-atras para generar todas las permutaciones dado un vector de enteros
	 * @param k - Es la posicion actual (nivel del arbol) a la que asignamos un candidato
	 * @param tamPermutacion - Indica el tamaño de la permutacion
	 * @param permutacion - Es el vector que almacena la permutaciones construidas por el algoritmo
	 * @param candidatos - Es la lista de candidatos a permutar
	 * @param pos - Es la lista de sus posiciones originales en el cromosoma para luego devolverlas
	 * @param c - Es la copia del cromosoma sobre el que evaluamos el resultado de la mutacion
	 */
	private void backTrackingPermutaciones(int k, int tamPermutacion, int[] permutacion, ArrayList<Integer> candidatos, ArrayList<Integer> pos, Cromosoma c){
		
		// Para todos los candidatos de la permutacion, generamos sus permutaciones
		for (int i = 0; i < candidatos.size(); i++){

			// Si K alcanza el tamaño deseado, hemos completado una permutacion, probamos la solucion
			if (k == tamPermutacion){
				
				// Insertamos la permutacion generada en el cromosmoa a mutar
				for (int j = 0; j < pos.size(); j++)
					c.unidades[pos.get(j)] = permutacion[j];

				// Evaluamos los resultados de la mutacion
				c.aptitud = c.evaluaCromosoma();
				
				// Si supera al mejor heuristico hasta ahora, nos lo quedamos
				if (mejorHeuristico.aptitud > c.aptitud)
					mejorHeuristico = new Cromosoma(c);
				
				// Salimos de bucle, pues ya tenemos una permutacion lista y no hay
				// que probar mas candidatos en esta rama 
				break;
			}
			else{
				
				// PODA: Si el candidato no ha sido usado, lo asignamos
				if (!usadosHeuristico.contains(candidatos.get(i))){
					
					// Asignamos un nuevo candidato a la posicion actual por la que vayamos (K)
					permutacion[k] = candidatos.get(i);
					
					// MARCAJE: Marcamos como usado el candidato (ya que no podemos repetir)
					usadosHeuristico.add(candidatos.get(i));
					
					// Pasamos a asignar el siguiente elemento de la permutacion 
					backTrackingPermutaciones(k+1, tamPermutacion, permutacion, candidatos, pos, c);
					
					// DESMARCAJE: Como vamos a probar con otro candidato, lo quitamos de los usados
					usadosHeuristico.remove(candidatos.get(i));
				}
				

			}
		}
		
	}

	/**
	 * Metodo de mutacion propio.
	 * Realiza una mutacion por desplazamiento. Se genera un numero aleatorio que determinara
	 * cuantas posiciones haremos un desplazamiento a la derecha de todos los genes
	 */
	private void mutacionDesplazamiento(){
		
		double prob;			// Probabilidad aleatoria asociada al gen para saber si muta o no

		// Procesamos la poblacion entera
		for (int i = 0; i < tamPoblacion; i++){
			
			// Generamos una probabilidad aleatoria
			prob = Math.random();
			
			// Si no supera el limite de probabilidad de mutacion, mutamos
			if (prob < probMutacion){
				
				// Actualizamos contador de mutaciones
				totalMutaciones++;
				
				// Creamos un generador de numeros aleatorios
				Random r = new Random();
				
				// Creamos el numero de desplazamientos a la derecha a realizar. Impedimos 
				// que sea igual al numero de unidades o 0 (dejaria el cromosoma intacto)
				int numDesplazamientos = r.nextInt(numUnidades-1);
				while (numDesplazamientos == 0)
					numDesplazamientos = r.nextInt(numUnidades-1);
				
				Cromosoma desplazado = new Cromosoma(pob[i]);
				
				// Copiamos desde el numero desplazado hasta el final; es decir, si es un desp de 2
				// comenzamos a copiar desde la 2 y despues procesamos desde el principio a la 2
				int k = numDesplazamientos;
				
				for (int j = 0; j < numUnidades; j++){
					
					// Copiamos las posiciones desplazadas
					desplazado.unidades[k] = pob[i].unidades[j];
					k++;
					
					// Si llegamos al final, seguimos copiando por el principio
					if (k == numUnidades){
						k = 0;
					}
					
				}
				
				// Asignamos al individuo, las unidades desplazadas
				pob[i] = new Cromosoma(desplazado);
				
				// Actualizamos aptitud del cromosoma
				pob[i].aptitud = pob[i].evaluaCromosoma();
				
			}

			
		}
	}
			
		// INVERSION
		// ---------------------------------------------------

	/**
	 * Operador (mutación) por inversión que se aplica entre 2 puntos de corte que se generan aleatoriamente
	 * y una probabilidad. Se realiza después de los cruces y las mutaciones
	 * @param probInversion
	 */
	private void operadorInversion(double probInversion){
		
		double prob;			// Probabilidad aleatoria asociada al gen para saber si muta o no

		// Procesamos la poblacion entera
		for (int i = 0; i < tamPoblacion; i++){
			
			// Creamos una copia del cromosoma a invertir porque los cambios solo
			// seran efectivos si tras la inversion obtenemos una aptitud mejor
			Cromosoma copia = new Cromosoma(pob[i]);
	
			// Generamos numero aleatorio entre 0..1
			prob = Math.random();
			
			if (prob < probInversion){
				
				// Actualizamos contador de inversiones
				totalInversiones++;
				
				// Generamos dos puntos de cruce aleatorio entre 0..numUnidades
				Random r = new Random();
				int puntoCruce = 0;
				int puntoCruce2 = 0;
				
				// Repetimos mientras salgan los mismos puntos de cruce y mientras
				// se elijan posiciones contiguas. Ej: 2 y 3,  ya que entonces solo hay
				// que invertir la pos 2, es decir, se queda como esta
				while (Math.abs(puntoCruce - puntoCruce2) < 1){
					puntoCruce = r.nextInt(pob[0].numUnidades);
					puntoCruce2 = r.nextInt(pob[0].numUnidades);
				}
				
				// Si el segundo punto de cruce es mas pequeño que el primero, los intercambiamos
				int aux;
				if (puntoCruce2 < puntoCruce){
					aux = puntoCruce;
					puntoCruce = puntoCruce2;
					puntoCruce2 = aux;
				}
				
				int k = puntoCruce2 - 1;
				
				// Rellenamos COPIA de dcha a izq (<--) mientras leemos POB de izq a dcha (-->
				for (int j = puntoCruce; j < puntoCruce2; j++){
					
					copia.unidades[k] = pob[i].unidades[j];
					k--;
				}
				// Evaluamos el cromosoma copiado e invertido
				copia.aptitud = copia.evaluaCromosoma();
				
				// Si el cromosoma invertido es mejor que el actual, lo actualizamos
				if (copia.aptitud > pob[i].aptitud)
					pob[i] = copia;
			
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
		
		double aux;
		int aux2;
		
		while (i <= j){
			
			while (copiaOrdenada[i].aptitud < pivote)
				i++;
			
			while (copiaOrdenada[j].aptitud > pivote)
				j--;	
			
		    if (i <= j) {
		    	
		    	// Intercambiamos cromosomas
		    	aux = copiaOrdenada[i].aptitud;
		    	copiaOrdenada[i].aptitud = copiaOrdenada[j].aptitud;
				copiaOrdenada[j].aptitud = aux;
				
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
	
	// METODOS
	// **************************************************************************************
	
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
		
		txtMejorCromosoma.setText(getMejorFenotipo());
		
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
	
									JTextField txtMejorCromosoma,
									
									JTextField txtMejorAptitud,
									JTextField txtMediaAptitud,
									JTextField txtPeorAptitud,
									
									JTextField txtTotalCruces,
									JTextField txtTotalMutaciones,
									JTextField txtTotalInversiones){
								 //Semaphore accesoGrafica){
		
		this.elitismo = elitismo;
		this.probInversion = probInversion;
		this.beta = beta;
		this.barraProgreso = barraProgreso;
		this.etqProgreso = etqProgreso;
		
		this.plot = plot;
		
		this.txtMejorCromosoma = txtMejorCromosoma;
		
		this.txtMejorAptitud = txtMejorAptitud;
		this.txtMediaAptitud = txtMediaAptitud;
		this.txtPeorAptitud = txtPeorAptitud;
		
		this.txtTotalCruces = txtTotalCruces;
		this.txtTotalMutaciones = txtTotalMutaciones;
		this.txtTotalInversiones = txtTotalInversiones;
		
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
	
	@Override
	public void done(){
	
		actualizaGrafica();
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

		// Eliminamos los repetidos con mutacions por intercambio
		eliminaRepetidos();

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
				
				// Eliminamos los repetidos con mutacions por intercambio
				eliminaRepetidos();
				
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
				reproduccion(tipoCruce);
				
				// Proceso de mutacion
				switch (tipoMutacion){
				
					default:
					case Intercambio: 	mutacionIntercambio();
										break;
					case Insercion: 	mutacionInsercion();
										break;
					case Heuristica: 	mutacionHeuristica();
										break;
					case Propio:		mutacionDesplazamiento();
										break;
				}

				// Ejecutamos inversion si se ha activado la caracteristica
				if (probInversion > 0)
					operadorInversion(probInversion);
				
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
				reproduccion(tipoCruce);

				// Proceso de mutacion
				switch (tipoMutacion){
				
					default:
					case Intercambio: 	mutacionIntercambio();
										break;
					case Insercion: 	mutacionInsercion();
										break;
					case Heuristica: 	mutacionHeuristica();
										break;
				}

				// Ejecutamos inversion si se ha activado la caracteristica
				if (probInversion > 0)
					operadorInversion(probInversion);
				
				// Eliminamos los repetidos con mutacions por intercambio
				eliminaRepetidos();
				
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
		
		// Cálculo del minimo de los valores de adaptación
		for (int i = 0; i < tamPoblacion; i++){
			
			// Si encontramos una aptitud peor, nos quedamos con ella
			if (pob[i].evaluaCromosoma() > peorAptitud)
				peorAptitud = this.pob[i].evaluaCromosoma();
			
		}
		
		// Actualizamos la peor aptitud global
		if (this.peorAptitud < peorAptitud)
			this.peorAptitud = peorAptitud;
		
		// Actualización de las aptitudes de la población
		for (int i = 0 ;  i < tamPoblacion; i++)
			pob[i].aptitud = peorAptitud - this.pob[i].evaluaCromosoma();

		// Revisamos tambien la aptitud del mejor cromosoma
		if (this.mejorCromosoma != null)
			this.mejorCromosoma.aptitud = peorAptitud - this.mejorCromosoma.evaluaCromosoma();
			
	}














}
