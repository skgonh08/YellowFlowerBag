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
public class BullishTheMorningStar extends CachedIndicator<Boolean> {

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
	 * @param tickCountFrom
	 *            the tick count for down trend
	 * @param tickCountTo
	 *  the tick count up to tick
	 * @param DojiNotBodyFactor
	 *            represent the Av Abs Rral body factor
	 * @param dojiBodyfactor
	 *            represent the Av Abs real body factor of p2 i.e. doji
	 */
	public BullishTheMorningStar(TimeSeries series, int tickCountFrom, int tickCountTo, double dojiNotBodyFactor, double dojiBodyFactor) {
		super(series);
		this.series = series;
		this.tickCountFrom = tickCountFrom;
		this.tickCountTo = tickCountTo;
		this.dojiNotBodyFactor = Decimal.valueOf(dojiNotBodyFactor);
		this.dojiBodyFactor = Decimal.valueOf(dojiBodyFactor);
	}

	@Override
	protected Boolean calculate(int index) {

		MorningStar_3Candles moSt3C = new MorningStar_3Candles(series, tickCountFrom, tickCountTo, dojiNotBodyFactor, dojiBodyFactor);
		MorningStar_4Candle_p1Doji moSt4Cp1D = new MorningStar_4Candle_p1Doji(series, tickCountFrom, tickCountTo, dojiNotBodyFactor, dojiBodyFactor);
		MorninsStar_4Candle_p2Doji moSt4Cp2D = new MorninsStar_4Candle_p2Doji(series, tickCountFrom, tickCountTo, dojiNotBodyFactor, dojiBodyFactor);
		
		boolean satisfied = moSt3C.calculate(index) ||   moSt4Cp1D.calculate(index)  || moSt4Cp2D.calculate(index);
		
		return satisfied;
	}
}
