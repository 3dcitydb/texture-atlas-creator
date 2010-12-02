
public class RectSize implements Comparable<RectSize>{
	int width;
	int height;
	int area;
	RectSize(int width,int height){
		this.width=width;
		this.height=height;
		this.area=width*height;
	}
	@Override
	/**
	 * take care of it!!!!
	 */
	public int compareTo(RectSize arg0) {
		// TODO Auto-generated method stub
		int a1=arg0.height*arg0.width;	
		return area > a1 ? -1 : area == a1 ? 0 : 1;
	}
}
