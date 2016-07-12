package Objects;

import Utilities.CommonUtils;
import Visualizers.SeatVisualizer;

public class Cell<X, Y> {
	public String x;
	public Integer y = -1;

	public Cell(String x, Integer y) {
		this.x = x;
		this.y = y;
	}

	public boolean isNull() {
		return x == null || y == -1;
	}

	public void setName(int x, int y) {
		int shiftLeft = SeatVisualizer.shiftLeft;
		int shiftTop = SeatVisualizer.shiftTop;
		int cx = (x - shiftLeft) / SeatVisualizer.tileSize.width;
		int cy = (y - shiftTop) / SeatVisualizer.tileSize.height;
		int width = SeatVisualizer.bounds.width;
		int height = SeatVisualizer.bounds.height;
		if (cx >= width || x < shiftLeft || cy >= height || y < shiftTop)
			return;
		this.x = CommonUtils.alphabet(cx);
		this.y = cy;
	}

	public String toString() {
		if (isNull())
			return "Null";
		return String.format("%s%d", x, y);
	}
}
