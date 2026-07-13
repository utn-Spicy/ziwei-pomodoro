package com.ziwei.pomodoro.service;

import com.ziwei.pomodoro.config.DeepSeekConfig;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.logging.Logger;

@Slf4j
@Service
public class SuggestionService {

    @Autowired
    private DeepSeekConfig deepSeekConfig;

    @Autowired
    private RestTemplate restTemplate;

    public String generateSuggestion(String personalityType, String element, Integer status) {
        String tone;
        switch (element) {
            case "木": tone = "语气鼓励生长，耐心而温和"; break;
            case "火": tone = "语气温和坚定，持续发光"; break;
            case "土": tone = "语气稳重踏实，不疾不徐"; break;
            case "金": tone = "语气简洁有力，直指核心"; break;
            case "水": tone = "语气灵动柔韧，顺势而为"; break;
            default: tone = "语气冷峻但有温度";
        }

        String style;
        if (status == 1) {
            style = "给一句肯定赞许的评语，认可他这次的专注成果。语气正向肯定。";
        } else if (status == 2) {
            style = "给一句温和鼓励的劝慰，告诉他中断不代表失败。语气温暖宽慰。";
        } else {
            style = "给一句提醒放松的短句，告诉他休息好才能继续前行。语气舒缓平静。";
        }

        String prompt = "你是一位紫微斗数命理导师。用户是【" + personalityType + "】性格，五行属" + element + "。" +
                tone + "请用30-50字给一句贴合命理的短句。" + style + "不要用标点符号。";

        Map<String, Object> requestBody = Map.of(
                "model", "deepseek-chat",
                "messages", new Object[]{ Map.of("role", "user", "content", prompt) },
                "max_tokens", 80
        );

        try {
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("Authorization", "Bearer " + deepSeekConfig.getApiKey());
            headers.set("Content-Type", "application/json");

            org.springframework.http.HttpEntity<Map<String, Object>> entity =
                    new org.springframework.http.HttpEntity<>(requestBody, headers);

            Map response = restTemplate.postForObject(
                    "https://api.deepseek.com/chat/completions",
                    entity,
                    Map.class
            );

            var choices = (java.util.List<Map>) response.get("choices");
            if (choices != null && !choices.isEmpty()) {
                var message = (Map) choices.get(0).get("message");
                return ((String) message.get("content")).trim();
            }
        } catch (Exception e) {
            log.error("调用DeepSeek API失败", e);
        }
        return element + "命前行 自有天时";
    }

}
