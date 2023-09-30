package movieTheater;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Date;
import java.time.LocalDateTime;
//MOVIE THEATRE PROGRAM
//LUKE EDWARDS COMPUTER SCIENCE 11 2021
//WESTERN CANADA HIGHSCHOOl
//FOREWORD: The purpose of this class serves as small demonstration of a new skilled I've learned while working on this program
//The ServerCLS acts as a Receipt line, where each purchase made on the main class is taken and told to the server.
public class serverCLS {
	static String seperate ="=======================================================================";
	public static void main(String[] args) throws IOException {	
		ServerSocket listener; //Creates the server socket that will connect to the main class. This class is now designated as the servicer
		Socket client = null;  //Creates another socket that will used later.
		BufferedReader request = null;
		while(true) {
			try {
				System.out.println("[SERVER] Waiting for connection...");
				listener = new ServerSocket(9090); //The server listens in a specific port. When a client connects to the same port (9090), the connection is made
				client = listener.accept(); //The client socket is now associated to the accept() function, which accepts the client connected to the current port.
				PrintWriter out = new PrintWriter(client.getOutputStream(), true); //PrintWriter connects to the console of the client, and by using the write() function it will write to the client's console
				request = new BufferedReader (new InputStreamReader(client.getInputStream())); //Buffered reader will read any input made by the client, and use that info as it needs.
				System.out.println("[SERVER] Connection found on port 9090!");
				System.out.println(seperate);	
				System.out.println("                     Cineplex mini-theatre SERVER");	
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Connection failed"); //If connection is not made by both the client and server, reset the server.
				break;
			}
			while(client.isConnected()){ //Perform this loop while the connection is still active
				try {
					String id = request.readLine();
					String movie = request.readLine();
					String column = request.readLine(); //Accepts all information given by the client, and uses it for the receipt
					String row = request.readLine();
					System.out.println("[SERVER] NEW PURCHASE BY USER " + id + " FOR FILM: " + movie + " IN THE SEATS COLUMN: "+ column + " ROW: " + row); //Print the receipt
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("[Server Error] Client not found."); //If the client somehow disconnects from the server during its writing, terminate the server.
					break;
				}
			}
		}
		client.close();
		request.close();
	}
}


