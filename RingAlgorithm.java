/*Prason Kumar
 * 1001415936
 * www.wikipedia.com
 * www.stackoverflow.com
 * http://www.cs.colostate.edu/~cs551/CourseNotes/Synchronization/RingElectExample.html
 */


import java.util.*;
import java.awt.Color;
import java.awt.event.*; 
import java.io.*; 
import java.net.ServerSocket; 
import java.net.Socket; 
import javax.swing.*; 
import javax.swing.Timer;
import javax.swing.border.EmptyBorder; 

public class RingAlgorithm extends JFrame {

		// declare the variables used as public in this class
	
	public JButton buttonRefresh = new JButton("Refresh"); // create a button in the UI
	public static JTextArea textArea = new JTextArea(); // create a text area for messages to be displayed
	public JLabel processNumber; // create to label in the UI
	public static JTextArea textpNumber = new JTextArea(); // create a textArea to represent the Process Number
	public JLabel coordinatorNumber;  // create to label in the UI
	public static JTextArea textCoordinatorNumber = new JTextArea(); // create a textArea to represent the Coordinator Number
	static int timeCap = 0;
	static ActionListener taskPerformer;
		public JPanel mainPane;  // declare the mainPane for UI
		public JButton btnElection = new JButton("Manual Election"); // create a button in the UI
		public JButton button_Crash = new JButton("Crash"); // create a button in the UI
		public JButton buttonReset = new JButton("Reset"); // create a button in the UI
		public static int portNumber = 0; // declare the initial port number and process number
		public static int procNo = 0;
		public static boolean portFlag = false;  // declare port flag to check the port connection status
		static ServerSocket serverSocket; // declare a socket for server
		public static int coordinator = 0, currentAlive = 0, crashedPort = 0; // declare some useful integer variables
		public static boolean isCoordinator = false, isCrashed = false, isIdle = true, crashFlag = false; // declare some useful boolean variables
		
		private static int defaultPort = 7080;  // Default port number, which acts as the starting port
		static String localhost ="localhost"; // declare the local localhost name
		static processThread thread = new processThread(); // declare a client thread
		int timeCap2 = 0;
		static ActionListener taskPerformer2;
		static ActionListener taskPerformer5;
		
