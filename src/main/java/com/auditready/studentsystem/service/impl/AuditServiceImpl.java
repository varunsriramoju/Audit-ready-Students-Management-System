package com.auditready.studentsystem.service.impl;

import com.auditready.studentsystem.entity.AuditLog;
import com.auditready.studentsystem.repository.AuditLogRepository;
import com.auditready.studentsystem.service.AuditService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void logCreate(String entityName, Long entityId, Object newEntity) {
        saveLog("CREATE", entityName, entityId, null, serialize(newEntity), "Created new " + entityName);
    }

    @Override
    @Transactional
    public void logUpdate(String entityName, Long entityId, Object oldEntity, Object newEntity) {
        String diff = generateDiff(oldEntity, newEntity);
        if (!diff.isEmpty()) {
            saveLog("UPDATE", entityName, entityId, serialize(oldEntity), serialize(newEntity), diff);
        }
    }

    @Override
    @Transactional
    public void logDelete(String entityName, Long entityId, Object oldEntity) {
        saveLog("DELETE", entityName, entityId, serialize(oldEntity), null, "Deleted " + entityName);
    }

    @Override
    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAllByOrderByChangedAtDesc();
    }

    @Override
    public List<AuditLog> getStudentLogs(Long studentId) {
        return auditLogRepository.findByEntityNameAndEntityIdOrderByChangedAtDesc("STUDENT", studentId);
    }

    private void saveLog(String action, String entityName, Long entityId, String oldVal, String newVal, String diff) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getName()
                : "SYSTEM";

        AuditLog auditLog = AuditLog.builder()
                .action(action)
                .entityName(entityName)
                .entityId(entityId)
                .changedBy(currentUser)
                .changedAt(LocalDateTime.now())
                .oldValues(oldVal)
                .newValues(newVal)
                .diff(diff)
                .build();

        auditLogRepository.save(auditLog);
    }

    private String serialize(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("Failed to serialize object for auditing", e);
            return "Error serializing";
        }
    }

    private String generateDiff(Object oldObj, Object newObj) {
        StringBuilder diff = new StringBuilder();
        Field[] fields = oldObj.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object oldVal = field.get(oldObj);
                Object newVal = field.get(newObj);

                if (oldVal == null && newVal == null)
                    continue;
                if (oldVal != null && oldVal.equals(newVal))
                    continue;

                if (diff.length() > 0)
                    diff.append(", ");
                diff.append(field.getName()).append(": [").append(oldVal).append("] -> [").append(newVal).append("]");
            } catch (Exception e) {
                log.error("Error comparing fields for diff", e);
            }
        }
        return diff.toString();
    }
}
