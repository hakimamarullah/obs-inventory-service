package com.sg.obs.models.converter;

import com.sg.obs.enums.InventoryType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class InventoryTypeConverter implements AttributeConverter<InventoryType, String> {

    @Override
    public String convertToDatabaseColumn(InventoryType attribute) {
        return attribute == null ? null : attribute.name(); // 'T' or 'W'
    }

    @Override
    public InventoryType convertToEntityAttribute(String dbData) {
        return dbData == null ? null : InventoryType.valueOf(dbData);
    }
}

