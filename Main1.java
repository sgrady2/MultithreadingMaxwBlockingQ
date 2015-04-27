import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class Main1 {
	private static final long MAX_PRODUCER_DELAY = 2000;
	private static final long MAX_CONSUMER_DELAY = 1000;
	private static final int PRINT_COUNT = 2000000;
	private static final int BUFFER_SIZE = 1;
	
	public static void main(String[] args) {
		LinkedList<String> queue = new LinkedList<String>();
		ProducerRunnable producer = new ProducerRunnable(queue);
		ConsumerRunnable comsumer = new ConsumerRunnable(queue);

		ExecutorService executor = Executors.newCachedThreadPool();	
		executor.execute(producer);
		executor.execute(comsumer);
		executor.shutdown();
	}
	
	private static class ProducerRunnable implements Runnable {
		private Queue<String> _queue;
		
		public ProducerRunnable(Queue<String> queue) {
			_queue = queue;
		}
		
		@Override
		public synchronized void run() {
			for (int i = 0; i < PRINT_COUNT; ++i) {
				//could add delaytime
				long delay = (long) (Math.random());
				try {
					Thread.sleep(delay);
					//synchronizing the linkedlist queue
					synchronized (_queue) {
						while (_queue.size() >= BUFFER_SIZE) {
							_queue.wait();
						}
						
						_queue.offer(UUID.randomUUID().toString());
						
						_queue.notifyAll();
					}
					//every 1,000 in the homework, show progress
					if (i%1000==0)
						System.out.println(i+" Strings produced " );
				} catch (InterruptedException ex) {
					System.err.println("Interrupted: " + ex);
				}				
			}
			
			System.out.println(Thread.currentThread().getName() + " done");
		}
	}
	
	private static class ConsumerRunnable implements Runnable {
		private Queue<String> _queue;
		
		public ConsumerRunnable(Queue<String> queue) {
			_queue = queue;
		}

		@Override
		public void run() {
			String max = null;
			for (int i = 0; i < PRINT_COUNT; ++i) {
				long delay = (long) (Math.random());
				try {
					Thread.sleep(delay);
					
					String n;
					
					//synchronizing queue 
					synchronized (_queue) {
						while (_queue.isEmpty()) {
							_queue.wait();
						}
	
						n = _queue.poll();
						//condition for first time 
						if (max==null)
							max=n;
						Thread.sleep(1);
						//condition for keeping max
						if(n.compareTo(max) > 0)
							max=n;
						//testing
						//System.out.println(n);
						_queue.notifyAll();
					}
					//every 1,000 in the homework, show progress
					if (i%1000==0)
					System.out.println(i+" Strings consumed " +"Current Max UUID String:"+ max);
				} catch (InterruptedException ex) {
					System.err.println("Interrupted: " + ex);
				}				
			}		
			System.out.println(Thread.currentThread().getName() + " done");
		}
	}
 }