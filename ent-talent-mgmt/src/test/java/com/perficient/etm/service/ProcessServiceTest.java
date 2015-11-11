package com.perficient.etm.service;

import javax.inject.Inject;

import org.junit.Test;

import com.perficient.etm.utils.SpringAppTest;

public class ProcessServiceTest extends SpringAppTest {
	@Inject 
	ProcessService processSvc;
	
	@Test
	public void basicSetup() {
//		System.out.println("This is a test. " + processSvc.initiateProcess(ReviewTyp));
	}
}
