package daopostgresimplementation;

import java.sql.SQLException;

/**
 * Used to signal that an unexpected SQLError exception arised from an invoked method.
 */
class UnknownPostgresException extends RuntimeException {
    public UnknownPostgresException(SQLException cause) {
        super("SQL Error, code: " + cause.getSQLState());
    }
}
