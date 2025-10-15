--Remove existing tables and associated sequences
DROP TABLE Sharing;

DROP TABLE Todos;
DROP SEQUENCE todoIDsequence;

DROP TABLE Noticeboards;
DROP SEQUENCE boardIDsequence;

DROP TABLE Users;
DROP SEQUENCE userIDsequence;

--Create user ID sequence
CREATE SEQUENCE userIDsequence
START WITH 0
MINVALUE 0
INCREMENT BY 1;

--Create Users table and set constraints
CREATE TABLE Users
(
    userID integer PRIMARY KEY NOT NULL DEFAULT nextval('userIDsequence'),
    username character varying(128) NOT NULL,
    password character varying(128) NOT NULL,
  
  CONSTRAINT "uniqueUsername" UNIQUE (username),
  CONSTRAINT "usernameIsValid" CHECK (username ~ '^[A-Za-z0-9\.\_\-]+$')
);


--Create board ID sequence
CREATE SEQUENCE boardIDsequence
START WITH 0
MINVALUE 0
INCREMENT BY 1;

--Create boards table and set constraints
CREATE TABLE Noticeboards
(
    boardID integer PRIMARY KEY NOT NULL DEFAULT nextval('boardIDsequence'),
    boardTitle character varying(128) NOT NULL,
    boardDescription character varying(256),
    userID integer REFERENCES Users(userID) ON DELETE CASCADE NOT NULL,
  
  CONSTRAINT "boardTitleIsNotBlank" CHECK (LENGTH(TRIM(boardTitle)) > 0),
  CONSTRAINT "boardTitleIsValid" CHECK (boardTitle ~ '^[A-Za-z0-9\@\#\&\_\.\-\/ ]+$'),

  CONSTRAINT "uniqueBoardIDandTitle" UNIQUE (userID, boardTitle)
);


--Create todo ID sequence
CREATE SEQUENCE todoIDsequence
START WITH 0
MINVALUE 0
INCREMENT BY 1;

--Create todos table and set constraints
CREATE TABLE ToDos
(
    todoID integer PRIMARY KEY NOT NULL DEFAULT nextval('todoIDsequence'),
    state boolean not null,
    todoTitle character varying(128) NOT NULL,
    todoDescription character varying(256) NOT NULL,
    activityURL character varying(2048) NOT NULL,
    imageURL character varying(2048) NOT NULL,
    expiryDate timestamp,
    ownerUserID integer REFERENCES Users(userID) ON DELETE CASCADE NOT NULL,   
    backgroundColor character(7) NOT NULL DEFAULT '#B6B6B6', -- Default color is Color.GRAY.brighter()

    boardID integer REFERENCES Noticeboards(boardID) ON DELETE CASCADE NOT NULL,
    boardIndex integer NOT NULL,

    CONSTRAINT "todoTitleIsNotBlank" CHECK (LENGTH(TRIM(todoTitle)) > 0),
    CONSTRAINT "todoTitleIsValid" CHECK (todoTitle ~ '^[A-Za-z0-9\@\#\&\_\.\- ]+$'),
    CONSTRAINT "todoColorIsRGBHex" CHECK (backgroundColor ~ '^\#[0-9A-Fa-f]{6}$'),

    CONSTRAINT "uniqueToDoIDandTitle" UNIQUE (boardID, todoTitle)
);

--Create todo sharing table
CREATE TABLE Sharing
(
  userID integer REFERENCES Users(userID) ON DELETE CASCADE NOT NULL,
  todoID integer REFERENCES ToDos(todoID) ON DELETE CASCADE NOT NULL,

  PRIMARY KEY (userID, todoID)
);



--Creating functions and procedures
--Create function to normalize boardIndexes of a board
CREATE OR REPLACE PROCEDURE normalizeBoardIndex(targetBoardID INT) --normalizes all ToDos boardIndex'es
AS $$
  BEGIN
    WITH normalizedRows AS (
      SELECT
        todoID, (ROW_NUMBER() OVER (ORDER BY boardIndex)) - 1 AS newIndex
      FROM ToDos
      WHERE boardID = targetBoardID
    )
    UPDATE ToDos t
    SET boardIndex = n.newIndex
    FROM normalizedRows n
    WHERE t.todoID = n.todoID;
  END;
