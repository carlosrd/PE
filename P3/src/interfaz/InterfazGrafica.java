package interfaz;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.math.plot.Plot2DPanel;

import agenetico.AGenetico;
import agenetico.AGenetico.tInicializacion;
import agenetico.AGenetico.tMutacion;
import agenetico.AGenetico.tSeleccion;

@SuppressWarnings("serial")
public class InterfazGrafica extends JFrame {
	
	// ATRIBUTOS 
	// *********************************************

	// Panel configuracion del algoritmo
	// ----------------------------------------------
	
	private JComboBox<String> listaMetodoInicializacion;
	private final String[] listaMetodoInicializacionOpciones = { "Completa", "Creciente", "Ramped & Half"};
	
	private JComboBox<String> listaMetodoSeleccion;
	private final String[] listaMetodoSeleccionOpciones = { "Ruleta", "Torneo", "Ranking" };
	
	private JComboBox<String> listaMetodoMutacion;
	private final String[] listaMetodoMutacionOpciones = { "Terminal Simple", "Funcional Simple", "Hibrida Simple", "SubArbol"};	
	
	private JSpinner spinnerTamPoblacion;
	private JSpinner spinnerMaxGeneraciones;
	private JSpinner spinnerMaxPartidas;
	private JSpinner spinnerMaxProfundidad;
	private JSpinner spinnerProbCruce;
	private JSpinner spinnerProbMutacion;
	private JSpinner spinnerElitismo;
	private JSpinner spinnerProbInversion;
	private JSpinner spinnerParametroBeta;

	private JButton botonCalcular;
	
	private Plot2DPanel plot;			// Panel grafica
	
	// Panel de resultados
	// ----------------------------------------------
	private JTextField txtMejorAptitud;
	private JTextField txtMediaAptitud;
	private JTextField txtPeorAptitud;
	
	private JTextField txtTotalCruces;
	private JTextField txtTotalMutaciones;
	private JTextField txtTotalInversiones;
	
	// Barra de Progreso
	// -----------------------------------------------
	JLabel etqProgreso;
	JProgressBar barraProgreso;
	
	// Modelos para los Spinners
	// -----------------------------------------------
	private final SpinnerModel modelPoblacion =
	        new SpinnerNumberModel(100, 		//initial value
	                               0, 			//min
	                               1000, 		//max
	                               10);       	//step
	
	private final SpinnerModel modelGeneraciones =
	        new SpinnerNumberModel(100, 		//initial value
	                               0, 			//min
	                               1000, 		//max
	                               10);       	//step
	
	private final SpinnerModel modelPartidas =
	        new SpinnerNumberModel(50, 		//initial value
	                               25, 			//min
	                               75, 		//max
	                               5);       	//step
	
	private final SpinnerModel modelProfundidad =
	        new SpinnerNumberModel(4, 			//initial value
	                               2, 			//min
	                               6, 			//max
	                               1);       	//step
	
	private final SpinnerModel modelCruce =
	        new SpinnerNumberModel(0.6, 		//initial value
	                               0, 			//min
	                               0.8, 		//max
	                               0.1);       //step
	
	private final SpinnerModel modelMutacion =
	        new SpinnerNumberModel(0.05, 		//initial value
	                               0, 			//min
	                               0.1, 		//max
	                               0.01);       //step
	
	private final SpinnerModel modelElitismo =
	        new SpinnerNumberModel(0, 			//initial value
	                               0, 			//min
	                               0.02, 		//max
	                               0.005);      //step
	
	private final SpinnerModel modelInversion =
	        new SpinnerNumberModel(0.05, 		//initial value
	                               0, 			//min
	                               0.1, 		//max
	                               0.01);       //step
	
	private final SpinnerModel modelBeta =
	        new SpinnerNumberModel(1.5, 		//initial value
	                               1, 			//min
	                               2, 		//max
	                               0.1);       //step
	
	// Visor de codigo
	// ----------------------------------------------
	JTextArea areaTextoCodigo;
	JTextArea visorNumerosLineasCodigo;
	JButton mostrarSimulador;
	
	// Variable para control de ventana de simulacion
	InterfazSimulacion simulador;
	
	// Variables de control de la interfaz
	// -----------------------------------------------
	
