package mpay.my.ecpos_manager_v2.webutil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipTool {
	public static void unzipFile(String zipFilePath, String destDir) throws Exception {
		File dir = new File(destDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		FileInputStream fis;
		byte[] buffer = new byte[1024];
		fis = new FileInputStream(zipFilePath);
		ZipInputStream zis = new ZipInputStream(fis);
		ZipEntry ze = zis.getNextEntry();
		while (ze != null) {
			String fileName = ze.getName();
			File newFile = new File(destDir + File.separator + fileName);
			//System.out.println("Unzipping to " + newFile.getAbsolutePath());
			new File(newFile.getParent()).mkdirs();
			FileOutputStream fos = new FileOutputStream(newFile);
			int len;
			while ((len = zis.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}
			fos.close();
			zis.closeEntry();
			ze = zis.getNextEntry();
		}
		zis.closeEntry();
		zis.close();
		fis.close();
	}
}