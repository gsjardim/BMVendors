package testPackage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import mainPackage.*;

public class TestClass {

	public static void main(String[] args) {
		
		//(String name, String type, String address1, String address2, String city, String province, String country,
		//		String postalCode, String phoneNumber)
		/*String name = "Guilherme";
		String type = "plus";
		String address1 = "14 Angus";
		String address2 = "casa";
		String city = "Kanata";
		String province = "ON";
		String country = "Canada";
		String postalCode = "k2l4e6";
		String phone = "819-576-4949";
		
		VendorsManager manager = new VendorsManager();
		
		BMVendor vendor1 = new BMVendor(name, type, address1, address2, city, province, country, postalCode, phone);
		
		manager.addToVendorsList(vendor1);
		
		BMVendor vendor2 = new BMVendor(name, type, address1, address2, city, province, country, postalCode, phone);
		
		BMVendor vendor3 = new BMVendor("Sanctuary Paint", type, address1, address2, city, province, country, postalCode, phone);
		BMVendor vendor4 = new BMVendor("Sanctuary Paint", type, address1, address2, city, province, country, postalCode, phone);
		BMVendor vendor5 = new BMVendor("Sanctuary Paint", type, address1, address2, city, province, "Brazil", postalCode, phone);
		
		manager.addToVendorsList(vendor2);
		manager.addToVendorsList(vendor3);
		manager.addToVendorsList(vendor4);
		manager.addToVendorsList(vendor5);
		
		System.out.println("The list size is: " + manager.getVendorsList().size());
		
		for(BMVendor v : manager.getVendorsList()) {
			System.out.println(v.toString());
		}
		
		String file = "/home/gjardim/Documents/Projects/SanctuaryPaint/BMVendors/test.csv";
		
		manager.saveToFile(file);*/
		String source = "/home/gjardim/Documents/Projects/SanctuaryPaint/BMVendors/US cities.csv";
		
		try(
		FileReader input = new FileReader(new File(source));
		CSVReader reader = new CSVReader(input)){
			List<String[]> results = new ArrayList<>();
			Iterator<String[]> it = reader.iterator();
			while(it.hasNext()) {
				String [] line = it.next();
				for(int i=0; i < line.length; i++) line[i] = line[i].trim();
				results.add(line);
			}
			results.forEach(result -> {
				System.out.println(Arrays.toString(result));
			});
		
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
