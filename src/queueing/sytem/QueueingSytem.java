/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package queueing.sytem;

import java.text.DecimalFormat;

/**
 *
 * @author CHARITY
 */
public class QueueingSytem {
    
	//declaration of variables
	static final int Q_LIMIT=100;
	static final int BUSY =1;
	static final int IDLE=0;
	
	static int next_event_type;
	static int num_custs_delayed;
	static int num_delays_required;
	static int num_events;
	static int num_in_q;
	static int server_status;
	static double area_num_in_q;
	static double area_server_status;
	static double mean_interarrival;
	static double mean_service;
	static double time;
	static double[] time_arrival=new double[Q_LIMIT+1];
	static double time_last_event;
	static double[] time_next_event =new double[3];
	static double total_of_delays;
	
	public static void main(String[] args) {
		  //specify the number  of events
		  num_events=2;
	      
		  //read the input parameters
	       mean_interarrival=1.0;
	       mean_service =0.5;
	       num_delays_required=1000;
	       
	       //write the report heading and the input parameters
	       System.out.println("        Single Server Queueing System");
	       System.out.println("        ______________________________\n");
	       System.out.println("Mean interarrival time is "+mean_interarrival);
	       System.out.println("Mean service time is "+mean_service);
	       System.out.println("Number of customers is "+num_delays_required);
	       
	       
	       //initialize the simulation
	       initialize();
	       //run the simulation while more delays are needed
	       while(num_custs_delayed<num_delays_required)
	       {
	    	   //determine next event
	    	   timing();
	    	   //update time-average statistical accumulators
	    	   update_time_avg_stats();
	    	   //invoke the appropriate event
	    	   switch(next_event_type)
	    	   {
	    	   case 1:
	    		   arrive();
	    		   break;
	    	   case 2:
	    		   depart();
	    		   break;
	    	   
	    	   }   
	       }
	    report();	
	}  
//end of main
public static void initialize()
	{
		//initialize the simulation clock
		time= 0.0;
		
		//initialize the state variables
		server_status=IDLE;
		num_in_q=0;
		time_last_event=0.0;
		
		//initialize the statistical variables
		num_custs_delayed= 0;
		total_of_delays =0.0;
		area_num_in_q =0.0;
		area_server_status =0.0;
		
		//initialize event list. Since no customers are present , the departure(service completion)
		//event is eliminated from consideration
		time_next_event[1] =time + expon(mean_interarrival);
		time_next_event[2] =1.0*Math.exp(30);
	}
	
public static void timing()
	{
		double min_time_next_event=1.0*Math.exp(30);
		next_event_type=0;
		//determine the type of next event to occur
		for(int i=1;i<=num_events;++i)
		{
			if(time_next_event[i]<min_time_next_event)
			{
				min_time_next_event=time_next_event[i];
				next_event_type=i;
			}
		}
		//check to see whether the event list is empty
		if(next_event_type==0)
		{
			//the event list is empty...so stop simulating
			System.out.println("The event list is empty at time "+time);
			System.exit(1);
		}		//the event list is not empty so advance the simulation clock
		time = min_time_next_event;
	}

	public static void arrive()  //arrival event function
	{
		double delay;
		//schedule next arrival
		time_next_event[1]= time +expon(mean_interarrival);
		//check to see whether server is busy
		if(server_status== BUSY)
		{
			//server is busy, so increment number of customers in queue
			++num_in_q;
			//check to see whether an overflow condition exists
			if(num_in_q>Q_LIMIT)
			{
				//the queue has overflown, so stop simulation
				System.out.println("Overflow of the array time arrival at time "+time);
				System.exit(2);
			}
//there is still room in queue, so store the time of arrival of the arriving customer at the
                        //new and of the time_arrival
		time_arrival[num_in_q]=time;
		}
		else
		{
			//server is idle , so arriving customer has a delay of zero.
			delay=0.0;
			total_of_delays+=delay;
			
			//increment the number of customers delayed ,and make server busy
			
			++num_custs_delayed;
			server_status=BUSY;
			
			//schedule a departure (service completion)
			time_next_event[2]=time +expon(mean_service);
		}
	}
	






public static void depart()
	{
		int i;
		double delay;
		//check to see whether the queue is empty
		if(num_in_q==0)
		{
			//the queue is empty so make the server idle and eliminate the departure(service completion)  event from consideration
			server_status=IDLE;
			time_next_event[2]=1.0e30;
		}
		else
		{
			//the queue is non empty ,so decrement the number of customers in queue
			--num_in_q;
//compute the delay of the customer who is beginning service and update the total delay accumulator 
			delay =time -time_arrival[1];
			total_of_delays+=delay;
			//increment the number of customers delayed and schedule departure
			++num_custs_delayed;
			time_next_event[2]=time+expon(mean_service);
			//move each customer in the queue(if any)
			for(i=1;i<=num_in_q;i++)
			{
				time_arrival[i]=time_arrival[i+1];
			}
		}
		
	}
	public static void report()
	{
		//compute the estimates of the desired estimates of performance
		DecimalFormat df=new DecimalFormat("#.###");
		System.out.println("Average delay in queue is "+df.format(total_of_delays/num_custs_delayed)+" minutes");
		System.out.println("Average number in queue is "+df.format(area_num_in_q/time));
		System.out.println("Server Utilization is "+df.format(area_server_status/time));
		System.out.println("The simulation ended");
	}
	


public static void update_time_avg_stats()
	{
		double time_since_last_event;
		//compute time since last event and update last_event_time marker
		time_since_last_event=time-time_last_event;
		time_last_event=time;
		//update area under num_in_queue function
		area_num_in_q+=num_in_q*time_since_last_event;
		//update area under server busy function
		area_server_status+=server_status*time_since_last_event;
	}
	
public static double expon(double mean) {
		double m;
		m = Math.random();
		return -(mean) * Math.log(m);
	}
}

    