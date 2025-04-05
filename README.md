# navcalc v1.02

Консольное Java-приложение для анализа геоданных и визуализации навигационных точек бота.

## 🔧 Сборка

Требуется Java 17+ и Maven:

```bash
mvn clean package
```

После сборки исполняемый JAR будет находиться в:

```
target/navcalc-jar-with-dependencies.jar
```

## 🚀 Запуск

Пример запуска:

```bash
java -jar target/navcalc-jar-with-dependencies.jar
```

## 📦 Структура

- `src/main/java/com/devone/navcalc/` — основной код
- `target/nav_report.html` — HTML с визуализацией (создаётся при запуске)