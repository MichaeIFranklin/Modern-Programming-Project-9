-- Create the Authors table
CREATE TABLE Authors (
   AuthorID NUMBER GENERATED AS IDENTITY,
   FirstName VARCHAR2 (20) NOT NULL,
   LastName VARCHAR2 (30) NOT NULL,
   PRIMARY KEY (AuthorID)
);

-- Create the Titles table
CREATE TABLE Titles (
   ISBN VARCHAR2 (20) NOT NULL,
   Title VARCHAR2 (100) NOT NULL,
   EditionNumber NUMBER NOT NULL,
   Copyright VARCHAR2 (4) NOT NULL,
   PRIMARY KEY (ISBN)
);

-- Create the AuthorISBN table
CREATE TABLE AuthorISBN (
   AuthorID NUMBER NOT NULL,
   ISBN VARCHAR2 (20) NOT NULL,
   PRIMARY KEY(AuthorID, ISBN),
   FOREIGN KEY (AuthorID) REFERENCES Authors (AuthorID), 
   FOREIGN KEY (ISBN) REFERENCES Titles (ISBN)
);

-- Insert data into the Authors table
INSERT INTO Authors (FirstName, LastName)
  VALUES ('Paul', 'Deitel');
INSERT INTO Authors (FirstName, LastName)
  VALUES ('Harvey', 'Deitel');
INSERT INTO Authors (FirstName, LastName)
  VALUES ('Abbey','Deitel');
INSERT INTO Authors (FirstName, LastName)
  VALUES ('Dan','Quirk');
INSERT INTO Authors (FirstName, LastName)
  VALUES ('Michael','Morgano');

 -- Insert data into the Titles table
INSERT INTO Titles (ISBN, Title, EditionNumber, Copyright)
  VALUES ('0132151006','Internet and World Wide Web How to Program',5,'2012');
INSERT INTO Titles (ISBN, Title, EditionNumber, Copyright)
  VALUES  ('0133807800','Java How to Program',10,'2015');
INSERT INTO Titles (ISBN, Title, EditionNumber, Copyright)
  VALUES ('0132575655','Java How to Program, Late Objects Version',10,'2015');
INSERT INTO Titles (ISBN, Title, EditionNumber, Copyright)
  VALUES  ('013299044X','C How to Program',7,'2013'); 
INSERT INTO Titles (ISBN, Title, EditionNumber, Copyright)
  VALUES ('0132990601','Simply Visual Basic 2010',4,'2013');
INSERT INTO Titles (ISBN, Title, EditionNumber, Copyright)
  VALUES ('0133406954','Visual Basic 2012 How to Program',6,'2014');
INSERT INTO Titles (ISBN, Title, EditionNumber, Copyright)
  VALUES ('0133379337','Visual C# 2012 How to Program',5,'2014');
INSERT INTO Titles (ISBN, Title, EditionNumber, Copyright)
  VALUES ('0136151574','Visual C++ How to Program',2,'2008');
INSERT INTO Titles (ISBN, Title, EditionNumber, Copyright)
  VALUES ('0133378713','C++ How to Program',9,'2014');
INSERT INTO Titles (ISBN, Title, EditionNumber, Copyright)
  VALUES ('0133764036','Android How to Program',2,'2015');
INSERT INTO Titles (ISBN, Title, EditionNumber, Copyright)
  VALUES ('0133570924','Android for Programmers: An App-Driven Approach, Volume 1',2,'2014');
INSERT INTO Titles (ISBN, Title, EditionNumber, Copyright)
  VALUES ('0132121360','Android for Programmers: An App-Driven Approach',1,'2012');

-- Insert data into the AuthorISBN table
INSERT INTO AuthorISBN (AuthorID, ISBN)
  VALUES (1,'0132151006');
INSERT INTO AuthorISBN (AuthorID, ISBN)
  VALUES (2,'0132151006');
INSERT INTO AuthorISBN (AuthorID, ISBN)
  VALUES (3,'0132151006');
INSERT INTO AuthorISBN (AuthorID, ISBN)
  VALUES (1,'0133807800');
INSERT INTO AuthorISBN (AuthorID, ISBN)
  VALUES (2,'0133807800');
INSERT INTO AuthorISBN (AuthorID, ISBN)
  VALUES (1,'0132575655');
INSERT INTO AuthorISBN (AuthorID, ISBN)
  VALUES (2,'0132575655');
INSERT INTO AuthorISBN (AuthorID, ISBN)
  VALUES (1,'013299044X');
INSERT INTO AuthorISBN (AuthorID, ISBN)
  VALUES (2,'013299044X');
INSERT INTO AuthorISBN (AuthorID, ISBN)
  VALUES (1,'0132990601');
INSERT INTO AuthorISBN (AuthorID, ISBN)
  VALUES (2,'0132990601');
INSERT INTO AuthorISBN (AuthorID, ISBN)
  VALUES (3,'0132990601');
INSERT INTO AuthorISBN (AuthorID, ISBN)
  VALUES (1,'0133406954');
INSERT INTO AuthorISBN (AuthorID, ISBN)
  VALUES (2,'0133406954');
INSERT INTO AuthorISBN (AuthorID, ISBN)
  VALUES (3,'0133406954');
INSERT INTO AuthorISBN (AuthorID, ISBN)
  VALUES (1,'0133379337');
INSERT INTO AuthorISBN (AuthorID, ISBN)
  VALUES (2,'0133379337');
INSERT INTO AuthorISBN (AuthorID, ISBN)
  VALUES (1,'0136151574');
INSERT INTO AuthorISBN (AuthorID, ISBN)
  VALUES (2,'0136151574');
INSERT INTO AuthorISBN (AuthorID, ISBN)
  VALUES (4,'0136151574');
INSERT INTO AuthorISBN (AuthorID, ISBN)
  VALUES (1,'0133378713');
INSERT INTO AuthorISBN (AuthorID, ISBN)
  VALUES (2,'0133378713');
INSERT INTO AuthorISBN (AuthorID, ISBN)
  VALUES (1,'0133764036');
INSERT INTO AuthorISBN (AuthorID, ISBN)
  VALUES (2,'0133764036');
INSERT INTO AuthorISBN (AuthorID, ISBN)
  VALUES (3,'0133764036');
INSERT INTO AuthorISBN (AuthorID, ISBN)
  VALUES (1,'0133570924');
INSERT INTO AuthorISBN (AuthorID, ISBN)
  VALUES (2,'0133570924');
INSERT INTO AuthorISBN (AuthorID, ISBN)
  VALUES (3,'0133570924');
INSERT INTO AuthorISBN (AuthorID, ISBN)
  VALUES (1,'0132121360');
INSERT INTO AuthorISBN (AuthorID, ISBN)
  VALUES (2,'0132121360');
INSERT INTO AuthorISBN (AuthorID, ISBN)
  VALUES (3,'0132121360');
INSERT INTO AuthorISBN (AuthorID, ISBN)
  VALUES (5,'0132121360');
