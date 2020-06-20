package com.sprouts.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SproutUtil {

	public static SproutMoves loadMovesFromFile(File file) throws IOException {
		if (!file.isFile())
			throw new IOException("Given file is not a file");
		return loadMovesFromFile(new FileInputStream(file));
	}

	public static SproutMoves loadMovesFromFile(InputStream is) throws IOException {
		int initialSproutCount = 0;
		List<String> rawMoves = new ArrayList<String>();
		
		try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
			String line;
			if ((line = br.readLine()) != null) {
				try {
					initialSproutCount = Integer.parseInt(line);
				} catch (NumberFormatException e) {
					throw new IOException("Initial sprout count is not formatted correctly!");
				}
			} else {
				throw new IOException("First line must start with initialSproutCount!");
			}
			
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (!line.isEmpty())
					rawMoves.add(line.replace(' ', ','));
			}
		}
		
		return new SproutMoves(initialSproutCount, rawMoves);
	}
	
	public static class SproutMoves {
		
		public final int initialSproutCount;
		public final List<String> rawMoves;
		
		private SproutMoves(int initialSproutCount, List<String> rawMoves) {
			this.initialSproutCount = initialSproutCount;
			this.rawMoves = rawMoves;
		}
	}
}
