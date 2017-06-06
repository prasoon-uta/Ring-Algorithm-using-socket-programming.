# Ring-Algorithm-using-socket-programming.
This program is an implementation of Ring election algorithm for electing co-coordinator process.
Instructions:
1.	Run the program as many times as number of processes required in algorithm (Restricted to max of 9 processes).
2.	Clicking on the manual election button will start the ring algorithm.
3.	Elector will update the coordinator to all processes once the election is complete.
4.	To keep the ring process on track, ‘ALIVE’ message is sent constantly into the ring from each process.
5.	If any one of the non-coordinator process is crashed (by clicking Crash button manually), that process will be by-passed by all other active process.
6.	By clicking the reset button, any crashed process can be brought back into the ring structure (by clicking the Reset button), and it will automatically invoke the election process to make sure that the coordinator is up to date.
7.	If the ALIVE message is not going down in any of the process, click the Refresh button manually from any one of the process to start sending the ALIVE token process again.
