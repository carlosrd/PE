package agenetico;

public class CromosomaF1 extends Cromosoma {

	// NOTAS
	// **************************************************************************************
	/*				 _							_
	 *				|			  xMax - xMin	 |
	 *  	lcrom = | log_2 ( 1 + ------------ ) |
	 *				|				 TOL  		 |
	 *
	 *  Log_base(2) ==> Math.log(num) / Math.log(2);
	 */
	
	// ATRIBUTOS
	// **************************************************************************************

	private final double[] xMin = {0};			// Extremo A del intervalo considerado para valores del dominio
	private final double[] xMax = {25};			// Extremo B del intervlao considerado para valores del dominio
	private final int numVariables = 1;			// Numero de variables de la funcion de evaluacion
	
	// CONSTRUCTORA
	// **************************************************************************************

	/**
	 * Constructora basica (solo se utiliza en las copias)
	 */
	public CromosomaF1(){
		
		// No hacemos nada; solo sirve para crear el objeto durante la copia
		
	}
	
	/**
	 * Constructora estandar
	 * @param tolerancia
	 */
	public CromosomaF1(double tolerancia){

		// Creamos el array de variables (genes) que representa el cromosoma
		variables = new Gen[numVariables];
		
		// Inicializamos el array de fenotipos
		fenotipos = new double[numVariables];
		
		// Inicializamos los genes
		for (int i = 0; i < numVariables; i++){
			variables[i] = new Gen(xMin[i], xMax[i], tolerancia);
			longTotal += variables[i].longitudCromosoma;
		}		
	}
	
	@Override
	public int getNumVariables(){
		
		return numVariables;
		
	}
	
	/**
	 * Inicializa el cromosoma y los genes asociados a el
	 */
	@Override
	public void inicializaCromosoma() {

		for (int i = 0; i < numVariables; i++)
			variables[i].inicializaGen();
		
	}
	
	
	/**
	 * Calcula la adaptacion del individuo (su aptitud) 
	 */
	@Override
	public double evaluaCromosoma(){
		
		// Calculamos el fenotipos correspondiente a los genes del cromosoma
		for (int i = 0; i < numVariables; i++)
			fenotipos[i] = variables[i].calculaFenotipo();
		
		// Pasamos los fenotiposs a la funcion objetivo concreta
		return funcionEvaluacion(fenotipos);
		
	}

	/**
	 * Realiza el calculo de la funcion objetivo
	 * @return El valor de los fenotipos sometidos a la funcion de evaluacion
	 */
	@Override
	public double funcionEvaluacion(double[] vars) {
		
		/*
		 * 					sen x
		 * 		f(x) = -------------------------
		 *							    cos x
		 *				1 + raiz(x) + ---------
		 *								1 + x							
		 */	
		
		double x = vars[0];
		
		double numerador = Math.sin(x);
		double denominador = 1 + Math.sqrt(x) + ( Math.cos(x) / (1+x) );
		
		return numerador / denominador;

	}

	/**
	 * Devuelve una copia exacta de este mismo cromosoma
	 */
	@Override
	public CromosomaF1 copiaCromosoma(){
		
		CromosomaF1 c = new CromosomaF1();
		
		c.aptitud = this.aptitud;
		
		c.fenotipos = new double[numVariables];
		
		for (int i = 0; i < numVariables; i++)
			c.fenotipos[i] = this.fenotipos[i];
		
		c.variables = new Gen[numVariables];
		
		for (int i = 0; i < numVariables; i++)
			c.variables[i] = this.variables[i].copiaGen();
		
		c.puntAcumulada = this.puntAcumulada;
		c.puntuacion = this.puntuacion;
		c.longTotal = this.longTotal;
		
		return c;
	}

	

}
