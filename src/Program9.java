//******************************************************************************
//
//  Developer:     Michael Franklin
//
//  Project #:     Project 9
//
//  File Name:     Program9.java
//
//  Course:        COSC 4301 - Modern Programming
//
//  Due Date:      05/05/2022
//
//  Instructor:    Fred Kumi 
//
//  Description:   Driver class to run and test the Project 9 classes and 
//				   functionality.
//
//
//******************************************************************************

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Program9
{	
	private Scanner inputStream;
	private DatabaseHelper database;
	
	//***************************************************************
	//
	//  Method:       main
	// 
	//  Description:  The main method of the program
	//
	//  Parameters:   String array
	//
	//  Returns:      N/A 
	//
	//**************************************************************
	public static void main(String[] args)
	{	       
		// Create an object of the main class and use it to call
		// the non-static developerInfo method
		Program9 obj = new Program9();
		obj.developerInfo();

		// run setup
		obj.Setup();

		// show main menu (handles cleanup when needed)
		obj.ShowMainMenu();
	}

	
	//***************************************************************
	//
	//  Method:       Setup (Non Static)
	// 
	//  Description:  sets up needed systems for the program
	//
	//  Parameters:   None
	//
	//  Returns:      N/A 
	//
	//**************************************************************
	public void Setup()
	{
		// setup scanner to get user input
		inputStream = new Scanner(System.in);
		
		// setup database helper
		database = new DatabaseHelper();
		
		// check that we are connected to the database
		if (database.IsConnected())
		{
			// print that setup is complete
			System.out.println("Setup Complete");
		}
		
	}

	
	//***************************************************************
	//
	//  Method:       ShowMainMenu
	// 
	//  Description:  displays and handles main menu
	//
	//  Parameters:   None
	//
	//  Returns:      N/A 
	//
	//**************************************************************
	public void ShowMainMenu()
	{
		// check that we are still connected to the database
		if (database.IsConnected())
		{
			Integer input = 0;
			while (input != 7)
			{
				// display menu
				System.out.println(
						"\nMain Menu:\n\n1. List all authors.\n"
						+ "2. Add a new author.\n"
						+ "3. Edit the existing information of an author.\n"
						+ "4. Add an author to a book title.\n"
						+ "5. Get all books written by an author\n"
						+ "6. Delete an author.\n"
						+ "7. Exit");
				
				// get input
				String inputStr = GetUserInput();
				
				// validate input
				input = ValidateNumericInput(inputStr, 1, 7);
				if (input != -1)
				{
					// containers for info values
					AuthorInfo author = null;
					TitleInfo title = null;
					
					// handle input
					switch(input)
					{
					case 1: // List all authors
						System.out.println("All authors:");
						ListAuthors(false, "");
						break;
					case 2: // add author
						AddAuthor();
						break;
					case 3: // edit author
						// display authors menu
						System.out.println("Select an author to edit:");
						author = AuthorsMenu();
						
						// prompt new info for author
						if (author != null)
						{
							UpdateAuthor(author);
						}						
						break;
					case 4: // add author to book
						// create a container for known ISBNs just in case
						// "create new book" is selected
						ArrayList<String> ISBNs = new ArrayList<String>();
						
						// display titles menu
						System.out.println("Select a book to add an author to:");
						title = TitlesMenu(ISBNs);
						
						
						// if existing title selected
						if (title.GetEdition() != 0)
						{
							// display authors menu (not linked to title)
							System.out.println("Select an author to add to " + title.GetTitle() + ":");
							author = AuthorsMenu(title.GetISBN());
							
							// add author id and title ISBN to authorISBN table
							if (author != null)
							{
								LinkTitleAuthor(title.GetISBN(),author.GetID());
							}
						}
						else
						{
							// select author for new title
							System.out.println("Select an author for the new book:");
							// pass a non-empty "invalid" ISBN to allow for author creation during selection
							author = AuthorsMenu("0");
							
							// prompt info for new book
							if (author != null)
							{
								
								title = AddTitle(ISBNs);
								
								// link new title and author
								if (title != null)
								{
									LinkTitleAuthor(title.GetISBN(),author.GetID());
								}
							}
						}
						break;
					case 5: // list books by author
						// display authors menu
						System.out.println("List all books by which author?");
						author = AuthorsMenu();
						
						// list titles written by author
						if (author != null)
						{
							if (ListTitles(false, author.GetID()).size() == 0)
								System.out.println("No books written by this author.");
						}
						break;
					case 6: // delete author
						// display authors menu
						System.out.println("Select an author to delete:");
						author = AuthorsMenu();
						
						// recursively delete author and references
						if (author != null)
						{
							DeleteAuthor(author);
						}
						break;
					case 7: // exit
						break;
					}
				}	
			}
		}
	}
	
	
	// ***************************************************************
	//
	// Method: ValidateNumericInput
	//
	// Description: validates that input is a non-negative integer and within 
	//				passed range
	//
	// Parameters: String: input from the user
	//			   Integer: minimum value input can be, null to ignore
	//	   		   Integer: maximum value input can be, null to ignore
	//
	// Returns: Integer: validated output, -1 if input is not valid
	//
	// **************************************************************
	public int ValidateNumericInput(String input, Integer min, Integer max)
	{
		int output = -1;
		
		// validate input is a number
		try
		{
			output = Integer.parseInt(input);

			// input string was a number, check range
			if ((max == null && output < min) || // only min specified or
					(min == null && output > max) || // only max specified or
					((min != null && max != null) && // both specified
					(output < min || output > max)))
			{
				// input out of range
				System.out.println(output + " is not a valid response. Please try again.");
				// invalid input
				output = -1;
			}
		}
		catch (NumberFormatException e)
		{
			// input not a number
			System.out.println("Please input numbers only.");
		}
		
		return output;
	}
	
	
	// ***************************************************************
	//
	// Method: ValidateISBN
	//
	// Description: validates the passed ISBN
	//
	// Parameters: String: ISBN to check
	//			   List(String): a list of existing ISBNs
	//
	// Returns: Boolean: if passed ISBN is valid or not
	//
	// **************************************************************
	public boolean ValidateISBN(String isbn, List<String> ISBNs)
	{
		boolean valid = false;
		
		// first check that there are 10 characters
		if (isbn.length() == 10)
		{
			// check if there is an X on the end or not
			if (isbn.charAt(isbn.length()-1) == 'X')
			{
				// remove X
				isbn = isbn.substring(0,isbn.length()-1);
			}
			
			// check if all characters are numbers
			try
			{
				Integer.parseInt(isbn);
				
				// ISBN is valid
				valid = true;
				
				// make sure ISBN doesn't already exist
				int index = 0;
				while (valid && index < ISBNs.size())
				{
					if (ISBNs.get(index).equals(isbn))
					{
						// ISBN already exists
						// print message to user
						System.out.print("ISBN already in use. Please try a different one.");
						valid = false;
					}
					index++;
				}				
			}
			catch (NumberFormatException e)
			{
				// ISBN not valid
				// print invalid message
				System.out.print("Invalid ISBN format. Please try again.");
			}
		}
		else
		{
			// ISBN not valid
			// print invalid message
			System.out.print("Invalid ISBN format. Please try again.");
		}
		
		return valid;
	}
	
	
	//***************************************************************
	//
	//  Method:       ListTitles
	// 
	//  Description:  displays a list of books, either all or just some
	//				  depending on the parameters
	//
	//  Parameters:   Boolean: whether or not to number each item in the list
	// 				  Integer: List titles written by the author with this ID
	//				  		   Set to 0 to show all titles
	//
	//  Returns:      List(TitleInfo): returned books
	//
	//**************************************************************
	public List<TitleInfo> ListTitles(boolean numbered, int authID)
	{
		// get authors
		List<TitleInfo> titles = database.GetTitles(authID);
		
		// display authors
		for (int i = 0; i < titles.size();i++)
		{
			if (numbered)
				 System.out.print((i + 1) + ". ");
			 System.out.println("Title: " + titles.get(i).GetTitle());
			 System.out.println("ISBN: " + titles.get(i).GetISBN());
			 System.out.println("Edition: " + titles.get(i).GetEdition() + ", Copyright Year: " + titles.get(i).GetYear());
			 System.out.println();
		}
		 
		return titles;
	}
	
	
	//***************************************************************
	//
	//  Method:       TitlesMenu
	// 
	//  Description:  displays a list of titles as a menu to select from
	//				  also handles selection.
	//
	//  Parameters:   List(String): a list of existing ISBNs, populated if 
	//								"create new book" is selected
	//
	//  Returns:      TitleInfo: info of selected book, has an edition of
	//							 0 if "create new book" was selected
	//
	//**************************************************************
	public TitleInfo TitlesMenu(List<String> ISBNs)
	{
		int input = -1;
		TitleInfo title = null;
		int total = 0;
		
		List<TitleInfo> titles = null;
		
		while (input == -1)
		{
			// display list of all titles
			titles = ListTitles(true, 0);
			total = titles.size();
			if (total == 0)
				System.out.println("No Books found.");
			
			System.out.println((total + 1) + ". Create New Book");
			System.out.println((total + 2) + ". Return to Main Menu");
			
			// get input
			String inputStr = GetUserInput();
			
			// validate input (add one to total range to allow for cancel option)
			input = ValidateNumericInput(inputStr, 1, total + 2);
		}
			
		// check for title selection
		if (input < total+1)
		{
			// get title info from selection
			title = titles.get(input-1);			
		}
		// check for create new title selection
		else if (input == total+1)
		{			
			// populate ISBNs list with all known ISBNs
			for (int i = 0; i < titles.size(); i++)
			{
				ISBNs.add(titles.get(i).GetISBN());
			}
			
			// set title info to default info with an edition of 0
			title = new TitleInfo();
		}
		
		return title;
	}
	
		
	//***************************************************************
	//
	//  Method:       AddTitle
	// 
	//  Description:  prompts user for info for new author, then sends 
	//				  insert request to database
	//
	//  Parameters:   List(String): a list of existing ISBNs, used during 
	//								 ISBN validation
	//
	//  Returns:      TitleInfo: info of the created title
	//							 null if creation unsuccessful
	//
	//**************************************************************
	public TitleInfo AddTitle(List<String> ISBNs)
	{
		TitleInfo book = null;
		String title = "";
		String ISBN = "";
		int edition = -1;
		String yearStr = "";
		String input = "";
		
		// prompt user for a last name
		System.out.println("\nInput a title for new book:"
				+ "\n(input -1 to cancel and return to main menu)");
		input = GetUserInput();
		
		// if not canceling creation
		if (!input.equals("-1"))
		{
			title = input;
			
			// prompt user for a ISBN
			boolean valid = false;
			while (!valid)
			{
				System.out.println("\nInput a valid ISBN for new book:"
						+ "\n(Valid format: ########## OR #########X # is any digit 0 through 9)"
						+ "\n(input -1 to cancel and return to main menu)");
				input = GetUserInput();
				
				// if not canceling creation
				if (!input.equals("-1"))
				{
					// validate ISBN format
					if (ValidateISBN(input, ISBNs))
					{
						// valid format
						valid = true;
						ISBN = input;
					}
				}
				else
				{
					// set valid to stop looping
					valid = true;
				}
			}
		}
		
		
		// if not canceling creation
		if (!input.equals("-1"))
		{
			// prompt user for edition number
			String editionStr = "";
			while (edition == -1)
			{
				System.out.println("\nInput an edition number for new book:\n"
						+ "(positive numbers only)"
						+ "\n(input -1 to cancel and return to main menu)");
				input = GetUserInput();
				
				// if not canceling creation
				if (!input.equals("-1"))
				{
					editionStr = input;
					// validate that edition is a number and greater than 0
					edition = ValidateNumericInput(editionStr, 1, null);
				}
				else
				{
					// set edition to exit loop
					edition = 0;
				}
			}
		}
		
		// if not canceling creation
		if (!input.equals("-1"))
		{
			// prompt user for copyright year
			int year = -1;
			while (year == -1)
			{
				System.out.println("\nInput a copyright year for new book:\n"
						+ "(Valid years include 1800 to Present)"
						+ "\n(input -1 to cancel and return to main menu)");
				input = GetUserInput();
				
				// if not canceling creation
				if (!input.equals("-1"))
				{
					yearStr = input;
					// validate that year is a number and between 1800 and present year
					year = ValidateNumericInput(yearStr, 1800, 2022);
				}
				else
				{
					// set year to exit loop
					year = 0;
				}
			}
		}
		
		
		// if not canceling creation
		if (!input.equals("-1"))
		{
			// send insert request to database
			book = database.AddTitle(ISBN, title, edition, yearStr);
		}
		
		// return title info for use later
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
		// send insert database request
		database.LinkTitleAuthor(ISBN, authID);
	}
	
	
	//***************************************************************
	//
	//  Method:       ListAuthors
	// 
	//  Description:  displays a list of authors, either all or just some
	//				  depending on the parameters
	//
	//  Parameters:   Boolean: whether or not to number each item in the list
	//				  String: only show authors not linked to this ISBN
	//						  pass an empty string to show all authors
	//
	//  Returns:      List(AuthorInfo): returned authors
	//
	//**************************************************************
	public List<AuthorInfo> ListAuthors(boolean numbered,  String ISBN)
	{
		// get authors
		List<AuthorInfo> authors = database.GetAuthors(ISBN);
		
		// display authors
		for (int i = 0; i < authors.size();i++)
		{
			if (numbered)
				 System.out.print((i + 1) + ". ");
			 System.out.println(authors.get(i).GetFirstName() + " " + authors.get(i).GetLastName());
		}
		 
		return authors;
	}
	
	
	//***************************************************************
	//
	//  Method:       AuthorsMenu
	// 
	//  Description:  displays a list of authors as a menu to select from
	//
	//  Parameters:   String: only show authors not linked to this ISBN
	//						  pass an empty string to show all authors
	//
	//  Returns:      AuthorInfo: info of the selected author
	//
	//**************************************************************
	public AuthorInfo AuthorsMenu()
	{
		return AuthorsMenu("");
	}
	public AuthorInfo AuthorsMenu(String ISBN)
	{
		int input = -1;
		AuthorInfo author = null;
		int total = 0;
		
		List<AuthorInfo> authors = null;
		
		while (input == -1)
		{
			// display list of authors
			authors = ListAuthors(true, ISBN);
			total = authors.size();
			if (total == 0)
			{
				// if we are looking for all authors
				if (ISBN.isEmpty())
				{
					// generic message
					System.out.println("No Authors found.");
				}
				else
				{
					// no more unlinked authors message
					System.out.println("All Authors are linked to this book.");
				}
			}
			if (!ISBN.isEmpty())
			{
				System.out.println((total + 1) + ". Create New Author");
				System.out.println((total + 2) + ". Return to Main Menu");	
			}
			else
			{
				System.out.println((total + 1) + ". Return to Main Menu");	
			}
			// get input
			String inputStr = GetUserInput();
			
			// validate input (add one or two to total range to allow for cancel option)
			if (!ISBN.isEmpty())
				input = ValidateNumericInput(inputStr, 1, total + 2);
			else
				input = ValidateNumericInput(inputStr, 1, total + 1);
		}
			
		// check for author selection
		if (input < total + 1)
		{
			// get author info from selection
			author = authors.get(input-1);
		}
		// check for add author selection if we are linking a title
		else if ((input == total + 1) && !ISBN.isEmpty())
		{
			// prompt for author info
			author = AddAuthor();
		}
			
		return author;
	}
	
	
	//***************************************************************
	//
	//  Method:       UpdateAuthor
	// 
	//  Description:  prompts user for new info for passed author, then send 
	//				  update request to database
	//
	//  Parameters:   AuthorInfo: info of author to update
	//
	//  Returns:      N/A
	//
	//**************************************************************
	public void UpdateAuthor(AuthorInfo author)
	{
		// prompt user for a new first name
		System.out.println("Input a new first name for "
				+ author.GetFirstName() + " " + author.GetLastName()
				+ ":\nLeave blank and press enter to skip.\n");
		String first = inputStream.nextLine();
		
		// prompt user for a new last name
		System.out.println("Input a new last name for "
				+ author.GetFirstName() + " " + author.GetLastName()
				+ ":\nLeave blank and press enter to skip.\n");
		String last = inputStream.nextLine();
		
		// check if nothing was entered at all
		if (last.isEmpty() && first.isEmpty())
		{
			// output message
			System.out.println("Nothing entered, returning to Main Menu.");
		}
		else
		{
			// fill in blank inputs
			if (first.isEmpty())
				first = author.GetFirstName();
			if (last.isEmpty())
				last = author.GetLastName();
			
			// send update request to database
			database.UpdateAuthor(author.GetID(), first, last);
		}
	}
	
	
	//***************************************************************
	//
	//  Method:       AddAuthor
	// 
	//  Description:  prompts user for info for new author, then sends 
	//				  insert request to database
	//
	//  Parameters:   None
	//
	//  Returns:      AuthorInfo: Info of the inserted author
	//
	//**************************************************************
	public AuthorInfo AddAuthor()
	{
		String first = "";
		String last = "";
		AuthorInfo author = null;
		
		// prompt user for a first name
		System.out.println("\nInput a first name for new author:"
				+ "\n(input -1 to cancel and return to main menu)");
		String input = GetUserInput();
		
		// if not canceling creation
		if (!input.equals("-1"))
		{
			first = input;
			
			// prompt user for a last name
			System.out.println("\nInput a last name for new author:"
					+ "\n(input -1 to cancel and return to main menu)");
			input = GetUserInput();
		}
		
		if (!input.equals("-1"))
		{
			last = input;
			
			// send insert request to database
			author = database.AddAuthor(first, last);
		}	
		
		return author;
	}
	
	
	//***************************************************************
	//
	//  Method:       DeleteAuthor
	// 
	//  Description:  sends delete request to database
	//
	//  Parameters:   AuthorInfo: info of author to delete
	//
	//  Returns:      N/A
	//
	//**************************************************************
	public void DeleteAuthor(AuthorInfo author)
	{
		// send delete request to database
		database.DeleteAuthor(author.GetID());
	}
	
	// ***************************************************************
	//
	// Method: GetUserInput
	//
	// Description: prompts the user for input and returns user input
	//
	// Parameters: None
	//
	// Returns: String: input from user
	//
	// **************************************************************
	public String GetUserInput()
	{			
		String input = "";
		while(input.equals(""))
		{
			input = inputStream.nextLine();
		}
		
		return input;
	}
	
	
	//***************************************************************
	//
	//  Method:       developerInfo (Non Static)
	// 
	//  Description:  The developer information method of the program
	//
	//  Parameters:   None
	//
	//  Returns:      N/A 
	//
	//**************************************************************
	public void developerInfo()
	{
		System.out.println("Name:    Michael Franklin");
		System.out.println("Course:  COSC 4301 Modern Programming");
		System.out.println("Project: Nine\n");
	}

}

