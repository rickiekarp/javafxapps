package net.rickiekarp.colorpuzzlefx.view.ai.solver;

import net.rickiekarp.colorpuzzlefx.ai.SolverManager;
import net.rickiekarp.colorpuzzlefx.view.ai.benchmark.BenchmarkViewPopup;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Paint;
import javafx.util.Duration;

import de.saxsys.mvvmfx.ViewModel;
import net.rickiekarp.colorpuzzlefx.ai.Solver;
import net.rickiekarp.colorpuzzlefx.core.ColorProfile;
import net.rickiekarp.colorpuzzlefx.core.Colors;
import net.rickiekarp.colorpuzzlefx.core.GameLogic;

import java.util.Optional;

public class SolverViewModel implements ViewModel{

    private StringProperty speedLabel = new SimpleStringProperty();
    private ObjectProperty<Paint> nextColor = new SimpleObjectProperty<>();

    private BooleanProperty autoButtonPressed = new SimpleBooleanProperty();
    private DoubleProperty waitTime = new SimpleDoubleProperty(1);

    private ObservableList<String> solverNames = FXCollections.observableArrayList();
    private StringProperty selectedSolverName = new SimpleStringProperty();

    private Solver solver;
    private GameLogic gameLogic;
    private SolverManager solverManager;
    private Colors nextStep;
    private ColorProfile colorProfile = new ColorProfile();

    private Timeline timeline;

    public SolverViewModel(GameLogic gameLogic, SolverManager solverManager) {
        this.gameLogic = gameLogic;
        this.solverManager = solverManager;

        solverNames.addAll(solverManager.getSolverNames());


        selectedSolverName.addListener((observable1, oldValue1, newValue) -> {
            if (newValue != null) {
                final Optional<Solver> solverOptional = solverManager.getSolver(newValue);

                if (solverOptional.isPresent()) {
                    solver = solverOptional.get();
                    solver.setGame(gameLogic);
                    nextStep = solver.nextStep();
                    nextColor.set(colorProfile.getColor(nextStep));
                }
            }
        });


        speedLabel.bind(Bindings.format("Wait time: %1$.1f s", waitTime));

        waitTime.addListener((observable, oldValue, newValue) -> {
           this.timeline = createTimeline();
        });


        this.timeline = createTimeline();
    }

    private Timeline createTimeline() {
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(waitTime.get()), event -> {
            this.next();
        });

        Timeline timeline = new Timeline(keyFrame);
        timeline.setCycleCount(Animation.INDEFINITE);
        return timeline;
    }

    public void auto() {
        if(autoButtonPressed.get()) {
            timeline.play();
        } else {
            timeline.pause();
        }
    }

    public void pause() {
        timeline.pause();

        autoButtonPressed.setValue(false);
    }

    public void next() {

        if(gameLogic.isGameFinished()) {
            pause();
        } else {


            if(nextStep != null) {
                gameLogic.selectColor(nextStep);
            }

            nextStep = solver.nextStep();
            nextColor.set(colorProfile.getColor(nextStep));

        }
    }

    public DoubleProperty waitTime() {
        return waitTime;
    }


    public double getMinWaitTime() {
        return 0.1;
    }

    public double getMaxWaitTime() {
        return 5;
    }

    public double getWaitTimeIncrement() {
        return 0.3;
    }

    public BooleanProperty autoButtonPressed() {
        return autoButtonPressed;
    }

    public ObservableValue<Paint> nextColor() {
        return nextColor;
    }


    public ObservableStringValue speedLabel() {
        return speedLabel;
    }

    public StringProperty selectedSolverName() {
        return selectedSolverName;
    }

    public ObservableList<String> solverNames() {
        return solverNames;
    }

    public void openBenchmark() {
        BenchmarkViewPopup.open();
        SolverViewPopup.close();
    }
}
