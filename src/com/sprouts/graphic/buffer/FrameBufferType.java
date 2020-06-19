package com.sprouts.graphic.buffer;

public enum FrameBufferType {

	COLOR(true, false),
	DEPTH(false, true),
	DEPTH_AND_COLOR(true, true),
	MULTISAMPLED_DEPTH_AND_COLOR(true, true);
	
	private final boolean texAttach;
	private final boolean depthAttach;
	
	private FrameBufferType(boolean texAttach, boolean depthAttach) {
		this.texAttach = texAttach;
		this.depthAttach = depthAttach;
	}
	
	public boolean hasColorAttachment() {
		return texAttach;
	}

	public boolean hasDepthAttachment() {
		return depthAttach;
	}
}
