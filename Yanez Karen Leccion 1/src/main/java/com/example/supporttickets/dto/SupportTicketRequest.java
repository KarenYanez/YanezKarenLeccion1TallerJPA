package com.example.supporttickets.dto;

import com.example.supporttickets.model.TicketStatus;
import com.example.supporttickets.model.TicketPriority;
import com.example.supporttickets.model.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SupportTicketRequest {

    @NotBlank(message = "El nombre del solicitante es obligatorio")
    private String requesterName;

    @NotNull(message = "El estado es obligatorio")
    private TicketStatus status;

    @NotNull(message = "La prioridad es obligatoria")
    private TicketPriority priority;

    @NotBlank(message = "La categoría es obligatoria")
    private String category;

    @NotNull(message = "El costo estimado es obligatorio")
    @Positive(message = "El costo estimado debe ser positivo")
    private BigDecimal estimatedCost;

    @NotNull(message = "La moneda es obligatoria")
    private Currency currency;

    @NotNull(message = "La fecha de vencimiento es obligatoria")
    private LocalDate dueDate;

    public SupportTicketRequest() {
    }

    public SupportTicketRequest(String requesterName, TicketStatus status, TicketPriority priority, 
                               String category, BigDecimal estimatedCost, Currency currency, LocalDate dueDate) {
        this.requesterName = requesterName;
        this.status = status;
        this.priority = priority;
        this.category = category;
        this.estimatedCost = estimatedCost;
        this.currency = currency;
        this.dueDate = dueDate;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public TicketPriority getPriority() {
        return priority;
    }

    public void setPriority(TicketPriority priority) {
        this.priority = priority;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(BigDecimal estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}
