package com.icbt.dao;

import com.icbt.model.BillingSummaryRow;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class ReportDAO {
    public List<BillingSummaryRow> billingSummary(LocalDate from, LocalDate to) {
        String sql = """
                SELECT t.name AS treatment_name, COUNT(b.bill_id) AS bill_count,
                       COALESCE(SUM(b.total_amount), 0) AS total_amount
                FROM bill b
                JOIN appointment a ON a.appointment_id = b.appointment_id
                JOIN treatment_type t ON t.treatment_type_id = a.treatment_type_id
                WHERE DATE(b.generated_at) BETWEEN ? AND ?
                GROUP BY t.treatment_type_id, t.name
                ORDER BY t.name
                """;
        List<BillingSummaryRow> rows = new ArrayList<>();
        try (Connection connection = DBConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, Date.valueOf(from));
            statement.setDate(2, Date.valueOf(to));
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    rows.add(new BillingSummaryRow(resultSet.getString("treatment_name"),
                            resultSet.getLong("bill_count"), resultSet.getBigDecimal("total_amount")));
                }
            }
            return rows;
        } catch (SQLException exception) {
            throw new DataAccessException("Could not generate the billing report", exception);
        }
    }
}
