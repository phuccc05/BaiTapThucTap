package org.example.baitapthuctap;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.junit.jupiter.api.BeforeEach;

@SpringBootTest
@ActiveProfiles("test")
@SpringJUnitConfig
public abstract class BaseTestClass {
    @BeforeEach
    public void setUp() {
        // Setup common test data hoặc mock configurations
        setupTestData();
    }

    /**
     * Override method này trong các test class con để setup test data
     */
    protected void setupTestData() {
        // Default implementation - có thể override
    }
}
