package ch.alni.mcp.fred.service;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for date range constraints.
 */
public class DateRangeValidator implements ConstraintValidator<ValidDateRange, DateRange> {

    @Override
    public boolean isValid(DateRange value, ConstraintValidatorContext context) {
        if (value != null && value.start() != null && value.end() != null) {
            return !value.start().isAfter(value.end());
        }
        else {
            return true;
        }
    }
}
