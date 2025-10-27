package daopostgresimplementation;

//Java imports
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.NoSuchElementException;
import java.sql.*;

//App imports
import dao.ToDoDAO;
import dto.ToDoDTO;
import model.ToDo;

/**
 * <p>PostgreSQL specific implementation of the {@link ToDoDAO} interface.</p>
 */
public class PostgresToDoDAO implements ToDoDAO {
    private final Connection connection;

    //Utility SQL state constants
    private static final String VALUE_TOO_LONG_SQL_STATE = "22001";
    private static final String CONSTAINT_CHECK_VIOLATION_SQL_STATE = "23514";
    private static final String DUPLICATE_KEY_VALUE_SQL_STATE = "23505";
    private static final String FOREIGN_KEY_VIOLATION_SQL_STATE = "23503";


    /**
     * Instantiates a new PostgresNoticeboardDAO object.
     * @param con the connection to the PostgreSQL database
     */
    public PostgresToDoDAO(Connection con) {
        connection = con;
    }

    /**
     * {@inheritDoc}
     */
    public List<Integer> getToDoListByBoardID(int boardID) {
        ArrayList<Integer> list = new ArrayList<>();

        try(PreparedStatement getStatement = connection.prepareStatement("SELECT userID FROM Todos WHERE boardID=?")) {
            getStatement.setInt(1, boardID);

            ResultSet res = getStatement.executeQuery();

            while(res.next())
                list.add(Integer.valueOf(res.getString(1)));

            return list.stream().toList();
        } catch (SQLException e) {
            //e.printStackTrace();
            return list.stream().toList();
        }
    }

