package com.driver.controllers;

import com.driver.services.DriverService;
import com.driver.services.impl.DriverServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/driver")
public class DriverController {
	@Autowired
	DriverService driverService;
	//register driver
	@PostMapping(value = "/register")
	public ResponseEntity<Void> registerDriver(@RequestParam String mobile, @RequestParam String password){
		driverService.register(mobile,password);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	//delete driver
	@DeleteMapping(value = "/delete")
	public void deleteDriver(@RequestParam Integer driverId){
		driverService.removeDriver(driverId);
	}
//update status of cab available or unavailable
	@PutMapping("/status")
	public void updateStatus(@RequestParam Integer driverId){
	driverService.updateStatus(driverId);
	}
}
