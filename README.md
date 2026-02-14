# cyber-cafe-management-system
Java Swing application for cyber cafe management

🚀 Features

🔐 Secure Login System

User authentication with SHA-256 password hashing

👤 Customer Management

Add, update, view customer details

🖥️ Computer/System Management

Track systems (Available / Occupied / Maintenance)

⏱️ Session Management

Start & end sessions

Automatic time calculation

💰 Billing Module

Calculates charges based on usage duration

🎨 User Interface Enhancements

Background images

Transparent panels

Highlighted table row selection

Auto-refresh functionality

🛠️ Tech Stack

Programming Language: Java

GUI: Java Swing

Database: MySQL

Database Connectivity: JDBC

IDE: NetBeans

Security: SHA-256 Password Hashing

📂 Project Structure

CyberCafeManagement/

│

├── src/

│   ├── com.cybercafe.ui

│   │   ├── LoginForm.java

│   │   ├── MainFrame.java

│   │   ├── CustomerPanel.java

│   │   ├── ComputerPanel.java

│   │   └── BillingPanel.java

│   │

│   ├── com.cybercafe.util

│   │   ├── DBConnection.java

│   │   └── PasswordUtil.java

│

├── resources/

│   ├── bg_customers.jpg

│   ├── bg_computers.jpg

│   └── bg_billing.jpg

│

└── README.md

⚙️ Setup & Installation

1️⃣ Database Setup

Create a MySQL database:

CREATE DATABASE cybercafe;


Import the required tables (users, customers, computers, sessions).

2️⃣ Configure Database Connection

Update DBConnection.java:

private static final String URL = "jdbc:mysql://localhost:3306/cybercafe";

private static final String USER = "root";

private static final String PASS = "your_password";

3️⃣ Add MySQL Connector

Add mysql-connector-j-9.x.x.jar to project libraries (NetBeans)

4️⃣ Run the Application

Run LoginForm.java

Login using stored credentials

🔑 Sample Login
Username: admin
Password: 123


(Password is stored as SHA-256 hash in the database)



📈 Future Enhancements

Role-based access (Admin / Staff)

Report generation (Daily & Monthly sales)

Bill export (PDF / Print)

JavaFX or Web-based UI

Connection pooling for performance

👩‍💻 Author

Afreen Jenifer

Computer Science Student

Project developed as part of academic learning
