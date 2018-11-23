package ticktrader.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ticktrader.dto.Tick;
import ticktrader.recorder.PrintTickRecorder;
import ticktrader.service.FutureTickService;
import ticktrader.service.TickService;
import ticktrader.strategy.RecordStrategy;
import ticktrader.strategy.Strategy;

public class FutureTickLogService extends FutureTickService {
	private static final Logger ticklogger = LoggerFactory.getLogger("ticklog");

	public FutureTickLogService(String baseFolder, int year, Strategy ob) {
		super(baseFolder, year, ob);
	}

	@Override
	public void onTick(Tick tick) {
		ticklogger.info("{}", tick);
	}

	public static void main(String arg[]){
		Strategy strategy = new RecordStrategy(new PrintTickRecorder());
		TickService tickService = new FutureTickLogService("/Users/harvey/Documents/Tick/future/", 2011, strategy);
		Thread mkt = new Thread(tickService);
		mkt.setName("TickService");
		mkt.start();
	}
}
