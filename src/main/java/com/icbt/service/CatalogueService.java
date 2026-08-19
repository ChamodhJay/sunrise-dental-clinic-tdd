package com.icbt.service;

import com.icbt.dao.ReferenceDataDAO;
import com.icbt.model.ClinicFeeSchedule;
import com.icbt.model.StaffRole;
import com.icbt.model.StaffUser;
import com.icbt.model.TreatmentType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public final class CatalogueService {
    private final ReferenceDataDAO referenceDataDAO = new ReferenceDataDAO();

    public List<TreatmentType> treatments(StaffUser user) {
        requireManager(user);
        return referenceDataDAO.findTreatmentTypes(false);
    }

    public ClinicFeeSchedule activeFee(StaffUser user) {
        requireManager(user);
        return referenceDataDAO.findActiveFeeSchedule()
                .orElseThrow(() -> new BusinessRuleException("No active consultation fee is configured."));
    }

    public void saveTreatment(StaffUser user, String id, String name, String price, boolean active) {
        requireManager(user);
        if (name == null || name.isBlank() || name.length() > 100) {
            throw new BusinessRuleException("Treatment name is required and must not exceed 100 characters.");
        }
        BigDecimal amount = parseMoney(price, "Enter a non-negative treatment price.");
        UUID treatmentId = id == null || id.isBlank() ? UUID.randomUUID() : parseUuid(id);
        boolean duplicateName = referenceDataDAO.findTreatmentTypes(false).stream()
                .anyMatch(existing -> existing.getName().equalsIgnoreCase(name.trim())
                        && !existing.getTreatmentTypeId().equals(treatmentId));
        if (duplicateName) {
            throw new BusinessRuleException("Another treatment already uses that name.");
        }
        referenceDataDAO.saveTreatment(treatmentId, name.trim(), amount, active, user);
    }

    public ClinicFeeSchedule changeConsultationFee(StaffUser user, String fee) {
        requireManager(user);
        return referenceDataDAO.replaceActiveFee(
                parseMoney(fee, "Enter a non-negative consultation fee."), LocalDate.now(), user);
    }

    private BigDecimal parseMoney(String value, String errorMessage) {
        try {
            BigDecimal amount = new BigDecimal(value).setScale(2);
            if (amount.signum() < 0) {
                throw new NumberFormatException();
            }
            return amount;
        } catch (RuntimeException exception) {
            throw new BusinessRuleException(errorMessage);
        }
    }

    private UUID parseUuid(String id) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException exception) {
            throw new BusinessRuleException("Invalid treatment identifier.");
        }
    }

    private void requireManager(StaffUser user) {
        if (user == null || !user.hasRole(StaffRole.CLINIC_MANAGER)) {
            throw new SecurityException("Only the clinic manager can maintain catalogue and fee data.");
        }
    }
}
