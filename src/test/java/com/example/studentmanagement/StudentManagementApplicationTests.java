package com.example.studentmanagement;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "app.storage.file-path=target/test-data/students.json")
class StudentManagementApplicationTests {

	@Test
	void contextLoads() {
	}

}
