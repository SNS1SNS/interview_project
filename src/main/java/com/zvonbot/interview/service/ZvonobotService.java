package com.zvonbot.interview.service;

import com.zvonbot.interview.config.ZvonobotConfig;
import com.zvonbot.interview.dto.zvonobot.ZvonobotCallRequest;
import com.zvonbot.interview.dto.zvonobot.ZvonobotResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Сервис для работы с API Звонобота.
 * Предоставляет методы для отправки голосовых сообщений, SMS и получения информации.
 */
@Service
public class ZvonobotService {
    
    private static final Logger logger = LoggerFactory.getLogger(ZvonobotService.class);
    
    // Константы для улучшения читаемости
    private static final String DUTY_PHONE = "duty";
    private static final String PHONE_CLEANUP_REGEX = "[^0-9]";
    private static final String UNICODE_PATTERN = "\\\\u([0-9a-fA-F]{4})";
    private static final int DEFAULT_GENDER = 0; // Женский голос
    private static final int DEFAULT_IVR_DIGIT = 1;
    private static final int DEFAULT_NEED_BLOCK = 0;
    private static final String MINIMAL_SMS_TEXT = ".";
    private static final Integer PREDEFINED_AUDIO_ID = 247273;
    
    private final WebClient webClient;
    private final ZvonobotConfig config;
    private String cachedOutgoingPhone = null;
    
    @Autowired
    public ZvonobotService(WebClient webClient, ZvonobotConfig config) {
        this.webClient = webClient;
        this.config = config;
    }
    
    /**
     * Декодирует Unicode-последовательности в читаемый текст
     */
    private String decodeUnicode(String text) {
        if (text == null) return null;
        
        Pattern pattern = Pattern.compile(UNICODE_PATTERN);
        Matcher matcher = pattern.matcher(text);
        StringBuffer sb = new StringBuffer();
        
        while (matcher.find()) {
            String unicode = matcher.group(1);
            char ch = (char) Integer.parseInt(unicode, 16);
            matcher.appendReplacement(sb, String.valueOf(ch));
        }
        matcher.appendTail(sb);
        
        return sb.toString();
    }
    
    /**
     * Отправляет голосовое сообщение с автоматическим выбором исходящего номера.
     * 
     * @param phone номер телефона получателя
     * @param text текст для голосового сообщения
     * @return Mono с ответом от API Звонобота
     */
    public Mono<ZvonobotResponse> sendVoiceMessage(String phone, String text) {
        return sendVoiceMessage(phone, text, null, null);
    }
    
    /**
     * Отправляет голосовое сообщение с возможностью указать предварительно смодерированный аудиофайл.
     * 
     * @param phone номер телефона получателя
     * @param text текст для голосового сообщения
     * @param recordId ID предварительно смодерированного аудиофайла
     * @return Mono с ответом от API Звонобота
     */
    public Mono<ZvonobotResponse> sendVoiceMessage(String phone, String text, Integer recordId) {
        return sendVoiceMessage(phone, text, recordId, null);
    }
    
