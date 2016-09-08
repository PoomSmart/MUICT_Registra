package Objects;

public class Position<X, Y> implements Cloneable, Comparable<Object> {
	public Integer x = -1;
	public Integer y = -1;

	public Position(Integer x, Integer y) {
		this.x = x;
		this.y = y;
	}
	
	public Position() {
		this(-1, -1);
	}

	public String toString() {
		return String.format("{%d, %d}", x, y);
	}

	@SuppressWarnings("unchecked")
	public String toCellString() {
		return Cell.cellPositionByPosition((Position<Integer, Integer>) this).toString();
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

	public static boolean isSame(Position<Integer, Integer> p, int x, int y) {
		return p.x == x && p.y == y;
	}

	public static boolean isNull(Position<Integer, Integer> p) {
		return p.x == -1 || p.y == -1;
	}

	public static Position<Integer, Integer> positionByCellString(String cellString) {
		Integer alphabet = cellString.charAt(0) - 'A';
		Integer num = Integer.parseInt(cellString.substring(1, cellString.length()));
		Position<Integer, Integer> position = new Position<Integer, Integer>(num, alphabet);
		return position;
	}
	
	public static Position<Integer, Integer> positionByCellPosition(Cell<String, Integer> cell) {
		if (cell == null)
			return null;
		if (cell.isNull())
			return null;
		return positionByCellString(cell.toString());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Position))
			return false;
		@SuppressWarnings("unchecked")
		Position<Integer, Integer> position = (Position<Integer, Integer>) obj;
		return position.x == x && position.y == y;
	}

	@Override
	public int compareTo(Object obj) {
		if (!(obj instanceof Position))
			return 1;
		@SuppressWarnings("unchecked")
		Position<Integer, Integer> position = (Position<Integer, Integer>) obj;
		if (Position.isNull(position))
			return 1;
		int yy = y.compareTo(position.y);
		if (yy == 0) {
			if (position.x > x)
				return -1;
			if (position.x < x)
				return 1;
		}
		return yy;
	}
}