	private tInicializacion tipoInicializacion;
	private tSeleccion tipoSeleccion;
	private tMutacion tipoMutacion;
	
	// Variables a pasar al Algoritmo Genetico
	// -----------------------------------------------

	private int tamPoblacion;
	private int maxGeneraciones;
	private int maxProfundidad;
	private int maxPartidas;
	private double probCruce;
	private double probMutacion;
	private double elitismo;
	private double probInversion;
	private double beta;
	
	private AGenetico ag;
	
	// CONSTRUCTORA
	// ********************************
	
	public InterfazGrafica() { // Constructora
		inicializarInterfaz();
		//JOptionPane.showMessageDialog(null,"Practica 1 PE");
		
		// Seteamos oyente al cierre de la aplicación
		this.addWindowListener(new WindowAdapter()
		{public void windowClosing(WindowEvent e)
			{
			
			if (simulador != null && simulador.isVisible()){
				simulador.setVisible(false);
				simulador.dispose();
			}
			System.exit(0);
			}
		}
		);
	}
	

	// METODOS
	// ********************************
	
	public static void main(String[] args) {
		// obj representa el frame
		InterfazGrafica obj = new InterfazGrafica();
		obj.setVisible(true);
		obj.setEnabled(true);
		obj.setMinimumSize(new Dimension(1000,635));
		obj.setSize(1000,635);
	}

	private void inicializarInterfaz() { // Añadimos menu y panel
		
		//this.setJMenuBar(getMenuPrincipal());
		this.setContentPane(getPanelPrincipal());
		this.setTitle("[PE] Práctica 3: Programación Genética - Nuevo problema");
		
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE); // Sirve para que se termine el proceso cuando se pulsa sobre la X de cerrar

