package org.citygml.GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.*;

import org.citygml.bin.TextureAtlasGenerator ;
import javax.swing.BoxLayout;

public class UI extends JFrame{
	TextureAtlasGenerator main;
	JLabel jlI = new JLabel("Input GML File:");
	JLabel jlO = new JLabel("Output GML File:");
	JLabel jlS = new JLabel("Atlas Maximum Size:");
	JLabel jlAl = new JLabel("Packing Algorithm:");
	
	JButton jbi = new JButton("...");
	JButton jbo = new JButton("...");
	
	JButton jbRun = new JButton("Run");
	
	JTextField in = new JTextField();
	JTextField out = new JTextField();
	JTextField wt = new JTextField();
	JTextField ht = new JTextField();
	
	public UI(TextureAtlasGenerator main){
		this.main=main;	
		initialize();
	}
	public void initialize(){
		this.setLayout(new BorderLayout());
		this.setMinimumSize(new Dimension(500,300));
		this.setLocation(100, 100);
		
		Dimension butD= new Dimension(30,20);
		Dimension texFD= new Dimension(300,20);
		Dimension Leb= new Dimension(100,20);
		
		JPanel jpIN = new JPanel();
		jpIN.setLayout(new FlowLayout(FlowLayout.LEFT));
		in.setPreferredSize(texFD);
		jbi.setPreferredSize(butD);
		jlI.setPreferredSize(Leb);
		jpIN.add(jlI);
		jpIN.add(in);
		jpIN.add(jbi);
		
		JPanel jpOut = new JPanel();
		jpOut.setLayout(new FlowLayout(FlowLayout.LEFT));
		out.setPreferredSize(texFD);
		jbo.setPreferredSize(butD);
		jpOut.add(jlO);
		jlO.setPreferredSize(Leb);
		jpOut.add(out);
		jpOut.add(jbo);
		
		JPanel jpat= new JPanel();
		jpat.setLayout(new FlowLayout(FlowLayout.LEFT));
		ht.setPreferredSize(butD);
		wt.setPreferredSize(butD);
		jlS.setPreferredSize(Leb);
		jpat.add(jlS);
		jpat.add(wt);
		jpat.add(new JLabel("x"));
		jpat.add(ht);
		
		
		
		
//		JPanel center = this.getContentPane();
		GridLayout  b=new GridLayout(0,1);
		b.setVgap(10);
		this.getContentPane().setLayout(b);
		this.getContentPane().add(jpIN);
		this.getContentPane().add(jpat);
		this.getContentPane().add(jpOut);

		
		
		
		
//		this.getContentPane().add(center,BorderLayout.CENTER);
		
		this.pack();
		
	}
}
