package com.example.livestock.service.impl;

import com.example.livestock.dto.OwnerRequest;
import com.example.livestock.dto.OwnerResponse;
import com.example.livestock.entity.Owner;
import com.example.livestock.entity.User;
import com.example.livestock.enums.AccountStatus;
import com.example.livestock.enums.Role;
import com.example.livestock.exception.BadRequestException;
import com.example.livestock.exception.ResourceNotFoundException;
import com.example.livestock.mapper.DtoMapper;
import com.example.livestock.repository.OwnerRepository;
import com.example.livestock.repository.UserRepository;
import com.example.livestock.security.CurrentUserService;
import com.example.livestock.service.OwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OwnerServiceImpl implements OwnerService {
    private final OwnerRepository ownerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserService currentUserService;

    @Override
    public OwnerResponse create(OwnerRequest request) {
        if (userRepository.existsByEmail(request.user().email())) {
            throw new BadRequestException("Email already exists");
        }
        if (ownerRepository.existsByNationalId(request.nationalId())) {
            throw new BadRequestException("National ID already exists");
        }
        User user = new User();
        user.setFullName(request.user().fullName());
        user.setEmail(request.user().email());
        user.setPhoneNumber(request.user().phoneNumber());
        user.setPassword(passwordEncoder.encode(request.user().password()));
        user.setRole(Role.OWNER);
        user.setAccountStatus(AccountStatus.ACTIVE);

        Owner owner = new Owner();
        owner.setUser(user);
        owner.setNationalId(request.nationalId());
        owner.setAddress(request.address());
        return DtoMapper.toOwner(ownerRepository.save(owner));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OwnerResponse> findAll(Pageable pageable) {
        return ownerRepository.findByActiveTrue(pageable).map(DtoMapper::toOwner);
    }

    @Override
    public OwnerResponse update(Long id, OwnerRequest request) {
        Owner owner = owner(id);
        owner.setNationalId(request.nationalId());
        owner.setAddress(request.address());
        owner.getUser().setFullName(request.user().fullName());
        owner.getUser().setPhoneNumber(request.user().phoneNumber());
        return DtoMapper.toOwner(owner);
    }

    @Override
    public void deactivate(Long id) {
        Owner owner = owner(id);
        owner.setActive(false);
        owner.getUser().setActive(false);
        owner.getUser().setAccountStatus(AccountStatus.DISABLED);
    }

    @Override
    @Transactional(readOnly = true)
    public OwnerResponse currentProfile() {
        User user = currentUserService.getCurrentUser();
        return ownerRepository.findByUserIdAndActiveTrue(user.getId())
                .map(DtoMapper::toOwner)
                .orElseThrow(() -> new ResourceNotFoundException("Owner profile not found"));
    }

    private Owner owner(Long id) {
        return ownerRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));
    }
}
