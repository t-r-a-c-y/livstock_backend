package com.example.livestock.service.impl;

import com.example.livestock.dto.UserResponse;
import com.example.livestock.enums.AccountStatus;
import com.example.livestock.exception.BadRequestException;
import com.example.livestock.exception.ResourceNotFoundException;
import com.example.livestock.mapper.DtoMapper;
import com.example.livestock.repository.UserRepository;
import com.example.livestock.security.CurrentUserService;
import com.example.livestock.service.AuditLogService;
import com.example.livestock.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final AuditLogService auditLogService;

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> findAll(boolean includeInactive, Pageable pageable) {
        return (includeInactive ? userRepository.findAll(pageable) : userRepository.findByActiveTrue(pageable))
                .map(DtoMapper::toUser);
    }

    @Override
    public UserResponse deactivate(Long id) {
        var actor = currentUserService.getCurrentUser();
        if (actor.getId().equals(id)) {
            throw new BadRequestException("Admin cannot deactivate their own account");
        }
        var user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setActive(false);
        user.setAccountStatus(AccountStatus.DISABLED);
        auditLogService.record("DEACTIVATE_USER", "User", user.getId(), actor, user.getEmail() + " was deactivated");
        return DtoMapper.toUser(user);
    }

    @Override
    public UserResponse activate(Long id) {
        var actor = currentUserService.getCurrentUser();
        var user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setActive(true);
        user.setAccountStatus(AccountStatus.ACTIVE);
        auditLogService.record("ACTIVATE_USER", "User", user.getId(), actor, user.getEmail() + " was activated");
        return DtoMapper.toUser(user);
    }
}
