package com.auditready.studentsystem.repository;

import com.auditready.studentsystem.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByEntityNameAndEntityIdOrderByChangedAtDesc(String entityName, Long entityId);

    List<AuditLog> findAllByOrderByChangedAtDesc();
}
