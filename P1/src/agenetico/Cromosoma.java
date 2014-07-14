package agenetico;

public abstract class Cromosoma {

	// ATRIBUTOS
	// **************************************************************************************

	protected Gen[] variables;			// En un mismo cromosoma, tenemos distintas variables (subcromosomas)
	protected double[] fenotipos; 		// Array de fenotipos (Para las distintas variables de las funciones)
	protected double aptitud;			// Funci�n de evaluaci�n fitness adaptaci�n);
	protected double puntuacion; 		// Puntuaci�n relativa(aptitud/suma)
	protected double puntAcumulada;		// Puntuaci�n acumulada para selecci�n
	
	protected int longTotal;			// Longitud total del cromosoma (suma de las longitudes de cada variable)
	
	
	// METODOS ABSTRACTOS (Se implementan en cada cromosoma en concreto)
	// **************************************************************************************
	
	public abstract void inicializaCromosoma();	
	public abstract double evaluaCromosoma();
	public abstract double funcionEvaluacion(double[] vars);
	public abstract Cromosoma copiaCromosoma();
	public abstract int getNumVariables();
	
}
