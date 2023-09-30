package movieTheater;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.Scanner;
import java.util.Date;
//MOVIE THEATRE PROGRAM
//LUKE EDWARDS COMPUTER SCIENCE 11 2021
//WESTERN CANADA HIGHSCHOOl
//FOREWORD: This is the main class for the program. This is the program that the client would interact with to make their purchases.
//VERY IMPORTANT PLEASE READ: LIKE A REAL WEBSITE, THE SERVER MUST BE ACTIVE BEFORE THE CLIENT CAN BE USED. 
//IF THE SERVER IS MANUALLY TERMINATED BY THE USER WHILE THE MAIN CLIENT IS STILL RUNNING, THE CLIENT WILL STILL BE OPERATIONAL
//BUT THE SERVERR WILL BE UNABLE TO START/CONNECT TO THE CLIENT UNTIL BOTH ARE TERMINATED AND THE FIRST STEPS ARE REPEATED
//In the login sequence, you are more than welcome to create your own account, or check the accountInformation text file and use that login information. 
//WARNING, MANUALLY CHANGING ANY OF THE FILES MAY PRODUCE CAUSE THE PROGRAM TO FAIL, ALTER THE FILES AT YOUR OWN WARNING.
public class movieTheaterCLS {
	static String seperate ="=======================================================================";
	static String [] moviesavailable = {"Infinity War", "SOUL", "Whiplash", "The Godfather", "Macbeth", "Greyhound", "The Great Gatsby", "Insidious"}; //creates a list that holds every movie available. Used for menu mainly
	public static void displayArray(int manta[][]) {
		System.out.println(seperate);
		System.out.println("Here are the available seats (O = Not taken)(X = Taken)");
		System.out.println();
		for(int col = 0; col < manta.length; col++ ) {
			for(int row = 0; row < manta.length; row++) {
				 if(manta[col][row] == 0)System.out.print("| O |");
				 if(manta[col][row] == 1)System.out.print("| X |");		//Method takes the movie that was selected by client and displays it
				}
			System.out.print(" [" + col + "]");
			 if(col == 2)System.out.print("    COLUMNS");					//The rest of the method just adds quality of viewing formatting.
			System.out.println();
		}
		for(int row = 0; row < manta.length; row++) {
			System.out.print(" [" + row + "] ");
		}
		System.out.println();		

		for(int row = 0; row < manta.length/2; row++) {					//The ROW and COLUMN text additions actual calculate where they should be placed as the array is displayed
			System.out.print("      ");									//If I wanted to add bigger movie theaters with more seats, the display could handle it properly
		}
		System.out.print("ROW");		

		System.out.println();

	}
	public static int[][] occupancy(String movieName, String id, int manta[][], Socket socket, int sizeArray) throws IOException {
		Scanner scanner = new Scanner(System.in);
		int columnVal = 0;														//occupancy() first checks the availability of seats, then performs the purchasing stage
		int rowVal = 0;
		boolean error = false;
		boolean cancel = false, restart = false;
		do {
			restart = false;
			do {
				error = false;
				System.out.println("Current movie selected: " + movieName);
				displayArray(manta);
				System.out.println("Please enter the COLUMN NUMBER (VERTICAL) you would like to purchase");		//Asking client for seat selection
				do {
					error = false;
					try {
						columnVal = scanner.nextInt();
					}
					catch(Exception e){
						error = true;
						System.out.println("ERROR: Please enter a valid seat.");
						scanner.nextLine();
					}
					if(columnVal < 0||columnVal > sizeArray)System.out.println("Please enter a valid number.");
				}while(error||columnVal < 0||columnVal > sizeArray);
				System.out.println("Please enter the ROW NUMBER (HORITZONTAL) you would like to purchase");
				do {
					error = false;
					try {
						rowVal = scanner.nextInt();
					}
					catch(Exception e){
						error = true;
						System.out.println("ERROR: Please enter a valid seat.");
						scanner.nextLine();
					}
					if(rowVal < 0||rowVal > sizeArray)System.out.println("Please enter a valid number.");
				}while(error||rowVal < 0||rowVal > sizeArray);
				if(manta[columnVal][rowVal] == 1) {
					System.out.println("That seat is not available. Please enter a different selection. ");	
					System.out.println("If you need to see the seat availiability once more, please enter 1"); //If seat is not available try selection again.
					System.out.println("If not, please enter any other number.");
					try{//TODO ADD LEAVE FUNCTION HERE
					if(scanner.nextInt() == 1) {
						displayArray(manta);
						continue;
						}
					}catch(Exception e) {
						error = true;
						System.out.println("Please enter a valid number and try again");
						scanner.nextLine();
						}
					}
			}while(manta[columnVal][rowVal] == 1 || error);
			System.out.println("Seat available! Would you like to pay with cash, Cineplex points, or select another seat?\n" + seperate); //If seat is open, continue with checkout
			System.out.println("CURRENT SEAT: Row " +  rowVal + " | Column: " + columnVal  + "\nCURRENT CINEPLEX POINTS BALANCE: " + currentBalance(id));
			
			double checkoutCash = (4.50 + (1.5 * columnVal)); //Formula to determine price of seat, further back the seat, more elevated price and vice versa;
			checkoutCash = checkoutCash + (checkoutCash * 0.05); //with tax calculation
			int points = (int) (checkoutCash * 100); //Point calculation
			
			System.out.println("TOTAL COST IN CASH WITH TAX = " + checkoutCash+ "\nTOTAL COST IN POINTS : " + points);
			System.out.println(seperate + "\n 1. Cash \n 2. Cineplex Points \n 3. Select different seat \n 4. Exit Selection"); //Checkout options
			int pick = scanner.nextInt();
			if(pick == 1) {
				System.out.println(seperate + "\nTransaction successful! " + points + " points were added to your account!"); //balanceUpdate() is meant to add points to the account, in correspondence to the amount of cash spent
				balanceUpdate(id, points);
			}
			else if(pick == 2) {                                  //Paying with points, checks if the client has enough points in their balance to complete the transaction
				if(currentBalance(id) < points) {
					System.out.println(seperate + "\nUnsuccessful transaction, you do not seem to have the necessary points for this transaction..."); //if not, they are returned to the checkout screen
				}
				if(currentBalance(id) >= points) {
					balanceUpdate(id, -points);
					System.out.println(seperate + "\nTransaction successful! " + points + " points were subtracted from your account!");
				}
			}
			else if(pick == 3) {
				restart = true; //If they wish to pick another seat, this will bring them to the seat selection stage
			}
			else if(pick == 4) {
				cancel = true; //If they wish to completely exit the transaction, this will bring them to the main page.
				break;
			}
			
			
		}while(restart == true);
		FileWriter append = new FileWriter(movieName);
		if(cancel == false) {
			System.out.println("CONFIRMATION: Your seat is Column: " + rowVal + " and Row: " + columnVal + ". Enjoy your film!"); //IF the checkout is successful, confirm their seat has been established
			manta[columnVal][rowVal] = 1; //Changes the array to be have the new seat.
			//displayArray(manta);
			for(int col = 0; col < manta.length; col++) {
				for(int row = 0; row < manta.length; row++) {
					   append.write(manta[col][row] + " ");                //Opens up the file of the movie and writes the new array
					}
				 append.write("\n");
				}
			try {
				PrintWriter send = new PrintWriter(socket.getOutputStream(), true); //instanciates output PW to write to client
				send.println(id);
				send.println(movieName);
				send.println(columnVal); //Sends the info for the purchase to the server to be used for the receipt.
				send.println(rowVal);
				//send.close();
			}catch(Exception e) {
				System.out.println("[SERVER] Recept line failiure. Could not connect to server port. (ServerCLS not running");//If connection is not made, continue but notify the client.
			}
		}
		append.close();
		return manta; //Return the new array
	}
	public static int[][] seatDatabase(String file, int sizeY, int sizeX) throws FileNotFoundException{
		File infile = new File(file);
		Scanner content = new Scanner(infile);
		int[][] arr = new int[sizeY][sizeX];
		for(int col = 0; col < arr.length; col++) {
			for(int row = 0; row < arr.length; row++) {
				   arr[col][row] = content.nextInt();	//Reads the file of the movie, takes the values from the file and create an array
				 
				 //System.out.print(infinityWar[col][row] + " ");
				}
			 //System.out.println();
			}
		content.close();
		return arr;
	}
	public static Integer balanceUpdate(String id, Integer checkoutTotal) throws IOException { //Updates the balance of the client, used during the checkout process
																								//The method can be used whether a subtraction or addition of points is taking place because of the formula used
		File infile = new File("accountBalance.txt");    										//Opens the file containing the points of the clients
		Scanner contents = new Scanner(infile);
		String document = " ";
		Integer currentBalance = 0;
		while(contents.hasNext()){
				if(contents.next().equals(id)) {					//Finds the email of the client currently logged in and finds their points
					currentBalance = contents.nextInt();			//Points are taken initially as an int value
			}
		}
		contents = new Scanner(infile);
		while(contents.hasNext()){
			document = document + contents.next() + System.lineSeparator();  //The entire document is rewritten into a string
		}
		Integer newBalance = currentBalance + checkoutTotal; //The int taken from the txt file is either (+/-) based on the transaction being preformed
		String oldBalance = currentBalance.toString(); //The old balance of points is changed back into a string
		String stringBalance = newBalance.toString(); //The new balance in changed into a string
		
		String newContent = document.replaceAll(oldBalance, stringBalance);  //The string that contains the original txt file gets appended to change to the old value of points
		contents = new Scanner(infile);
		FileWriter myWriter = new FileWriter("accountBalance.txt");
		myWriter.write(newContent); //The file is rewritten with the changed points
		myWriter.close();
		return currentBalance;   //Return the new balance of points in the account
	}
	public static int currentBalance(String id) throws FileNotFoundException { //Gives the user the current balance of their points
		int currentBalance = 0;
		File infile = new File("accountBalance.txt");   //Preforms the same steps as balanceUpdate() but instead of appending the values it just returns the current balance
		Scanner contents = new Scanner(infile);
		while(contents.hasNext()){
			if(contents.next().equals(id)) {
				currentBalance = contents.nextInt();
		}
	}
		return currentBalance;
	} 
	public static void main(String[] args) throws IOException {
		try {
			Socket socket = new Socket("127.0.0.1", 9090); //Tries to connect to the ServerCLS, if not able to connect, terminate the class.
			boolean logout, successfulLog;
			boolean error = false;
			String id = "";
			int menu = 0;
			Scanner input = new Scanner(System.in);
			boolean credentials = false;
			while(true) {
			do {
				do {
					do {
						successfulLog = true;
						logout = false;
						error = false;	//LOGIN sequence
						System.out.println(seperate);
						System.out.println("		LOGIN"); 
						System.out.println(seperate);
						credentials = false;
						System.out.println("If you need to make an account, please input '1'"); //Asks the user if they already have an account or not
						System.out.println("If you have an account with us already, please input '2");
						
					try {
						menu = input.nextInt();
					}
					catch(Exception e) {
						System.out.println("Please input a valid number");
						input.nextLine();
						error = true;
					}
					}while(error);
					if(menu == 1) {
						String email = "";
						String password = "";
						String confirm = "";
						do {
							error = false;
							System.out.println(seperate);
							System.out.println("Please input your email you would like associated with the account");
							try{
								email = input.next(); //Asks for the email to be used
								email.replaceAll(" ", ""); //Ensures there are no spaces in the password
							}
							catch(Exception e) {
								error = true;
								System.out.println(seperate + "The inputed email was not handled properly. Try again");
								input.nextLine();
							}
							if(!email.contains("@"))System.out.println("Sorry, your email is not valid. (Please enter an email with an '@' symbol)");
						}while(!email.contains("@") || error); //Do while email does not contain an @ symbol
						do {
							error = false;
							System.out.println(seperate);
							System.out.println("Please input a password for the account (Ensure that the password is between 4-10 characters long and no spaces allowed!)");
							try {
							password = input.next();
							}
							catch(Exception e) {
							error = true;
							System.out.println(seperate + "The inputed password was not handled properly. Try again");
							input.nextLine();
							}
							password.replaceAll(" ", ""); //Ensures there are no spaces in the password.
							if(password.length() > 10 || password.length() < 4)System.out.println("Sorry, your password is not valid.(Between 4-10 characters)");
						}while(error || password.length() > 10 || password.length() < 4); // do while password doesn't meet the standards for a password
						do {
							error = false;
							System.out.println(seperate);
							System.out.println("\t\t\tNEW ACCOUNT");
							System.out.println("EMAIL: " + email); //Displays new account details
							System.out.println("PASSWORD: " + password);
							System.out.println(seperate + "\nCONFIRM THESE DETAILS?W (y/n)");
						try{
							confirm = input.next();
							confirm.toLowerCase();
						}
						catch(Exception e) {
							error = true;
							System.out.println(seperate + "The inputed answer was not one of the options listed. Try again"); //Retry response if answer is not valid
							}
						}while(error|| !confirm.equalsIgnoreCase("y") && !confirm.equalsIgnoreCase("n") );
						if(confirm.equalsIgnoreCase("y")) {
							File pointsFile = new File("accountBalance.txt");    			//Opens the file containing the points of the clients
							Scanner membership = new Scanner(pointsFile);
							File accountFile = new File("clientInformation.txt");
							String document1 = ""; 
							String document2 = ""; 
							Scanner account = new Scanner(accountFile);
							while(membership.hasNext()){
								document1 = document1 + membership.next() + System.lineSeparator();  //The entire document is rewritten into a string
							}
							while(account.hasNext()){
								document2 = document2 + account.next() + System.lineSeparator();  //The entire document is rewritten into a string
							}
							document1 = document1 + "\n" + email + "\n0"; //Create new points for new account
							document2 = document2 + "\n" + email + "\n" + password; //append the account document with new account
							FileWriter pointsNew = new FileWriter(pointsFile);
							FileWriter accountNew = new FileWriter(accountFile);
							pointsNew.write(document1);
							accountNew.write(document2);
							accountNew.close();
							pointsNew.close();
							System.out.println("Account has been created! Enjoy!");
							id = email;
						}
						else {
							successfulLog = false;
							continue;
						}
		
					}
					
					else if(menu == 2) {
							File infile = new File("clientInformation.txt");
							Scanner contents = new Scanner(infile);
							System.out.println(">First, please enter the email associated with your account");
							Scanner scanner = new Scanner(System.in);
							id = scanner.next();																//Asks client for Email and Password
							System.out.println(">Now, please enter the password associated with your account");
							String password = scanner.next();
							while(contents.hasNext()){
								if(contents.next().equals(id)) {
									if(contents.next().equals(password)) {									//Takes email and looks through a .txt file to find match, if the next string after matches the password then the login is successful
										credentials = true;
										System.out.println("Login Successful! Welcome user: " + id);
										break;
									}
								}
							}
							
							if(!credentials) {
								System.out.println("Sorry! It appears your login information was incorrect! Please try again."); //Will restart if email cannot be found
								successfulLog = false;
								System.out.println(seperate);
								}
								//Email is remembered by server throughout entire program
					
					}
				}while(!successfulLog);
			
				
				while(!logout) {
					int choice = 0;
					Scanner selection = new Scanner(System.in);
					do {
						error = false;
					System.out.println(seperate);
					System.out.println("                       	Cineplex mini-theatre");		//Start Menu
					System.out.println(seperate);
					System.out.println("CURRENTLY LOGGED IN AS: " + id + "\n" + seperate);
					System.out.println("1. See available movies and book tickets");
					System.out.println("2. Check the point balance for the account");
					System.out.println("3. Logout");
					try {
					choice = selection.nextInt();
					}
					catch(Exception e){
						error = true;
						System.out.println("Please enter a valid number\n" + seperate);
						selection.nextLine();
					}
					}while(error == true);
					if(choice == 1) {
						int pick = 0;
						do {
							error = false;
							System.out.println(seperate);
							System.out.println("Here are the available movies for " + new Date());
							for(int i = 0; i < moviesavailable.length; i++) {
								System.out.println(i + " : " + moviesavailable[i]);  //Checks string [] that holds every available movie
							}
							System.out.println("Enter your selection below: ");
							try {
							pick = selection.nextInt();
							}
							catch(Exception e){
								System.out.println("ERROR: Please enter a valid selection");
								selection.nextLine();
								error = false;
							}
						//String nameMovie;
						}while(error);
						if(pick > 0 && pick <=5) {
							int arr[][] = seatDatabase(moviesavailable[pick], 5, 5); //From the selected movie, its corresponding file is submitted and the actual array is returned
							occupancy(moviesavailable[pick], id, arr, socket, 12); 
						}
						else if(pick >=5) {
							int arr[][] = seatDatabase(moviesavailable[pick], 12, 12); //From the selected movie, its corresponding file is submitted and the actual array is returned
							occupancy(moviesavailable[pick], id, arr, socket, 12);
						}
					}
					else if(choice == 2) {
						System.out.println("***********************************************************************");
						System.out.println("The current balance for the user " + id + " is " + currentBalance(id) + " Cineplex points!"); //Checks balance of current user
						System.out.println("***********************************************************************");
					}
					else if(choice == 3) {
						logout = true; //Returns to LOGIN sequence
					}
					
				}
			}while(logout == false);
		}
		}catch(Exception e){
			System.out.println("The server is unable to connect to the client. Ensure that the ServerCLS script is running FIRST and then run the main class"); //If the client tries to start without the serverCLS running, terminate the server.
		}
	}
}