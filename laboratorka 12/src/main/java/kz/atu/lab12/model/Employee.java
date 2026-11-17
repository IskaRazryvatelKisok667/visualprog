package kz.atu.lab12.model;

/** Индивидуальный вариант №3: сотрудник (ФИО, отдел, стаж в годах). */
public record Employee(int id, String fullName, String department, int experience) {
}
