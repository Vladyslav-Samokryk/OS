 class Process {
    int cputime;
    int arrivaltime;
    int ioblocking;
    int cpudone;
    int ionext;
    int lotnext;
    int numblocked;
    int numTickets;


   Process(int cputime, int ioblocking, int cpudone, int ionext, int lotnext, int numblocked, int numTickets) {
       this.cputime = cputime;
       this.ioblocking = ioblocking;
       this.cpudone = cpudone;
       this.ionext = ionext;
       this.lotnext = lotnext;
       this.numblocked = numblocked;
       this.numTickets = numTickets;
  } 	
}
