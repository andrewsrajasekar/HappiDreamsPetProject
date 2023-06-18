package com.happidreampets.app.database.utils;
import com.happidreampets.app.database.model.Product.WEIGHT_UNITS;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class WeightUnitConverter implements AttributeConverter<WEIGHT_UNITS, Integer> {

    @Override
    public Integer convertToDatabaseColumn(WEIGHT_UNITS weightUnit) {
        if(weightUnit == null){
            return null;
        }
        return weightUnit.getUnit();
    }

    @Override
    public WEIGHT_UNITS convertToEntityAttribute(Integer unit) {
        if(unit == null){
            return null;
        }
        for (WEIGHT_UNITS weightUnit : WEIGHT_UNITS.values()) {
            if (weightUnit.getUnit().equals(unit)) {
                return weightUnit;
            }
        }
        throw new IllegalArgumentException("Unknown unit: " + unit);
    }
}
