package com.yearup.dealership.db;

import com.yearup.dealership.models.Vehicle;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleDao {
    private DataSource dataSource;

    public VehicleDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void addVehicle(Vehicle vehicle) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("""
                     INSERT INTO vehicles
                     (VIN, make, model, year, SOLD, color, vehicleType, odometer, price)
                     VALUES
                     (?, ?, ?, ?, ?, ?, ?, ?, ?)
                     """)) {

            preparedStatement.setString(1, vehicle.getVin());
            preparedStatement.setString(2, vehicle.getMake());
            preparedStatement.setString(3, vehicle.getModel());
            preparedStatement.setInt(4, vehicle.getYear());
            preparedStatement.setBoolean(5, vehicle.isSold());
            preparedStatement.setString(6, vehicle.getColor());
            preparedStatement.setString(7, vehicle.getVehicleType());
            preparedStatement.setInt(8, vehicle.getOdometer());
            preparedStatement.setDouble(9, vehicle.getPrice());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("An unexpected database error occurred, please try again.");
            e.printStackTrace();
        }
    }

    public void removeVehicle(String VIN) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("""
                     DELETE FROM vehicles
                     WHERE VIN = ?
                     """)) {

            preparedStatement.setString(1, VIN);
            int rows = preparedStatement.executeUpdate();

            // Validate in case no matches are found
            if (rows == 0) {
                System.out.println("No vehicle found with VIN: " + VIN);
            }

        } catch (SQLException e) {
            System.err.println("An unexpected database error occurred, please try again.");
            e.printStackTrace();
        }
    }

    public List<Vehicle> searchByPriceRange(double minPrice, double maxPrice) {
        List<Vehicle> vehicles = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("""
                     SELECT *
                     FROM vehicles v
                     WHERE v.price >= ? AND v.price <= ?
                     """
             )) {

            preparedStatement.setDouble(1, minPrice);
            preparedStatement.setDouble(2, maxPrice);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Vehicle vehicle = createVehicleFromResultSet(resultSet);
                    vehicles.add(vehicle);
                }
            }
        } catch (SQLException e) {
            System.err.println("An unexpected database error occurred, please try again.");
            e.printStackTrace();
        }

        return vehicles;
    }

    public List<Vehicle> searchByMakeModel(String make, String model) {
        List<Vehicle> vehicles = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("""
                     SELECT *
                     FROM vehicles v
                     WHERE v.make = ? AND v.model = ?
                     """
             )) {

            preparedStatement.setString(1, make);
            preparedStatement.setString(2, model);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Vehicle vehicle = createVehicleFromResultSet(resultSet);
                    vehicles.add(vehicle);
                }
            }
        } catch (SQLException e) {
            System.err.println("An unexpected database error occurred, please try again.");
            e.printStackTrace();
        }

        return vehicles;
    }

    public List<Vehicle> searchByYearRange(int minYear, int maxYear) {
        List<Vehicle> vehicles = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("""
                     SELECT *
                     FROM vehicles v
                     WHERE v.year >= ? AND v.year <= ?
                     """
             )) {

            preparedStatement.setInt(1, minYear);
            preparedStatement.setInt(2, maxYear);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Vehicle vehicle = createVehicleFromResultSet(resultSet);
                    vehicles.add(vehicle);
                }
            }
        } catch (SQLException e) {
            System.err.println("An unexpected database error occurred, please try again.");
            e.printStackTrace();
        }

        return vehicles;
    }

    public List<Vehicle> searchByColor(String color) {
        List<Vehicle> vehicles = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("""
                     SELECT *
                     FROM vehicles v
                     WHERE v.color = ?
                     """
             )) {

            preparedStatement.setString(1, color);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Vehicle vehicle = createVehicleFromResultSet(resultSet);
                    vehicles.add(vehicle);
                }
            }
        } catch (SQLException e) {
            System.err.println("An unexpected database error occurred, please try again.");
            e.printStackTrace();
        }

        return vehicles;
    }

    public List<Vehicle> searchByMileageRange(int minMileage, int maxMileage) {
        List<Vehicle> vehicles = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("""
                     SELECT *
                     FROM vehicles v
                     WHERE v.odometer >= ? AND v.odometer <= ?
                     """
             )) {

            preparedStatement.setInt(1, minMileage);
            preparedStatement.setInt(2, maxMileage);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Vehicle vehicle = createVehicleFromResultSet(resultSet);
                    vehicles.add(vehicle);
                }
            }
        } catch (SQLException e) {
            System.err.println("An unexpected database error occurred, please try again.");
            e.printStackTrace();
        }

        return vehicles;
    }

    public List<Vehicle> searchByType(String type) {
        List<Vehicle> vehicles = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("""
                     SELECT *
                     FROM vehicles v
                     WHERE v.vehicleType = ?
                     """
             )) {

            preparedStatement.setString(1, type);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Vehicle vehicle = createVehicleFromResultSet(resultSet);
                    vehicles.add(vehicle);
                }
            }
        } catch (SQLException e) {
            System.err.println("An unexpected database error occurred, please try again.");
            e.printStackTrace();
        }

        return vehicles;
    }

    private Vehicle createVehicleFromResultSet(ResultSet resultSet) throws SQLException {
        Vehicle vehicle = new Vehicle();
        vehicle.setVin(resultSet.getString("VIN"));
        vehicle.setMake(resultSet.getString("make"));
        vehicle.setModel(resultSet.getString("model"));
        vehicle.setYear(resultSet.getInt("year"));
        vehicle.setSold(resultSet.getBoolean("SOLD"));
        vehicle.setColor(resultSet.getString("color"));
        vehicle.setVehicleType(resultSet.getString("vehicleType"));
        vehicle.setOdometer(resultSet.getInt("odometer"));
        vehicle.setPrice(resultSet.getDouble("price"));
        return vehicle;
    }
}
