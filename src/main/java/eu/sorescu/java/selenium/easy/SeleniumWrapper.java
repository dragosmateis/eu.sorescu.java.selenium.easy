package eu.sorescu.java.selenium.easy;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.google.common.collect.ImmutableMap;

public class SeleniumWrapper {

	public ChromeDriver driver;

	public SeleniumWrapper() {
		setChromeDriverPath();
		DesiredCapabilities dc = new DesiredCapabilities();
		dc.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR,
				UnexpectedAlertBehaviour.DISMISS);
		dc.setCapability(CapabilityType.SUPPORTS_ALERTS, false);
		ChromeOptions chromeOptions = new ChromeOptions();
		DesiredCapabilities capabilities = DesiredCapabilities.chrome();
		try {
			capabilities.setCapability("chrome.prefs", ImmutableMap
					.of("download.default_directory",
							File.createTempFile("d1402251426", "delete_me")
									.getParent(),
							"download.prompt_for_download", "false",
							"download.extensions_to_open", "pdf"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		driver = new ChromeDriver(chromeOptions);
		driver.manage().window().maximize();
	}

	private void setChromeDriverPath() {
		InputStream input;
		String osName = System.getProperty("os.name").toLowerCase();
		String executableName;
		if (osName.contains("linux")) {
			if (System.getProperty("os.arch").indexOf("64") >= 0)
				executableName = "linux_64_chromedriver";
			else
				executableName = "linux_32_chromedriver";
		} else if (osName.contains("windows")) {
			executableName = "win_chromedriver.exe";
		} else if (osName.contains("mac")) {
			executableName = "mac_chromedriver";
		} else {
			throw new java.lang.InstantiationError("OS " + osName
					+ " Selenium driver not supported.");
		}
		String driverExecutableLocation;
		try {
			URI uri = this.getClass().getResource(executableName).toURI();
			driverExecutableLocation = Paths.get(uri).toAbsolutePath()
					.toString();
		} catch (Throwable e1) {
			input = this.getClass().getResourceAsStream(executableName);
			try {
				File chromeDriverFile = File.createTempFile("chrome_driver_",
						"");
				IOUtils.copy(input, new FileOutputStream(chromeDriverFile));
				driverExecutableLocation = chromeDriverFile.getAbsolutePath();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		System.setProperty("webdriver.chrome.driver", driverExecutableLocation);
	}

	public byte[] captureScreenToByteArray() {
		return driver.getScreenshotAs(OutputType.BYTES);
	}

	public File captureScreenToFile() {
		return driver.getScreenshotAs(OutputType.FILE);
	}

	private static final Properties scriptCache = new Properties();

	public static final String getScript(String fileName) throws IOException {
		if (!scriptCache.containsKey(fileName)) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			IOUtils.copy(SeleniumWrapper.class.getResourceAsStream(fileName),
					baos);
			scriptCache.setProperty(fileName, baos.toString());
		}
		return scriptCache.getProperty(fileName);
	}

	public Object eval(String script, Object... args) throws IOException {
		if (!"function".equals(driver.executeScript("return typeof(jQuery)"))) {
			driver.executeScript(getScript("jquery-1.11.0.min.js"));
			driver.executeScript("jQuery.noConflict()");
		}
		System.out.println(script);
		return driver.executeScript(script, args);
	}

	public void url(String url) {
		driver.get(url);
	}

	public String url() {
		return driver.getCurrentUrl();
	}

	public void close() {
		driver.quit();
	}

	private String currentPath = "";

	protected void SwitchToPath(String path) {
		try {
			String[] tokens = path.split("\\s");
			if (tokens[0].length() > 0)
				if (tokens[0].equals("invalid")
						|| !driver.getWindowHandle().equals(tokens[0]))
					driver.switchTo().window(tokens[0]);
			driver.switchTo().defaultContent();
			for (int tokenIdx = 1; tokenIdx < tokens.length; tokenIdx++)
				if (tokens[tokenIdx].length() > 0)
					driver.switchTo().frame(Integer.parseInt(tokens[tokenIdx]));
			// System.out.println(" SwitchToPath SETTING path " + path);
			this.currentPath = path;
		} catch (Throwable t) {
			// System.out.println(" SwitchToPath FAILED SETTING path " + path);
			this.currentPath = "invalid";
		}
	}

	@SuppressWarnings("unchecked")
	public WebElementReference UNIQUE(String selector) throws IOException {
		List<WebElementReference> result = new ArrayList<WebElementReference>();
		try {
			// System.out.println("************************* " + selector);
			List<WebElement> webElements = (List<WebElement>) eval(
					"var result=[];var temp=jQuery(arguments[0]);for(var i=0;i<temp.length;i++)result[i]=temp[i];return result;",
					selector);
			// System.out.println("*** " + webElements.size());
			if (webElements.size() == 1)
				return new WebElementReference(this, this.currentPath,
						webElements.get(0), selector);
			if (webElements.size() > 1)
				return null;
		} catch (Throwable t) {
			t.printStackTrace();
		}
		ArrayList<String> toDo = new ArrayList<String>();
		for (String windowHandle : driver.getWindowHandles())
			toDo.add(windowHandle);
		while (toDo.size() > 0) {
			try {
				SwitchToPath(toDo.remove(0));
				try {
					List<WebElement> webElements = (List<WebElement>) eval(
							"var result=[];var temp=jQuery(arguments[0]);for(var i=0;i<temp.length;i++)result[i]=temp[i];return result;",
							selector);
					for (WebElement element : webElements)
						result.add(new WebElementReference(this,
								this.currentPath, element, selector));
				} catch (Throwable t) {
					// t.printStackTrace();
				}
				List<WebElement> iframes = driver
						.findElementsByTagName("iframe");
				if (!currentPath.equals("invalid"))
					for (int i = 0; i < iframes.size(); i++) {
						toDo.add(currentPath + " " + i);
					}
			} catch (Throwable e) {
				// e.printStackTrace();
			}
		}
		if (result.size() == 1)
			return result.get(0);
		else
			throw new RuntimeException("UNIQUE " + result.size()
					+ " results for: " + selector);
	}

	@Override
	protected void finalize() throws Throwable {
		this.close();
	}
}