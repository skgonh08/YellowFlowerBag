package ta4j.trading.byingSellingRules;

import ta4j.Decimal;
import ta4j.Rule;
import ta4j.TimeSeries;
import ta4j.indicators.candles.BearishDarkCloudCoverIndicator;
import ta4j.indicators.candles.BearishEngulfingIndicator;
import ta4j.indicators.candles.BearishHaramiIndicator;
import ta4j.indicators.candles.BullishEngulfingIndicator;
import ta4j.indicators.candles.BullishHaramiIndicator;
import ta4j.indicators.candles.BullishPaperUmbrellaIndicator;
import ta4j.indicators.candles.BullishPiercingIndicator;
import ta4j.indicators.candles.BullishTheMorningStar;
import ta4j.indicators.candles.DownTrendIndicator;
import ta4j.indicators.candles.ThreeBlackCrowsIndicator;
import ta4j.indicators.candles.ThreeWhiteSoldiersIndicator;
import ta4j.indicators.candles.UpTrendIndicator;
import ta4j.indicators.simple.AmountIndicator;
import ta4j.indicators.simple.ClosePriceIndicator;
import ta4j.indicators.simple.VolumeIndicator;
import ta4j.indicators.trackers.EMAIndicator;
import ta4j.indicators.trackers.SMAIndicator;
import ta4j.trading.rules.BooleanIndicatorRule;
import ta4j.trading.rules.CrossedDownIndicatorRule;
import ta4j.trading.rules.StopLossRule;

public class BuyingSellingRuleFromIndicators implements BuyingSellingRule{

private TimeSeries series;
	
public BuyingSellingRuleFromIndicators(TimeSeries series){
	this.series = series;
}

		//Get Close price indicator
		ClosePriceIndicator closePrice = new ClosePriceIndicator(series);

		// Getting the exponential moving average (EMA) of the close price over
		// the
		// last 5 ticks
		EMAIndicator shortEma = new EMAIndicator(closePrice, 5);

		// Getting a longer EMA (e.g. over the 30 last ticks)
		EMAIndicator longEma = new EMAIndicator(closePrice, 11);

		BullishHaramiIndicator BuHarami = new BullishHaramiIndicator(series, 6, 20, 0.60, 0.30);
		BullishEngulfingIndicator BulEngulf = new BullishEngulfingIndicator(series, 20, 20, 0.50);
		BullishPiercingIndicator BullPier = new BullishPiercingIndicator(series, 20, 20, 0.50);
		BullishPaperUmbrellaIndicator BullPaper = new BullishPaperUmbrellaIndicator(series, 5, 5, 0.25);
		ThreeWhiteSoldiersIndicator threeWhiteSo = new ThreeWhiteSoldiersIndicator(series, 5, Decimal.ONE);
		BullishTheMorningStar morningStar = new BullishTheMorningStar(series, 6, 6, 0.50, 0.25);

		BearishEngulfingIndicator BearEngulf = new BearishEngulfingIndicator(series);
		BearishHaramiIndicator BearHarami = new BearishHaramiIndicator(series);
		BearishDarkCloudCoverIndicator BeDarkClCo = new BearishDarkCloudCoverIndicator(series);
		ThreeBlackCrowsIndicator threeBlackCr = new ThreeBlackCrowsIndicator(series, 5, Decimal.ONE);

		UpTrendIndicator uptrend = new UpTrendIndicator(series, 20);
		DownTrendIndicator downtrend = new DownTrendIndicator(series, 20);

		VolumeIndicator volumeIndicator = new VolumeIndicator(series);
		SMAIndicator volumeSMA = new SMAIndicator(volumeIndicator, 10);

		AmountIndicator amountIndicator = new AmountIndicator(series);
		
		
		public Rule buyingRule(){
			return (/*
					 * new CrossedUpIndicatorRule(shortEma, longEma))
					 * .or(new CrossedDownIndicatorRule(closePrice,
					 * Decimal.valueOf("900"))) .or(new
					 * BooleanIndicatorRule(threeWhiteSo)) .or(
					 */new BooleanIndicatorRule(BuHarami))
					.or(new BooleanIndicatorRule(BulEngulf))/*
					 * .or(new BooleanIndicatorRule(BullPier)) */
					.or(new BooleanIndicatorRule(BullPaper)).or(new BooleanIndicatorRule(morningStar));
					// .and(new OverIndicatorRule(volumeIndicator, volumeSMA)));
		}
		
		public Rule sellingRule(){
			return (new CrossedDownIndicatorRule(shortEma, longEma)
					.or(new StopLossRule(closePrice, Decimal.valueOf("1")))
					// .or(new StopGainRule(closePrice, Decimal.valueOf("3")))
					.or(new BooleanIndicatorRule(threeBlackCr))
					.or((new BooleanIndicatorRule(BearEngulf)).and(new BooleanIndicatorRule(uptrend)))
					.or((new BooleanIndicatorRule(BearHarami)).and(new BooleanIndicatorRule(uptrend)))
					.or((new BooleanIndicatorRule(BeDarkClCo)).and(new BooleanIndicatorRule(uptrend))));
					// .and(new OverIndicatorRule(volumeIndicator, volumeSMA)));
		}
}
