package com.icbt.model;

import org.junit.Test;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BillingSummaryRowTest {
    @Test
    public void exposesEveryJspFieldAsAReadableJavaBeanProperty() throws Exception {
        BillingSummaryRow row = new BillingSummaryRow(
                "Dental Cleaning", 3L, new BigDecimal("5400.00"));
        Map<String, PropertyDescriptor> properties = Arrays.stream(Introspector
                .getBeanInfo(BillingSummaryRow.class)
                .getPropertyDescriptors())
                .collect(Collectors.toMap(PropertyDescriptor::getName, descriptor -> descriptor));

        assertReadableValue(properties, "treatmentName", row, "Dental Cleaning");
        assertReadableValue(properties, "billCount", row, 3L);
        assertReadableValue(properties, "totalAmount", row, new BigDecimal("5400.00"));
    }

    private void assertReadableValue(Map<String, PropertyDescriptor> properties,
                                     String propertyName,
                                     BillingSummaryRow row,
                                     Object expected) throws Exception {
        PropertyDescriptor descriptor = properties.get(propertyName);
        assertNotNull("Missing JavaBean property: " + propertyName, descriptor);
        assertNotNull("Missing JavaBean getter: " + propertyName, descriptor.getReadMethod());
        assertEquals(expected, descriptor.getReadMethod().invoke(row));
    }
}
