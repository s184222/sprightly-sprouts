package com.sprouts.graphic.post;

import com.sprouts.IResource;
import com.sprouts.graphic.buffer.FrameBuffer;

public interface IPost extends IResource {

	public void setSize(int width, int height);
	
	public void process(FrameBuffer source);
	
	public FrameBuffer getTargetBuffer();
	
}
