package com.example.supporttickets.controller;

import com.example.supporttickets.dto.SupportTicketRequest;
import com.example.supporttickets.dto.SupportTicketResponse;
import com.example.supporttickets.model.TicketStatus;
import com.example.supporttickets.model.TicketPriority;
import com.example.supporttickets.model.Currency;
import com.example.supporttickets.service.SupportTicketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SupportTicketController.class)
class SupportTicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SupportTicketService supportTicketService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createTicket_ShouldReturnCreatedTicket() throws Exception {
        SupportTicketRequest request = new SupportTicketRequest(
                "Juan Pérez", TicketStatus.OPEN, TicketPriority.HIGH,
                "NETWORK", new BigDecimal("150.50"), Currency.USD,
                LocalDate.of(2025, 12, 31)
        );

        SupportTicketResponse response = new SupportTicketResponse(
                1L, "ST-2025-000001", "Juan Pérez", TicketStatus.OPEN,
                TicketPriority.HIGH, "NETWORK", new BigDecimal("150.50"),
                Currency.USD, LocalDateTime.now(), LocalDate.of(2025, 12, 31)
        );

        when(supportTicketService.createTicket(any(SupportTicketRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/support-tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.ticketNumber").value("ST-2025-000001"))
                .andExpect(jsonPath("$.requesterName").value("Juan Pérez"))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.priority").value("HIGH"));
    }

    @Test
    void getTickets_ShouldReturnPageOfTickets() throws Exception {
        List<SupportTicketResponse> tickets = Arrays.asList(
                new SupportTicketResponse(1L, "ST-2025-000001", "Juan Pérez", TicketStatus.OPEN,
                        TicketPriority.HIGH, "NETWORK", new BigDecimal("150.50"),
                        Currency.USD, LocalDateTime.now(), LocalDate.of(2025, 12, 31)),
                new SupportTicketResponse(2L, "ST-2025-000002", "María García", TicketStatus.IN_PROGRESS,
                        TicketPriority.MEDIUM, "SOFTWARE", new BigDecimal("200.00"),
                        Currency.EUR, LocalDateTime.now(), LocalDate.of(2025, 12, 25))
        );

        Page<SupportTicketResponse> page = new PageImpl<>(tickets);

        when(supportTicketService.findTicketsWithFilters(
                anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyInt(), anyInt(), anyString()))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/support-tickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void getTickets_WithFilters_ShouldReturnFilteredResults() throws Exception {
        List<SupportTicketResponse> tickets = Arrays.asList(
                new SupportTicketResponse(1L, "ST-2025-000001", "Juan Pérez", TicketStatus.OPEN,
                        TicketPriority.HIGH, "NETWORK", new BigDecimal("150.50"),
                        Currency.USD, LocalDateTime.now(), LocalDate.of(2025, 12, 31))
        );

        Page<SupportTicketResponse> page = new PageImpl<>(tickets);

        when(supportTicketService.findTicketsWithFilters(
                eq("juan"), eq("OPEN"), eq("USD"), eq("50"), eq("300"),
                anyString(), anyString(), eq(0), eq(10), eq("createdAt,desc")))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/support-tickets")
                        .param("q", "juan")
                        .param("status", "OPEN")
                        .param("currency", "USD")
                        .param("minCost", "50")
                        .param("maxCost", "300")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "createdAt,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].status").value("OPEN"))
                .andExpect(jsonPath("$.content[0].currency").value("USD"));
    }
}
