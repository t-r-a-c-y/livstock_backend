package com.livestock.backend.service;

import com.livestock.backend.model.UserProfile;
import com.livestock.backend.repository.AnimalRepository;
import com.livestock.backend.repository.FinancialRecordRepository;
import com.livestock.backend.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class DashboardService {

    private final AnimalRepository animalRepository;
    private final FinancialRecordRepository financialRecordRepository;
    private final NotificationRepository notificationRepository;
    private final AuthService authService;

    public DashboardService(AnimalRepository animalRepository, FinancialRecordRepository financialRecordRepository,
                            NotificationRepository notificationRepository, AuthService authService) {
        this.animalRepository = animalRepository;
        this.financialRecordRepository = financialRecordRepository;
        this.notificationRepository = notificationRepository;
        this.authService = authService;
    }

    public Map<String, Integer> getDashboardStats() {
        UserProfile currentUser = authService.getCurrentUser();
        UUID ownerId = currentUser.getOwner() != null ? currentUser.getOwner().getId() : null;

        Map<String, Integer> stats = new HashMap<>();
        if (ownerId != null) {
            stats.put("totalAnimals", (int) animalRepository.countByOwnerId(ownerId));
            stats.put("totalFinancialRecords", (int) financialRecordRepository.countByOwnerId(ownerId));
            stats.put("unreadNotifications", (int) notificationRepository.countByUserIdAndIsReadFalse(currentUser.getId()));
        } else {
            stats.put("totalAnimals", 0);
            stats.put("totalFinancialRecords", 0);
            stats.put("unreadNotifications", (int) notificationRepository.countByUserIdAndIsReadFalse(currentUser.getId()));
        }
        return stats;
    }
}