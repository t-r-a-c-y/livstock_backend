package com.livestock.backend.service;

import com.livestock.backend.dto.request.ReportGenerateRequest;
import com.livestock.backend.dto.response.ReportResponse;
import com.livestock.backend.model.Report;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReportMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "generatedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "data", ignore = true)
    Report toEntity(ReportGenerateRequest request);

    @Mapping(target = "generatedBy", expression = "java(report.getGeneratedBy() != null ? report.getGeneratedBy().getName() : null)")
    @Mapping(target = "data", expression = "java(parseData(report.getData()))")
    ReportResponse toResponse(Report report);

    default java.util.Map<String, Object> parseData(String data) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().readValue(data, java.util.Map.class);
        } catch (Exception e) {
            return new java.util.HashMap<>();
        }
    }
}