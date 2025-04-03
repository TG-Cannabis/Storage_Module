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

```sh
git clone <repository-url>
cd storage-module
