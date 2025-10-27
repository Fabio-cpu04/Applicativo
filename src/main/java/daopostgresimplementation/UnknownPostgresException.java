package daopostgresimplementation;

import java.sql.SQLException;

/**
 * <p>Used to signal that an unexpected SQLError exception arised from an invoked method.</p>
 */
class UnknownPostgresException extends RuntimeException {
    /**
     * <p>Instantiates a new UnknownPostgresException with attached the SQL state.</p>
     * @param cause the SQLException that caused the UnknownPostgresException
     */
    public UnknownPostgresException(SQLException cause) {
        super("SQL Error, code: " + cause.getSQLState());
    }
}
