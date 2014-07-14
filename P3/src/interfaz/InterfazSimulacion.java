package interfaz;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import agenetico.Cromosoma;

@SuppressWarnings("serial")
public class InterfazSimulacion extends JFrame {

	TableroInvasores tInvasores;
	Cromosoma cromosomaEmular;
	
	// Controles de ejecucion
	JTextField visorTiempo;
	JTextField visorAdaptacion;
	JButton botonEjecutar;
	
	public InterfazSimulacion(Cromosoma mejorCromosoma){
		
		inicializarInterfazSimulador();
		
		cromosomaEmular = mejorCromosoma;
		
	}
	
	public void inicializarInterfazSimulador(){

		this.setContentPane(getPanelTableroJuego());
		this.setTitle("[PE] Práctica 3: Simulación de programa");
		
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Sirve para que se termine el proceso cuando se pulsa sobre la X de cerrar

		visorTiempo.setEditable(false);
		visorTiempo.setEnabled(false);
		
		visorAdaptacion.setEditable(false);
		visorAdaptacion.setEnabled(false);
		
	}

	private JPanel getPanelTableroJuego(){
		
		JPanel panelTablero = new JPanel();
		panelTablero.setLayout(new BorderLayout());
		
		tInvasores = new TableroInvasores();
		panelTablero.add(tInvasores,"Center");
		
		panelTablero.add(getPanelControlJuego(),"West");
		
		return panelTablero;
	
	}

	private JPanel getPanelControlJuego(){
		
		JPanel panelControl = new JPanel();
		
		panelControl.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.anchor = GridBagConstraints.LINE_START; // bottom of space
		c.insets = new Insets(5,20,0,20);  //top padding
		c.ipady = 5; 		
		c.fill = GridBagConstraints.HORIZONTAL;
		
		
		// FORMULARIO: Configuracion
		// ·································································
		
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.LINE_START;
		panelControl.add(new JLabel(" > Control de la simulación:"), c);
		
		
		// VISOR TIEMPO
		// ·································································
		
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		panelControl.add(new JLabel("Tiempo:"), c);

		
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 1;
		
		visorTiempo = new JTextField("0");
		panelControl.add(visorTiempo, c);
		
		
		// VISOR TIEMPO
		// ·································································
		
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		panelControl.add(new JLabel("Aptitud:"), c);

		
		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 1;
		
		visorAdaptacion = new JTextField("0");
		panelControl.add(visorAdaptacion, c);
		
		// BOTON EJECUTAR
		// ·································································
		
		botonEjecutar = new JButton("Ejecutar...");
		botonEjecutar.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				System.out.println("APT: " + cromosomaEmular.aptitud + "  EVA:" + cromosomaEmular.evaluacion + " EVA FUN: " + cromosomaEmular.evaluaCromosoma2());
				tInvasores.inicializaEspacio();
				
				tInvasores.preparaEjecucion(cromosomaEmular.getArbol(),
											visorTiempo, visorAdaptacion, botonEjecutar);
				
				Thread t = new Thread(tInvasores);
				t.start();
			}
					
		});
		
		JPanel panelBoton = new JPanel();
		
		panelBoton.setLayout(new FlowLayout());
		panelBoton.add(botonEjecutar);
		
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 2;		
		c.ipady = 5;
		c.anchor = GridBagConstraints.CENTER;
		
		panelControl.add(panelBoton, c);
		
		return panelControl;
		
	}
	
}
