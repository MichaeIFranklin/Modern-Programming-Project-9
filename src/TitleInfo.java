//******************************************************************************
//
//  Developer:     Michael Franklin
//
//  Project #:     Project 9
//
//  File Name:     TitleInfo.java
//
//  Course:        COSC 4301 - Modern Programming
//
//  Due Date:      05/05/2022
//
//  Instructor:    Fred Kumi 
//
//  Description:   Holds info about returned book records
//
//
//******************************************************************************

public class TitleInfo {
	private String ISBN;
	private String Title;
	private int Edition;
	private String Year;
	
	public TitleInfo(String ISBN, String Title, int Edition, String Year)
	{
		this.ISBN = ISBN;
		this.Title = Title;
		this.Edition = Edition;
		this.Year = Year;
	}
	
	public TitleInfo() {
		// use default empty values
		this.ISBN = "";
		this.Title = "";
		this.Edition = 0;
		this.Year = "";
	}

	// accessors
	public int GetEdition()
	{
		return Edition;
	}
	public String GetTitle()
	{
		return Title;
	}
	public String GetISBN()
	{
		return ISBN;
	}
	public String GetYear()
	{
		return Year;
	}
}
