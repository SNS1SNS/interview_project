package com.zvonbot.interview.controller;

import com.zvonbot.interview.dto.ApiResponse;
import com.zvonbot.interview.dto.MessageRequest;
import com.zvonbot.interview.dto.zvonobot.ZvonobotResponse;
import com.zvonbot.interview.service.ZvonobotService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class MessageController {
    
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
    
    private final ZvonobotService zvonobotService;
    
    @Autowired
    public MessageController(ZvonobotService zvonobotService) {
        this.zvonobotService = zvonobotService;
    }
    
    @PostMapping("/send-voice")
    public Mono<ResponseEntity<ApiResponse>> sendVoiceMessage(@Valid @RequestBody MessageRequest request) {
        logger.info("Получен запрос на отправку голосового сообщения: {}", request);
        
        return zvonobotService.sendVoiceMessage(request.getCleanPhone(), request.getText(), request.getRecordId(), request.getOutgoingPhone())
                .map(this::handleZvonobotResponse)
                .onErrorResume(error -> {
                    logger.error("Ошибка при отправке голосового сообщения: {}", error.getMessage());
                    return Mono.just(ResponseEntity.ok(ApiResponse.error("Ошибка при отправке голосового сообщения: " + error.getMessage())));
                });
    }
    
    @PostMapping("/send-sms")
    public Mono<ResponseEntity<ApiResponse>> sendSms(@Valid @RequestBody MessageRequest request) {
        logger.info("Получен запрос на отправку SMS: {}", request);
        
        return zvonobotService.sendSms(request.getCleanPhone(), request.getText(), request.getOutgoingPhone())
                .map(this::handleZvonobotResponse)
                .onErrorResume(error -> {
                    logger.error("Ошибка при отправке SMS: {}", error.getMessage());
                    return Mono.just(ResponseEntity.ok(ApiResponse.error("Ошибка при отправке SMS: " + error.getMessage())));
                });
    }
    
    @GetMapping("/test-api-key")
    public Mono<ResponseEntity<ApiResponse>> testApiKey() {
        logger.info("Тестирование API ключа");
        
        return zvonobotService.testApiKey()
                .map(this::handleZvonobotResponse)
                .onErrorResume(error -> {
                    logger.error("Ошибка при тестировании API ключа: {}", error.getMessage());
                    return Mono.just(ResponseEntity.ok(ApiResponse.error("Ошибка при тестировании API ключа: " + error.getMessage())));
                });
    }
    
    @GetMapping("/get-phones")
    public Mono<ResponseEntity<ApiResponse>> getOutgoingPhones() {
        logger.info("Получение списка исходящих номеров");
        
        return zvonobotService.getOutgoingPhones()
                .map(this::handleZvonobotResponse)
                .onErrorResume(error -> {
                    logger.error("Ошибка при получении номеров: {}", error.getMessage());
                    return Mono.just(ResponseEntity.ok(ApiResponse.error("Ошибка при получении номеров: " + error.getMessage())));
                });
    }
    
    @GetMapping("/get-records")
    public Mono<ResponseEntity<ApiResponse>> getRecords() {
        logger.info("Получение списка предварительно смодерированных аудиофайлов");
        
        return zvonobotService.getRecords()
                .map(this::handleZvonobotResponse)
                .onErrorResume(error -> {
                    logger.error("Ошибка при получении аудиофайлов: {}", error.getMessage());
                    return Mono.just(ResponseEntity.ok(ApiResponse.error("Ошибка при получении аудиофайлов: " + error.getMessage())));
                });
    }
    
    @GetMapping("/get-profile")
    public Mono<ResponseEntity<ApiResponse>> getUserProfile() {
        logger.info("Получение профиля пользователя");
        
        return zvonobotService.getUserProfile()
                .map(this::handleZvonobotResponse)
                .onErrorResume(error -> {
                    logger.error("Ошибка при получении профиля: {}", error.getMessage());
                    return Mono.just(ResponseEntity.ok(ApiResponse.error("Ошибка при получении профиля: " + error.getMessage())));
                });
    }
    
    private ResponseEntity<ApiResponse> handleZvonobotResponse(ZvonobotResponse zvonobotResponse) {
        if (zvonobotResponse.isSuccess()) {
            // Если это тест API ключа и есть данные пользователя
            if (zvonobotResponse.getData() != null && zvonobotResponse.getData() instanceof Map) {
                Map<String, Object> data = (Map<String, Object>) zvonobotResponse.getData();
                if (data.containsKey("email") && data.containsKey("balance")) {
                    String email = (String) data.get("email");
                    Object balance = data.get("balance");
                    return ResponseEntity.ok(ApiResponse.success(
                        String.format("API ключ работает! Email: %s, Баланс: %s", email, balance),
                        zvonobotResponse.getData()
                    ));
                }
            }
            return ResponseEntity.ok(ApiResponse.success("Операция выполнена успешно", zvonobotResponse.getData()));
        } else {
            String errorMessage = zvonobotResponse.getError();
            if (errorMessage == null || errorMessage.isEmpty()) {
                errorMessage = "Неизвестная ошибка API";
            }
            return ResponseEntity.ok(ApiResponse.error(errorMessage));
        }
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleException(Exception e) {
        logger.error("Неожиданная ошибка: {}", e.getMessage(), e);
        return ResponseEntity.ok(ApiResponse.error("Внутренняя ошибка сервера: " + e.getMessage()));
    }
} 