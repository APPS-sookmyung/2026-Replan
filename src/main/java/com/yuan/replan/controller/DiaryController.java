package com.yuan.replan.controller;

import com.yuan.replan.entity.Diary;
import com.yuan.replan.service.DiaryService;
import com.yuan.replan.service.GeminiService;
import com.yuan.replan.service.KeywordService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/diary")
public class DiaryController {

    private final DiaryService diaryService;
    private final GeminiService geminiService;
    private final KeywordService keywordService;

    public DiaryController(
            DiaryService diaryService,
            GeminiService geminiService,
            KeywordService keywordService
    ) {
        this.diaryService = diaryService;
        this.geminiService = geminiService;
        this.keywordService = keywordService;
    }
    @PostMapping
    public Diary saveDiary(@RequestParam String content) {
        return diaryService.saveDiary(content);
    }

    @GetMapping
    public List<Diary> getAllDiaries() {
        return diaryService.getAllDiaries();
    }

    @DeleteMapping("/{diaryId}")
    public void deleteDiary(@PathVariable Long diaryId) {
        diaryService.deleteDiary(diaryId);
    }

    @PostMapping("/analyze")
    public String analyzeDiary(@RequestParam String content) {
        String keywords = geminiService.analyzeDiary(content);
        keywordService.saveKeywords(keywords);
        return keywords;
    }

}
