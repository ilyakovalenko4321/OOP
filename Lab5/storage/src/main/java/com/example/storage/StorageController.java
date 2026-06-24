package com.example.storage;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/**")
public class StorageController {

    private final Path root = Paths.get("server_storage").toAbsolutePath().normalize();

    public StorageController() throws IOException {
        if (!Files.exists(root)) {
            Files.createDirectories(root);
        }
    }

    @PutMapping
    public ResponseEntity<String> upload(HttpServletRequest request) {
        try {
            Path path = getFullPath(request.getRequestURI());
            Files.createDirectories(path.getParent());

            byte[] bytes = request.getInputStream().readAllBytes();

            if (bytes.length == 0) {
                System.out.println("Warning: Received 0 bytes for path: " + path);
            }


            Files.write(path, bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            return ResponseEntity.status(HttpStatus.CREATED).body("File saved: " + request.getRequestURI());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> get(HttpServletRequest request) {
        try {
            Path path = getFullPath(request.getRequestURI());

            if (!Files.exists(path)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Path not found");
            }

            if (Files.isDirectory(path)) {
                try (Stream<Path> stream = Files.list(path)) {
                    List<String> files = stream
                            .map(p -> p.getFileName().toString() + (Files.isDirectory(p) ? "/" : ""))
                            .collect(Collectors.toList());
                    return ResponseEntity.ok(files);
                }
            }

            byte[] data = Files.readAllBytes(path);
            return ResponseEntity.ok().body(data);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @RequestMapping(method = RequestMethod.HEAD)
    public ResponseEntity<?> head(HttpServletRequest request) {
        try {
            Path path = getFullPath(request.getRequestURI());
            if (!Files.exists(path)) return ResponseEntity.notFound().build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(Files.size(path)))
                    .header("Last-Modified", Files.getLastModifiedTime(path).toString())
                    .build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }


    @DeleteMapping
    public ResponseEntity<?> delete(HttpServletRequest request) {
        try {
            Path path = getFullPath(request.getRequestURI());
            if (!Files.exists(path)) return ResponseEntity.notFound().build();


            try (Stream<Path> walk = Files.walk(path)) {
                walk.sorted((p1, p2) -> p2.compareTo(p1))
                        .forEach(p -> {
                            try { Files.delete(p); } catch (IOException ignored) {}
                        });
            }
            return ResponseEntity.noContent().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private Path getFullPath(String uri) {
        String cleanUri = uri.startsWith("/") ? uri.substring(1) : uri;
        return root.resolve(cleanUri).normalize();
    }
}