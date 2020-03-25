package com.sprouts.graphic.shader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import com.sprouts.math.Mat4;
import com.sprouts.math.Vec2;
import com.sprouts.math.Vec3;
import com.sprouts.math.Vec4;

public abstract class ShaderProgram {

	private static FloatBuffer mat4Buffer;
	
	private int programID;
	private int vertexShaderID;
	private int fragmentShaderID;

	public ShaderProgram(String vertexFile, String fragmentFile) {
		vertexShaderID = loadShader(vertexFile, GL30.GL_VERTEX_SHADER);
		fragmentShaderID = loadShader(fragmentFile, GL30.GL_FRAGMENT_SHADER);
		programID = GL30.glCreateProgram();

		GL30.glAttachShader(programID, vertexShaderID);
		GL30.glAttachShader(programID, fragmentShaderID);
		bindAttributes();
		GL30.glLinkProgram(programID);
		GL30.glValidateProgram(programID);
	}

	protected abstract void bindAttributes();
	
	protected void bindAttribute(int attribute, String variableName) {
		GL30.glBindAttribLocation(programID, attribute, variableName);
	}

	protected void uniformFloat(int location, float f) {
		GL30.glUniform1f(location, f);
	}
	
	protected void uniformVec2(int location, Vec2 v) {
		GL30.glUniform2f(location, v.x, v.y);
	}

	protected void uniformVec3(int location, Vec3 v) {
		GL30.glUniform3f(location, v.x, v.y, v.z);
	}

	protected void uniformVec4(int location, Vec4 v) {
		GL30.glUniform4f(location, v.x, v.y, v.z, v.w);
	}

	protected void uniformMat4(int location, Mat4 m) {
		if (mat4Buffer == null)
			mat4Buffer = FloatBuffer.allocate(4 * 4);
		mat4Buffer.clear();
		m.writeBuffer(mat4Buffer);
		mat4Buffer.flip();
		
		GL30.glUniformMatrix4fv(location, true, mat4Buffer);
	}
	
	protected int getUniformLocation(String uniformName) {
		return GL30.glGetUniformLocation(programID, uniformName);
	}
	
	public void enable() {
		GL30.glUseProgram(programID);
	}

	public void disable() {
		GL30.glUseProgram(0);
	}

	public void dispose() {
		disable();
		
		GL30.glDetachShader(programID, vertexShaderID);
		GL30.glDetachShader(programID, fragmentShaderID);
		
		GL30.glDeleteShader(vertexShaderID);
		GL30.glDeleteShader(fragmentShaderID);
		
		GL30.glDeleteProgram(programID);
	}

	public int loadShader(String path, int type) {
		StringBuilder shaderSource = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
			String line;
			while ((line = reader.readLine()) != null) {
				shaderSource.append(line);
				shaderSource.append('\n');
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to load shader: " + path, e);
		}
		
		int shaderID = GL30.glCreateShader(type);
		GL30.glShaderSource(shaderID, shaderSource);
		GL30.glCompileShader(shaderID);
		
		if (GL30.glGetShaderi(shaderID, GL30.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			System.out.println(GL30.glGetShaderInfoLog(shaderID, 500));
			throw new RuntimeException("Could not compile shader: " + path);
		}
		
		return shaderID;
	}
}
