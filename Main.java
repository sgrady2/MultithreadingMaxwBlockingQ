import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
	private static final int ARRAY_BLOCKING_SIZE = 10;
	private static final long MAX_PRODUCER_DELAY = 1000;
	private static final long MAX_CONSUMER_DELAY = 2000;
	private static final int PRINT_COUNT = 2000000;
	
	
	public static void main(String[] args) {
		ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(ARRAY_BLOCKING_SIZE);		
		ProducerRunnable producer = new ProducerRunnable(queue);
		ConsumerRunnable comsumer = new ConsumerRunnable(queue);

		ExecutorService executor = Executors.newCachedThreadPool();	
		executor.execute(producer);
		executor.execute(comsumer);
		executor.shutdown();
	}
	//implementing Runnable interface (mainly just overriding the run()) for the first thread 
	//which will be the producer thread
	private static class ProducerRunnable implements Runnable {
		private BlockingQueue<String> _queue;
		
		public ProducerRunnable(BlockingQueue<String> queue) {
			_queue = queue;
		}
		
		@Override
		public synchronized void run() {
			for (int i = 0; i < PRINT_COUNT; ++i) {
				long delay = (long) (Math.random());
				try {
					Thread.sleep(delay);
					
					_queue.put(UUID.randomUUID().toString());
					Thread.yield();
					if (i%1000 == 0){
						System.out.println(i+ " Strings produced”");
					}
				} catch (InterruptedException ex) {
					System.err.println("Interrupted: " + ex);
				}				
			}
			
			System.out.println(Thread.currentThread().getName() + " done");
		}
	}
	//implementing Runnable interface (mainly just overriding the run()) for the second thread 
	//which will be the Consumer thread
	private static class ConsumerRunnable implements Runnable {
		private BlockingQueue<String> _queue;
		
		public ConsumerRunnable(BlockingQueue<String> queue) {
			_queue = queue;
		}

		@Override
		public synchronized void run() {
			String max = null;
			for (int i = 0; i < PRINT_COUNT; ++i) {
				long delay = (long) (Math.random());
				
				try {
					Thread.sleep(delay);
					
					String n = _queue.take();
					if (max == null)
						max = n;
					Thread.sleep(1);
					if (n.compareTo(max) > 0){
						max = n;
						
					}
					
					
					if (i%1000 == 0){
						System.out.println(i+ " Strings Consumed”MAX: "+max);
					}
				} catch (InterruptedException ex) {
					System.err.println("Interrupted: " + ex);
				}				
			}		
			System.out.println(Thread.currentThread().getName() + " done");
		}
	}
 }
