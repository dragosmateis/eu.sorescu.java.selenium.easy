package eu.sorescu.java.selenium.easy;

import java.io.IOException;

public class Browser {
	public SeleniumWrapper seleniumWrapper = null;
	private int GLOBAL_TIMEOUT = 10000;

	public Browser url(String url) {
		if (this.seleniumWrapper == null)
			this.seleniumWrapper = new SeleniumWrapper();
		this.seleniumWrapper.url(url);
		return this;
	}

	public String url() {
		return this.seleniumWrapper.url();
	}

	public Object evalJs(String expression, Object... params) throws IOException {
		if (params == null)
			params = new Object[0];
		return this.seleniumWrapper.eval(expression, params);
	}

	/*
	 * public void draw(String path, Point[] points) throws Throwable { Timeout
	 * timeout = new Timeout(this.GLOBAL_TIMEOUT); for (;;) { try {
	 * this.seleniumWrapper.UNIQUE(path).draw(points); return; } catch
	 * (Throwable t) { t.printStackTrace(); if (timeout.done()) throw t; } } }
	 */

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

	// public int clickAny(String[] path) throws Throwable {
	// Timeout timeout = new Timeout(this.GLOBAL_TIMEOUT);
	// int i = 0;
	// for (;;) {
	// try {
	// this.seleniumWrapper.UNIQUE(path[i]).click();
	// return i;
	// } catch (Throwable t) {
	// if (timeout.done())
	// throw new RuntimeException("None available: " +
	// Arrays.asList(path).toString());
	// }
	// i++;
	// i %= path.length;
	// }
	// }
	//
	// public void clickAnyUntilFirst(String[] strings) throws Throwable {
	// Timeout timeout = new Timeout(this.GLOBAL_TIMEOUT);
	// for (;;) {
	// try {
	// if (this.clickAny(strings) == 0)
	// return;
	// } catch (Throwable t) {
	// if (timeout.done())
	// throw t;
	// }
	// }
	// }

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

	// public void setForm(Properties kv) throws Throwable {
	// Timeout timeout = new Timeout(this.GLOBAL_TIMEOUT);
	// for (;;) {
	// boolean success = true;
	// for (String key : kv.stringPropertyNames()) {
	// try {
	// if (this.val(key).equals(kv.getProperty(key)))
	// continue;
	// success = false;
	// this.val(key, kv.getProperty(key));
	// } catch (Throwable t) {
	// if (timeout.done())
	// throw t;
	// }
	// }
	// if (success)
	// return;
	// if (timeout.done())
	// throw new TimeoutException("e1310101805 - could not set all values");
	// }
	// }

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

	public final void close() {
		this.seleniumWrapper.driver.close();
	}
}