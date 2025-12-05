package com.yearup.dealership.db;

import com.yearup.dealership.models.SalesContract;

import javax.sql.DataSource;
import java.sql.*;

public class SalesDao {
    private DataSource dataSource;

    public SalesDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void addSalesContract(SalesContract salesContract) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("""
                     INSERT INTO sales_contracts
                     (VIN, sale_date, price)
                     VALUES
                     (?, ?, ?)
                     """, PreparedStatement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, salesContract.getVin());
            preparedStatement.setDate(2, Date.valueOf(salesContract.getSaleDate()));
            preparedStatement.setDouble(3, salesContract.getPrice());

            preparedStatement.executeUpdate();

            // Update vehicle sold status in vehicles
            try (PreparedStatement updateVehicle = connection.prepareStatement(
                    "UPDATE vehicles SET SOLD = true WHERE VIN = ?")) {
                updateVehicle.setString(1, salesContract.getVin());
                updateVehicle.executeUpdate();
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int contractId = generatedKeys.getInt(1);
                    System.out.println("New Contract ID: " + contractId);
                }
            }

        } catch (SQLException e) {
            System.err.println("An unexpected database error occurred, please try again.");
            e.printStackTrace();
        }
    }
}
