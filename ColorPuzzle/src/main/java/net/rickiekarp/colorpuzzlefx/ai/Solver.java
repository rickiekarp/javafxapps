package net.rickiekarp.colorpuzzlefx.ai;

import net.rickiekarp.colorpuzzlefx.core.Colors;
import net.rickiekarp.colorpuzzlefx.core.GameLogic;

public interface Solver {

    void setGame(GameLogic game);

    Colors nextStep();
}
