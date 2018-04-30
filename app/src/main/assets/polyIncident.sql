-- add mishap table
CREATE TABLE mishap (
	id INT PRIMARY KEY,
	name VARCHAR(20) NOT NULL,
	state VARCHAR(20) NOT NULL CHECK (state IN ('TODO','INPROGRESS','DONE')),
	priority VARCHAR(20) NOT NULL CHECK (priority IN ('HIGH','LOW','MEDIUM','MAJOR','CRITICAL')),
	dateStart DATE NOT NULL,
	dateEnd DATE NOT NULL,
	description TEXT,
	idDeclarant INT NOT NULL, 
	place VARCHAR(20) NOT NULL,
	tag CHAR (10) NOT NULL
);