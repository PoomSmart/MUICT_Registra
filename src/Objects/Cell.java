package Objects;

import Utilities.CommonUtils;
import Visualizers.SeatVisualizer;

public class Cell<X, Y> {
	public String alphabet;
	public Integer number = -1;

	public Cell(String alphabet, Integer number) {
		this.alphabet = alphabet;
		this.number = number;
	}

	public boolean isNull() {
		return alphabet == null || number == -1;
	}

	public void setName(int x, int y) {
		int shiftLeft = SeatVisualizer.shiftLeft;
		int shiftTop = SeatVisualizer.shiftTop;
		int cx = (x - shiftLeft) / SeatVisualizer.tileSize.width;
		int cy = (y - shiftTop) / SeatVisualizer.tileSize.height;
		int width = SeatVisualizer.bounds.width;
		int height = SeatVisualizer.bounds.height;
		if (cx > width || x < shiftLeft || cy > height || y < shiftTop)
			return;
		this.alphabet = CommonUtils.alphabet(height - cy - 1);
		this.number = width - cx;
	}

	public String toString() {
		if (isNull())
			return "Null";
		return String.format("%s%d", alphabet, number);
	}
	
	public static Cell<String, Integer> cellPositionByPosition(Position<Integer, Integer> pos) {
		return new Cell<String, Integer>(CommonUtils.alphabet(pos.y), pos.x);
	}
}
