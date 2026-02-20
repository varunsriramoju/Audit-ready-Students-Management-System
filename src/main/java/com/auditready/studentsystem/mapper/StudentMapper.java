package com.auditready.studentsystem.mapper;

import com.auditready.studentsystem.dto.StudentDto;
import com.auditready.studentsystem.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface StudentMapper {
    StudentDto toDto(Student student);

    Student toEntity(StudentDto studentDto);
}
