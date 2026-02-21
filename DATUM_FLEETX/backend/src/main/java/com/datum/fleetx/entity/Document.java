package com.datum.fleetx.entity;

import com.datum.fleetx.entity.base.BaseEntity;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.util.UUID;

/**
 * Document - Represents uploaded documents (BOL, POD, etc.)
 */
@Entity
@Table(name = "documents")
@Data
@EqualsAndHashCode(callSuper = true)
public class Document extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "load_id")
    private Load load;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_id")
    private StaffMember uploadedBy;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false)
    private DocumentType documentType;
    
    @Column(name = "file_name", nullable = false)
    private String fileName;
    
    @Column(name = "file_path", nullable = false)
    private String filePath;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    @Column(name = "content_type")
    private String contentType;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "uploaded_at", nullable = false)
    private Instant uploadedAt;
    
    @Column(name = "verified")
    private Boolean verified = false;
    
    @Column(name = "verified_by_id")
    private UUID verifiedById;
    
    @Column(name = "verified_at")
    private Instant verifiedAt;
    
    public enum DocumentType {
        BILL_OF_LADING,
        PROOF_OF_DELIVERY,
        RATE_CONTRACT,
        INVOICE,
        RECEIPT,
        LICENSE,
        INSURANCE,
        MAINTENANCE_RECORD,
        ACCIDENT_REPORT,
        HAZMAT_CERT,
        OTHER
    }
}
