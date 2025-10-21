package chapter2ThreadSafety;


import annotations.GuardedBy;
import annotations.NotThreadSafe;
import annotations.ThreadSafe;

import javax.servlet.*;
import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@NotThreadSafe
public class UnsafeCountStatelessFactorizer implements Servlet {

    /* EXEMPLO 1
    // AtomicLong make this thread-safe with one shared state variable
//    private long count = 0;
    private final AtomicLong count = new AtomicLong(0);
    public long getCount() {
        return count.get();
    }

     */

    /* Exemplo 2 - Using atomic references, we cannot update both lastNumber and lastFactors simultaneously, even
         though each call to set is atomic; there is still a WINDOW of vulnerability when one has been modified and the
         other has not, and during that time other threads may see an inconsistent state. Similarly, the two values,
         thread B could have changed them, and again A may observe an inconsistent state.

         USE ATOMIC OPERATIONS FOR THIS KIND OF SCENARIO, NOT ONLY ATOMIC REFERENCES/VARIABLES.
     */

    /* Exemplo 3 usando Atomic References with intrinsic lock
    @GuardedBy("this") // Using intrinsic lock to guard both variables
    private final AtomicReference<BigInteger> lastNumber =
            new AtomicReference<>(BigInteger.ZERO);
    @GuardedBy("this")
    private final AtomicReference<BigInteger[]> lastFactors =
            new AtomicReference<>();

     */
    @GuardedBy("this") // Using synchronized methods to guard both variables and turn operations atomic
    private BigInteger lastNumber;
    @GuardedBy("this")
    private BigInteger[] lastFactors;


    @Override
    public void init(ServletConfig config) throws ServletException {

    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    /* EXEMPLO 1
    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        System.out.printf("servlet id: %d, thread: %s\n",
                System.identityHashCode(this), Thread.currentThread().getName());
        BigInteger i = extractFromRequest(req);
        BigInteger[] factors = factor(i);
        // AtomicLong make this thread-safe with one shared state variable
        long result = count.incrementAndGet();// Unsafe increment
        encodeIntoResponse(res, factors);
        System.out.printf("Thread name: %s, number: %s, factors: %s, count: %d \n",
                Thread.currentThread().getName(), i, java.util.Arrays.toString(factors), result);
    }
     */

    @Override
    /* Exemplo 3 - using Atomic References
       public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
       System.out.printf("servlet id: %d, thread: %s\n",
                System.identityHashCode(this), Thread.currentThread().getName());
        BigInteger i = extractFromRequest(req);
        if (i.equals(lastNumber.get())) {
            encodeIntoResponse(res, lastFactors.get());
        } else {
            BigInteger[] factors = factor(i);
            lastNumber.set(i);
            lastFactors.set(factors);
            encodeIntoResponse(res, factors);
        }
        System.out.printf("Thread name: %s, number: %s, factors: %s, count: %d \n",
                Thread.currentThread().getName(), i, lastFactors, i);
    }
     */

    public synchronized void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        System.out.printf("servlet id: %d, thread: %s\n",
                System.identityHashCode(this), Thread.currentThread().getName());
        BigInteger i = extractFromRequest(req);
        if (i.equals(lastNumber)) {
            encodeIntoResponse(res, lastFactors);
        } else {
            BigInteger[] factors = factor(i);
            lastNumber = i;
            lastFactors = factors;
            encodeIntoResponse(res, factors);
        }
        System.out.printf("Thread name: %s, number: %s, factors: %s, count: %d \n",
                Thread.currentThread().getName(), i, lastFactors, i);
    }

    @Override
    public String getServletInfo() {
        return "";
    }

    @Override
    public void destroy() {

    }

    private BigInteger extractFromRequest(ServletRequest req) {
        String input = req.getParameter("number");
        try {
            return new BigInteger(input);
        } catch (NumberFormatException e) {
            return BigInteger.ZERO; // or throw an exception if preferred
        }
    }

    private BigInteger[] factor(BigInteger number) {
        java.util.List<BigInteger> factors = new java.util.ArrayList<>();
        BigInteger two = BigInteger.valueOf(2);
        for (BigInteger i = two; i.compareTo(number) <= 0; i = i.add(BigInteger.ONE)) {
            while (number.mod(i).equals(BigInteger.ZERO)) {
                factors.add(i);
                number = number.divide(i);
            }
        }
        return factors.toArray(new BigInteger[0]);
    }

    private void encodeIntoResponse(ServletResponse res, BigInteger[] factors) throws IOException {
        res.getWriter().write(java.util.Arrays.toString(factors));
    }
}
