/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2017 Marc de Verdelhan & respective authors (see AUTHORS)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package ta4j.indicators.candles;

import ta4j.Decimal;
import ta4j.Tick;
import ta4j.TimeSeries;
import ta4j.indicators.CachedIndicator;

/**
 * Bearish Harami pattern indicator.
 * <p>
 * @see http://www.investopedia.com/terms/b/bullishharami.asp
 */
public class BullishHaramiIndicator extends CachedIndicator<Boolean> {

    private final TimeSeries series;
    private final int tickCountFrom;
    private final int tickCountTo;
    private final double p1BodyFactor, p2BodyFactor;
    
   
    
    /**
     * Constructor.
     * @param series a time series
     * @param tickCount count the Tick for down Trend
     * @param p1BodyFactor It is the body factor of Av Abs Real body of pattern 1
     * @param p2BodyFactor It is the body factor of Av Abs Real body of pattern 2 
     */
    public BullishHaramiIndicator(TimeSeries series, int tickCountFrom, int tickCountTo, double p1BodyFactor, double p2BodyFactor) {
        super(series);
        this.series = series;
        this.tickCountFrom = tickCountFrom;
        this.tickCountTo= tickCountTo;
        this.p1BodyFactor = p1BodyFactor;
        this.p2BodyFactor = p2BodyFactor;
    }

    @Override
    protected Boolean calculate(int index) {
    	boolean condition1, condition2, condition3, condition4, condition5, condition6, satisfied;
    	
    	DojiIndicator p1DojiInd = new DojiIndicator(series, 5, Decimal.valueOf(p1BodyFactor)); 
    	DojiIndicator p2DojiInd = new DojiIndicator(series, 5, Decimal.valueOf(p2BodyFactor));
    	DownTrendIndicator downtrend = new DownTrendIndicator(series, tickCountFrom, tickCountTo, 0);
    	
        if (index < 1) {
            // Harami is a 2-candle pattern
            return false;
        }
        Tick prevTick = series.getTick(index-1);
        Tick currTick = series.getTick(index);
        if (prevTick.isBearish() && currTick.isBullish()) {
            final Decimal prevOpenPrice = prevTick.getOpenPrice();
            final Decimal prevClosePrice = prevTick.getClosePrice();
            final Decimal currOpenPrice = currTick.getOpenPrice();
            final Decimal currClosePrice = currTick.getClosePrice();
            
            final Decimal prevStickLength = prevTick.getMaxPrice().minus(prevTick.getMinPrice());
            final Decimal currStickLength = currTick.getMaxPrice().minus(currTick.getMinPrice());
            
            condition1 = currOpenPrice.isGreaterThanOrEqual(prevClosePrice) && currClosePrice.isLessThanOrEqual(prevOpenPrice);
            
            condition2 = downtrend.calculate(index);
            condition3 = !p1DojiInd.calculate(index-1);
            condition4 = !p2DojiInd.calculate(index);
            // p2's close price is just below (i.e.99%) the p1's open price
            condition5 = currClosePrice.isGreaterThanOrEqual(prevOpenPrice.multipliedBy(Decimal.valueOf(0.99)));
            // Current tick activity is less than previous tick activity to check that current tick is not doji or spinning top.
            condition6 = currStickLength.isLessThanOrEqual(prevStickLength);
            
            satisfied = condition1 && condition2 && condition3 && condition4 && condition5 && condition6;
            if (satisfied){
            	currTick.setSatisfiedIndicator("BuHara");
            	System.out.println("Index : "+index+" Satified Indicator: BullishHaramiIndicator");
            }
            
            return satisfied;
        }
        return false;
    }
}
