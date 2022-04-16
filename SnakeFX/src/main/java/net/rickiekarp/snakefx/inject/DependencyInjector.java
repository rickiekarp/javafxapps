package net.rickiekarp.snakefx.inject;

import net.rickiekarp.snakefx.core.*;
import net.rickiekarp.snakefx.util.KeyboardHandler;
import net.rickiekarp.snakefx.view.ViewModel;
import javafx.util.Callback;

import java.util.HashMap;
import java.util.Map;

@Deprecated
public class DependencyInjector implements Callback<Class<?>, Object> {

    private final Map<Class<?>, Object> instances = new HashMap<>();

    public DependencyInjector() {
        injectCore();
        injectOthers();
    }

    private void injectCore() {
        final ViewModel viewModel = new ViewModel();
        final Grid grid = new Grid();
        final GameLoop gameLoop = new GameLoop(viewModel);
        final Snake snake = new Snake(viewModel, grid, gameLoop);
        final FoodGenerator foodGenerator = new FoodGenerator(viewModel, grid);
        final NewGameFunction newGameFunction = new NewGameFunction(viewModel, grid, snake, foodGenerator);

        put(ViewModel.class, viewModel);
        put(Grid.class, grid);
        put(GameLoop.class, gameLoop);
        put(Snake.class, snake);
        put(FoodGenerator.class, foodGenerator);
        put(NewGameFunction.class, newGameFunction);
    }

    private void injectOthers() {
        final KeyboardHandler keyboardHandler = new KeyboardHandler(get(ViewModel.class));
        put(KeyboardHandler.class, keyboardHandler);
    }


    public <T> void put(Class<T> clazz, T instance) {
        instances.put(clazz, instance);
    }

    public <T> T get(Class<T> clazz) {
        return (T) instances.get(clazz);
    }

    @Override
    public Object call(Class<?> clazz) {
        return instances.get(clazz);
    }
}
