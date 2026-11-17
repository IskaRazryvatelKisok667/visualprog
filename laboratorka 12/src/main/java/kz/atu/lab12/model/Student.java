package kz.atu.lab12.model;

/** Неизменяемая модель строки таблицы students. */
public record Student(int id, String fullName, String groupName, double gpa) {
}
