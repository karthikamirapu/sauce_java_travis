
/**
 * Created by Karthik on 1/20/15.
 */
/*import com.saucelabs.common.SauceOnDemandAuthentication;
import com.saucelabs.common.SauceOnDemandSessionIdProvider;
import com.sun.tools.classfile.ConstantPool;*/
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.By;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import javax.xml.ws.WebEndpoint;
import java.net.URL;
import java.util.List;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;

/**
 * Simple {@link RemoteWebDriver} test that demonstrates how to run your Selenium tests with <a href="http://saucelabs.com/ondemand">Sauce OnDemand</a>.
 * *
 * @author Ross Rowe
 */
public class PropertySearchTest {

    private WebDriver driver;
    private WebElement element;
    public static final String PRICE_LOW = "50000";
    public static final String PRICE_HIGH= "75000";

    //public SauceOnDemandAuthentication authentication = new SauceOnDemandAuthentication(System.getenv("SAUCE_USERNAME"), System.getenv("SAUCE_ACCESS_KEY"));


    @Before
    public void setUp() throws Exception {

        DesiredCapabilities capabilities = DesiredCapabilities.firefox();
        capabilities.setCapability("version", "35");
        capabilities.setCapability("platform", Platform.XP);
        capabilities.setCapability("name","kingkarthik");
        this.driver = new RemoteWebDriver(
                new URL("http://karthikamirapu:75ff59d4-4d26-429a-bb15-f10ccb6c7383@ondemand.saucelabs.com:80/wd/hub"),
                capabilities);
        //this.driver = new FirefoxDriver();
    }

    @Rule
    public TestWatcher watchman = new TestWatcher() {
        @Override
        protected void failed(Throwable e,Description d) {
            System.out.println("FAILED: "+d.getClassName()+"->"+d.getMethodName());
        }

        @Override
        protected void succeeded(Description d) {
            System.out.println("PASSED: "+d.getClassName()+"->"+d.getMethodName());
        }
    };

    @Test
    public void propertyPriceFilterTest() throws Exception {

        String investment_amount_str;
        String investment_portfolio;
        String return_filter;
        String appr_filter;
        String property_class_filter;
        String property_price_min_filter;
        String property_price_max_filter;
        String year_built_min_filter;
        String year_built_max_filter;
        String HIL_list[];

        int i=0;
        int temp_investment;
        int sizeofoddrows=0;
        int sizeofevenrows=0;

        driver.get("https://investor.homeunion.com");
        driver.manage().window().maximize();
        try { Thread.sleep(5000l); } catch (Exception e) { throw new RuntimeException(e); }

        driver.findElement(By.xpath("//html/body/div[2]/div[3]/div/div[2]/div/div[3]/div/div[7]/div/div/div/div/div/div/div/a")).click();
        driver.findElement(By.id("login-link")).click();
        try { Thread.sleep(2000l); } catch (Exception e) { throw new RuntimeException(e); }

        //Enter Username
        driver.findElement(By.id("login-username-input")).sendKeys("demo@homeunion.com");
        //Enter password
        driver.findElement(By.id("login-password-input")).sendKeys("123456");
        // what are we waiting for??? Time to login
        driver.findElement(By.id("login-btn")).click();
        //wait for sometime till the page loads
        try { Thread.sleep(3000l); } catch (Exception e) { throw new RuntimeException(e); }

        //Go to browse page since we are testing browse page
        driver.findElement(By.xpath("//html/body/div[2]/div[3]/div/div[1]/div/ul/li[2]/a/span[1]")).click();
        try { Thread.sleep(1000l); } catch (Exception e) { throw new RuntimeException(e); }
        //then go to property search
        driver.findElement(By.xpath("//html/body/div[2]/div[3]/div/div[1]/div/ul/li[2]/ul/li[2]/a")).click();
        //wait a sec for page to load
        try { Thread.sleep(3000l); } catch (Exception e) { throw new RuntimeException(e); }

        //Set the property price min value
        driver.findElement(By.id("price_from_c")).sendKeys(PRICE_LOW);
        //Set the property price max value
        driver.findElement(By.id("price_to_c")).sendKeys(PRICE_HIGH);
        //Clear the return and appreciation fields
        driver.findElement(By.id("srch_return")).clear();
        driver.findElement(By.id("srch_appreciation")).clear();
        //Search....
        driver.findElement(By.id("prop_srch_btn")).click();
        //wait a sec for page to load
        try { Thread.sleep(3000l); } catch (Exception e) { throw new RuntimeException(e); }

        //Time to do some validation
        String price_low = PRICE_LOW;
        String price_high = PRICE_HIGH;
        String property_price;
        String property_price_val;
        String match_found="";
        boolean final_page = false;

        do {
             //Check odd rows in search results
             List<WebElement> odd_rows = driver.findElements(By.xpath("//tr[@class='odd']/td[@class=' sorting_1']"));
             sizeofoddrows = odd_rows.size();


             for (i = 0; i < sizeofoddrows; i++) {
                 property_price = odd_rows.get(i).getText();
                 property_price_val = property_price.replaceAll("(?<=\\d),(?=\\d)|\\$", "");
                 temp_investment = Integer.parseInt(property_price_val);
                 if (temp_investment < Integer.parseInt(PRICE_LOW) || temp_investment > Integer.parseInt(PRICE_HIGH)) {
                     Assert.fail("Property price is not within search limits");
                    }
                }

             //Check even rows in search results
             List<WebElement> even_rows = driver.findElements(By.xpath("//tr[@class='even']/td[@class=' sorting_1']"));
             sizeofevenrows = even_rows.size();

             for (i = 0; i < sizeofevenrows; i++) {
                 property_price = even_rows.get(i).getText();
                 property_price_val = property_price.replaceAll("(?<=\\d),(?=\\d)|\\$", "");
                 temp_investment = Integer.parseInt(property_price_val);
                 if (temp_investment < Integer.parseInt(PRICE_LOW) || temp_investment > Integer.parseInt(PRICE_HIGH)) {
                     Assert.fail("Property price is not within search limits");
                    }
             }

            //Below code is to check whether this is the last page
            WebElement next_page_element = driver.findElement(By.xpath("//ul[contains(@class, 'pagination')]/li[contains(@class,'next')]"));

            match_found = next_page_element.getAttribute("class");
            if(match_found.matches("(.*)disabled"))
                final_page = true; //yep, this is the last page
            else {
                driver.findElement(By.cssSelector("i.fa.fa-angle-right")).click(); //too bad, still few more pages to check
                //wait a sec for page to load
                try { Thread.sleep(1500l); } catch (Exception e) { throw new RuntimeException(e); }
            }

        }while (final_page == false);
    }

    @After
    public void tearDown() throws Exception {
        driver.quit();
    }

}

