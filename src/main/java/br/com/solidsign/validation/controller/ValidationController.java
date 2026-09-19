package br.com.solidsign.validation.controller;

import br.com.solidsign.validation.model.ValidationReportsResponseDTO;
import br.com.solidsign.validation.service.ValidationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Exposes two validation endpoints:
 *   POST /api/pdf/validate/batch — reads files from the server-side input directory
 *   POST /api/pdf/validate/form  — receives PDF files via multipart form upload
 */
@RestController
@RequestMapping("/api/pdf/validate")
public class ValidationController {

    private final ValidationService service;

    public ValidationController(ValidationService service) {
        this.service = service;
    }

    /**
     * Validates all PDF files found in the configured {@code solidsign.batch.input-path}.
     *
     * Example:
     *   curl -X POST http://localhost:8094/api/pdf/validate/batch
     */
    @PostMapping("/batch")
    public ResponseEntity<ValidationReportsResponseDTO> validateBatch() throws IOException {
        return ResponseEntity.ok(service.validateBatch());
    }

    /**
     * Validates PDF files sent as multipart form data. CORS-enabled for direct use from a
     * browser front-end. {@code authorization}/{@code baseUrl} are optional per-request
     * overrides of the configured {@code solidsign.api.*} properties.
     *
     * Example:
     *   curl -X POST http://localhost:8094/api/pdf/validate/form \
     *        -F "document=@/path/to/signed.pdf" \
     *        -F "document=@/path/to/other.pdf"
     */
    @CrossOrigin
    @PostMapping("/form")
    public ResponseEntity<ValidationReportsResponseDTO> validateForm(
            @RequestPart("document") List<MultipartFile> files,
            @RequestPart(value = "authorization", required = false) String authorization,
            @RequestPart(value = "baseUrl", required = false) String baseUrl) throws IOException {
        return ResponseEntity.ok(service.validateForm(files, authorization, baseUrl));
    }
}
