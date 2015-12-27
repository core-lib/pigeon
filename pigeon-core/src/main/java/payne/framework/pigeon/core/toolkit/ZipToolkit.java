package payne.framework.pigeon.core.toolkit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Stack;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

public class ZipToolkit {

	public static void pack(String source, String target) throws IOException {
		pack(new File(source), new File(target));
	}

	public static void pack(File source, File target) throws IOException {
		if (!source.exists()) {
			throw new FileNotFoundException("file not found at path : " + source.getAbsolutePath());
		}
		
		ZipArchiveOutputStream zipArchiveOutputStream = null;
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(target);
			zipArchiveOutputStream = new ZipArchiveOutputStream(fileOutputStream);

			Stack<String> parent = new Stack<String>();
			parent.push(null);
			Stack<File> file = new Stack<File>();
			file.push(source);

			while (!file.isEmpty()) {
				File f = file.pop();
				String p = parent.pop();

				if (f.isDirectory()) {
					File[] children = f.listFiles();
					for (File child : children) {
						parent.push(p == null ? f.getName() : p + File.separator + f.getName());
						file.push(child);
					}
				} else {
					ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(f, p == null ? f.getName() : p + File.separator + f.getName());
					zipArchiveOutputStream.putArchiveEntry(zipArchiveEntry);
					FileInputStream fis = null;
					try {
						fis = new FileInputStream(f);
						IOToolkit.transmit(fis, zipArchiveOutputStream);
						zipArchiveOutputStream.closeArchiveEntry();
					} finally {
						IOToolkit.close(fis);
					}
				}
			}
		} finally {
			IOToolkit.close(zipArchiveOutputStream);
			IOToolkit.close(fileOutputStream);
		}
	}

	public static void unpack(String source, String target) throws IOException {
		unpack(new File(source), new File(target));
	}

	public static void unpack(File source, File target) throws IOException {
		if (!source.exists()) {
			throw new FileNotFoundException("file not found at path : " + source.getAbsolutePath());
		}
		ZipArchiveInputStream zipArchiveInputStream = null;
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(source);
			zipArchiveInputStream = new ZipArchiveInputStream(fileInputStream);
			ZipArchiveEntry zipArchiveEntry = null;
			while ((zipArchiveEntry = zipArchiveInputStream.getNextZipEntry()) != null) {
				if (zipArchiveEntry.isDirectory()) {
					new File(target, zipArchiveEntry.getName()).mkdirs();
				}
				FileOutputStream fileOutputStream = null;
				try {
					File file = new File(target, zipArchiveEntry.getName());
					if (!file.getParentFile().exists()) {
						file.getParentFile().mkdirs();
					}
					fileOutputStream = new FileOutputStream(file);
					IOToolkit.transmit(zipArchiveInputStream, fileOutputStream);
				} finally {
					IOToolkit.close(fileOutputStream);
				}
			}
		} finally {
			IOToolkit.close(zipArchiveInputStream);
		}
	}
	
}