		// Constructor which executes the UI for users
		public RingAlgorithm() {
			
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setBounds(200, 200, 550, 450);
			mainPane = new JPanel(); // create a new panel
			mainPane.setBackground(Color.black);
			mainPane.setBorder(new EmptyBorder(8, 8, 8, 8));
			setContentPane(mainPane);
			mainPane.setLayout(null);
			textArea.setEditable(false); // set the text are to non-editable
			
			// create a scrollable pane for the text area
			JScrollPane scrollPane = new JScrollPane(); 
			scrollPane.setViewportView(textArea);
			scrollPane.setBounds(30, 75, 330, 180);
			mainPane.add(scrollPane);
			
			// Create a new label and set the position
			processNumber = new JLabel("Process Number"); 
			
			processNumber.setForeground(Color.white);
			
			processNumber.setBounds(50, 25, 149, 23);
			mainPane.add(processNumber);
			
			// Create a new tetxbox in order to list the process number
			textpNumber.setBounds(200, 25, 45, 23);
			textpNumber.setEditable(false);
			mainPane.add(textpNumber);
			
			// Create a Refresh button in order to refresh the token passing
			buttonRefresh.setBounds(50, 50, 95, 23);
			buttonRefresh.setEnabled(false);
			mainPane.add(buttonRefresh);
			
			// Create a Manual Election button
			btnElection.setBounds(40, 272, 125, 23);
			mainPane.add(btnElection);
			
			// Create a Crash button
			button_Crash.setBounds(40, 300	, 125, 23);
			//button_Crash.setBounds(230, 272, 95, 23);
			button_Crash.setEnabled(false);
			mainPane.add(button_Crash);
			
			// Create a Reset button
			buttonReset.setBounds(40, 330, 125, 23);
			buttonReset.setEnabled(false);
			mainPane.add(buttonReset);
			
			// Create a new label and set the position
			coordinatorNumber = new JLabel("New Coordinator");
			coordinatorNumber.setBounds(40, 360, 125, 23);
			coordinatorNumber.setForeground(Color.white);
			mainPane.add(coordinatorNumber);
			
			// Create a new tetxbox in order to list the coordinator number
			textCoordinatorNumber.setBounds(155, 360, 30, 30);
			textCoordinatorNumber.setEditable(false);
			mainPane.add(textCoordinatorNumber);
			
			// executes this part of code when 'Manual Election' button is clicked
			btnElection.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					
					isIdle = false;
					
					
					
					// Initial process which starts the manual election
					textArea.append("\n Token Out: " + "ELECTION " + procNo); // Print the first Token Out command
					String currToken = "ELECTION " + procNo;
					initiateElection(currToken, procNo+1); // Calling the initiateElection method
					
										
					btnElection.setEnabled(false); // set the Election button to be disabled
					button_Crash.setEnabled(false); // set the Crash button to be disabled
					buttonRefresh.setEnabled(false); // set the Refresh button to be disabled
					System.out.println("Clicked");
										
				}
			});
			
			// executes this part of code when 'Crash' button is clicked
			button_Crash.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					
					isCrashed = true; // Change the boolean value when crashed
					
					button_Crash.setEnabled(false); // set the Crash button to be disabled
					buttonReset.setEnabled(true); // set the Reset button to be enabled
					btnElection.setEnabled(false); // set the Manual Election button to be disabled
					buttonRefresh.setEnabled(false); // set the Refresh button to be disabled
					
					// Delay the time in before crashing the process
					long startTime = new Date().getTime();
					long currentTime = new Date().getTime();
                    while(true) {
                        currentTime = new Date().getTime();
                        if((currentTime - startTime) >= 2000) {
                            break;
                        }
                    }

					// Crash the process
					try {
						thread.suspend();
						serverSocket.close();
						crashedPort = defaultPort + procNo; // Assign the crashed port
						textArea.append("\n Processor crashed..!!!");
					} catch (IOException ex) {  // Catch actions if not crashed
						isCrashed = false;
						textArea.append("\n Unable to crash this processor, Please try again later..!!!");
					}
					
													
					System.out.println("Clicked");
										
				}
			});
			
			// executes this part of code when 'Reset' button is clicked
			buttonReset.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					
					isCrashed = false;
					
					button_Crash.setEnabled(true); // set the Crash button to be enabled
					buttonReset.setEnabled(false); // set the Reset button to be disabled
					btnElection.setEnabled(true);  // set the Manual Election button to be enabled
					buttonRefresh.setEnabled(true); // set the Refresh button to be enabled
					
                    // Reset the crashed process
					try {
						serverSocket = new ServerSocket(crashedPort);
						thread.informReset(crashedPort, procNo, serverSocket);  // update the thread variables
						thread.resume();
						textArea.append("\n Thread Restarted..!!  \n Initiating Election..");
						crashedPort = 0;
					} catch (IOException ex) {  // Exception to shut down the process when crashed port is unavailable
						System.out.println("\n Port not available for Process Restart..!! \n Process Restart failed..!!");
						System.exit(1);
					}
					
					isIdle = false; // boolean value change
					
					// Initiate the election whenever the process is restarted
					textArea.append("\n Token Out: " + "ELECTION " + procNo);
					String currToken = "ELECTION " + procNo;
					initiateElection(currToken, procNo+1);
					
					System.out.println("Clicked");
										
				}
			});
			
			// executes this part of code when 'Refresh' button is clicked
			buttonRefresh.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					
					// Manual Refresh the Alive token whenever the process didn't respond
					thread.nextAlive(procNo+1);
										
				}
			});
			
			//executes this part when timer exceeds 10 seconds
			taskPerformer = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					
					if(timeCap2 == 0) {
						
						System.out.println("Inside taskPerformer");
						
						isIdle = false;
						
						// Initial process which starts the manual election
						textArea.append("\n Token Out: " + "ELECTION " + procNo); // Print the first Token Out command
						String currToken = "ELECTION " + procNo;
						initiateElection(currToken, procNo+1); // Calling the initiateElection method
						
											
						btnElection.setEnabled(false); // set the Election button to be disabled
						button_Crash.setEnabled(false); // set the Crash button to be disabled
						buttonRefresh.setEnabled(false); // set the Refresh button to be disabled
						
						timeCap2 = 1;
											
					}
						
				}
					
					    
			};
			
			//executes for process 2 and process 5
			taskPerformer2 = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					
					if(timeCap2 == 0) {
						
						System.out.println("Inside taskPerformer2");
						
						isIdle = false;
						
						// Initial process which starts the manual election
						textArea.append("\n Token Out: " + "ELECTION " + procNo); // Print the first Token Out command
						String currToken = "ELECTION " + procNo;
						initiateElection(currToken, procNo+1); // Calling the initiateElection method
						
											
						btnElection.setEnabled(false); // set the Election button to be disabled
						button_Crash.setEnabled(false); // set the Crash button to be disabled
						buttonRefresh.setEnabled(false); // set the Refresh button to be disabled
						
						timeCap2 = 1;
											
					}
						
				}

			};
			
			taskPerformer5 = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					
					if(timeCap2 == 0) {
						
						System.out.println("Inside taskPerformer5");
						
						isIdle = false;
						
						// Initial process which starts the manual election
						textArea.append("\n Token Out: " + "ELECTION " + procNo); // Print the first Token Out command
						String currToken = "ELECTION " + procNo;
						initiateElection(currToken, procNo+1); // Calling the initiateElection method
						
											
						btnElection.setEnabled(false); // set the Election button to be disabled
						button_Crash.setEnabled(false); // set the Crash button to be disabled
						buttonRefresh.setEnabled(false); // set the Refresh button to be disabled
						
						timeCap2 = 1;
											
					}
						
				}
					
					    
			};
			
			// executes this when a window is closed
			addWindowListener(new WindowAdapter() {		
				@SuppressWarnings("deprecation")
				@Override
				public void windowClosing(WindowEvent arg0) {

					// inform the previous process whenever a process is removed from the ring
					isIdle = false;
					String currToken = "EXIT " + procNo;
					informExit(currToken, procNo-1);
					System.out.println("Window Closed, Process " + procNo);
				}
			});
		}
		
		//Sending the election message to the next process and inform about the election
	    public void initiateElection(String token, int proccessN0) {
	    	// if the process number is more than 8, minus it with 8 inorder to bring the number back within 8
	        if(proccessN0>8) {
	        	proccessN0 = proccessN0 - 8;
	        }
	        try {
	        	// Create a socket based on the process number
	            Socket socket = new Socket(localhost, defaultPort+proccessN0);
	            // Create the PrintWriter for the socket
	            PrintWriter out = new PrintWriter(socket.getOutputStream());
	            // send the token into the printwriter for that particular socket
	            out.println(token);
	            out.flush();  // flush the printwriter output
	            out.close();  // close the printwriter
	            socket.close();  // close the socket
	        } catch(Exception ex) {
	        	initiateElection(token, proccessN0+1); // inform to next next if the next is unavailable
	        }
	    }
	    
	    // Sending the exit message to the previous process in the ring whenever a process is removed
	    private void informExit(String token, int procID) {
	    	if(procID == 0 || procID < 0) {
	    		if(coordinator != 0) {
	    			procID = coordinator;
	    		}else {
	    			procID = 8;
	    		}
	        }
	        try {
	            Socket socket = new Socket(localhost, defaultPort+procID);
	            PrintWriter out = new PrintWriter(socket.getOutputStream());
	            out.println(token);
	            out.flush();
	            out.close();
	            socket.close();
	        } catch(Exception ex) {
	        	initiateElection(token, procID-1);  // inform to previous of previous if previous process is unavailable
	        }
	    }
	    
	  
		
		// Main method
		public static void main(String[] args) {
			
			RingAlgorithm gui = new RingAlgorithm();
			gui.setVisible(true);		// setting the frame visible
			gui.setTitle("Ring Algorithm");
			
			// open server sockets starting with port 7081. Limit the number to 8
			// Check the available port number before making the connection
			for(int i=1;i<9;i++)
			{
				ServerSocket tempSkt;
				portNumber = defaultPort + i;
				try{
					tempSkt= new ServerSocket(portNumber); // declare a new socket
					procNo = portNumber - defaultPort; // Process number identification
					String tempProcess = Integer.toString(procNo);
					textpNumber.setText(tempProcess); // Inform the GUI
					tempSkt.close(); // close the socket
					textArea.append("Connected to the port : " + portNumber);
					portFlag = true;
					
					//First time when the coordinator is not elected
					/*if(timeCap == 0 && procNo == 1) {
						Timer timer = new Timer(10000 ,taskPerformer);
						System.out.println("Timer initiated");
						timer.setInitialDelay(0);
						timer.start();
						System.out.println("Timer started");
						timeCap = 1;
					}*/
					
					//Election by process 2 and process 5
					if(procNo == 2 ) {
						Timer timer2 = new Timer(10000 ,taskPerformer2);
						System.out.println("Timer2 initiated");
						timer2.setInitialDelay(30000);
						timer2.start();
						System.out.println("Timer2 started");
						timeCap = 1;
					}
					
					if(procNo == 5 ) {
						Timer timer5 = new Timer(10000 ,taskPerformer5);
						System.out.println("Timer5 initiated");
						timer5.setInitialDelay(30000);
						timer5.start();
						System.out.println("Timer5 started");
						timeCap = 1;
					}
					
					break;  // break the for loop whenever a port is available
					
				}catch (IOException e){
					System.out.println("Socket is running at " + portNumber);  				
				}
			}
			
			// Limit the process when it exceeds 8
			if(portFlag == false) {
				System.out.println("Exceed the total number of alotted ports.....");
				System.exit(1);
			}
			else
			{
				// Connect the identified available port to the server socket
				try {
					serverSocket = new ServerSocket(portNumber);
					thread.start(); // Start the thread
					thread.init(portNumber, procNo, gui, serverSocket); // inform the initial variables
				} catch (IOException e) {
					System.out.println("Something went wrong while trying to connect to port " + portNumber);
					System.exit(1);
				}
			}
		}
	
}


