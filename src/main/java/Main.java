import controller.Controller;
import gui.GUI;
import model.Noticeboard;
import model.ToDo;
import model.User;

import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        Controller ctrl = Controller.get();

        //Creating and logging user
        User u1 = new User("user1", "passwordsicura1");
        ctrl.setLoggedUser(u1);

        //Creating and adding noticeboards
        Noticeboard freetime = new Noticeboard("Tempo libero","Attivita ed Hobby da svolgere durante il tempo Libero");
        Noticeboard work = new Noticeboard("Lavoro","Task da svolgere durante in orario di Lavoro");
        Noticeboard uni = new Noticeboard("Universita","Studio, Homework e Progetti.");

        Noticeboard travel = new Noticeboard("Viaggi", "Idee, piani e appunti per viaggi futuri o passati");
        Noticeboard health = new Noticeboard("Salute e Benessere", "Promemoria per attività fisica, visite mediche e alimentazione");

        u1.addNoticeboard(freetime);
        u1.addNoticeboard(work);
        u1.addNoticeboard(uni);

        u1.addNoticeboard(travel);
        u1.addNoticeboard(health);

        //Creating and adding ToDos
        LocalDateTime now = LocalDateTime.parse("2025-08-11T11:25");

        ToDo t1 = new ToDo("Passeggiata al parco", "Rilassati con una camminata al parco dopo cena.", "https://maps.app.goo.gl/parco", "", now.plusDays(1), u1.getUsername(), "#A8DADC");
        ToDo t2 = new ToDo("Guarda un film", "Serata cinema con amici: scegli tra i film in uscita!", "https://www.netflix.com", "", now.plusHours(6), u1.getUsername(), "#B8E1DD");
        ToDo t3 = new ToDo("Leggi un libro", "Continua la lettura di '1984' di Orwell.", "https://www.goodreads.com/book/show/5470.1984", "", now.plusDays(3), u1.getUsername(), "#A8DADC");

        ToDo t4 = new ToDo("Scrivere report settimanale", "Compila il report con le attività completate.", "https://company-internal.com/report", "", now.plusDays(1), u1.getUsername(), "#457B9D");
        ToDo t5 = new ToDo("Meeting con il cliente", "Incontro per la presentazione del progetto Alpha.", "https://zoom.us/j/meeting123", "", now.plusHours(4), u1.getUsername(), "#1D3557");

        ToDo t6 = new ToDo("Studia per l'esame di Reti", "Rivedi gli appunti e fai gli esercizi del modulo 3.", "https://elearning.university.edu/reti", "", now.plusDays(2), u1.getUsername(), "#FCA311");
        ToDo t7 = new ToDo("Consegna progetto di Basi di Dati", "Deadline del progetto: carica su Moodle.", "https://moodle.university.edu", "", now.plusDays(1).plusHours(3), u1.getUsername(), "#FFE66D");
        ToDo t8 = new ToDo("Lezione di Intelligenza Artificiale", "Segui la lezione online e prendi appunti.", "https://teams.microsoft.com/lessonIA", "", now.plusHours(2), u1.getUsername(), "#FCA311");
        ToDo t9 = new ToDo("Prenota ricevimento con il prof", "Contatta il docente per chiarimenti sul progetto.", "https://university.edu/docenti", "", now.plusDays(4), u1.getUsername(), "#FFE66D");

        ToDo t10 = new ToDo("Organizza weekend a Venezia", "Prenota hotel e controlla gli orari dei treni per il fine settimana.", "https://www.trenitalia.com", "", now.plusDays(5), u1.getUsername(), "#D8E2DC");
        ToDo t11 = new ToDo("Prepara valigia per il viaggio", "Fai una lista e prepara lo zaino con tutto il necessario.", "https://example.com/checklist-viaggio.pdf", "", now.plusDays(4).plusHours(2), u1.getUsername(), "#FFCAD4");

        ToDo t12 = new ToDo("Allenamento mattutino", "Sessione di stretching e yoga per iniziare bene la giornata.", "https://www.youtube.com/watch?v=v7AYKMP6rOE", "", now.plusHours(10), u1.getUsername(), "#CDEAC0");
        ToDo t13 = new ToDo("Visita di controllo dal medico", "Controllo annuale dal medico di base – porta gli esami del sangue.", "https://salute.gov.it", "", now.plusDays(7), u1.getUsername(), "#BFD8B8");


        freetime.addToDo(t1);
        freetime.addToDo(t2);
        freetime.addToDo(t3);

        work.addToDo(t4);
        work.addToDo(t5);

        uni.addToDo(t6);
        uni.addToDo(t7);
        uni.addToDo(t8);
        uni.addToDo(t9);

        travel.addToDo(t10);
        travel.addToDo(t11);

        health.addToDo(t12);
        health.addToDo(t13);

        //TEMPORARILY INCLUDES A Controller PARAMETER, THIS IS A REPLACEMENT FOR A DATABASE CONNECTION
        GUI gui = new GUI();
    }
}
