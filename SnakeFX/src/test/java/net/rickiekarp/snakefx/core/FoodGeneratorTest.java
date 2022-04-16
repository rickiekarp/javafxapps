package net.rickiekarp.snakefx.core;


import net.rickiekarp.snakefx.view.ViewModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class FoodGeneratorTest {

	private FoodGenerator foodGenerator;

	private Grid grid;

	private ViewModel viewModel;

	@BeforeEach
	void setup() {
		viewModel = new ViewModel();
		grid = mock(Grid.class);
		foodGenerator = new FoodGenerator(viewModel, grid);
	}

	@Test
	void testGenerateFood() {
		final GameField field = new GameField(0, 0, 10);

		when(grid.getRandomEmptyField()).thenReturn(field);

		foodGenerator.generateFood();

		Assertions.assertEquals(field.getState(), State.FOOD);
	}

	@Test
	void testGenerationWhenPointsAreAddedToProperty() {
		final GameField field = new GameField(0, 0, 10);
		field.changeState(State.EMPTY);
		when(grid.getRandomEmptyField()).thenReturn(field);

		viewModel.getPoints().set(1);

		Assertions.assertEquals(field.getState(), State.FOOD);
	}

	@Test
	void testNoFoodIsGeneratedWhenPointsPropertyIsResetToZero() {
		final GameField field = new GameField(0, 0, 10);
		field.changeState(State.EMPTY);

		final GameField secondField = new GameField(0, 1, 10);
		secondField.changeState(State.EMPTY);
		when(grid.getRandomEmptyField()).thenReturn(field).thenReturn(secondField);


		// first set the points to 10, food is generated for first field.
		viewModel.getPoints().set(10);


		// now set the points back to 0. now no food must be generated for
		// second field.
		viewModel.getPoints().set(0);

		Assertions.assertEquals(secondField.getState(), State.EMPTY);
	}
}