//class for handling of threads
class processThread extends Thread {
	
	// declare all the variables 
	public int processNumber;
	public int portNumber;
	public BufferedReader readBuff;
	public PrintWriter printtoNext;
	public RingAlgorithm gui;
	public ServerSocket servSoc;
	public Socket client;	
	public int lastportNumber=0;
	public String procString;
	public int clientportNumber;
	public int defaultPort = 7080;
	public int coordinator = 0;
	static String localhost ="localhost"; // declare the local localhost name
	
	// thread initializing
	public void init(int portNumber, int processNumber ,RingAlgorithm gui, ServerSocket servSoc){
		
		this.portNumber = portNumber; // assign the port number
		this.processNumber = processNumber; // assign the process number 
		this.gui = gui; // assign the frame
		this.servSoc = servSoc;	 // assign the socket
		gui.textArea.append("\n Started Thread");
	}
	
	// thread to update the reset variables
	public void informReset(int portNumber, int processNumber ,ServerSocket servSoc) {
		this.portNumber = portNumber; // assign the port number
		this.processNumber = processNumber; // assign the token 
		this.servSoc = servSoc;	 // assign the socket
	}
	
	// run method of the thread
	public void run(){
		
		while(true) {
		
			try {
				
				client = servSoc.accept(); // Accept the incoming socket
				readBuff = new BufferedReader(new InputStreamReader(client.getInputStream()));		//Open input reader
				
				//if(readBuff.ready()) {
					
		            String token = readBuff.readLine(); // Read the received token
		            
		            Thread.sleep(2000);
		            
		            // String Tokenize the received token
		            StringTokenizer stringTokenizer = new StringTokenizer(token);					//Break the token into strings 
		            switch(stringTokenizer.nextToken()) {											//iterate through string's first word
		                case "ELECTION":  // action to be performed when it is an election message
		                	
		                	// Print the received token
		                	gui.textArea.append("\n Token In:  "+token);
		                	gui.isIdle = false;
		                	
		                    try {
		                    	// if the election message reached the initiated process, find the eligible coordinator
		                        if(Integer.parseInt(stringTokenizer.nextToken()) == processNumber) {
		                            int[] processes = new int[8];
		                            processes[0] = processNumber;
		                            int counter = 1;
		                            while(stringTokenizer.hasMoreTokens()) {
		                            	processes[counter] = Integer.parseInt(stringTokenizer.nextToken());
		                                counter++;
		                            }
		                            findCoordinator(processes);  // find the coordinator
		                            if(coordinator != 0) {
		                            	// inform the coordinator to other processes
		                            	informCoordinator(coordinator, processNumber, processNumber);
		                            }
		                        }
		                        else {
		                        	// if not the initiated process, just add the process number to the token and pass the token to next processor
		                        	Thread.sleep(2000);
		                        	gui.textArea.append("\n Token Out:  "+token+" "+processNumber);
		                        	sendToNextProcessor(token+" "+processNumber, processNumber+1);
		                        }
		                    } catch(Exception ex) {
		                        ex.printStackTrace();
		                    }
		                    break;
		                case "COORDINATOR":  // action to be performed when it is an coordinator message
		                	String token2 = stringTokenizer.nextToken();  // coordinator number
		                	String token3 = stringTokenizer.nextToken();   // identified processor
		                    try {
		                    	Thread.sleep(2000);
		                        if(Integer.parseInt(token2) == processNumber) {  // if the message is passed to the coordinator
		                            gui.isCoordinator = true;
		                            gui.textArea.append("\n This is the new Coordinator..!!! Elected by the Processor " + token3);
		                        }
		                        else { // informing the previous coordinator about the new coordinator
		                            if(gui.isCoordinator == true) {
		                            	gui.textArea.append("\n" + processNumber + " is not coordinator anymore");
		                            	gui.isCoordinator = false;
		                            }
		                        }
		                        // if the coordinator message is circled backed to the elector
		                        if(Integer.parseInt(token3) == processNumber) {
		                        	gui.textArea.append("\n New Coordinator is elected ---> " + token2 + " and informed all other process");
		                        	gui.btnElection.setEnabled(true); // Enable the Manual Election button
		                        	gui.buttonRefresh.setEnabled(true); // Enable the refresh button
		                        	if(gui.isCrashed == false){ // Enable the Crash button if not crashed
		                        		gui.button_Crash.setEnabled(true);
		                        	}
		                        	gui.isIdle = true;
		                        	// Start sending the Alive token in order to keep the ring process in sync
		                        	if (processNumber == coordinator){ // if the elector is the coordinator
		                        		nextAlive(processNumber+1); // then start the alive message from the next available processor
		                        	} else { // or else start the alive message from the current processor
		                        		String tokenAlive = "ALIVE " + coordinator + " " + processNumber;
		                        		verifyAlive(gui.coordinator, processNumber+1, processNumber+1, tokenAlive);
		                        	}
	                            }
		                        else { // if the coordinator message is received by other processor
		                        	if(!gui.isCoordinator) {  // Print the new coordinator info
		                        		gui.textArea.append("\n New Coordinator is --> " + token2 + "  Elected By " + token3);
		                        	}
		                        	gui.textCoordinatorNumber.setText(token2); // update the GUI
		                        	// update the variables
		                        	gui.coordinator = Integer.parseInt(token2);
		                            coordinator = Integer.parseInt(token2);
		                        	sendToNextProcessor(token, processNumber+1); // send the message to next processor
		                        	gui.isIdle = true;
		                        	gui.button_Crash.setEnabled(true); // Crash button is enabled
		                        	gui.buttonRefresh.setEnabled(true); // Refresh button is enabled
		                        	if(gui.currentAlive == processNumber - 1)
		                        	{
		                        		// initiate the alive message when the last alive message was stopped here
		                        		if (processNumber == coordinator){
			                        		nextAlive(processNumber+1);
			                        	} else {
			                        		String tokenAlive = "ALIVE " + coordinator + " " + processNumber;
			                        		verifyAlive(gui.coordinator, processNumber+1, processNumber, tokenAlive);
			                        	}
		                        	}
		                        }
		                    } catch(Exception ex) {
		                        ex.printStackTrace();
		                    }
		                    break;
		                case "ALIVE": // if the message is an ALIVE message
		                	String tokenCNo = stringTokenizer.nextToken(); // coordinator number
		                	String tokenPNo = stringTokenizer.nextToken();  // process number
		                	if(gui.isIdle == true) {
		                		try {
		                			// if the ALIVE message is circled back to the initiator
			                        if(Integer.parseInt(tokenPNo) == processNumber) {
			                        	// check the status
			                        	if(stringTokenizer.hasMoreTokens()) {
				                            if(stringTokenizer.nextToken().equals("OK")){
				                            	// if the status is OK update the GUI
				                            	gui.textArea.append("\n [" + tokenPNo + "] --> Coordinator Alive");
				                            	if (processNumber+1 == coordinator){
					                        		nextAlive(processNumber+2);
					                        	} else {
					                        		nextAlive(processNumber+1);
					                        	}
				                            }
			                        	} else {  // else initiate the elction process
			                        		gui.textArea.append("\n Coordinator not responding... \n Initiating new election...");
			                        		gui.textArea.append("\n Token Out: " + "ELECTION " + processNumber);
			            					String currToken = "ELECTION " + processNumber;
			            					gui.initiateElection(currToken, processNumber+1);
			                        	}
			                        	
			                        }
			                        else if(Integer.parseInt(tokenCNo) == processNumber) { // if the alive message is received by the coordinator
			                            	gui.textArea.append("\n [" + tokenPNo + "] --> Coordinator Validation Passed");
			                            	// append OK status and send it back to the initiator
			                            	token = token + " OK";
			                            	sendToNextProcessor(token, processNumber+1);
			                        }
			                        else { // else forward it to the next processor
			                        	sendToNextProcessor(token, processNumber+1);
			                        }
			                        
			                    } catch(Exception ex) {
			                        ex.printStackTrace();
			                    }
		                	}
		                    break;
		                case "EXIT": // if it is EXIT message
		                	// Initiate the election process since the next processor was removed from the ring
		                	String token4 = stringTokenizer.nextToken();
		                	gui.isIdle = false;
		                	gui.textArea.append("\n Process " + token4 + " is removed from the Ring, Initiating Election.. ");
		                	String electToken = "ELECTION " + processNumber;
		                	gui.textArea.append("\n Token Out: " + electToken);
		                	sendToNextProcessor(electToken, processNumber+1);
		                    break;
		                case "NEXT_ALIVE": // if it is NEXT_ALIVE message
		                	// send the alive message from this processor since the previous one was a coordinator
		                	int targetNo = Integer.parseInt(stringTokenizer.nextToken());
		                	if(targetNo == processNumber) {
		                		if(targetNo == coordinator) {
		                			nextAlive(1);
		                		} else {
			                		gui.currentAlive = targetNo - 1;
			                		String tokenNextAlive = "ALIVE " + coordinator + " " + processNumber;
			                		verifyAlive(gui.coordinator, processNumber+1, processNumber, tokenNextAlive);
		                		}
		                	}
		            }
		            
		          
	            
			} catch (IOException e) {
				//Logger.getLogger(RingAlgorithm.class.getName()).log(Level.SEVERE, null, e);
				System.out.println("Exception : " + e);
			} catch (NullPointerException e)
	        {
	        	//Logger.getLogger(RingAlgorithm.class.getName()).log(Level.SEVERE, null, e);
	        	System.out.println("Exception : " + e);
	        } catch (InterruptedException e) {
				// TODO Auto-generated catch block
	        	System.out.println("Exception : " + e);
			} finally {
	            try {
	            	readBuff.close();
	            } catch (IOException ex) {
	            	System.out.println("Exception : " + ex);
	            }
	        }
		}
	}
	
