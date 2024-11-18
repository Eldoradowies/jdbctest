package com.example.test;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.sql.*;

public class Main extends Application {

    // Database credentials
    private static final String URL = "jdbc:mysql://localhost:3306/login_schema"; // Update with your DB info
    private static final String USER = "root"; // MySQL username
    private static final String PASSWORD = "root"; // MySQL password

    private TextField usernameField;
    private TextField passwordField;
    private TextArea resultArea;
    private ListView<String> userListView;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("User Management System");

        // Create UI components
        usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        passwordField = new TextField();
        passwordField.setPromptText("Enter password");

        resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Arial", 14));
        resultArea.setPrefHeight(100);

        userListView = new ListView<>();
        userListView.setPrefHeight(150);

        Button createButton = new Button("Create User");
        Button readButton = new Button("Read Users");
        Button updateButton = new Button("Update User");
        Button deleteButton = new Button("Delete User");

        // Set button actions
        createButton.setOnAction(e -> createUser());
        readButton.setOnAction(e -> readUsers());
        updateButton.setOnAction(e -> updateUser());
        deleteButton.setOnAction(e -> deleteUser());

        // Set up ListView selection to populate usernameField
        userListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                usernameField.setText(newValue); // Populate usernameField with selected username
            }
        });

        // Layout
        HBox inputBox = new HBox(10, usernameField, passwordField);
        inputBox.setAlignment(Pos.CENTER);

        VBox buttonBox = new VBox(10, createButton, readButton, updateButton, deleteButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox root = new VBox(20, inputBox, buttonBox, userListView, resultArea);
        root.setAlignment(Pos.CENTER);
        root.setMinWidth(400);
        root.setMinHeight(450);

        // Apply styles
        root.setStyle("-fx-background-color: #f4f4f9; -fx-padding: 20;");
        inputBox.setStyle("-fx-spacing: 15;");
        buttonBox.setStyle("-fx-spacing: 15;");
        resultArea.setStyle("-fx-background-color: #e9ecef; -fx-border-color: #ccc;");

        // Scene
        Scene scene = new Scene(root, 500, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void createUser() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String query = "INSERT INTO users (username, password) VALUES (?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    resultArea.setText("User created successfully!");
                    clearInputFields();
                    readUsers(); // Refresh the list view
                } else {
                    resultArea.setText("Failed to create user.");
                }
            }
        } catch (SQLException e) {
            resultArea.setText("Error creating user: " + e.getMessage());
        }
    }

    private void readUsers() {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String query = "SELECT username FROM users";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                userListView.getItems().clear(); // Clear the previous list
                while (rs.next()) {
                    String username = rs.getString("username");
                    userListView.getItems().add(username);
                }
            }
        } catch (SQLException e) {
            resultArea.setText("Error reading users: " + e.getMessage());
        }
    }

    private void updateUser() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String query = "UPDATE users SET password = ? WHERE username = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, password);
                stmt.setString(2, username);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    resultArea.setText("User updated successfully!");
                    clearInputFields();
                    readUsers(); // Refresh the list view
                } else {
                    resultArea.setText("User not found or no changes made.");
                }
            }
        } catch (SQLException e) {
            resultArea.setText("Error updating user: " + e.getMessage());
        }
    }

    private void deleteUser() {
        String username = usernameField.getText();

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String query = "DELETE FROM users WHERE username = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, username);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    resultArea.setText("User deleted successfully!");
                    clearInputFields();
                    readUsers(); // Refresh the list view
                } else {
                    resultArea.setText("User not found.");
                }
            }
        } catch (SQLException e) {
            resultArea.setText("Error deleting user: " + e.getMessage());
        }
    }

    private void clearInputFields() {
        usernameField.clear();
        passwordField.clear();
    }
}
