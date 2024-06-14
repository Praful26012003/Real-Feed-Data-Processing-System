package com.praful.feedapplication.dao;

public class SQLHandler {
    public static final String INSERT_ASSET_SPOT_PRICE = "INSERT IGNORE INTO spotPrice (date, metal, currency, weight_unit, ask, mid, bid, value, performance) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String INSERT_LAST_DAY_ASSET_DETAIL = "INSERT IGNORE INTO lastHistoricalSpotPrice (date, metal, currency, weight_unit, close, high, low, open) values (?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String FETCH_ASSET_SPOT_PRICE = "SELECT date, weight_unit, ask, mid, bid, value, performance FROM spotPrice ORDER BY date DESC LIMIT 1";
    public static final String FETCH_BETWEEN_TIMESTAMP_PAGINATED_ASSET_SPOT_PRICES = "SELECT date, ask, mid, bid, value, performance FROM spotPrice WHERE date BETWEEN ? and ? LIMIT ?";
    public static final String FETCH_FORWARD_PAGINATED_ASSET_SPOT_PRICES = "SELECT date, ask, mid, bid, value, performance FROM spotPrice WHERE date > ? and date <= ? LIMIT ?";
    public static final String FETCH_REVERSE_PAGINATED_ASSET_SPOT_PRICES = "SELECT date, ask, mid, bid, value, performance FROM spotPrice WHERE date >= ? and date < ? ORDER BY date DESC LIMIT ?";
    public static final String INSERT_USER = "INSERT INTO users (username, password) VALUES (?, ?)";
    public static final String INSERT_USER_ASSET = "INSERT INTO ledger (username, gold, amount) VALUES (?, ?, ?)";
    public static final String FETCH_USER = "SELECT username, password FROM users WHERE username = ?";
    public static final String FETCH_USER_ASSET_DETAIL = "SELECT ledger_id, gold, amount, username FROM ledger WHERE username = ?";
    public static final String REMOVE_USER = "DELETE FROM users WHERE username = ?";
    public static final String INSERT_TRANSACTION = "INSERT INTO transaction_table (transaction_id, username, gold, amount, mode, date, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
    public static final String FETCH_TRANSACTION_BY_TRANSACTION_ID = "SELECT transaction_id, update_ts, username, gold, amount, mode, status FROM transaction_table WHERE transaction_id = ?";
    public static final String FETCH_TRANSACTION_BY_STATUS = "SELECT transaction_id, update_ts, username, gold, amount, mode, status FROM transaction_table WHERE status = ? and create_ts <= NOW() - INTERVAL 5 MINUTE";
    public static final String FETCH_USER_TRANSACTIONS = "SELECT transaction_id, username, gold, amount, mode, update_ts, status FROM transaction_table WHERE username = ? ORDER BY transaction_id DESC";
    public static final String UPDATE_TRANSACTION_STATUS = "UPDATE transaction_table SET status = ? WHERE transaction_id = ?";
    public static final String UPDATE_USER_AMOUNT_BALANCE = "UPDATE ledger SET amount = ? WHERE username = ?";
    public static final String UPDATE_USER_GOLD_BALANCE = "UPDATE ledger SET gold = ? WHERE username = ?";
}