	//Sending the token message to the next process
    private void sendToNextProcessor(String token, int procID) {
        if(procID>8) {
        	procID = procID - 8;
        }
        try {
            Socket socket = new Socket(localhost, defaultPort+procID);
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            out.println(token);
            out.flush();
            out.close();
            socket.close();
        } catch(Exception ex) {
        	sendToNextProcessor(token, procID+1); // send it to next next if the next one was unavailable
        }
    }
    
    //After Election tokens are passed, check which process becomes coordinator
    private void findCoordinator(int[] processes) {
        int newCoordinator = processes[0];
      //Check the first process number and compare with other live processes and elect Coordinator with highest process number
        for(int i = 1; i<processes.length; i++) {
            if(processes[i]>newCoordinator) {					
            	newCoordinator = processes[i];
            }
        }  
        
        // assign the variables with the new coordinator value
        gui.coordinator = newCoordinator;
        coordinator = newCoordinator;
        gui.textCoordinatorNumber.setText(Integer.toString(newCoordinator));
        
    }
    
    //After confirming the coordinator, check which process becomes coordinator
    private void informCoordinator(int coord, int procNo, int electedNo) {
    	int tempProc;
    	if(coord == procNo) {
    		tempProc = 1;
        } else {
        	tempProc = procNo + 1;
        }
    	// initiate the coordinator token and pass it to all the active processes
    	String token = "COORDINATOR " + coord + " " + electedNo;
        try {
            Socket socket = new Socket(localhost, defaultPort+tempProc);
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            out.println(token);
            out.flush();
            out.close();
            socket.close();
        } catch(Exception ex) {
        	informCoordinator(coord, tempProc, electedNo); // inform to next next if the next one was unavailable
        }
    }
    
