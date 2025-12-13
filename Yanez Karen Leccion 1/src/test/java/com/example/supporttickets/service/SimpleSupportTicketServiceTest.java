package com.example.supporttickets.service;

import com.example.supporttickets.dto.SupportTicketRequest;
import com.example.supporttickets.dto.SupportTicketResponse;
import com.example.supporttickets.exception.InvalidFilterException;
import com.example.supporttickets.model.SupportTicket;
import com.example.supporttickets.model.TicketStatus;
import com.example.supporttickets.model.TicketPriority;
import com.example.supporttickets.model.Currency;
import com.example.supporttickets.repository.SupportTicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SimpleSupportTicketServiceTest {

    @Mock
    private SupportTicketRepository supportTicketRepository;

    @InjectMocks
    private SupportTicketService supportTicketService;

    private SupportTicketRequest sampleRequest;

    @BeforeEach
    void setUp() {
        sampleRequest = new SupportTicketRequest(
                "Juan Pérez", TicketStatus.OPEN, TicketPriority.HIGH,
                "NETWORK", new BigDecimal("150.50"), Currency.USD,
                LocalDate.of(2025, 12, 31)
        );
    }

    @Test
    void createTicket_ShouldReturnTicketWithGeneratedNumber() {
        SupportTicket savedTicket = new SupportTicket();
        savedTicket.setId(1L);
        savedTicket.setTicketNumber("ST-2025-000001");
        savedTicket.setRequesterName(sampleRequest.getRequesterName());
        savedTicket.setStatus(sampleRequest.getStatus());
        savedTicket.setPriority(sampleRequest.getPriority());
        savedTicket.setCategory(sampleRequest.getCategory());
        savedTicket.setEstimatedCost(sampleRequest.getEstimatedCost());
        savedTicket.setCurrency(sampleRequest.getCurrency());
        savedTicket.setDueDate(sampleRequest.getDueDate());
        savedTicket.setCreatedAt(LocalDateTime.now());

        when(supportTicketRepository.existsByTicketNumber(anyString())).thenReturn(false);
        when(supportTicketRepository.save(any(SupportTicket.class))).thenReturn(savedTicket);

        SupportTicketResponse result = supportTicketService.createTicket(sampleRequest);

        assertNotNull(result);
        assertEquals("ST-2025-000001", result.getTicketNumber());
        assertEquals("Juan Pérez", result.getRequesterName());
        assertEquals(TicketStatus.OPEN, result.getStatus());
        verify(supportTicketRepository).save(any(SupportTicket.class));
    }

    @Test
    void findTicketsWithFilters_WithValidFilters_ShouldReturnFilteredPage() {
        List<SupportTicket> tickets = Arrays.asList(createSampleTicket());
        Page<SupportTicket> ticketPage = new PageImpl<>(tickets);

        when(supportTicketRepository.findWithFilters(
                anyString(), any(), any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(ticketPage);

        Page<SupportTicketResponse> result = supportTicketService.findTicketsWithFilters(
                "juan", "OPEN", "USD", "50", "300", 
                "2025-01-01T00:00:00", "2025-12-31T23:59:59",
                0, 20, "createdAt,desc");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Juan Pérez", result.getContent().get(0).getRequesterName());
    }

    @Test
    void findTicketsWithFilters_WithInvalidStatus_ShouldThrowException() {
        InvalidFilterException exception = assertThrows(InvalidFilterException.class, () ->
                supportTicketService.findTicketsWithFilters(
                        null, "INVALID_STATUS", null, null, null, null, null, 0, 20, "createdAt,desc"));

        assertTrue(exception.getMessage().contains("Estado inválido"));
    }

    @Test
    void findTicketsWithFilters_WithInvalidCurrency_ShouldThrowException() {
        InvalidFilterException exception = assertThrows(InvalidFilterException.class, () ->
                supportTicketService.findTicketsWithFilters(
                        null, null, "INVALID_CURRENCY", null, null, null, null, 0, 20, "createdAt,desc"));

        assertTrue(exception.getMessage().contains("Moneda inválida"));
    }

    @Test
    void findTicketsWithFilters_WithNegativeCost_ShouldThrowException() {
        InvalidFilterException exception = assertThrows(InvalidFilterException.class, () ->
                supportTicketService.findTicketsWithFilters(
                        null, null, null, "-50", null, null, null, 0, 20, "createdAt,desc"));

        assertTrue(exception.getMessage().contains("minCost debe ser mayor o igual a 0"));
    }

    @Test
    void findTicketsWithFilters_WithInvalidDateRange_ShouldThrowException() {
        InvalidFilterException exception = assertThrows(InvalidFilterException.class, () ->
                supportTicketService.findTicketsWithFilters(
                        null, null, null, null, null, 
                        "2025-12-31T23:59:59", "2025-01-01T00:00:00", 0, 20, "createdAt,desc"));

        assertTrue(exception.getMessage().contains("La fecha 'from' debe ser anterior"));
    }

    @Test
    void findTicketsWithFilters_WithInvalidSortField_ShouldThrowException() {
        InvalidFilterException exception = assertThrows(InvalidFilterException.class, () ->
                supportTicketService.findTicketsWithFilters(
                        null, null, null, null, null, null, null, 0, 20, "invalidField,desc"));

        assertTrue(exception.getMessage().contains("Campo de ordenamiento inválido"));
    }

    @Test
    void findTicketsWithFilters_WithMinCostGreaterThanMaxCost_ShouldThrowException() {
        InvalidFilterException exception = assertThrows(InvalidFilterException.class, () ->
                supportTicketService.findTicketsWithFilters(
                        null, null, null, "300", "150", null, null, 0, 20, "createdAt,desc"));

        assertTrue(exception.getMessage().contains("minCost debe ser menor o igual a maxCost"));
    }

    private SupportTicket createSampleTicket() {
        SupportTicket ticket = new SupportTicket();
        ticket.setId(1L);
        ticket.setTicketNumber("ST-2025-000001");
        ticket.setRequesterName("Juan Pérez");
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setPriority(TicketPriority.HIGH);
        ticket.setCategory("NETWORK");
        ticket.setEstimatedCost(new BigDecimal("150.50"));
        ticket.setCurrency(Currency.USD);
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setDueDate(LocalDate.of(2025, 12, 31));
        return ticket;
    }
}
