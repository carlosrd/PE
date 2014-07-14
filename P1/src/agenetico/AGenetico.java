package agenetico;

import java.util.Random;

import javax.swing.JTextArea;

/* Esquema de un algoritmo genetico simple:
 * 
 * 		funcion Algoritmo_Genético() {
 * 			TPoblacion pob; // población
 * 			. . .
 * 			obtener_parametros(parametros);
 * 			pob = poblacion_inicial();
 * 			evaluacion(pob, tam_pob, pos_mejor, sumadaptacion);
 * 			// bucle de evolución
 * 			mientras no se alcanza la condición de terminación hacer{
 * 				selección(pob, parámetros);
 * 				reproduccion(pob, parámetros);
 * 				evaluacion(pob, parámetros, pos_mejor, sumadaptacion);
 * 			}
 * 			devolver pob[pos_mejor];
 * 		}
 * 
 * 
 * -----------------------------------------------------------------------------
 * 		AGenetico AG = new AGenetico();
 * 		AG.inicializa(); //crea población inicial de cromosomas
 * 		AG.evaluarPoblacion();//evalúa los individuos y coge el mejor
 * 		while (!GA.terminado()) {
 * 			AG.numgeneracion++;
 * 			AG.seleccion();
 * 			AG.reproduccion();
 * 			AG.mutacion();
 * 			AG.evaluarPoblacion();
 * 			. . .
 * 		}
 * 		devolver pob[pos_mejor];
 * 
 */


public class AGenetico {

	// ATRIBUTOS
	// **************************************************************************************

	// Estructuras / Parametros basicos
	// --------------------------------------------
	private Cromosoma[] pob; 				// Población
	private int tamPoblacion; 				// Tamaño población
	private int maxGeneraciones; 			// Número máximo de generaciones
	private double probCruce;	 			// Probabilidad de cruce
	private double probMutacion; 			// Probabilidad de mutación
	private double tolerancia; 				// Tolerancia de la representación
	
	private int numGeneracion;				// Generacion actual
	
	// Atributos para la mostrar las graficas
	// --------------------------------------------
	private double[] mejorAptitudPorGeneracion;
	private double[] mediaAptitud;
	private double[] mejorAptitudAbsoluto;
	
	// Evaluacion poblacion
	// --------------------------------------------
	private Cromosoma mejorCromosoma; 		// Mejor individuo
	private int posMejor; 					// Posición del mejor cromosoma
	
	private boolean maximizando; 			// Indica si se trata de una maximizacion o minimzacion de funciones
	
	// Elitismo
	// --------------------------------------------
	private Cromosoma[] elite;
	private Cromosoma[] copiaPob;
	private int[] indicesPob;
	
	// CONSTRUCTORA
	// **************************************************************************************
	
	/**
	 * Crea un algoritmo genetico nuevo con los parametros pasados desde el formulario
	 * @param tamPoblacion - Tamaño de la poblacion a evaluar
	 * @param maxGeneraciones - Numero maximo de generaciones (iteraciones) de la poblacion
	 * @param probCruce - Probabilidad de cruce
	 * @param probMutacion - Probabilidad de mutación
	 * @param tolerancia - Precisión del resultado
	 */
	public AGenetico(int tamPoblacion, int maxGeneraciones, double probCruce, double probMutacion, double tolerancia){
		
		// Inicializacion de los valores pasados por el formulario
		
		this.tamPoblacion = tamPoblacion;
		this.maxGeneraciones = maxGeneraciones;
		this.probCruce = probCruce;
		this.probMutacion = probMutacion;
		this.tolerancia = tolerancia;

		// Arrays para mostrar la grafica
		mejorAptitudPorGeneracion = new double[maxGeneraciones];
		mediaAptitud = new double[maxGeneraciones];
		mejorAptitudAbsoluto = new double[maxGeneraciones];
		
	}
	
	
	// CONSULTORAS
	// **************************************************************************************
	
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


	
	// OPERADORES ALGORITMO GENETICO
	// **************************************************************************************
	
