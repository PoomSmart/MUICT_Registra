package Objects;

public class Position<X, Y> implements Cloneable {
	public final Integer x;
	public final Integer y;
	
	public static final Position<Integer, Integer> nullPosition = new Position<Integer, Integer>(-1, -1);

	public Position(Integer x, Integer y) {
		this.x = x;
		this.y = y;
	}
	
	public boolean equals(Position<X, Y> position) {
		return x == position.x && y == position.y;
	}
	
	public String toString() {
		return String.format("{%d, %d}", x, y);
	}
	
	public Position<X, Y> clone() {
		return new Position<X, Y>(x, y);
	}
}
