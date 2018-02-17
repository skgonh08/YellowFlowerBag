package ta4j.indicators.candles;

import javax.servlet.http.HttpSession;

import ta4j.Decimal;
import ta4j.Indicator;
import ta4j.Tick;
import ta4j.TimeSeries;
import ta4j.indicators.CachedIndicator;
import ta4j.indicators.simple.AbsoluteIndicator;

/**
 * Bullish The Morning Star indicator.
 * <p>
 * 
 * @see http://www.investopedia.com/terms/b/bullishengulfingpattern.asp
 */
public class MorninsStar_4Candle_p2Doji extends CachedIndicator<Boolean> {

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
	public MorninsStar_4Candle_p2Doji(TimeSeries series, int tickCountFrom, int tickCountTo, Decimal dojiNotBodyFactor, Decimal dojiBodyFactor) {
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
		DojiIndicator dojiInd = new DojiIndicator(series, 10, dojiBodyFactor);
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

		boolean condition1 = false, condition2, condition3, condition4, condition5, condition6, satisfied;
		Decimal p2AbsRealBody, p0AbsRealBody, p2TOp0BodyRatio;


		if (index < 3) {
			// Morning Star is a 4-candle pattern
			return false;
		}
		Tick p0Tick = series.getTick(index - 3);
		Tick p1Tick = series.getTick(index - 2);
		Tick p2Tick = series.getTick(index - 1);
		Tick p3Tick = series.getTick(index);

	
		if (p0Tick.isBearish() && p3Tick.isBullish()) {

			final Decimal p0OpenPrice = p0Tick.getOpenPrice();
			final Decimal p0ClosePrice = p0Tick.getClosePrice();
			final Decimal p1OpenPrice = p1Tick.getOpenPrice();
			final Decimal p1ClosePrice = p1Tick.getClosePrice();
			final Decimal p2OpenPrice = p2Tick.getOpenPrice();
			final Decimal p2ClosePrice = p2Tick.getClosePrice();
			final Decimal p3OpenPrice = p3Tick.getOpenPrice();
			final Decimal p3ClosePrice = p3Tick.getClosePrice();

			// p0; p1; p2; p3 candels
			// p0 >= 0.50, p1<=0.50, p3<=0.25, downtrend
			//dojiNotInd 0.50, dojiInd 0.25
			//0.995 = 99.5 %
			condition1 = p2OpenPrice.isLessThanOrEqual(p0OpenPrice) && p2ClosePrice.isLessThanOrEqual(p0OpenPrice)
					&& (p3OpenPrice.isGreaterThanOrEqual(p2ClosePrice.multipliedBy(Decimal.valueOf(0.995)))
							|| p3OpenPrice.isGreaterThanOrEqual(p2OpenPrice.multipliedBy(Decimal.valueOf(0.995))));
	

			condition2 = downtrend.calculate(index);
			condition3 = !dojiNotInd.calculate(index - 3); //p0
			condition4 = dojiNotInd.calculate(index - 2); // p1
			
			p2AbsRealBody = (p2ClosePrice.minus(p2OpenPrice)).abs();
			p0AbsRealBody = (p0ClosePrice.minus(p0OpenPrice)).abs();
			p2TOp0BodyRatio = p2AbsRealBody.dividedBy(p0AbsRealBody);
			
			condition5 = p2TOp0BodyRatio.isLessThanOrEqual(dojiBodyFactor); //p2
			//condition5 = dojiInd.calculate(index - 1); // if p2 is doji 
			condition6 = !dojiNotInd.calculate(index); // p3 in not spining top

			
			satisfied = condition1 & condition2 & condition3 & condition4 & condition5 & condition6;

			if (condition2) {
			/*	System.out.print("\nMS_4C_p2D_Index: " + index + ", Date: " + series.getTick(index).getDateName());
				System.out.println(";  p0_index :" + (index - 3) + ";   p0 Date: " + p0Tick.getDateName() +";   Satisfied :" + satisfied);
				System.out.println("MS_4C_p2D_condition :" + condition1 + ";   downTrend : " + condition2
						+ ";   p0_!not_Doji: " + condition3 + ";   p1_not_doji: " + condition4 +
						";  p2_doji : " + condition5 );
				System.out.println("p3_!not_doji : " + condition6 + "\n");
		*/	}

			if (satisfied) {
				p3Tick.setSatisfiedIndicator("BuMoStr_4c_p2Do)");
				System.out.println("\nIndex : " + index + " Satified Indicator: BullishMorningStarIndicator(4 candle; p2 Doji)");
				// System.out.println("; Date: " +
				// series.getTick(index).getDateName());
				// System.out.println("p1 Date" + p1Tick.getDateName());
				// System.out.println("con1:" + condition1 + ";
				// DownTrnd:" + condition2 + ";!p1Doji: "
				// + p1dojiInd.getValue(index) + "; " + condition3 + ";
				// p2doji: " + condition4);
			}

			return satisfied;

		}

		return false;
	}
}
