package com.sprouts.graphic.tessellator2d;

import com.sprouts.graphic.tessellator.VertexLayerID;

public interface ILayeredTessellator2D extends ITessellator2D {

	/**
	 * Rebuilds the layer pointed to by the given {@code layerId}. If the second
	 * parameter {@code rebuildAscendingLayers}, is true then all ascending layers
	 * that have been added by the {@link #pushLayer()} method on the given layer
	 * will be removed. If these layers are required to be rebuilt then one must call
	 * the {@link #pushLayer()} and {@link #popLayer()} methods for each ascending
	 * layer that should be rebuilt. If {@code rebuildAscendingLayers} is false, then
	 * only the vertices on the layer pointed to by {@code layerId} will be removed.
	 * In this case it is only allowed to call the {@link #pushLayer()} method once,
	 * before a second call to the {@link #popLayer()} method is made.
	 * <br><br>
	 * If {@code layerId} is {@code null} then the root layer will be rebuilt.
	 * 
	 * @param layerId - the id that points to the layer that should be rebuilt.
	 * @param rebuildAscendingLayers - whether the ascending layers should also be
	 *                                 rebuilt.
	 * 
	 * @throws IllegalStateException if the tessellator is closed, or if a layer is
	 *                               already being rebuilt at the moment of calling this
	 *                               method.
	 * @throws IllegalArgumentException if no layer is pointed to by the given id.
	 * 
	 * @see #pushLayer()
	 * @see #popLayer()
	 * @see #finishRebuilding()
	 */
	public void rebuildLayer(VertexLayerID layerId, boolean rebuildAscendingLayers);

	/**
	 * Finishes rebuilding the current building layer. If the tessellator is not building
	 * then an exception is thrown.
	 * 
	 * @throws IllegalStateException if the tessellator is closed, or the tessellator
	 *                               is not currently rebuilding a layer, or if the layers
	 *                               have not been popped properly.
	 * 
	 * @see #rebuildLayer(VertexLayerID, boolean)
	 */
	public void finishRebuilding();

	/**
	 * Pushes a new layer to the building layer stack. If the current layer is null then
	 * the building layer itself will be added to the stack. Note that any layer that is
	 * added to the building layer stack must also be popped with {@link #popLayer()}.
	 * If any vertices have been drawn, and the current layer has any building ascending
	 * layers then it is not considered tightly packed and an exception is thrown.
	 * 
	 * @return The layer id of the layer that was added to the building layer stack, or
	 *         null if that layer is the root layer.
	 * 
	 * @throws IllegalStateException if the tessellator is closed, or if the tessellator
	 *                               is not currently building, or if the current layer
	 *                               is not tightly packed, or if an attempt is made to
	 *                               rebuild an ascending layer when it is not permitted.
	 * 
	 * @see #popLayer()
	 */
	public VertexLayerID pushLayer();

	/**
	 * Pops the current layer from the building layer stack. If the current layer is the
	 * building layer itself, then the current layer will be set to null and the building
	 * can be finished. If the current layer is not the building layer and there are no
	 * layers to be popped, then an exception is thrown.
	 * 
	 * @throws IllegalStateException if the tessellator is closed, or if the tessellator
	 *                               is not currently building, or if there is no layer
	 *                               to pop, or if the current layer is not tightly packed.
	 * 
	 * @see #pushLayer()
	 */
	public void popLayer();

	/**
	 * Draws all the cached layers of this tessellator.
	 * 
	 * @throws IllegalStateException if the tessellator is closed, or if the tessellator is
	 *                               currently rebuilding.
	 */
	public void drawLayers();
	
	/**
	 * @return True, if the tessellator is currently rebuilding a layer, false otherwise.
	 * 
	 * @see #rebuildLayer(VertexLayerID, boolean)
	 */
	public boolean isBuilding();
	
	/**
	 * Queries and returns the depth of the layer pointed to by the {@code layerId}.
	 * 
	 * @param layerId - the layer id of the layer.
	 * 
	 * @return The depth of the layer.
	 * 
	 * @see #pushLayer()
	 */
	public int getLayerDepth(VertexLayerID layerId);
	
}
