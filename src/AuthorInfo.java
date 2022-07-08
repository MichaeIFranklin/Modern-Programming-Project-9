//******************************************************************************
//
//  Developer:     Michael Franklin
//
//  Project #:     Project 9
//
//  File Name:     AuthorInfo.java
//
//  Course:        COSC 4301 - Modern Programming
//
//  Due Date:      05/05/2022
//
//  Instructor:    Fred Kumi 
//
//  Description:   Holds info about returned author records
//
//
//******************************************************************************

public class AuthorInfo {
	private int ID;
	private String firstName;
	private String lastName;
	
	public AuthorInfo(int ID, String firstName, String lastName)
	{
		this.ID = ID;
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	// accessors
	public int GetID()
	{
		return ID;
	}
	public String GetFirstName()
	{
		return firstName;
	}
	public String GetLastName()
	{
		return lastName;
	}
}
