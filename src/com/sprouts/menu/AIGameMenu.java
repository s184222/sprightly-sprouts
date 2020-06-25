package com.sprouts.menu;

import java.util.List;

import com.sprouts.SproutsMain;
import com.sprouts.ai.AIFacade;
import com.sprouts.ai.player.Player;
import com.sprouts.ai.player.RandomPlayer;
import com.sprouts.game.move.IdMove;

public class AIGameMenu extends GameMenu {

	private final AIFacade aiFacade;
	private Player aiPlayer;
	
	public AIGameMenu(SproutsMain main) {
		super(main);
		
		aiFacade = new AIFacade();
		aiPlayer = new RandomPlayer();
	}

	@Override
	public void reset(int initialSproutCount) {
		super.reset(initialSproutCount);

		aiFacade.createFreshPosition(initialSproutCount);

		aiPlayer = new RandomPlayer();
	}
	
	@Override
	public boolean executeMoves(List<String> rawMoves) {
		if (super.executeMoves(rawMoves)) {
			aiFacade.makeMoves(rawMoves);
			return true;
		}
		
		return false;
	}
	
	@Override
	protected void onMoveExecuted(String move) {
		if (!facade.isGameOver()) {
			try {
				aiFacade.makeMove(move);
				IdMove aiMove = aiPlayer.getMove(aiFacade.getPosition());

				facade.executeMove(aiMove.toString());
				aiFacade.makeMove(aiMove.toString());
			} catch (Exception e) {
				main.setMenu(new GameOverSproutsMenu(main, "You beat the computer!"));
			}

			if (facade.isGameOver())
				main.setMenu(new GameOverSproutsMenu(main, "You lost to a computer..."));
		} else {
			main.setMenu(new GameOverSproutsMenu(main, "You beat the computer!"));
		}
	}
}
