# Storage Module

The **Storage Module** is responsible for handling data storage using **MongoDB**. This module consumes messages from **Kafka** and saves them into a MongoDB collection.

## Requirements

Before running the Storage Module, ensure you have the following installed:

- **Docker** (to run MongoDB)
- **Java** (version **17 to 21**, preferably **21**)
- **Maven** (for building the project)

## Setup

### 1. Start MongoDB (Docker Compose)

Run the following command to start a **MongoDB container**:

```bash
docker compose up -d
```

### 2. Clone the Repository

```bash
git clone https://github.com/TG-Cannabis/Storage_Module.git
cd storage-module
```

### 3. Generate the jar file and execute it:
```bash
mvn clean package
java -jar target/storage-module-1.0.0.jar
```