package com.example.supporttickets.controller;

import com.example.supporttickets.dto.SupportTicketRequest;
import com.example.supporttickets.dto.SupportTicketResponse;
import com.example.supporttickets.service.SupportTicketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/support-tickets")
@CrossOrigin(origins = "*")
public class SupportTicketController {

    private final SupportTicketService supportTicketService;

    @Autowired
    public SupportTicketController(SupportTicketService supportTicketService) {
        this.supportTicketService = supportTicketService;
    }

    @PostMapping
    public ResponseEntity<SupportTicketResponse> createTicket(@Valid @RequestBody SupportTicketRequest request) {
        SupportTicketResponse response = supportTicketService.createTicket(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<SupportTicketResponse>> getTickets(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) String minCost,
            @RequestParam(required = false) String maxCost,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        Page<SupportTicketResponse> tickets = supportTicketService.findTicketsWithFilters(
                q, status, currency, minCost, maxCost, from, to, page, size, sort);
        
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupportTicketResponse> getTicketById(@PathVariable Long id) {
        SupportTicketResponse ticket = supportTicketService.findTicketById(id);
        return ResponseEntity.ok(ticket);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupportTicketResponse> updateTicket(
            @PathVariable Long id, 
            @Valid @RequestBody SupportTicketRequest request) {
        SupportTicketResponse updatedTicket = supportTicketService.updateTicket(id, request);
        return ResponseEntity.ok(updatedTicket);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SupportTicketResponse> partialUpdateTicket(
            @PathVariable Long id, 
            @RequestBody SupportTicketRequest request) {
        SupportTicketResponse updatedTicket = supportTicketService.partialUpdateTicket(id, request);
        return ResponseEntity.ok(updatedTicket);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        supportTicketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }
}