    // method to pass the alive message to the next processor from the initiator
    private void verifyAlive(int coordID, int procID, int electedID, String token) {
    	if(gui.isIdle == true) {
    		long time = 1500 * electedID;
    		try {
				Thread.sleep(time);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    		if(procID>8) {
            	procID = procID - 8;
            }
	        try {
	            Socket socket = new Socket(localhost, defaultPort+procID);
	            PrintWriter out = new PrintWriter(socket.getOutputStream());
	            out.println(token);
	            out.flush();
	            out.close();
	            socket.close();
	            gui.currentAlive = electedID;
	        } catch(Exception ex) {
	        	verifyAlive(coordID, procID+1, electedID, token); // pass to next next if next was unavailable
	        }
    	}
    }
    
    // Method to initiate the NEXT_ALIVE message from the current processor
    public void nextAlive(int nextProc) {
    	if(nextProc>8) {
    		nextProc = nextProc - 8;
        }
    	String token = "NEXT_ALIVE " + nextProc;
        try {
            Socket socket = new Socket(localhost, defaultPort+nextProc);
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            out.println(token);
            out.flush();
            out.close();
            socket.close();
        } catch(Exception ex) {
        	// forward to next next if the next one was unavailable
        	if(nextProc+1 == coordinator) {
        		nextAlive(nextProc+2);
        	} else {
        		nextAlive(nextProc+1);
        	}
        }
    }
	
}
