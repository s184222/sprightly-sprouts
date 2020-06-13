package com.sprouts.graphic.tessellator2d.shader;

import static org.lwjgl.opengl.GL11.GL_BYTE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;

import com.sprouts.graphic.buffer.VertexArray;
import com.sprouts.graphic.buffer.VertexBuffer;
import com.sprouts.graphic.shader.ShaderProgram;
import com.sprouts.math.Mat4;

public class Tessellator2DShader extends ShaderProgram {

	public static final int POSITION_ATTRIB_INDEX  = 0;
	public static final int COLOR_ATTRIB_INDEX     = 1;
	public static final int TEX_COORD_ATTRIB_INDEX = 2;
	public static final int TEX_INDEX_ATTRIB_INDEX = 3;
	
	private static final int BYTE_SIZE       = 1;
	private static final int FLOAT_BYTE_SIZE = 4 * BYTE_SIZE;
	
	private static final int POSITION_SIZE  = 2;
	private static final int COLOR_SIZE     = 4;
	private static final int TEX_COORD_SIZE = 2;
	private static final int TEX_INDEX_SIZE = 1;
	
	private static final int POSITION_BYTE_SIZE  = POSITION_SIZE * FLOAT_BYTE_SIZE;
	private static final int COLOR_BYTE_SIZE     = COLOR_SIZE * BYTE_SIZE;
	private static final int TEX_COORD_BYTE_SIZE = TEX_COORD_SIZE * FLOAT_BYTE_SIZE;
	private static final int TEX_INDEX_BYTE_SIZE = TEX_INDEX_SIZE * BYTE_SIZE;

	private static final int VERTEX_BYTE_SIZE = POSITION_BYTE_SIZE + COLOR_BYTE_SIZE + TEX_COORD_BYTE_SIZE + TEX_INDEX_BYTE_SIZE;
	
	private static final int POSITION_OFFSET  = 0;
	private static final int COLOR_OFFSET     = POSITION_OFFSET + POSITION_BYTE_SIZE;
	private static final int TEX_COORD_OFFSET = COLOR_OFFSET + COLOR_BYTE_SIZE;
	private static final int TEX_INDEX_OFFSET = TEX_COORD_OFFSET + TEX_COORD_BYTE_SIZE;
	
	private final int projMatLocation;
	private final int textureSamplersLocation;
	
	public Tessellator2DShader(String vertexFile, String fragmentFile) {
		super(vertexFile, fragmentFile);

		projMatLocation = getUniformLocation("u_ProjMat");
		textureSamplersLocation = getUniformLocation("u_TextureSamplers");
	}

	@Override
	protected void bindAttributes() {
		bindAttribute(POSITION_ATTRIB_INDEX, "a_Position");
		bindAttribute(COLOR_ATTRIB_INDEX, "a_Color");
		bindAttribute(TEX_COORD_ATTRIB_INDEX, "a_TexCoord");
		bindAttribute(TEX_INDEX_ATTRIB_INDEX, "a_TexIndex");
	}
	
	public void setProjMat(Mat4 projMat) {
		uniformMat4(projMatLocation, projMat);
	}
	
	public void setTextureSamplers(int[] textureSamplers) {
		uniformIntArray(textureSamplersLocation, textureSamplers);
	}

	public void initBuffers(VertexArray vertexArray, VertexBuffer buffer) {
		vertexArray.storeBuffer(POSITION_ATTRIB_INDEX, buffer, GL_FLOAT, false, POSITION_SIZE, VERTEX_BYTE_SIZE, POSITION_OFFSET);
		vertexArray.storeBuffer(COLOR_ATTRIB_INDEX, buffer, GL_UNSIGNED_BYTE, true, COLOR_SIZE, VERTEX_BYTE_SIZE, COLOR_OFFSET);
		vertexArray.storeBuffer(TEX_COORD_ATTRIB_INDEX, buffer, GL_FLOAT, false, TEX_COORD_SIZE, VERTEX_BYTE_SIZE, TEX_COORD_OFFSET);
		vertexArray.storeBuffer(TEX_INDEX_ATTRIB_INDEX, buffer, GL_BYTE, false, TEX_INDEX_SIZE, VERTEX_BYTE_SIZE, TEX_INDEX_OFFSET);
	}
	
	public int getVertexByteSize() {
		return VERTEX_BYTE_SIZE;
	}
}
