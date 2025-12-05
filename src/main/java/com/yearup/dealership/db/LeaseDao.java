package com.yearup.dealership.db;

import com.yearup.dealership.models.LeaseContract;

import javax.sql.DataSource;
import java.sql.*;

public class LeaseDao {
    private DataSource dataSource;

    public LeaseDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void addLeaseContract(LeaseContract leaseContract) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("""
                     INSERT INTO lease_contracts
                     (VIN, lease_start, lease_end, monthly_payment)
                     VALUES
                     (?, ?, ?, ?)
                     """, PreparedStatement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, leaseContract.getVin());
            preparedStatement.setDate(2, Date.valueOf(leaseContract.getLeaseStart()));
            preparedStatement.setDate(3, Date.valueOf(leaseContract.getLeaseEnd()));
            preparedStatement.setDouble(4, leaseContract.getMonthlyPayment());

            preparedStatement.executeUpdate();

            // Update vehicle sold status in vehicles
            try (PreparedStatement updateVehicle = connection.prepareStatement(
                    "UPDATE vehicles SET SOLD = true WHERE VIN = ?")) {
                updateVehicle.setString(1, leaseContract.getVin());
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
