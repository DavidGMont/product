package me.davidgarmo.soundseeker.product.web.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@WebServlet("/api/v1/upload")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 10, maxRequestSize = 1024 * 1024 * 50)
public class FileUploadServlet extends HttpServlet {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String UPLOAD_DIR = "uploads";
    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(
            Arrays.asList("gif", "jpg", "jpeg", "png", "webp"));
    private static final Set<String> ALLOWED_MIME_TYPES = new HashSet<>(
            Arrays.asList(
                    "image/gif",
                    "image/jpeg",
                    "image/png",
                    "image/webp"));
    private static final Map<String, byte[][]> FILE_SIGNATURES = new HashMap<>();

    static {
        FILE_SIGNATURES.put("gif", new byte[][]{
                {(byte) 0x47, (byte) 0x49, (byte) 0x46, (byte) 0x38, (byte) 0x37, (byte) 0x61},
                {(byte) 0x47, (byte) 0x49, (byte) 0x46, (byte) 0x38, (byte) 0x39, (byte) 0x61}
        });

        FILE_SIGNATURES.put("jpg", new byte[][]{
                {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0},
                {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE1},
                {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE2},
                {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE3},
                {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xEE},
                {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xDB}
        });

        FILE_SIGNATURES.put("jpeg", FILE_SIGNATURES.get("jpg"));

        FILE_SIGNATURES.put("png", new byte[][]{
                {(byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47}}
        );

        FILE_SIGNATURES.put("webp", new byte[][]{
                {(byte) 0x52, (byte) 0x49, (byte) 0x46, (byte) 0x46, 0, 0, 0, 0, (byte) 0x57, (byte) 0x45, (byte) 0x42, (byte) 0x50}
        });
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String realPath = request.getServletContext().getRealPath("/");
        File uploadDir = new File(realPath, UPLOAD_DIR);

        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String fileName = "";
        String originalFileName = "";

        try {
            for (Part part : request.getParts()) {
                if (part.getSubmittedFileName() == null || part.getSubmittedFileName().isEmpty()) {
                    continue;
                }

                originalFileName = getFileName(part);
                String extension = getFileExtension(originalFileName);

                if (!isValidExtension(extension)) {
                    sendErrorResponse(response, "File type not permitted. Allowed types: " + String.join(", ", ALLOWED_EXTENSIONS) + ".");
                    return;
                }

                String contentType = part.getContentType();
                if (!isValidMimeType(contentType)) {
                    sendErrorResponse(response, "File type not permitted. Invalid MIME type: " + contentType + ".");
                    return;
                }

                long timestamp = System.currentTimeMillis();
                fileName = timestamp + "." + extension;

                try (InputStream inputStream = part.getInputStream();
                     FileOutputStream outputStream = new FileOutputStream(new File(uploadDir, fileName))) {
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
            }

            if (fileName.isEmpty()) {
                sendErrorResponse(response, "No file uploaded.");
                return;
            }

            String fileUrl = request.getContextPath() + "/uploads/" + fileName;

            response.setContentType("application/json");
            response.getWriter().print("{\"success\": true, \"filePath\": \"" + fileUrl
                    + "\", \"fileName\": \"" + fileName
                    + "\", \"originalFileName\": \"" + originalFileName + "\"}");

        } catch (Exception e) {
            LOGGER.fatal("Error uploading file: {}", e.getMessage());
            sendErrorResponse(response, "An error occurred while uploading the file: " + e.getMessage());
        }
    }

    private String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        for (String content : contentDisposition.split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return "unknown";
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    private boolean isValidExtension(String extension) {
        return ALLOWED_EXTENSIONS.contains(extension.toLowerCase());
    }

    private boolean isValidMimeType(String mimeType) {
        if (mimeType == null) {
            return false;
        }
        return ALLOWED_MIME_TYPES.contains(mimeType.toLowerCase());
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json");
        response.getWriter().print("{\"success\": false, \"error\": \"" + message + "\"}");
    }
}