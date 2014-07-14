package agenetico;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class Arbol {

	// ATRIBUTOS
	// **************************************************************************************

	private String dato;			// Dato del nodo en cuestion: Operando u operador
	
	private Arbol Hi;				// Hijo izquierdo
	private Arbol Hc;				// Hijo central
	private Arbol Hd;				// Hijo derecho
	
	private int numNodos;
	private int nivel;				// Indica la profundidad actual del nodo 
	
	private int maxProfundidad;		// Indica la profundidad maxima permitida para el arbol
	

	private static final String[] conjFunciones = new String[]{"IF","EQ","PROGN2","PROGN3"};
	private static final String[] conjTerminales = new String[]{"IZQUIERDA","DERECHA","FUEGO","DIST-X","DIST-Y"};//,"FUEGO", "FUEGO"};

	// CONSTRUCTORAS
	// **************************************************************************************

	public Arbol(int maxProfundidad){
		
		this.maxProfundidad = maxProfundidad;
		
		this.dato = "";			// Dato del nodo en cuestion: Operando u operador
		
		this.Hi = null;
		this.Hc = null;
		this.Hd = null;
		
	}
	
	// Constructora por copia
	public Arbol(Arbol a){
		
		this.maxProfundidad = a.maxProfundidad;
		this.dato = a.dato;
		this.numNodos = a.numNodos;
		this.nivel = a.nivel;
		
		if (a.Hi != null)
			this.Hi = new Arbol(a.Hi);
		
		if (a.Hc != null)
			this.Hc = new Arbol(a.Hc);
		
		if (a.Hd != null)
			this.Hd = new Arbol(a.Hd);
				
	}
	
	// CONSULTORAS
	// **************************************************************************************
	
	public String[] getConjuntoFunciones(){
		return conjFunciones;
	}
	
	public String[] getConjuntoTerminales(){
		return conjTerminales;
	}
	
	public String getDato(){
		return dato;
	}
	
	public Arbol getHijoIzquierdo(){
		return Hi;
	}
	
	public Arbol getHijoCentral(){
		return Hc;
	}
	
	public Arbol getHijoDerecho(){
		return Hd;
	}
	
	public int getNumNodos(){
		return numNodos;
	}
	
	public int getMaxProfundidad(){
		return maxProfundidad;
	}
	
	public int getProfundidad(){
		return nivel;
	}
	
	
	
	// MODIFICADORAS
	// **************************************************************************************
	
	public void setDato(String dato){
		this.dato = dato;
	}
	
	public void setHijoIzquierdo(Arbol a){
		this.Hi = a;
	}
	
	public void setHijoCentral(Arbol a){
		this.Hc = a;
	}
	
	public void setHijoDerecho(Arbol a){
		this.Hd = a;
	}
	
	// METODOS
	// **************************************************************************************

	public void inicializacionCompleta(int profundidad){
		
		/*
		 * 	Inicialización completa (Full initialization)
		 * 		* Vamos tomando nodos del conjunto de funciones hasta llegar a una máxima 
		 * 		  profundidad del árbol definida previamente
		 * 		* Una vez llegados a la profundidad máxima los símbolos sólo se toman del 
		 * 		  conjunto de símbolos terminales
		 * 
		 * 	PSEUDOCODIGO:
		 *  -------------------------------------------------------------
		 * 	funcion inicializacionCompleta(profundidad) {
		 * 		si profundidad < maximaProdundidad entonces
		 * 			nodo = aleatorio(conjFunciones)
		 * 			para i = 1 hasta número de hijos del nodo hacer
		 * 				Hijoi = inicializacionCompleta(profundidad+1 )
		 * 		eoc
		 * 			nodo = aleatorio(conjTerminales)
		 * 
		 * 		devolver nodo
		 * 	}
		 */
		
		Random r = new Random();
		
		if (profundidad < maxProfundidad - 1){
			dato = conjFunciones[r.nextInt(conjFunciones.length)];
		
			Hi = new Arbol(maxProfundidad);
			Hd = new Arbol(maxProfundidad);
			
			Hi.inicializacionCompleta(profundidad + 1);
			numNodos += Hi.numNodos;
			
			Hd.inicializacionCompleta(profundidad + 1);
			numNodos += Hd.numNodos;
			
			if (dato.equals("IF") || dato.equals("PROGN3")){
				
				Hc = new Arbol(maxProfundidad);
				
				Hc.inicializacionCompleta(profundidad + 1);
				numNodos += Hc.numNodos;
			}
			
		}
		else {
			
			dato = conjTerminales[r.nextInt(conjTerminales.length)];
			numNodos = 1;

		}
			
		nivel = profundidad;
			
	}
	
	public void inicializacionCreciente(int profundidad){
		
		/*
		 * 	Inicialización creciente (Grow initialization)
		 * 		* Vamos tomando nodos del conjunto completo (funciones y terminales) 
		 * 		  hasta llegar al límite de profundidad especificado previamente
		 * 		* Una vez llegados a la profundidad máxima este método de inicialización 
		 * 		  se comporta igual que el método de inicialización completa
		 * 
		 * 	PSEUDOCODIGO:
		 *  -------------------------------------------------------------
		 *	función inicializacionCreciente(profundidad) {
		 *		si profundidad < maximaProdundidad árbol entonces
		 *			nodo = aleatorio(conjFunciones OR conjTerminales)
		 *			para i = 1 hasta número de hijos del nodo hacer
		 *				Hijoi = inicializacionCreciente(profundidad+1 )
		 *		eoc
		 *			nodo = aleatorio(conjTerminales)
		 *
		 *		devolver nodo
		 *	}
		 */
		
		Random r = new Random();
		double p = Math.random();
		
		// Si es mayor de 0'5 cogemos una funcion, sino, un terminal
		if (profundidad == 0 || (profundidad < (maxProfundidad - 1) && p > 0.5)){
						
			dato = conjFunciones[r.nextInt(conjFunciones.length)];
		
			Hi = new Arbol(maxProfundidad);
			Hd = new Arbol(maxProfundidad);
			
			Hi.inicializacionCreciente(profundidad + 1);
			numNodos += Hi.numNodos;
			
			Hd.inicializacionCreciente(profundidad + 1);
			numNodos += Hd.numNodos;
			
			if (dato.equals("IF") || dato.equals("PROGN3")){
			
				Hc = new Arbol(maxProfundidad);
				
				Hc.inicializacionCreciente(profundidad + 1);
				numNodos += Hc.numNodos;
			
			}
			
		}
		else {
			
			dato = conjTerminales[r.nextInt(conjTerminales.length)];
			numNodos = 1;

		}
		
		nivel = profundidad;
			
		
	}

	public boolean esHoja(){
		return numNodos == 1;
	}

	public Arbol buscarNodo(int n){
		
		// Iremos apilando los nodos hasta encontrar el deseado
		// El nodo buscado se encontrara en la cima de la pila
		Stack<Arbol> pilaNodos = new Stack<Arbol>();
		
		int i = 0;
		Arbol nodo;
		
		// Apilamos la raiz
		pilaNodos.push(this);
		
		// Mientras la pila no este vacia, desapilamos la cima
		while (!pilaNodos.isEmpty()) {
			
			// Desapilamos la cima
			nodo = pilaNodos.pop();
			
			// Comprobamos si es el nodo buscado, si lo es, lo devolvemos
			if (i == n) 
				return nodo;
			else {
				
				// Sino, apilamos los hijos y en la siguiente iteracion comprobamos si
				// alguno es el buscado, sino, apilaremos sus hijos y asi sucesivamente
				
				if (nodo.Hd != null)
					pilaNodos.push(nodo.Hd);
				
				if (nodo.Hc != null)
					pilaNodos.push(nodo.Hc);
				
				if (nodo.Hi != null)
					pilaNodos.push(nodo.Hi);
			}
			
			// Damos el nodo actual por no valido; pasamos al siguiente
			i++;
		}
		
		// No deberia llegar hasta este null, sino encontrar el nodo antes
		// ya que hemos escogido el minimo numero de nodos entre los arboles
		// a cruzar, por lo que ambos deben poseer dicho nodo
		return null;
		
	}
	
	public ArrayList<Arbol> buscarTerminales(){
		
		// Lista de nodos terminales
		ArrayList<Arbol> nodosTerminales = new ArrayList<Arbol>();
		
		// Pila para recorrer el arbol
		Stack<Arbol> pilaNodos = new Stack<Arbol>();
		Arbol nodo;
		
		// Apilamos la raiz
		pilaNodos.push(this);
		
		// Mientras la pila no este vacia, desapilamos la cima
		while (!pilaNodos.isEmpty()) {
			
			// Desapilamos la cima
			nodo = pilaNodos.pop();
			
			// Si el nodo es una hoja, lo añadimos a la lista de terminales
			if (nodo.esHoja()) 
				nodosTerminales.add(nodo);

			// Si posee hijos, los apilamos para procesarlos
			if (nodo.Hd != null)
				pilaNodos.push(nodo.Hd);
				
			if (nodo.Hc != null)
				pilaNodos.push(nodo.Hc);
				
			if (nodo.Hi != null)
				pilaNodos.push(nodo.Hi);
			
		}
		
		return nodosTerminales;
		
	}

	public ArrayList<Arbol> buscarFunciones(){
		
		// Lista de nodos funciones
		ArrayList<Arbol> nodosFunciones = new ArrayList<Arbol>();
		
		// Pila para recorrer el arbol
		Stack<Arbol> pilaNodos = new Stack<Arbol>();
		Arbol nodo;
		
		// Apilamos la raiz
		pilaNodos.push(this);
		
		// Mientras la pila no este vacia, desapilamos la cima
		while (!pilaNodos.isEmpty()) {
			
			// Desapilamos la cima
			nodo = pilaNodos.pop();
			
			// Si el nodo es una hoja, lo añadimos a la lista de terminales
			if (!nodo.esHoja()) 
				nodosFunciones.add(nodo);

			// Si posee hijos, los apilamos para procesarlos
			if (nodo.Hd != null)
				pilaNodos.push(nodo.Hd);
				
			if (nodo.Hc != null)
				pilaNodos.push(nodo.Hc);
				
			if (nodo.Hi != null)
				pilaNodos.push(nodo.Hi);
			
		}
		
		return nodosFunciones;
		
	}
	
	public int ajustaNodos(int profundidad){
		
		int nodosIzq;
		int nodosDcha;
		int nodosCentro;
		
		this.numNodos = 0;
		
		// Comprobamos si tienen hijos
		if (this.Hi != null){
			nodosIzq = this.Hi.ajustaNodos(profundidad + 1);
			this.numNodos += nodosIzq;		
		}
		
		if (this.Hc != null){
			nodosCentro = this.Hc.ajustaNodos(profundidad + 1);
			this.numNodos += nodosCentro;
		}
		
		if (this.Hd != null){
			nodosDcha = this.Hd.ajustaNodos(profundidad + 1);
			this.numNodos += nodosDcha;
		}
		
		// Contamos el nodo propio
		this.numNodos++;
		
		// El nivel (profundidad del nodo) solo lo actualizamos una vez
		// Si tiene hijos (no es hoja), necesariamente tiene hijo izquierdo
		this.nivel = profundidad;
		
		return this.numNodos;
	}

	public int calculaMaxProfundidad(){
		
		ArrayList<Arbol> nodosTerminales = buscarTerminales();
		
		int maxProfActual = Integer.MIN_VALUE;
		
		for (int i = 0; i < nodosTerminales.size(); i++){
			
			int prof  = nodosTerminales.get(i).nivel;
			if (prof > maxProfActual)
				maxProfActual = prof;
			
		}
		
		return maxProfActual;
	}
	
	public String toString(){

		String codigoGenerado = "";
		
		if (this.esHoja())
			codigoGenerado = this.dato;
		else if (this.dato.equals("EQ") || (this.dato.equals("PROGN2"))){
			
			codigoGenerado = "(" + this.dato;
			codigoGenerado += " (" + this.Hi.toString();
			codigoGenerado += " " + this.Hd.toString() + ")";		
			
		}
		else if (this.dato.equals("PROGN3")){
			
			codigoGenerado = "(" + this.dato;
			codigoGenerado += " (" + this.Hi.toString();
			codigoGenerado += " " + this.Hc.toString();
			codigoGenerado += " " + this.Hd.toString()+ ")";	
		}
		else if (this.dato.equals("IF")){
			
			codigoGenerado = "\t\n(" + this.dato;
			codigoGenerado += " " + this.Hi.toString();
			codigoGenerado += "\n\t " + this.Hc.toString();
			codigoGenerado += "\n\t " + this.Hd.toString()+ ")";		
		}	
		
		return codigoGenerado;
	}
	

}
