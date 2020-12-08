package com.code;// Run() is called from Scheduling.main() and is where
// the scheduling algorithm written by the user resides.
// User modification should occur within the Run() function.

import java.util.Vector;
import java.io.*;

public class SchedulingAlgorithm {

  public static Results Run(int runtime, Vector<sProcess> processVector, Results result) {
    int i = 0;
    int comptime = 0;
    int currentProcess = 0;
    int size = processVector.size();
    int completed = 0;
    String resultsFile = "Summary-Processes";

    result.schedulingType = "Batch (Nonpreemptive)";
    result.schedulingName = "Lottery";
    try {
      //BufferedWriter out = new BufferedWriter(new FileWriter(resultsFile));
      //OutputStream out = new FileOutputStream(resultsFile);
      PrintStream out = new PrintStream(new FileOutputStream(resultsFile));
      sProcess process = getLotteryProcess(processVector);
      currentProcess = processVector.indexOf(process);
      out.println("Process: " + currentProcess + " registered... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.tickets +")");
      while (comptime < runtime) {
        if (process.cpudone == process.cputime) {
          completed++;
          out.println("Process: " + currentProcess + " completed... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.tickets +")");
          if (completed == size) {
            result.compuTime = comptime;
            out.close();
            return result;
          }
          //here comes my lottery algorithm
          while(true) {
            sProcess chosenProcess = getLotteryProcess(processVector);
            if (chosenProcess.cpudone < chosenProcess.cputime) {
              process = chosenProcess;
              break;
            }
          }
          currentProcess = processVector.indexOf(process);
          out.println("Process: " + currentProcess + " registered... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.tickets + ")");
        }      
        if (process.ioblocking == process.ionext) {
          out.println("Process: " + currentProcess + " I/O blocked... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.tickets + ")");
          process.numblocked++;
          process.ionext = 0;
          //here comes my lottery algorithm
          while(true) {
            sProcess chosenProcess = getLotteryProcess(processVector);
            if (chosenProcess.cpudone < chosenProcess.cputime) {
              process = chosenProcess;
              break;
            }
          }
          currentProcess = processVector.indexOf(process);
          out.println("Process: " + currentProcess + " registered... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.tickets +")");
        }        
        process.cpudone++;       
        if (process.ioblocking > 0) {
          process.ionext++;
        }
        comptime++;
      }
      out.close();
    } catch (IOException e) { /* Handle exceptions */ }
    result.compuTime = comptime;
    return result;
  }

  private static sProcess getLotteryProcess(Vector<sProcess> processes) {
    int totalTickets = 0;
    for(sProcess process : processes) {
      if(process.cputime == process.cpudone) {
        continue;
      }
        totalTickets += process.tickets;
    }

    int chosenTicket = (int)(Math.random()*totalTickets) + 1;
    int curTicketNum = 0;

    for(sProcess process : processes) {
      if(process.cputime == process.cpudone) {
        continue;
      }
      curTicketNum += process.tickets;
      if(curTicketNum >= chosenTicket) {
        return process;
      }
    }

    return null;
  }
}
