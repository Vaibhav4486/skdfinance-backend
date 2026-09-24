package com.skdfinance.backend.service;

import com.skdfinance.backend.dto.ServiceOfferingDTO;
import com.skdfinance.backend.entity.ServiceOffering;
import com.skdfinance.backend.repository.ServiceOfferingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceOfferingService {

    private final ServiceOfferingRepository serviceOfferingRepository;

    public List<ServiceOfferingDTO> getAllServices() {
        return serviceOfferingRepository.findAllByOrderByDisplayOrderAsc()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ServiceOfferingDTO getServiceBySlug(String slug) {
        ServiceOffering serviceOffering = serviceOfferingRepository.findBySlug(slug)
                .orElseThrow(() -> new NoSuchElementException("Service not found: " + slug));
        return toDTO(serviceOffering);
    }

    public ServiceOfferingDTO createServiceOffering(ServiceOfferingDTO request) {
        // shape (blank/null) is already covered by @NotBlank/@NotNull on the DTO via @Valid;
        // this is the business rule the annotations can't express
        serviceOfferingRepository.findBySlug(request.getSlug())
                .ifPresent(existing -> {
                    throw new IllegalStateException("A service with this slug already exists");
                });

        ServiceOffering serviceOffering = ServiceOffering.builder()
                .name(request.getName())
                .slug(request.getSlug())
                .shortDescription(request.getShortDescription())
                .startingRate(request.getStartingRate())
                .maxAmount(request.getMaxAmount())
                .maxTenure(request.getMaxTenure())
                .iconKey(request.getIconKey())
                .displayOrder(request.getDisplayOrder())
                .build();

        ServiceOffering saved = serviceOfferingRepository.save(serviceOffering);
        return toDTO(saved);
    }

    public ServiceOfferingDTO updateServiceOffering(Long id, ServiceOfferingDTO request) {
        ServiceOffering existing = serviceOfferingRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Service not found: " + id));

        if (!existing.getSlug().equals(request.getSlug())) {
            serviceOfferingRepository.findBySlug(request.getSlug())
                    .ifPresent(other -> {
                        if (!other.getId().equals(id)) {
                            throw new IllegalStateException("A service with this slug already exists");
                        }
                    });
        }

        existing.setName(request.getName());
        existing.setSlug(request.getSlug());
        existing.setShortDescription(request.getShortDescription());
        existing.setStartingRate(request.getStartingRate());
        existing.setMaxAmount(request.getMaxAmount());
        existing.setMaxTenure(request.getMaxTenure());
        existing.setIconKey(request.getIconKey());
        existing.setDisplayOrder(request.getDisplayOrder());

        ServiceOffering saved = serviceOfferingRepository.save(existing);
        return toDTO(saved);
    }

    public void deleteServiceOffering(Long id) {
        if (!serviceOfferingRepository.existsById(id)) {
            throw new NoSuchElementException("Service not found: " + id);
        }
        serviceOfferingRepository.deleteById(id);
    }

    private ServiceOfferingDTO toDTO(ServiceOffering serviceOffering) {
        return ServiceOfferingDTO.builder()
                .id(serviceOffering.getId())
                .name(serviceOffering.getName())
                .slug(serviceOffering.getSlug())
                .shortDescription(serviceOffering.getShortDescription())
                .startingRate(serviceOffering.getStartingRate())
                .maxAmount(serviceOffering.getMaxAmount())
                .maxTenure(serviceOffering.getMaxTenure())
                .iconKey(serviceOffering.getIconKey())
                .displayOrder(serviceOffering.getDisplayOrder())
                .build();
    }
}
