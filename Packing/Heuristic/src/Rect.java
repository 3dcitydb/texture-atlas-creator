

public class Rect {
	int x;
	int y;
	int width;
	int height;
	boolean rotated=false;
	
	int score1=Integer.MAX_VALUE, score2=Integer.MAX_VALUE;
	
	Rect(){}
	Rect(Rect r){
		this.x=r.x;
		this.y=r.y;
		this.width=r.width;
		this.height=r.height;
		this.score1=r.score1;
		this.score2=r.score2;
	}
	
	public void rotate(){
		int tmp=width;
		width=height;
		height=tmp;
		rotated=true;
	}
	public static boolean isContainedIn(Rect a, Rect b){
		return a.x >= b.x && a.y >= b.y 
			&& a.x+a.width <= b.x+b.width 
			&& a.y+a.height <= b.y+b.height;
	}
}