    /**
     * Отправляет голосовое сообщение с полным контролем параметров.
     * 
     * @param phone номер телефона получателя
     * @param text текст для голосового сообщения
     * @param recordId ID предварительно смодерированного аудиофайла
     * @param outgoingPhone исходящий номер телефона
     * @return Mono с ответом от API Звонобота
     */
    public Mono<ZvonobotResponse> sendVoiceMessage(String phone, String text, Integer recordId, String outgoingPhone) {
        // Очищаем номер телефона от лишних символов
        String cleanPhone = phone.replaceAll(PHONE_CLEANUP_REGEX, "");
        
        Mono<String> outgoingPhoneMono = outgoingPhone != null && !outgoingPhone.trim().isEmpty() 
            ? Mono.just(outgoingPhone) 
            : getOutgoingPhone();
        
        return outgoingPhoneMono
                .flatMap(outgoingPhoneValue -> {
                    ZvonobotCallRequest request = new ZvonobotCallRequest();
                    request.setApiKey(config.getApiKey());
                    request.setPhone(cleanPhone);
                    
                    if (DUTY_PHONE.equals(outgoingPhoneValue)) { // Use dutyPhone if no specific outgoingPhone
                        request.setDutyPhone(1);
                    } else {
                        request.setOutgoingPhone(outgoingPhoneValue);
                    }
                    
                    // Если указан recordId, используем предварительно смодерированное аудио
                    if (recordId != null) {
                        ZvonobotCallRequest.Record record = new ZvonobotCallRequest.Record();
                        record.setId(recordId);
                        record.setText(""); // Пустой текст, так как используем предварительно смодерированное аудио
                        record.setGender(DEFAULT_GENDER); // Женский голос
                        request.setRecord(record);
                        logger.info("Используем предварительно смодерированное аудио с recordId: {}", recordId);
                    } else {
                        // Иначе используем генерацию голоса из текста
                        ZvonobotCallRequest.Record record = new ZvonobotCallRequest.Record();
                        record.setText(text);
                        record.setGender(DEFAULT_GENDER); // Женский голос
                        request.setRecord(record);
                        logger.info("Используем генерацию голоса из текста");
                    }
                    
                    logger.info("Отправка голосового сообщения на номер: {} с исходящего номера: {}", cleanPhone, outgoingPhoneValue);
                    logger.info("Запрос к API Звонобота: {}", request);
                    
                    return webClient.post()
                            .uri("/apiCalls/create")
                            .bodyValue(request)
                            .retrieve()
                            .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                                    response -> response.bodyToMono(String.class)
                                            .flatMap(errorBody -> {
                                                String decodedError = decodeUnicode(errorBody);
                                                logger.error("HTTP ошибка {}: {}", response.statusCode(), decodedError);
                                                return Mono.error(new RuntimeException("API ошибка: " + decodedError));
                                            }))
                            .bodyToMono(ZvonobotResponse.class)
                            .doOnSuccess(response -> {
                                logger.info("Ответ от API Звонобота: {}", response);
                                if (response.isSuccess()) {
                                    logger.info("Голосовое сообщение успешно отправлено на номер: {}", cleanPhone);
                                } else {
                                    logger.error("Ошибка отправки голосового сообщения на номер {}: {}", cleanPhone, response.getError());
                                }
                            })
                            .doOnError(error -> {
                                logger.error("Ошибка при отправке голосового сообщения на номер {}: {}", cleanPhone, error.getMessage());
                            });
                });
    }
    
    /**
     * Отправляет SMS сообщение с автоматическим выбором исходящего номера.
     * 
     * @param phone номер телефона получателя
     * @param text текст SMS сообщения
     * @return Mono с ответом от API Звонобота
     */
    public Mono<ZvonobotResponse> sendSms(String phone, String text) {
        return sendSms(phone, text, null);
    }
    
    /**
     * Отправляет SMS сообщение с указанным исходящим номером.
     * 
     * @param phone номер телефона получателя
     * @param text текст SMS сообщения
     * @param outgoingPhone исходящий номер телефона
     * @return Mono с ответом от API Звонобота
     */
    public Mono<ZvonobotResponse> sendSms(String phone, String text, String outgoingPhone) {
        // Очищаем номер телефона от лишних символов
        String cleanPhone = phone.replaceAll(PHONE_CLEANUP_REGEX, "");
        
        Mono<String> outgoingPhoneMono = outgoingPhone != null && !outgoingPhone.trim().isEmpty() 
            ? Mono.just(outgoingPhone) 
            : getOutgoingPhone();
        
        return outgoingPhoneMono
                .flatMap(outgoingPhoneValue -> {
                    // Создаем запрос для отправки SMS через звонок с IVR
                    ZvonobotCallRequest request = new ZvonobotCallRequest();
                    request.setApiKey(config.getApiKey());
                    request.setPhone(cleanPhone);
                    
                    if (DUTY_PHONE.equals(outgoingPhoneValue)) {
                        request.setDutyPhone(1);
                    } else {
                        request.setOutgoingPhone(outgoingPhoneValue);
                    }
                    
                    // Для SMS создаем минимальный record с очень коротким текстом
                    // Короткий текст может не требовать модерации
                    ZvonobotCallRequest.Record record = new ZvonobotCallRequest.Record();
                    record.setText(MINIMAL_SMS_TEXT); // Минимальный текст - одна точка
                    record.setGender(DEFAULT_GENDER); // Женский голос
                    request.setRecord(record);
                    
                    // Добавляем IVR для отправки SMS
                    ZvonobotCallRequest.Ivr ivr = new ZvonobotCallRequest.Ivr();
                    ivr.setDigit(DEFAULT_IVR_DIGIT); // Любая цифра
                    ivr.setSmsText(text); // Текст SMS для отправки
                    ivr.setNeedBlock(DEFAULT_NEED_BLOCK); // Не добавлять в черный список
                    
                    request.setIvrs(new ZvonobotCallRequest.Ivr[]{ivr});
                    
                    logger.info("Отправка SMS на номер: {} с исходящего номера: {}", cleanPhone, outgoingPhoneValue);
                    logger.info("Запрос к API Звонобота: {}", request);
                    
                    return webClient.post()
                            .uri("/apiCalls/create")
                            .bodyValue(request)
                            .retrieve()
                            .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                                    response -> response.bodyToMono(String.class)
                                            .flatMap(errorBody -> {
                                                String decodedError = decodeUnicode(errorBody);
                                                logger.error("HTTP ошибка {}: {}", response.statusCode(), decodedError);
                                                return Mono.error(new RuntimeException("API ошибка: " + decodedError));
                                            }))
                            .bodyToMono(ZvonobotResponse.class)
                            .doOnSuccess(response -> {
                                logger.info("Ответ от API Звонобота: {}", response);
                                if (response.isSuccess()) {
                                    logger.info("SMS успешно отправлено на номер: {}", cleanPhone);
                                } else {
                                    logger.error("Ошибка отправки SMS на номер {}: {}", cleanPhone, response.getError());
                                }
                            })
                            .doOnError(error -> {
                                logger.error("Ошибка при отправке SMS на номер {}: {}", cleanPhone, error.getMessage());
                            });
                });
    }
    
    public Mono<ZvonobotResponse> testApiKey() {
        logger.info("Тестирование API ключа: {}", config.getApiKey().substring(0, Math.min(10, config.getApiKey().length())) + "...");
        
        // Создаем простой запрос для проверки API ключа
        var request = Map.of("apiKey", config.getApiKey());
        
        return webClient.post()
                .uri("/apiCalls/userInfo")
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    String decodedError = decodeUnicode(errorBody);
                                    logger.error("HTTP ошибка при тестировании API ключа {}: {}", response.statusCode(), decodedError);
                                    return Mono.error(new RuntimeException("API ошибка: " + decodedError));
                                }))
                .bodyToMono(ZvonobotResponse.class)
                .doOnSuccess(response -> {
                    logger.info("Ответ от API при тестировании ключа: {}", response);
                })
                .doOnError(error -> {
                    logger.error("Ошибка при тестировании API ключа: {}", error.getMessage());
                });
    }
    
    public Mono<ZvonobotResponse> getOutgoingPhones() {
        logger.info("Получение списка исходящих номеров");
        
        var request = Map.of("apiKey", config.getApiKey());
        
        return webClient.post()
                .uri("/apiCalls/getPhones")
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    String decodedError = decodeUnicode(errorBody);
                                    logger.error("HTTP ошибка при получении номеров {}: {}", response.statusCode(), decodedError);
                                    return Mono.error(new RuntimeException("API ошибка: " + decodedError));
                                }))
                .bodyToMono(ZvonobotResponse.class)
                .doOnSuccess(response -> {
                    logger.info("Список исходящих номеров: {}", response);
                })
                .doOnError(error -> {
                    logger.error("Ошибка при получении исходящих номеров: {}", error.getMessage());
                });
    }
    
    public Mono<ZvonobotResponse> getRecords() {
        logger.info("Получение списка предварительно смодерированных аудиофайлов");
        
        var request = Map.of("apiKey", config.getApiKey());
        
        return webClient.post()
                .uri("/apiCalls/getRecords")
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    String decodedError = decodeUnicode(errorBody);
                                    logger.error("HTTP ошибка при получении аудиофайлов {}: {}", response.statusCode(), decodedError);
                                    return Mono.error(new RuntimeException("API ошибка: " + decodedError));
                                }))
                .bodyToMono(ZvonobotResponse.class)
                .doOnSuccess(response -> {
                    logger.info("Список аудиофайлов: {}", response);
                })
                .doOnError(error -> {
                    logger.error("Ошибка при получении аудиофайлов: {}", error.getMessage());
                });
    }
    
    public Mono<ZvonobotResponse> getUserProfile() {
        logger.info("Получение профиля пользователя");
        
        var request = Map.of("apiKey", config.getApiKey());
        
        return webClient.post()
                .uri("/apiCalls/userInfo")
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    String decodedError = decodeUnicode(errorBody);
                                    logger.error("HTTP ошибка при получении профиля {}: {}", response.statusCode(), decodedError);
                                    return Mono.error(new RuntimeException("API ошибка: " + decodedError));
                                }))
                .bodyToMono(ZvonobotResponse.class)
                .doOnSuccess(response -> {
                    logger.info("Профиль пользователя: {}", response);
                })
                .doOnError(error -> {
                    logger.error("Ошибка при получении профиля: {}", error.getMessage());
                });
    }
    
    private Mono<String> getOutgoingPhone() {
        if (cachedOutgoingPhone != null) {
            return Mono.just(cachedOutgoingPhone);
        }
        
        return getOutgoingPhones()
                .map(response -> {
                    if (response.isSuccess() && response.getData() != null) {
                        // Данные приходят как массив объектов с номерами
                        if (response.getData() instanceof List) {
                            List<?> phones = (List<?>) response.getData();
                            if (!phones.isEmpty()) {
                                // Берем первый номер из списка
                                Object firstPhone = phones.get(0);
                                if (firstPhone instanceof Map) {
                                    Map<String, Object> phoneData = (Map<String, Object>) firstPhone;
                                    if (phoneData.containsKey("phone")) {
                                        cachedOutgoingPhone = phoneData.get("phone").toString();
                                        logger.info("Кэширован исходящий номер: {}", cachedOutgoingPhone);
                                        return cachedOutgoingPhone;
                                    }
                                }
                            }
                        }
                    }
                    // Если не удалось получить номер, используем дежурный
                    cachedOutgoingPhone = DUTY_PHONE;
                    logger.info("Используем дежурный номер");
                    return cachedOutgoingPhone;
                })
                .onErrorReturn(DUTY_PHONE); // В случае ошибки используем дежурный номер
    }
} 