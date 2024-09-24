package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import io.github.bonigarcia.wdm.WebDriverManager;



public class EcommerceTests {
    private WebDriver driver;
    private String fullName; // Store the user's full name
    private String email;
    private String password = "SecurePassword123";


    @BeforeClass
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://magento.softwaretestingboard.com/");
    }

    @Test
    public void testCreateNewCustomerAccount() {
        driver.get("https://magento.softwaretestingboard.com/customer/account/create/");

        WebDriverWait wait = new WebDriverWait(driver, 10);


        String uniqueSuffix = String.valueOf(System.currentTimeMillis()); // Unique suffix based on current time
        String email = "john.doe+" + uniqueSuffix + "@example.com"; // Unique email
        String firstName = "John";
        String lastName = "Doe";
        String password = "SecurePassword123";
        fullName = firstName + " " + lastName;

        WebElement firstNameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("firstname")));
        firstNameInput.sendKeys(firstName);

        driver.findElement(By.id("lastname")).sendKeys(lastName);

        driver.findElement(By.id("email_address")).sendKeys(email);
        driver.findElement(By.id("password")).sendKeys(password);

        String passwordStrength = driver.findElement(By.id("password-strength-meter")).getText();
        Assert.assertNotEquals(passwordStrength, "No Password", "Password strength is incorrect");

        driver.findElement(By.id("password-confirmation")).sendKeys(password);

        driver.findElement(By.cssSelector("button.action.submit.primary[title='Create an Account']")).click();

        WebElement successMessageElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("message-success")));
        String successMessage = successMessageElement.getText();
        Assert.assertEquals(successMessage, "Thank you for registering with Main Website Store.", "Account creation failed.");

    }

    @Test(dependsOnMethods = {"testCreateNewCustomerAccount"})
    public void testCustomerLogin() {
        WebDriverWait wait = new WebDriverWait(driver, 20);

        // Verify successful login by checking for the welcome message
        WebElement welcomeMessageElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("logged-in")));
        String welcomeMessage = welcomeMessageElement.getText();
        Assert.assertTrue(welcomeMessage.contains(fullName), "Login failed: welcome message not found.");

    }



    @Test(dependsOnMethods = {"testCustomerLogin"})
    public void testAddMultipleItemsToCompareList() {
        WebDriverWait wait = new WebDriverWait(driver, 30);

        driver.get("https://magento.softwaretestingboard.com/women/tops-women/jackets-women.html");
        WebElement junoJacketCompareButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html//main[@id='maincontent']/div[@class='columns']/div[@class='column main']/div[3]/ol/li[2]/div[@class='product-item-info']//div[@class='actions-secondary']/a[2]")));
        junoJacketCompareButton.click();


        WebElement junoMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//main[@id='maincontent']//div[@role='alert']/div/div")));
        Assert.assertTrue(junoMessage.getText().contains("You added product Juno Jacket to the"), "Juno Jacket was not added to compare list.");

        WebElement oliviaJacketCompareButton = wait.until(ExpectedConditions.
                elementToBeClickable(By.xpath(
                        "/html//main[@id='maincontent']/div[@class='columns']/div[@class='column main']/div[3]/ol/li[1]/div[@class='product-item-info']//div[@class='actions-secondary']/a[2]")));
        oliviaJacketCompareButton.click();
        WebElement oliviaMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[role='alert'] > .message.message-success.success")));
        Assert.assertTrue(oliviaMessage.getText().contains("You added product Olivia 1/4 Zip Light Jacket to the"), "Olivia Jacket was not added to compare list.");

        driver.get("https://magento.softwaretestingboard.com/catalog/product_compare/index/uenc/aHR0cHM6Ly9tYWdlbnRvLnNvZnR3YXJldGVzdGluZ2JvYXJkLmNvbS9jdXN0b21lci9zZWN0aW9uL2xvYWQvP3NlY3Rpb25zPWNvbXBhcmUtcHJvZHVjdHMmZm9yY2VfbmV3X3NlY3Rpb25fdGltZXN0YW1wPWZhbHNlJl89MTcyNzExMjUyNzE1NA%2C%2C/");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html//main[@id='maincontent']//span[@class='base']")));

        Assert.assertTrue(driver.getPageSource().contains("Juno Jacket"), "Juno Jacket is not in the comparison list.");
        Assert.assertTrue(driver.getPageSource().contains("Olivia 1/4 Zip Light Jacket"), "Olivia Jacket is not in the comparison list.");
    }



    @Test(dependsOnMethods = {"testCustomerLogin"})
    public void testAddItemToWishlist() {
        WebDriverWait wait = new WebDriverWait(driver, 30);

        driver.get("https://magento.softwaretestingboard.com/women/tops-women/tanks-women.html");

        WebElement addToWishlistButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("/html//main[@id='maincontent']/div[@class='columns']/div[@class='column main']/div[3]/ol/li[1]/div[@class='product-item-info']//div[@class='actions-secondary']/a[1]"))); // Adjust the XPath as necessary
        addToWishlistButton.click();

        driver.get("https://magento.softwaretestingboard.com/wishlist");

        WebElement itemInWishlist = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//form[@id='wishlist-view-form']//ol[@class='product-items']/li[@class='product-item']//strong/a[@title='Breathe-Easy Tank']"))); // Adjust the XPath as necessary
        Assert.assertTrue(itemInWishlist.isDisplayed(), "Breathe-Easy Tank is not present in the wishlist.");
    }

    @Test(dependsOnMethods = {"testAddItemToWishlist"})
    public void testEditWishlist() {
        WebDriverWait wait = new WebDriverWait(driver, 30);

        driver.get("https://magento.softwaretestingboard.com/wishlist/");

        WebElement deleteButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//form[@id='wishlist-view-form']//ol[@class='product-items']/li[@class='product-item']/div[@class='product-item-info']//a[@title='Remove Item']")));

        deleteButton.click();

        WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//main[@id='maincontent']//div[@role='alert']/div/div[1]")));
        Assert.assertTrue(successMessage.getText().contains("Breathe-Easy Tank has been removed from your Wish List."), "Success message not displayed as expected.");


        WebElement itemInWishlist = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//form[@id='wishlist-view-form']//ol[@class='product-items']/li[@class='product-item']//strong/a[@title='Breathe-Easy Tank']")));
        Assert.assertFalse(itemInWishlist.isDisplayed(), "Breathe-Easy Tank is still present in the wishlist.");
    }





    @Test(dependsOnMethods = {"testCustomerLogin"})
    public void testCreateOrderAndValidate() {
        WebDriverWait wait = new WebDriverWait(driver, 60);

        driver.get("https://magento.softwaretestingboard.com/women/tops-women/jackets-women.html");

        WebElement junoJacketAddToCartButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("/html//main[@id='maincontent']/div[@class='columns']/div[@class='column main']/div[3]/ol/li[2]/div[@class='product-item-info']//form[@action='https://magento.softwaretestingboard.com/checkout/cart/add/uenc/aHR0cHM6Ly9tYWdlbnRvLnNvZnR3YXJldGVzdGluZ2JvYXJkLmNvbS93b21lbi90b3BzLXdvbWVuL2phY2tldHMtd29tZW4uaHRtbA%2C%2C/product/1380/']/button[@title='Add to Cart']/span[.='Add to Cart']")));
        junoJacketAddToCartButton.click();


        WebElement sizeOption = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[@id='product-options-wrapper']//div[@class='swatch-opt']/div[1]/div[@role='listbox']/div[1]")));
        sizeOption.click();

        WebElement colorOption = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[@id='product-options-wrapper']//div[@class='swatch-opt']/div[2]/div[@role='listbox']/div[1]")));
        colorOption.click();

        WebElement addToCartFinalButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("/html//button[@id='product-addtocart-button']")));
        addToCartFinalButton.click();

        WebElement cartIcon = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body//div[@class='minicart-wrapper']/a[@href='https://magento.softwaretestingboard.com/checkout/cart/']")));
        cartIcon.click();
        cartIcon.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='minicart-content-wrapper']//div[@class='subtotal']")));



        WebElement proceedToCheckoutButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html//button[@id='top-cart-btn-checkout']")));
        proceedToCheckoutButton.click();

        // Step 4: Enter shipping address (dummy data)
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='shipping-new-address-form']/div[@name='shippingAddress.firstname']//input[@name='firstname']"))).sendKeys("John");
        driver.findElement(By.xpath("/html//input[@id='LRPSXMK']")).sendKeys("Doe");
        driver.findElement(By.xpath("/html//input[@id='H3EACNE']")).sendKeys("123 Main St");
        driver.findElement(By.xpath("//div[@id='shipping-new-address-form']/div[@name='shippingAddress.city']//input[@name='city']")).sendKeys("Los Angeles");
        driver.findElement(By.xpath("/html//input[@id='YB3UNRA']")).sendKeys("90001");
        // driver.findElement(By.id("shipping:State")).sendKeys("Alaska");
        // driver.findElement(By.id("shipping:Country")).sendKeys("United States");
        driver.findElement(By.xpath("//div[@id='shipping-new-address-form']/div[@name='shippingAddress.telephone']//input[@name='telephone']")).sendKeys("9000112344");

        // Select state and country (update as necessary)
        WebElement stateDropdown = driver.findElement(By.xpath("/html//select[@id='IE7FMCD']"));
        stateDropdown.click();
        stateDropdown.findElement(By.xpath("//option[text()='California']")).click();

        WebElement countryDropdown = driver.findElement(By.xpath("/html//select[@id='T8HWCHC']"));
        countryDropdown.click();
        countryDropdown.findElement(By.xpath("//option[text()='United States']")).click();

        WebElement bestWayShipping = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@id='checkout-shipping-method-load']/table[@class='table-checkout-shipping-method']/tbody/tr[1]//input[@name='ko_unique_1']")));
        bestWayShipping.click();

        WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@id='shipping-method-buttons-container']//button[@type='submit']/span[.='Next']")));
        nextButton.click();

        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html//div[@id='checkout-payment-method-load']//div[@class='payment-group']/div[2]//button[@title='Place Order']"))).click(); // Select payment method
        WebElement placeOrderButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@title='Place Order']")));
        placeOrderButton.click();

        driver.get("https://magento.softwaretestingboard.com/sales/order/history/");
        WebElement ordersTable = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html//table[@id='my-orders-table']//td[@class='col date']")));
        Assert.assertTrue(ordersTable.isDisplayed(), "Orders table is not displayed.");

        Assert.assertTrue(driver.getPageSource().contains("Juno Jacket"), "Order for Juno Jacket not found in order history.");
    }



    @AfterClass
    public void tearDown() {
        driver.quit();
    }


}

