package agenetico;

public class Gen {

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
	
	protected int[] genes;				// Contiene la codificacion de una variable de una funcion en concreto
	private double xMin;				// Extremo A del intervalo considerado para valores del dominio
	private double xMax;				// Extremo B del intervalo considerado para valores del dominio
	
	private double tolerancia;			// Precision de la variable
	protected int longitudCromosoma;	// Longitud del cromosoma correspondiente a la variable
	
	/**
	 * Constructora principal. Para el calculo de la longitud del cromosoma es necesario proporcionar
	 * 3 parametros: 
	 * @param xMin - El extremo izquierdo del intervalo a considerar
	 * @param xMax - El extremo derecho del intervalo a considerar
	 * @param tolerancia - La precision del gen a alcanzar
	 */
	public Gen(double xMin, double xMax, double tolerancia){
		
		// Ajustamos valores iniciales
		this.xMin = xMin;
		this.xMax = xMax;
		this.tolerancia = tolerancia;
		
		// Calculamos la longitud del cromosoma segun la formula
		double lcrom = ((xMax - xMin) / tolerancia) + 1;
		
		// Le hacemos el log en base (2) y funcion cielo (redondeo por arriba => ceil() )
		longitudCromosoma = (int) Math.ceil(Math.log(lcrom) / Math.log(2));
		
		genes = new int[longitudCromosoma];
	}
	
	/**
	 * Rellena el gen actual con valores aleatorios (0 si es menor que 0'5 y 1 si es mayor)
	 */
	public void inicializaGen() {
		
		// Rellenamos todos los genes del cromosoma 
		for (int i = 0; i < longitudCromosoma; i++){
			
			// Si el siguiente aleatorio es menor que 0'5 asignamos 0, sino 1
			if (Math.random() < 0.5)
				genes[i] = 0;
			else
				genes[i] = 1;
			
		} 
	}
	
	/**
	 * Calcula el valor concreto del individuo
	 */
	public double calculaFenotipo() {
		
		/*
		 * 									(xMax - xMin)
		 * 		x(v) = xMin + bin2dec(v) * ---------------
		 * 									(2^lcrom) - 1
		 * 
		 */
		
		// Calculamos el fenotipo segun la formula
		return xMin + (xMax - xMin) * bin_dec() / (Math.pow(2,longitudCromosoma) - 1);
				
	}
	
	/**
	 * Traduce una codificacion binaria de un gen (una variable) a su valor decimal
	 * @return Valor decimal del gen
	 */
	public int bin_dec(){

		// Acumulador del valor decimal
		int valorDecimal = 0;
		
		// Potencia inicial por la que multiplicamos (bit mas significativo)
		int potencia = longitudCromosoma - 1;
		
		// Procesamos el cromosoma entero
		for (int i = 0; i < longitudCromosoma; i++){
			
			// Vamos sumando potencias de 2 si genes[i] contiene un 1
			if (genes[i] == 1)
				valorDecimal += Math.pow(2, potencia);
			
			// Siguiente potencia
			potencia--;
			
		}
		
		return valorDecimal;
	}
	
	/**
	 * Realiza una copia exacta de este gen (de esta variable)
	 * @return Copia del gen
	 */
	public Gen copiaGen(){
		
		// Creamos un nuevo objeto inicializandolo igual que el actual
		Gen g = new Gen(xMin,xMax,tolerancia);
		
		// Copiamos los valores de los genes
		for (int i = 0; i < longitudCromosoma; i++)
			g.genes[i] = genes[i];
		
		// Devolvemos la copia
		return g;
	}
}
