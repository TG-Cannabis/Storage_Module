print("Iniciando configuración de MongoDB...");

db = db.getSiblingDB('fogDatabase'); // Seleccionar o crear base de datos

db.createCollection('sensorData'); // Crear colección

print("Base de datos y colección creadas correctamente!");
