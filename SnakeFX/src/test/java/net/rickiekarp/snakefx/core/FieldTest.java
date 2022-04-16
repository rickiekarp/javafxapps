package net.rickiekarp.snakefx.core;

import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FieldTest {

	/**
	 * When the Field is initialized, the rectangle inside of the field has to
	 * be created and the state has to be set to the default (empty).
	 */
	@Test
	void testInitialization() {
		int x = 3;
		int y = 5;

		int sizeInPixel = 100;

		GameField field = new GameField(x, y, sizeInPixel);

		Rectangle rectangle = field.getRectangle();

		Assertions.assertEquals(rectangle.getWidth(), sizeInPixel);
		Assertions.assertEquals(rectangle.getHeight(), sizeInPixel);

		/*
		 * the x value has to be (x * sizeInPixel) because there are x other
		 * Fields on the left of this field, each with the same sizeInPixel.
		 * Same is true for the y value.
		 */
		Assertions.assertEquals(rectangle.getX(), 300);
		Assertions.assertEquals(rectangle.getY(), 500);

		Assertions.assertEquals(field.getX(), x);
		Assertions.assertEquals(field.getY(), y);

		Assertions.assertEquals(field.getState(), State.EMPTY);

		Assertions.assertEquals(rectangle.getFill(), State.EMPTY.getColor());

	}

	/**
	 * Test the behavior of the method {@link GameField#changeState}.
	 */
	@Test
	void testChangeState() {

		GameField field = new GameField(1, 1, 10);

		field.changeState(State.HEAD);

		Assertions.assertEquals(field.getState(), State.HEAD);
		Assertions.assertEquals(field.getRectangle().getFill(), 
				State.HEAD.getColor());
	}
}
