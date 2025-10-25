package chapter3sharingobjects;

import annotations.GuardedBy;
import annotations.NotThreadSafe;
import annotations.ThreadSafe;

public class NoVisibility {
    private static boolean ready;
    private static int number;

    // Reordering can make this thread loop forever
    @NotThreadSafe
    private static class ReaderThread extends Thread {
        public void run() {
            while (!ready) {
                Thread.yield();
                System.out.println("Dentro do while: " + number);
            }
            System.out.println("Fora do while: " + number);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new ReaderThread().start();
        Thread.sleep(1000);
        number = 42;
        ready = true;
    }

    // Mutable integer class without synchronization - getter and setter can cause visibility issues - not thread-safe
    @NotThreadSafe
    public class MutableInteger {
        private int value;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    @ThreadSafe
    public class SynchronizedInteger {
        @GuardedBy("this")
        private int value;

        public synchronized int getValue() {
            return value;
        }

        public synchronized void setValue(int value) {
            this.value = value;
        }
    }
}