		tipoInicializacion = tInicializacion.Completa;
		tipoSeleccion = tSeleccion.Ruleta;
		tipoMutacion = tMutacion.Terminal;
		
		

		
	}
	
	private void actualizarParametros(){
		
		this.tamPoblacion = (Integer) spinnerTamPoblacion.getValue(); 
		this.maxGeneraciones = (Integer) spinnerMaxGeneraciones.getValue(); 
		this.maxProfundidad = (Integer) spinnerMaxProfundidad.getValue();
		this.maxPartidas = (Integer) spinnerMaxPartidas.getValue();
		this.probCruce = (Double) spinnerProbCruce.getValue(); 
		this.probMutacion = (Double) spinnerProbMutacion.getValue();
		this.elitismo = (Double) spinnerElitismo.getValue(); 
		this.probInversion = (Double) spinnerProbInversion.getValue();
		this.beta = (Double) spinnerParametroBeta.getValue();
		
	}

	private void actualizaNumLineasCodigo(){
		for (int i = visorNumerosLineasCodigo.getLineCount(); i <= areaTextoCodigo.getLineCount(); i++)
			visorNumerosLineasCodigo.append( i +"\n");
	}
	
	/* Este metodo pasa a ser de AGenetico para evitar condiciones
	 * de carrera e inconsistencias de memoria por ejecuciones concurrentes
	public void actualizaGrafica(){
		
		synchronized(ag){
		double[] a = ag.getMediaAptitud();
		double[] b = ag.getMejorAptitudAbsoluto();
		double[] c = ag.getMejorAptitudPorGeneracion();
		
		int maxGeneraciones = ag.getMaxGeneraciones();
		double[] enumerado = new double[maxGeneraciones];
		for(int i = 0; i < maxGeneraciones; i++)
			enumerado[i] = i;
		

    	plot.removeAllPlots();
		plot.addLinePlot("Mejor absoluto", enumerado, b);
		plot.addLinePlot("Media de la generacion", enumerado, a);
		plot.addLinePlot("Mejor de la generacion", enumerado, c);
		plot.addLegend("SOUTH");
		plot.revalidate();
		
		txtMejorCromosoma.setText(ag.getMejorFenotipo());
		
		txtMejorAptitud.setText(String.valueOf(b[maxGeneraciones-1]));
		txtMediaAptitud.setText(String.valueOf(a[maxGeneraciones-1]));
		txtPeorAptitud.setText(String.valueOf(ag.getPeorAptitud()));
		
		txtTotalCruces.setText(String.valueOf(ag.getTotalCruces()));
		txtTotalMutaciones.setText(String.valueOf(ag.getTotalMutaciones()));
		txtTotalInversiones.setText(String.valueOf(ag.getTotalInversiones()));
		}
		
	}*/

	// PANELES
	// ************************************************************************************
	
	private JPanel getPanelPrincipal(){
		
		JPanel panelPrincipal = new JPanel();
		/*BorderLayout b = new BorderLayout();
		panelPrincipal.setLayout(b);*/

		panelPrincipal.setLayout(new BorderLayout(5,5));

		// Panel principal: Combinado de Panel configuracion (izq) + panel grafica (dcha)
		JSplitPane panelCombinado = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, getPanelIzquierdo(),getPanelGrafica());
		panelCombinado.setResizeWeight(0);
		panelCombinado.setOneTouchExpandable(true);

		//panelPrincipal.add(panelCombinado,"Center");
		
		panelPrincipal.add(getPanelTabulado(),"Center");
		//panelPrincipal.add(getPanelResultados(), "Center");
		//panelPrincipal.add(getPanelGrafica(),"Center");
		//panelPrincipal.add(getPanelConfiguracion(),"West");
		
		
		// PANEL IZQUIERDO
		
		TitledBorder tituloSeccionConfig;
		tituloSeccionConfig = BorderFactory.createTitledBorder("Configuración");
		tituloSeccionConfig.setTitleJustification(TitledBorder.LEFT);
		
		JPanel pComb = new JPanel();
		pComb.setBorder(tituloSeccionConfig);
		pComb.setLayout(new BorderLayout());
		pComb.add(getPanelIzquierdo(),"North");
		
		pComb.add(getPanelProgreso(),"South");
		
		panelPrincipal.add(pComb,"West");//getPanelIzquierdo(),"West");
		
		panelPrincipal.validate();

		return panelPrincipal;
	}

	private JTabbedPane getPanelTabulado(){
		
		JTabbedPane panelTabulado = new JTabbedPane();
		
		panelTabulado.add("Codigo generado", getPanelCodigo());
			
		panelTabulado.add("Resultados",getPanelGraficaYResultados());
		

		return panelTabulado;
	}
	
	private JPanel getPanelGraficaYResultados(){

		JPanel panelResultados = new JPanel();
		
		panelResultados.setLayout(new BorderLayout());
		
		panelResultados.add(getPanelGrafica(), "Center");
		
		panelResultados.add(getPanelConsola(), "South");
		
		return panelResultados;
		
	}
	
	private JPanel getPanelCodigo(){
		
		// Preparar el area para escritura de codigo:
		// -----------------------------------------------------------------------------
		areaTextoCodigo = new JTextArea("> Codigo generado por el mejor cromosoma:\n",8,1);
		areaTextoCodigo.setFont(new Font("MonoSpaced", Font.PLAIN, 12));
		areaTextoCodigo.setEditable(false);
					
		// Preparar el area visor de numero de linea
		// -----------------------------------------------------------------------------
		visorNumerosLineasCodigo = new JTextArea("");
		visorNumerosLineasCodigo.setColumns(4);
		visorNumerosLineasCodigo.setEditable(false);

		visorNumerosLineasCodigo.setFont(new Font("MonoSpaced", Font.PLAIN, 12));
		visorNumerosLineasCodigo.setBackground(new Color(237,237,237));
		visorNumerosLineasCodigo.setMaximumSize(new Dimension(0,10));
		actualizaNumLineasCodigo();
		
		// Preparar el panel conjunto
		// -----------------------------------------------------------------------------
		JPanel panelTotal = new JPanel();
		panelTotal.setLayout(new BorderLayout());
		
		panelTotal.add(visorNumerosLineasCodigo,"West");
		panelTotal.add(areaTextoCodigo,"Center");
		
		JScrollPane scrollPane = new JScrollPane(panelTotal);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		
		JPanel panelCombinado = new JPanel();
		panelCombinado.setLayout(new BorderLayout());
		
		panelCombinado.add(scrollPane,"Center");
		
		JPanel panelInferiorBotones = new JPanel();
		panelInferiorBotones.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.anchor = GridBagConstraints.LINE_START; // bottom of space
		c.insets = new Insets(5,10,0,10);  //top padding
		c.ipady = 5; 		
		c.fill = GridBagConstraints.HORIZONTAL;
		
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 7;
		c.weightx = 0;

		// MEJOR APTITUD
		// ------------------------------------
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.weightx = 0;
		
		mostrarSimulador = new JButton("Iniciar ejecución...");
		mostrarSimulador.setEnabled(false);
		mostrarSimulador.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (simulador != null){
					simulador.dispose();
					simulador.setVisible(false);
				}
	
				simulador = new InterfazSimulacion(ag.getMejorCromosomaAbsoluto());
				simulador.setVisible(true);
				simulador.setEnabled(true);
				simulador.setResizable(false);
				simulador.setSize(800,630);
				simulador.requestFocus();
			}
		});
		
		panelInferiorBotones.add(mostrarSimulador, c);
		
		
		panelCombinado.add(panelInferiorBotones,"South");
		return panelCombinado;
	}
	
	private JPanel getPanelConsola(){
		
		JPanel panelConsola = new JPanel();
		
		panelConsola.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.anchor = GridBagConstraints.LINE_START; // bottom of space
		c.insets = new Insets(5,10,0,10);  //top padding
		c.ipady = 5; 		
		c.fill = GridBagConstraints.HORIZONTAL;
		
		// MEJOR APTITUD
		// ------------------------------------
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.weightx = 0;
		panelConsola.add(new JLabel("Mejor aptitud: "), c);
		
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 1;
		c.weightx = 1;
		txtMejorAptitud = new JTextField();
		panelConsola.add(txtMejorAptitud, c);
		
		// MEDIA APTITUD
		// ------------------------------------
		c.gridx = 2;
		c.gridy = 0;
		c.gridwidth = 1;
		c.weightx = 0;
		panelConsola.add(new JLabel("Media aptitud: "), c);
		
		c.gridx = 3;
		c.gridy = 0;
		c.gridwidth = 1;
		c.weightx = 1;
		txtMediaAptitud = new JTextField();
		panelConsola.add(txtMediaAptitud, c);
		
		// PEOR APTITUD
		// ------------------------------------
		c.gridx = 4;
		c.gridy = 0;
		c.gridwidth = 1;
		c.weightx = 0;
		panelConsola.add(new JLabel("Peor aptitud: "), c);
		
		c.gridx = 5;
		c.gridy = 0;
		c.gridwidth = 1;
		c.weightx = 1;
		txtPeorAptitud = new JTextField();
		panelConsola.add(txtPeorAptitud, c);

		
		// TOTAL CRUCES
		// ------------------------------------
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.weightx = 0;
		panelConsola.add(new JLabel("Total Cruces: "), c);
		
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 1;
		c.weightx = 1;
		txtTotalCruces = new JTextField();
		panelConsola.add(txtTotalCruces, c);
		
		// TOTAL MUTACIONES
		// ------------------------------------
		c.gridx = 2;
		c.gridy = 1;
		c.gridwidth = 1;
		c.weightx = 0;
		panelConsola.add(new JLabel("Total Mutaciones: "), c);
		
		c.gridx = 3;
		c.gridy = 1;
		c.gridwidth = 1;
		c.weightx = 1;
		txtTotalMutaciones = new JTextField();
		panelConsola.add(txtTotalMutaciones, c);
		
		// TOTAL INVERSIONES
		// ------------------------------------
		c.gridx = 4;
		c.gridy = 1;
		c.gridwidth = 1;
		c.weightx = 0;
		panelConsola.add(new JLabel("Total Inversiones: "), c);
		
		c.gridx = 5;
		c.gridy = 1;
		c.gridwidth = 1;
		c.weightx = 1;
		txtTotalInversiones = new JTextField();
		panelConsola.add(txtTotalInversiones, c);
		
		TitledBorder tituloSeccionResultados;
		tituloSeccionResultados = BorderFactory.createTitledBorder("Resultados");
		tituloSeccionResultados.setTitleJustification(TitledBorder.LEFT);	
		
		panelConsola.setBorder(tituloSeccionResultados);
		
		return panelConsola;
	}
	
	private JPanel getPanelGrafica(){
		
		TitledBorder tituloSeccionGrafica;
		tituloSeccionGrafica = BorderFactory.createTitledBorder("Representacion gráfica");
		tituloSeccionGrafica.setTitleJustification(TitledBorder.LEFT);	
		
		plot = new Plot2DPanel();
		plot.setBorder(tituloSeccionGrafica);
		plot.setMaximumSize(new Dimension(900,900));
		
		return plot;
		
	}
	
	
	
	private JPanel getPanelIzquierdo(){
				
		JPanel panelIzquierdo = new JPanel();
		
		panelIzquierdo.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.anchor = GridBagConstraints.LINE_START; // bottom of space
		c.insets = new Insets(5,10,0,10);  //top padding
		c.ipady = 5; 		
		c.fill = GridBagConstraints.HORIZONTAL;
		
		// FORMULARIO: Configuracion
		// ·································································
		
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.LINE_START;
		panelIzquierdo.add(new JLabel(" > Introduzca los parámetros de configuración del algoritmo:"), c);
		
		// OPCION: Tamaño de la poblacion
		// ·································································
		
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		panelIzquierdo.add(new JLabel("Tamaño de la población:"), c);
		
		spinnerTamPoblacion = new JSpinner(modelPoblacion);
		spinnerTamPoblacion.setToolTipText("Valores admitidos: [0 .. 1000]");
		
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 1;

		panelIzquierdo.add(spinnerTamPoblacion, c);
		
		
		// OPCION: Max generaciones
		// ·································································
	
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		panelIzquierdo.add(new JLabel("Nº máximo generaciones:"), c);
		
		spinnerMaxGeneraciones = new JSpinner(modelGeneraciones);
		spinnerMaxGeneraciones.setToolTipText("Valores admitidos: [0 .. 1000]");
	
		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 1;
		panelIzquierdo.add(spinnerMaxGeneraciones, c);
		
		// OPCION: Max Partidas
		// ·································································
	
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 1;
		panelIzquierdo.add(new JLabel("Nº partidas evaluación:"), c);
		
		spinnerMaxPartidas = new JSpinner(modelPartidas);
		spinnerMaxPartidas.setToolTipText("Valores admitidos: [25 .. 75]");
	
		c.gridx = 1;
		c.gridy = 3;
		c.gridwidth = 1;
		panelIzquierdo.add(spinnerMaxPartidas, c);
		
		
		// COMBO BOX: Lista Metodos Inicializacion
		// ·································································
		
		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 1;
		panelIzquierdo.add(new JLabel("Método de inicialización:"), c);
		
		listaMetodoInicializacion = new JComboBox<String>(listaMetodoInicializacionOpciones);
		listaMetodoInicializacion.setSelectedIndex(0);
		listaMetodoInicializacion.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				
				int metodoInicializacion = listaMetodoInicializacion.getSelectedIndex() + 1;
				
				switch (metodoInicializacion){
				
					default:
							
					case 1: tipoInicializacion = tInicializacion.Completa;
							break;
					case 2: tipoInicializacion = tInicializacion.Creciente;
							break;
					case 3: tipoInicializacion = tInicializacion.Hibrida;
							break; 
							
				}
				
			}
		}
		);
		
		c.gridx = 1;
		c.gridy = 4;
		c.gridwidth = 1;
		c.ipady = 0; 
		panelIzquierdo.add(listaMetodoInicializacion, c);
		
		
		// OPCION: Maxima Profundidad
		// ·································································
		
		c.gridx = 0;
		c.gridy = 5;
		c.gridwidth = 1;
		c.ipady = 5; 
		panelIzquierdo.add(new JLabel("Profundidad máxima:"), c);
		
		spinnerMaxProfundidad = new JSpinner(modelProfundidad);
		spinnerMaxProfundidad.setToolTipText("Valores admitidos: [2..5]");
		
		c.gridx = 1;
		c.gridy = 5;
		c.gridwidth = 1;

		panelIzquierdo.add(spinnerMaxProfundidad, c);
		
		// COMBO BOX: Lista Metodos Seleccion
		// ·································································
		
		c.gridx = 0;
		c.gridy = 6;
		c.gridwidth = 1;
		panelIzquierdo.add(new JLabel("Método de selección:"), c);
		
		listaMetodoSeleccion = new JComboBox<String>(listaMetodoSeleccionOpciones);
		listaMetodoSeleccion.setSelectedIndex(0);
		listaMetodoSeleccion.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				
				int metodoSeleccion = listaMetodoSeleccion.getSelectedIndex() + 1;
				spinnerParametroBeta.setEnabled(false);
				
				switch (metodoSeleccion){
				
					default:
							
					case 1: tipoSeleccion = tSeleccion.Ruleta;
							break;
					case 2: tipoSeleccion = tSeleccion.Torneo;
							break;
					case 3: tipoSeleccion = tSeleccion.Ranking;
							spinnerParametroBeta.setEnabled(true);
							break;
							
				}
				
			}
		}
		);
		
		c.gridx = 1;
		c.gridy = 6;
		c.gridwidth = 1;
		c.ipady = 0; 
		panelIzquierdo.add(listaMetodoSeleccion, c);
		
		// OPCION: Parametro Beta
		// ·································································
		
		c.gridx = 0;
		c.gridy = 7;
		c.gridwidth = 1;
		c.ipady = 3; 
		panelIzquierdo.add(new JLabel("Parametro Beta:"), c);
		
		spinnerParametroBeta = new JSpinner(modelBeta);
		spinnerParametroBeta.setToolTipText("Valores admitidos: [1..2]");
		spinnerParametroBeta.setEnabled(false);
		
		c.gridx = 1;
		c.gridy = 7;
		c.gridwidth = 1;

		panelIzquierdo.add(spinnerParametroBeta, c);
		
		// OPCION: Probabilidad de cruce
		// ·································································
		
		c.gridx = 0;
		c.gridy = 8;
		c.gridwidth = 1;
		c.ipady = 5;
		panelIzquierdo.add(new JLabel("Probabilidad de cruce:"), c);
		
		spinnerProbCruce = new JSpinner(modelCruce);
		spinnerProbCruce.setToolTipText("Valores admitidos: [0 .. 0'8]");
		
		c.gridx = 1;
		c.gridy = 8;
		c.gridwidth = 1;
		panelIzquierdo.add(spinnerProbCruce, c);
		
		// OPCION: Probabilidad de mutacion
		// ·································································
		
		c.gridx = 0;
		c.gridy = 9;
		c.gridwidth = 1;
		c.ipady = 5;
		panelIzquierdo.add(new JLabel("Probabilidad de mutación:"), c);
		
		spinnerProbMutacion = new JSpinner(modelMutacion);
		spinnerProbMutacion.setToolTipText("Valores admitidos: [0 .. 0'1]");
		
		c.gridx = 1;
		c.gridy = 9;
		c.gridwidth = 1;
		panelIzquierdo.add(spinnerProbMutacion, c);
		
		// COMBO BOX: Lista Metodos Cruce
		// ·································································
		
		c.gridx = 0;
		c.gridy = 10;
		c.gridwidth = 1;
		c.ipady = 5; 
		panelIzquierdo.add(new JLabel("Método de mutación:"), c);
		
		listaMetodoMutacion = new JComboBox<String>(listaMetodoMutacionOpciones);
		listaMetodoMutacion.setSelectedIndex(0);
		listaMetodoMutacion.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				
				int metodoMutacion = listaMetodoMutacion.getSelectedIndex() + 1;
				
				switch (metodoMutacion){
				
					default:
					case 1: tipoMutacion = tMutacion.Terminal;
							break;
					case 2: tipoMutacion = tMutacion.Funcional;
							break;
					case 3: tipoMutacion = tMutacion.Hibrida;
							break;
					case 4: tipoMutacion = tMutacion.SubArbol;
							break;
					
				}
				
			}
		}
		);
		
		c.gridx = 1;
		c.gridy = 10;
		c.gridwidth = 1;
		c.ipady = 0; 
		panelIzquierdo.add(listaMetodoMutacion, c);
		
		// OPCION: Elitismo
		// ·································································
		
		c.gridx = 0;
		c.gridy = 11;
		c.gridwidth = 1;
		c.ipady = 5; 
		panelIzquierdo.add(new JLabel("Elitismo:"), c);

		spinnerElitismo = new JSpinner(modelElitismo);
		spinnerElitismo.setToolTipText("Valores admitidos: [0 .. 0'02]");
		
		c.gridx = 1;
		c.gridy = 11;
		c.gridwidth = 1;
		panelIzquierdo.add(spinnerElitismo, c);
		
		// OPCION: Inversion
		// ·································································
		
		c.gridx = 0;
		c.gridy = 12;
		c.gridwidth = 1;
		//c.ipady = 5; 
		panelIzquierdo.add(new JLabel("Inversion:"), c);
		
		spinnerProbInversion = new JSpinner(modelInversion);
		spinnerProbInversion.setToolTipText("Valores admitidos: Reales [0 .. 0'1]");
		
		c.gridx = 1;
		c.gridy = 12;
		c.gridwidth = 1;
		panelIzquierdo.add(spinnerProbInversion, c);
		
		
		// BOTON - Evaluar
		// ·································································
		
		botonCalcular = new JButton("Evaluar", new ImageIcon(this.getClass().getResource("/resources/icons/menu/iconCompileMenu.png")));
		botonCalcular.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {

				actualizarParametros();
				ag = new AGenetico(	tamPoblacion,
									maxGeneraciones,
									probCruce,
									probMutacion,
									maxProfundidad,
									maxPartidas,
									tipoInicializacion,
									tipoSeleccion,
									tipoMutacion);

				// Le cargamos los parametros restantes y le pasamos la grafica y los JTextField
				// donde mostrar los resultados
				ag.preparaAlgoritmo(elitismo, 
									probInversion, 
									beta, 
									etqProgreso, 
									barraProgreso,
									plot, 
									txtMejorAptitud, 
									txtMediaAptitud, 
									txtPeorAptitud, 
									txtTotalCruces, 
									txtTotalMutaciones, 
									txtTotalInversiones,
									areaTextoCodigo,
									visorNumerosLineasCodigo);
			
				// Ejecutamos el Algoritmo Genetico en un Thread a parte (para evitar
				// congelacion de la GUI y llevar seguimiento de la ejecucion con la barra de progreso)
				ag.execute();
				//ag.ejecutaAlgoritmo(elitismo,probInversion,beta);
			}
			
		});
		
		JPanel panelBoton = new JPanel();
		
		panelBoton.setLayout(new FlowLayout());
		panelBoton.add(botonCalcular);
		
		c.gridx = 0;
		c.gridy = 13;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.CENTER;

		panelIzquierdo.add(panelBoton, c);
		
		
		return panelIzquierdo;
		
	}
	
	private JPanel getPanelProgreso(){
		
		JPanel panelProgreso = new JPanel();
		panelProgreso.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.anchor = GridBagConstraints.LINE_START; // bottom of space
		c.insets = new Insets(5,10,5,5);  //top padding
		
		c.fill = GridBagConstraints.HORIZONTAL;
		
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.weighty = 0;
		c.anchor = GridBagConstraints.LAST_LINE_START;
		
		etqProgreso = new JLabel("Progreso evaluación...");
		etqProgreso.setVisible(false);
		panelProgreso.add(etqProgreso, c);
		
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 2;
		c.weightx = 1;
		c.ipady = 5; 
		c.anchor = GridBagConstraints.CENTER;
		
		barraProgreso = new JProgressBar(0,100);
		barraProgreso.setValue(0);
		barraProgreso.setStringPainted(true);
		barraProgreso.setVisible(false);
		barraProgreso.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent arg0) {
				
				// Bloqueo de los botones y enseñamos la barra de progreso
				// mientras la barra este entre el 1 y el 99
				int progreso = barraProgreso.getValue();
				if (progreso != 0 && progreso != 100){
					botonCalcular.setEnabled(false);
					mostrarSimulador.setEnabled(false);
				}
				else {
					// Sino, es 0 o 100; escondemos la barra y liberamos los botones
					botonCalcular.setEnabled(true);
					mostrarSimulador.setEnabled(true);
				}
				
			}
			
		});

		panelProgreso.add(barraProgreso, c);
		
		return panelProgreso;
	
	}


}
