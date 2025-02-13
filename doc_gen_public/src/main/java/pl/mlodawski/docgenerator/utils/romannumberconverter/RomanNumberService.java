package pl.mlodawski.docgenerator.utils.romannumberconverter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.mlodawski.docgenerator.pluginsystem.core.registry.ServiceOperation;
import pl.mlodawski.docgenerator.utils.romannumberconverter.exception.RomanNumberException;

import java.util.TreeMap;

/*
 @Author Michał Młodawski
 */
/**
 * RomanNumberService provides functionality for converting integer numbers
 * to their corresponding Roman numeral representations.
 *
 * This class utilizes a TreeMap to map integer values to their Roman numeral
 * representations. The conversion process iteratively determines the largest
 * Roman numeral value less than or equal to the input number, appending its
 * corresponding symbol to the resultant string until the number is fully
 * converted.
 *
 * Key Features:
 * - Supports conversion of positive integers to Roman numerals.
 * - Logs a warning and throws a custom RomanNumberException for invalid inputs,
 *   such as numbers less than or equal to zero.
 * - Implements the IRomanNumber interface, ensuring method compatibility.
 *
 * Exceptions:
 * - Throws RomanNumberException if the input number is less than or equal to zero.
 *
 * Dependencies:
 * - Uses Lombok's @Slf4j for logging.
 * - Marked as a Spring service with the @Service annotation.
 * - Relies on TreeMap for ordered storage and retrieval of Roman numeral mappings.
 */
@Service
@Slf4j
public class RomanNumberService implements IRomanNumber {

    private final TreeMap<Integer, String> romanNumerals = new TreeMap<>() {{
        this.put(1000, "M");
        this.put(900, "CM");
        this.put(500, "D");
        this.put(400, "CD");
        this.put(100, "C");
        this.put(90, "XC");
        this.put(50, "L");
        this.put(40, "XL");
        this.put(10, "X");
        this.put(9, "IX");
        this.put(5, "V");
        this.put(4, "IV");
        this.put(1, "I");
    }};


    @ServiceOperation("RomanNumberService.toRoman")
    public final String toRoman(final Integer number) {
        if (number > 0) {
            final Integer floorKey = this.romanNumerals.floorKey(number);
            if (number.equals(floorKey)) {
                return this.romanNumerals.get(number);
            }
            return this.romanNumerals.get(floorKey) + this.toRoman(number - floorKey);
        }
        RomanNumberService.log.warn("Number to convert must be greater or equal from zero");
        throw new RomanNumberException("Number to convert must be greater or equal from zero");
    }

}
