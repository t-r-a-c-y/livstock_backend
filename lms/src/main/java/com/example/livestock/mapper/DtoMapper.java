package com.example.livestock.mapper;

import com.example.livestock.dto.*;
import com.example.livestock.entity.*;

public final class DtoMapper {
    private DtoMapper() {
    }

    public static UserResponse toUser(User user) {
        return new UserResponse(user.getId(), user.getFullName(), user.getEmail(), user.getPhoneNumber(),
                user.getRole(), user.getAccountStatus(), user.isActive());
    }

    public static OwnerResponse toOwner(Owner owner) {
        return new OwnerResponse(owner.getId(), toUser(owner.getUser()), owner.getNationalId(), owner.getAddress(), owner.isActive());
    }

    public static AnimalResponse toAnimal(Animal animal) {
        return new AnimalResponse(animal.getId(), animal.getOwner().getId(), animal.getOwner().getUser().getFullName(),
                animal.getTagNumber(), animal.getAnimalType(), animal.getBreed(), animal.getGender(),
                animal.getDateOfBirth(), animal.getColor(), animal.getWeight(), animal.getAnimalStatus(), animal.isActive());
    }

    public static BreedingRecordResponse toBreeding(BreedingRecord record) {
        return new BreedingRecordResponse(record.getId(), record.getCow().getId(), record.getCow().getTagNumber(),
                record.getCow().getOwner().getId(), record.getMatingDate(), record.getMaleAnimalUsed(),
                record.getPregnancyStatus(), record.getExpectedBirthDate(), record.getActualBirthDate(), record.getNotes(),
                record.isActive());
    }

    public static HealthRecordResponse toHealth(HealthRecord record) {
        return new HealthRecordResponse(record.getId(), record.getAnimal().getId(), record.getAnimal().getTagNumber(),
                record.getDiagnosis(), record.getTreatment(), record.getMedication(), record.getVeterinarianName(),
                record.getVisitDate(), record.getNextVisitDate(), record.getNotes(), record.isActive());
    }

    public static VaccinationRecordResponse toVaccination(VaccinationRecord record) {
        return new VaccinationRecordResponse(record.getId(), record.getAnimal().getId(), record.getAnimal().getTagNumber(),
                record.getVaccineName(), record.getVaccinationDate(), record.getNextDueDate(), record.getAdministeredBy(),
                record.getNotes(), record.isActive());
    }

    public static MessageResponse toMessage(Message message) {
        return new MessageResponse(message.getId(), message.getSender().getId(), message.getSender().getFullName(),
                message.getReceiver().getId(), message.getReceiver().getFullName(),
                message.getAnimal() == null ? null : message.getAnimal().getId(),
                message.getAnimal() == null ? null : message.getAnimal().getTagNumber(),
                message.getSubject(), message.getMessageBody(), message.getMessageStatus(), message.getCreatedAt());
    }

    public static NotificationResponse toNotification(Notification notification) {
        return new NotificationResponse(notification.getId(), notification.getTitle(), notification.getMessage(),
                notification.getNotificationType(), notification.isReadStatus(), notification.getCreatedAt());
    }

    public static ReportLogResponse toReportLog(ReportLog log) {
        return new ReportLogResponse(log.getId(), log.getReportName(), log.getReportType(), log.getExportFormat(),
                log.getGeneratedBy().getFullName(), log.getGeneratedAt(), log.getFilePath());
    }
}