    /**
     * {@inheritDoc}
     */
    public int loadToDosByBoardID(int boardID, List<ToDo> todos) {
        if(todos == null)
            throw new IllegalArgumentException("The board's todo list cannot be null.");

        try(PreparedStatement loadToDosStatement = connection.prepareStatement("SELECT todoID, state, todoTitle, todoDescription, activityURL, imageURL, expiryDate, ownerUserID, backgroundColor, boardIndex FROM Todos WHERE boardID=? ORDER BY boardIndex ASC")) {
            loadToDosStatement.setInt(1, boardID);

            ResultSet res = loadToDosStatement.executeQuery();
            while(res.next())
                todos.add(new ToDo(res.getInt(1), res.getBoolean(2), res.getString(3), res.getString(4), res.getString(5),
                        res.getString(6), Optional.ofNullable(res.getTimestamp(7)).map(Timestamp::toLocalDateTime).orElse(null), res.getInt(8), res.getString(9)));

            return 0;
        } catch (SQLException e) {
            //e.printStackTrace();
            return -1;
        }
    }
    /**
     * {@inheritDoc}
     */
    public ToDo loadToDoByToDoID(int todoID) {
        try(PreparedStatement loadToDoStatement = connection.prepareStatement("SELECT state, todoTitle, todoDescription, activityURL, imageURL, expiryDate, ownerUserID, backgroundColor FROM Todos WHERE todoID=?")) {
            loadToDoStatement.setInt(1, todoID);

            ResultSet res = loadToDoStatement.executeQuery();
            res.next();

            return new ToDo(todoID, res.getBoolean(1), res.getString(2), res.getString(3), res.getString(4),
                    res.getString(5), res.getTimestamp(6).toLocalDateTime(), res.getInt(7), res.getString(8));
        } catch (SQLException e) {
            //e.printStackTrace();
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public int addToDo(ToDoDTO todo, int boardID, int boardIndex) {
        try(PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO Todos (state, todoTitle, todoDescription, activityURL, imageURL, expiryDate, ownerUserID, backgroundColor, boardID, boardIndex) VALUES (?,?,?,?,?,?,?,?,?,?)")) {
            insertStatement.setBoolean(1, todo.isCompleted());
            insertStatement.setString(2, todo.getTitle());
            insertStatement.setString(3, todo.getDescription());
            insertStatement.setString(4, todo.getActivityURL());
            insertStatement.setString(5, todo.getImageURL());
            if(todo.getExpiryDate() != null)
                insertStatement.setTimestamp(6, Timestamp.valueOf(todo.getExpiryDate()));
            else
                insertStatement.setNull(6, java.sql.Types.TIMESTAMP);
            insertStatement.setInt(7, todo.getOwnerUserID());
            insertStatement.setString(8, todo.getBackgroundColor());
            insertStatement.setInt(9, boardID);
            insertStatement.setInt(10, boardIndex);

            insertStatement.executeUpdate();

            //Get the new board's ID
            try (PreparedStatement boardIDStatement = connection.prepareStatement("SELECT todoID FROM Todos WHERE todoTitle=? AND boardID=?")) {
                boardIDStatement.setString(1, todo.getTitle());
                boardIDStatement.setInt(2, boardID);

                ResultSet resSet = boardIDStatement.executeQuery();
                resSet.next();

                return resSet.getInt(1);
            }
        } catch (SQLException e) {
            //e.printStackTrace();
            String code = e.getSQLState();
            switch (code) {
                case VALUE_TOO_LONG_SQL_STATE -> throw new IllegalArgumentException("The title cannot be longer than 128 characters.");
                case FOREIGN_KEY_VIOLATION_SQL_STATE -> throw new IllegalArgumentException("No board with the ID " + boardID + " or user with the id " + todo.getOwnerUserID() + " exists");
                case DUPLICATE_KEY_VALUE_SQL_STATE -> throw new IllegalStateException("A ToDo with the same title exists already for this board");
                case CONSTAINT_CHECK_VIOLATION_SQL_STATE -> {
                    if(!todo.getBackgroundColor().matches("^#[0-9A-Fa-f]{6}$"))
                        throw new IllegalArgumentException("ToDo background color is not valid, must be in the following format \"#rrggbb\"");

                    if (todo.getTitle().isBlank())
                        throw new IllegalArgumentException("ToDo title cannot be blank");
                    else
                        throw new IllegalArgumentException("ToDo title is not valid.");
                }
                default -> { return -1; }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateToDoCompletionState(boolean isCompleted, int todoID) {
        try(PreparedStatement updateStatement = connection.prepareStatement("UPDATE Todos SET state=? WHERE todoID=?")) {
            updateStatement.setBoolean(1, isCompleted);
            updateStatement.setInt(2, todoID);
            int res = updateStatement.executeUpdate();

            if(res == 0)
                throw new NoSuchElementException("The target todo (ID: " + todoID + ") does not exist");
        } catch (SQLException e) {
            //e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateToDoTitle(String newTitle, int todoID) {
        try(PreparedStatement updateStatement = connection.prepareStatement("UPDATE Todos SET todoTitle=? WHERE todoID=?")) {
            updateStatement.setString(1, newTitle);
            updateStatement.setInt(2, todoID);
            int res = updateStatement.executeUpdate();

            if(res == 0)
                throw new NoSuchElementException("The target todo (ID: " + todoID + ") does not exist");
        } catch (SQLException e) {
            //e.printStackTrace();
            String code = e.getSQLState();
            switch (code) {
                case VALUE_TOO_LONG_SQL_STATE -> throw new IllegalArgumentException("The title cannot be longer than 128 characters.");
                case DUPLICATE_KEY_VALUE_SQL_STATE -> throw new IllegalStateException("A todo with the same title exists already for this board");
                case CONSTAINT_CHECK_VIOLATION_SQL_STATE -> {
                    if (newTitle.isBlank())
                        throw new IllegalArgumentException("ToDo title cannot be blank");
                    else
                        throw new IllegalArgumentException("ToDo title is not valid.");
                }
                default -> throw new UnknownPostgresException(e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateToDoDescription(String newDescription, int todoID) {
        try(PreparedStatement updateStatement = connection.prepareStatement("UPDATE Todos SET todoDescription=? WHERE todoID=?")) {
            updateStatement.setString(1, newDescription);
            updateStatement.setInt(2, todoID);
            int res = updateStatement.executeUpdate();

            if(res == 0)
                throw new NoSuchElementException("The target todo (ID: " + todoID + ") does not exist");
        } catch (SQLException e) {
            //e.printStackTrace();
            String code = e.getSQLState();
            if(code.equals(VALUE_TOO_LONG_SQL_STATE))
                throw new IllegalArgumentException("ToDo description cannot be longer than 256 characters.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateToDoActivityURL(String newURL, int todoID) {
        try(PreparedStatement updateStatement = connection.prepareStatement("UPDATE Todos SET activityURL=? WHERE todoID=?")) {
            updateStatement.setString(1, newURL);
            updateStatement.setInt(2, todoID);
            int res = updateStatement.executeUpdate();

            if(res == 0)
                throw new NoSuchElementException("The target todo (ID: " + todoID + ") does not exist");
        } catch (SQLException e) {
            //e.printStackTrace();
            String code = e.getSQLState();
            if(code.equals(VALUE_TOO_LONG_SQL_STATE))
                throw new IllegalArgumentException("ToDo activity URL cannot be longer than 256 characters.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateToDoImageURL(String newURL, int todoID) {
        try(PreparedStatement updateStatement = connection.prepareStatement("UPDATE Todos SET imageURL=? WHERE todoID=?")) {
            updateStatement.setString(1, newURL);
            updateStatement.setInt(2, todoID);
            int res = updateStatement.executeUpdate();

            if(res == 0)
                throw new NoSuchElementException("The target todo (ID: " + todoID + ") does not exist");
        } catch (SQLException e) {
            //e.printStackTrace();
            String code = e.getSQLState();
            if(code.equals(VALUE_TOO_LONG_SQL_STATE))
                throw new IllegalArgumentException("ToDo image URL cannot be longer than 256 characters.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateToDoExpiryDate(LocalDateTime newDate, int todoID) {
        try(PreparedStatement updateStatement = connection.prepareStatement("UPDATE Todos SET expiryDate=? WHERE todoID=?")) {
            if(newDate != null)
                updateStatement.setTimestamp(1, Timestamp.valueOf(newDate));
            else
                updateStatement.setNull(1, Types.TIMESTAMP);

            updateStatement.setInt(2, todoID);
            int res = updateStatement.executeUpdate();

            if(res == 0)
                throw new NoSuchElementException("The target todo (ID: " + todoID + ") does not exist");
        } catch (SQLException e) {
            //e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateToDoBackgroundColor(String newColor, int todoID) {
        try(PreparedStatement updateStatement = connection.prepareStatement("UPDATE Todos SET backgroundColor=? WHERE todoID=?")) {
            updateStatement.setString(1, newColor);
            updateStatement.setInt(2, todoID);
            int res = updateStatement.executeUpdate();

            if(res == 0)
                throw new NoSuchElementException("The target todo (ID: " + todoID + ") does not exist");
        } catch (SQLException e) {
            //e.printStackTrace();
            String code = e.getSQLState();
            switch (code){
                case VALUE_TOO_LONG_SQL_STATE -> throw new IllegalArgumentException("ToDo background color cannot be longer than 7 characters.");
                case CONSTAINT_CHECK_VIOLATION_SQL_STATE -> throw new IllegalArgumentException("ToDo background color is not valid, must be in the following format \"#RRGGBB\"");
                default -> throw new UnknownPostgresException(e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void moveToDoToIndex(int todoID, int newIndex) {
        try(PreparedStatement moveQuery = connection.prepareStatement("CALL moveToDo(?, ?)")) {
            moveQuery.setInt(1, todoID);
            moveQuery.setInt(2, newIndex);

            moveQuery.executeUpdate();
        } catch (SQLException e) {
            //e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void moveToDoToBoard(int todoID, int oldBoardID, int newBoardID) {
        try {
            if (oldBoardID == newBoardID)
                return;

            //Set auto commit to false in order to avoid cataclysm
            connection.setAutoCommit(false);

            final String moveQuery = """
                UPDATE ToDos SET boardID = ?, boardIndex =
                (SELECT COUNT(todoID) FROM Todos WHERE boardID = ?)
                WHERE todoID = ?
            """;

            //Modify ToDo board and index
            try (PreparedStatement moveStatement = connection.prepareStatement(moveQuery)) {
                moveStatement.setInt(1, newBoardID);
                moveStatement.setInt(2, newBoardID);
                moveStatement.setInt(3, todoID);

                int res = moveStatement.executeUpdate();
                if (res == 0)
                    throw new NoSuchElementException("The target todo (ID: " + todoID + ") does not exist");

                //Normalize old board index to fill in the hole
                try (PreparedStatement normalizeStatement = connection.prepareStatement("CALL normalizeBoardIndex(?)")) {
                    normalizeStatement.setInt(1, oldBoardID);
                normalizeStatement.executeUpdate();

                connection.commit();
                }
            }
        } catch (SQLException e) {
            if(e.getSQLState().equals(DUPLICATE_KEY_VALUE_SQL_STATE))
                throw new IllegalStateException("You can't move a ToDo to a Noticeboard where a ToDo with the same title exists.");

            try {
                connection.rollback();
            }
            catch (SQLException e1) {
                //e1.printStackTrace();
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            }
            catch (SQLException e1) {
                //e1.printStackTrace();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeToDo(int todoID) {
        try(PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM Todos WHERE todoID=?")) {
            deleteStatement.setInt(1, todoID);

            int res = deleteStatement.executeUpdate();
            if(res == 0)
                throw new NoSuchElementException("The target ToDo (ID: " + todoID + ") does not exist");
        } catch (SQLException e) {
            //e.printStackTrace();
        }
    }
}