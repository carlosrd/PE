package agenetico;

public class CromosomaF2 extends Cromosoma {

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

	private final double[] xMin = {-6,-6};			// Extremo A del intervalo considerado para valores del dominio
	private final double[] xMax = {6,6};			// Extremo B del intervlao considerado para valores del dominio
	private final int numVariables = 2;			// Numero de variables de la funcion de evaluacion
	
	// CONSTRUCTORA
	// **************************************************************************************

	/**
	 * Constructora basica (solo se utiliza en las copias)
	 */
	public CromosomaF2(){
		
		// No hacemos nada; solo sirve para crear el objeto durante la copia
		
	}
	
	/**
	 * Constructora estandar
	 * @param tolerancia
	 */
	public CromosomaF2(double tolerancia){
		
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
		 * 				  2186 - (x^2 + y - 11)^2 - (x + y^2 - 7)^2
		 * 		f(x,y) = --------------------------------------------
		 *							   	   2186
		 */
		
		double x = vars[0];
		double y = vars[1];
		
		double cuadrado1 = Math.pow(x, 2) + y - 11;
		double cuadrado2 = x + Math.pow(y, 2) - 7;
		
		double numerador = 2186 - Math.pow(cuadrado1, 2) - Math.pow(cuadrado2, 2);
		
		return numerador / 2186;

	}

	/**
	 * Devuelve una copia exacta de este mismo cromosoma
	 */
	@Override
	public CromosomaF2 copiaCromosoma(){
		
		CromosomaF2 c = new CromosomaF2();
		
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

