package com.sprouts.menu;

import java.util.List;

import com.sprouts.SproutsMain;

import sprouts.ai.AIFacade;
import sprouts.ai.player.Player;
import sprouts.ai.player.RandomPlayer;
import sprouts.game.move.IdMove;
import sprouts.game.move.MovePipeLineException;

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
			aiFacade.makeMove(move);
			
			IdMove aiMove = aiPlayer.getMove(aiFacade.getPosition());
	
			try {
				facade.executeMove(aiMove.toString());
				aiFacade.makeMove(aiMove.toString());
			} catch (MovePipeLineException e) {
				main.setMenu(new GameOverSproutsMenu(main, "You beat the computer!"));
			}

			if (facade.isGameOver())
				main.setMenu(new GameOverSproutsMenu(main, "You lost to a computer..."));
		} else {
			main.setMenu(new GameOverSproutsMenu(main, "You beat the computer!"));
		}
	}
}
