package main.strategies;

import java.util.List;
import main.game.map.Map;
import main.game.map.Monster;
import main.game.map.Point;
import main.game.map.Rock;
import main.tracker.PathTracker;

public class FewerObstaclesAndShorterDistance implements Strategy {

	private final PathTracker pathTracker;

	public FewerObstaclesAndShorterDistance() {
		this.pathTracker = new PathTracker();
	}

	@Override
	public Point evaluatePossbileNextStep(List<Point> possibleNextSteps, Map map) {
		Point bestStep = null;
		double bestScore = Double.MAX_VALUE;
		Point target = map.getTreasureLocation();

		for (Point step : possibleNextSteps) {
			int obstacleCount = countObstaclesAround(step, map);
			double distance = calculateDistance(step, target);
			double score = distance + obstacleCount * 2; // Ajustar o peso conforme necessário

			if (score < bestScore) {
				bestScore = score;
				bestStep = step;
			}
		}

		if (bestStep != null) {
			pathTracker.addPathPoint(bestStep); // Adiciona o melhor passo ao caminho
			// Verifica e adiciona obstáculos encontrados ao redor
			if (countObstaclesAround(bestStep, map) > 0) {
				pathTracker.addObstacle(bestStep); // Adiciona o ponto como obstáculo se aplicável
			}
		}

		return bestStep;
	}

	private int countObstaclesAround(Point point, Map map) {
		int obstacles = 0;
		int[][] directions = {
				{-1, 0}, {1, 0},  // cima e baixo
				{0, -1}, {0, 1},  // esquerda e direita
				{-1, -1}, {-1, 1}, {1, -1}, {1, 1} // diagonais
		};

		for (int[] direction : directions) {
			int newX = point.getPositionX() + direction[0];
			int newY = point.getPositionY() + direction[1];
			if (newX >= 0 && newY >= 0 && newX < map.getScenarioSize()[0] && newY < map.getScenarioSize()[1]) {
				String cell = map.get(new Point(newX, newY));
				if (isObstacle(cell)) {
					obstacles++;
				}
			}
		}
		return obstacles;
	}
	private boolean isObstacle(String cell) {
		return cell.equals(Rock.CHARACTER) || cell.equals(Monster.CHARACTER);
	}


	private double calculateDistance(Point a, Point b) {
		return Math.sqrt(Math.pow(a.getPositionX() - b.getPositionX(), 2) +
				Math.pow(a.getPositionY() - b.getPositionY(), 2));
	}

	public PathTracker getPathTracker() {
		return pathTracker;
	}
}