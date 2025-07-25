# Документация API для фронтенд разработчиков

## Базовый URL
```
http://localhost:8081/api
```

## Аутентификация

В текущей версии API не требует аутентификации. Все запросы выполняются с предустановленным API ключом.

## Общий формат ответов

Все API endpoints возвращают ответы в едином формате:

### Успешный ответ
```json
{
  "success": true,
  "message": "Описание результата",
  "data": {
    // Данные от внешнего API
  }
}
```

### Ответ с ошибкой
```json
{
  "success": false,
  "message": "Ошибка выполнения запроса",
  "error": "Детальное описание ошибки"
}
```

## Endpoints

### 1. Отправка голосового сообщения

**POST** `/send-voice`

**Тело запроса:**
```json
{
  "phone": "79991234567",
  "text": "Текст для голосового сообщения",
  "recordId": 123
}
```

**Параметры:**
- `phone` (string, обязательный) - номер телефона в формате 7XXXXXXXXXX
- `text` (string, обязательный) - текст для генерации голоса (используется только если recordId не указан)
- `recordId` (integer, опциональный) - ID предварительно смодерированного аудиофайла

**Примечание:** Если указан `recordId`, то используется предварительно смодерированное аудио. Если `recordId` не указан, то генерируется голос из текста.

**Ответ:**
```json
{
  "success": true,
  "message": "Сообщение успешно отправлено",
  "data": {
    "result": "ok",
    "data": {...}
  }
}
```

### 2. Отправка SMS

**URL:** `POST /api/send-sms`  
**Описание:** Отправляет SMS сообщение на указанный номер телефона

#### Запрос

**Headers:**
```
Content-Type: application/json
```

**Body:**
```json
{
  "phone": "+77079621630",
  "text": "Текст SMS сообщения"
}
```

**Параметры:**
- `phone` (string, обязательный) - Номер телефона в формате `+7XXXXXXXXXX` или `7XXXXXXXXXX` (11 цифр, начинается с 7). Поддерживаются различные форматы: `+7 707 962 16 30`, `+7-707-962-16-30`, `77079621630`
- `text` (string, обязательный) - Текст SMS сообщения

#### Ответ

**Успешный ответ (200 OK):**
```json
{
  "success": true,
  "message": "Сообщение успешно отправлено",
  "data": {
    "result": "ok",
    "data": {
      "id": 12346,
      "status": "created"
    }
  }
}
```

**Ошибка валидации (400 Bad Request):**
```json
{
  "success": false,
  "message": "Ошибка выполнения запроса",
  "error": "Ошибка валидации: text: Текст сообщения обязателен"
}
```

### 3. Получение профиля пользователя

**GET** `/get-profile`

Возвращает информацию о профиле пользователя (email, баланс, настройки).

**Ответ:**
```json
{
  "success": true,
  "message": "Операция выполнена успешно",
  "data": {
    "email": "user@example.com",
    "phone": "77079621630",
    "balance": 60,
    "status": "active"
  }
}
```

### 4. Получение списка предварительно смодерированных аудиофайлов

**GET** `/get-records`

Возвращает список доступных предварительно смодерированных аудиофайлов.

**Ответ:**
```json
{
  "success": true,
  "message": "Операция выполнена успешно",
  "data": [
    {
      "id": 1,
      "name": "Приветствие",
      "duration": 5
    }
  ]
}
```

**Примечание:** Если список пуст, это означает, что для отправки голосовых сообщений потребуется модерация аккаунта.

## Коды ошибок

### Ошибки валидации (400 Bad Request)

| Поле | Ошибка | Описание |
|------|--------|----------|
| phone | Номер телефона обязателен | Поле phone не может быть пустым |
| phone | Номер телефона должен быть в формате 7XXXXXXXXXX | Неверный формат номера телефона |
| text | Текст сообщения обязателен | Поле text не может быть пустым |

### Ошибки API

| Ошибка | Описание |
|--------|----------|
| Invalid API key | Неверный API ключ |
| Phone number not found | Номер телефона не найден |
| Insufficient balance | Недостаточно средств на балансе |
| Text too long | Текст сообщения слишком длинный |
| Service temporarily unavailable | Сервис временно недоступен |

## Примеры использования

### JavaScript (Fetch API)

```javascript
// Отправка голосового сообщения
async function sendVoiceMessage(phone, text) {
  try {
    const response = await fetch('http://localhost:8081/api/send-voice', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        phone: phone,
        text: text
      })
    });
    
    const result = await response.json();
    
    if (result.success) {
      console.log('Голосовое сообщение отправлено:', result.data);
      return result.data;
    } else {
      console.error('Ошибка отправки:', result.error);
      throw new Error(result.error);
    }
  } catch (error) {
    console.error('Ошибка запроса:', error);
    throw error;
  }
}

// Отправка SMS
async function sendSms(phone, text) {
  try {
    const response = await fetch('http://localhost:8081/api/send-sms', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        phone: phone,
        text: text
      })
    });
    
    const result = await response.json();
    
    if (result.success) {
      console.log('SMS отправлено:', result.data);
      return result.data;
    } else {
      console.error('Ошибка отправки:', result.error);
      throw new Error(result.error);
    }
  } catch (error) {
    console.error('Ошибка запроса:', error);
    throw error;
  }
}

// Пример использования
sendVoiceMessage('+77079621630', 'Привет! Это тестовое голосовое сообщение.')
  .then(data => console.log('Успех:', data))
  .catch(error => console.error('Ошибка:', error));

sendSms('+77079621630', 'Привет! Это тестовое SMS.')
  .then(data => console.log('Успех:', data))
  .catch(error => console.error('Ошибка:', error));
```

