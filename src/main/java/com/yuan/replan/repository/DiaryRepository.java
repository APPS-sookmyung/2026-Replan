package com.yuan.replan.repository;

import com.yuan.replan.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

    List<Diary> findAllByOrderByCreatedAtDescIdDesc();
}
