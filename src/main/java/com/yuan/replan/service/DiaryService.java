package com.yuan.replan.service;

import com.yuan.replan.repository.DiaryRepository;
import com.yuan.replan.entity.Diary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiaryService {

    private final DiaryRepository diaryRepository;

    public DiaryService(DiaryRepository diaryRepository) {
        this.diaryRepository = diaryRepository;
    }

    public Diary saveDiary(String content) {
        Diary diary = new Diary(content);
        return diaryRepository.save(diary);
    }

    public List<Diary> getAllDiaries() {
        return diaryRepository.findAllByOrderByCreatedAtDescIdDesc();
    }

    public void deleteDiary(Long diaryId) {
        if (!diaryRepository.existsById(diaryId)) {
            throw new IllegalArgumentException("해당 일기를 찾을 수 없습니다.");
        }

        diaryRepository.deleteById(diaryId);
    }
}
