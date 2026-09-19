package br.com.solidsign.validation.service;

import br.com.solidsign.validation.model.ValidationReportsResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class ValidationService {

    private static final Logger log = LoggerFactory.getLogger(ValidationService.class);

    private final RestTemplate restTemplate;

    @Value("${solidsign.api.base-url}")
    private String baseUrl;

    @Value("${solidsign.api.authorization}")
    private String authorization;

    @Value("${solidsign.batch.input-path}")
    private String batchInputPath;

    public ValidationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Validates a batch of PDF files from the configured input directory.
     * @return validation report for all files
     */
    public ValidationReportsResponseDTO validateBatch() throws IOException {
        File dir = new File(batchInputPath);
        File[] pdfs = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".pdf"));
        if (pdfs == null || pdfs.length == 0) {
            throw new IllegalArgumentException("No PDF files found in: " + batchInputPath);
        }

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        for (int i = 0; i < pdfs.length; i++) {
            body.add("signedFile[" + i + "]", new FileSystemResource(pdfs[i]));
        }
        log.info("Validating {} PDF file(s) from {}", pdfs.length, batchInputPath);
        return callApi(body, null, null);
    }

    /**
     * Validates PDF files received via multipart form upload.
     * @param files uploaded PDF files
     * @param authorizationOverride optional per-request Bearer token (falls back to solidsign.api.authorization)
     * @param baseUrlOverride optional per-request API base URL (falls back to solidsign.api.base-url)
     * @return validation report
     */
    public ValidationReportsResponseDTO validateForm(List<MultipartFile> files, String authorizationOverride, String baseUrlOverride) throws IOException {
        // The real SolidSign API reads indexed multipart fields (signedFile[0], signedFile[1], ...),
        // not a plain repeated "document" field — that field name silently produced empty uploads.
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        for (int i = 0; i < files.size(); i++) {
            MultipartFile mf = files.get(i);
            Path tmp = Files.createTempFile("solidsign-pdf-", ".pdf");
            mf.transferTo(tmp);
            tmp.toFile().deleteOnExit();
            String originalName = mf.getOriginalFilename();
            body.add("signedFile[" + i + "]", new FileSystemResource(tmp.toFile()) {
                @Override public String getFilename() { return originalName; }
            });
        }
        log.info("Validating {} uploaded PDF file(s)", files.size());
        return callApi(body, authorizationOverride, baseUrlOverride);
    }

    private ValidationReportsResponseDTO callApi(MultiValueMap<String, Object> body, String authorizationOverride, String baseUrlOverride) {
        String auth = (authorizationOverride != null && !authorizationOverride.isBlank()) ? authorizationOverride : authorization;
        String effectiveBaseUrl = (baseUrlOverride != null && !baseUrlOverride.isBlank()) ? baseUrlOverride : baseUrl;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set(HttpHeaders.AUTHORIZATION, auth.startsWith("Bearer ") ? auth : "Bearer " + auth);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
        String url = effectiveBaseUrl.replaceAll("/+$", "") + "/solidsign/dsig/validation/verify-pdf";

        ResponseEntity<ValidationReportsResponseDTO> response =
            restTemplate.exchange(url, HttpMethod.POST, request, ValidationReportsResponseDTO.class);

        return response.getBody();
    }
}
