package com.GJardim.BMVendors;
/**
 * This class is responsible to store a list of {@link BMVendor} objects
 */
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.opencsv.CSVWriter;


public class VendorsManager {
	
	private List<BMVendor> vendorsList;
	
	public VendorsManager() {
		vendorsList = new ArrayList<>();
	}
	
	public List<BMVendor> getVendorsList() {
		return this.vendorsList;
	}
	
	/**
	 * This method adds the new BMVendor to the list only if it is not a duplicate
	 * @param vendor - {@link BMVendor}
	 * @return a list of BMVendor
	 */
	public List<BMVendor> addToVendorsList(BMVendor vendor) {
		boolean exists = false;
		for (BMVendor v : getVendorsList()) {
			if(v.equals(vendor)) {
				exists = true;
				break;
			}
		}
		if(!exists) getVendorsList().add(vendor);
		
		return getVendorsList();
	}
	
	/**
	 * This method saves the content of the list of vendors to the csv file
	 * @param targetFile - String - path to the target file
	 */
	public void saveToFile(String targetFile) {
		
		try(FileWriter output = new FileWriter(new File(targetFile));
				CSVWriter writer = new CSVWriter(output))
		{
			String[] header = {"Name", "Type", "Phone", "Address 1", "Address 2", "City", "Province/State", "Country", 
					"Postal Code" };
			writer.writeNext(header);
			
			Consumer<? super BMVendor> action = vendor -> {
				String[] data = {
						vendor.getName(),
						vendor.getType(),
						vendor.getPhoneNumber(),
						vendor.getAddress1(),
						vendor.getAddress2(),
						vendor.getCity(),
						vendor.getProvince(),
						vendor.getCountry(),
						vendor.getPostalCode()
						
				};
				writer.writeNext(data);
			};
			getVendorsList().forEach(action);
			
		}
		catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	

}
