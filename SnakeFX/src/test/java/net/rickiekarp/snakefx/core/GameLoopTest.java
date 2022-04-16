package net.rickiekarp.snakefx.core;


import net.rickiekarp.snakefx.view.ViewModel;
import javafx.animation.Animation.Status;
import javafx.animation.Timeline;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameLoopTest {
	private GameLoop gameLoop;
	private ViewModel viewModel;

	@BeforeEach
	void setup() {
		viewModel = new ViewModel();
		viewModel.getSpeed().set(SpeedLevel.EASY);
		gameLoop = new GameLoop(viewModel);
	}

	@Test
	void testStoppedTimelineStaysStoppedAfterSpeedChange() {
		Assertions.assertEquals(getTimeline().getStatus(), Status.STOPPED);
		viewModel.getSpeed().set(SpeedLevel.HARD);
		Assertions.assertEquals(getTimeline().getStatus(), Status.STOPPED);
	}

//	@Test
//	void testPlayingTimelineStaysPlayingAfterSpeedChange() {
//		getTimeline().play();
//		Assertions.assertEquals(getTimeline().getStatus(), Status.RUNNING);
//		viewModel.getSpeed().set(SpeedLevel.HARD);
//		Assertions.assertEquals(getTimeline().getStatus(), Status.RUNNING);
//	}

//	@Test
//	void testTimelineIsPlayingAndStoppedAfterChangeInViewModel() {
//		Assertions.assertEquals(viewModel.getGameloopStatus().get(), Status.STOPPED);
//		Assertions.assertEquals(getTimeline().getStatus(), Status.STOPPED);
//		viewModel.getGameloopStatus().set(Status.RUNNING);
//		Assertions.assertEquals(getTimeline().getStatus(), Status.RUNNING);
//
//		viewModel.getGameloopStatus().set(Status.PAUSED);
//		Assertions.assertEquals(getTimeline().getStatus(), Status.PAUSED);
//	}

	private Timeline getTimeline() {
		return gameLoop.getTimeline();
	}
}
