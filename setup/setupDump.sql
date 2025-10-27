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
    backgroundColor character(7) NOT NULL DEFAULT '#FFFFFF',

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