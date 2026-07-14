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
        for (File pdf : pdfs) {
            body.add("document", new FileSystemResource(pdf));
        }
        log.info("Validating {} PDF file(s) from {}", pdfs.length, batchInputPath);
        return callApi(body);
    }

    /**
     * Validates PDF files received via multipart form upload.
     * @param files uploaded PDF files
     * @return validation report
     */
    public ValidationReportsResponseDTO validateForm(List<MultipartFile> files) throws IOException {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        for (MultipartFile mf : files) {
            Path tmp = Files.createTempFile("solidsign-pdf-", ".pdf");
            mf.transferTo(tmp);
            tmp.toFile().deleteOnExit();
            body.add("document", new FileSystemResource(tmp.toFile()) {
                @Override public String getFilename() { return mf.getOriginalFilename(); }
            });
        }
        log.info("Validating {} uploaded PDF file(s)", files.size());
        return callApi(body);
    }

    private ValidationReportsResponseDTO callApi(MultiValueMap<String, Object> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set(HttpHeaders.AUTHORIZATION, authorization);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
        String url = baseUrl.replaceAll("/+$", "") + "/solidsign/dsig/validation/verify-pdf";

        ResponseEntity<ValidationReportsResponseDTO> response =
            restTemplate.exchange(url, HttpMethod.POST, request, ValidationReportsResponseDTO.class);

        return response.getBody();
    }
}