	/**
	 * Crea la poblacion inicial y la inicializa segun el tipo de cromosoma indicado (numero de funcion) y
	 * el numero de variables seleccionado (solo se usa en el caso de la funcion 4
	 * @param tipoCromosoma - Funcion a ejecutar (1..5)
	 * @param numVariables - Valor de "n" para la funcion 4; para el resto viene definido como constante
	 */
	public void poblacionInicial(int tipoCromosoma, int numVariables){
		
		/* Esquema de funcion:
		 * -------------------------------------------------------------------
		 * 	for (int j = 0; j < tampoblacion; j++) {
		 * 		poblacion[j] = new CromosomaProblemaConcreto();
		 * 		poblacion[j].inicializaCromosoma();
		 * 		poblacion[j].aptitud=poblacion[j].evalua();{aptitud}
		 *	} 
		 *	
		 *	public void inicializaCromosoma() {
		 *		for (int i = 0; i <longitudCromosoma; i++) {
		 *			genes[i] = MyRandom.boolRandom(); // Si es < 0.5, asigna 0; caso contrario asigna 1
		 *		}
		 *	}
		*/
		
		// Creamos una nueva poblacion de cromosomas con el tamaño indicado
		pob = new Cromosoma[tamPoblacion];
		
		// Procedemos a rellenarlas del cromosoma particular de cada funcion (tipoCromosoma).
		// Es igual para todas las funciones (excepto la 4 que depende del 
		// numero de variables seleccionado):
		// 		1 - Creacion del cromosoma concreto de la funcion
		//		2 - Inicializacion del mismo
		//		3 - Evaluacion del mismo
		switch (tipoCromosoma){
		
			default:
			case 1: for (int i = 0; i < tamPoblacion; i++){
						pob[i] = new CromosomaF1(tolerancia);
						pob[i].inicializaCromosoma();
						pob[i].aptitud = pob[i].evaluaCromosoma();
					}
			
					break;
					
			case 2: for (int i = 0; i < tamPoblacion; i++){
						pob[i] = new CromosomaF2(tolerancia);
						pob[i].inicializaCromosoma();
						pob[i].aptitud = pob[i].evaluaCromosoma();
					}
	
					break;
					
			case 3: for (int i = 0; i < tamPoblacion; i++){
						pob[i] = new CromosomaF3(tolerancia);
						pob[i].inicializaCromosoma();
						pob[i].aptitud = pob[i].evaluaCromosoma();
					}
			
					break;
					
			case 4: double[] xMin = new double[numVariables];
					double[] xMax = new double[numVariables];
					
					// Creamos tantos intervalos [0,PI] como variables se hayan seleccionado
					for (int i = 0; i < numVariables; i++){
						xMin[i] = 0;
						xMax[i] = Math.PI;
					}
				
					for (int i = 0; i < tamPoblacion; i++){
						pob[i] = new CromosomaF4(tolerancia,xMin,xMax,numVariables);
						pob[i].inicializaCromosoma();
						pob[i].aptitud = pob[i].evaluaCromosoma();
					}
			
					break;
					
			case 5: for (int i = 0; i < tamPoblacion; i++){
						pob[i] = new CromosomaF5(tolerancia);
						pob[i].inicializaCromosoma();
						pob[i].aptitud = pob[i].evaluaCromosoma();
					}
			
					break;		
		
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
		double aptitudMejor = 0;							// Mejor aptitud
		
		
		// Distinguimos si hay que maximizar o minimar y realizar un desplazamiento de la adaptacion
		
		// Esto es porque hay que distinguir entre funcion de adaptacion y de evaluacion. La primera
		// no puede tomar valores negativos, mientras que la segunda si. Para evitar esto, es necesario
		// una operación de desplazamiento, de manera que un problema de minimizacion, se positivice y
		// se convierta en uno de maximizacion
		
		// La solución está en realizar una modificacion de los valores de la funcion de adaptacion
		// de forma que se obtengan valores positivos y que cuanto menor sea el valor de la funcion
		// (mas cercano al optimo) mayor sea el correspondiente valor revisado.
		
		if (maximizando)
			revisaAdaptacionMaximizando();
		else
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
			mejorCromosoma = pob[posMejor].copiaCromosoma();
		
		// Actualizacion de los datos para las graficas
		mejorAptitudPorGeneracion[numGeneracion] = pob[posMejor].evaluaCromosoma();
		mediaAptitud[numGeneracion] = sumaAptitudAux / tamPoblacion;
		mejorAptitudAbsoluto[numGeneracion] = mejorCromosoma.evaluaCromosoma();
		
	}
	
	/**
	 * Seleccion de los siguientes elementos mediante el metodo de Ruleta
	 */
	public void seleccionRuleta(){
		
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
			
			while (prob > pob[posSuperviviente].puntAcumulada && posSuperviviente < tamPoblacion)
				posSuperviviente++;
			
			selSupervivientes[i] = posSuperviviente;
		}
		
		
		Cromosoma[] nuevaPob = new Cromosoma[tamPoblacion];
		
		for (int i = 0; i < tamPoblacion; i++)
			nuevaPob[i] = pob[selSupervivientes[i]].copiaCromosoma();	// copia auxiliar
		
		// Trasladamos la copia auxiliar a la poblacion
		for (int i = 0; i < tamPoblacion; i++)
			pob[i] = nuevaPob[i];

	}
	
