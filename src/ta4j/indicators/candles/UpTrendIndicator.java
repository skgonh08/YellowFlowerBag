
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
 * Up Trend indicator.
 * <p>
 * @see http://www.investopedia.com/terms/b/bearishengulfingp.asp
 */
public class UpTrendIndicator extends CachedIndicator<Boolean> {

    private final TimeSeries series;
    private final int tickCount;
    private int offsetIndex = 0;
    
    
    /**
     * Constructor.
     * @param series a time series
     */
    public UpTrendIndicator(TimeSeries series, int tickCount) {
        super(series);
        this.series = series;
        this.tickCount = tickCount;
    }

    public UpTrendIndicator(TimeSeries series, int tickCount, int offsetIndex) {
        super(series);
        this.series = series;
        this.tickCount = tickCount;
        this.offsetIndex = offsetIndex;
    }
    
    @Override
    protected Boolean calculate(int index ) {
    	double xxbar = 0;
    	double xybar = 0;
    	double sumx = 0;
    	double sumy = 0;
    	double xbar = 0;
    	double ybar = 0;
    	double yi = 0;
        if (index - offsetIndex < tickCount) {
        	
            return false;
        }
        
        
        // first pass: read in data, compute xbar and ybar
        for (int n = 1; n <=tickCount; n++) {
        	
//        	if ((index-n)==0){
//        		break;
//        	}
        	//sumx += (index-n) ;
        	sumy += (series.getTick(index - offsetIndex-n).getMaxPrice().toDouble() 
        			+ series.getTick(index - offsetIndex -n).getMinPrice().toDouble() 
        			+ series.getTick(index - offsetIndex - n).getClosePrice().toDouble())/3;
        	/*sumy += series.getTick(index- offsetIndex - n).getClosePrice().toDouble();*/
        }
        
        sumx = (tickCount)*(tickCount+1)/2;
        xbar = sumx / tickCount;
        ybar = sumy / tickCount;

        // second pass: compute summary statistics
        for (int n = 1; n <=tickCount; n++)  {
//        	if ((index- offsetIndex -n)==0){
//        		break;
//        	}
        	yi = (series.getTick(index  - offsetIndex-n).getMaxPrice().toDouble() 
        			+ series.getTick(index  - offsetIndex -n).getMinPrice().toDouble() 
        			+ series.getTick(index - offsetIndex- n).getClosePrice().toDouble())/3;
        	/*yi += series.getTick(index - offsetIndex - n).getClosePrice().toDouble();*/
            xxbar += Math.pow((n - xbar), 2);
            xybar += ((tickCount-n) - xbar) * (yi - ybar);
//            System.out.println("yi "+yi);
//            System.out.println(" index - offsetIndex -n : "+(index - offsetIndex -n));
        }
        double beta1 = xybar / xxbar;
        
        if(beta1 >=  1.5) { // tan(2 deg) = 0.466
        	//System.out.println("Index : "+index+ " offsetIndex: " + offsetIndex +/*" Beta: "+beta1+
        	//					" sumx : "+ Math.round(sumx)+ " sumy: "+sumy+ "xxbar: "+xxbar+" xybar: "+xybar+*/" Satified Indicator: DownTrendIndicator");
        	return true;
        }
        return false;
      
    }
}
