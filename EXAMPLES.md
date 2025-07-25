# 📚 Примеры использования API

## 🎤 Отправка голосовых сообщений

### Базовый пример
```bash
curl -X POST http://localhost:8081/api/send-voice \
  -H "Content-Type: application/json" \
  -d '{
    "phone": "+77079621630",
    "text": "Привет! Это тестовое голосовое сообщение."
  }'
```

### С предварительно смодерированным аудиофайлом
```bash
curl -X POST http://localhost:8081/api/send-voice \
  -H "Content-Type: application/json" \
  -d '{
    "phone": "+77079621630",
    "text": "Привет!",
    "recordId": 247273
  }'
```

### С указанным исходящим номером
```bash
curl -X POST http://localhost:8081/api/send-voice \
  -H "Content-Type: application/json" \
  -d '{
    "phone": "+77079621630",
    "text": "Привет!",
    "outgoingPhone": "77719470943"
  }'
```

## 📱 Отправка SMS

### Базовый пример
```bash
curl -X POST http://localhost:8081/api/send-sms \
  -H "Content-Type: application/json" \
  -d '{
    "phone": "+77079621630",
    "text": "Привет! Это тестовое SMS сообщение."
  }'
```

### С указанным исходящим номером
```bash
curl -X POST http://localhost:8081/api/send-sms \
  -H "Content-Type: application/json" \
  -d '{
    "phone": "+77079621630",
    "text": "Привет!",
    "outgoingPhone": "77719470943"
  }'
```

## 🔍 Информационные запросы

### Тестирование API ключа
```bash
curl -X GET http://localhost:8081/api/test-api-key
```

### Получение списка номеров
```bash
curl -X GET http://localhost:8081/api/get-phones
```

### Получение списка аудиофайлов
```bash
curl -X GET http://localhost:8081/api/get-records
```

### Получение профиля пользователя
```bash
curl -X GET http://localhost:8081/api/get-profile
```

## 💻 Примеры на JavaScript

### Отправка голосового сообщения
```javascript
const response = await fetch('http://localhost:8081/api/send-voice', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    phone: '+77079621630',
    text: 'Привет! Это тестовое голосовое сообщение.',
    recordId: 247273,
    outgoingPhone: '77719470943'
  })
});

const result = await response.json();
console.log(result);
```

### Отправка SMS
```javascript
const response = await fetch('http://localhost:8081/api/send-sms', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    phone: '+77079621630',
    text: 'Привет! Это тестовое SMS сообщение.',
    outgoingPhone: '77719470943'
  })
});

const result = await response.json();
console.log(result);
```

## 🐍 Примеры на Python

### Отправка голосового сообщения
```python
import requests
import json

url = "http://localhost:8081/api/send-voice"
data = {
    "phone": "+77079621630",
    "text": "Привет! Это тестовое голосовое сообщение.",
    "recordId": 247273,
    "outgoingPhone": "77719470943"
}

response = requests.post(url, json=data)
result = response.json()
print(result)
```

### Отправка SMS
```python
import requests
import json

url = "http://localhost:8081/api/send-sms"
data = {
    "phone": "+77079621630",
    "text": "Привет! Это тестовое SMS сообщение.",
    "outgoingPhone": "77719470943"
}

response = requests.post(url, json=data)
result = response.json()
print(result)
```

## 📋 Примеры ответов

### Успешный ответ
```json
{
  "success": true,
  "message": "Операция выполнена успешно",
  "data": {
    "result": "ok",
    "data": {
      "callId": 12345,
      "status": "created"
    }
  }
}
```

### Ответ с ошибкой
```json
{
  "success": false,
  "message": "Ошибка выполнения запроса",
  "error": "Для генерации роликов требуется снять модерацию с аккаунта. Свяжитесь с менеджером"
}
```

### Ответ с валидационной ошибкой
```json
{
  "success": false,
  "message": "Ошибка выполнения запроса",
  "error": "Ошибка валидации: phone: Номер телефона должен быть в формате +7XXXXXXXXXX или 7XXXXXXXXXX"
}
```

## 🔧 Настройка окружения

### Переменные окружения
```bash
# Установка API ключа
export ZVONOBOT_API_KEY="ваш_api_ключ_звонобота"

# Запуск приложения
mvn spring-boot:run
```

### Проверка работоспособности
```bash
# Тестирование API ключа
curl -X GET http://localhost:8081/api/test-api-key

# Ожидаемый ответ
{
  "success": true,
  "message": "API ключ работает! Email: user@example.com, Баланс: 60",
  "data": {
    "email": "user@example.com",
    "balance": 60
  }
}
```

## 🚨 Обработка ошибок

### Типичные ошибки и их решения

1. **Ошибка модерации**
   ```
   "Для генерации роликов требуется снять модерацию с аккаунта"
   ```
   **Решение:** Свяжитесь с менеджером Звонобота для снятия модерации

2. **Ошибка валидации номера**
   ```
   "Номер телефона должен быть в формате +7XXXXXXXXXX"
   ```
   **Решение:** Используйте правильный формат номера

3. **Ошибка API ключа**
   ```
   "API ключ недействителен"
   ```
   **Решение:** Проверьте правильность API ключа

4. **Ошибка сети**
   ```
   "Ошибка сети при подключении к API Звонобота"
   ```
   **Решение:** Проверьте интернет-соединение и доступность API 