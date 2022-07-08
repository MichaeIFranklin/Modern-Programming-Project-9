//******************************************************************************
//
//  Developer:     Michael Franklin
//
//  Project #:     Project 9
//
//  File Name:     DatabaseHelper.java
//
//  Course:        COSC 4301 - Modern Programming
//
//  Due Date:      05/05/2022
//
//  Instructor:    Fred Kumi 
//
//  Description:   Handles database connectivity, query and response handling
//
//
//******************************************************************************

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseHelper {
	// Tables Infos
	final private String AuthorTable = "Authors";
	final private String AuthorID = "AuthorID";
	final private String AuthorFirstName = "FirstName";
	final private String AuthorLastName = "LastName";
	final private String AuthISBNTable = "AuthorISBN";
	final private String TitlesTable = "Titles";
	final private String TitlesISBN = "ISBN";
	final private String TitlesTitle = "Title";
	final private String TitlesEdition = "EditionNumber";
	final private String TitlesYear = "Copyright";
	
	private Connection connection;
	private ConnectToOracleDB dbConnect;
	
	public DatabaseHelper()
	{
		// connect to database
		connection = ConnectToDatabase();
		
		// add shutdown hook
		Runtime.getRuntime().addShutdownHook(new Thread() {
		       public void run()
		       {
		           // run server close sequence before full termination
		    	   CloseConnection();
		       }
		       });
	}
	
	// returns true if we have a connection to the database
	public boolean IsConnected()
	{
		return connection != null;
	}
	
	
	// ***************************************************************
	//
	// Method:      ConnectToDatabase
	//
	// Description: Setup connection to database
	//
	// Parameters:  None
	//
	// Returns:     N/A
	//
	// **************************************************************
	public Connection ConnectToDatabase() 
	{
		Connection connection = null;
		
		dbConnect = new ConnectToOracleDB();
		
		try 
		{
		   dbConnect.loadDrivers();
		   connection = dbConnect.connectDriver();
		}
		catch (Exception e) 
		{
            //System.out.println("Something terrible went wrong");
            System.exit(0);
		}
		
		return connection;
	}
	
	
	//***************************************************************
	//
	//  Method:       GetTitles
	// 
	//  Description:  gets a list of titles from the database, either all or 
	//				  just some depending on the parameters
	//
	//  Parameters:	  Integer: List titles written by the author with this ID
	//				  		   Set to 0 to show all titles
	//
	//  Returns:      List(AuthorInfo): returned titles
	//
	//**************************************************************
	public List<TitleInfo> GetTitles(int authID)
	{
		ArrayList<TitleInfo> titles = new ArrayList<TitleInfo>();
		String statement = "";
		Statement stmt = null;
		ResultSet res = null;
		
		// send the SQL query
		if (authID == 0)
		{
			// get all titles
			statement = "SELECT * FROM " + TitlesTable;
		}
		else
		{
			// get all title by the passed author
			statement = "SELECT * FROM " + TitlesTable + "\n"
					+ "WHERE " + TitlesISBN + " IN\n"
					+ "(\n"
					+ "\tSELECT " + TitlesISBN + " FROM " + AuthISBNTable + "\n"
					+ "\tWHERE " + AuthorID + " = " + authID + "\n"
					+ ")";
		}
		
		
		try
		{
			stmt = connection.createStatement();
	        res = stmt.executeQuery(statement);
		
			if (res != null)
			{
				 // while there are still records to read
				 while(res.next())
				 {
		            // parse titles
					titles.add(new TitleInfo(
							res.getString(TitlesISBN),
							res.getString(TitlesTitle),
							res.getInt(TitlesEdition),
							res.getString(TitlesYear)));			        	
				}
			}
		}
		catch(SQLException e)
		{
			// print out error
			e.printStackTrace();
		}
		
		// cleanup database resources
		CloseResources(stmt, res);
		
		return titles;
	}
	
	
	//***************************************************************
	//
	//  Method:       AddTitle
	// 
	//  Description:  sends title insert request to database
	//
	//  Parameters:   String: ISBN name of book
	//	  			  String: title of book
	//				  Integer: edition of new book
	//				  String: copyright year of book
	//
	//  Returns:      TitleInfo: info of added book if successful
	//							 info is null if not successful
	//
	//**************************************************************
	public TitleInfo AddTitle(String ISBN, String title, int edition, String year)
	{
		// send the SQL query
		String statement = "INSERT INTO " + TitlesTable
				+ " (" + TitlesISBN + ", " + TitlesTitle + ", " + TitlesEdition + ", " + TitlesYear + ") " 
				+ "VALUES (\'" + ISBN + "\', \'"
				+ title + "\', "
				+ edition + ", \'"
				+ year + "\')";
		Statement stmt = null;
		TitleInfo book = null;
		
		try
		{
			stmt = connection.createStatement();
			int count = stmt.executeUpdate(statement);
		
			if (count == 1)
			{				 
	            // create title info
				book = new TitleInfo(
						ISBN,
						title,
						edition,
						year);			        	
				 
				// insert successful
				System.out.println("New book added.");
			}
			else
			{
				// print out error
				System.out.println("New book not added.");
			}
		}
		catch(SQLException e)
		{
			// print out error
			System.out.println("New book not added.");
			System.out.println("SQL: " + statement);
			e.printStackTrace();
		}
		
		// cleanup database resources
		CloseResources(stmt);
		
		return book;
	}
		
	
	//***************************************************************
	//
	//  Method:       LinkTitleAuthor
	// 
	//  Description:  sends an insert request to the database to link a title 
	// 				  to an author
	//
	//  Parameters:   String: the ISBN of the title to link
	//				  Integer: the ID of the author to link
	//
	//  Returns:      N/A
	//
	//**************************************************************
	public void LinkTitleAuthor(String ISBN, int authID)
	{
		// send the SQL query
		String statement = "INSERT INTO " + AuthISBNTable
				+ " (" + AuthorID + ", " + TitlesISBN + ") " 
				+ " VALUES (" + authID + ", " + "\'" + ISBN + "\')";
		Statement stmt = null;
		
		try
		{
			stmt = connection.createStatement();
			int count = stmt.executeUpdate(statement);
		
			if (count == 1)
			{

	            // insert successful
				System.out.println("Author added to book.");
			}
			else
			{
				// print out error
				System.out.println("Author not added to book.");
			}
		}
		catch(SQLException e)
		{
			// print out error
			System.out.println("Author not added to book.");
			System.out.println("SQL: " + statement);
			e.printStackTrace();
		}
		
		// cleanup database resources
		CloseResources(stmt);
	}
	
	
	//***************************************************************
	//
	//  Method:       GetAuthors
	// 
	//  Description:  gets a list of authors from the database, either all or 
	//				  just some depending on the parameters
	//
	//  Parameters:  String: only show authors not linked to this ISBN
	//						 pass an empty string to show all authors
	//
	//  Returns:      List(AuthorInfo): returned authors
	//
	//**************************************************************
	public List<AuthorInfo> GetAuthors(String ISBN)
	{
		ArrayList<AuthorInfo> authors = new ArrayList<AuthorInfo>();
		String statement = "";
		Statement stmt = null;
		ResultSet res = null;
		
		// send the SQL query
		if (ISBN.isEmpty())
		{
			// get all authors
			statement = "SELECT * FROM " + AuthorTable;
		}
		else
		{
			// get all authors that aren't linked to the passed ISBN
			statement = "SELECT * FROM " + AuthorTable + "\n"
					+ "WHERE " + AuthorID + " NOT IN\n"
					+ "(\n"
					+ "\tSELECT " + AuthorID + " FROM " + AuthISBNTable + "\n"
					+ "\tWHERE " + TitlesISBN + " = \'" + ISBN + "\'\n"
					+ ")";
		}
		
		try
		{
			stmt = connection.createStatement();
	        res = stmt.executeQuery(statement);
		
			if (res != null)
			{
				 // while there are still records to read
				 while(res.next())
				 {
		            // parse authors
					authors.add(new AuthorInfo(
							res.getInt(AuthorID),
							res.getString(AuthorFirstName),
							res.getString(AuthorLastName)));			        	
				}
			}
		}
		catch(SQLException e)
		{
			// print out error
			System.out.println("Error while getting authors.");
			System.out.println("SQL: " + statement);
			e.printStackTrace();
		}
		
		// cleanup database resources
		CloseResources(stmt, res);
		
		return authors;
	}
	
	
	//***************************************************************
	//
	//  Method:       AddAuthor
	// 
	//  Description:  sends author insert request to database
	//
	//  Parameters:   String: first name of author
	//	  			  String: last name of author
	//
	//  Returns:      AuthorInfo: Info of the inserted author
	//
	//**************************************************************
	public AuthorInfo AddAuthor(String first, String last)
	{
		// send the SQL query
		String statement = "INSERT INTO " + AuthorTable
				+ " (" + AuthorFirstName + ", " + AuthorLastName + ") " 
				+ " VALUES (\'" + first + "\', \'" + last + "\')";
		Statement stmt = null;
		ResultSet res = null;
		AuthorInfo author = null;
		
		try
		{
			final String[] cols = { AuthorID };
			stmt = connection.createStatement();
			int count = stmt.executeUpdate(statement, cols);
		
			if (count == 1)
			{
				res = stmt.getGeneratedKeys();
			
				if (res != null)
				{
					 // while there are still records to read
					 while(res.next())
					 {
			            // parse authors
						author = new AuthorInfo(
								res.getInt(1),
								first,
								last);			        	
					}
				}
				
	            // insert successful
				System.out.println("New author added.");
			}
			else
			{
				// print out error
				System.out.println("New author not added.");
			}
		}
		catch(SQLException e)
		{
			// print out error
			System.out.println("New author not added.");
			System.out.println("SQL: " + statement);
			e.printStackTrace();
		}
		
		// cleanup database resources
		CloseResources(stmt, res);
		
		return author;
	}
		
	
	//***************************************************************
	//
	//  Method:       UpdateAuthor
	// 
	//  Description:  sends author update request to database
	//
	//  Parameters:   Integer: ID of author to update
	//				  String: new first name of author
	//	  			  String: new last name of author
	//
	//  Returns:      N/A
	//
	//**************************************************************
	public void UpdateAuthor(int id, String first, String last)
	{
		// send the SQL query
		String statement = "UPDATE " + AuthorTable + " SET "
				+ AuthorFirstName + " = \'" + first + "\', "
				+ AuthorLastName + " = \'" + last
				+ "\' WHERE " + AuthorID + " = " + id;
		Statement stmt = null;
		
		try
		{
			stmt = connection.createStatement();
			int count = stmt.executeUpdate(statement);
		
			if (count == 1)
			{

	            // update successful
				System.out.println("Author info updated.");
			}
			else
			{
				// print out error
				System.out.println("Author info not updated.");
			}
		}
		catch(SQLException e)
		{
			// print out error
			System.out.println("Author info not updated.");
			System.out.println("SQL: " + statement);
			e.printStackTrace();
		}
		
		// cleanup database resources
		CloseResources(stmt);
	}
	
	
	//***************************************************************
	//
	//  Method:       DeleteAuthor
	// 
	//  Description:  sends author delete request to database
	//
	//  Parameters:   Integer: ID of author to delete
	//
	//  Returns:      N/A
	//
	//**************************************************************
	public void DeleteAuthor(int id)
	{
		// send the ISBN delete SQL query
		String statement = "DELETE FROM " + AuthISBNTable
				+ " WHERE " + AuthorID + " = " + id;
		Statement stmt = null;
		
		try
		{
			stmt = connection.createStatement();
			stmt.executeUpdate(statement);

            // ISBN delete successful
			// send Author delete SQL query
			statement = "DELETE FROM " + AuthorTable
					+ " WHERE " + AuthorID + " = " + id;
			int count = stmt.executeUpdate(statement);
			
			if (count > 0)
			{
				// Author delete successful
				// check for any orphaned Titles
				statement = "DELETE FROM " + TitlesTable + " WHERE " + TitlesISBN + " IN\n"
						+ "(\n"
						+ "\tSELECT " + TitlesISBN + " FROM " + TitlesTable + "\n"
						+ "\tWHERE " + TitlesISBN + " NOT IN\n"
						+ "\t(\n"
						+ "\t\tSELECT " + TitlesISBN + " FROM " + AuthISBNTable + "\n"
						+ "\t)\n"
						+ "\tGROUP BY " + TitlesISBN +"\n"
						+ ")";
				stmt.executeUpdate(statement);
				
				// Author and references fully deleted from database
				System.out.println("Author deleted.");
			}
			else
			{
				// print out error
				System.out.println("Author not deleted. Could not find author in database.");
			}
		}
		catch(SQLException e)
		{
			// print out error
			System.out.println("Author not deleted.");
			System.out.println("SQL: " + statement);
			e.printStackTrace();
		}
		
		// cleanup database resources
		CloseResources(stmt);
	}

	
	//***************************************************************
	//
	//  Method:       CloseResources
	// 
	//  Description:  closes all open database query resources for cleaning
	//
	//  Parameters:   None
	//
	//  Returns:      N/A 
	//
	//**************************************************************
	public void CloseResources(Statement statement)
	{
		CloseResources(statement, null);
	}
	public void CloseResources(Statement statement, ResultSet results)
	{
					
		try 
		{
			if (statement != null)
			{
				statement.close();
			}
			if (results != null)
			{
				results.close();
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}	
	
	
	
	//***************************************************************
	//
	//  Method:       CloseConnection
	// 
	//  Description:  closes all open connections
	//
	//  Parameters:   None
	//
	//  Returns:      N/A 
	//
	//**************************************************************
	public void CloseConnection()
	{
		if (dbConnect != null)
		{			
			try 
			{
				dbConnect.closeDBConnection();
			}
			catch (Exception e) 
			{	            
	            e.printStackTrace();
				System.exit(0);
			}
		}
	}		
	
}
