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
 * Bearish engulfing pattern indicator.
 * <p>
 * @see http://www.investopedia.com/terms/b/bearishengulfingp.asp
 */
public class BearishEngulfingIndicator extends CachedIndicator<Boolean> {

    private final TimeSeries series;
    
    /**
     * Constructor.
     * @param series a time series
     */
    public BearishEngulfingIndicator(TimeSeries series) {
        super(series);
        this.series = series;
    }

    @Override
    protected Boolean calculate(int index) {
    	boolean satisfied;
        if (index < 1) {
            // Engulfing is a 2-candle pattern
            return false;
        }
        Tick prevTick = series.getTick(index-1);
        Tick currTick = series.getTick(index);
        if (prevTick.isBullish() && currTick.isBearish()) {
            final Decimal prevOpenPrice = prevTick.getOpenPrice();
            final Decimal prevClosePrice = prevTick.getClosePrice();
            final Decimal currOpenPrice = currTick.getOpenPrice();
            final Decimal currClosePrice = currTick.getClosePrice();
            satisfied =  currOpenPrice.isGreaterThan(prevOpenPrice) && currOpenPrice.isGreaterThan(prevClosePrice)
                    && currClosePrice.isLessThan(prevOpenPrice) && currClosePrice.isLessThan(prevClosePrice);
            if(satisfied){
            	currTick.setSatisfiedIndicator("BeEng");
            	System.out.println("Index : "+index+" Satified Indicator: BearishEngulfingIndicator");
            }
            
            return satisfied;
        }
        return false;
    }
}
