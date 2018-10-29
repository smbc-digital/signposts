/*
Creates table in Visualise.db SqLite file
*/
CREATE TABLE QueryLog( QueryID INTEGER PRIMARY KEY autoincrement, User text, Query text, Timestamp DATETIME DEFAULT CURRENT_TIMESTAMP )`