	/**
	 * Seleccion de los siguientes elementos mediante el metodo de Torneo
	 * La implementacion utilizada es Torneo Deterministico de 3 individuos
	 */
	public void seleccionTorneo(){
		
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
			nuevaPob[i] = pob[selSupervivientes[i]].copiaCromosoma();	// copia auxiliar
		
		// Trasladamos la copia auxiliar a la poblacion
		for (int i = 0; i < tamPoblacion; i++)
			pob[i] = nuevaPob[i];

	}
	
	/**
	 * Fase de reproduccion de los individuso (cruce)
	 */
	public void reproduccion(){
		
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
		int puntoCruce;				// Cuando dividamos el cromosoma en partes iguales, porque parte realizamos el cruce
		
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
		
		// Generamos un punto de cruce aleatorio entre 0..lCrom
		Random r = new Random();
		puntoCruce = r.nextInt(pob[0].longTotal);
		
		// Cruzamos los individuos de 2 en 2 (el actual con el siguiente)
		for (int i = 0; i < indiceCruce; i += 2){
			cruce(selCruce[i], selCruce[i+1], puntoCruce);
		}

	
	}
	
	/**
	 * Realiza el cruce de 2 individuos concretos (padres) en el punto de cruce dado
	 * y actualiza la población con los nuevos individuos (hijos)
	 * @param padre1 - Individuo a cruzar 1
	 * @param padre2 - Individuo a cruzar 2
	 * @param puntoCruce - Punto de cruce entre individuo 1 y 2
	 */
	public void cruce(int padre1, int padre2, int puntoCruce){
		
		/* Esquema de funcion:
		 * -------------------------------------------------------------------
		 * 	cruce(padre1,padre2,hijo1,hijo2, puntoCruce){
		 * 		entero i;
		 * 		hijo1.genes.iniciar();
		 * 		hijo2.genes.iniciar();
		 * 		// primera parte del intercambio: 1 a 1 y 2 a 2
		 * 		para cada i desde 0 hasta punto_cruce hacer{
		 * 			hijo1.genes.insertar(padre1.genes[i]);
		 * 			hijo2.genes.insertar(padre2.genes[i]);
		 * 		}
		 * 
		 * 		// segunda parte: 1 a 2 y 2 a 1
		 * 		para cada i desde punto_cruce hasta lcrom; hacer{
		 * 			hijo1.genes.insertar(padre2.genes[i]);
		 * 			hijo2.genes.insertar(padre1.genes[i]);
		 * 		}
		 * 
		 * 		// se evalúan
		 * 		hijo1.aptitud = evalua(hijo1, . . . );
		 * 		hijo2.aptitud = evalua(hijo2, . . . );
		 * 	}
		 * 
		 */
			
		// Se mantienen los genes hasta el punto de cruce
		Cromosoma hijo1 = pob[padre1].copiaCromosoma();
		Cromosoma hijo2 = pob[padre2].copiaCromosoma();
		
		int varActual;			// Indice de la variable actual a la que nos referimos dentro del cromosoma
		int posActual;			// Posicion actual a mutar
			
		// Intercambiamos los genes en el punto de cruce
		for (int i = puntoCruce; i < pob[0].longTotal; i++){
			
			// Calculamos en que gen (variable) hay que escribir
			varActual = ajustaIndiceVariable(i);
			posActual = i % pob[0].variables[varActual].longitudCromosoma;
			
			int aux = hijo1.variables[varActual].genes[posActual];
			hijo1.variables[varActual].genes[posActual] = hijo2.variables[varActual].genes[posActual];
			hijo2.variables[varActual].genes[posActual] = aux;
			
		}
		
		// Se evaluan (calculan aptitud)
		hijo1.evaluaCromosoma();
		hijo2.evaluaCromosoma();
		
		// Los hijos sustituyen a los progenitores
		pob[padre1] = hijo1;
		pob[padre2] = hijo2;
		
	}
	
