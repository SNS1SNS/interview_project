// Конфигурация для Zvonobot API Test

/**
 * Основные настройки приложения
 */
const CONFIG = {
    // API настройки
    API: {
        BASE_URL: 'http://localhost:8081/api',
        TIMEOUT: 30000, // 30 секунд
        RETRY_ATTEMPTS: 3
    },
    
    // UI настройки
    UI: {
        NOTIFICATION_DURATION: 3000, // 3 секунды
        MAX_PHONE_NUMBERS: 10,
        ANIMATION_DURATION: 300
    },
    
    // Валидация
    VALIDATION: {
        PHONE_REGEX: /^7\d{10}$/,
        MIN_TEXT_LENGTH: 1,
        MAX_TEXT_LENGTH: 1000
    },
    
    // Тестовые данные
    TEST_DATA: {
        PHONE: '+77079621630',
        VOICE_TEXT: 'Привет! Это тестовое голосовое сообщение на русском языке от API Звонобота.',
        SMS_TEXT: 'Привет! Это тестовое SMS сообщение на русском языке.',
        RECORD_ID: '247273'
    },
    
    // Сообщения
    MESSAGES: {
        SUCCESS: {
            API_KEY_TEST: 'API ключ работает!',
            PHONES_LOADED: 'Номера успешно загружены',
            PROFILE_LOADED: 'Профиль пользователя получен',
            RECORDS_LOADED: 'Аудиофайлы загружены',
            MESSAGE_SENT: 'Сообщение отправлено'
        },
        ERROR: {
            VALIDATION: 'Ошибка валидации',
            NETWORK: 'Ошибка сети',
            API: 'Ошибка API',
            UNKNOWN: 'Неизвестная ошибка'
        },
        LOADING: {
            API_KEY: '🔑 Тестирование API ключа...',
            PHONES: '📞 Получение списка номеров...',
            PROFILE: '👤 Получение профиля пользователя...',
            RECORDS: '🎵 Получение списка аудиофайлов...',
            SENDING: '🔄 Отправка запроса...'
        }
    },
    
    // Цвета для уведомлений
    COLORS: {
        SUCCESS: '#28a745',
        ERROR: '#dc3545',
        WARNING: '#ffc107',
        INFO: '#17a2b8',
        PRIMARY: '#007bff'
    }
};

/**
 * Получение значения конфигурации
 */
function getConfig(path) {
    return path.split('.').reduce((obj, key) => obj && obj[key], CONFIG);
}

/**
 * Установка значения конфигурации
 */
function setConfig(path, value) {
    const keys = path.split('.');
    const lastKey = keys.pop();
    const obj = keys.reduce((obj, key) => obj[key] = obj[key] || {}, CONFIG);
    obj[lastKey] = value;
}

/**
 * Проверка окружения
 */
function isDevelopment() {
    return window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1';
}

/**
 * Логирование (только в режиме разработки)
 */
function log(message, type = 'info') {
    if (isDevelopment()) {
        const timestamp = new Date().toLocaleTimeString();
        const prefix = `[${timestamp}]`;
        
        switch (type) {
            case 'error':
                console.error(prefix, message);
                break;
            case 'warn':
                console.warn(prefix, message);
                break;
            case 'success':
                console.log(prefix, '✅', message);
                break;
            default:
                console.log(prefix, message);
        }
    }
}

/**
 * Экспорт конфигурации
 */
window.CONFIG = CONFIG;
window.getConfig = getConfig;
window.setConfig = setConfig;
window.isDevelopment = isDevelopment;
window.log = log; 