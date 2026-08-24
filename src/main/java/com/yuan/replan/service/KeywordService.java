package com.yuan.replan.service;

import com.yuan.replan.entity.Keyword;
import com.yuan.replan.repository.KeywordRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KeywordService {

    private final KeywordRepository keywordRepository;

    public KeywordService(KeywordRepository keywordRepository) {
        this.keywordRepository = keywordRepository;
    }

    public void saveKeywords(String keywords) {

        String[] keywordArray = keywords.split(",");

        for (String keyword : keywordArray) {
            Keyword newKeyword = new Keyword(keyword.trim());
            keywordRepository.save(newKeyword);
        }
    }

    public List<Keyword> getAllKeywords() {
        return keywordRepository.findAll();
    }

    public String getKeywordsAsString() {
        List<Keyword> keywords = keywordRepository.findAll();
        return keywords.stream()
                .map(Keyword::getName)
                .collect(Collectors.joining(", "));
        // 테이블 형태로 만들어진 키워드를 하나의 문자열로 합쳐줌
    }

    public String getWeightedKeywordsAsString() {

        List<Keyword> keywords = keywordRepository.findAll();

        return keywords.stream()
                .map(keyword -> {
                    double weight = calculateTimeWeight(keyword.getCreatedAt());

                    if (weight == 0.0) {
                        return null;
                    }

                    return keyword.getName() + " (가중치: " + weight + ")";
                })
                .filter(keyword -> keyword != null)
                .collect(Collectors.joining(", "));
    }

    private double calculateTimeWeight(LocalDateTime createdAt) {

        long days = Duration.between(
                createdAt,
                LocalDateTime.now()
        ).toDays();

        if (days <= 3) {
            return 1.0;
        }

        if (days <= 7) {
            return 0.6;
        }

        if (days <= 14) {
            return 0.3;
        }

        return 0.0;
    }
}