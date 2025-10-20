package chapter2ThreadSafety;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

public class ThreadWorker<T extends Servlet> implements Runnable {

    private T statelessFactorizer;
    private ServletRequest request;
    private ServletResponse response;

    public ThreadWorker(T statelessFactorizer, ServletRequest request, ServletResponse response) {
        this.statelessFactorizer = statelessFactorizer;
        this.request = request;
        this.response = response;
    }

    @Override
    public void run() {
        try {
            statelessFactorizer.service(request, response);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
