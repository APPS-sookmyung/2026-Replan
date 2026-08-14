package com.yuan.replan.service;

import com.yuan.replan.entity.Keyword;
import com.yuan.replan.repository.KeywordRepository;
import org.springframework.stereotype.Service;

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
}