package eu.sorescu.java.selenium.easy;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import eu.sorescu.java.lang.Functionals;
import org.apache.commons.io.IOUtils;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.google.common.collect.ImmutableMap;
import org.openqa.selenium.remote.RemoteWebDriver;

public class SeleniumWrapper {

    private RemoteWebDriver driver;

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
//        driver.manage().window().maximize();
    }

    private void setChromeDriverPath() {
        String osName = System.getProperty("os.name").toLowerCase();
        String executableName;
        if (osName.contains("linux")) {
            executableName = System.getProperty("os.arch").contains("64") ? "linux_64_chromedriver" : "linux_32_chromedriver";
        } else if (osName.contains("windows")) {
            executableName = "win_chromedriver.exe";
        } else if (osName.contains("mac")) {
            executableName = "mac_chromedriver";
        } else {
            throw new java.lang.InstantiationError("OS " + osName + " Selenium driver not supported.");
        }
        String driverExecutableLocation;
        try {
            URI uri = this.getClass().getResource(executableName).toURI();
            driverExecutableLocation = Paths.get(uri).toAbsolutePath().toString();
        } catch (Throwable e1) {
            try {
                File chromeDriverFile = File.createTempFile("chrome_driver_", "");
                Files.copy(this.getClass().getResourceAsStream(executableName), chromeDriverFile.toPath());
                driverExecutableLocation = chromeDriverFile.getAbsolutePath();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        System.setProperty("webdriver.chrome.driver", driverExecutableLocation);
    }

    private static final Map<String,String> scriptCache = new HashMap<>();

    public static String getScript(String fileName) throws IOException {
        return scriptCache.computeIfAbsent(fileName, name-> Functionals.TryGet(()-> IOUtils.toString(SeleniumWrapper.class.getResourceAsStream(name))));
    }

    public Object eval(String expression, Object...args)throws IOException{
        return executeScript("return("+expression+");",args);
    }

    public Object executeScript(String script, Object... args) throws IOException {
        if (!"function".equals(driver.executeScript("return typeof(jQuery)"))) {
            driver.executeScript(getScript("jquery-1.11.0.min.js"));
            driver.executeScript("jQuery.noConflict()");
        }
        if (!"function".equals(driver.executeScript("return typeof(sQuery)"))) {
            driver.executeScript(getScript("sQuery.js"));
        }
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
            this.currentPath = path;
        } catch (Throwable t) {
            this.currentPath = "invalid";
        }
    }

    @SuppressWarnings("unchecked")
    private WebElementReference UNIQUE(String selector) throws IOException {
        List<WebElementReference> result = new ArrayList<WebElementReference>();
        try {
//            List<WebElement> webElements = (List<WebElement>) executeScript(
//                    "var result=[];var temp=jQuery(arguments[0]);for(var i=0;i<temp.length;i++)result[i]=temp[i];return result;",
//                    selector);
            List<WebElement> webElements = (List<WebElement>) eval("sQuery(arguments[0]).toArray()",selector);
            if (webElements.size() == 1)
                return new WebElementReference(this, this.currentPath,
                        webElements.get(0), selector);
            if (webElements.size() > 1)
                return null;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        ArrayList<String> toDo = new ArrayList<String>();
        toDo.addAll(driver.getWindowHandles());
        while (toDo.size() > 0) {
            try {
                SwitchToPath(toDo.remove(0));
                try {
//                    List<WebElement> webElements = (List<WebElement>) executeScript(
//                            "var result=[];var temp=jQuery(arguments[0]);for(var i=0;i<temp.length;i++)result[i]=temp[i];return result;",
//                            selector);
                    List<WebElement> webElements=(List<WebElement>) eval("sQuery(arguments[0]).toArray()");
                    for (WebElement element : webElements)
                        result.add(new WebElementReference(this,
                                this.currentPath, element, selector));
                } catch (Throwable ignored) {
                }
                List<WebElement> iframes = driver
                        .findElementsByTagName("iframe");
                if (!currentPath.equals("invalid"))
                    for (int i = 0; i < iframes.size(); i++) {
                        toDo.add(currentPath + " " + i);
                    }
            } catch (Throwable ignored) {
            }
        }
        if (result.size() == 1)
            return result.get(0);
        else
            throw new RuntimeException("UNIQUE " + result.size()
                    + " results for: " + selector);
    }

    int GLOBAL_TIMEOUT = 20000;

    public WebElementReference WAIT_UNIQUE(String selector) throws IOException {
        Timeout timeout = new Timeout(GLOBAL_TIMEOUT);
        for (; ; ) {
            try {
                WebElementReference result = UNIQUE(selector);
                if (result != null)
                    return result;
                if (timeout.done())
                    throw new RuntimeException("No element found for "
                            + selector);
            } catch (Throwable t) {
                if (timeout.done())
                    throw new RuntimeException(t);
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.close();
    }
}