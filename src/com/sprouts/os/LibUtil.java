package com.sprouts.os;

import java.io.File;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;

public class LibUtil {

	private static final String NATIVES_PATH = "lib/natives";
	private static final String WINDOWS_NATIVES_PATH = NATIVES_PATH + "/windows";
	private static final String LINUX_NATIVES_PATH = NATIVES_PATH + "/windows";

	public static void loadNatives() {
		// Look for native files
		Queue<File> filesToSearch = new LinkedList<File>();
		filesToSearch.add(new File(getNativesPath()));
	
		String libraryPath = System.getProperty("java.library.path");
		
		File file;
		while ((file = filesToSearch.poll()) != null) {
			if (file.isDirectory()) {
				for (File subFile : file.listFiles())
					filesToSearch.add(subFile);
			} else if (isNativeFile(file)) {
				libraryPath += File.pathSeparator + file.getAbsolutePath();
			}
		}
		
		System.setProperty("java.library.path", libraryPath);
	}
	
	private static boolean isNativeFile(File file) {
		String name = file.getName();
		return name.endsWith(".dll") || name.endsWith(".os");
	}
	
	public static String getNativesPath() {
		String osname = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
		if (osname.indexOf("win") >= 0)
			return WINDOWS_NATIVES_PATH;
		if (osname.indexOf("nux") >= 0)
			return LINUX_NATIVES_PATH;
		
		throw new UnknownOSException("Unknown OS: " + osname);
	}
}
