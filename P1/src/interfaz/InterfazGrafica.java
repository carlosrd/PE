package interfaz;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.math.plot.Plot2DPanel;

import agenetico.AGenetico;

@SuppressWarnings("serial")
public class InterfazGrafica extends JFrame {
	
	// ATRIBUTOS 
	// *********************************************
	
	private JComboBox<String> listaFunciones;
	private final String[] listaFuncionesOpciones = { "1","2","3","4","5" };
	
	private JComboBox<String> listaMetodoCruce;
	private final String[] listaMetodoCruceOpciones = { "Monopunto" };
	
	private JComboBox<String> listaMetodoSeleccion;
	private final String[] listaMetodoSeleccionOpciones = { "Ruleta", "Torneo" };
	
	private JTextField txtTamPoblacion;
	private JTextField txtMaxGeneraciones;
	private JTextField txtProbCruce;
	private JTextField txtProbMutacion;
	private JTextField txtTolerancia;
	private JTextField txtValorN;
	private JTextField txtElitismo;
	
	private JTextArea areaTextoResultados;

	private Border defaultBorder;		// Borde por defecto de los JTextfield. Sirve para restablecerlo una vez el
										// el campo correspondiente contiene un dato correcto.
	
	private Plot2DPanel plot;			// Panel grafica
	
	
	// Variables de control de la interfaz
	// -----------------------------------------------
	
	private int metodoSeleccion;
	
	// Variables a pasar al Algoritmo Genetico
	// -----------------------------------------------

	private int funcionElegida;
	private int numVariables;
	private int tamPoblacion;
	private int maxGeneraciones;
	private double probCruce;
	private double probMutacion;
	private double tolerancia;
	private double elitismo;

	private AGenetico ag;
	

	// CONSTRUCTORA
	// ********************************
	
	public InterfazGrafica() { // Constructora
		inicializarInterfaz();
		//JOptionPane.showMessageDialog(null,"Practica 1 PE");
	}
	

	// METODOS
	// ********************************
	
	public static void main(String[] args) {
		// obj representa el frame
		InterfazGrafica obj = new InterfazGrafica();
		obj.setVisible(true);
		obj.setEnabled(true);
		obj.setMinimumSize(new Dimension(1000,600));
		obj.setSize(1000,600);
	}

	private void inicializarInterfaz() { // Añadimos menu y panel
		//this.setJMenuBar(getMenuPrincipal());
		this.setContentPane(getPanelPrincipal());
		this.setTitle("[PE] Práctica 1: Algoritmo Genético Simple");
		
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE); // Sirve para que se termine el proceso cuando se pulsa sobre la X de cerrar

