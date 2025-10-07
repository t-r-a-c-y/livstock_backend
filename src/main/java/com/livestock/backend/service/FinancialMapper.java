package com.livestock.backend.service;

import com.livestock.backend.dto.request.FinancialRequest;
import com.livestock.backend.dto.response.FinancialResponse;
import com.livestock.backend.model.Animal;
import com.livestock.backend.model.FinancialRecord;
import com.livestock.backend.model.Owner;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FinancialMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "animal", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "receiptImage", ignore = true)
    FinancialRecord toEntity(FinancialRequest request);

    FinancialResponse toResponse(FinancialRecord record);

    default FinancialResponse.OwnerSummary mapOwnerSummary(Owner owner) {
        if (owner == null) return null;
        FinancialResponse.OwnerSummary summary = new FinancialResponse.OwnerSummary();
        summary.setId(owner.getId());
        summary.setName(owner.getName());
        return summary;
    }

    default FinancialResponse.AnimalSummary mapAnimalSummary(Animal animal) {
        if (animal == null) return null;
        FinancialResponse.AnimalSummary summary = new FinancialResponse.AnimalSummary();
        summary.setId(animal.getId());
        summary.setTagId(animal.getTagId());
        return summary;
    }
}