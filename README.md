# MedVision (Mobile part)

**MedVision** - Android-додаток для пацієнтів та лікарів для управління медичними даними та їх аналізу, включаючи КТ-знімки та звіти.
Додаток підтримує безпечну авторизацію, керування профілем, завантаження аналізів, генерацію PDF-звітів та порівняння медичних зображень із тепловими картами.

### Стек технологій

- **Мова:** Kotlin
- **Архітектура:** MVVM + Clean Architecture
- **UI:** Jetpack Compose + Material 3
- **Навігація:** Jetpack Navigation
- **Сховище:** EncryptedSharedPreferences
- **Асинхронність:** Kotlin Coroutines + Flow
- **DI:** Hilt

### Основний функціонал

- **Автентифікація** з використанням шифрованого сховища (EncryptedSharedPreferences)
- **Панелі пацієнта та лікаря** з різним інтерфейсом
- **Керування профілем** з редагуванням аватара та валідацією даних
- **Керування аналізами**: завантаження, перегляд, пошук і фільтрація
- **Нотатки для лікарів** до зображень аналізів
- **Візуалізація теплових карт** і порівняння аналізів
- **Генерація та завантаження PDF-звітів**
- **Локалізація**: українська та англійська мови

---

*MedVision є дослідницьким проєктом і не є медичним інструментом для постановки діагнозу.

---

# MedVision (Mobile part)

**MedVision** is an Android application designed for patients and doctors to manage and analyze medical data, including CT scan images and analysis reports.  
The app allows secure authentication, patient profile management, analysis uploads, PDF report generation, and medical image comparison with heatmaps.

### Tech Stack
- **Language:** Kotlin
- **Architecture:** MVVM + Clean Architecture
- **UI:** Jetpack Compose + Material 3
- **Navigation:** Jetpack Navigation
- **Persistence:** EncryptedSharedPreferences
- **Asynchronous:** Kotlin Coroutines + Flow
- **Dependency Injection:** Hilt

### Key Features
- **Authentication** with encrypted storage (EncryptedSharedPreferences)
- **Doctor and Patient panels** with role-based UI
- **User profile management** with avatar editing and validation
- **Analysis management**: upload, view, filter, and search
- **Notes for doctors** on analysis images
- **Heatmap visualization** and analysis comparison
- **PDF report generation and downloading**
- **Localization**: Ukrainian and English languages

---

*MedVision is a research tool and **not a diagnostic medical system**.

---