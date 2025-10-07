package com.livestock.backend.dto.response;

import lombok.Data;

import java.util.Map;

@Data
public class DashboardStatsResponse {
    private Map<String, Integer> animals;
    private Map<String, Integer> owners;
    private Map<String, Double> financials;
    private Map<String, Integer> activities;
    private Map<String, Integer> notifications;
}