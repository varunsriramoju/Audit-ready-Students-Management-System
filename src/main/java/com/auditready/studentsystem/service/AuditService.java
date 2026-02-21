package com.auditready.studentsystem.service;

import com.auditready.studentsystem.entity.AuditLog;
import java.util.List;

public interface AuditService {
    void logCreate(String entityName, Long entityId, Object newEntity);

    void logUpdate(String entityName, Long entityId, Object oldEntity, Object newEntity);

    void logDelete(String entityName, Long entityId, Object oldEntity);

    List<AuditLog> getAllLogs();

    List<AuditLog> getStudentLogs(Long studentId);
}
