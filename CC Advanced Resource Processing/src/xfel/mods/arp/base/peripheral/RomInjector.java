/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package xfel.mods.arp.base.peripheral;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cpw.mods.fml.common.FMLCommonHandler;

/**
 * This class helps you writing your peripheral api and help files into
 * computercrafts rom directory. You don't need to bother about the file any
 * more after injecting it.
 * 
 * It also detects the file's version if the first line of an injected file ends
 * with <code>version "someVersion"</code>, where "someVersion" might be your
 * mods version. The file is only overwritten if the new file has a higher
 * version number.
 * 
 * @author Xfel
 * 
 */
public class RomInjector {

	private static final String VERSION_MARKER = "version ";

	private static final String COMPUTERCRAFT_ROM_DIRECTORY = "mods/ComputerCraft/lua/rom";

	public static void setMinecraftHome(File mchome){
		romDirectory = new File(mchome, COMPUTERCRAFT_ROM_DIRECTORY);
		if (!romDirectory.exists() && !romDirectory.mkdirs()) {
			throw new RuntimeException(
					"Unable to locate ComputerCraft rom directory at: "
							+ romDirectory.getAbsolutePath());
		}
	}
	
	private static File romDirectory;

	/**
	 * Takes a source file from the classpath.
	 * 
	 * @see #injectFile(String, InputStream)
	 */
	public static void injectClasspathFile(String targetName, String sourceName) {
		InputStream source = RomInjector.class.getClassLoader()
				.getResourceAsStream(sourceName);

		if (source == null) {
			throw new IllegalArgumentException("Resource not found: "
					+ sourceName);
		}

		try {
			injectFile(targetName, source);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				source.close();
			} catch (IOException e) {
				System.err.println("Error closing stream:");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Writes the given <code>InputStream</code> contents to the target file
	 * name.
	 * 
	 * @param targetName
	 *            a path relative to the rom directory
	 * @param source
	 *            an input stream containing the source file
	 * @throws IOException
	 */
	public static void injectFile(String targetName, InputStream source)
			throws IOException {
		File targetFile = new File(romDirectory, targetName);

		BufferedInputStream bufferedSource;
		if (source instanceof BufferedInputStream) {
			bufferedSource = (BufferedInputStream) source;
		} else {
			bufferedSource = new BufferedInputStream(source);
		}

		if (targetFile.exists()) {
			FileInputStream fis = new FileInputStream(targetFile);
			try {
				bufferedSource.mark(256);
				if (versionCheck(fis, bufferedSource)) {
					return;
				}
				bufferedSource.reset();
			} finally {
				try {
					fis.close();
				} catch (IOException e) {
					System.err.println("Error closing stream:");
					e.printStackTrace();
				}
			}
		} else if (!targetFile.getParentFile().exists()&&!targetFile.getParentFile().mkdirs()) {
			System.err.println("Could not create file: " + targetFile);
			return;
		}
		
		System.out.println("Injecting api file: "+targetName);
		
		FileOutputStream fos = new FileOutputStream(targetFile);
		try {
			copy(bufferedSource, fos);
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				System.err.println("Error closing stream:");
				e.printStackTrace();
			}
		}
	}

	private static final int IO_BUFFER_SIZE = 4 * 1024;

	private static void copy(InputStream in, OutputStream out)
			throws IOException {
		byte[] b = new byte[IO_BUFFER_SIZE];
		int read;
		while ((read = in.read(b)) != -1) {
			out.write(b, 0, read);
		}
	}

	private static boolean versionCheck(InputStream targetFile,
			InputStream sourceFile) throws IOException {
		String sourceVerString = readVersionString(sourceFile);
		String targetVerString = readVersionString(targetFile);
		return targetVerString.compareToIgnoreCase(sourceVerString) <= 0;
	}

	private static String readVersionString(InputStream in) throws IOException {
		StringBuilder sb = new StringBuilder();

		while (true) {
			int ch = in.read();
			if (ch == -1) {
				throw new EOFException();
			}
			if (ch == '\r' || ch == '\n') {
				break;
			}
			sb.append((char) ch);
		}

		int idx = sb.indexOf(VERSION_MARKER);

		if (idx == -1) {
			return "0.0.0";
		}

		return sb.substring(idx + VERSION_MARKER.length());
	}

}
