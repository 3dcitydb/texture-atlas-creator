import java.util.LinkedList;

public class DisjointRectCollection {

	LinkedList<Rect> rects;

	public boolean Add(Rect r) {
		// Degenerate rectangles are ignored.
		if (r.width == 0 || r.height == 0)
			return true;

		if (!disjoint(r))
			return false;
		rects.add(r);
		return true;
	}

	public void Clear() {
		rects.clear();
	}

	public boolean disjoint(Rect r) {
		// Degenerate rectangles are ignored.
		if (r.width == 0 || r.height == 0)
			return true;

		for (int i = 0; i < rects.size(); ++i)
			if (!disjoint(rects.get(i), r))
				return false;
		return true;
	}

	public static boolean disjoint(Rect a, Rect b) {
		if (a.x + a.width <= b.x || b.x + b.width <= a.x
				|| a.y + a.height <= b.y || b.y + b.height <= a.y)
			return true;
		return false;
	}
}
