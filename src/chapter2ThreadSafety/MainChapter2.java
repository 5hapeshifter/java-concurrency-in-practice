package chapter2ThreadSafety;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;

public class MainChapter2 {

    public static void main(String[] args) throws ServletException, IOException {
//        List<Thread> safeWorkers = new java.util.ArrayList<>();
//
//        System.out.printf("\n\nStatelessFactorizer starts:\n\n");
//        StatelessFactorizer factorizer = new StatelessFactorizer();
//        for (int i = 20 ; i <25 ; i++){
//            MockServletRequest request = new MockServletRequest(String.valueOf(i));
//            MockServletResponse response = new MockServletResponse();
//            ThreadWorker worker = new ThreadWorker(factorizer, request, response);
//            safeWorkers.add(new Thread(worker));
//        }
//         safeWorkers.forEach(Thread::start);

        //////////////////////

        System.out.printf("\n\nUnsafeCountStatelessFactorizer starts:\n\n");
        List<Thread> unsafeWorkers = new java.util.ArrayList<>();

        UnsafeCountStatelessFactorizer unsafeFactorizer = new UnsafeCountStatelessFactorizer();
        for (int i = 20 ; i <30 ; i++){
            MockServletRequest request = new MockServletRequest(String.valueOf(i));
            MockServletResponse response = new MockServletResponse();
            ThreadWorker worker = new ThreadWorker(unsafeFactorizer, request, response);
            unsafeWorkers.add(new Thread(worker));
        }
//        unsafeWorkers.forEach(Thread::start);
        for (int i = 0; i < unsafeWorkers.size(); i++) {
            unsafeWorkers.get(i).start();
        }

        //////////////////////

//        System.out.println("Lazy initialization starts:\n\n");
//        List<Thread> lazies = new java.util.ArrayList<>();
//
//        /**
//         * Lazy initialization is not thread-safe - EXAMPLE
//         */
//        LazyInitExample lazyInitExample = new LazyInitExample();
//        Runnable lazyWorker = () -> {
//            System.out.println("Items: " + lazyInitExample.getItems());
//        };
//
//        for (int i = 0 ; i <20 ; i++){
//            lazies.add(new Thread(lazyWorker));
//        }
//        lazies.forEach(Thread::start);

        //////////////////////


    }
}
