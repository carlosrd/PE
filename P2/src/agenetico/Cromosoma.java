package agenetico;

import java.util.ArrayList;
import java.util.Random;

public class Cromosoma {

	// ATRIBUTOS
	// **************************************************************************************

	protected int[] unidades;			// Cromosoma que representa las unidadeses hospitalarias
	protected int numUnidades;			// Numero de unidadeses el cromosoma 
	
	protected double aptitud;			// Función de evaluación fitness adaptación);
	protected double puntuacion; 		// Puntuación relativa(aptitud/suma)
	protected double puntAcumulada;		// Puntuación acumulada para selección
	
	protected int[][] distancia;		// Matriz de distancias (para evaluacion del cromosoma)
	protected int[][] flujo;			// Matriz de flujos (para evaluacion del cromosoma)
	
	// CONSTRUCTORAS
	// **************************************************************************************
	
	public Cromosoma(int tamCromosoma, int[][] distancia, int[][] flujo){
		
		this.numUnidades = tamCromosoma;
		
		ArrayList<Integer> permutacion = new ArrayList<Integer>();
		
		for (int i = 0; i < numUnidades; i++)
			permutacion.add(i);
		
		Random r = new Random();
		unidades = new int[numUnidades];
		
		for (int i = 0; i < numUnidades; i++){
			
			int posAleatoria = r.nextInt(numUnidades-i);
			
			unidades[i] = permutacion.remove(posAleatoria);
			
		}
		
		this.distancia = distancia;
		this.flujo = flujo;
		
	}
	
	// Constructora por copia
	public Cromosoma(Cromosoma c){
		
		numUnidades = c.numUnidades;
		
		unidades = new int[numUnidades];
		
		for (int i = 0; i < numUnidades; i++){
			unidades[i] = c.unidades[i];
		}
		
		aptitud = c.aptitud;
		puntuacion = c.puntuacion;
		puntAcumulada = c.puntAcumulada;
	
		distancia = c.distancia;
		flujo = c.flujo;
		
	}
	
	// METODOS 
	// **************************************************************************************
	
	/**
	 * Evalua el cromosoma segun la funcion dada a minimizar
	 * @return
	 */
	public int evaluaCromosoma(){
		
		int totalAptitud = 0;
		
		for (int i = 0; i < numUnidades; i++){
			for (int j = 0; j < numUnidades; j++){
				
				totalAptitud += flujo[i][j] * distancia[unidades[i]][unidades[j]];
						
			}
		}
		
		return totalAptitud;
	}

	/**
	 * Devuelve un String con un identificador unico de cromosoma. Para los valores mayores
	 * que 10, las decenas se sustituyen por letras para no confundir secuencias. Ej: Una
	 * secuencia con "...,1,10,11,.." sin codificar queda "11011" lo que conlleva a confusiones.
	 * Con esta funcion quedaria codificado: "1A0A1"
	 * @return
	 */
	public String getHash(){
		
		String fenotipo = "";
		
		for (int i = 0; i < numUnidades; i++){
			
			int unidad = unidades[i];
			
			if (unidad < 10)
				fenotipo += unidad;
			else if (unidad < 20){
					
					unidad = unidad % 10;
					fenotipo += "A" + unidad;
			}
			else if (unidad < 30){
				
					unidad = unidad % 10;
					fenotipo += "B" + unidad;
			}
			else if (unidad < 40){
					
					unidad = unidad % 10;
					fenotipo += "C" + unidad;
		}
		}
		return fenotipo;
	}
	
	/**
	 * Devuelve el fenotipo formateado con comas para mostrarlo
	 * @return
	 */
	public String getFenotipo(){
		
		String fenotipo = "";
		
		for (int i = 0; i < numUnidades; i++){
		
			fenotipo += unidades[i];
			if (i != numUnidades - 1)
				fenotipo += ", ";
		}
		
		return fenotipo;
		
	}
}