	/**
	 * Fase de mutación de los individuos
	 */
	public void mutacion(){
		
		/* Esquema de funcion:
		 * -------------------------------------------------------------------
		 * 	mutacion(pob, tam_pob, prob_mut, . . . ){
		 * 		booleano mutado;
		 * 		entero i,j;
		 * 		real prob;
		 * 
		 * 		para cada i desde 0 hasta tam_pob hacer{
		 * 			mutado = false;
		 * 			para cada j desde 0 hasta lcrom hacer{
		 * 				// se genera un numero aleatorio en [0 1)
		 * 				prob = alea();
		 * 				// mutan los genes con prob<prob_mut
		 * 				si (prob<prob_mut){
		 * 					pob[i].genes[j] = not( pob[i].genes[j]);
		 * 					mutado = true;
		 * 				}
		 * 				si (mutado)
		 * 					pob[i].aptitud = pob[i].evalua();
		 * 			}
		 * 		}
		 * 	}
		 * 
		 */
		
		boolean mutado;			// Indica si el cromosoma ha mutado para volver a evaluarlo
		double prob;			// Probabilidad aleatoria asociada al gen para saber si muta o no
		int varActual;			// Indice de la variable actual a la que nos referimos dentro del cromosoma
		int posActual;			// Posicion actual a mutar
		
		// Procesamos la poblacion entera
		for (int i = 0; i < tamPoblacion; i++){
			
			// Cada gen al principio lo marcamos como no mutado
			mutado = false;
			
			// Procesamos todos los genes de cada individuo en busca de mutaciones
			for (int j = 0; j < pob[0].longTotal; j++){
				
				// Generamos numero aleatorio entre 0..1
				prob = Math.random();
				
				// Calculamos a que variable pertenece la posicion "j" del cromosoma global
				varActual = ajustaIndiceVariable(j);
				
				// Ajustamos la posicion relativa "j" a la variable calculada anterioremente
				posActual = j % pob[0].variables[varActual].longitudCromosoma;
				
				// Mutar
				if (prob < probMutacion){
					
					if (pob[i].variables[varActual].genes[posActual] == 0)
						pob[i].variables[varActual].genes[posActual] = 1;
					else 
						pob[i].variables[varActual].genes[posActual] = 0;
					
					// Marcar como mutado para su reevaluacion
					mutado = true;
				}
				
			}
			
			// Si ha mutado, hay que volver a evaluar el cromosoma
			if (mutado)
				pob[i].evaluaCromosoma();
			
		}
	}

