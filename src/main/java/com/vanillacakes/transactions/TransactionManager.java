package com.vanillacakes.transactions;

import com.vanillacakes.ConnectionFactory;

import java.sql.Connection;

/**
 * Executes the callback inside a transaction.
 * Commits on success and rolls back on failure.
 */
public class TransactionManager {
    private final ConnectionFactory connectionFactory;

    public TransactionManager(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public <T> T execute(TransactionCallback<T> callback) {
        try (Connection connection = connectionFactory.create()) {
            connection.setAutoCommit(false);

            try {
                T result = callback.execute(connection);
                connection.commit();
                return result;
            } catch (Exception e) {
                connection.rollback();
                throw e;
            }

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
