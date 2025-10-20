package chapter2ThreadSafety;


import annotations.ThreadSafe;

import javax.servlet.*;
import java.io.IOException;
import java.math.BigInteger;

@ThreadSafe
public class StatelessFactorizer implements Servlet {
    @Override
    public void init(ServletConfig config) throws ServletException {

    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        BigInteger i = extractFromRequest(req);
        BigInteger[] factors = factor(i);
        encodeIntoResponse(res, factors);
        System.out.printf("Thread name: %s, number: %s, factors: %s%n",
                Thread.currentThread().getName(), i, java.util.Arrays.toString(factors));
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
