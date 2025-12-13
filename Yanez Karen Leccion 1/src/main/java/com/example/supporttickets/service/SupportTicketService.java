package com.example.supporttickets.service;

import com.example.supporttickets.dto.SupportTicketRequest;
import com.example.supporttickets.dto.SupportTicketResponse;
import com.example.supporttickets.exception.InvalidFilterException;
import com.example.supporttickets.model.SupportTicket;
import com.example.supporttickets.model.TicketStatus;
import com.example.supporttickets.model.Currency;
import com.example.supporttickets.repository.SupportTicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
@Transactional
public class SupportTicketService {

    private final SupportTicketRepository supportTicketRepository;
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Autowired
    public SupportTicketService(SupportTicketRepository supportTicketRepository) {
        this.supportTicketRepository = supportTicketRepository;
    }

    public SupportTicketResponse createTicket(SupportTicketRequest request) {
        String ticketNumber = generateTicketNumber();
        
        SupportTicket ticket = new SupportTicket();
        ticket.setTicketNumber(ticketNumber);
        ticket.setRequesterName(request.getRequesterName());
        ticket.setStatus(request.getStatus());
        ticket.setPriority(request.getPriority());
        ticket.setCategory(request.getCategory());
        ticket.setEstimatedCost(request.getEstimatedCost());
        ticket.setCurrency(request.getCurrency());
        ticket.setDueDate(request.getDueDate());
        
        SupportTicket savedTicket = supportTicketRepository.save(ticket);
        return convertToResponse(savedTicket);
    }

    public Page<SupportTicketResponse> findTicketsWithFilters(
            String q, String status, String currency, 
            String minCost, String maxCost, String from, String to,
            int page, int size, String sort) {

        TicketStatus statusEnum = parseStatus(status);
        Currency currencyEnum = parseCurrency(currency);
        BigDecimal minCostDecimal = parseBigDecimal(minCost, "minCost");
        BigDecimal maxCostDecimal = parseBigDecimal(maxCost, "maxCost");
        LocalDateTime fromDateTime = parseDateTime(from, "from");
        LocalDateTime toDateTime = parseDateTime(to, "to");

        validateDateRange(fromDateTime, toDateTime);
        validateCostRange(minCostDecimal, maxCostDecimal);

        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        
        Page<SupportTicket> tickets = supportTicketRepository.findWithFilters(
                q, statusEnum, currencyEnum, minCostDecimal, maxCostDecimal, 
                fromDateTime, toDateTime, pageable);
        
        return tickets.map(this::convertToResponse);
    }

    private String generateTicketNumber() {
        String prefix = "ST-2025-";
        int counter = 1;
        String ticketNumber;
        
        do {
            ticketNumber = String.format("%s%06d", prefix, counter);
            counter++;
        } while (supportTicketRepository.existsByTicketNumber(ticketNumber));
        
        return ticketNumber;
    }

    private TicketStatus parseStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return null;
        }
        try {
            return TicketStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidFilterException("Estado inválido. Valores permitidos: OPEN, IN_PROGRESS, RESOLVED, CLOSED, CANCELLED");
        }
    }

    private Currency parseCurrency(String currency) {
        if (currency == null || currency.trim().isEmpty()) {
            return null;
        }
        try {
            return Currency.valueOf(currency.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidFilterException("Moneda inválida. Valores permitidos: USD, EUR");
        }
    }

    private BigDecimal parseBigDecimal(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            BigDecimal decimal = new BigDecimal(value);
            if (decimal.compareTo(BigDecimal.ZERO) < 0) {
                throw new InvalidFilterException(fieldName + " debe ser mayor o igual a 0");
            }
            return decimal;
        } catch (NumberFormatException e) {
            throw new InvalidFilterException(fieldName + " debe ser un número válido");
        }
    }

    private LocalDateTime parseDateTime(String dateTime, String fieldName) {
        if (dateTime == null || dateTime.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTime, ISO_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new InvalidFilterException(fieldName + " debe tener formato ISO-8601: yyyy-MM-dd'T'HH:mm:ss");
        }
    }

    private void validateDateRange(LocalDateTime from, LocalDateTime to) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new InvalidFilterException("La fecha 'from' debe ser anterior o igual a la fecha 'to'");
        }
    }

    private void validateCostRange(BigDecimal minCost, BigDecimal maxCost) {
        if (minCost != null && maxCost != null && minCost.compareTo(maxCost) > 0) {
            throw new InvalidFilterException("minCost debe ser menor o igual a maxCost");
        }
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.trim().isEmpty()) {
            return Sort.by("createdAt").descending();
        }
        
        String[] parts = sort.split(",");
        String field = parts[0].trim();
        Sort.Direction direction = parts.length > 1 && parts[1].trim().equalsIgnoreCase("desc") 
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        
        validateSortField(field);
        return Sort.by(direction, field);
    }

    private void validateSortField(String field) {
        switch (field) {
            case "id":
            case "ticketNumber":
            case "requesterName":
            case "status":
            case "priority":
            case "category":
            case "estimatedCost":
            case "currency":
            case "createdAt":
            case "dueDate":
                break;
            default:
                throw new InvalidFilterException("Campo de ordenamiento inválido. Campos permitidos: id, ticketNumber, requesterName, status, priority, category, estimatedCost, currency, createdAt, dueDate");
        }
    }

    private SupportTicketResponse convertToResponse(SupportTicket ticket) {
        return new SupportTicketResponse(
                ticket.getId(),
                ticket.getTicketNumber(),
                ticket.getRequesterName(),
                ticket.getStatus(),
                ticket.getPriority(),
                ticket.getCategory(),
                ticket.getEstimatedCost(),
                ticket.getCurrency(),
                ticket.getCreatedAt(),
                ticket.getDueDate()
        );
    }
}
