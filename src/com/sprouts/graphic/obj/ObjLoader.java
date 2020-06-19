package com.sprouts.graphic.obj;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.sprouts.graphic.buffer.VertexArray;
import com.sprouts.graphic.buffer.VertexBuffer;
import com.sprouts.graphic.obj.shader.BasicObjShader;
import com.sprouts.graphic.tessellator.VertexAttribBuilder;
import com.sprouts.math.Vec2;
import com.sprouts.math.Vec3;

public class ObjLoader {

	public ObjLoader() {

	}

	public static ObjData loadObj(String path) throws IOException {
		InputStream is = ObjLoader.class.getResourceAsStream(path);
		if (is == null) {
			throw new IOException("File was not found: " + path);
		}
		BufferedReader buf = new BufferedReader(new InputStreamReader(is));
		String line = buf.readLine();

		List<Vec3> vPositions = new ArrayList<Vec3>();
		List<Vec2> vTexCoords = new ArrayList<Vec2>();
		List<Vec3> vNormals = new ArrayList<Vec3>();

		String[] currentLine;
		String[] currentVertex;

		while (true) {
			if (line.startsWith("v ")) {
				currentLine = line.split(" ");

				Vec3 pos = new Vec3();

				pos.x = Float.parseFloat(currentLine[1]);
				pos.y = Float.parseFloat(currentLine[2]);
				pos.z = Float.parseFloat(currentLine[3]);

				vPositions.add(pos);
			}

			else if (line.startsWith("vt ")) {
				currentLine = line.split(" ");

				Vec2 texCoords = new Vec2();

				texCoords.x = Float.parseFloat(currentLine[1]);
				texCoords.y = Float.parseFloat(currentLine[2]);

				vTexCoords.add(texCoords);
			}

			else if (line.startsWith("vn ")) {
				currentLine = line.split(" ");

				Vec3 normal = new Vec3();

				normal.x = Float.parseFloat(currentLine[1]);
				normal.y = Float.parseFloat(currentLine[2]);
				normal.z = Float.parseFloat(currentLine[3]);

				vNormals.add(normal);
			}

			else if (line.startsWith("f ")) {
				break;
			}

			line = buf.readLine();
		}

		VertexAttribBuilder builder = new VertexAttribBuilder(8 * VertexAttribBuilder.FLOAT_BYTE_SIZE,
				vPositions.size());

		while (line != null) {

			if (line.startsWith("f ")) {

				currentLine = line.split(" ");

				for (int i = 1; i < currentLine.length; i++) {

					currentVertex = currentLine[i].split("/");

					builder.put(vPositions.get(Integer.parseInt(currentVertex[0]) - 1));

					builder.put(vTexCoords.get(Integer.parseInt(currentVertex[1]) - 1));

					builder.put(vNormals.get(Integer.parseInt(currentVertex[2]) - 1));

				}

			}
			
			line = buf.readLine();
		}

		buf.close();
		
		System.out.println(vPositions.get(0).x + ", " + vPositions.get(0).y + ", " + vPositions.get(0).z);
		
		vPositions.clear();
		vTexCoords.clear();
		vNormals.clear();
		
		int numVertices = builder.getPosition() / builder.getVertexSize();
		
		VertexBuffer vertexBuffer = new VertexBuffer(builder.getVertexSize(), numVertices);
		
		builder.writeBuffer(vertexBuffer);
		
		builder.close();

		return new ObjData(vertexBuffer, numVertices);

	}
}
