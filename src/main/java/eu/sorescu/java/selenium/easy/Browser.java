package eu.sorescu.java.selenium.easy;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Browser {
	public SeleniumWrapper seleniumWrapper = null;
	private int GLOBAL_TIMEOUT = 10000;

	public Browser url(String url) {
		if (this.seleniumWrapper == null)
			this.seleniumWrapper = new SeleniumWrapper();
		if (this.seleniumWrapper != null) {
			String currentUrl = this.seleniumWrapper.url();
			if (currentUrl != null)
				try {
					url = new URL(new URL(currentUrl), url).toString();
				} catch (MalformedURLException e) {
					throw new RuntimeException(e);
				}
		}
		this.seleniumWrapper.url(url);
		return this;
	}

	public String url() {
		return this.seleniumWrapper.url();
	}

	public Object evalJs(String expression, Object... params)
			throws IOException {
		if (params == null)
			params = new Object[0];
		return this.seleniumWrapper.eval(expression, params);
	}

	public void click(String path) throws Throwable {
		Timeout timeout = new Timeout(this.GLOBAL_TIMEOUT);
		for (;;) {
			try {
				this.seleniumWrapper.UNIQUE(path).click();
				return;
			} catch (Throwable t) {
				if (timeout.done())
					throw new Throwable(path + " not identified.", t);
			}
		}
	}

	public void type(String target, String value) throws Throwable {
		Timeout timeout = new Timeout(this.GLOBAL_TIMEOUT);
		WebElementReference targetObject = null;
		for (;;) {
			try {
				targetObject = this.seleniumWrapper.UNIQUE(target);
				break;
			} catch (Throwable t) {
				if (timeout.done())
					throw t;
			}
		}
		;
		targetObject.type(value);
	}

	public String val(String path) throws Throwable {
		Timeout timeout = new Timeout(this.GLOBAL_TIMEOUT);
		for (;;) {
			try {
				return String.valueOf(this.seleniumWrapper.UNIQUE(path).get());
			} catch (Throwable t) {
				if (timeout.done())
					throw t;
			}
		}
	}

	public void val(String path, String value) throws Throwable {
		Timeout timeout = new Timeout(this.GLOBAL_TIMEOUT);
		for (;;) {
			try {
				this.seleniumWrapper.UNIQUE(path).set(value);
				return;
			} catch (Throwable t) {
				if (timeout.done())
					throw t;
			}
		}
	}

	public void testEQ(String selector, String value) throws Throwable {
		Timeout timeout = new Timeout(this.GLOBAL_TIMEOUT);
		for (;;) {
			try {
				if (value.equals(this.seleniumWrapper.UNIQUE(selector).get()))
					return;
			} catch (Throwable t) {
				if (timeout.done())
					throw t;
			}
		}
	}
}