package eu.sorescu.java.selenium.easy.test;

import eu.sorescu.java.selenium.easy.Browser;

public class Test {
	public static void main(String[] args) throws Throwable {
		Browser browser = new Browser();
		browser.url("http://www.google.com");
		browser.val("input[name=q]", "Test");
		browser.url("http://www.csee.wvu.edu/~riggs/html/select_example.html");
		browser.val("select[name=report]", "Week 3");
		browser.val("select[name=report]", "week2.html");
		browser.val("select[name=report]", 2);
		// browser.close();
	}
}