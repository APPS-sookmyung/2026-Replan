package com.yuan.replan.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;


    // 일기 분석 → 상태 키워드 추출
    public String analyzeDiary(String diary) {

        Client client = Client.builder()
                .apiKey(apiKey)
                .build();

        String prompt =
                "다음 일기를 분석하여 사용자의 향후 Todo를 세분화하고 "
                        + "개인 맞춤형 상세 계획을 세우는 데 실제로 영향을 줄 수 있는 "
                        + "상태 또는 행동 특성을 최대 5개 추출해라.\n"
                        + "계획의 작업량, 소요시간, 난이도, 휴식, 순서 조정에 활용할 수 있는 정보만 선택해라.\n"
                        + "'프로젝트', '공부', '과제'처럼 단순히 일기의 주제를 나타내는 단어는 제외해라.\n"
                        + "집중도, 피로도, 진행 속도, 미루는 경향, 컨디션, 시간 활용 습관 등을 우선해라.\n"
                        + "키워드만 쉼표로 구분해서 출력해라.\n\n"
                        + "일기:\n" + diary;

        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-3.6-flash",
                        prompt,
                        null
                );

        return response.text();
    }


    // Todo + 사용자 상태 → 개인화 계획 생성
    public String generatePlan(
            String title,
            LocalDateTime deadline,
            String keywords) {

        Client client = Client.builder()
                .apiKey(apiKey)
                .build();

        LocalDateTime now = LocalDateTime.now();

        if (!deadline.isAfter(now)) {
            return "마감 시간이 이미 지났습니다.";
        }

        Duration remaining = Duration.between(now, deadline);
        long remainingHours = remaining.toHours();

        String prompt =
                "사용자의 Todo와 현재 상태를 바탕으로 개인 맞춤형 세부 계획을 작성해줘.\n\n"
                        + "Todo: " + title + "\n"
                        + "현재 시각: " + now + "\n"
                        + "마감 시각: " + deadline + "\n"
                        + "남은 시간: 약 " + remainingHours + "시간\n"
                        + "사용자 상태 키워드: " + keywords + "\n\n"

                        + "요구사항:\n"
                        + "1. Todo를 마감 전까지 완료할 수 있도록 현실적인 세부 작업으로 나눠줘.\n"
                        + "2. 사용자가 제공하지 않은 시험 범위, 과제 범위 등의 구체적인 내용을 임의로 만들어내지 마.\n"
                        + "3. 사용자의 현재 상태를 작업량과 휴식 배치에 반영해줘.\n"
                        + "4. 마감 직전에 일이 몰리지 않도록 분산해줘.\n"
                        + "5. 전체 계획을 이렇게 구성한 이유를 reason에 2~3문장으로 작성해줘.\n"
                        + "6. 반드시 JSON 형식으로만 응답해줘.\n"
                        + "7. JSON은 reason과 items를 포함해야 해.\n"
                        + "8. items의 각 항목에는 content, startTime, endTime, durationMinutes를 포함해줘.\n"
                        + "9. startTime과 endTime은 현재 시각과 마감 시각을 기준으로 실제 계획 시각을 정해줘.\n"
                        + "10. startTime과 endTime은 ISO 8601 형식으로 작성해줘.";

        GenerateContentConfig config =
                GenerateContentConfig.builder()
                        .responseMimeType("application/json")
                        .build();

        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-3.6-flash",
                        prompt,
                        config
                );

        return response.text();
    }
}

