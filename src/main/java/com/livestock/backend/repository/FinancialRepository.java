package com.livestock.backend.repository;


import com.livestock.backend.model.FinancialRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FinancialRepository extends JpaRepository<FinancialRecord, UUID>, JpaSpecificationExecutor<FinancialRecord> {
}