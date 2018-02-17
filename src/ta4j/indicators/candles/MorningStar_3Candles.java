package ta4j.indicators.candles;

import javax.servlet.http.HttpSession;

import ta4j.Decimal;
import ta4j.Tick;
import ta4j.TimeSeries;
import ta4j.indicators.CachedIndicator;

/**
 * Bullish The Morning Star indicator.
 * <p>
 * 
 * @see http://www.investopedia.com/terms/b/bullishengulfingpattern.asp
 */
public class MorningStar_3Candles extends CachedIndicator<Boolean> {

	private final TimeSeries series;
	private final int tickCountFrom;
	private final int tickCountTo;
	private final Decimal dojiNotBodyFactor;
	private final Decimal dojiBodyFactor;

	/**
	 * Constructor.
	 * 
	 * @param series
	 *            a time series
	 * @param tickCount
	 *            the tick count for down trend
	 * @param DojiNotBodyFactor
	 *            represent the Av Abs Rral body factor
	 * @param dojiBodyfactor
	 *            represent the Av Abs real body factor of p2 i.e. doji
	 */
	public MorningStar_3Candles(TimeSeries series, int tickCountFrom, int tickCountTo, Decimal dojiNotBodyFactor, Decimal dojiBodyFactor) {
		super(series);
		this.series = series;
		this.tickCountFrom = tickCountFrom;
		this.tickCountTo = tickCountTo;
		this.dojiNotBodyFactor = dojiNotBodyFactor;
		this.dojiBodyFactor = dojiBodyFactor;
	}

	@Override
	protected Boolean calculate(int index) {

		DojiIndicator dojiNotInd = new DojiIndicator(series, 5, dojiNotBodyFactor);
		DojiIndicator dojiInd = new DojiIndicator(series, 5, dojiBodyFactor);
		DownTrendIndicator downtrend = new DownTrendIndicator(series, tickCountFrom, tickCountTo, 1); // I
																						// have
																						// to
																						// correct
																						// it
																						// the
																						// ofSetIndex
																						// value
																						// with
																						// brother

		// Condition2(index) or condition2(index-1) correct it with brother

		boolean condition1, condition2, condition3, condition4, condition5, satisfied;

		if (index < 2) {
			// Morning Star is a 3-candle pattern or 4-candle pattern
			return false;
		}
		Tick p1Tick = series.getTick(index - 2);
		Tick p2Tick = series.getTick(index - 1);
		Tick p3Tick = series.getTick(index);

		if (p1Tick.isBearish() && p3Tick.isBullish()) {

			final Decimal p1OpenPrice = p1Tick.getOpenPrice();
			final Decimal p1ClosePrice = p1Tick.getClosePrice();
			final Decimal p2OpenPrice = p2Tick.getOpenPrice();
			final Decimal p2ClosePrice = p2Tick.getClosePrice();
			final Decimal p3OpenPrice = p3Tick.getOpenPrice();
			final Decimal p3ClosePrice = p3Tick.getClosePrice();

			//0.996 = 99.6 %
			condition1 = p2OpenPrice.isLessThanOrEqual(p1OpenPrice) && p2ClosePrice.isLessThanOrEqual(p1OpenPrice)
					&& (p3OpenPrice.isGreaterThanOrEqual(p2ClosePrice.multipliedBy(Decimal.valueOf(0.996)))
							|| p3OpenPrice.isGreaterThanOrEqual(p2OpenPrice.multipliedBy(Decimal.valueOf(0.996))));

			// && p3ClosePrice.isGreaterThan(p1OpenPrice);

			condition2 = downtrend.calculate(index);
			condition3 = !dojiNotInd.calculate(index - 2); //p1
			condition4 = dojiInd.getValue(index - 1); //p2
			condition5 = !dojiNotInd.calculate(index); //p3

			satisfied = condition1 & condition2 & condition3 & condition4 & condition5;

			if (condition2) {
			/*	System.out.print("\nTrue_Index: " + index + ", Date: " + series.getTick(index).getDateName());
				System.out.println(";  p1_index :" + (index - 2) + ";   p1 Date: " + p1Tick.getDateName() +";   Satisfied :" + satisfied);
				System.out.println("MoStr(3 candle) condition :" + condition1 + ";   downTrend : " + condition2
						+ ";   p1_Not_Doji: " + condition3 + ";   p2doji: " + condition4 + "\n");
			*/}

			if (satisfied) {
				p3Tick.setSatisfiedIndicator("BuMoStr_3c");
				System.out.println("\nIndex : " + index + " Satified Indicator: BullishMorningStarIndicator(3 candle)");
				// System.out.print("; Date: " +
				// series.getTick(index).getDateName());
				// System.out.println("p1 Date" + p1Tick.getDateName());
				// System.out.println("con1:" + condition1 + "; DownTrnd:" +
				// condition2 + ";!p1Doji: "
				// + p1dojiInd.getValue(index) + "; " + condition3 + ";
				// p2doji: " + condition4 +
				// p2dojiInd.getTimeSeries().getTickCount());
			}

			return satisfied;
		}

		return false;
	}
}
