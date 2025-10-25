package chapter2ThreadSafety;


import annotations.GuardedBy;
import annotations.NotThreadSafe;
import annotations.ThreadSafe;

import javax.servlet.*;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

/**
 * Using sychronized blocks to make compound actions atomic
 */
@ThreadSafe
public class UnsafeCountStatelessFactorizer2 implements Servlet {

    @GuardedBy("this")
    private BigInteger lastNumber;
    @GuardedBy("this")
    private BigInteger[] lastFactors;
    @GuardedBy("this")
    private long hits;
    @GuardedBy("this")
    private long cacheHits;

    public synchronized long getHits() {
        return hits;
    }

    public long getCacheHitRatio() {
        return cacheHits / hits;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {

    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public synchronized void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        System.out.printf("servlet id: %d, thread: %s\n",
                System.identityHashCode(this), Thread.currentThread().getName());
        BigInteger i = extractFromRequest(req);
        BigInteger[] factors = null;

        // checking if cache is valid
        synchronized (this) {
            ++hits;
            if (i.equals(lastNumber)) {
                ++cacheHits;
                factors = lastFactors.clone();
            }
        }
        if (factors == null) {
            factors = factor(i);

            // updating cache data
            synchronized (this) {
                lastNumber = i;
                lastFactors = factors.clone();
            }
        }

        encodeIntoResponse(res, factors);

        System.out.printf("Thread name: %s, number: %s, factors: %s, count: %d \n",
                Thread.currentThread().getName(), i, Arrays.toString(lastFactors), i);
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
