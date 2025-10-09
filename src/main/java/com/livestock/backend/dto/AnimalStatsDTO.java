package com.livestock.backend.dto;

import lombok.Data;

@Data
public class AnimalStatsDTO {
    private long totalAnimals;
    private long cows;
    private long calves;
    private long goats;
    private long kids;
    private long healthy;
    private long sick;
    private long sold;
    private long dead;

    public AnimalStatsDTO(long totalAnimals, long cows, long calves, long goats, long kids, long healthy, long sick, long sold, long dead) {
        this.totalAnimals = totalAnimals;
        this.cows = cows;
        this.calves = calves;
        this.goats = goats;
        this.kids = kids;
        this.healthy = healthy;
        this.sick = sick;
        this.sold = sold;
        this.dead = dead;
    }
}