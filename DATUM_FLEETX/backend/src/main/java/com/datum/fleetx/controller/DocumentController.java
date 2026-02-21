package com.datum.fleetx.controller;

import com.datum.fleetx.dto.ApiResponse;
import com.datum.fleetx.entity.Document;
import com.datum.fleetx.entity.Load;
import com.datum.fleetx.repository.DocumentRepository;
import com.datum.fleetx.repository.LoadRepository;
import com.datum.fleetx.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Document Controller - Handle POD (Proof of Delivery) and BOL (Bill of Lading) uploads
 */
@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentRepository documentRepository;
    private final LoadRepository loadRepository;

    /**
     * Upload a document for a load (POD, BOL, etc.)
     */
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<Document>> uploadDocument(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("file") MultipartFile file,
            @RequestParam("loadId") UUID loadId,
            @RequestParam("documentType") Document.DocumentType documentType,
            @RequestParam(value = "description", required = false) String description) throws IOException {
        
        // Verify load belongs to user's company
        Load load = loadRepository.findById(loadId)
            .filter(l -> l.getCompany().getId().equals(userDetails.getCompanyId()))
            .orElseThrow(() -> new RuntimeException("Load not found"));
        
        // Save document
        Document document = new Document();
        document.setDocumentType(documentType);
        document.setFileName(file.getOriginalFilename());
        document.setContentType(file.getContentType());
        document.setFileSize(file.getSize());
        document.setFilePath("/uploads/" + file.getOriginalFilename());
        document.setDescription(description);
        document.setLoad(load);
        
        document = documentRepository.save(document);
        
        return ResponseEntity.ok(ApiResponse.success("Document uploaded successfully", document));
    }

    /**
     * Get all documents for a load
     */
    @GetMapping("/load/{loadId}")
    public ResponseEntity<ApiResponse<List<Document>>> getDocumentsByLoad(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID loadId) {
        
        // Verify load belongs to user's company
        Load load = loadRepository.findById(loadId)
            .filter(l -> l.getCompany().getId().equals(userDetails.getCompanyId()))
            .orElseThrow(() -> new RuntimeException("Load not found"));
        
        List<Document> documents = documentRepository.findByLoadId(loadId);
        return ResponseEntity.ok(ApiResponse.success(documents));
    }

    /**
     * Get document by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Document>> getDocument(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id) {
        
        Document document = documentRepository.findById(id)
            .filter(d -> d.getLoad().getCompany().getId().equals(userDetails.getCompanyId()))
            .orElseThrow(() -> new RuntimeException("Document not found"));
        
        return ResponseEntity.ok(ApiResponse.success(document));
    }

    /**
     * Download document
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<String> downloadDocument(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id) {
        
        Document document = documentRepository.findById(id)
            .filter(d -> d.getLoad().getCompany().getId().equals(userDetails.getCompanyId()))
            .orElseThrow(() -> new RuntimeException("Document not found"));
        
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getFileName() + "\"")
            .contentType(MediaType.parseMediaType(document.getContentType()))
            .body(document.getFilePath());
    }

    /**
     * Get document as base64 (for preview)
     */
    @GetMapping("/{id}/preview")
    public ResponseEntity<ApiResponse<Map<String, String>>> previewDocument(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id) {
        
        Document document = documentRepository.findById(id)
            .filter(d -> d.getLoad().getCompany().getId().equals(userDetails.getCompanyId()))
            .orElseThrow(() -> new RuntimeException("Document not found"));
        
        String base64 = Base64.getEncoder().encodeToString(
            document.getFilePath() != null ? document.getFilePath().getBytes() : new byte[0]);
        String mimeType = document.getContentType() != null ? document.getContentType() : "application/octet-stream";
        String dataUrl = "data:" + mimeType + ";base64," + base64;
        
        Map<String, String> preview = new HashMap<>();
        preview.put("fileName", document.getFileName());
        preview.put("fileType", document.getContentType());
        preview.put("dataUrl", dataUrl);
        
        return ResponseEntity.ok(ApiResponse.success(preview));
    }

    /**
     * Delete a document
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id) {
        
        Document document = documentRepository.findById(id)
            .filter(d -> d.getLoad().getCompany().getId().equals(userDetails.getCompanyId()))
            .orElseThrow(() -> new RuntimeException("Document not found"));
        
        document.setActive(false);
        documentRepository.save(document);
        
        return ResponseEntity.ok(ApiResponse.success("Document deleted successfully", null));
    }

    /**
     * Upload Proof of Delivery (POD)
     */
    @PostMapping("/pod")
    public ResponseEntity<ApiResponse<Document>> uploadPOD(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("file") MultipartFile file,
            @RequestParam("loadId") UUID loadId) throws IOException {
        
        return uploadDocument(userDetails, file, loadId, 
            Document.DocumentType.PROOF_OF_DELIVERY, "Proof of Delivery");
    }

    /**
     * Upload Bill of Lading (BOL)
     */
    @PostMapping("/bol")
    public ResponseEntity<ApiResponse<Document>> uploadBOL(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("file") MultipartFile file,
            @RequestParam("loadId") UUID loadId) throws IOException {
        
        return uploadDocument(userDetails, file, loadId, 
            Document.DocumentType.BILL_OF_LADING, "Bill of Lading");
    }
}
