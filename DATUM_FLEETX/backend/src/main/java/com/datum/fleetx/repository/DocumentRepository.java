package com.datum.fleetx.repository;

import com.datum.fleetx.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {
    
    List<Document> findByLoadId(UUID loadId);
    
    List<Document> findByLoadIdAndActiveTrue(UUID loadId);
    
    List<Document> findByCompanyId(UUID companyId);
    
    List<Document> findByDocumentType(Document.DocumentType documentType);
}
