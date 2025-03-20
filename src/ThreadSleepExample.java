import java.util.concurrent.*;

public class ThreadSleepExample {
    public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors()); // Número dinâmico de threads

        scheduler.scheduleAtFixedRate(() -> {
            try {
                System.out.println("Thread 1 is sleeping for 5 seconds");
                Thread.sleep(5000);
                System.out.println("Thread 1 is awake");
            } catch (InterruptedException e) {
                System.out.println("Thread 1 was interrupted");
            }
        }, 1, 1, TimeUnit.SECONDS);

        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("Thread 2 is running");
        }, 1, 1, TimeUnit.SECONDS);
    }
}
