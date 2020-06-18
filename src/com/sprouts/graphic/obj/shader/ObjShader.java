package com.sprouts.graphic.obj.shader;

import static org.lwjgl.opengl.GL11.GL_FLOAT;

import com.sprouts.graphic.buffer.VertexArray;
import com.sprouts.graphic.buffer.VertexBuffer;
import com.sprouts.graphic.shader.ShaderProgram;
import com.sprouts.math.Mat4;

public class ObjShader extends ShaderProgram {
	
	public static final int POSITION_ATTRIB_INDEX  = 0;
	public static final int TEX_COORD_ATTRIB_INDEX = 1;
	public static final int NORMAL_COORD_ATTRIB_INDEX  = 2;	
	
	private static final int BYTE_SIZE       = 1;
	private static final int FLOAT_BYTE_SIZE = 4 * BYTE_SIZE;
	
	private static final int POSITION_SIZE  = 3;
	private static final int TEX_COORD_SIZE = 2;
	private static final int NORMAL_COORD_SIZE  = 3;	
	
	private static final int POSITION_BYTE_SIZE  = POSITION_SIZE * FLOAT_BYTE_SIZE;
	private static final int TEX_COORD_BYTE_SIZE = TEX_COORD_SIZE * FLOAT_BYTE_SIZE;
	private static final int NORMAL_COORD_BYTE_SIZE = NORMAL_COORD_SIZE * FLOAT_BYTE_SIZE;

	private static final int VERTEX_BYTE_SIZE = POSITION_BYTE_SIZE +  TEX_COORD_BYTE_SIZE + NORMAL_COORD_BYTE_SIZE;
	
	private static final int POSITION_OFFSET  = 0;
	private static final int TEX_COORD_OFFSET = POSITION_OFFSET + POSITION_BYTE_SIZE;
	private static final int NORMAL_COORD_OFFSET = TEX_COORD_OFFSET + TEX_COORD_BYTE_SIZE;
	
	private final int projMatLocation;
	private final int viewMatLocation;
	private final int modlMatLocation;

	private final int textureSamplerLocation;

	public ObjShader(String vertexFile, String fragmentFile) {
		super(vertexFile, fragmentFile);
		
		projMatLocation = getUniformLocation("u_ProjMat");
		viewMatLocation = getUniformLocation("u_ViewMat");
		modlMatLocation = getUniformLocation("u_ModlMat");

		textureSamplerLocation = getUniformLocation("u_TextureSampler");
	}

	@Override
	protected void bindAttributes() {
		bindAttribute(POSITION_ATTRIB_INDEX, "a_Position");
		bindAttribute(TEX_COORD_ATTRIB_INDEX, "a_TexCoord");
		bindAttribute(NORMAL_COORD_ATTRIB_INDEX, "a_NormalCoord");
	}
	
	public void setProjMat(Mat4 projMat) {
		uniformMat4(projMatLocation, projMat);
	}

	public void setViewMat(Mat4 viewMat) {
		uniformMat4(viewMatLocation, viewMat);
	}
	
	public void setModlMat(Mat4 modlMat) {
		uniformMat4(modlMatLocation, modlMat);
	}
	
	public void setTextureSampler(int textureSampler) {
		uniformInt(textureSamplerLocation, textureSampler);
	}
	
	public void initBuffers(VertexArray vertexArray, VertexBuffer buffer) {
		vertexArray.storeBuffer(POSITION_ATTRIB_INDEX, buffer, GL_FLOAT, false, POSITION_SIZE, VERTEX_BYTE_SIZE, POSITION_OFFSET);
		vertexArray.storeBuffer(TEX_COORD_ATTRIB_INDEX, buffer, GL_FLOAT, false, TEX_COORD_SIZE, VERTEX_BYTE_SIZE, TEX_COORD_OFFSET);
		vertexArray.storeBuffer(NORMAL_COORD_ATTRIB_INDEX, buffer, GL_FLOAT, false, NORMAL_COORD_SIZE , VERTEX_BYTE_SIZE, NORMAL_COORD_OFFSET);

	}
	
	public int getVertexByteSize() {
		return VERTEX_BYTE_SIZE;
	}
}
