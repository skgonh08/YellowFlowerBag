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
package ta4j.indicators.oscillators;

import ta4j.Decimal;
import ta4j.Indicator;
import ta4j.indicators.CachedIndicator;
import ta4j.indicators.trackers.EMAIndicator;

/**
 * Percentage price oscillator (PPO) indicator.
 * <p>
 */
public class PPOIndicator extends CachedIndicator<Decimal> {

    private final EMAIndicator shortTermEma;

    private final EMAIndicator longTermEma;

    public PPOIndicator(Indicator<Decimal> indicator, int shortTimeFrame, int longTimeFrame) {
        super(indicator);
        if (shortTimeFrame > longTimeFrame) {
            throw new IllegalArgumentException("Long term period count must be greater than short term period count");
        }
        shortTermEma = new EMAIndicator(indicator, shortTimeFrame);
        longTermEma = new EMAIndicator(indicator, longTimeFrame);
    }

    @Override
    protected Decimal calculate(int index) {
        Decimal shortEmaValue = shortTermEma.getValue(index);
        Decimal longEmaValue = longTermEma.getValue(index);
        return shortEmaValue.minus(longEmaValue)
                .dividedBy(longEmaValue)
                .multipliedBy(Decimal.HUNDRED);
    }
}
