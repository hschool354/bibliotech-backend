package com.example.Bibliotech_backend.service;

import com.example.Bibliotech_backend.dto.CategoryRequest;
import com.example.Bibliotech_backend.dto.DealDTO;
import com.example.Bibliotech_backend.model.Category;
import com.example.Bibliotech_backend.model.Deal;
import com.example.Bibliotech_backend.repository.DealRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DealService {

    @Autowired
    private DealRepository dealRepository;
    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);
    private final IdGeneratorService idGeneratorService;

    public DealService(IdGeneratorService idGeneratorService) {
        this.idGeneratorService = idGeneratorService;
    }

    public List<DealDTO> getActiveDeals() {
        return dealRepository.findByIsActiveTrue().stream().map(deal -> {
            DealDTO dto = new DealDTO();
            dto.setDealId(deal.getDealId());
            dto.setDealName(deal.getDealName());
            dto.setDiscountPercentage(deal.getDiscountPercentage().doubleValue());
            dto.setStartDate(deal.getStartDate());
            dto.setEndDate(deal.getEndDate());
            dto.setIsActive(deal.getIsActive());
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * Thêm mới deal với ID tự tăng
     */
    @Transactional
    public Deal addDeal(Deal dealRequest) {
        logger.debug("Adding new deal: {}", dealRequest.getDealName());

        Deal deal = new Deal();
        int dealID = idGeneratorService.generateDealId();
        deal.setDealId(dealID);

        deal.setDealName(dealRequest.getDealName());
        deal.setDiscountPercentage(dealRequest.getDiscountPercentage());
        deal.setStartDate(dealRequest.getStartDate());
        deal.setEndDate(dealRequest.getEndDate());
        deal.setIsActive(dealRequest.getIsActive());

        return dealRepository.save(deal);
    }


    public Optional<Deal> updateDeal(Integer dealId, Deal dealDetails) {
        return dealRepository.findById(dealId).map(deal -> {
            deal.setDealName(dealDetails.getDealName());
            deal.setDiscountPercentage(dealDetails.getDiscountPercentage());
            deal.setStartDate(dealDetails.getStartDate());
            deal.setEndDate(dealDetails.getEndDate());
            deal.setIsActive(dealDetails.getIsActive());
            return dealRepository.save(deal);
        });
    }
}