package net.rickiekarp.snakefx.core;


import net.rickiekarp.snakefx.settings.Config;
import net.rickiekarp.snakefx.view.ViewModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class GridTest {

	private Grid grid;
	private ViewModel viewModel;

	@BeforeEach
	void setUp() {
		viewModel = new ViewModel();
		viewModel.getGridSize().set(Config.ROW_AND_COLUMN_COUNT.get());
		grid = new Grid();

		grid.init();
	}

	/**
	 * The init method has to create a list of {@link GameField} instances with the
	 * right coordinates.
	 */
	@Test
	void testInitialization() {

		final List<GameField> fields = grid.getFields();

		final int fieldCount = Config.ROW_AND_COLUMN_COUNT.get() * Config.ROW_AND_COLUMN_COUNT.get();
		Assertions.assertEquals(fields.size(), fieldCount);

		// first check the calculated Size for all Fields

        fields.forEach(field->{
			Assertions.assertEquals(field.getRectangle().getWidth(), Config.GRID_SIZE_IN_PIXEL.get() / Config.ROW_AND_COLUMN_COUNT.get());
			Assertions.assertEquals(field.getRectangle().getHeight(), Config.GRID_SIZE_IN_PIXEL.get() / Config.ROW_AND_COLUMN_COUNT.get());
        });

		// choose some sample fields and check there x and y values
		/*
		 * o|_|_|_
		 * _|_|_|_
		 * _|_|_|_
		 * | | |
		 */
		final GameField x0y0 = fields.get(0);
		Assertions.assertEquals(x0y0.getX(), 0);
		Assertions.assertEquals(x0y0.getY(), 0);

		/*
		 * _|_|_|_
		 * _|_|_|_
		 * _|o|_|_
		 * | | |
		 */
		final GameField x1y2 = fields.get(9);
		Assertions.assertEquals(x1y2.getX(), 9);
		Assertions.assertEquals(x1y2.getY(), 0);

		/*
		 * _|_|_|_
		 * _|_|_|_
		 * _|_|_|_
		 * | | |o
		 */
		final GameField x3y3 = fields.get(fieldCount - 1);
		Assertions.assertEquals(x3y3.getX(), 19);
		Assertions.assertEquals(x3y3.getY(), 19);
	}

	@Test
	void testGetXY() {
		final GameField x2y1 = grid.getXY(2, 1);

		Assertions.assertEquals(x2y1.getX(), 2);
		Assertions.assertEquals(x2y1.getY(), 1);
	}

	/**
	 * when the value for x or y (or both) is bigger than the grid then null has
	 * to be returned.
	 */
	@Test
	void testGetXYFail() {
		final GameField x2y5 = grid.getXY(2, 5);
		Assertions.assertNotNull(x2y5);
	}

	/**
	 * From a given field get the field next to it from a given direction.
	 */
	@Test
	void testGetFromDirection() {
		final GameField x2y2 = grid.getXY(2, 2);

		final GameField x2y3 = grid.getFromDirection(x2y2, Direction.DOWN);

		Assertions.assertEquals(x2y3.getX(), 2);
		Assertions.assertEquals(x2y3.getY(), 3);

		final GameField x3y3 = grid.getFromDirection(x2y3, Direction.RIGHT);

		Assertions.assertEquals(x3y3.getX(), 3);
		Assertions.assertEquals(x3y3.getY(), 3);
	}

	/**
	 * In the game when the snake moves outside of the grid on one side it
	 * appears again on the other side.
	 * 
	 * When a field is located directly at the border of the grid and the
	 * getFromDirection method is called with the direction to the outside of
	 * the grid, the field on the other side of the grid on the same row/column
	 * has to be returned.
	 */
	@Test
	void testGetFromDirectionOtherSideOfTheGrid() {

		GameField x0y3 = grid.getXY(0, 3);
		final GameField x3y3 = grid.getFromDirection(x0y3, Direction.LEFT);

		Assertions.assertEquals(x3y3.getX(), 19);
		Assertions.assertEquals(x3y3.getY(), 3);

		x0y3 = grid.getFromDirection(x3y3, Direction.RIGHT);
		Assertions.assertEquals(x0y3.getX(), 0);
		Assertions.assertEquals(x0y3.getY(), 3);

		GameField x2y0 = grid.getXY(2, 0);
		final GameField x2y3 = grid.getFromDirection(x2y0, Direction.UP);

		Assertions.assertEquals(x2y3.getX(), 2);
		Assertions.assertEquals(x2y3.getY(), 19);

		x2y0 = grid.getFromDirection(x2y3, Direction.DOWN);
		Assertions.assertEquals(x2y0.getX(), 2);
		Assertions.assertEquals(x2y0.getY(), 0);
	}

	/**
	 * When the newGame method is called, all fields must be reset to status
	 * EMPTY.
	 */
	@Test
	void testNewGame() {

		// First change the state of some fields

		final GameField x2y1 = grid.getXY(2, 1);
		x2y1.changeState(State.FOOD);

		final GameField x3y3 = grid.getXY(3, 3);

		x3y3.changeState(State.HEAD);

		final GameField x0y2 = grid.getXY(0, 2);
		x0y2.changeState(State.TAIL);

		grid.newGame();

		// now the fields must be EMPTY
		Assertions.assertEquals(x2y1.getState(), State.EMPTY);
		Assertions.assertEquals(x3y3.getState(), State.EMPTY);
		Assertions.assertEquals(x0y2.getState(), State.EMPTY);

		// All other fields must be empty too
		final List<GameField> fields = grid.getFields();

        fields.forEach(field -> {
            Assertions.assertEquals(field.getState(), State.EMPTY);
        });
	}
}
