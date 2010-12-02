import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Random;


public class Main {
	
	Main(int w,int h,ArrayList<Rect> r, Dimension box){
		frame = new Vis(w,h,r,box);
		frame.setVisible(true);
	}
	Vis frame;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HeuristicBinPacking mr= new HeuristicBinPacking(500,500) ;
		Rect r;
	
		ArrayList<RectSize> vx= new ArrayList<RectSize>();
		ArrayList<Rect> result=null;

		//---------------------
		int numberOfRun=1;
		int rectCount=100;
		//---------------------
		int[] time = new int[numberOfRun];
		int[] recCount= new int[time.length];
		float[] oc= new float[time.length];
		
		
		long sumRec=0,sumTime=0;
		for (int zz = 0; zz < time.length; zz++) {
			vx.clear();
			mr.clear();
			System.out.println(zz+" "+vx.size());
			int fullArea = mr.binHeight * mr.binWidth;
			int bussy = 0;
			RectSize rs;
			int rc = 0;
			while (rc < rectCount && bussy < fullArea) {
				rs = new RectSize((int) (Math.random()*100 ),
						(int) (Math.random()*50));
				if (fullArea > bussy + rs.area) {
					vx.add(rs);
					bussy += rs.area;
				}
				rc++;
			}
			recCount[zz] = vx.size();
			long start = System.currentTimeMillis();
			result = mr
					.insert(vx, FreeRectChoiceHeuristic.RectContactPointRule);
			long stop = System.currentTimeMillis();
			time[zz] = (int) (stop - start);
			oc[zz]=mr.occupancy();
			sumRec += recCount[zz];
			sumTime += time[zz];
		}

		for (int zz = 0; zz < time.length; zz++) {
			System.out.println( recCount[zz] + "," + time[zz]+","+oc[zz]);

		}
		System.out.println("avg Rect:" + sumRec / time.length + "  ,time:"
				+ sumTime / time.length);
		Main m = new Main(mr.binWidth, mr.binHeight, result,mr.getBoundingBox());

	}

}
