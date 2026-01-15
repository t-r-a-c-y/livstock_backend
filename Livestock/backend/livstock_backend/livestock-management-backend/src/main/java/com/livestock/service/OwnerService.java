package com.livestock.service;

import com.livestock.dto.request.OwnerRequest;
import com.livestock.entity.Owner;
import java.util.List;
import java.util.UUID;

public interface OwnerService {
    Owner createOwner(OwnerRequest request);
    List<Owner> getAllOwners();
    Owner getOwnerById(UUID id);
    Owner updateOwner(UUID id, OwnerRequest request);
    void deleteOwner(UUID id);
}