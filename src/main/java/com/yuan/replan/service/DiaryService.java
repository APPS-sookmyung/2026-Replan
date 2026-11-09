package com.yuan.replan.service;

import com.yuan.replan.repository.DiaryRepository;
import com.yuan.replan.entity.Diary;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class DiaryService {
    private final CurrentUser currentUser;

    private final DiaryRepository diaryRepository;

    public DiaryService(DiaryRepository diaryRepository, CurrentUser currentUser) {
        this.currentUser = currentUser;
        this.diaryRepository = diaryRepository;
    }

    public Diary saveDiary(String content) {
        Diary diary = new Diary(content);
        diary.setUser(currentUser.get());
        return diaryRepository.save(diary);
    }

    public List<Diary> getAllDiaries() {
        return diaryRepository.findAllByUserIdOrderByCreatedAtDescIdDesc(currentUser.id());
    }

    public void deleteDiary(Long diaryId) {
        Diary diary = diaryRepository.findByIdAndUserId(diaryId, currentUser.id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        diaryRepository.delete(diary);
    }
}
