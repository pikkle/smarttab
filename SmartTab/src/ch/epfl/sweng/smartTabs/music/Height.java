package ch.epfl.sweng.smartTabs.music;

/**
 * @author Faton Ramadani
 */
public enum Height {
	C (0), CD (1), D (2), DD (3), E (4), F (5), FD(6) , G (7), GD (8), A (9), AD (10), B (11);

	private static final int MAXHALFTONE = 12;
	private final int myIndex;
	Height(final int index) {
		this.myIndex = index;
	}
	
	public int getIndex() {
		return myIndex;
	}
	
	public static int getMax() {
		return MAXHALFTONE;
	}

	public Height get(int i) {
		if (i < 0 || i > MAXHALFTONE) {
			throw new IllegalArgumentException();
		}
		return Height.values()[i];
	}
}