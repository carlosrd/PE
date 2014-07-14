package agenetico;

public class EvaluacionArbol {
	
	// ATRIBUTOS
	// **************************************************************************************

	private int valor;
	private int tiempo;
	private int distancia;
	//private boolean termina;
	
	// CONSTRUCTORA
	// **************************************************************************************

	public EvaluacionArbol(int valor, int tiempo, int distancia) { //, boolean termina) {
		
		this.valor = valor;
		this.tiempo = tiempo;
		this.distancia = distancia;
		//this.termina = termina;
	}
	
	// CONSULTORAS
	// **************************************************************************************

	public int getValor() {
		return valor;
	}

	public int getTiempo() {
		return tiempo;
	}

	public int getDistancia() {
		return distancia;
	}
	
/*	public boolean getFinDeJuego() {
		return termina;
	}*/
	
	// MODIFICADORAS
	// **************************************************************************************

	public void setValor(int valor) {
		this.valor = valor;
	}
}