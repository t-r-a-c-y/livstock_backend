package com.livestock.backend.service;



import com.livestock.backend.dto.response.DashboardStatsResponse;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final AnimalService animalService;
    private final OwnerService ownerService;
    private final FinancialService financialService;
    private final ActivityService activityService;
    private final NotificationService notificationService;

    public DashboardService(AnimalService animalService, OwnerService ownerService, FinancialService financialService, ActivityService activityService, NotificationService notificationService) {
        this.animalService = animalService;
        this.ownerService = ownerService;
        this.financialService = financialService;
        this.activityService = activityService;
        this.notificationService = notificationService;
    }

    public DashboardStatsResponse getDashboardStats() {
        DashboardStatsResponse response = new DashboardStatsResponse();
        response.setAnimals(animalService.getAnimalStats());
        // Set others similarly
        return response;
    }
}