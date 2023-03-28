package com.driver.services.impl;

import com.driver.model.*;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository2;

	@Autowired
	DriverRepository driverRepository2;

	@Autowired
	TripBookingRepository tripBookingRepository2;

	@Override
	public void register(Customer customer) {
		//Save the customer in database
		customerRepository2.save(customer);
	}

	@Override
	public void deleteCustomer(Integer customerId) {
		// Delete customer without using deleteById function
		Customer customer = customerRepository2.findById(customerId).get();
		List<TripBooking>bookedTrips = customer.getTripBookingList();//all trips of that customer
		//iterate to all trips & find driver-->cab.cab as available .trip status canceled

		for(TripBooking trip : bookedTrips){
			Driver driver = trip.getDriver();
			Cab cab = driver.getCab();
			cab.setAvailable(true);
			driverRepository2.save(driver);
			trip.setStatus(TripStatus.CANCELED);
		}
		customerRepository2.delete(customer);

	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query
      List<Driver>driverList = driverRepository2.findAll(); //find all drivers
   Driver driver =null;
   for (Driver currDriver : driverList){
	if (currDriver.getCab().getAvailable()){ //if current drivers cab is available
		if(driver ==null || currDriver.getDriverId()<driver.getDriverId()){
			//if that current drivers Id is lower than drivers Id
			driver = currDriver;
		}
	}
}
if (driver==null)throw new Exception("No cab available!");
TripBooking newTripbooking = new TripBooking();
newTripbooking.setCustomer(customerRepository2.findById(customerId).get());//assign given customer  id set for new tripbooking
newTripbooking.setFromLocation(fromLocation); //set location for transaction,distanceinKm
newTripbooking.setToLocation(toLocation);
newTripbooking.setDistanceInKm(distanceInKm);
newTripbooking.setStatus(TripStatus.CONFIRMED);
newTripbooking.setDriver(driver);
int rate = driver.getCab().getPerKmRate();
newTripbooking.setBill(distanceInKm*rate);

driver.getCab().setAvailable(false);
driverRepository2.save(driver);

Customer customer = customerRepository2.findById(customerId).get();
customer.getTripBookingList().add(newTripbooking);
customerRepository2.save(customer);
tripBookingRepository2.save(newTripbooking);

return newTripbooking;
	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly

TripBooking tripbooked = tripBookingRepository2.findById(tripId).get();
tripbooked.setStatus(TripStatus.CANCELED);
tripbooked.setBill(0);
tripbooked.getDriver().getCab().setAvailable(true);
tripBookingRepository2.save(tripbooked);
	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
TripBooking tripBooked = tripBookingRepository2.findById(tripId).get();
tripBooked.setStatus(TripStatus.COMPLETED);
tripBooked.getDriver().getCab().setAvailable(true);
tripBookingRepository2.save(tripBooked);
	}
}
