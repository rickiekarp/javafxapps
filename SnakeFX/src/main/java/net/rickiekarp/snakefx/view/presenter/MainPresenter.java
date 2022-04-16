package net.rickiekarp.snakefx.view.presenter;

import net.rickiekarp.snakefx.core.Grid;
import javafx.scene.layout.Pane;

import java.util.function.Consumer;

/**
 * Presenter class for the main.fxml file.
 */
@Deprecated
public class MainPresenter {
	private final Pane gridContainer;
	private final Grid grid;
	private final Consumer<?> newGameFunction;

	public MainPresenter(final Grid grid, final Consumer<?> newGameFunction) {
		this.gridContainer = new Pane();
		this.grid = grid;
		this.newGameFunction = newGameFunction;
	}

	public void initialize() {
		grid.init();

        grid.getFields().forEach(field -> {
            gridContainer.getChildren().add(field.getRectangle());
        });

		newGameFunction.accept(null);
	}

	public void newGame() {
		newGameFunction.accept(null);
	}

	public Pane getGridContainer() {
		return gridContainer;
	}
}
