package ticktrader.provider;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Before;
import org.junit.Test;
import ticktrader.dto.Settle;

import java.io.InputStream;
import java.io.ByteArrayInputStream; // New import
import java.nio.charset.StandardCharsets; // New import
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
// Scanner will not be needed in setUp anymore, but might be used by SettleContractProvider if it reads other files.
// Keep for now, or remove if SettleContractProvider's only scanner usage was for settle.csv
// import java.util.Scanner;
import java.util.concurrent.ConcurrentNavigableMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SettleContractProviderTest {

    private SettleContractProvider settleContractProvider;

    // Define test CSV data as a string
    private final String testCsvData =
            "Date,contract,price\n" +
            "2023/10/20,TX202310,16500\n" +
            "2023/11/17,TX202311,16800\n" +
            "2023/12/15,TX202312,17000\n" +
            "2024/01/19,TX202401,17200\n" +
            "2024/02/16,TX202402,17500\n" +
            "# Weekly contracts to be ignored by the provider (comment line)\n" +
            "2023/10/25,TX202310W4,16600\n" + // This line will be processed, W will be filtered
            "2023/11/01,TX202311W1,16700";   // This line will be processed, W will be filtered

    @Before
    public void setUp() { // Removed "throws Exception" as direct stream creation is less prone to checked exceptions here
        InputStream testStream = new ByteArrayInputStream(testCsvData.getBytes(StandardCharsets.UTF_8));
        // Use the new static factory method for testing
        this.settleContractProvider = SettleContractProvider.getInstanceForTest(testStream);
    }

    @Test
    public void testClosestDate() {
        // Test case 1: Date before the first settlement date
        LocalDate date1 = LocalDate.of(2023, 10, 15);
        LocalDate expected1 = LocalDate.of(2023, 10, 20);
        assertEquals(expected1, settleContractProvider.closestDate(date1));

        // Test case 2: Date exactly on a settlement date
        LocalDate date2 = LocalDate.of(2023, 11, 17);
        LocalDate expected2 = LocalDate.of(2023, 11, 17);
        assertEquals(expected2, settleContractProvider.closestDate(date2));

        // Test case 3: Date between two settlement dates
        LocalDate date3 = LocalDate.of(2023, 12, 1);
        LocalDate expected3 = LocalDate.of(2023, 12, 15);
        assertEquals(expected3, settleContractProvider.closestDate(date3));

        // Test case 4: Date after the last settlement date
        LocalDate date4 = LocalDate.of(2024, 3, 1);
        assertNull(settleContractProvider.closestDate(date4)); // Expect null as no ceiling key
    }

    @Test
    public void testClosestContract() {
        // Test case 1: Date before the first settlement date
        LocalDate date1 = LocalDate.of(2023, 10, 15);
        String expectedContract1 = "TX202310"; // Closest is 2023/10/20
        assertEquals(expectedContract1, settleContractProvider.closestContract(date1));

        // Test case 2: Date exactly on a settlement date (should give next contract)
        LocalDate date2 = LocalDate.of(2023, 10, 20);
        String expectedContract2 = "TX202311"; // Next contract is 2023/11/17
        assertEquals(expectedContract2, settleContractProvider.closestContract(date2));

        // Test case 3: Date between two settlement dates
        LocalDate date3 = LocalDate.of(2023, 12, 1);
        String expectedContract3 = "TX202312"; // Closest is 2023/12/15
        assertEquals(expectedContract3, settleContractProvider.closestContract(date3));

        // Test case 4: Date just before the last settlement date
        LocalDate date4 = LocalDate.of(2024, 2, 15);
        String expectedContract4 = "TX202402"; // Closest is 2024/02/16
        assertEquals(expectedContract4, settleContractProvider.closestContract(date4));

        // Test case 5: Date exactly on the second to last settlement date (should give last contract)
        LocalDate date5 = LocalDate.of(2024, 1, 19); // Corrected month from 01 to 1
        String expectedContract5 = "TX202402"; // Next contract is 2024/02/16
        assertEquals(expectedContract5, settleContractProvider.closestContract(date5));
    }

    @Test(expected = NullPointerException.class)
    public void testClosestContract_AfterLast() {
        // Test case: Date after the last settlement date.
        // Based on current SettleContractProvider, if closestDate returns null,
        // it would lead to NullPointerException when calling his.get(key).
        LocalDate date = LocalDate.of(2024, 3, 1);
        settleContractProvider.closestContract(date); // This should throw NPE
    }

    @Test
    public void testExactlyContractDay() {
        // Test case 1: Date exactly on a settlement date
        // Use a LocalDate instance that would have been created by the parser during setUp
        // to ensure maximum consistency, though LocalDate.of() should be equivalent.
        LocalDate date1 = LocalDate.parse("2023/10/20", DateTimeFormatter.ofPattern("yyyy/M/d"));
        String expectedContract1 = "TX202310";
        assertEquals(expectedContract1, settleContractProvider.exactlyContractDay(date1));

        // Test case 2: Date not on a settlement date
        LocalDate date2 = LocalDate.of(2023, 10, 21);
        assertNull(settleContractProvider.exactlyContractDay(date2)); // Expect null as no exact match

        // Test case 3: Another exact match
        LocalDate date3 = LocalDate.of(2024, 2, 16);
        String expectedContract3 = "TX202402";
        assertEquals(expectedContract3, settleContractProvider.exactlyContractDay(date3));
    }

    @Test
    public void testWeeklyContractsIgnored() throws NoSuchFieldException, IllegalAccessException {
        // Access the 'his' map via reflection to check its contents
        Field hisField = SettleContractProvider.class.getDeclaredField("his");
        hisField.setAccessible(true);
        @SuppressWarnings("unchecked")
        ConcurrentNavigableMap<LocalDate, Settle> hisMap = (ConcurrentNavigableMap<LocalDate, Settle>) hisField.get(this.settleContractProvider);

        // Check that weekly contracts are not loaded
        LocalDate weeklyDate1 = LocalDate.parse("2023/10/25", DateTimeFormatter.ofPattern("yyyy/M/d"));
        LocalDate weeklyDate2 = LocalDate.parse("2023/11/01", DateTimeFormatter.ofPattern("yyyy/M/d"));

        assertNull("Weekly contract TX202310W4 should not be loaded", hisMap.get(weeklyDate1));
        assertNull("Weekly contract TX202311W1 should not be loaded", hisMap.get(weeklyDate2));
        assertEquals("Map size should reflect only non-weekly contracts from test settle.csv", 5, hisMap.size());
    }
}
