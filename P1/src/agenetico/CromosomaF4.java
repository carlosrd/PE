package agenetico;

public class CromosomaF4 extends Cromosoma {

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

	private double[] xMin;		// Extremo A del intervalo considerado para valores del dominio
	private double[] xMax;			// Extremo B del intervlao considerado para valores del dominio
	private int numVariables;					// Numero de variables de la funcion de evaluacion
	
	// CONSTRUCTORA
	// **************************************************************************************

	/**
	 * Constructora basica (solo se utiliza en las copias)
	 */
	public CromosomaF4(){
		
		// No hacemos nada; solo sirve para crear el objeto durante la copia
		
	}
	
	/**
	 * Constructora estandar
	 * @param tolerancia
	 */
	public CromosomaF4(double tolerancia, double[] xMin, double[] xMax, int numVariables){
		
		// En esta funcion, como depende de "n", estos datos no son constantes y deben
		// ser proporcionados en la creacion del cromosoma y en su copia
		this.xMin = xMin;
		this.xMax = xMax;
		this.numVariables = numVariables;
		
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
	
	public void setNumVariables(int n){
		
		numVariables = n;
		
		
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
		 * 				  				   n						   (i + 2) * x_i^2
		 * 		f(x_i|i = 1..n) =  - ( SUMATORIO ( sen(x_i) * sen^20( ----------------- ) ) )
		 *							     i = 1   							  PI
		 */
		
		double x_i;
		
		double fraccion, seno2;
		double sumatorio = 0;
		
		for (int i = 1; i <= numVariables; i++){
			
			x_i = vars[i-1]; 
			
			fraccion = (i + 1) * Math.pow(x_i, 2);
			fraccion /= Math.PI;
			seno2 = Math.pow(Math.sin(fraccion),20);
			
			sumatorio += Math.sin(x_i) * seno2;
		}
		
		return -sumatorio;

	}

	/**
	 * Devuelve una copia exacta de este mismo cromosoma
	 */
	@Override
	public CromosomaF4 copiaCromosoma(){
		
		CromosomaF4 c = new CromosomaF4();
		
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
		
		c.xMin = this.xMin;
		c.xMax = this.xMax;
		c.numVariables = this.numVariables;
		
		return c;
	}

	

}