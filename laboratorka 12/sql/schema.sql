-- Схема базы данных students.db (создаётся приложением автоматически)
CREATE TABLE IF NOT EXISTS students (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    full_name  TEXT NOT NULL,
    group_name TEXT NOT NULL,
    gpa        REAL NOT NULL CHECK (gpa BETWEEN 0 AND 4)
);

-- Индивидуальный вариант №3 «Сотрудники»
CREATE TABLE IF NOT EXISTS employees (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    full_name  TEXT NOT NULL,
    department TEXT NOT NULL,
    experience INTEGER NOT NULL CHECK (experience BETWEEN 0 AND 60)
);
