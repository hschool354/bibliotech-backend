package com.example.Bibliotech_backend.controller;

import com.example.Bibliotech_backend.dto.DealDTO;
import com.example.Bibliotech_backend.model.Deal;
import com.example.Bibliotech_backend.service.DealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/deals")
public class DealController {

    @Autowired
    private DealService dealService;

    @GetMapping
    public List<DealDTO> getActiveDeals() {
        return dealService.getActiveDeals();
    }

    @PostMapping
    public ResponseEntity<Deal> createDeal(@RequestBody Deal deal) {
        Deal createdDeal = dealService.addDeal(deal);
        return new ResponseEntity<>(createdDeal, HttpStatus.CREATED);
    }

    @PutMapping("/{dealId}")
    public ResponseEntity<Deal> updateDeal(@PathVariable Integer dealId, @RequestBody Deal dealDetails) {
        Optional<Deal> updatedDeal = dealService.updateDeal(dealId, dealDetails);
        return updatedDeal.map(deal -> new ResponseEntity<>(deal, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
