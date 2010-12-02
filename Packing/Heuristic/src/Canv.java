import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;


public class Canv extends Canvas {
	ArrayList<Rect> result;
	int w, h;
	Color[] mc= new Color[10];
	Dimension box;
	Canv(int w, int h, ArrayList<Rect> result,Dimension box){
		
		this.result=result;
		this.w=w;
		this.h=h;
		this.box= box;
		
		mc[0]=Color.yellow;
		mc[1]=Color.black;
		mc[2]=Color.cyan;
		mc[3]=Color.darkGray;
		mc[4]=Color.green;
		mc[5]=Color.red;
		mc[6]=Color.magenta;
		mc[7]=Color.lightGray;
		mc[8]=Color.orange;
		mc[9]=Color.pink;
		
		
		
		
	}
	@Override
	public void paint(Graphics g) {
		// TODO Auto-generated method stub
		super.paint(g);
		
		g.translate(10, h+10);
		g.setColor(Color.blue);
		g.fillRect(0, -h, w, h);
		g.setColor(Color.red);
		g.drawLine(0, 0, 300, 0);
		g.drawLine(0, 0, 0, -300);
		Rect r;
		for (int i=0;i<result.size();i++){
			r= result.get(i);
			g.setColor(mc[i%10]);
			g.fillRect(r.x, -r.y-r.height, r.width, r.height);
		}
		g.setColor(Color.red);
		g.drawLine(box.width, 0, box.width, -box.height);
		g.drawLine(0,  -box.height, box.width,  -box.height);
		
		
	}

}
