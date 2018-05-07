package tk.srwhiteskull.studentmanager;

import java.util.concurrent.atomic.AtomicInteger;

public class GeneradorID {
    private final static AtomicInteger c = new AtomicInteger(0);
    public static int getID() {
        return c.incrementAndGet();
    }
}
