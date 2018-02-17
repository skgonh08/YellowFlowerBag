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
public class BullishPiercingIndicator extends CachedIndicator<Boolean> {

    private final TimeSeries series;
    private final int tickCountFrom;
    private final int tickCountTo;
    private final Decimal bodyFactor;
    
   
    
    /**
     * Constructor.
     * @param series a time series
     * @param tickCount tick Count for the down trend
     */
    public BullishPiercingIndicator(TimeSeries series, int tickCountFrom, int tickCountTo, double bodyFactor) {
        super(series);
        this.series = series;
        this.tickCountFrom = tickCountFrom;
        this.tickCountTo = tickCountTo;
        this.bodyFactor = Decimal.valueOf(bodyFactor);
    }

    @Override
    protected Boolean calculate(int index) {
    	
    	DojiIndicator dojiInd = new DojiIndicator(series, 5, Decimal.valueOf(0.50)); // 15% is the real absolute body of Average Real Body
    	DownTrendIndicator downtrend = new DownTrendIndicator(series, tickCountFrom, tickCountTo, 1);  
    	
    	boolean condition1, condition2, condition3, satisfied;
        if (index < 1) {
            // Piercing is a 2-candle pattern
            return false;
        }
        Tick prevTick = series.getTick(index-1);
        Tick currTick = series.getTick(index);
        if (prevTick.isBearish() && currTick.isBullish()) {
            final Decimal prevOpenPrice = prevTick.getOpenPrice();
            final Decimal prevClosePrice = prevTick.getClosePrice();
            final Decimal currOpenPrice = currTick.getOpenPrice();
            final Decimal currClosePrice = currTick.getClosePrice();
            final Decimal prevRealBody = prevOpenPrice.minus(prevClosePrice); 
            final Decimal currRealBody = currClosePrice.minus(currOpenPrice);
            final Decimal bodyRatio = currRealBody.dividedBy(prevRealBody);
            
            condition1 = // currOpenPrice.isLessThan(prevOpenPrice) && 
            		//currOpenPrice.isGreaterThan(prevClosePrice) && 
                    currClosePrice.isGreaterThanOrEqual(prevOpenPrice)
                    && bodyRatio.isGreaterThan(Decimal.valueOf(0.50)) ;
                    //&& bodyRatio.isLessThanOrEqual(Decimal.valueOf(1.00));   // Sign should be omit according to formula
            		//&& currClosePrice.isGreaterThan(prevClosePrice);
            
            condition2 = downtrend.calculate(index);
            condition3 = !dojiInd.calculate(index-1);
            
            satisfied = condition1 && condition2 && condition3;
            
            if (satisfied){
            	currTick.setSatisfiedIndicator("BuPiercing");
            	System.out.println("Index : "+index+" Satified Indicator: BullishPierciengIndicator");
            }
            
            return satisfied;
        }
        return false;
    }
}
