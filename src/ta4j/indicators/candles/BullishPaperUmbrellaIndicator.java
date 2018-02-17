package ta4j.indicators.candles;

import javax.servlet.http.HttpSession;

import ta4j.Decimal;
import ta4j.Tick;
import ta4j.TimeSeries;
import ta4j.indicators.CachedIndicator;

/**
 * Bullish Paper Umbrella pattern indicator.
 * <p>
 * 
 * @see http://www.investopedia.com/terms/b/bullishengulfingpattern.asp
 */
public class BullishPaperUmbrellaIndicator extends CachedIndicator<Boolean> {

	private final TimeSeries series;
	private final int tickCountFrom;
	private final int tickCountTo;
	private final Decimal bodyFactor;

	/**
	 * Constructor.
	 * 
	 * @param series
	 *            a time series
	 * @param tickCount
	 *            tick count for down Trend
	 * @param bodyFactor
	 *            is the body factor of spinning top; Umbrella body should be >
	 *            bodyFactor of Av bodies of 5 candels
	 */
	public BullishPaperUmbrellaIndicator(TimeSeries series, int tickCountFrom, int tickCountTo, double bodyFactor) {
		super(series);
		this.series = series;
		this.tickCountFrom = tickCountFrom;
		this.tickCountTo = tickCountTo;
		this.bodyFactor = Decimal.valueOf(bodyFactor);
	}

	@Override
	protected Boolean calculate(int index) {

		DojiIndicator dojiInd = new DojiIndicator(series, 5, bodyFactor); // 20%
																							// is
																							// the
																							// real
																							// absolute
																							// body
																							// of
																							// Average
																							// Real
																							// Body
		DownTrendIndicator downtrend = new DownTrendIndicator(series, tickCountFrom, tickCountTo, 1);

		boolean condition1, condition2, condition3, condition4, satisfied;
		if (index < 1) {
			// bullish Paper Umbrella is a 1-candle pattern
			return false;
		}
		Tick prevTick = series.getTick(index - 1);
		Tick currTick = series.getTick(index);
		if (prevTick.isBearish() && currTick.isBullish()) {
			// final Decimal prevOpenPrice = prevTick.getOpenPrice();
			// final Decimal prevClosePrice = prevTick.getClosePrice();
			final Decimal currOpenPrice = currTick.getOpenPrice();
			final Decimal currClosePrice = currTick.getClosePrice();
			final Decimal currMaxPrice = currTick.getMaxPrice();
			final Decimal currMinPrice = currTick.getMinPrice();
			
			final Decimal upperShadow = currMaxPrice.minus(currClosePrice);
			final Decimal realBody = currClosePrice.minus(currOpenPrice);
			final Decimal lowerShadow = currOpenPrice.minus(currMinPrice);
			
			condition1 = (lowerShadow.dividedBy(realBody)).isGreaterThanOrEqual(Decimal.valueOf(2));
			// currOpenPrice.isLessThan(prevOpenPrice) &&
			// currOpenPrice.isLessThan(prevClosePrice)
			// && currClosePrice.isGreaterThan(prevOpenPrice) &&
			// currClosePrice.isGreaterThan(prevClosePrice);

			condition2 = downtrend.calculate(index);
			condition3 = !dojiInd.calculate(index);
			condition4 = (upperShadow.dividedBy(realBody)).isLessThanOrEqual(Decimal.valueOf(1));
 
			satisfied = condition1 && condition2 && condition3 && condition4;

			if (satisfied) {
				currTick.setSatisfiedIndicator("BuPap");
				System.out.println("Index : " + index + " Satified Indicator: BullishPaperUmbrellaIndicator");
				/*
				  System.out.println("Index: " +index+ " O-L: "+ currOpenPrice.minus(currMinPrice) + "; C-O: "+currClosePrice.minus(currOpenPrice)) ;
				  System.out.print("; Down Trend : " + condition2);
				  System.out.println("; Umbrella body is not spinning top:" +
				  condition3);
				 */}

			return satisfied;
		}
		return false;
	}
}
