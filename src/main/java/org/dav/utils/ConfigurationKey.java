package org.dav.utils;

public final class ConfigurationKey {

    public static final String CAROUSEL_KEY = "UI_CAROUSEL";
    public static final String LIBRARY_ISSUE_CONFIG = "LIBRARY_ISSUE_CONFIG";
    public static final String SERVICE_ACCOUNT_CONFIG = "SERVICE_ACCOUNT_CONFIG";
    public static final String ROLE_MEMBER = "ROLE_member";
    public static final String ROLE_LIBRARIAN = "ROLE_librarian";

    public ConfigurationKey(){
        throw new RuntimeException("This class can not be instantiated");
    }
}
