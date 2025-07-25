// UI функции для Zvonobot API Test

/**
 * Переключает между табами
 */
function switchTab(tabName) {
    // Убираем активный класс у всех табов
    document.querySelectorAll('.tab').forEach(tab => tab.classList.remove('active'));
    document.querySelectorAll('.tab-content').forEach(content => content.classList.remove('active'));
    
    // Добавляем активный класс к выбранному табу
    document.querySelector(`[onclick="switchTab('${tabName}')"]`).classList.add('active');
    document.getElementById(`${tabName}-tab`).classList.add('active');
}

/**
 * Обновляет выпадающие списки номеров
 */
function updatePhoneDropdowns(phonesData) {
    if (!phonesData || !Array.isArray(phonesData)) {
        console.warn('Данные номеров не являются массивом:', phonesData);
        return;
    }
    
    // Извлекаем номера из данных
    const phoneNumbers = phonesData
        .filter(phone => phone && phone.phone)
        .map(phone => phone.phone)
        .slice(0, 10); // Ограничиваем до 10 номеров для удобства
    
    if (phoneNumbers.length === 0) {
        console.warn('Не найдено номеров для заполнения');
        return;
    }
    
    // Обновляем выпадающие списки для голосовых сообщений
    const voicePhoneSelect = document.getElementById('voice-outgoing-phone');
    updateSelectOptions(voicePhoneSelect, phoneNumbers);
    
    // Обновляем выпадающие списки для SMS
    const smsPhoneSelect = document.getElementById('sms-outgoing-phone');
    updateSelectOptions(smsPhoneSelect, phoneNumbers);
    
    // Показываем уведомление о успешном обновлении
    showPhoneUpdateNotification(phoneNumbers.length);
    
    console.log(`Обновлено ${phoneNumbers.length} номеров в выпадающих списках`);
}

/**
 * Показывает уведомление об обновлении номеров
 */
