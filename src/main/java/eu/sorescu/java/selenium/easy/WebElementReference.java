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

	public void set(String value) throws IOException {
		this.seleniumWrapper.SwitchToPath(this.path);
		// this.seleniumWrapper.driver.findElement(by)
		this.seleniumWrapper
				.eval("var nodes=jQuery(arguments[0]);nodes.val(arguments[1]);return nodes;",
						this.element, value);
	}

	public Object get() throws IOException {
		this.seleniumWrapper.SwitchToPath(this.path);
		return this.seleniumWrapper
				.eval("var jq=jQuery(arguments[0]);var tn=jq.prop('tagName').toLowerCase();if(tn=='select')return jq.val();if(tn=='input')return jq.val();if(tn=='textarea')return jq.val();return jq.text();",
						this.element);
	}

	//
	// public void draw(Point[] points) throws Exception {
	// try {
	// this.seleniumWrapper.SwitchToPath(this.path);
	// element.getText();
	// Actions action = new Actions(this.seleniumWrapper.driver);
	// action.moveToElement(element, points[0].x, points[0].y)
	// .clickAndHold()
	// .dragAndDropBy(element, points[1].x - points[0].x,
	// points[1].y - points[0].y).build().perform();
	// } catch (Throwable t) {
	// throw new Exception("e1402121529 - could not get native "
	// + this.mnemonic + " : " + t.getMessage(), t);
	// }
	// }

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