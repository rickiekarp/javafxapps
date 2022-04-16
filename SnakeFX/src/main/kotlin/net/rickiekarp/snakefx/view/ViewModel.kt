package net.rickiekarp.snakefx.view

import net.rickiekarp.snakefx.core.Direction
import net.rickiekarp.snakefx.core.SpeedLevel
import javafx.animation.Animation.Status
import javafx.beans.property.*

import net.rickiekarp.snakefx.settings.Config.*

/**
 * This class is the central viewmodel that contains the current state of the
 * applications main properties.
 */
class ViewModel {

    val points: IntegerProperty = SimpleIntegerProperty(0)

    val speed: ObjectProperty<SpeedLevel> = SimpleObjectProperty(SpeedLevel.MEDIUM)

    val collision: BooleanProperty = SimpleBooleanProperty(false)

    val gameloopStatus: ObjectProperty<Status> = SimpleObjectProperty(Status.STOPPED)

    val gridSize: IntegerProperty = SimpleIntegerProperty(ROW_AND_COLUMN_COUNT.get())

    val newHighscoreWindowOpen: BooleanProperty = SimpleBooleanProperty(false)

    val snakeDirection: ObjectProperty<Direction> = SimpleObjectProperty(Direction.UP)

    val pointIncrement: IntegerProperty = SimpleIntegerProperty(speed.get().fps / 5)
}