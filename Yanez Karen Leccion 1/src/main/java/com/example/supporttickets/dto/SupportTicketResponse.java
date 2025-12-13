package com.example.supporttickets.dto;

import com.example.supporttickets.model.TicketStatus;
import com.example.supporttickets.model.TicketPriority;
import com.example.supporttickets.model.Currency;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class SupportTicketResponse {

    private Long id;
    private String ticketNumber;
    private String requesterName;
    private TicketStatus status;
    private TicketPriority priority;
    private String category;
    private BigDecimal estimatedCost;
    private Currency currency;
    private LocalDateTime createdAt;
    private LocalDate dueDate;

    public SupportTicketResponse() {
    }

    public SupportTicketResponse(Long id, String ticketNumber, String requesterName, TicketStatus status, 
                                TicketPriority priority, String category, BigDecimal estimatedCost, 
                                Currency currency, LocalDateTime createdAt, LocalDate dueDate) {
        this.id = id;
        this.ticketNumber = ticketNumber;
        this.requesterName = requesterName;
        this.status = status;
        this.priority = priority;
        this.category = category;
        this.estimatedCost = estimatedCost;
        this.currency = currency;
        this.createdAt = createdAt;
        this.dueDate = dueDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}