$$ LANGUAGE plpgsql;

--Create trigger function to normalize boardIndex on remove
CREATE OR REPLACE FUNCTION normalizeIndex() --normalizes all ToDos boardIndex'es
RETURNS TRIGGER AS
$$
  BEGIN
    WITH normalizedRows AS (
      SELECT
        todoID, (ROW_NUMBER() OVER (ORDER BY boardIndex)) - 1 AS newIndex
      FROM ToDos
      WHERE boardID = OLD.boardID
    )
    UPDATE ToDos t
    SET boardIndex = n.newIndex
    FROM normalizedRows n
    WHERE t.todoID = n.todoID;

    RETURN NULL;
  END;
$$ LANGUAGE plpgsql;

--Create procedure to move a ToDo to a new index
CREATE OR REPLACE PROCEDURE moveToDo(p_todoID INT, newIndex INT)
AS $$
DECLARE
  targetBoardID INT;
  oldIndex INT;
BEGIN
    SELECT boardID, boardIndex INTO targetBoardID, oldIndex FROM ToDos WHERE todoID = p_todoID;
    IF oldIndex = newIndex THEN
       RETURN; --Early exit, no need to update
    ELSIF newIndex < oldIndex THEN
        UPDATE ToDos
        SET boardIndex = boardIndex + 1
        WHERE boardID = targetBoardID
          AND todoID <> p_todoID
          AND boardIndex >= newIndex
          AND boardIndex < oldIndex;
    ELSIF newIndex > oldIndex THEN
        UPDATE ToDos
        SET boardIndex = boardIndex - 1
        WHERE boardID = targetBoardID
          AND todoID <> p_todoID
          AND boardIndex > oldIndex
          AND boardIndex <= newIndex;
    END IF;

    -- Moves ToDo to new index
    UPDATE ToDos
    SET boardIndex = newIndex
    WHERE todoID = p_todoID;
END;
$$ LANGUAGE plpgsql;

--Create trigger function to prevent user sharing its own todos
CREATE OR REPLACE FUNCTION preventSelfSharing()
RETURNS TRIGGER AS
$$
  BEGIN
    IF NEW.userID = (SELECT ownerUserID FROM ToDos WHERE todoID = NEW.todoID) THEN
      RAISE EXCEPTION 'An user cannot share its own todos' USING ERRCODE = '23514'; --check violation
    END IF;
    RETURN NEW;
  END;
$$ LANGUAGE plpgsql;

--Create trigger function to add the user's default noticeboards on user creation
CREATE OR REPLACE FUNCTION addDefaultNoticeboards()
RETURNS TRIGGER AS
$$
  BEGIN
    INSERT INTO Noticeboards (boardTitle, boardDescription, userID) VALUES
    ('Universita',   'Attivita ed Hobby da svolgere durante il tempo Libero', NEW.userID),
    ('Lavoro',       'Task da svolgere durante in orario di Lavoro',          NEW.userID),
    ('Tempo Libero', 'Studio, Homework e Progetti.',                          NEW.userID);

    RETURN NEW;
  END;
$$ LANGUAGE plpgsql;



-- Assign trigger functions
--Assign trigger to call addDefaultNoticeboards on user creation
CREATE TRIGGER addDefaultNoticeboardsTrigger
AFTER INSERT ON Users
FOR EACH ROW
EXECUTE FUNCTION addDefaultNoticeboards();

--Assign trigger to call preventSelfSharing trigger function
CREATE TRIGGER preventSelfSharingTrigger
BEFORE INSERT ON Sharing
FOR EACH ROW
EXECUTE FUNCTION preventSelfSharing();

--Assign trigger to call normalizeIndex trigger function
CREATE TRIGGER normalizeIndexTrigger
AFTER DELETE ON ToDos
FOR EACH ROW
EXECUTE FUNCTION normalizeIndex();




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