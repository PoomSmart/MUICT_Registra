package Visualizers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class GraphPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private int width = (int) (1000 * 1.5);
	private int height = (int) (450 * 1.5);
	private int padding = 25;
	private int labelPadding = 25;
	private Color pointColor = new Color(100, 100, 100, 180);
	private Color gridColor = new Color(200, 200, 200, 200);
	private Map<Integer, Color> graphColors = new HashMap<Integer, Color>();
	private static final Stroke GRAPH_STROKE = new BasicStroke(2f);
	private int pointWidth = 4;
	private int numberYDivisions = 10;
	private List<List<Integer>> data;
	private int currentMaxIndex = 0;
	private int currentMaxVersions = 0;
	private static Random random = new Random();

	public static double xMultiplier = 1;

	public GraphPanel(String name, List<List<Integer>> scores) {
		this.data = scores;
		for (List<Integer> subscores : scores) {
			if (currentMaxVersions < subscores.size()) {
				currentMaxIndex = scores.indexOf(subscores);
				currentMaxVersions = subscores.size();
			}
		}
		JFrame frame = new JFrame(name == null ? "Multiple data" : name);
		frame.getContentPane().add(this);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		double xScale = ((double) getWidth() - (2 * padding) - labelPadding) / (data.get(currentMaxIndex).size() - 1);
		double yScale = ((double) getHeight() - 2 * padding - labelPadding) / (getMaxScore() - getMinScore());

		List<List<Point>> graphPoints = new Vector<>();

		// Adding points
		for (List<Integer> subscores : data) {
			List<Point> subgraphPoints = new Vector<>();
			for (int i = 0; i < subscores.size(); i++) {
				int x1 = (int) (i * xScale + padding + labelPadding);
				int y1 = (int) ((getMaxScore() - subscores.get(i)) * yScale + padding);
				subgraphPoints.add(new Point(x1, y1));
			}
			graphPoints.add(subgraphPoints);
		}

		// draw white background
		g2.setColor(Color.WHITE);
		g2.fillRect(padding + labelPadding, padding, getWidth() - (2 * padding) - labelPadding,
				getHeight() - 2 * padding - labelPadding);
		g2.setColor(Color.BLACK);
		FontMetrics metrics = g2.getFontMetrics();

		// create hatch marks and grid lines for y axis.
		for (int i = 0; i < numberYDivisions + 1; i++) {
			int x0 = padding + labelPadding;
			int x1 = pointWidth + padding + labelPadding;
			int y0 = getHeight()
					- ((i * (getHeight() - padding * 2 - labelPadding)) / numberYDivisions + padding + labelPadding);
			int y1 = y0;
			if (data.size() > 0) {
				g2.setColor(gridColor);
				g2.drawLine(padding + labelPadding + 1 + pointWidth, y0, getWidth() - padding, y1);
				g2.setColor(Color.BLACK);
				String yLabel = ((int) ((getMinScore()
						+ (getMaxScore() - getMinScore()) * ((i * 1.0) / numberYDivisions)) * 100)) / 100 + "";
				int labelWidth = metrics.stringWidth(yLabel);
				g2.drawString(yLabel, x0 - labelWidth - 5, y0 + (metrics.getHeight() / 2) - 3);
			}
			g2.drawLine(x0, y0, x1, y1);
		}

		// and for x axis
		for (int i = 0; i < data.get(currentMaxIndex).size(); i++) {
			if (data.get(currentMaxIndex).size() > 1) {
				int x0 = i * (getWidth() - padding * 2 - labelPadding) / (data.get(currentMaxIndex).size() - 1)
						+ padding + labelPadding;
				int x1 = x0;
				int y0 = getHeight() - padding - labelPadding;
				int y1 = y0 - pointWidth;
				if ((i % ((int) ((data.get(currentMaxIndex).size() / 20.0)) + 1)) == 0) {
					g2.setColor(gridColor);
					g2.drawLine(x0, getHeight() - padding - labelPadding - 1 - pointWidth, x1, padding);
					g2.setColor(Color.BLACK);
					String xLabel = (int) ((i + 1) * xMultiplier) + "";
					int labelWidth = metrics.stringWidth(xLabel);
					g2.drawString(xLabel, x0 - labelWidth / 2, y0 + metrics.getHeight() + 3);
				}
				g2.drawLine(x0, y0, x1, y1);
			}
		}

		// create x and y axes
		g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, padding + labelPadding, padding);
		g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, getWidth() - padding,
				getHeight() - padding - labelPadding);

		Stroke oldStroke = g2.getStroke();
		g2.setStroke(GRAPH_STROKE);
		int colorIndex = 0;
		for (List<Point> subgraphPoints : graphPoints) {
			g2.setColor(graphColors.getOrDefault(colorIndex++,
					new Color(random.nextInt(230), random.nextInt(230), random.nextInt(230))));
			for (int i = 0; i < subgraphPoints.size() - 1; i++) {
				int x1 = subgraphPoints.get(i).x;
				int y1 = subgraphPoints.get(i).y;
				int x2 = subgraphPoints.get(i + 1).x;
				int y2 = subgraphPoints.get(i + 1).y;
				g2.drawLine(x1, y1, x2, y2);
			}
		}

		g2.setStroke(oldStroke);
		colorIndex = 0;
		for (List<Point> subgraphPoints : graphPoints) {
			Color numColor = graphColors.getOrDefault(colorIndex, Color.BLACK);
			g2.setColor(numColor);
			for (int i = 0; i < subgraphPoints.size(); i++) {
				int gx = subgraphPoints.get(i).x;
				int gy = subgraphPoints.get(i).y;
				int x = gx - pointWidth / 2;
				int y = gy - pointWidth / 2;
				g2.setColor(pointColor);
				g2.fillOval(x, y, pointWidth, pointWidth);
				String num = data.get(colorIndex).get(i) + "";
				int labelWidth = metrics.stringWidth(num);
				g2.drawOval(gx - 9, gy - 9, 18, 18);
				Color whiteA = new Color(1.0f, 1.0f, 1.0f, 0.8f);
				g2.setColor(whiteA);
				whiteA = null;
				g2.fillOval(gx - 9, gy - 9, 18, 18);
				g2.setColor(numColor);
				g2.drawString(num, x - labelWidth / 2 + 3, y + 5);
			}
			colorIndex++;
		}
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(width, height);
	}

	private Integer getMinScore() {
		return 0;
	}

	private Integer getMaxScore() {
		Integer maxScore = Integer.MIN_VALUE;
		for (List<Integer> subscores : data) {
			for (Integer score : subscores)
				maxScore = Math.max(maxScore, score);
		}
		return maxScore;
	}

	public void setScores(List<List<Integer>> data) {
		this.data = data;
		invalidate();
		this.repaint();
	}

	public List<List<Integer>> getScores() {
		return data;
	}

	public void addGraphColor(Integer index, Color color) {
		graphColors.put(index, color);
	}

	public void clearGraphColors() {
		graphColors.clear();
	}

	public static void constructGraphs(String name, List<List<Integer>> data) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new GraphPanel(name, data);
			}
		});
	}

	public static void constructGraphs(List<List<Integer>> scores) {
		constructGraphs(null, scores);
	}

	public static void constructGraph(String name, Collection<Integer> data) {
		List<List<Integer>> scores = new Vector<>();
		List<Integer> subscores = new Vector<>();
		for (Integer d : data)
			subscores.add(d);
		scores.add(subscores);
		constructGraphs(name, scores);
	}

	public void writeToFile(String filename, List<String> colNames) {
		try {
			FileOutputStream fileOut = new FileOutputStream(filename + ".xlsx");
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet worksheet = workbook.createSheet("Worksheet");
			XSSFRow row = worksheet.createRow(0);
			XSSFCell cellDay = row.createCell(0);
			cellDay.setCellValue("Day");
			XSSFCell cellValue;
			for (int col = 0; col < data.size(); col++) {
				cellValue = row.createCell(1 + col);
				cellValue.setCellValue(colNames.get(col));
			}
			for (int i = 0; i < data.get(0).size(); i++) {
				row = worksheet.createRow(i + 1);
				cellDay = row.createCell(0);
				cellDay.setCellValue(i + 1);
				for (int col = 0; col < data.size(); col++) {
					cellValue = row.createCell(col + 1);
					cellValue.setCellValue(data.get(col).get(i));
				}
			}
			
			workbook.write(fileOut);
			fileOut.flush();
			fileOut.close();
			workbook.close();
			workbook = null;
			fileOut = null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}