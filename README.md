# Stack Search

An Android app for searching Stack Overflow questions and browsing answers.  
Built with **Kotlin**, **Jetpack Compose**, **Retrofit**, **Room**, **Hilt** and **MVVM/Clean architecture**.

---

## Features
- Search Stack Overflow questions
- View newest questions with pull-to-refresh
- Question details with answers and sorting
- Local caching with Room
- Unit tests for use cases, repositories, mappers and view models

---

## Setup
1. Clone the repo
2. Create a `secrets.properties` file at the project root:

   ```properties
   STACK_EXCHANGE_API_KEY=your_key_here
   ```

3. Build and run on any Android device or emulator with internet access

---

## Testing
Tests live in:  
`/app/src/test/kotlin/com/michaelbentz/stacksearch`

Run with:

```bash
./gradlew test
```

---

## API
- [Advanced Search](https://api.stackexchange.com/docs/advanced-search)
- [Answers on Questions](https://api.stackexchange.com/docs/answers-on-questions)
