package com.caju.transactionauthorizer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicationTest {

	@Test
	@DisplayName("Test Application Context Loading")
	void contextLoads() {
		// Ensure that the application context loads without errors
		Assertions.assertTrue(true);
	}

}
