package syncontainers;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

/**
 * Created by fengwu on 15/5/17.
 */
public class CountDownLatchDemo {

    /**
     * Simple framework for timing concurrent execution
     * @param executor
     * @param concurrency
     * @param action
     * @return
     */
    public static long time(Executor executor, int concurrency,
                            final Runnable action) throws InterruptedException {
        final CountDownLatch ready = new CountDownLatch(concurrency);
        final CountDownLatch start = new CountDownLatch(1);
        final CountDownLatch done = new CountDownLatch(concurrency);

        for (int i = 0; i < concurrency; i++) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    ready.countDown(); // Tell timer we're ready
                    try {
                        start.await(); // Wait till peers are ready
                    }catch (InterruptedException e){
                        Thread.currentThread().interrupt();
                    }finally {
                        done.countDown();  // Tell timer we're done
                    }
                }
            });
        }
        ready.await();  // wait for all workers to be ready
        long startNanos = System.nanoTime();
        start.countDown();  // And they're off!
        done.wait();  // Wait for all workers to finish
        return System.nanoTime() - startNanos;
    }


    public static void main(String[] args){

    }

}
