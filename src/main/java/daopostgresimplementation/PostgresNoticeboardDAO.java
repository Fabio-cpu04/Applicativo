package daopostgresimplementation;

//Java imports
import java.sql.*;
import java.util.*;

//App imports
import dao.NoticeboardDAO;
import dto.NoticeboardDTO;
import model.Noticeboard;
import model.ToDo;

/**
 * <p>PostgreSQL specific implementation of the {@link NoticeboardDAO} interface.</p>
 */
public class PostgresNoticeboardDAO implements NoticeboardDAO {
    private final Connection connection;

    //Utility SQL state constants
    private static final String VALUE_TOO_LONG_SQL_STATE = "22001";
    private static final String CONSTAINT_CHECK_VIOLATION_SQL_STATE = "23514";
    private static final String DUPLICATE_KEY_VALUE_SQL_STATE = "23505";
    private static final String FOREIGN_KEY_VIOLATION_SQL_STATE = "23503";

    /**
     * <p>Instantiates a new PostgresNoticeboardDAO object.</p>
     * @param con the {@link Connection} to the PostgreSQL database
     */
    public PostgresNoticeboardDAO(Connection con) {
        connection = con;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> getNoticeboardIDsByUserID(int userID) {
        ArrayList<Integer> list = new ArrayList<>();

        try(PreparedStatement getQuery = connection.prepareStatement("SELECT boardID FROM Noticeboards WHERE userID=? ORDER BY boardID")) {
            getQuery.setInt(1, userID);
            ResultSet res = getQuery.executeQuery();

            while(res.next())
                list.add(Integer.valueOf(res.getString(1)));

            return list.stream().toList();
        } catch (SQLException e) {
            //e.printStackTrace();
            return list;
        }
    }

    /**
     * {@inheritDoc}
     */
    public int loadNoticeboardsByUserID(int userID, List<Noticeboard> boards) {
        if (boards == null)
            throw new IllegalArgumentException("The user's board list cannot be null.");

        //Query strings
        final String boardsAndToDosQuery = """
            SELECT
                Noticeboards.boardID, boardTitle, boardDescription,
                todoID, state, todoTitle, todoDescription, activityURL, imageURL, expiryDate, Noticeboards.userID, backgroundColor
            FROM Noticeboards LEFT JOIN Todos ON Noticeboards.boardID = Todos.boardID
            WHERE userID=?
            ORDER BY boardID, boardIndex ASC
        """;
        final String sharingDataQuery = """
            SELECT
                boardID, todoID, username
            FROM Users NATURAL JOIN (Sharing NATURAL JOIN Todos)
            WHERE ownerUserID=?
            ORDER BY boardID, todoID ASC
        """;

        //Load noticeboards
        try (PreparedStatement boardsAndToDosStatement = connection.prepareStatement(boardsAndToDosQuery)) {

            boardsAndToDosStatement.setInt(1, userID);
            ResultSet res = boardsAndToDosStatement.executeQuery();

            TreeMap<Integer, Noticeboard> map = new TreeMap<>();
            while (res.next()) {
                //If boards is not in map, then add to map & boards
                int boardID = res.getInt(1);
                if (!map.containsKey(boardID)) {
                    Noticeboard board = new Noticeboard(boardID, res.getString(2), res.getString(3), res.getInt(11));
                    map.put(boardID, board);
                    boards.add(board);
                }

                //If todo exists (ToDo does not exist if ID is 0 AND title is null, so we have to negate it) add it to board
                int todoID = res.getInt(4);
                String todoTitle = res.getString(6);
                if (todoID != 0 || todoTitle != null) {
                    ToDo todo = new ToDo(todoID, res.getBoolean(5), todoTitle,
                            res.getString(7), res.getString(8), res.getString(9),
                            Optional.ofNullable(res.getTimestamp(10)).map(Timestamp::toLocalDateTime).orElse(null),
                            res.getInt(11), res.getString(12));

                    map.get(boardID).addToDo(todo);
                }
            }

            //Load share data for all ToDos
            try (PreparedStatement sharingDataStatement = connection.prepareStatement(sharingDataQuery)) {
                sharingDataStatement.setInt(1, userID);
                res = sharingDataStatement.executeQuery();

                while (res.next()) {
                    Noticeboard board = map.get(res.getInt(1));
                    board.getToDo(res.getInt(2)).addSharedUser(res.getString(3));
                }
            }

            return 0;
        } catch (SQLException e) {
            //e.printStackTrace();
            return -1;
        }
    }
    /**
     * {@inheritDoc}
     */
    public Noticeboard loadNoticeboardByBoardID(int boardID) {
        //Query strings
        final String boardsAndToDosQuery = """
            SELECT
                boardTitle, boardDescription, userID,
                todoID, state, todoTitle, todoDescription, activityURL, imageURL, expiryDate, backgroundColor
            FROM Noticeboards NATURAL JOIN Todos
            WHERE boardID=?
            ORDER BY boardID, boardIndex ASC
        """;
        final String sharingDataQuery = """
            SELECT
                todoID, username
            FROM Users NATURAL JOIN (Sharing NATURAL JOIN Todos)
            WHERE boardID=?
            ORDER BY boardID, todoID ASC
        """;

        try (PreparedStatement boardsAndToDosStatement = connection.prepareStatement(boardsAndToDosQuery)) {
            boardsAndToDosStatement.setInt(1, boardID);
            ResultSet res = boardsAndToDosStatement.executeQuery();

            Noticeboard board = null;
            while(res.next()) {
                if (board == null)
                    board = new Noticeboard(boardID, res.getString(1), res.getString(2), res.getInt(3));

                ToDo todo = new ToDo(res.getInt(4), res.getBoolean(5), res.getString(6),
                        res.getString(7), res.getString(8), res.getString(9),
                        Optional.ofNullable(res.getTimestamp(10)).map(Timestamp::toLocalDateTime).orElse(null),
                        res.getInt(3), res.getString(11));

                board.addToDo(todo);
            }

            //In case no ToDos are present, load just the Noticeoard's metadata
            if(board == null)
                board = this.getNoticeboardMetadataByBoardID(boardID);
            else { //Load ToDo share data
                try (PreparedStatement sharingDataStatement = connection.prepareStatement(sharingDataQuery)) {
                sharingDataStatement.setInt(1, boardID);
                res = sharingDataStatement.executeQuery();

                while(res.next())
                    board.getToDo(res.getInt(2)).addSharedUser(res.getString(3));
                }
            }

            return board;
        } catch (SQLException e) {
            //e.printStackTrace();
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public int getNoticeboardsMetadataByUserID(int userID, List<Noticeboard> boards) {
        if(boards == null)
            throw new IllegalArgumentException("The user's board list cannot be null.");

        try (PreparedStatement boardsStatement = connection.prepareStatement("SELECT boardID, boardTitle, boardDescription FROM Noticeboards WHERE userID=? ORDER BY boardID")) {
            boardsStatement.setInt(1, userID);
            ResultSet res = boardsStatement.executeQuery();

            while(res.next())
                boards.add(new Noticeboard(res.getInt(1), res.getString(2), res.getString(3), userID));

            return 0;
        } catch (SQLException e) {
            //e.printStackTrace();
            return -1;
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Noticeboard getNoticeboardMetadataByBoardID(int boardID) {
        try(PreparedStatement boardStatement = connection.prepareStatement("SELECT boardTitle, boardDescription FROM Noticeboards WHERE boardID=? ORDER BY boardID")) {
            boardStatement.setInt(1, boardID);
            ResultSet res = boardStatement.executeQuery();

            res.next();

            return new Noticeboard(boardID, res.getString(1), res.getString(2), res.getInt(3));
        } catch (SQLException e) {
            //e.printStackTrace();
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int addNoticeboard(String title, String description, int userID) {
        try(PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO Noticeboards (boardTitle, boardDescription, userID) VALUES (?,?,?)")) {
            insertStatement.setString(1, title);
            insertStatement.setString(2, description);
            insertStatement.setInt(3, userID);
            insertStatement.executeUpdate();

            //Get the new board's ID
            try (PreparedStatement boardIDStatement = connection.prepareStatement("SELECT boardID FROM Noticeboards WHERE boardTitle=? AND userID=?")) {
                boardIDStatement.setString(1, title);
                boardIDStatement.setInt(2, userID);

                ResultSet resSet = boardIDStatement.executeQuery();
                resSet.next();

                return resSet.getInt(1);
            }
        } catch (SQLException e) {
            //e.printStackTrace();
            String code = e.getSQLState();
            switch (code) {
                case VALUE_TOO_LONG_SQL_STATE -> { //username or password exceed 128 chars
                    if(title.length() > 128)
                        throw new IllegalArgumentException("The title cannot exceed 128 characters.");
                    else if(description.length() > 256)
                        throw new IllegalArgumentException("not validThe description cannot exceed 256 characters.");
                }
                case FOREIGN_KEY_VIOLATION_SQL_STATE -> throw new IllegalArgumentException("No user with the ID " + userID + " exists");
                case DUPLICATE_KEY_VALUE_SQL_STATE -> throw new IllegalStateException("A noticeboard with the same title exists already for this user");
                case CONSTAINT_CHECK_VIOLATION_SQL_STATE -> {
                    if (title.isBlank())
                        throw new IllegalArgumentException("Noticeboard title cannot be blank");
                    else
                        throw new IllegalArgumentException("Noticeboard title is not valid.");
                }
                default -> throw new UnknownPostgresException(e);
            }
            return -1;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateNoticeboard(NoticeboardDTO board) {
        try(PreparedStatement updateStatement = connection.prepareStatement("UPDATE Noticeboards SET boardTitle=?, boardDescription=? WHERE boardID=?")) {
            updateStatement.setString(1, board.getTitle());
            updateStatement.setString(2, board.getDescription());
            updateStatement.setInt(3, board.getBoardID());
            int res = updateStatement.executeUpdate();

            if(res == 0)
                throw new NoSuchElementException("The target noticeboard (ID: " + board.getBoardID() + ") does not exist");
        } catch (SQLException e) {
            //e.printStackTrace();
            String code = e.getSQLState();
            switch (code) {
                case VALUE_TOO_LONG_SQL_STATE -> { //username or password exceed 128 chars
                    if (board.getTitle().length() > 128)
                        throw new IllegalArgumentException("The title cannot exceed 128 characters.");
                    else if (board.getDescription().length() > 256)
                        throw new IllegalArgumentException("The description cannot exceed 256 characters.");
                }
                case DUPLICATE_KEY_VALUE_SQL_STATE -> throw new IllegalStateException("A noticeboard with the same title exists already for this user");
                case CONSTAINT_CHECK_VIOLATION_SQL_STATE -> {
                    if (board.getTitle().isBlank())
                        throw new IllegalArgumentException("Noticeboard title cannot be blank");
                    else
                        throw new IllegalArgumentException("Noticeboard title is not valid.");
                }
                default -> throw new UnknownPostgresException(e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateNoticeboardTitle(String newTitle, int boardID) {
        try(PreparedStatement updateStatement = connection.prepareStatement("UPDATE Noticeboards SET boardTitle=? WHERE boardID=?")) {
            updateStatement.setString(1, newTitle);
            updateStatement.setInt(2, boardID);
            int res = updateStatement.executeUpdate();

            if(res == 0)
                throw new NoSuchElementException("The target noticeboard (ID: " + boardID + ") does not exist");
        } catch (SQLException e) {
            //e.printStackTrace();
            String code = e.getSQLState();
            switch (code) {
                case VALUE_TOO_LONG_SQL_STATE -> throw new IllegalArgumentException("The title cannot exceed 128 characters.");
                case DUPLICATE_KEY_VALUE_SQL_STATE -> throw new IllegalStateException("A noticeboard with the same title exists already for this user");
                case CONSTAINT_CHECK_VIOLATION_SQL_STATE -> {
                    if (newTitle.isBlank())
                        throw new IllegalArgumentException("Noticeboard title cannot be blank");
                    else
                        throw new IllegalArgumentException("Noticeboard title is not valid.");
                }
                default -> throw new UnknownPostgresException(e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateNoticeboardDescription(String newDescription, int boardID) {
        try(PreparedStatement updateStatement = connection.prepareStatement("UPDATE Noticeboards SET boardDescription=? WHERE boardID=?")) {
            updateStatement.setString(1, newDescription);
            updateStatement.setInt(2, boardID);
            int res = updateStatement.executeUpdate();

            if(res == 0)
                throw new NoSuchElementException("The target noticeboard (ID: " + boardID + ") does not exist");
        } catch (SQLException e) {
            //e.printStackTrace();
            if(e.getSQLState().equals(VALUE_TOO_LONG_SQL_STATE))
                throw new IllegalArgumentException("The description cannot exceed 256 characters.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeNoticeboard(int boardID) {
        try(PreparedStatement removeStatement = connection.prepareStatement("DELETE FROM Noticeboards WHERE boardID=?")) {
            removeStatement.setInt(1, boardID);
            int res = removeStatement.executeUpdate();

            if(res == 0)
                throw new NoSuchElementException("The target noticeboard (ID: " + boardID + ") does not exist");
        } catch (SQLException e) {
            //e.printStackTrace();
        }
    }
}