		funcionElegida = 1;
		numVariables = 1;
		metodoSeleccion = 1;
		
	}
	
	private JPanel getPanelPrincipal(){
		
		JPanel panelPrincipal = new JPanel();
		/*BorderLayout b = new BorderLayout();
		panelPrincipal.setLayout(b);*/

		panelPrincipal.setLayout(new BorderLayout());

		// Panel principal: Combinado de Panel configuracion (izq) + panel grafica (dcha)
		JSplitPane panelCombinado = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, getPanelIzquierdo(),getPanelGrafica());
		panelCombinado.setResizeWeight(0);
		panelCombinado.setOneTouchExpandable(true);

		panelPrincipal.add(panelCombinado,"Center");

		panelPrincipal.validate();

		return panelPrincipal;
	}
	
	private JPanel getPanelConfiguracion(){
		
		
		
		// Etiqueta superior explicativa del panel
		// ----------------------------------------------------------------
		
		JPanel etqSuperior = new JPanel();
		etqSuperior.setLayout(new GridLayout(1,1));
		etqSuperior.add(new JLabel("Introduzca los parámetros de configuración del algoritmo:"));
		
		// Lo introducimos en un FlowLayout para mejor visualizacion
		JPanel panelAjustado = new JPanel();
		FlowLayout fl = new FlowLayout();
		fl.setAlignment(FlowLayout.LEFT);
		panelAjustado.setLayout(fl);
		panelAjustado.add(etqSuperior);
	
		
		// Formulario de configuracion
		// ----------------------------------------------------------------
		
		JPanel formularioConfiguracion = new JPanel();
		
		formularioConfiguracion.setLayout(new GridLayout(10,2,7,4));
		
		// COMBO BOX: Lista de funciones
		// ·································································
		
		formularioConfiguracion.add(new JLabel("Función:"));
		
		listaFunciones = new JComboBox<String>(listaFuncionesOpciones);
		listaFunciones.setSelectedIndex(0);
		listaFunciones.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				
				funcionElegida = listaFunciones.getSelectedIndex() + 1;
				
				if (funcionElegida == 4)
					txtValorN.setEnabled(true);
				else
					txtValorN.setEnabled(false);
			}
		}
		);
		
		formularioConfiguracion.add(listaFunciones);
		
		// OPCION: Tamaño de la poblacion
		// ·································································
		
		formularioConfiguracion.add(new JLabel("Tamaño de la población:"));
		
		txtTamPoblacion = new JTextField("100");
		txtTamPoblacion.setToolTipText("Valores admitidos: Enteros [1..1000]");
		
		// Insertamos un listener para que cambie el borde a rojo si los datos son incorrectos
		txtTamPoblacion.addCaretListener(new CaretListener(){
			
			@Override
			public void caretUpdate(CaretEvent arg0) {
				
				int tamPoblacion = -1;
				boolean esEntero = true;
				
				try {
					tamPoblacion = Integer.parseInt(txtTamPoblacion.getText());
				} catch (Exception e){
					esEntero = false;
				}
				
				if (tamPoblacion < 1 || tamPoblacion > 1000 || !esEntero){
					Border border = BorderFactory.createLineBorder(Color.red,2);
					txtTamPoblacion.setBorder(border);
				}
				else
					txtTamPoblacion.setBorder(defaultBorder);
			}
			
		});
		
		
		formularioConfiguracion.add(txtTamPoblacion);
		
		// OPCION: Max generaciones
		// ·································································
		
		formularioConfiguracion.add(new JLabel("Nº máximo generaciones:"));
		
		txtMaxGeneraciones = new JTextField("100");
		txtMaxGeneraciones.setToolTipText("Valores admitidos: Enteros [1..1000]");
		
		// Insertamos un listener para que cambie el borde a rojo si los datos son incorrectos
		txtMaxGeneraciones.addCaretListener(new CaretListener(){
			
			@Override
			public void caretUpdate(CaretEvent arg0) {
				
				int maxGeneraciones = -1;
				boolean esEntero = true;
				
				try {
					maxGeneraciones = Integer.parseInt(txtMaxGeneraciones.getText());
				} catch (Exception e){
					esEntero = false;
				}
				
				if (maxGeneraciones < 1 || maxGeneraciones > 1000 || !esEntero){
					Border border = BorderFactory.createLineBorder(Color.red,2);
					txtMaxGeneraciones.setBorder(border);
				}
				else
					txtMaxGeneraciones.setBorder(defaultBorder);
			}
			
		});
		
		
		formularioConfiguracion.add(txtMaxGeneraciones);
		
		
		// OPCION: Probabilidad de cruce
		// ·································································
		
		formularioConfiguracion.add(new JLabel("Probabilidad de cruce:"));
		
		txtProbCruce = new JTextField("0.6");
		txtProbCruce.setToolTipText("Valores admitidos: Reales [0'5 .. 0'8]");
		
		// Insertamos un listener para que cambie el borde a rojo si los datos son incorrectos
		txtProbCruce.addCaretListener(new CaretListener(){
			
			@Override
			public void caretUpdate(CaretEvent arg0) {
				
				double probCruce = -1;
				boolean esReal = true;
				
				try {
					probCruce = Double.parseDouble(txtProbCruce.getText());
				} catch (Exception e){
					esReal = false;
				}
				
				if (probCruce < 0.5 || probCruce > 0.8 || !esReal){
					Border border = BorderFactory.createLineBorder(Color.red,2);
					txtProbCruce.setBorder(border);
				}
				else
					txtProbCruce.setBorder(defaultBorder);
			}
			
		});

		formularioConfiguracion.add(txtProbCruce);
		
		// OPCION: Probabilidad de mutacion
		// ·································································
		
		formularioConfiguracion.add(new JLabel("Probabilidad de mutación:"));
		
		txtProbMutacion = new JTextField("0.05");
		txtProbMutacion.setToolTipText("Valores admitidos: Reales [0'01 .. 0'1]");
		
		// Insertamos un listener para que cambie el borde a rojo si los datos son incorrectos
		txtProbMutacion.addCaretListener(new CaretListener(){
			
			@Override
			public void caretUpdate(CaretEvent arg0) {
				
				double probMutacion = -1;
				boolean esReal = true;
				
				try {
					probMutacion = Double.parseDouble(txtProbMutacion.getText());
				} catch (Exception e){
					esReal = false;
				}
				
				if (probMutacion < 0.01 || probMutacion > 0.1 || !esReal){
					Border border = BorderFactory.createLineBorder(Color.red,2);
					txtProbMutacion.setBorder(border);
				}
				else
					txtProbMutacion.setBorder(defaultBorder);
			}
			
		});
		
		
		formularioConfiguracion.add(txtProbMutacion);
		
		// OPCION: Probabilidad de mutacion
		// ·································································
		
		formularioConfiguracion.add(new JLabel("Tolerancia:"));
		
		txtTolerancia = new JTextField("0.0001");
		txtTolerancia.setToolTipText("Valores admitidos: Reales [1E-9 .. 0'1]");
		
		// Insertamos un listener para que cambie el borde a rojo si los datos son incorrectos
		txtTolerancia.addCaretListener(new CaretListener(){
			
			@Override
			public void caretUpdate(CaretEvent arg0) {
				
				double tolerancia = -1;
				boolean esReal = true;
				
				try {
					tolerancia = Double.parseDouble(txtTolerancia.getText());
				} catch (Exception e){
					esReal = false;
				}
				
				if (tolerancia < 1E-9 || tolerancia > 0.1 || !esReal){
					Border border = BorderFactory.createLineBorder(Color.red,2);
					txtTolerancia.setBorder(border);
				}
				else
					txtTolerancia.setBorder(defaultBorder);
			}
			
		});
		
		formularioConfiguracion.add(txtTolerancia);
		
		// OPCION: Probabilidad de mutacion
		// ·································································
		
		formularioConfiguracion.add(new JLabel("Valor de N:"));
		
		txtValorN = new JTextField("1");
		txtValorN.setEnabled(false);
		txtValorN.setToolTipText("Valores admitidos: Enteros [1..8]");
		
		// Insertamos un listener para que cambie el borde a rojo si los datos son incorrectos
		txtValorN.addCaretListener(new CaretListener(){
			
			@Override
			public void caretUpdate(CaretEvent arg0) {
				
				int valorN = -1;
				boolean esEntero = true;
				
				try {
					valorN = Integer.parseInt(txtValorN.getText());
				} catch (Exception e){
					esEntero = false;
				}
				
				if (valorN < 1 || valorN > 8 || !esEntero){
					Border border = BorderFactory.createLineBorder(Color.red,2);
					txtValorN.setBorder(border);
				}
				else
					txtValorN.setBorder(defaultBorder);
			}
			
		});
		
		
		formularioConfiguracion.add(new JPanel().add(txtValorN));
		
		
		// COMBO BOX: Lista de funciones
		// ·································································
		
		formularioConfiguracion.add(new JLabel("Método de selección:"));
		
		listaMetodoSeleccion = new JComboBox<String>(listaMetodoSeleccionOpciones);
		listaMetodoSeleccion.setSelectedIndex(0);
		listaMetodoSeleccion.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				metodoSeleccion = listaMetodoSeleccion.getSelectedIndex() + 1;
			}
		}
		);
		
		formularioConfiguracion.add(listaMetodoSeleccion);
		
		// COMBO BOX: Lista de funciones
		// ·································································
		
		formularioConfiguracion.add(new JLabel("Método de cruce:"));
		
		listaMetodoCruce = new JComboBox<String>(listaMetodoCruceOpciones);
		listaMetodoCruce.setSelectedIndex(0);
		listaMetodoCruce.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				// No hacemos nada...
			}
		}
		);
		
		formularioConfiguracion.add(listaMetodoCruce);
		
		// OPCION: Probabilidad de mutacion
		// ·································································
		
		formularioConfiguracion.add(new JLabel("Elitismo:"));
		
		txtElitismo = new JTextField("0");
		txtElitismo.setToolTipText("Valores admitidos: Reales [0 .. 0'02]");
		
		// Insertamos un listener para que cambie el borde a rojo si los datos son incorrectos
		txtElitismo.addCaretListener(new CaretListener(){
			
			@Override
			public void caretUpdate(CaretEvent arg0) {
				
				double elitismo = -1;
				boolean esReal = true;
				
				try {
					elitismo = Double.parseDouble(txtElitismo.getText());
				} catch (Exception e){
					esReal = false;
				}
				
				if (elitismo < 0 || elitismo > 0.02 || !esReal){
					Border border = BorderFactory.createLineBorder(Color.red,2);
					txtElitismo.setBorder(border);
				}
				else
					txtElitismo.setBorder(defaultBorder);
			}
			
		});
		
		formularioConfiguracion.add(txtElitismo);
		
		JPanel panelAjustado2 = new JPanel();
		FlowLayout fl2 = new FlowLayout();
		fl2.setAlignment(FlowLayout.LEFT);
		panelAjustado2.setLayout(fl);
		panelAjustado2.add(formularioConfiguracion);
	
		// Panel final
		// ----------------------------------------------------------------
		
		TitledBorder tituloSeccionConfiguracion;
		tituloSeccionConfiguracion = BorderFactory.createTitledBorder("Configuración");
		tituloSeccionConfiguracion.setTitleJustification(TitledBorder.LEFT);	
		
		defaultBorder = txtTamPoblacion.getBorder();

		JPanel panelConfiguracion = new JPanel();
		
		panelConfiguracion.setBorder(tituloSeccionConfiguracion);
		panelConfiguracion.setLayout(new BorderLayout());
		panelConfiguracion.add(panelAjustado,"North");
		panelConfiguracion.add(panelAjustado2,"Center");
		panelConfiguracion.add(getPanelBotonEvaluar(),"South");
		panelConfiguracion.setMinimumSize(new Dimension(100,380));
		
		return panelConfiguracion;
		
	}

	private JPanel getPanelGrafica(){
		
		TitledBorder tituloSeccionGrafica;
		tituloSeccionGrafica = BorderFactory.createTitledBorder("Representacion gráfica");
		tituloSeccionGrafica.setTitleJustification(TitledBorder.LEFT);	
		
		plot = new Plot2DPanel();
		plot.setBorder(tituloSeccionGrafica);
		plot.setMinimumSize(new Dimension(900,900));
		
		return plot;
		
	}

	private JSplitPane getPanelIzquierdo(){
		
		
		JSplitPane panelCombinado = new JSplitPane(JSplitPane.VERTICAL_SPLIT, getPanelConfiguracion(),getPanelResultados());
		panelCombinado.setResizeWeight(0);
		panelCombinado.setOneTouchExpandable(true);
		
		/*JPanel panelTotal = new JPanel();
		panelTotal.setLayout(new BorderLayout());
		
		panelTotal.add(getPanelConfiguracion(),"Center");
		panelTotal.add(getPanelResultados(),"South");
		*/
		panelCombinado.setMinimumSize(new Dimension(350,1));
		
		return panelCombinado;//panelTotal;
	}
	
	private JPanel getPanelBotonEvaluar() {
		
		JButton botonCalcular = new JButton("Evaluar");
		botonCalcular.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (actualizarParametros()){
					ag = new AGenetico(tamPoblacion,maxGeneraciones,probCruce,probMutacion,tolerancia);
					ag.ejecutaAlgoritmo(areaTextoResultados, funcionElegida, numVariables, metodoSeleccion, elitismo);
					
					actualizaGrafica();
				}
				

				
			}
			
		});
		
		
		JPanel panelBoton = new JPanel();
		
		panelBoton.setLayout(new FlowLayout());
		panelBoton.add(botonCalcular);
		
		return panelBoton;
	}

	private JScrollPane getPanelResultados(){
		
		TitledBorder tituloSeccion;
		tituloSeccion = BorderFactory.createTitledBorder("Resultados");
		tituloSeccion.setTitleJustification(TitledBorder.LEFT);

		areaTextoResultados = new JTextArea("",8,1);
		areaTextoResultados.setEditable(false);
		areaTextoResultados.setLineWrap(true);	
		
		JScrollPane scrollPane = new JScrollPane(areaTextoResultados);
		scrollPane.setBorder(tituloSeccion);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setMaximumSize(new Dimension(1,225));	// Ancho: X - Alto: 225
		scrollPane.setMinimumSize(new Dimension(1,150));	// Ancho: X - Alto: 150
		return scrollPane;
		
	}

	private boolean actualizarParametros(){
		
		boolean exito = true;
		
		// Ademas de actualizar, comprobamos que son numeros
		try{
			this.tamPoblacion = Integer.parseInt(txtTamPoblacion.getText());
			this.maxGeneraciones = Integer.parseInt(txtMaxGeneraciones.getText());
			this.probCruce = Double.parseDouble(txtProbCruce.getText());
			this.probMutacion = Double.parseDouble(txtProbMutacion.getText());
			this.tolerancia = Double.parseDouble(txtTolerancia.getText());
			this.numVariables = Integer.parseInt(txtValorN.getText());
			this.elitismo = Double.parseDouble(txtElitismo.getText());
		} catch (Exception e){
			exito = false;
			JOptionPane.showMessageDialog(this, 
					  "Existen parámetros del formulario incorrectos\nPor favor, revise los campos resaltados en rojo y compruebe que contienen numeros",
					  "Error",
					  JOptionPane.ERROR_MESSAGE);
		}
		
		// Si son convertibles a numeros, comprobamos que estan en rangos validos
		if (tamPoblacion < 1 || tamPoblacion > 1000 ||
			maxGeneraciones < 1 || maxGeneraciones > 1000 ||
			probCruce < 0.5 || probCruce > 0.8 ||
			probMutacion < 0.01 || probMutacion > 0.1 ||
			tolerancia < 1E-9 || tolerancia > 0.1 ||
			numVariables < 1 || numVariables > 8 ||
			elitismo < 0 || elitismo > 0.02){
			exito = false;
			JOptionPane.showMessageDialog(this, 
					  "Existen parámetros del formulario incorrectos\nPor favor, revise los campos resaltados en rojo y compruebe que están dentro de los rango válidos",
					  "Error",
					  JOptionPane.ERROR_MESSAGE);
		}
			
		return exito;
	}

	private void actualizaGrafica(){
		
		double[] a = ag.getMediaAptitud();
		double[] b = ag.getMejorAptitudAbsoluto();
		double[] c = ag.getMejorAptitudPorGeneracion();
		
		double[] enumerado = new double[ag.getMaxGeneraciones()];
		for(int i = 0; i < ag.getMaxGeneraciones(); i++)
			enumerado[i] = i;
		
    	plot.removeAllPlots();
		plot.addLinePlot("Mejor absoluto", enumerado, b);
		plot.addLinePlot("Media de la generacion", enumerado, a);
		plot.addLinePlot("Mejor de la generacion", enumerado, c);
		plot.addLegend("SOUTH");
		plot.revalidate();
	}

}
