package de.thmshmm.kafka;

import java.security.Principal;

/**
 * Created by Thomas Hamm on 21.07.17.
 */
public class CommonNamePrincipal implements Principal {
    private static final String SEPARATOR = ":";

    private String principalType;
    private String name;

    public CommonNamePrincipal(String principalType, String name) {
        this.principalType = principalType;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommonNamePrincipal)) return false;

        CommonNamePrincipal that = (CommonNamePrincipal) o;

        if (!principalType.equals(that.principalType)) return false;

        return name.equals(that.name);
    }

    @Override
    public String toString() {
        return principalType + SEPARATOR + name;
    }

    @Override
    public int hashCode() {
        int result = principalType.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    public String getPrincipalType() {
        return principalType;
    }

    public String getName() { return name; }
}
