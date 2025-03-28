package me.davidgarmo.soundseeker.product.web.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@WebServlet("/api/v1/upload")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 10, maxRequestSize = 1024 * 1024 * 50)
public class FileUploadServlet extends HttpServlet {
    private static final String UPLOAD_DIR = "uploads";
    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(
            Arrays.asList("jpg", "jpeg", "png", "webp", "gif"));
    private static final Set<String> ALLOWED_MIME_TYPES = new HashSet<>(
            Arrays.asList(
                    "image/jpeg",
                    "image/png",
                    "image/webp",
                    "image/gif"));

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
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.setContentType("application/json");
                    response.getWriter().print("{\"success\": false, \"error\": \"Invalid file extension. Allowed: "
                            + String.join(", ", ALLOWED_EXTENSIONS) + "\"}");
                    return;
                }

                String contentType = part.getContentType();
                if (!isValidMimeType(contentType)) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.setContentType("application/json");
                    response.getWriter().print("{\"success\": false, \"error\": \"File type not permitted: " + contentType + "\"}");
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
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json");
                response.getWriter().print("{\"success\": false, \"error\": \"No file uploaded.\"}");
                return;
            }

            String fileUrl = request.getContextPath() + "/uploads/" + fileName;

            response.setContentType("application/json");
            response.getWriter().print("{\"success\": true, \"filePath\": \"" + fileUrl
                    + "\", \"fileName\": \"" + fileName
                    + "\", \"originalFileName\": \"" + originalFileName + "\"}");

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().print("{\"success\": false, \"error\": \"" + e.getMessage() + "\"}");
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
}