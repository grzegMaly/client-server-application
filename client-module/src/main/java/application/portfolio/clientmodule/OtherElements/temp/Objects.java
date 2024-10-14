package application.portfolio.clientmodule.OtherElements.temp;

import application.portfolio.clientmodule.OtherElements.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Objects {

    private static List<PersonDAO> persons = new ArrayList<>();
    private static final PersonDAO admin = new PersonDAO("Hugo", "Boss", "ADMIN", "hugo", "boss");
    private static final PersonDAO manager1 = new PersonDAO("Mike", "Tys", "MANAGER", "admin", "admin");
    private static final PersonDAO manager2 = new PersonDAO("Tim", "Lee", "MANAGER", "admin1", "admin1");
    private static final GroupDAO managersGroup = new GroupDAO("Managers", admin);

    private static final GroupDAO testingGroup1 = new GroupDAO("Testing", manager1);
    private static final GroupDAO testingGroup2 = new GroupDAO("Testing", manager2);

    private static final GroupDAO developmentGroup = new GroupDAO("Development", manager1);
    private static final GroupDAO programmingGroup = new GroupDAO("Programming", manager1);
    private static final GroupDAO uiGroup = new GroupDAO("UI Design", manager2);

    static {
        initPersons();
    }

    private static void initPersons() {

        admin.addOwnedGroups(managersGroup);
        manager1.addOwnedGroups(testingGroup1, developmentGroup, programmingGroup);
        manager2.addOwnedGroups(testingGroup2, uiGroup);

        manager1.addToGroup(managersGroup);
        manager2.addToGroup(managersGroup);

        PersonDAO employee1 = new PersonDAO("John", "Doe", "EMPLOYEE", "john.doe@example.com", "password123");
        PersonDAO employee2 = new PersonDAO("Jane", "Smith", "EMPLOYEE", "jane.smith@example.com", "password456");
        PersonDAO employee3 = new PersonDAO("Michael", "Brown", "EMPLOYEE", "michael.brown@example.com", "password789");
        PersonDAO employee4 = new PersonDAO("Emily", "Davis", "EMPLOYEE", "emily.davis@example.com", "passwordabc");
        PersonDAO employee5 = new PersonDAO("Chris", "Johnson", "EMPLOYEE", "chris.johnson@example.com", "passworddef");
        PersonDAO employee6 = new PersonDAO("Sara", "Wilson", "EMPLOYEE", "sara.wilson@example.com", "passwordghi");
        PersonDAO employee7 = new PersonDAO("Tom", "Anderson", "EMPLOYEE", "tom.anderson@example.com", "passwordjkl");
        PersonDAO employee8 = new PersonDAO("Laura", "Miller", "EMPLOYEE", "laura.miller@example.com", "passwordmno");
        PersonDAO employee9 = new PersonDAO("David", "Garcia", "EMPLOYEE", "david.garcia@example.com", "passwordpqr");
        PersonDAO employee10 = new PersonDAO("Sophia", "Martinez", "EMPLOYEE", "sophia.martinez@example.com", "passwordstu");

        employee1.addToGroup(testingGroup1, developmentGroup, programmingGroup);
        employee2.addToGroup(testingGroup1, developmentGroup);
        employee3.addToGroup(testingGroup1, programmingGroup);
        employee4.addToGroup(developmentGroup, programmingGroup);
        employee5.addToGroup(testingGroup1, developmentGroup);
        employee6.addToGroup(testingGroup2, uiGroup);
        employee7.addToGroup(testingGroup2);
        employee8.addToGroup(uiGroup);
        employee9.addToGroup(uiGroup);
        employee10.addToGroup(testingGroup2, uiGroup);

        persons.addAll(List.of(admin, manager1, manager2,
                employee1,
                employee2,
                employee3,
                employee4,
                employee5,
                employee6,
                employee7,
                employee8,
                employee9,
                employee10
                ));
    }

    public static List<PersonDAO> getPersons() {
        return persons;
    }

    public static List<TaskDAO> getTasks() {

        List<PersonDAO> persons = Objects.getPersons();

        return List.of(
                new TaskDAO("Task1", persons.get(3),
                        manager1, LocalDate.now(), LocalDate.now().minusDays(5), "Do one thing"),
                new TaskDAO("Task2",persons.get(4),
                        manager1, LocalDate.now(), LocalDate.now().minusDays(5), "Do something"),
                new TaskDAO("Task3", persons.get(5),
                        manager1, LocalDate.now(), LocalDate.now().minusDays(5), "Raus"),
                new TaskDAO("Task4", persons.get(6),
                        manager1, LocalDate.now(), LocalDate.now().minusDays(5), "Es regnet"),
                new TaskDAO("Task5", persons.get(7),
                        manager1, LocalDate.now(), LocalDate.now().minusDays(5), "Ich muss int kaufhaus gehen"),
                new TaskDAO("Task6", persons.get(8),
                        manager2, LocalDate.now(), LocalDate.now().minusDays(5), "Ich suche eine roke"),
                new TaskDAO("Task7", persons.get(9),
                        manager1, LocalDate.now(), LocalDate.now().minusDays(5), "Es schneit sehr stark"),
                new TaskDAO("Task8", persons.get(10),
                        manager2, LocalDate.now(), LocalDate.now().minusDays(5), "Do one thing"),
                new TaskDAO("Task9", persons.get(3),
                        manager1, LocalDate.now(), LocalDate.now().minusDays(5), "Do something"),
                new TaskDAO("Task10", persons.get(7),
                        manager2, LocalDate.now(), LocalDate.now().minusDays(5), "Raus"),
                new TaskDAO("Task11", persons.get(8),
                        manager2, LocalDate.now(), LocalDate.now().minusDays(5), "Es regnet"),
                new TaskDAO("Task12", persons.get(9),
                        manager2, LocalDate.now(), LocalDate.now().minusDays(5), "Ich muss int kaufhaus gehen"),
                new TaskDAO("Task13", persons.get(4),
                        manager1, LocalDate.now(), LocalDate.now().minusDays(5), "Ich suche eine roke"),
                new TaskDAO("Task14", persons.get(3),
                        manager1, LocalDate.now(), LocalDate.now().minusDays(5), "Es schneit sehr stark"),
                new TaskDAO("Task1 od admina do mg1", manager1, admin, LocalDate.now(), LocalDate.now().minusDays(3), "Przydziel pracowników"),
                new TaskDAO("Task2 od admina do mg1", manager1, admin, LocalDate.now(), LocalDate.now().minusDays(3), "Zrób testy"),
                new TaskDAO("Task1 od admina do mg2", manager2, admin, LocalDate.now(), LocalDate.now().minusDays(3), "Wdróż pracownika"),
                new TaskDAO("Task2 od admina do mg2", manager2, admin, LocalDate.now(), LocalDate.now().minusDays(3), "Inwentaryzacja"),
                new TaskDAO("Task3 od admina do mg2", manager2, admin, LocalDate.now(), LocalDate.now().minusDays(3), "Sprzątanie")
        );
    }

    private static List<MessageDAO> messages() {
        List<MessageDAO> messages = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();

        // Messages between manager1 and employees in his groups
        messages.add(new MessageDAO(manager1, persons.get(3), "Cześć, jak idzie praca?", now.minusHours(2).minusMinutes(10)));
        messages.add(new MessageDAO(persons.get(3), manager1, "W porządku, robię postępy!", now.minusHours(2)));
        messages.add(new MessageDAO(manager1, persons.get(4), "Musisz dodać kilka testów do projektu.", now.minusHours(1).minusMinutes(45)));
        messages.add(new MessageDAO(persons.get(4), manager1, "Już nad tym pracuję.", now.minusHours(1).minusMinutes(30)));
        messages.add(new MessageDAO(manager1, persons.get(5), "Zakończ implementację do końca tygodnia.", now.minusHours(1).minusMinutes(20)));
        messages.add(new MessageDAO(persons.get(5), manager1, "Tak jest!", now.minusHours(1).minusMinutes(10)));

        // Messages between manager2 and employees in his groups
        messages.add(new MessageDAO(manager2, persons.get(6), "Czy skończyłeś analizę?", now.minusHours(2).minusMinutes(30)));
        messages.add(new MessageDAO(persons.get(6), manager2, "Jeszcze nie, potrzebuję więcej czasu.", now.minusHours(2).minusMinutes(15)));
        messages.add(new MessageDAO(manager2, persons.get(8), "Skonsultuj się z zespołem UI, mają kilka uwag.", now.minusHours(1).minusMinutes(50)));
        messages.add(new MessageDAO(persons.get(8), manager2, "Jasne, już się kontaktuję.", now.minusHours(1).minusMinutes(35)));

        // Messages between employees in manager1's groups
        messages.add(new MessageDAO(persons.get(3), persons.get(4), "Sprawdziłeś kod, który wrzuciłem?", now.minusHours(3).minusMinutes(20)));
        messages.add(new MessageDAO(persons.get(4), persons.get(3), "Tak, wygląda dobrze, ale warto dodać kilka testów.", now.minusHours(3).minusMinutes(5)));
        messages.add(new MessageDAO(persons.get(5), persons.get(3), "Kiedy planujesz zakończyć refaktoryzację?", now.minusHours(2).minusMinutes(50)));
        messages.add(new MessageDAO(persons.get(3), persons.get(5), "Do końca tygodnia.", now.minusHours(2).minusMinutes(30)));

        // Messages between employees in manager2's groups
        messages.add(new MessageDAO(persons.get(6), persons.get(9), "Czy możesz mi pomóc z analizą?", now.minusHours(3).minusMinutes(10)));
        messages.add(new MessageDAO(persons.get(9), persons.get(6), "Oczywiście, przekaż mi dane.", now.minusHours(2).minusMinutes(50)));
        messages.add(new MessageDAO(persons.get(8), persons.get(6), "Przygotuj raport na jutrzejsze spotkanie.", now.minusHours(1).minusMinutes(45)));
        messages.add(new MessageDAO(persons.get(6), persons.get(8), "Zrobione, wysyłam ci teraz.", now.minusHours(1).minusMinutes(30)));



        messages.add(new MessageDAO(manager1, manager2, "Jak idą postępy w waszym projekcie?", now.minusHours(4)));
        messages.add(new MessageDAO(manager2, manager1, "Postępy są dobre, ale potrzebujemy więcej zasobów.", now.minusHours(3).minusMinutes(59)));
        messages.add(new MessageDAO(manager1, manager2, "Czy udało się załatwić te dodatkowe zasoby?", now.minusHours(3).minusMinutes(58)));
        messages.add(new MessageDAO(manager2, manager1, "Tak, zasoby zostały już przydzielone.", now.minusHours(3).minusMinutes(57)));
        messages.add(new MessageDAO(manager1, manager2, "Mamy jakieś nowe problemy na horyzoncie?", now.minusHours(3).minusMinutes(56)));
        messages.add(new MessageDAO(manager2, manager1, "Nie ma nowych problemów, wszystko pod kontrolą.", now.minusHours(3).minusMinutes(55)));
        messages.add(new MessageDAO(manager1, manager2, "Muszę przygotować raport do końca tygodnia.", now.minusHours(3).minusMinutes(54)));
        messages.add(new MessageDAO(manager2, manager1, "Raport będzie gotowy do końca tygodnia.", now.minusHours(3).minusMinutes(53)));
        messages.add(new MessageDAO(manager1, manager2, "Jeśli potrzebujesz pomocy, daj znać.", now.minusHours(3).minusMinutes(52)));
        messages.add(new MessageDAO(manager2, manager1, "Na razie nie potrzebujemy dodatkowej pomocy.", now.minusHours(3).minusMinutes(51)));
        messages.add(new MessageDAO(manager1, manager2, "Spotkanie zespołu zaplanowane na poniedziałek.", now.minusHours(3).minusMinutes(50)));
        messages.add(new MessageDAO(manager2, manager1, "Tak, spotkanie zespołu zostało potwierdzone.", now.minusHours(3).minusMinutes(49)));
        messages.add(new MessageDAO(manager1, manager2, "Pamiętaj o jutrzejszej prezentacji.", now.minusHours(3).minusMinutes(48)));
        messages.add(new MessageDAO(manager2, manager1, "Prezentacja jest gotowa na jutro.", now.minusHours(3).minusMinutes(47)));
        messages.add(new MessageDAO(manager1, manager2, "Czy jesteś gotowy na jutrzejsze spotkanie?", now.minusHours(3).minusMinutes(46)));
        messages.add(new MessageDAO(manager2, manager1, "Wszystko gotowe na jutrzejsze spotkanie.", now.minusHours(3).minusMinutes(45)));
        messages.add(new MessageDAO(manager1, manager2, "Potrzebujemy więcej czasu na ten projekt.", now.minusHours(3).minusMinutes(44)));
        messages.add(new MessageDAO(manager2, manager1, "Projekt powinien zakończyć się zgodnie z planem.", now.minusHours(3).minusMinutes(43)));
        messages.add(new MessageDAO(manager1, manager2, "Upewnij się, że wszyscy wiedzą o deadline.", now.minusHours(3).minusMinutes(42)));
        messages.add(new MessageDAO(manager2, manager1, "Wszyscy są świadomi nadchodzącego deadline.", now.minusHours(3).minusMinutes(41)));
        messages.add(new MessageDAO(manager1, manager2, "Czekam na twoje uwagi do dokumentacji.", now.minusHours(3).minusMinutes(40)));
        messages.add(new MessageDAO(manager2, manager1, "Dokumentacja została przeanalizowana i wygląda dobrze.", now.minusHours(3).minusMinutes(39)));
        messages.add(new MessageDAO(manager1, manager2, "Prześlij mi proszę najnowsze wyniki testów.", now.minusHours(3).minusMinutes(38)));
        messages.add(new MessageDAO(manager2, manager1, "Wyniki testów zostaną przesłane za godzinę.", now.minusHours(3).minusMinutes(37)));
        messages.add(new MessageDAO(manager1, manager2, "W przyszłym tygodniu będzie spotkanie z klientem.", now.minusHours(3).minusMinutes(36)));
        messages.add(new MessageDAO(manager2, manager1, "Klient potwierdził swoje uczestnictwo w spotkaniu.", now.minusHours(3).minusMinutes(35)));
        messages.add(new MessageDAO(manager1, manager2, "Musimy poprawić wydajność systemu.", now.minusHours(3).minusMinutes(34)));
        messages.add(new MessageDAO(manager2, manager1, "Pracujemy nad poprawą wydajności.", now.minusHours(3).minusMinutes(33)));
        messages.add(new MessageDAO(manager1, manager2, "Proszę przekaż mi status prac.", now.minusHours(3).minusMinutes(32)));
        messages.add(new MessageDAO(manager2, manager1, "Status prac został przesłany na maila.", now.minusHours(3).minusMinutes(31)));
        messages.add(new MessageDAO(manager1, manager2, "Jest coś, na co powinniśmy zwrócić uwagę?", now.minusHours(3).minusMinutes(30)));
        messages.add(new MessageDAO(manager2, manager1, "Nic szczególnego nie pojawiło się w nowych wymaganiach.", now.minusHours(3).minusMinutes(29)));
        messages.add(new MessageDAO(manager1, manager2, "Czy sprawdziliście wszystkie nowe wymagania?", now.minusHours(3).minusMinutes(28)));
        messages.add(new MessageDAO(manager2, manager1, "Kończę teraz przegląd, dam znać wkrótce.", now.minusHours(3).minusMinutes(27)));
        messages.add(new MessageDAO(manager1, manager2, "Daj znać, gdy skończysz.", now.minusHours(3).minusMinutes(26)));
        messages.add(new MessageDAO(manager2, manager1, "Termin został potwierdzony.", now.minusHours(3).minusMinutes(25)));
        messages.add(new MessageDAO(manager1, manager2, "Proszę o szybkie potwierdzenie terminu.", now.minusHours(3).minusMinutes(24)));
        messages.add(new MessageDAO(manager2, manager1, "Dziękuję za twoje wsparcie, Mike.", now.minusHours(3).minusMinutes(23)));
        messages.add(new MessageDAO(manager1, manager2, "Dzięki za szybkie odpowiedzi.", now.minusHours(3).minusMinutes(22)));
        messages.add(new MessageDAO(manager2, manager1, "Nie ma sprawy, zawsze chętnie pomogę.", now.minusHours(3).minusMinutes(21 )));

        return messages;
    }

    public static List<MessageDAO> getMessages(PersonDAO sender, PersonDAO receiver) {
        return Objects.messages().stream()
                .filter(msg -> (msg.getSender().equals(sender) && msg.getReceiver().equals(receiver)) ||
                        (msg.getSender().equals(receiver) && msg.getReceiver().equals(sender)))
                .sorted(Comparator.comparing(MessageDAO::getTimestamp))
                .collect(Collectors.toList());
    }

    public static List<NoteDAO> loadNotes() {

        NoteDAO note1 = new NoteDAO("Title1", NoteDAO.NoteType.REGULAR_NOTE, "Siema co tam");
        note1.setCategory(NoteDAO.Category.DIARY);

        NoteDAO note2 = new NoteDAO("Title2", NoteDAO.NoteType.REGULAR_NOTE, "No Elo");
        note2.setCategory(NoteDAO.Category.MEETING);

        NoteDAO note3 = new NoteDAO("Title3", NoteDAO.NoteType.DEADLINE_NOTE, "Testowe");
        note3.setPriority(NoteDAO.Priority.HIGH);
        note3.setDeadline(LocalDate.now().plusDays(3));

        return List.of(
                note1, note2, note3
        );
    }
}