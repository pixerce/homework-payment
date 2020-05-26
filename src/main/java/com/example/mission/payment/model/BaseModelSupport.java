package com.example.mission.payment.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

@JsonIgnoreProperties(
        ignoreUnknown = true
)

@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class BaseModelSupport implements Serializable {

    private static final long serialVersionUID = -2503611548746466300L;

    public BaseModelSupport() {
    }

    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o, new String[0]);
    }

    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, new String[0]);
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
    }
}
