package genetic_algorithm;

import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.*;

public class GUI { 
    JFrame frame;
    JTextArea area;
    JScrollPane sp;
    JLabel[] parameters = new JLabel[5];
    
    public GUI(){  
	    frame = new JFrame();//creating instance of JFrame  
	              
	    //JButton b = new JButton("click"); //creating instance of JButton  
	    //b.setBounds(130,100,100, 40);  
	
	    JLabel label = new JLabel();
	    label.setFont(new Font("SansSerif", Font.PLAIN, 32));
	    label.setBounds(10, 0, 500, 90);
	    label.setText("Timeline of your Civilization");
	    
	    area = new JTextArea(20, 40);  
	    area.setFont(new Font("SansSerif", Font.PLAIN, 24));
	    area.setEditable(false);
	    
	    sp = new JScrollPane(area);
	    frame.setSize(900,900); //400 width and 500 height  
	    frame.setVisible(true); //making the frame visible
	              
	    //frame.add(b); //adding button in JFrame  
	    frame.add(label);
	    sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);  
	    frame.getContentPane().setLayout(new FlowLayout());  
	    frame.getContentPane().add(sp);
	    
	    for(int i = 0; i < parameters.length; i++) {
	    	parameters[i] = new JLabel();
	    	parameters[i].setText(Parameters.values()[i].name() + ": 0");
		    parameters[i].setFont(new Font("SansSerif", Font.PLAIN, 24));
	    	frame.add(parameters[i]);
	    }
    }
    /*
    public static void main(String[] args) {

    }
    */
    
    public void logText(String text) {
    	String currentText = this.area.getText();
    	this.area.setText(currentText + "\n" + text);
    	JScrollBar vertical = this.sp.getVerticalScrollBar();
    	vertical.setValue( vertical.getMaximum() );
    }
    
    public void update(double[] values) {
    	for(int i = 0; i < this.parameters.length; i++) {
	    	this.parameters[i].setText(Parameters.values()[i].name() + ": " + values[i]);
    	}
    }
}
