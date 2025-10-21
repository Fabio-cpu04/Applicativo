-- Insert Users
INSERT INTO Users (username, password) VALUES
                                           ('admin', 'admin'),
                                           ('user1', 'password1'),
                                           ('user2', 'password2');

--Remove trigger-inserted Noticeboards
DELETE FROM Noticeboards;

-- Insert Noticeboards (Gentilmente offerto da un LLM)
INSERT INTO Noticeboards (boardTitle, boardDescription, userID) VALUES
                                                                    ('Progetto Lavoro',    'Task per il lavoro settimanale',     0), --0
                                                                    ('Lista Personale',    'ToDo quotidiani',                    0), --1
                                                                    ('Spesa Mensile',      'Lista spesa di fine mese',           0), --2
                                                                    ('Sviluppo App',       'Feature e bugfix',                   1), --3
                                                                    ('Viaggio Estate',     'Preparativi per vacanza',            1), --4
                                                                    ('Formazione',         'Corsi di aggiornamento',             1), --5
                                                                    ('Casa',               'Manutenzione e faccende domestiche', 2), --6
                                                                    ('Progetti Freelance', 'Clienti e scadenze',                 2), --7
                                                                    ('Sport',              'Allenamenti e gare',                 2); --8

-- Insert ToDos (Gentilmente offerto da un LLM)
INSERT INTO ToDos (state, todoTitle, todoDescription, activityURL, imageURL, expiryDate, ownerUserID, backgroundColor, boardID, boardIndex) VALUES
-- Board 9 (Progetto Lavoro)
(false, 'Preparare Report',          'Scrivere il report settimanale',          'http://example.com/report', '',          '2025-09-01 12:00:00', 0, '#FFFFFF',   9, 0),
(true,  'Inviare Email',             'Mandare email di aggiornamento',          'http://example.com/email', '',           '2025-09-03 12:00:00', 0, '#0e0a0a',   9, 1),
(false, 'Revisionare Codice',        'Controllare codice per bug',              '', '',                                   '2025-09-05 14:00:00', 0, '#B6B6B6',   9, 2),
(false, 'Fare Backup',               'Eseguire backup del progetto',            '', '',                                   '2025-09-07 09:00:00', 0, '#B6B6B6',   9, 3),
-- Board 10 (Lista Personale)
(true,  'Comprare Cibo',             'Acquistare verdure e frutta',             '', '',                                   '2025-09-01 19:30:00', 0, '#FFCC00',   10, 0),
(false, 'Pulire Casa',               'Fare le pulizie settimanali',             '', '',                                   '2025-09-04 09:00:00', 0, '#FFFFFF',   10, 1),
(true,  'Pagare Bollette',           'Pagare luce e gas',                       '', '',                                   '2025-09-08 23:59:59', 0, '#B6B6B6',   10, 2),
-- Board 11 (Spesa Mensile)
(false, 'Assegnare Task',            'Distribuire i task ai membri del team',   '', '',                                   '2025-09-01 20:15:00', 0, '#00FF00',   11, 0),
(true,  'Organizzare Riunione',      'Pianificare meeting',                     'http://meet.example.com/riunione', '',   '2025-09-06 15:00:00', 0, '#8f0000',   11, 1),
(true,  'Caricare Report',           'Upload su server',                        'http://intranet.local/upload', '',       '2025-09-10 11:00:00', 0, '#B6B6B6',   11, 2),
-- Board 12 (Sviluppo App)
(false, 'Aggiornare Documentazione', 'Manuale utente nuova release',            '', '',                                   '2025-09-01 21:00:00', 1, '#99CCFF',   12, 0),
-- Board 13 (Viaggio Estate)
(false, 'Prenotare Hotel',           'Prenotazione per viaggio estivo',         '', '',                                   '2025-09-01 22:00:00', 1, '#00CCCC',   13, 0),
(true,  'Acquistare Biglietti',      'Biglietti aerei Roma->Tokyo',             '', '',                                   '2025-09-09 09:00:00', 1, '#FF9900',   13, 1),
-- Board 15 (Casa)
(true,  'Sistemare Tetto',           'Riparare perdita in mansarda',            '', '',                                   '2025-09-01 23:00:00', 2, '#996633',   15, 0),
-- Board 16 (Progetti Freelance)
(false, 'Contattare Clienti',        'Follow-up su proposte commerciali',       '', '',                                   '2025-09-01 17:30:00', 2, '#99FF99',   16, 0),
-- Board 17 (Sport)
(true,  'Allenamento Corsa',         '10km al parco',                           '', '',                                   '2025-09-01 16:30:00', 2, '#FF66CC',   17, 0),
(false, 'Lezione Yoga',              'Corso di yoga intermedio',                '', '',                                   '2025-09-05 18:00:00', 2, '#66CCFF',   17, 1);


-- Insert Sharing (Gentilmente offerto da un LLM)
INSERT INTO Sharing (userID, todoID) VALUES
                                         (0,10), (0,11), (0,14), (0,15), --admin
                                         (1, 0), (1,1), (1,2),           --user1
                                         (2, 3), (2,4), (2,5);           --user2