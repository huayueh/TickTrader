package ticktrader.provider;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ticktrader.dto.Settle;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Author: huayueh
 * Date: 2015/4/16
 */
public class SettleContractProvider implements ContractProvider {
    private static final Logger logger = LoggerFactory.getLogger(SettleContractProvider.class);
    private static SettleContractProvider instance = null; // Lazy initialization
    protected final ConcurrentNavigableMap<LocalDate, Settle> his = new ConcurrentSkipListMap<>(); // Reverted to protected final

    // Private constructor for default "settle.csv" loading
    private SettleContractProvider() {
        this(SettleContractProvider.class.getClassLoader().getResourceAsStream("settle.csv"));
        // Note: The original code had a logger.info here after loading.
        // It's now in loadData or could be added here if specific to default construction.
    }

    // Public constructor for InputStream-based loading (primarily for testing)
    public SettleContractProvider(InputStream inputStream) {
        loadData(inputStream);
    }

    private void loadData(InputStream is) {
        if (is == null) {
            logger.error("InputStream for SettleContractProvider is null. Settle data will not be loaded.");
            return;
        }
        // Clear map in case this method is ever called on an existing instance, though not typical for constructors.
        // For the new design, 'his' is final and initialized empty, so clear() isn't strictly needed here if only called from constructor.
        // However, if an instance could theoretically reload data, it would be. Given it's called from constructor, it's on a fresh map.
        // this.his.clear();

        Scanner scan;
        String line;
        try {
            scan = new Scanner(is);
            while (scan.hasNextLine()) { // Changed from scan.hasNext() to ensure full line reading
                line = scan.nextLine();
                String[] ary = StringUtils.split(line, ",");
                if (ary.length == 3) {
                    String dateStr = ary[0].trim();
                    if (dateStr.equalsIgnoreCase("Date")) { // Skip header row
                        continue;
                    }
                    String contract = ary[1].trim();
                    String price = ary[2].trim();
                    LocalDate ldate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy/M/d"));
                    if (!contract.contains("W")) { // remove weekly contract
                        Settle settle = new Settle(ldate, contract, NumberUtils.toDouble(price));
                        his.put(ldate, settle);
                        // Consider moving logger.debug("{}", settle) here if more granular logging is needed.
                    }
                }
            }
            logger.info("SettleContractProvider data loaded."); // General success message
        } catch (Exception ex) {
            logger.error("Error loading settle data from InputStream", ex);
        } finally {
            try {
                if (is != null) is.close();
            } catch (java.io.IOException e) {
                logger.error("Failed to close input stream for settle data", e);
            }
        }
    }

    public static SettleContractProvider getInstance() {
        if (instance == null) {
            synchronized (SettleContractProvider.class) {
                if (instance == null) {
                    instance = new SettleContractProvider(); // Uses private constructor for default CSV
                }
            }
        }
        return instance;
    }

    // New static factory method for testing, returns a new instance each time
    public static SettleContractProvider getInstanceForTest(InputStream is) {
        return new SettleContractProvider(is);
    }

    public LocalDate closestDate(LocalDate day){
        return his.ceilingKey(day);
    }

    @Override
    public String closestContract(LocalDate time) {
        LocalDate key = closestDate(time);
        if (key.equals(time)){
            key = closestDate(time.plusDays(1));
        }
        Settle settle = his.get(key);
        return settle.getContract();
    }

    public String exactlyContractDay(LocalDate time) {
        Settle settle = his.get(time);
        if (settle == null) {
            return null;
        }
        return settle.getContract();
    }
}
