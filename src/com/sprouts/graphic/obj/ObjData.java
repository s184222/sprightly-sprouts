package com.sprouts.graphic.obj;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import com.sprouts.IResource;
import com.sprouts.graphic.buffer.VertexArray;
import com.sprouts.graphic.buffer.VertexBuffer;
import com.sprouts.graphic.obj.shader.ObjShader;
import com.sprouts.graphic.texture.Texture;

public class ObjData implements IResource {
	
	private VertexArray vertexArray;
	private VertexBuffer vertexBuffer;
	
	private int numVertices;

	private Texture texture;

	public ObjData(VertexBuffer vertexBuffer, int numVertices) {
		this.vertexBuffer = vertexBuffer;
		this.numVertices = numVertices;
		
		vertexArray = new VertexArray();	
	}
	
	public void initBuffers(ObjShader shader) {
		shader.initBuffers(vertexArray, vertexBuffer);
	}
	
	public void drawBuffer() {
		glActiveTexture(GL_TEXTURE0);
		texture.bind();
		
		vertexArray.bind();
		glDrawArrays(GL_TRIANGLES, 0, numVertices);
	}
	
	public void setTexture(Texture texture) {
		this.texture = texture;
	}
	
	public Texture getTexture() {
		return texture;
	}

	@Override
	public void dispose() {
		vertexArray.dispose();
		vertexBuffer.dispose();
	}
}
