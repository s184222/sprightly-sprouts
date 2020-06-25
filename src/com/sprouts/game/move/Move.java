package com.sprouts.game.move;

import java.util.List;

import com.sprouts.game.model.Edge;
import com.sprouts.game.model.Region;
import com.sprouts.game.model.Sprout;

/**
 * 
 * A move representation container, where the sprouts and edges are specified by their objects.
 * 
 * @author Rasmus Møller Larsen, s184190
 *
 */
public class Move {
	
	public Sprout from;
	public Sprout to;
	
	public Region region;

	// optional to fill out.
	
	public List<Sprout> inners;
	
	public Edge fromEdge;
	public Edge toEdge;
	

}
