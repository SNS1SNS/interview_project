// API функции для Zvonobot API Test

const API_BASE_URL = 'http://localhost:8081/api';

/**
 * Декодирует Unicode-последовательности в читаемый текст
 */
function decodeUnicode(str) {
    return str.replace(/\\u([0-9a-fA-F]{4})/g, function(match, group) {
        return String.fromCharCode(parseInt(group, 16));
    });
}

/**
 * Показывает результат операции
 */
function showResult(message, type = 'success') {
    const decodedMessage = decodeUnicode(message);
    const resultDiv = document.getElementById('result');
    resultDiv.textContent = decodedMessage;
    resultDiv.className = `result ${type} fade-in`;
    resultDiv.style.display = 'block';
    
    // Плавная прокрутка к результату
    resultDiv.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
}

/**
 * Показывает загрузку
 */
function showLoading(message) {
    showResult(message, 'loading');
}

/**
 * Блокирует/разблокирует кнопки
 */
function setButtonsState(disabled) {
    const buttons = document.querySelectorAll('button');
    buttons.forEach(button => {
        button.disabled = disabled;
    });
}

/**
 * Показывает прогресс с анимированным индикатором
 */
function showProgress(message) {
    const resultDiv = document.getElementById('result');
    resultDiv.innerHTML = `
        <div style="text-align: center; padding: 20px;">
            <div style="margin-bottom: 15px;">${message}</div>
            <div style="width: 100%; height: 4px; background-color: #e9ecef; border-radius: 2px; overflow: hidden;">
                <div style="width: 100%; height: 100%; background-color: #007bff; animation: progress 2s ease-in-out infinite;"></div>
            </div>
        </div>
    `;
    resultDiv.className = 'result loading fade-in';
    resultDiv.style.display = 'block';
}

/**
 * Отправляет запрос к API
 */
async function sendRequest(endpoint, data) {
    try {
        setButtonsState(true);
        showProgress('🔄 Отправка запроса...');
        
        const response = await fetch(`${API_BASE_URL}/${endpoint}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json; charset=UTF-8',
            },
            body: JSON.stringify(data)
        });
        
        const result = await response.json();
        
        if (result.success) {
            showResult(`✅ Успешно!\n\nОтвет сервера:\n${JSON.stringify(result, null, 2)}`, 'success');
        } else {
            showResult(`❌ Ошибка!\n\n${result.error}`, 'error');
        }
        
        return result;
    } catch (error) {
        showResult(`❌ Ошибка сети!\n\n${error.message}`, 'error');
        throw error;
    } finally {
        setButtonsState(false);
    }
}

/**
 * Тестирует API ключ
 */
async function testApiKey() {
    try {
        setButtonsState(true);
        showProgress('🔑 Тестирование API ключа...');
        
        const response = await fetch(`${API_BASE_URL}/test-api-key`, {
            method: 'GET'
        });
        
        const result = await response.json();
        
        if (result.success) {
            showResult(`✅ ${result.message}\n\nОтвет сервера:\n${JSON.stringify(result.data, null, 2)}`, 'success');
        } else {
            showResult(`❌ Проблема с API ключом!\n\n${result.error || 'Неизвестная ошибка'}`, 'error');
        }
        
        return result;
    } catch (error) {
        showResult(`❌ Ошибка при тестировании API ключа!\n\n${error.message}`, 'error');
        throw error;
    } finally {
        setButtonsState(false);
    }
}

/**
 * Получает профиль пользователя
 */
async function getUserProfile() {
    try {
        setButtonsState(true);
        showProgress('👤 Получение профиля пользователя...');
        
        const response = await fetch(`${API_BASE_URL}/get-profile`, {
            method: 'GET'
        });
        const result = await response.json();
        if (result.success) {
            showResult(`✅ Профиль пользователя получен!\n\nОтвет сервера:\n${JSON.stringify(result.data, null, 2)}`, 'success');
        } else {
            showResult(`❌ Ошибка при получении профиля пользователя!\n\n${result.error}`, 'error');
        }
    } catch (error) {
        showResult(`❌ Ошибка сети при получении профиля пользователя!\n\n${error.message}`, 'error');
        throw error;
    } finally {
        setButtonsState(false);
    }
}

/**
 * Получает список исходящих номеров
 */
async function getOutgoingPhones() {
    try {
        setButtonsState(true);
        showProgress('📞 Получение списка номеров...');
        
        const response = await fetch(`${API_BASE_URL}/get-phones`, {
            method: 'GET'
        });
        const result = await response.json();
        if (result.success) {
            showResult(`✅ Получен список исходящих номеров!\n\nОтвет сервера:\n${JSON.stringify(result.data, null, 2)}`, 'success');
            
            // Автоматически заполняем выпадающие списки номерами
            updatePhoneDropdowns(result.data);
        } else {
            showResult(`❌ Ошибка при получении исходящих номеров!\n\n${result.error}`, 'error');
        }
    } catch (error) {
        showResult(`❌ Ошибка сети при получении исходящих номеров!\n\n${error.message}`, 'error');
        throw error;
    } finally {
        setButtonsState(false);
    }
}

/**
 * Получает список аудиофайлов
 */
async function getRecords() {
    try {
        setButtonsState(true);
        showProgress('🎵 Получение списка аудиофайлов...');
        
        const response = await fetch(`${API_BASE_URL}/get-records`, {
            method: 'GET'
        });
        const result = await response.json();
        if (result.success) {
            if (result.data && result.data.length > 0) {
                showResult(`✅ Получен список предварительно смодерированных аудиофайлов!\n\nДоступно файлов: ${result.data.length}\n\nОтвет сервера:\n${JSON.stringify(result.data, null, 2)}`, 'success');
            } else {
                showResult(`⚠️ Список предварительно смодерированных аудиофайлов пуст!\n\nЭто означает, что для отправки голосовых сообщений потребуется модерация аккаунта.\n\nПопробуйте отправить SMS сообщение вместо голосового.`, 'warning');
            }
        } else {
            showResult(`❌ Ошибка при получении аудиофайлов!\n\n${result.error}`, 'error');
        }
    } catch (error) {
        showResult(`❌ Ошибка сети при получении аудиофайлов!\n\n${error.message}`, 'error');
        throw error;
    } finally {
        setButtonsState(false);
    }
}

/**
 * Тестирует отправку SMS
 */
async function testSms() {
    const phone = '+77079621630';
    const text = 'Привет! Это тестовое SMS сообщение на русском языке.';
    const outgoingPhone = document.getElementById('sms-outgoing-phone').value;

    // Очищаем номер телефона от лишних символов
    let cleanPhone = phone.replace(/[\s\-\(\)\+]/g, '');
    
    // Валидация
    if (!/^7\d{10}$/.test(cleanPhone)) {
        showResult('❌ Ошибка валидации!\n\nНомер телефона должен быть в формате 7XXXXXXXXXX\n\nПримеры:\n+7 707 962 16 30\n+7-707-962-16-30\n77079621630', 'error');
        return;
    }

    if (!text.trim()) {
        showResult('❌ Ошибка валидации!\n\nТекст сообщения не может быть пустым', 'error');
        return;
    }

    try {
        const requestData = { phone: cleanPhone, text };
        if (outgoingPhone && outgoingPhone.trim() !== '') {
            requestData.outgoingPhone = outgoingPhone;
        }
        await sendRequest('send-sms', requestData);
    } catch (error) {
        // Ошибка уже обработана в sendRequest
    }
} 