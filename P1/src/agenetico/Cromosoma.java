package agenetico;

public abstract class Cromosoma {

	// ATRIBUTOS
	// **************************************************************************************

	protected Gen[] variables;			// En un mismo cromosoma, tenemos distintas variables (subcromosomas)
	protected double[] fenotipos; 		// Array de fenotipos (Para las distintas variables de las funciones)
	protected double aptitud;			// Función de evaluación fitness adaptación);
	protected double puntuacion; 		// Puntuación relativa(aptitud/suma)
	protected double puntAcumulada;		// Puntuación acumulada para selección
	
	protected int longTotal;			// Longitud total del cromosoma (suma de las longitudes de cada variable)
	
	
	// METODOS ABSTRACTOS (Se implementan en cada cromosoma en concreto)
	// **************************************************************************************
	
	public abstract void inicializaCromosoma();	
	public abstract double evaluaCromosoma();
	public abstract double funcionEvaluacion(double[] vars);
	public abstract Cromosoma copiaCromosoma();
	public abstract int getNumVariables();
	
}
