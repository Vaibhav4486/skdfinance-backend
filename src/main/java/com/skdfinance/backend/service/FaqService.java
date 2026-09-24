package com.skdfinance.backend.service;

import com.skdfinance.backend.dto.FaqDTO;
import com.skdfinance.backend.entity.Faq;
import com.skdfinance.backend.repository.FaqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FaqService {

    private final FaqRepository faqRepository;

    public List<FaqDTO> getPublishedFaqs(String category) {
        List<Faq> faqs = StringUtils.hasText(category)
                ? faqRepository.findByCategoryAndPublishedTrueOrderByDisplayOrderAsc(category)
                : faqRepository.findByPublishedTrueOrderByDisplayOrderAsc();

        return faqs.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<FaqDTO> getAllFaqsForAdmin() {
        return faqRepository.findAllByOrderByDisplayOrderAsc()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public FaqDTO createFaq(FaqDTO request) {
        Faq faq = Faq.builder()
                .question(request.getQuestion())
                .answer(request.getAnswer())
                .category(request.getCategory())
                .displayOrder(request.getDisplayOrder())
                .published(request.getPublished() != null && request.getPublished())
                .build();

        Faq saved = faqRepository.save(faq);
        return toDTO(saved);
    }

    public FaqDTO updateFaq(Long id, FaqDTO request) {
        Faq faq = faqRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("FAQ not found: " + id));

        faq.setQuestion(request.getQuestion());
        faq.setAnswer(request.getAnswer());
        faq.setCategory(request.getCategory());
        faq.setDisplayOrder(request.getDisplayOrder());
        if (request.getPublished() != null) {
            faq.setPublished(request.getPublished());
        }

        Faq saved = faqRepository.save(faq);
        return toDTO(saved);
    }

    public FaqDTO setPublished(Long id, boolean published) {
        Faq faq = faqRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("FAQ not found: " + id));

        faq.setPublished(published);
        Faq saved = faqRepository.save(faq);
        return toDTO(saved);
    }

    public void deleteFaq(Long id) {
        if (!faqRepository.existsById(id)) {
            throw new NoSuchElementException("FAQ not found: " + id);
        }
        faqRepository.deleteById(id);
    }

    private FaqDTO toDTO(Faq faq) {
        return FaqDTO.builder()
                .id(faq.getId())
                .question(faq.getQuestion())
                .answer(faq.getAnswer())
                .category(faq.getCategory())
                .displayOrder(faq.getDisplayOrder())
                .published(faq.isPublished())
                .build();
    }
}