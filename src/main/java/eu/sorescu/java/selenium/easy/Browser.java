package eu.sorescu.java.selenium.easy;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Browser {
    public SeleniumWrapper seleniumWrapper = null;

    public Browser url(String url) {
        if (seleniumWrapper == null)
            seleniumWrapper = new SeleniumWrapper();
        String currentUrl = seleniumWrapper.url();
        if (currentUrl.substring(0, 4).equals("http"))
            try {
                url = new URL(new URL(currentUrl), url).toString();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        seleniumWrapper.url(url);
        return this;
    }

    public String url() {
        if (seleniumWrapper == null)
            return null;
        return seleniumWrapper.url();
    }

    public Object evalJs(String expression, Object... params)
            throws IOException {
        if (params == null)
            params = new Object[0];
        return seleniumWrapper.executeScript(expression, params);
    }

    public void click(String path) throws Throwable {
        seleniumWrapper.WAIT_UNIQUE(path).click();
    }

    public void type(String target, String value) throws Throwable {
        seleniumWrapper.WAIT_UNIQUE(target).type(value);
    }

    public String val(String path) throws Throwable {
        return String.valueOf(seleniumWrapper.WAIT_UNIQUE(path).get());
    }

    public void val(String path, String value) throws Throwable {
        seleniumWrapper.WAIT_UNIQUE(path).set(value);
    }

    public void val(String path, Number value) throws Throwable {
        seleniumWrapper.WAIT_UNIQUE(path).set(value);
    }

    public void val(String path, boolean value) throws Throwable {
        seleniumWrapper.WAIT_UNIQUE(path).set(value);
    }

    public void testEQ(String selector, String value) throws Throwable {
        Timeout timeout = new Timeout(seleniumWrapper.GLOBAL_TIMEOUT);
        for (; ; ) {
            try {
                if (value.equals(seleniumWrapper.WAIT_UNIQUE(selector).get()))
                    return;
            } catch (Throwable t) {
                if (timeout.done())
                    throw t;
            }
        }
    }

    public void testRegex(String selector, String regex) throws Throwable {
        Timeout timeout = new Timeout(seleniumWrapper.GLOBAL_TIMEOUT);
        for (; ; ) {
            try {
                if (seleniumWrapper.WAIT_UNIQUE(selector).get().toString()
                        .matches(regex))
                    return;
            } catch (Throwable t) {
                if (timeout.done())
                    throw t;
            }
        }
    }

    public void close() {
        seleniumWrapper.close();
    }

    public String attr(String path, String attrName) throws Throwable {
        return String.valueOf(seleniumWrapper.WAIT_UNIQUE(path).getAttr(
                attrName));
    }

    public Object with(String context, String expression) throws Throwable {
        return seleniumWrapper.WAIT_UNIQUE(context).with(expression);
    }
}
