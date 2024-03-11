package project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

public class ProjectOneSolution implements Developers {


    Connection connection = null;
    private void createTable() throws SQLException {
        Connection connection1 = connectToDatabase();
        Statement statement = connection1.createStatement();
        String createTable = "CREATE TABLE IF NOT EXISTS developers(name Text, age Integer, location Text, skill Text)";
        statement.execute(createTable);
        statement.close();
        closeDatabase();
    }

    private void loadFromTextToDB(String fileName){

        try(BufferedReader reader = new BufferedReader(new FileReader(fileName))){
            String line;

            while( (line = reader.readLine()) != null ){
                String mode = line.substring(0, line.lastIndexOf('#'));
                String[] parts = mode.split(",");
                String name = parts[0].trim();
                int age = Integer.parseInt(parts[1].trim());
                String location = parts[2].trim();
                String skill = parts[3].trim();
                uploadToDB(name, age, location, skill);
            }

        }catch (IOException exception){
            System.out.println(exception.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void uploadToDB(String name, int age, String location, String skill) throws SQLException{
       try{
           Connection conn = connectToDatabase();
           Statement statement = conn.createStatement();
           String uploadSQL = String.format("INSERT INTO developers(name, age, location, skill) VALUES('%S', %d, '%s', '%s')", name, age, location, skill);
           statement.execute(uploadSQL);
           statement.close();
           closeDatabase();
       }catch (SQLIntegrityConstraintViolationException e){
           System.out.println("This user already exists" + e.getMessage());
       }
    }

    private Connection connectToDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/Developer", "root", "zikans@19");
        return connection;
    }

    private void closeDatabase() throws SQLException{
        try{
            if( connection != null ){
                connection.close();
            }
        } catch (SQLException exception){
            System.out.println(exception.getMessage());
        }
    }

    @Override
    public ResultSet loadDevelopers() {
        ResultSet resultSet = null;
        try{
            createTable();
            loadFromTextToDB("/Users/decagon/Downloads/project/src/Project.txt");
            Connection conn = connectToDatabase();
            Statement statement = conn.createStatement();
            String selectStatement = "SELECT * FROM developers";
            resultSet = statement.executeQuery(selectStatement);


        } catch (SQLException exception){
            System.out.println(exception.getMessage());
        }
        return resultSet;
    }

    public static void main(String[] args) {
        ProjectOneSolution projectOneSolution = new ProjectOneSolution();
        projectOneSolution.loadDevelopers();
    }
}
