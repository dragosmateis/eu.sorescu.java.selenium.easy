package eu.sorescu.java.selenium.easy;

import java.io.IOException;
import org.openqa.selenium.WebElement;

public class WebElementReference {
	public final SeleniumWrapper seleniumWrapper;
	public final String path;
	public final String mnemonic;
	public final WebElement element;

	@Override
	public String toString() {
		return super.toString() + "/" + this.path;
	}

	public WebElementReference(SeleniumWrapper seleniumWrapper, String path,
			WebElement element, String mnemonic) {
		this.seleniumWrapper = seleniumWrapper;
		this.path = path;
		this.mnemonic = mnemonic;
		this.element = element;
	}

	public void set(Object value) throws IOException {
		this.seleniumWrapper.SwitchToPath(this.path);
		this.seleniumWrapper.eval(
				SeleniumWrapper.getScript("WebElementSetValue.js"),
				this.element, value);
	}

	public Object get() throws IOException {
		this.seleniumWrapper.SwitchToPath(this.path);
		return this.seleniumWrapper.eval(
				SeleniumWrapper.getScript("WebElementGetValue.js"),
				this.element);
	}

	public void click() throws Exception {
		try {
			this.seleniumWrapper.SwitchToPath(this.path);
			element.click();
			System.out.println(this.path + " " + element);
		} catch (Throwable t) {
			throw new Exception("e1309041421 - could not click "
					+ this.mnemonic + ": " + t.getMessage(), t);
		}
	}

	public void clear() {
		this.seleniumWrapper.SwitchToPath(this.path);
		element.clear();
	}

	public void submit() {
		this.seleniumWrapper.SwitchToPath(this.path);
		element.submit();
	}

	public void type(String keysToSend) {
		this.seleniumWrapper.SwitchToPath(this.path);
		element.sendKeys(keysToSend);
	}

	public String getText() {
		this.seleniumWrapper.SwitchToPath(this.path);
		return element.getText();
	}

	public String getAttr(String name) {
		this.seleniumWrapper.SwitchToPath(this.path);
		return element.getAttribute(name);
	}

	public String getCss(String name) {
		this.seleniumWrapper.SwitchToPath(this.path);
		return element.getCssValue(name);
	}

	public String getTagName() {
		this.seleniumWrapper.SwitchToPath(this.path);
		return element.getTagName();
	}
}