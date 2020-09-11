/*This program was made to scrape specific information from the store locator in the Benjamin Moore website.
 * Based on a csv file containing a list of cities, the program will generate another csv file containing information about the
 * vendors in those cities.
 * */
package mainPackage;
/**
 * Author: Guilherme Jardim
 * version: 1.0
 * Date: 28 July 2020
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.*;

import com.opencsv.CSVReader;

public class SeleniumScript {
	/**
	 * The path for the csv file with the list of cities
	 */
	private static String sourceFile = "/home/gjardim/Documents/Projects/SanctuaryPaint/BMVendors/CAcities.csv";
	/**
	 * The target output file path
	 */
	private static String targetFile = "/home/gjardim/Documents/Projects/SanctuaryPaint/BMVendors/CA_BMvendors.csv";
	/**
	 * An instance of the VendorsManager class, which contains properties and methods to store the results found.
	 */
	private static VendorsManager manager = new VendorsManager();
	private static WebElement inputBox;
	private static WebElement searchButton;
	private static WebElement radiusField;
	private static String targetCountry = "Canada";
	/**
	 * This variable stores the cumulative total of results found. Each vendor is one result.
	 */
	private static int resultsCounter = 0;

	public static void main(String[] args) {

		System.setProperty("webdriver.gecko.driver", "/home/gjardim/Programs/Gecko_Driver/geckodriver"); //Gecko driver is the driver for Firefox browser
		WebDriver driver = new FirefoxDriver();
		driver.get("https://www.benjaminmoore.com/en-ca/store-locator");
		getWebElements(driver);
		
		List<String[]> citiesList = loadFromCsvFile(sourceFile);
		
		String location = "";
		for (int index = 0; index < citiesList.size(); index++) {
			String[] city = citiesList.get(index);
			location = city[0] + ", " + city[1] + ", " + getTargetCountry();
			scrapeVendorsInfo(driver, location);

		}

		driver.close();

		manager.saveToFile(targetFile);
		System.out.println("Total of records found: " + getCounter());
		System.out.println("Total of records saved to file: " + manager.getVendorsList().size());

	}
	/**
	 * This method finds the respective WebElement and loads them into the class variables.
	 * @param driver - the Selenium WebDriver
	 */
	private static void getWebElements(WebDriver driver) {
		inputBox = driver.findElement(By.id("retail-search-input-query"));
		searchButton = driver.findElement(
				By.xpath("/html/body/div[1]/main/div[2]/div[1]/div/div[2]/div/form/div[2]/div[1]/div/span/button"));
		radiusField = driver.findElement(By.cssSelector("#radius-single-button"));

	}
	/**
	 * This method will automatically send location keys to the target field, search and scrape the results found and save them to
	 * the list of Benjamin Moore Vendors.
	 * @param driver - the Selenium WebDriver
	 * @param location - a String which tells the city, province and country to search for
	 */
	private static void scrapeVendorsInfo(WebDriver driver, String location) {

		inputBox.click();
		inputBox.sendKeys(location);
		// driver.findElement(By.xpath("//*[@id=\"typeahead-13-7307-option-0\"]")).click();
		radiusField.click();
		driver.findElement(By.cssSelector("li[event-label='within 99.4194 miles']")).click();

		if (searchButton != null)
			searchButton.click();

		try {
			Thread.sleep(2500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		boolean thereIsError = true;

		do {

			List<WebElement> vendorsFromSite = new ArrayList<>();
			vendorsFromSite = driver.findElements(By.className("detail-container"));

			System.out.println(vendorsFromSite.size() + " records found for " + location);
			try {
				if (vendorsFromSite.size() > 0) {
					for (int i = 0; i < vendorsFromSite.size(); i++) {
						String name = vendorsFromSite.get(i).findElement(By.cssSelector("a.store-name"))
								.getAttribute("innerHTML").trim();
						String phone = vendorsFromSite.get(i)
								.findElement(By.cssSelector("a[data-ng-bind='::result.phone']"))
								.getAttribute("innerHTML").trim();
						String address1 = vendorsFromSite.get(i)
								.findElement(By.cssSelector("span[data-ng-bind='::result.addressLine1']"))
								.getAttribute("innerHTML").trim();
						String address2 = vendorsFromSite.get(i)
								.findElement(By.cssSelector("span[data-ng-bind='::result.addressLine2']"))
								.getAttribute("innerHTML").trim();
						String city = vendorsFromSite.get(i)
								.findElement(By.cssSelector("span[data-ng-bind='::result.city']"))
								.getAttribute("innerHTML").trim();
						String state = vendorsFromSite.get(i)
								.findElement(By.cssSelector("span[data-ng-bind='::result.state']"))
								.getAttribute("innerHTML").trim();
						String postalCode = vendorsFromSite.get(i)
								.findElement(By.cssSelector("span[data-ng-bind='::result.zipCode']"))
								.getAttribute("innerHTML").trim();

						String type = "";
						String classe = vendorsFromSite.get(i).findElement(By.cssSelector(".bmc-marker-container"))
								.getAttribute("class");
						if (classe.equals("bmc-marker-container is-premiere"))
							type = "Authorized retailer plus";
						else if (classe.equals("bmc-marker-container is-specialty"))
							type = "Specialty retailer";
						else
							type = "Authorized retailer";

						System.out.println(name + ", " + type + ", " + phone + ", " + address1 + ", " + address2 + ", "
								+ city + ", " + state + ", " + postalCode);
						BMVendor bmVendor = new BMVendor(name, type, address1, address2, city, state,
								getTargetCountry(), postalCode, phone);

						manager.addToVendorsList(bmVendor);

					}
					incrementCounter(vendorsFromSite.size());
					System.out.println();
				}
				thereIsError = false;

			} catch (StaleElementReferenceException ex) {
				
				System.out.println("\n" + ex.getMessage());
				thereIsError = true;

			}

		} while (thereIsError);

	}
	/**
	 * This methods reads a csv file and returns a List of String arrays.
	 * @param file - String
	 * @return results - List<String[]>
	 */
	private static List<String[]> loadFromCsvFile(String file) {
		try (FileReader input = new FileReader(new File(file)); CSVReader reader = new CSVReader(input)) {
			List<String[]> results = new ArrayList<>();
			Iterator<String[]> it = reader.iterator();
			while (it.hasNext()) {
				String[] line = it.next();
				for (int i = 0; i < line.length; i++)
					line[i] = line[i].trim();
				results.add(line);
			}
			return results;

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	private static String getTargetCountry() {
		return targetCountry;
	}
	
	private static void incrementCounter(int increment) {
		resultsCounter += increment;
	}
	
	private static int getCounter() {
		return resultsCounter;
	}

}
