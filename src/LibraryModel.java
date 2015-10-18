/*
 * LibraryModel.java
 * Author:
 * Created on:
 */



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.*;

public class LibraryModel {
	//db connection.
	Connection con;

    // For use in creating dialogs and making them modal
    private JFrame dialogParent;

    public LibraryModel(JFrame parent, String userid, String password) {
	dialogParent = parent;


		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			con = DriverManager.getConnection("jdbc:postgresql://db.ecs.vuw.ac.nz/"+userid+"_jdbc", userid, password);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}




    }
/*
 * method for looking up a single book ISBN
 * returns a string detailing the book title
 * and the authors, ordered by AuthorSeqNo.
 */
    public String bookLookup(int isbn) {

    	//thing to lookup.
    	String lookupTitle = "SELECT * FROM Book WHERE Book.ISBN=" + isbn;

    	String lookupAuthor = "SELECT author.name, author.surname, Book_Author.authorseqno "
    			+"FROM Book_Author, author WHERE Book_Author.ISBN="+ isbn
    			+"AND author.authorid = Book_Author.authorid ORDER BY authorseqno";

    	String toReturn = " ";
    	//now we crate a statement and execture query.
    	try {

    		//statement and results for title return.
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(lookupTitle);
			//iterate over rs to get book's details.
			if(rs.next()){
				//get's title.
				//System.out.println(rs.getString("Title"));
				toReturn ="Title of Book: " + rs.getString("Title") +"\n";
			}
			//return if no book found. don't waste time checking authors.
			else{ return "No book. Please check your ISBN.";}
			//statement and results for Author lookup.
			Statement stmt2 =con.createStatement();
			ResultSet rs2 = stmt.executeQuery(lookupAuthor);

			//check author details are there
			while(rs2.next()){
				//concat authors to return string.
				toReturn +=  "\nAuthor: "+ rs2.getString("name")+rs2.getString("surname")+"\n";

			}

			return toReturn;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	return "No book. Please check your isbn";
    }

    /*
     * returns a catalogue detailing all books in the database.
     * returns: String detailing catalogue, ordered by ISBN.
     */
    public String showCatalogue() {

    	String toReturn = "********CATALOGUE*******\n";

    	String query = "SELECT * FROM Book";

    	Statement stmt;
		try {

			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);


			while(rs.next()){

				toReturn+= "----------------------------------------------------\n";
				toReturn+= "---"+ rs.getString("Title")+ "\n";

				toReturn += "ISBN: " + rs.getString("ISBN") +"\n";
				toReturn += "Edition number: " + rs.getString("Edition_No") +"\n";

				toReturn += "copies in stock versus total copies: " + rs.getString("NumLeft") + "/";
				toReturn += rs.getString("NumofCop")+ "\n";
				toReturn += "---------------------------------------------------\n";

			}
			//if nothing was appended to Catalogue return that we found no books.
			if(toReturn.equals("CATALOGUE")){ return "no books found";}
			//if reaches here all is well. return catalogue.
			return toReturn;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	return "Show Catalogue Stub";
    }
/*
 * A method that will return a string detailing all loaned books.
 * It will detail the customers ID and then the book details.
 * Returns: String detailing all loaned books.
 */
    public String showLoanedBooks() {
    	//query and statement.
    	String query = "SELECT * FROM Cust_Book, Book WHERE Cust_Book.isbn = Book.isbn";
    	Statement stmt;
    	//the string to return with all answers.
    	String toReturn = "";
    	try {

    		stmt = con.createStatement();
			ResultSet rs =stmt.executeQuery(query);

			while(rs.next()){
				//basic data to print.
				toReturn += "\n===============loaned===============\n";
				toReturn += "CustomerId: " +rs.getString("CustomerId")+ "\n";

				toReturn += "===book details===\n";
				toReturn += "Book Title: " + rs.getString("Title");

			}

			if(!toReturn.equals("")){ return toReturn;}
			return("nothing on loan");
    	} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	return "Show Loaned Books Stub";
    }



    /*
     * method that returns string showing author details
     * returns: String AuthorDetails.
     */
    public String showAuthor(int authorID) {
    	String toReturn = "";
    	String query = "SELECT * FROM Author WHERE Author.AuthorId =" + authorID;
    	try {

    		Statement stmt = con.createStatement();
    		ResultSet rs = stmt.executeQuery(query);

    		while(rs.next()){
                toReturn+= "=========AUTHOR=========\n";
    			toReturn+= rs.getString("Name") + " " + rs.getString("Surname");
    			toReturn+= "=========================";
    		}

    		if(!toReturn.equals("")){return toReturn;}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	return "No author found. Check authorID.";
    }


    /*
     * Displays all Authors details;
     * returns: String detailing all customers info.
     */
    public String showAllAuthors() {
    	String toReturn = "";
    	String query = "SELECT * FROM Author ORDER BY Author.AuthorId";
    	try {

    		Statement stmt = con.createStatement();
    		ResultSet rs = stmt.executeQuery(query);

    		while(rs.next()){
                toReturn+= "\n=========AUTHOR=========\n";
    			toReturn+= "        " + rs.getString("Name") + " " + rs.getString("Surname");
    			toReturn+= "\n=======================";
    		}

    		if(!toReturn.equals("")){return toReturn;}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	return "No authors.";
    }
    /*
     * Shows a single customers details based on input customerID
     * returns: String detailing a customer.
     */
    public String showCustomer(int customerID) {
       	String toReturn = "";
    	String query = "SELECT * FROM Customer WHERE Customer.CustomerID =" + customerID;
    	try {

    		Statement stmt = con.createStatement();
    		ResultSet rs = stmt.executeQuery(query);

    		while(rs.next()){
                toReturn+= "\n=========Customer=========\n";
    			toReturn+= rs.getString("F_Name") + " " + rs.getString("L_Name");
    			toReturn+= "\n========================";
    		}

    		if(!toReturn.equals("")){return toReturn;}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	return "No Customer found. Check CustomerID.";
    }

    /*
     * Shows all customers ordered by CustomerId.
     * returns: list of customers in string form.
     */
    public String showAllCustomers() {
     	String toReturn = "";
    	String query = "SELECT * FROM Customer ORDER BY Customer.CustomerID";
    	try {

    		Statement stmt = con.createStatement();
    		ResultSet rs = stmt.executeQuery(query);


    		while(rs.next()){

                toReturn+= "\n=========Customer=========\n";
    			toReturn+= "        " + rs.getString("F_Name") + " " + rs.getString("L_Name");
    			toReturn+= "\n========================";
    		}

    		if(!toReturn.equals("")){return toReturn;}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	return "No Customers.";
    }

    /*
     *Checks whether customer and book exists.
     *If so locks the db.
     *adds corresponding tuple to Cust_Book.
     *alter entry in Book to show that a copy has been borrowed.
     *unlocks db.
     *
     *returns: string with book details.
     */
    public String borrowBook(int isbn, int customerID, int day, int month, int year) {
    	try {
    	
    	con.setAutoCommit(false); // lock the db while we alter tables.
		con.setReadOnly(false);
    	
    	
    	//variables to save
    	int booksLeft = 0;

    	//Check customer.
    	if(showCustomer(customerID).equals("No Customer found. Check CustomerID.")){
    		return "Customer non-existent.";
    	}
    	//check book.
    	if(bookLookup(isbn).equals("No book. Please check your ISBN.")){
    		return "book non-existent.";
    	}
    	//check if there are any copies left.
    	String query = "SELECT b.NumLeft FROM Book b WHERE b.ISBN =" + isbn;
    	Statement stmt;
	   
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			if(rs.next()){
				booksLeft = rs.getInt("NumLeft");
				if(booksLeft<=0){return "no copies left";}
			}

	

	   	/*
	   	 * Now we have checked that the request is legitimate we
	   	 * can begin inserting and altering the needed tuples.
	   	 */


	   	
			//create statement
			stmt = con.createStatement();
			//create an update insertion string to use for updating Cust_Book.
			String insertCust = "Insert Into Cust_Book VALUES (" + isbn + ", '" + year + "-" + month + "-" + day+ "', " + customerID + ");";
			stmt.executeUpdate(insertCust); //insert into.

			JOptionPane.showMessageDialog(null, "Lock is in place, press ok to continue");


			//update Book table.
			String upDateBook = "UPDATE Book SET NumLeft = " + (booksLeft - 1) + " WHERE ISBN = " + isbn + ";";
			stmt.executeUpdate(upDateBook);


			con.commit();
			con.setAutoCommit(true); // unlock the db.
			con.setReadOnly(true); //set access to read only.

		} catch (SQLException e) {	e.printStackTrace();}



    	return "Borrow Book Stub";
    }

    
    /*
     * 1st.Need to check if book exists and customer exists. 
     * 2nd. lock the tables
     * 3rd. add one to the books total.
     */
    public String returnBook(int isbn, int customerid) {
    	
    	String query = "SELECT * FROM Book WHERE isbn = " + isbn;
    	
    	try {
    		//check that book exists and customer exists.
    		if(bookLookup(isbn).equals("No book. Please check your ISBN.")){ return "book does not exist";}
    		if(showCustomer(customerid).equals("No Customer found. Check CustomerID.")){return "not a valid customer";}
    		
    		
    		
    		
    		con.setAutoCommit(false); // lock the db while we alter tables.
    		con.setReadOnly(false);
    		
    		Statement stmt = con.createStatement();
    		ResultSet rs = stmt.executeQuery(query);
    		
    		if(rs.next()){
    		
    		int booksLeft =	rs.getInt("NumLeft");
    		int totalBooks = rs.getInt("numofcop");
    		
    		if(booksLeft >= totalBooks){return "that books all there!";}
    		
    		//delete the entry from cust books.
    		String deleteCustBooks = "DELETE FROM Cust_Book WHERE isbn =" +isbn + " AND customerID =" + customerid + ";";
    		String upDateBook = "UPDATE Book SET NumLeft = " + (booksLeft + 1) + " WHERE ISBN = " + isbn + ";";
    		
    		
    		stmt = con.createStatement();
    		stmt.executeUpdate(deleteCustBooks);//delete bookloan record from the record of books on loan
    		stmt.executeUpdate(upDateBook);//add the book total of books left in the books record.
    		
    		return "succesfully returned book: " + bookLookup(isbn);
    		}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	return "Return Book Stub";
    }

    public void closeDBConnection() {
    }

    public String deleteCus(int customerID) {
    	
    	if(showCustomer(customerID).equals("No Customer found. Check CustomerID.")){return "customer is non existent";}
    	
    	try {
			
    		con.setAutoCommit(false);
			con.setReadOnly(false);
			
			String deleteCust = "DELETE FROM Customer WHERE customerID = " + customerID;
			
			Statement stmt = con.createStatement();
			stmt = con.createStatement();
    		stmt.executeUpdate(deleteCust);
			
    		return "deleted.";
			
    	} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // lock the db while we alter tables.
		
    	
    	
    	return "Somehow didn't work.";
    }

    public String deleteAuthor(int authorID) {

    	if(showAuthor(authorID).equals("No author found. Check authorID.")){return "author is non existent";}

    	try {

    		con.setAutoCommit(false);// lock the db while we alter tables.
    		con.setReadOnly(false);

    		String deleteAuth = "DELETE FROM author WHERE authorID = " + authorID;

    		Statement stmt = con.createStatement();
    		stmt = con.createStatement();
    		stmt.executeUpdate(deleteAuth);

    		return "deleted.";

    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	} 


    	return "Somehow didn't work.";
    }

    public String deleteBook(int isbn) {
    	
    	
    	if(bookLookup(isbn).equals("No book. Please check your ISBN.")){return "Book is non existent";}

    	try {

    		con.setAutoCommit(false);// lock the db while we alter tables.
    		con.setReadOnly(false);

    		String deleteBook= "DELETE FROM book WHERE isbn = " + isbn;

    		Statement stmt = con.createStatement();
    		stmt = con.createStatement();
    		stmt.executeUpdate(deleteBook);

    		return "deleted.";

    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	} 
    	
    	return "didn't work =(";
    }
}