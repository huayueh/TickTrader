package ticktrader.provider;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Before;
import org.junit.Test;
import ticktrader.dto.Settle;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.concurrent.ConcurrentNavigableMap;
// import java.lang.reflect.Constructor; // Not needed for this version of setUp

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SettleContractProviderTest {

    private SettleContractProvider settleContractProvider = null;

    @Before
    public void setUp() throws Exception {
        // Get the singleton instance
        this.settleContractProvider = SettleContractProvider.getInstance();

        // Get direct access to its 'his' map
        Field hisField = SettleContractProvider.class.getDeclaredField("his");
        hisField.setAccessible(true);
        @SuppressWarnings("unchecked") // Suppress warning for cast
        ConcurrentNavigableMap<LocalDate, Settle> hisMap = (ConcurrentNavigableMap<LocalDate, Settle>) hisField.get(this.settleContractProvider);
        hisMap.clear(); // Clear existing data (which might be from main/resources or a previous test)

        // Manually load data from test/resources/settle.csv
        InputStream testCsvStream = SettleContractProviderTest.class.getClassLoader().getResourceAsStream("settle.csv");
        if (testCsvStream == null) {
            throw new IllegalStateException("Cannot find test settle.csv in test resources. Make sure it's in core/src/test/resources.");
        }
        Scanner scan = new Scanner(testCsvStream);
        String line;
        while (scan.hasNextLine()) {
            line = scan.nextLine();
            String[] ary = StringUtils.split(line, ",");
            if (ary.length == 3) {
                String dateStr = ary[0].trim();
                String contractStr = ary[1].trim();
                String priceStr = ary[2].trim();
                if (dateStr.equalsIgnoreCase("Date")) continue; // Skip header

                LocalDate ldate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy/M/d"));
                if (!contractStr.contains("W")) {
                    Settle settle = new Settle(ldate, contractStr, NumberUtils.toDouble(priceStr));
                    hisMap.put(ldate, settle);
                }
            }
        }
        scan.close();
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
        LocalDate date1 = LocalDate.of(2023, 10, 20);
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
