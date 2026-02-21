package com.auditready.studentsystem.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String action; // CREATE, UPDATE, DELETE

    @Column(nullable = false)
    private String entityName;

    private Long entityId;

    @Column(nullable = false)
    private String changedBy;

    @Column(nullable = false)
    private LocalDateTime changedAt;

    @Column(columnDefinition = "TEXT")
    private String oldValues;

    @Column(columnDefinition = "TEXT")
    private String newValues;

    @Column(columnDefinition = "TEXT")
    private String diff; // Readable summary of changes
}
