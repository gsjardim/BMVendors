package com.GJardim.BMVendors;
/*This program was made to scrape specific information from the store locator in the Benjamin Moore website.
 * Based on a csv file containing a list of cities, the program will generate another csv file containing information about the
 * vendors in those cities.
 * */

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
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.*;
import org.openqa.selenium.interactions.Actions;

import com.opencsv.CSVReader;

public class SeleniumScript {
	/**
	 * The path for the csv file with the list of cities
	 */
	private static String sourceFile = "/home/gjardim/Documents/Projects/Projects/SanctuaryPaint/BMVendors/";
	/**
	 * The target output file path
	 */
	private static String targetFile = "/home/gjardim/Documents/Projects/Projects/SanctuaryPaint/BMVendors/";
	/**
	 * An instance of the VendorsManager class, which contains properties and
	 * methods to store the results found.
	 */
	private static VendorsManager manager = new VendorsManager();
	private static WebElement inputBox;
	private static WebElement searchButton;
	private static WebElement radiusField;
	private static WebDriver driver;
	private static String targetCountry = "CANADA";
	/**
	 * This variable stores the cumulative total of results found. Each vendor is
	 * one result.
	 */
	private static int resultsCounter = 0;
	

	public static void main(String[] args) {

		System.setProperty("webdriver.gecko.driver", "/home/gjardim/Programs/Gecko_Driver/geckodriver"); 
		// Gecko driver is the driver for Firefox browser
		String urlCountry = "ca";
		String sourceFileCountry = "CAcities.csv";
		String targetFileCountry = "CA_BMvendors.csv";
		if(targetCountry.equalsIgnoreCase("USA")) {
			urlCountry = "us";
			sourceFileCountry = "UScities.csv";
			targetFileCountry = "US_BMvendors.csv";
		}
		targetFile += targetFileCountry;
		sourceFile += sourceFileCountry;
		
		driver = new FirefoxDriver();
		driver.get("https://www.benjaminmoore.com/en-" + urlCountry + "/store-locator");
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		driver.manage().window().maximize();
		getWebElements();

		List<String[]> citiesList = loadFromCsvFile(sourceFile);

		String location = "";
		for (int index = 0; index < citiesList.size(); index++) {
			String[] city = citiesList.get(index);
			location = city[0] + ", " + city[1] + ", " + getTargetCountry();
			try {
				scrapeVendorsInfo(location);
			} catch (Exception ex) {
				manager.saveToFile(targetFile);
				System.out.println("BM Vendors Scraper ended prematurely.");
				ex.printStackTrace();
			}

		}

		driver.close();

		manager.saveToFile(targetFile);
		System.out.println("Total of records found: " + getCounter());
		System.out.println("Total of records saved to file: " + manager.getVendorsList().size());

	}

	/**
	 * This method finds the respective WebElement and loads them into the class
	 * variables.
	 * 
	 * @param driver - the Selenium WebDriver
	 */
	private static void getWebElements() {
		inputBox = driver.findElement(By.id("retail-search-input-query"));
		searchButton = driver.findElement(By.xpath("//div[contains(@class,'input-group')]//button"));
		radiusField = driver.findElement(By.xpath("//button[@id='radius-single-button']"));

	}

	/**
	 * This method will automatically send location keys to the target field, search
	 * and scrape the results found and save them to the list of Benjamin Moore
	 * Vendors.
	 * 
	 * @param driver   - the Selenium WebDriver
	 * @param location - a String which tells the city, province and country to
	 *                 search for
	 */
	private static void scrapeVendorsInfo(String location) {

		inputBox.click();
		inputBox.sendKeys(location);
		// driver.findElement(By.xpath("//*[@id=\"typeahead-13-7307-option-0\"]")).click();
		radiusField.click();
		driver.findElement(By.xpath("//li/label[contains(text(),'160 KM')]")).click();

		if (searchButton != null)
			searchButton.click();

		try {
			Thread.sleep(2500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

//		boolean thereIsError = true;
//
//		do {
		Actions actions = new Actions(driver);

		List<WebElement> vendorsFromSite = driver.findElements(By.xpath("//div[contains(@class,'detail-container')]"));
		if (vendorsFromSite != null && vendorsFromSite.size() > 0) {
			System.out.println(vendorsFromSite.size() + " records found for " + location);
			try {

				for (WebElement vendor : vendorsFromSite) {

					((JavascriptExecutor) driver)
							.executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'nearest'})", vendor);
					Thread.sleep(500);
					actions.keyDown(Keys.LEFT_CONTROL).click(vendor.findElement(By.xpath(".//a[@class='store-name']")))
							.keyUp(Keys.LEFT_CONTROL).build().perform();
					Thread.sleep(2000);
					List<String> browserTabs = new ArrayList<>(driver.getWindowHandles());
					driver.switchTo().window(browserTabs.get(1));
					Thread.sleep(1000);
					String name = getInnerText("//strong[contains(@data-ng-bind,'storeName')]");
					String address1 = getInnerText("//div[contains(@data-ng-bind,'addressLine1')]");
					String address2 = getInnerText("//div[contains(@data-ng-bind,'addressLine2')]");
					String city = getInnerText("//span[contains(@data-ng-bind,'city')]");
					String province = getInnerText(
							"//div[contains(@class,'detail-container')]//span[contains(@data-ng-bind,'state')]");
					String postalCode = getInnerText(
							"//div[contains(@class,'detail-container')]//span[contains(@data-ng-bind,'zipCode')]");
					String type = getInnerText("//div[@class='pr-label']");
					String phone = getInnerText("//p/a[contains(@data-ng-bind,'phone')]");
//
					System.out.println(name + ", " + type + ", " + phone + ", " + address1 + ", " + address2 + ", "
							+ city + ", " + province + ", " + postalCode);
					BMVendor bmVendor = new BMVendor(name, type, address1, address2, city, province, getTargetCountry(),
							postalCode, phone);

					manager.addToVendorsList(bmVendor);

					driver.close();
					driver.switchTo().window(browserTabs.get(0));

				}

			} catch (StaleElementReferenceException | InterruptedException ex) {

				System.out.println("\n" + ex.getMessage());
				// thereIsError = true;

			}
			incrementCounter(vendorsFromSite.size());
			System.out.println();
		} else {
			System.out.println("No vendors found for " + location);
		}
//				thereIsError = false;
//

//
//		} while (thereIsError);

	}

	private static String getInnerText(String xpath) {
		return driver.findElement(By.xpath(xpath)).getAttribute("innerText").trim();
	}

	/**
	 * This methods reads a csv file and returns a List of String arrays.
	 * 
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