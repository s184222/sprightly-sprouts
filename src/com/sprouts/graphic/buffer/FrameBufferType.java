package com.sprouts.graphic.buffer;

public enum FrameBufferType {

	TEXTURE(true, false),
	DEPTH(false, true),
	DEPTH_AND_TEXTURE(true, true);
	
	private final boolean texAttach;
	private final boolean depthAttach;
	
	private FrameBufferType(boolean texAttach, boolean depthAttach) {
		this.texAttach = texAttach;
		this.depthAttach = depthAttach;
	}
	
	public boolean hasTextureAttachment() {
		return texAttach;
	}

	public boolean hasDepthAttachment() {
		return depthAttach;
	}
}
