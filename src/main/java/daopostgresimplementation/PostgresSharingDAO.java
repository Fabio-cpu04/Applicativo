package daopostgresimplementation;

//Java imports
import java.util.*;
import java.sql.*;

//App imports
import dao.SharingDAO;
import model.Noticeboard;
import model.ToDo;

/**
 * <p>PostgreSQL specific implementation of the {@link SharingDAO} interface.</p>
 */
public class PostgresSharingDAO implements SharingDAO {
    private final Connection connection;

    //Utility SQL state constants
    private static final String CONSTAINT_CHECK_VIOLATION_SQL_STATE = "23514";
    private static final String DUPLICATE_KEY_VALUE_SQL_STATE = "23505";
    private static final String FOREIGN_KEY_VIOLATION_SQL_STATE = "23503";

    /**
     * Instantiates a new PostgresSharingDAO object.
     * @param con the connection to the PostgreSQL database
     */
    public PostgresSharingDAO(Connection con) {
        connection = con;
    }

    /**
     * {@inheritDoc}
     */
    public int loadSharedNoticeboardsByUserID(int userID, List<Noticeboard> boards) {
        if(boards == null)
            throw new IllegalArgumentException("The user's board list cannot be null.");

        //Query strings
        final String boardsAndToDosQuery = """
            SELECT
                Noticeboards.boardID, boardTitle, boardDescription,
                todoID, state, todoTitle, todoDescription, activityURL, imageURL, expiryDate, ownerUserID, backgroundColor
            FROM Noticeboards LEFT JOIN (Sharing NATURAL JOIN Todos) ON Noticeboards.boardID = Todos.boardID
            WHERE Sharing.userID=?
            ORDER BY boardID, boardIndex ASC
            """;
        final String sharingDataQuery = """
            SELECT
                boardID, todoID, username
            FROM (Users NATURAL JOIN Sharing) NATURAL JOIN Todos
            WHERE userID=?
            ORDER BY boardID, todoID ASC
            """;

        try (PreparedStatement boardsAndToDosStatements = connection.prepareStatement(boardsAndToDosQuery)) {
            boardsAndToDosStatements.setInt(1, userID);
            ResultSet res = boardsAndToDosStatements.executeQuery();

            TreeMap<Integer, Noticeboard> map = new TreeMap<>();
            while(res.next()) {
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
                if(todoID != 0 || todoTitle != null){
                    ToDo todo = new ToDo(todoID, res.getBoolean(5), todoTitle,
                            res.getString(7), res.getString(8), res.getString(9),
                            Optional.ofNullable(res.getTimestamp(10)).map(Timestamp::toLocalDateTime).orElse(null),
                            res.getInt(11), res.getString(12));

                    map.get(boardID).addToDo(todo);
                }
            }

            //Load ToDo share data
            try (PreparedStatement sharingDataStatement = connection.prepareStatement(sharingDataQuery)){
                sharingDataStatement.setInt(1, userID);
                res = sharingDataStatement.executeQuery();
                while (res.next())
                    map.get(res.getInt(1)).getToDo(res.getInt(2)).addSharedUser(res.getString(3));

                return 0;
            }
        } catch (SQLException _) {
            return -1;
        }
    }
    /**
     * {@inheritDoc}
     */
    public int getSharedNoticeboardsMetadataByUserID(int userID, List<Noticeboard> boards) {
        if(boards == null)
            throw new IllegalArgumentException("The user's board list cannot be null.");

        //Query strings
        final String boardsDataQuery = """
            SELECT Noticeboards.boardID, boardTitle, boardDescription, Noticeboards.userID
            FROM Noticeboards NATURAL JOIN
                (SELECT DISTINCT boardID
                FROM (Sharing NATURAL JOIN Todos)
                WHERE userID=?
                ORDER BY boardID ASC)
            """;
        try (PreparedStatement boardsDataStatement = connection.prepareStatement(boardsDataQuery)) {

            boardsDataStatement.setInt(1, userID);
            ResultSet res = boardsDataStatement.executeQuery();

            while (res.next())
                boards.add(new Noticeboard(res.getInt(1), res.getString(2), res.getString(3), res.getInt(4)));

            return 0;
        } catch (SQLException _) {
            return -1;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void shareToDo(int userID, int todoID) {
        try(PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO Sharing (userID, todoID) VALUES (?,?)")) {
            insertStatement.setInt(1, userID);
            insertStatement.setInt(2, todoID);

            insertStatement.executeUpdate();
        } catch (SQLException e) {
            String code = e.getSQLState();
            switch (code) {
                case FOREIGN_KEY_VIOLATION_SQL_STATE -> throw new NoSuchElementException("No user with the ID " + userID + " or ToDo with the ID "+ todoID + " exists");
                case DUPLICATE_KEY_VALUE_SQL_STATE -> throw new IllegalStateException("User (ID:" + userID + ") already shares ToDo (ID:" + todoID + ")");
                case CONSTAINT_CHECK_VIOLATION_SQL_STATE -> throw new IllegalArgumentException("A user cannot share a ToDo with itself, the todo (ID: " + todoID + ") is owned by user (ID: " + userID + ")"); //Trigger
                default -> throw new UnknownPostgresException(e);
            }
        }
    }
    /**
     * {@inheritDoc}
     */
    public void unshareToDo(int userID, int todoID) {
        try(PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM Sharing WHERE userID=? AND todoID=?")) {
            deleteStatement.setInt(1, userID);
            deleteStatement.setInt(2, todoID);

            int res = deleteStatement.executeUpdate();
            if(res == 0)
                throw new IllegalStateException("User (ID:" + userID + ") does not exist or ToDo (ID:" + todoID + ") is not shared with the User");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
