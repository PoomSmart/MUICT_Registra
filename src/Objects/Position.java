package Objects;

import Utilities.CommonUtils;

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

	@SuppressWarnings("unchecked")
	public String toCellString() {
		return CommonUtils.cellPositionByPosition((Position<Integer, Integer>) this).toString();
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
		if (!(obj instanceof Position))
			return false;
		@SuppressWarnings("unchecked")
		Position<X, Y> position = (Position<X, Y>) obj;
		return position.x == x && position.y == y;
	}

	public static boolean isSame(Position<Integer, Integer> p, int x, int y) {
		return p.x == x && p.y == y;
	}

	public static Position<Integer, Integer> positionFromCellString(String cellString) {
		Integer alphabet = cellString.charAt(0) - 'A';
		Integer num = Integer.parseInt(cellString.substring(1, cellString.length()));
		return new Position<Integer, Integer>(alphabet, num);
	}
}
