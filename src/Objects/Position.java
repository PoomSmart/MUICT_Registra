package Objects;

public class Position<X, Y> implements Cloneable {
	public Integer x;
	public Integer y;
	
	public static final Position<Integer, Integer> nullPosition = new Position<Integer, Integer>(-1, -1);

	public Position(Integer x, Integer y) {
		this.x = x;
		this.y = y;
	}
	
	public String toString() {
		return String.format("{%d, %d}", x, y);
	}
	
	public Position<X, Y> clone() {
		return new Position<X, Y>(x, y);
	}

	public Integer getX() {
		return x;
	}

	public Integer getY() {
		return y;
	}

	@Override
	public boolean equals(Object obj) {
		@SuppressWarnings("unchecked")
		Position<X, Y> position = (Position<X, Y>)obj;
		return position.x == x && position.y == y;
	}
}
