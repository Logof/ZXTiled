package org.github.logof.zxtiled.mapeditor.util;

import org.github.logof.zxtiled.mapeditor.Resources;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnzipUtility {

    private static final String TMP_FILE_EXTENSION = ".zip";
    private static final String TMP_FILE_PREFIX = "tmp_";

    public static void extractFiles(String filePath) {
        Path tempPath = null;
        try (InputStream inputStream = Resources.getInputStream("external/project.zip")
                                                .orElseThrow(() -> new RuntimeException("Error reading file project.zip"))) {
            tempPath = Files.createTempFile(TMP_FILE_PREFIX, TMP_FILE_EXTENSION);

            try (OutputStream outputStream = new BufferedOutputStream(Files.newOutputStream(tempPath))) {
                copy(inputStream, outputStream);
            }

            FileInputStream fileInputStream = new FileInputStream(String.valueOf(tempPath));
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            unzip(new ZipInputStream(bufferedInputStream), new File(filePath));
        } catch (IOException e) {
            throw new RuntimeException("Ошибка обработки сообщения", e);
        } finally {
            try {
                if (tempPath != null) {
                    Files.deleteIfExists(tempPath);
                }
            } catch (IOException e1) {
                throw new RuntimeException("Ошибка удаления временного файла", e1);
            }
        }
    }

    private static void copy(InputStream source, OutputStream target) throws IOException {
        byte[] buf = new byte[8192];
        int length;
        while ((length = source.read(buf)) != -1) {
            target.write(buf, 0, length);
        }
    }

    /**
     * Size of the buffer to read/write data
     */
    private static final int BUFFER_SIZE = 4096;

    /**
     * Extracts a zip file specified by the zipFilePath to a directory specified by
     * destinationDirectory (will be created if does not exists)
     *
     * @param zip
     * @param target
     * @throws IOException
     */
    public static void unzip(ZipInputStream zip, File target) throws IOException {
        try (zip) {
            ZipEntry entry;

            while ((entry = zip.getNextEntry()) != null) {
                File file = new File(target, entry.getName());

                if (!file.toPath().normalize().startsWith(target.toPath())) {
                    throw new IOException("Bad zip entry");
                }

                if (entry.isDirectory()) {
                    file.mkdirs();
                    continue;
                }

                byte[] buffer = new byte[BUFFER_SIZE];
                file.getParentFile().mkdirs();
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
                int count;

                while ((count = zip.read(buffer)) != -1) {
                    out.write(buffer, 0, count);
                }

                out.close();
            }
        }
    }

    /**
     * Extracts a zip entry (file entry)
     *
     * @param zipIn
     * @param filePath
     * @throws IOException
     */
    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }
}
