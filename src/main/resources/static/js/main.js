// Главный JavaScript файл для Zvonobot API Test

/**
 * Инициализация приложения при загрузке страницы
 */
document.addEventListener('DOMContentLoaded', function() {
    console.log('🚀 Инициализация Zvonobot API Test...');
    
    // Инициализируем страницу
    initializePage();
    
    // Добавляем обработчики событий
    setupEventListeners();
    
    console.log('✅ Приложение инициализировано');
});

/**
 * Настройка обработчиков событий
 */
function setupEventListeners() {
    // Обработчик формы голосового сообщения
    const voiceForm = document.getElementById('voice-form');
    if (voiceForm) {
        voiceForm.addEventListener('submit', handleVoiceFormSubmit);
    }
    
    // Обработчик формы SMS
    const smsForm = document.getElementById('sms-form');
    if (smsForm) {
        smsForm.addEventListener('submit', handleSmsFormSubmit);
    }
    
    // Добавляем обработчики для кнопок
    setupButtonHandlers();
    
    // Добавляем обработчики для табов
    setupTabHandlers();
}

/**
 * Настройка обработчиков для кнопок
 */
function setupButtonHandlers() {
    // Кнопка тестирования API ключа
    const testApiKeyBtn = document.querySelector('button[onclick="testApiKey()"]');
    if (testApiKeyBtn) {
        testApiKeyBtn.addEventListener('click', testApiKey);
    }
    
    // Кнопка получения профиля
    const getUserProfileBtn = document.querySelector('button[onclick="getUserProfile()"]');
    if (getUserProfileBtn) {
        getUserProfileBtn.addEventListener('click', getUserProfile);
    }
    
    // Кнопка получения номеров
    const getOutgoingPhonesBtn = document.querySelector('button[onclick="getOutgoingPhones()"]');
    if (getOutgoingPhonesBtn) {
        getOutgoingPhonesBtn.addEventListener('click', getOutgoingPhones);
    }
    
    // Кнопка получения аудио
    const getRecordsBtn = document.querySelector('button[onclick="getRecords()"]');
    if (getRecordsBtn) {
        getRecordsBtn.addEventListener('click', getRecords);
    }
    
    // Кнопка тестирования SMS
    const testSmsBtn = document.querySelector('button[onclick="testSms()"]');
    if (testSmsBtn) {
        testSmsBtn.addEventListener('click', testSms);
    }
}

/**
 * Настройка обработчиков для табов
 */
function setupTabHandlers() {
    const tabs = document.querySelectorAll('.tab');
    tabs.forEach(tab => {
        tab.addEventListener('click', function() {
            const tabName = this.getAttribute('onclick').match(/'([^']+)'/)[1];
            switchTab(tabName);
        });
    });
}

/**
 * Глобальные обработчики ошибок
 */
window.addEventListener('error', function(event) {
    console.error('🚨 Глобальная ошибка JavaScript:', event.error);
    showResult(`❌ Ошибка JavaScript: ${event.error.message}`, 'error');
});

window.addEventListener('unhandledrejection', function(event) {
    console.error('🚨 Необработанное отклонение промиса:', event.reason);
    showResult(`❌ Ошибка промиса: ${event.reason}`, 'error');
});

/**
 * Функция для отладки (только в режиме разработки)
 */
function debugInfo() {
    console.log('🔍 Отладочная информация:');
    console.log('- API Base URL:', API_BASE_URL);
    console.log('- Текущий активный таб:', document.querySelector('.tab.active')?.textContent);
    console.log('- Количество форм:', document.querySelectorAll('form').length);
    console.log('- Количество кнопок:', document.querySelectorAll('button').length);
}

// Экспортируем функции для глобального использования
window.switchTab = switchTab;
window.testApiKey = testApiKey;
window.getUserProfile = getUserProfile;
window.getOutgoingPhones = getOutgoingPhones;
window.getRecords = getRecords;
window.testSms = testSms;
window.debugInfo = debugInfo; 