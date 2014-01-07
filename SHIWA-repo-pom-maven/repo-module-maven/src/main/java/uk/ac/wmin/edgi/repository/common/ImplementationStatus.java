package uk.ac.wmin.edgi.repository.common;

import java.util.Arrays;
import java.util.List;

public enum ImplementationStatus {

    NEW("private"),
    READY("private"),
    FAILED("private"),
    PUBLIC("public"),
    OLD("private"),
    DEPRECATED("private"),
    COMPROMISED("private");

    private static List<String> statuses;
    private String friendlyName;

    static{
        statuses = Arrays.asList("public", "private");
    }

    private ImplementationStatus(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getFriendlyName(){
        return friendlyName;
    }

    public static List<String> getPossibleStatuses(){
        return statuses;
    }

}
