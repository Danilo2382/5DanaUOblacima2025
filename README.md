# 5 Dana U Oblacima 2025

This is a Spring Boot project as a solution for the first stage of "5 Dana u Oblacima 2025" competition.

## Environment Requirements for Build

The application is developed using the Spring Boot framework, with Maven as the build tool. To build and run the application, make sure you have the following installed on your machine:

- **Java 21**: Ensure you have Java Development Kit.
- **Maven 3.8+**: Apache Maven 3.6 or newer for building the project.
- **IDE (optional)**: An Integrated Development Environment (IDE) like IntelliJ IDEA, Eclipse, or VS Code.

## How to Build

To build the application, follow these steps:

1. **Clone the repository**:
   ```sh
   git clone [https://github.com/Danilo2382/5DanaUOblacima2025.git](https://github.com/Danilo2382/5DanaUOblacima2025.git) cd 5DanaUOblacima
   ```
3. **Build the project using Maven**:
   ```sh
   mvn clean install
   ```
   This will compile the project and download all necessary dependencies.

## Running the Application

Once the project is built, you can run the application with the following command:
```sh
mvn spring-boot:run
```

Alternatively, you can run the JAR file generated in the target directory:
```sh
java -jar target/*.jar
```

By default, the application is served on port 8080. You can access it by navigating to:
```
http://localhost:8080
```

## Building and Running the Application in IntelliJ IDEA

To build and run the application directly in IntelliJ IDEA, follow these steps:

1. **Import the Project**:
   - Open IntelliJ IDEA.
   - Select **File > Open...**.
   - Navigate to the project folder and open it.
   - IntelliJ will detect the Maven build configuration and automatically download all dependencies.

2. **Build the Project**:
   - Once the project is imported, go to the top menu and select **Build > Build Project** or use the shortcut `Ctrl+F9` (Windows/Linux) or `Cmd+F9` (Mac).
   - Ensure there are no errors in the build process.

3. **Run the Application**:
   - Open the main class (located in the `src/main/java` directory).
   - Right-click on the file and select **Run'**.
   - Alternatively, click on the green triangle icon next to the class name in the editor or in the project explorer.

4. **Access the Application**:
   - The application will start on the default port `8080`. 
   - Open your web browser and navigate to:
     ```
     http://localhost:8080
     ```
## List of Technologies Used

1. **Spring Boot**  
   A powerful Java framework for building production-ready applications with minimal configuration. It provides an opinionated approach to application development and integrates seamlessly with other Spring modules.

2. **Spring Web**  
   A module of Spring Boot used for building RESTful APIs and web applications. It simplifies handling HTTP requests and responses.

3. **Spring Data JPA**  
   A library that makes database interaction easier by using the Java Persistence API (JPA). It provides simple methods for performing CRUD operations without writing complex queries.

4. **H2 Database**  
   An in-memory database used during development and testing. It allows quick prototyping without the need for setting up an external database.

5. **Maven**  
   A build automation and dependency management tool that simplifies compiling, building, and packaging the application.

7. **Lombok**  
   A Java library that reduces boilerplate code by generating getters, setters, constructors, `toString` methods, and more during the compilation phase.

8. **Spring Boot DevTools**  
   A module that enhances the development experience by enabling hot-swapping of code changes and automatic server restarts during development.

9. **MapStruct**
    A code generator that greatly simplifies the implementation of mappings between Java bean types (converting DTOs to Entities and vice versa).

10. **Jakarta Validation**
    (Hibernate Validator) Used for validating incoming request data (e.g., @NotBlank, @Email, @Min) to ensure data integrity before processing.