### JavaScript (Axios)

```javascript
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8081/api';

// Отправка голосового сообщения
async function sendVoiceMessage(phone, text) {
  try {
    const response = await axios.post(`${API_BASE_URL}/send-voice`, {
      phone: phone,
      text: text
    });
    
    const result = response.data;
    
    if (result.success) {
      console.log('Голосовое сообщение отправлено:', result.data);
      return result.data;
    } else {
      throw new Error(result.error);
    }
  } catch (error) {
    if (error.response) {
      console.error('Ошибка API:', error.response.data);
      throw new Error(error.response.data.error || 'Ошибка API');
    } else {
      console.error('Ошибка сети:', error.message);
      throw error;
    }
  }
}

// Отправка SMS
async function sendSms(phone, text) {
  try {
    const response = await axios.post(`${API_BASE_URL}/send-sms`, {
      phone: phone,
      text: text
    });
    
    const result = response.data;
    
    if (result.success) {
      console.log('SMS отправлено:', result.data);
      return result.data;
    } else {
      throw new Error(result.error);
    }
  } catch (error) {
    if (error.response) {
      console.error('Ошибка API:', error.response.data);
      throw new Error(error.response.data.error || 'Ошибка API');
    } else {
      console.error('Ошибка сети:', error.message);
      throw error;
    }
  }
}
```

### React Hook

```javascript
import { useState } from 'react';

export function useMessageSender() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const sendVoiceMessage = async (phone, text) => {
    setLoading(true);
    setError(null);
    
    try {
      const response = await fetch('http://localhost:8081/api/send-voice', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ phone, text })
      });
      
      const result = await response.json();
      
      if (result.success) {
        return result.data;
      } else {
        throw new Error(result.error);
      }
    } catch (err) {
      setError(err.message);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const sendSms = async (phone, text) => {
    setLoading(true);
    setError(null);
    
    try {
      const response = await fetch('http://localhost:8081/api/send-sms', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ phone, text })
      });
      
      const result = await response.json();
      
      if (result.success) {
        return result.data;
      } else {
        throw new Error(result.error);
      }
    } catch (err) {
      setError(err.message);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  return {
    sendVoiceMessage,
    sendSms,
    loading,
    error
  };
}
```

### React Component

```jsx
import React, { useState } from 'react';
import { useMessageSender } from './useMessageSender';

function MessageSender() {
  const [phone, setPhone] = useState('');
  const [text, setText] = useState('');
  const [messageType, setMessageType] = useState('voice');
  const { sendVoiceMessage, sendSms, loading, error } = useMessageSender();

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    try {
      if (messageType === 'voice') {
        await sendVoiceMessage(phone, text);
        alert('Голосовое сообщение отправлено!');
      } else {
        await sendSms(phone, text);
        alert('SMS отправлено!');
      }
      
      setPhone('');
      setText('');
    } catch (err) {
      alert(`Ошибка: ${err.message}`);
    }
  };

  return (
    <div>
      <h2>Отправка сообщений</h2>
      
      <form onSubmit={handleSubmit}>
        <div>
          <label>
            Тип сообщения:
            <select 
              value={messageType} 
              onChange={(e) => setMessageType(e.target.value)}
            >
              <option value="voice">Голосовое сообщение</option>
              <option value="sms">SMS</option>
            </select>
          </label>
        </div>
        
        <div>
          <label>
            Номер телефона:
            <input
              type="text"
              value={phone}
              onChange={(e) => setPhone(e.target.value)}
              placeholder="79991234567"
              required
            />
          </label>
        </div>
        
        <div>
          <label>
            Текст сообщения:
            <textarea
              value={text}
              onChange={(e) => setText(e.target.value)}
              placeholder="Введите текст сообщения"
              required
            />
          </label>
        </div>
        
        <button type="submit" disabled={loading}>
          {loading ? 'Отправка...' : 'Отправить'}
        </button>
      </form>
      
      {error && (
        <div style={{ color: 'red', marginTop: '10px' }}>
          Ошибка: {error}
        </div>
      )}
    </div>
  );
}

export default MessageSender;
```

## Валидация на фронтенде

### Регулярное выражение для номера телефона

```javascript
const phoneRegex = /^\+?7\d{10}$/;

function validatePhone(phone) {
  // Очищаем номер от лишних символов
  const cleanPhone = phone.replace(/[\s\-\(\)\+]/g, '');
  return phoneRegex.test(cleanPhone);
}

// Пример использования
if (!validatePhone('+77079621630')) {
  alert('Неверный формат номера телефона. Используйте формат: +77079621630');
}
```

### Валидация текста

```javascript
function validateText(text) {
  if (!text || text.trim().length === 0) {
    return 'Текст сообщения не может быть пустым';
  }
  
  if (text.length > 1000) {
    return 'Текст сообщения слишком длинный (максимум 1000 символов)';
  }
  
  return null;
}

// Пример использования
const textError = validateText(messageText);
if (textError) {
  alert(textError);
  return;
}
```

## CORS

API поддерживает CORS для всех источников (`*`). Если вы планируете использовать API в продакшене, рекомендуется настроить более строгие CORS правила.

## Ограничения

- Максимальная длина текста: 1000 символов
- Формат номера телефона: российские номера (начинаются с +7 или 7). Поддерживаются форматы: `+7 707 962 16 30`, `+7-707-962-16-30`, `77079621630`
- Частота запросов: рекомендуется не более 10 запросов в секунду
- Таймаут запроса: 30 секунд

## Поддержка

При возникновении проблем с API, проверьте:
1. Правильность формата номера телефона
2. Наличие текста в сообщении
3. Доступность сервера
4. Логи сервера для детальной диагностики 