	/**
	 * Ordena la poblacion mediante QuickSort y despues se queda con tantos individuos como
	 * sea el tamaño de la elite, comenzando por el final del vector y avanzando hasta el comienzo
	 * @param tamElite
	 */
	public void separaMejores(int tamElite){
		
		// Creamos un array auxiliar para copiar la poblacion que se ordenara
		copiaPob = new Cromosoma[tamPoblacion];
		for (int i = 0; i < tamPoblacion; i++)
			copiaPob[i] = pob[i].copiaCromosoma();
		
		// Creamos un array de indices para ordenarlo y luego saber en que posicion
		// estaban originalmente y sustituirlos al final del ciclo evolutivo
		indicesPob = new int[tamPoblacion];
		for (int i = 0; i < tamPoblacion; i++)
			indicesPob[i] = i;
		
		// Se ordena el vector de cromosomas (la poblacion) y se aplica el mismo orden al vector de indices
		quickSort(0,tamPoblacion-1);
		
		// Calculamos en que posicion comienza la elite
		int inicioElite = tamPoblacion - tamElite;
		
		// Creamos una array auxiliar para almacenar la elite y recuperarla despuse del ciclo evolutivo
		elite = new Cromosoma[tamElite];
		
		// Se copia la elite (que se encuentra al final del vector de poblacion ordenado)
		for (int i = inicioElite; i < tamPoblacion; i++)
			elite[i - inicioElite] = copiaPob[i].copiaCromosoma();
		
	}

	/**
	 * Ordena un vector a partir del rango proporcionado para ello:  v[primero.. ultimo]
	 * @param primero
	 * @param ultimo
	 */
	public void quickSort(int primero, int ultimo){
	
		int i = primero;
		int j = ultimo;
		double pivote = copiaPob[primero + (ultimo-primero) / 2].aptitud;
		
		double aux;
		int aux2;
		
		while (i <= j){
			
			while (copiaPob[i].aptitud < pivote)
				i++;
			
			while (copiaPob[j].aptitud > pivote)
				j--;	
			
		    if (i <= j) {
		    	
		    	// Intercambiamos cromosomas
		    	aux = copiaPob[i].aptitud;
		    	copiaPob[i].aptitud = copiaPob[j].aptitud;
				copiaPob[j].aptitud = aux;
				
				// Intercambiamos indices de los mismos
				aux2 = indicesPob[i];
				indicesPob[i] = indicesPob[j];
				indicesPob[j] = aux2;
				
				i++;
				j--;
		    }
		    

		}
		
		if (primero < j)
			quickSort(primero,j);
		
		if (i < ultimo)
			quickSort(i,ultimo);  


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

	/**
	 * Comienza la ejecucion del algoritmo genetico para una funcion concreta y numero de variables
	 * usado en ella (solo para la función 4)
	 * @param consola - Objeto donde se mostraran los resultados
	 * @param tipoCromosoma - Funcion a ejecutar
	 * @param numVariables - Numero de variables de la funcion (Solo para la funcion 4)
	 */
	public void ejecutaAlgoritmo(JTextArea consola, int tipoCromosoma, int numVariables, int metodoCruce, double elitismo){
		
		// Las funciones 2 y 3 buscan maximos y la 1, 4 y 5 buscan minimos
		maximizando = tipoCromosoma == 2 || tipoCromosoma == 3;
		
		// Creamos una poblacion inicial del tipo correspondiente a la funcion y numero de variables (solo funcion 4)
		poblacionInicial(tipoCromosoma, numVariables);
		
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
				switch (metodoCruce){
					case 1: seleccionRuleta(); 
							break;
					case 2: seleccionTorneo(); 
							break;
				}
				
				// Proceso de reproduccion (cruce)
				reproduccion();
				
				// Proceso de mutacion
				mutacion();
				
				// Volvemos a incluir a la elite en la poblacion
				incluye(tamElite);
				
				// Revaluacion tras los cambios
				evaluarPoblacion();
			}

			
		}
		else{
			
			// Bucle de evolucion
			for (numGeneracion = 0; numGeneracion < maxGeneraciones; numGeneracion++){
				
				// Proceso de seleccion
				switch (metodoCruce){
					case 1: seleccionRuleta(); 
							break;
					case 2: seleccionTorneo(); 
							break;
				}
				
				// Proceso de reproduccion (cruce)
				reproduccion();
				
				// Proceso de mutacion
				mutacion();
				
				// Revaluacion tras los cambios
				evaluarPoblacion();
			}
			
		}
		
		
		
