import eu.sorescu.java.selenium.easy.Browser;

public class Test {
	public static void main(String[] args) throws Throwable {
		Browser browser = new Browser();
		browser.url("http://www.google.com");
		browser.val("input[name=q]", "Test");
	}
}