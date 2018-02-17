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


import javax.servlet.http.HttpSession;

import ta4j.Decimal;
import ta4j.Tick;
import ta4j.TimeSeries;
import ta4j.indicators.CachedIndicator;

/**
 * Bullish engulfing pattern indicator.
 * <p>
 * @see http://www.investopedia.com/terms/b/bullishengulfingpattern.asp
 */
public class BullishEngulfingIndicator extends CachedIndicator<Boolean> {

    private final TimeSeries series;
    private final int tickCountFrom;
    private final int tickCountTo;
    private final Decimal bodyFactor;

    
    /**
     * Constructor.
     * @param series a time series
     * @param tickCount the tick count for down trend
     * @param bodyFactor is the Av body factor of previous candle 
     */
    public BullishEngulfingIndicator(TimeSeries series, int tickCountFrom, int tickCountTo, double bodyFactor) {
        super(series);
        this.series = series;
        this.tickCountFrom = tickCountFrom;
        this.tickCountTo = tickCountTo;
        this.bodyFactor = Decimal.valueOf(bodyFactor);
    }

    @Override
    protected Boolean calculate(int index) {
    	
    	DojiIndicator dojiInd = new DojiIndicator(series, 5, bodyFactor); 
    	DownTrendIndicator downtrend = new DownTrendIndicator(series, tickCountFrom, tickCountTo, 1); 
    	
    	
    	boolean condition1, condition2, condition3, satisfied;
        if (index < 1) {
            // Engulfing is a 2-candle pattern
            return false;
        }
       Tick prevTick = series.getTick(index-1);
        Tick currTick = series.getTick(index);
        if (prevTick.isBearish() && currTick.isBullish()) {
            final Decimal prevOpenPrice = prevTick.getOpenPrice();
            final Decimal prevClosePrice = prevTick.getClosePrice();
            final Decimal currOpenPrice = currTick.getOpenPrice();
            final Decimal currClosePrice = currTick.getClosePrice();
            condition1 =  currOpenPrice.isLessThan(prevOpenPrice) && currOpenPrice.isLessThan(prevClosePrice)
                    && currClosePrice.isGreaterThan(prevOpenPrice) && currClosePrice.isGreaterThan(prevClosePrice);
            
            condition2 = downtrend.calculate(index);
            condition3 = !dojiInd.calculate(index - 1);
            satisfied = condition1 && condition2 && condition3;
            
            if (satisfied){
            	currTick.setSatisfiedIndicator("BuEng");
            	System.out.println("Index : "+index+" Satified Indicator: BullishEngulfingIndicator");
            	//System.out.println("con1, con2, con3 : " + condition1 + ";" + condition2 + ";" + condition3);
            }
            
            return satisfied;
        }
        return false;
    }
}