		// Mostramos los resultados por consola
		consola.setText("");
		consola.append("La función " + tipoCromosoma);

		if (maximizando)
			consola.append(" presenta un máximo de \n" + mejorCromosoma.funcionEvaluacion(mejorCromosoma.fenotipos));
		else
			consola.append(" presenta un mínimo de \n" + mejorCromosoma.funcionEvaluacion(mejorCromosoma.fenotipos));
		
		consola.append(" en:\n ");
		
		for (int v = 0; v < pob[0].getNumVariables(); v++)
			consola.append("\nx" + v + " = " + mejorCromosoma.variables[v].calculaFenotipo());

	}
	
	/**
	 * Revisa las aptitutes y las desplaza (las hace positivas) para evitar aptitudes negativas
	 */
	public void revisaAdaptacionMaximizando(){
		
		
		// Inicializamos a mas infinito
		double peorAptitud = Double.POSITIVE_INFINITY; 
		
		// Cálculo del maximo de los valores de adaptación
		for (int i = 0; i < tamPoblacion; i++){
			
			// Si encontramos una aptitud peor, nos quedamos con ella
			if (pob[i].evaluaCromosoma() < peorAptitud)
				peorAptitud = this.pob[i].evaluaCromosoma();
			
		}

		// Damos un 5% de margen para evitar sumaAptitud = 0
		peorAptitud *= 1.05;
		
		// Actualización de las aptitudes de la población
		for (int i = 0 ;  i < tamPoblacion; i++)
			pob[i].aptitud = peorAptitud + this.pob[i].evaluaCromosoma();
		
		if (this.mejorCromosoma != null)
			this.mejorCromosoma.aptitud = peorAptitud + this.mejorCromosoma.evaluaCromosoma();
			
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
		double peorAptitud = Double.NEGATIVE_INFINITY; 
		
		// Cálculo del minimo de los valores de adaptación
		for (int i = 0; i < tamPoblacion; i++){
			
			// Si encontramos una aptitud peor, nos quedamos con ella
			if (pob[i].evaluaCromosoma() > peorAptitud)
				peorAptitud = this.pob[i].evaluaCromosoma();
			
		}

		// Damos un 5% de margen para evitar sumaAptitud = 0
		peorAptitud *= 1.05;
		
		// Actualización de las aptitudes de la población
		for (int i = 0 ;  i < tamPoblacion; i++)
			pob[i].aptitud = peorAptitud - this.pob[i].evaluaCromosoma();

		// Revisamos tambien la aptitud del mejor cromosoma
		if (this.mejorCromosoma != null)
			this.mejorCromosoma.aptitud = peorAptitud - this.mejorCromosoma.evaluaCromosoma();
			
	}

	/**
	 * Dada una posicion de un gen en un cromosoma, esta funcion indica a que variable del
	 * cromosoma se refiere
	 * @param pos - Posicion del gen en el cromosoma global
	 * @return variable a la que pertenece la posicion
	 */
	public int ajustaIndiceVariable(int pos){
		
		// Inicializamos a la primera variable
		int varActual = 0;
		
		// La longitud minima actual es la longitud del primer subcromosoma (variable)
		int longActual = pob[0].variables[0].longitudCromosoma;
		
		// Mientras la posicion sea mayor que la longitud actual cubierta, pasamos
		// a la siguiente variable
		while (pos > longActual){
			varActual++;
			longActual += pob[0].variables[varActual].longitudCromosoma;
		}
		
		return varActual;
	}
}
