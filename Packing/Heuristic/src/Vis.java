import javax.swing.JFrame;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.*;

public class Vis extends JFrame {
	
	ArrayList<Rect> result;
	int w, h;
	Vis(int w, int h, ArrayList<Rect> result,Dimension box){
		this.setLocation(100, 100);
		this.setMinimumSize( new Dimension(w+30, h+30));
		this.result=result;
		this.w=w;
		this.h=h;
		Canv c= new Canv(w,h,result, box);
				
		this.getContentPane().add(c);
		
		
	}
}
