# AIMS-Ver2025
 Capstone Project for ITSS Software Development (IT4549 - IT4549E) and Software Design & Construction (IT4490)

## Requirements
- JDK >= 17
- Maven >= 3.9.5
- SQLite >= 3.44.0
- Spring Boot >= 3.1.5

## Getting Started

### Clone the repository to your local machine
```bash 
git clone https://github.com/trangntt-for-student/AIMS-Ver2025.git
```
Navigate to the project directory
```bash
cd AIMS-Ver2025
```

### Build the project with Maven
```bash
mvn clean install
```

You can skip this step if you are using an IDE like IntelliJ IDEA or Eclipse, which will automatically build the project.

### Create the application.properties configuration file

Create the `application.properties` file in the `src/main/resources/` directory.

Copy the contents of `application.properties.example` and paste them into `application.properties`. Modify the configuration settings according to your needs.

Visit: https://developer.paypal.com/dashboard/ to obtain the necessary information for PayPal configuration.
- Step 1: Create a sandbox account (if you don't have one).
- Step 2: Create a new application in the "Apps & Credentials" section to get the Client ID and Secret.

Visit: https://vietqr.vn/merchant/request to obtain the necessary information for VietQR configuration.
- Step 1: Create a new account.
- Step 2: Create a new application in the "Tich hop va ket noi --> API SERVICE" section to get the Username and Password.
- Step 3: Create a new bank account in the Bank Accounts section to get the Bank Account Number, Bank Account Name, and Bank Code.

### Run the application
Use Maven to run the application:
```bash
mvn spring-boot:run
```
Or run directly from your IDE by executing the `com.hust.soict.aims.App.java` class.

## Demo Video
Access the demo video at: [Link video demo GG Drive](https://drive.google.com/file/d/1nvYJ7yIdJZ5kNfJbcXc7dmvHD4p5XorA/view?usp=sharing)

## Copy Right
© 2026 AIMS - SoICT HUST by Dr. NGUYEN Thi Thu Trang and Teaching Assistants