function showPhoneUpdateNotification(count) {
    const notification = document.createElement('div');
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background-color: #28a745;
        color: white;
        padding: 15px 20px;
        border-radius: 8px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        z-index: 1000;
        font-weight: 600;
        animation: slideIn 0.3s ease-out;
    `;
    notification.textContent = `✅ Загружено ${count} номеров`;
    
    document.body.appendChild(notification);
    
    // Удаляем уведомление через 3 секунды
    setTimeout(() => {
        notification.style.animation = 'slideOut 0.3s ease-in';
        setTimeout(() => {
            if (notification.parentNode) {
                notification.parentNode.removeChild(notification);
            }
        }, 300);
    }, 3000);
}

/**
 * Обновляет опции в select элементе
 */
function updateSelectOptions(selectElement, phoneNumbers) {
    if (!selectElement) return;
    
    // Сохраняем текущее выбранное значение
    const currentValue = selectElement.value;
    
    // Очищаем существующие опции, кроме первой (Автоматический выбор)
    while (selectElement.children.length > 1) {
        selectElement.removeChild(selectElement.lastChild);
    }
    
    // Добавляем новые номера
    phoneNumbers.forEach(phone => {
        const option = document.createElement('option');
        option.value = phone;
        option.textContent = phone;
        selectElement.appendChild(option);
    });
    
    // Восстанавливаем выбранное значение, если оно все еще существует
    if (currentValue && phoneNumbers.includes(currentValue)) {
        selectElement.value = currentValue;
    }
}

/**
 * Валидирует номер телефона
 */
function validatePhone(phone) {
    const cleanPhone = phone.replace(/[\s\-\(\)\+]/g, '');
    return /^7\d{10}$/.test(cleanPhone);
}

/**
 * Очищает номер телефона от лишних символов
 */
function cleanPhoneNumber(phone) {
    return phone.replace(/[\s\-\(\)\+]/g, '');
}

/**
 * Показывает ошибку валидации
 */
function showValidationError(message) {
    showResult(`❌ Ошибка валидации!\n\n${message}`, 'error');
}

/**
 * Инициализация страницы
 */
function initializePage() {
    const testPhone = '+77079621630';
    const testVoiceText = 'Привет! Это тестовое голосовое сообщение на русском языке от API Звонобота.';
    const testSmsText = 'Привет! Это тестовое SMS сообщение на русском языке.';
    
    // Заполняем тестовые данные
    document.getElementById('voice-phone').value = testPhone;
    document.getElementById('voice-text').value = testVoiceText;
    document.getElementById('voice-record-id').value = '247273';
    
    document.getElementById('sms-phone').value = testPhone;
    document.getElementById('sms-text').value = testSmsText;
    
    // Автоматически получаем номера при загрузке страницы
    loadPhoneNumbersOnStartup();
}

/**
 * Загружает номера при запуске
 */
async function loadPhoneNumbersOnStartup() {
    try {
        console.log('Автоматическое получение номеров при загрузке страницы...');
        const response = await fetch(`${API_BASE_URL}/get-phones`, {
            method: 'GET'
        });
        const result = await response.json();
        if (result.success && result.data) {
            updatePhoneDropdowns(result.data);
            console.log('Номера успешно загружены при инициализации');
        }
    } catch (error) {
        console.log('Не удалось автоматически загрузить номера:', error.message);
        // Это не критическая ошибка, пользователь может нажать кнопку "Получить номера" вручную
    }
}

/**
 * Обработчик формы голосового сообщения
 */
function handleVoiceFormSubmit(e) {
    e.preventDefault();
    
    let phone = document.getElementById('voice-phone').value;
    const text = document.getElementById('voice-text').value;
    const recordId = document.getElementById('voice-record-id').value;
    const outgoingPhone = document.getElementById('voice-outgoing-phone').value;
    
    // Очищаем номер телефона от лишних символов
    phone = cleanPhoneNumber(phone);
    
    // Валидация
    if (!validatePhone(phone)) {
        showValidationError('Номер телефона должен быть в формате 7XXXXXXXXXX\n\nПримеры:\n+7 707 962 16 30\n+7-707-962-16-30\n77079621630');
        return;
    }
    
    if (!text.trim()) {
        showValidationError('Текст сообщения не может быть пустым');
        return;
    }
    
    // Отправляем запрос
    sendVoiceMessage(phone, text, recordId, outgoingPhone);
}

/**
 * Обработчик формы SMS
 */
function handleSmsFormSubmit(e) {
    e.preventDefault();
    
    let phone = document.getElementById('sms-phone').value;
    const text = document.getElementById('sms-text').value;
    const outgoingPhone = document.getElementById('sms-outgoing-phone').value;
    
    // Очищаем номер телефона от лишних символов
    phone = cleanPhoneNumber(phone);
    
    // Валидация
    if (!validatePhone(phone)) {
        showValidationError('Номер телефона должен быть в формате 7XXXXXXXXXX\n\nПримеры:\n+7 707 962 16 30\n+7-707-962-16-30\n77079621630');
        return;
    }
    
    if (!text.trim()) {
        showValidationError('Текст сообщения не может быть пустым');
        return;
    }
    
    // Отправляем запрос
    sendSmsMessage(phone, text, outgoingPhone);
}

/**
 * Отправляет голосовое сообщение
 */
async function sendVoiceMessage(phone, text, recordId, outgoingPhone) {
    try {
        const requestData = { phone, text };
        if (recordId && recordId.trim() !== '') {
            requestData.recordId = parseInt(recordId);
        }
        if (outgoingPhone && outgoingPhone.trim() !== '') {
            requestData.outgoingPhone = outgoingPhone;
        }
        await sendRequest('send-voice', requestData);
    } catch (error) {
        // Ошибка уже обработана в sendRequest
    }
}

/**
 * Отправляет SMS сообщение
 */
async function sendSmsMessage(phone, text, outgoingPhone) {
    try {
        const requestData = { phone, text };
        if (outgoingPhone && outgoingPhone.trim() !== '') {
            requestData.outgoingPhone = outgoingPhone;
        }
        await sendRequest('send-sms', requestData);
    } catch (error) {
        // Ошибка уже обработана в sendRequest
    }
} 