package com.example.studentmanagement.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Single source of truth for academic majors offered in the application.
 */
public enum Major {
    COMPUTER_SCIENCE("Computer Science"),
    SOFTWARE_ENGINEERING("Software Engineering"),
    INFORMATION_SYSTEM("Information System"),
    CYBER_SECURITY("Cyber Security");

    private final String displayName;

    Major(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Retrieve all available major display names.
     */
    public static List<String> allDisplayNames() {
        return Arrays.stream(values())
                .map(Major::getDisplayName)
                .collect(Collectors.toList());
    }

    /**
     * Check if a given string matches any defined major display name.
     */
    public static boolean isValid(String major) {
        if (major == null || major.isBlank()) {
            return false;
        }
        return Arrays.stream(values())
                .anyMatch(m -> m.getDisplayName().equalsIgnoreCase(major.trim()));
    }
}
