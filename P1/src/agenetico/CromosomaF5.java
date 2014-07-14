package agenetico;

public class CromosomaF5 extends Cromosoma {

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

	private final double[] xMin = {-10,-10};			// Extremo A del intervalo considerado para valores del dominio
	private final double[] xMax = {10,10};			// Extremo B del intervlao considerado para valores del dominio
	private final int numVariables = 2;			// Numero de variables de la funcion de evaluacion
	
	// CONSTRUCTORA
	// **************************************************************************************

	/**
	 * Constructora basica (solo se utiliza en las copias)
	 */
	public CromosomaF5(){
		
		// No hacemos nada; solo sirve para crear el objeto durante la copia
		
	}
	
	/**
	 * Constructora estandar
	 * @param tolerancia
	 */
	public CromosomaF5(double tolerancia){
		
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
		 * 				  		 	5									  5
		 * 		f(x_i|i = 1,2) = SUMATORIO (i * cos ((i+1)*x1 +i )) * SUMATORIO (i * cos ((i+1)*x2 +i )) * 
		 *						   i = 1   							 	i = 1
		 */
		
		double x1 = vars[0];
		double x2 = vars[1];
		
		double sumatorio1 = 0, sumatorio2 = 0;
		
		for (int i = 1; i <= 5; i++){
			
			sumatorio1 += i * Math.cos( (i+1) * x1 + i );
			sumatorio2 += i * Math.cos( (i+1) * x2 + i );

		}
		
		return sumatorio1 * sumatorio2;

	}

	/**
	 * Devuelve una copia exacta de este mismo cromosoma
	 */
	@Override
	public CromosomaF5 copiaCromosoma(){
		
		CromosomaF5 c = new CromosomaF5();
		
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