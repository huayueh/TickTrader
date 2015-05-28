package ticktrader;

import junit.framework.Assert;
import org.junit.Test;
import ticktrader.dto.Tick;
import ticktrader.service.FuturePriceFinder;

import java.net.URI;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Author: huayueh
 * Date: 2015/5/28
 */
public class FuturePriceFinderTest {

    @Test
    public void testSpecifyDay() {
        try {
            URI uriOpen = getClass().getResource("/2012_open_tick.csv").toURI();
            URI uriClose = getClass().getResource("/2012_last_tick.csv").toURI();
            FuturePriceFinder futurePriceFinder = new FuturePriceFinder(Paths.get(uriOpen), Paths.get(uriClose));
            Optional<Tick> openTick = futurePriceFinder.find(LocalDate.of(2012,7,19), "TX", FuturePriceFinder.Type.OPEN);
            Assert.assertTrue(openTick.isPresent());
        } catch (Exception e){
            Assert.fail("Exception:" + e.getMessage());
        }
    }
}
