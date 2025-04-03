print("Iniciando configuración de MongoDB...");

db = db.getSiblingDB('sensoresDB'); // Seleccionar o crear base de datos

db.createCollection('sensorData'); // Crear colección

db.sensorData.insertOne({ message: "MongoDB configurado correctamente" });

print("Base de datos y colección creadas correctamente!");
