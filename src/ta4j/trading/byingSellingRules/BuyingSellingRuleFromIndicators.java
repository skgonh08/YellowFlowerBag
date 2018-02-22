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

public class BuyingSellingRuleFromIndicators implements BuyingSellingRule {

	private TimeSeries series;
	ClosePriceIndicator closePrice;
	EMAIndicator shortEma;
	EMAIndicator longEma;
	BullishHaramiIndicator BuHarami;
	BullishEngulfingIndicator BulEngulf;
	BullishPiercingIndicator BullPier;
	BullishPaperUmbrellaIndicator BullPaper;
	ThreeWhiteSoldiersIndicator threeWhiteSo;
	SMAIndicator volumeSMA;
	BearishHaramiIndicator BearHarami;
	BearishDarkCloudCoverIndicator BeDarkClCo;
	BullishTheMorningStar morningStar;
	UpTrendIndicator uptrend;
	DownTrendIndicator downtrend;
	AmountIndicator amountIndicator;
	ThreeBlackCrowsIndicator threeBlackCr;
	VolumeIndicator volumeIndicator;
	BearishEngulfingIndicator BearEngulf;

	public BuyingSellingRuleFromIndicators(TimeSeries series) {
		this.series = series;
		initialize();
	}

	private void initialize() {
		closePrice = new ClosePriceIndicator(series);

		shortEma = new EMAIndicator(closePrice, 5);

		longEma = new EMAIndicator(closePrice, 11);

		BuHarami = new BullishHaramiIndicator(series, 6, 20, 0.60, 0.30);

		BulEngulf = new BullishEngulfingIndicator(series, 20, 20, 0.50);

		BullPier = new BullishPiercingIndicator(series, 20, 20, 0.50);

		BullPaper = new BullishPaperUmbrellaIndicator(series, 5, 5, 0.25);

		threeWhiteSo = new ThreeWhiteSoldiersIndicator(series, 5, Decimal.ONE);

		morningStar = new BullishTheMorningStar(series, 6, 6, 0.50, 0.25);

		BearHarami = new BearishHaramiIndicator(series);
		
		BearEngulf = new BearishEngulfingIndicator(series);

		BeDarkClCo = new BearishDarkCloudCoverIndicator(series);

		threeBlackCr = new ThreeBlackCrowsIndicator(series, 5, Decimal.ONE);

		uptrend = new UpTrendIndicator(series, 20);

		downtrend = new DownTrendIndicator(series, 20);

		volumeIndicator = new VolumeIndicator(series);

		volumeSMA = new SMAIndicator(volumeIndicator, 10);

		amountIndicator = new AmountIndicator(series);
	}

	public Rule buyingRule() {
		return (/*
				 * new CrossedUpIndicatorRule(shortEma, longEma)) .or(new
				 * CrossedDownIndicatorRule(closePrice, Decimal.valueOf("900")))
				 * .or(new BooleanIndicatorRule(threeWhiteSo)) .or(
				 */new BooleanIndicatorRule(BuHarami))
				.or(new BooleanIndicatorRule(
						BulEngulf))/*
									 * .or(new BooleanIndicatorRule(BullPier))
									 */
				.or(new BooleanIndicatorRule(BullPaper)).or(new BooleanIndicatorRule(morningStar));
		// .and(new OverIndicatorRule(volumeIndicator, volumeSMA)));
	}

	public Rule sellingRule() {
		return (new CrossedDownIndicatorRule(shortEma, longEma).or(new StopLossRule(closePrice, Decimal.valueOf("1")))
				// .or(new StopGainRule(closePrice, Decimal.valueOf("3")))
				.or(new BooleanIndicatorRule(threeBlackCr))
				.or((new BooleanIndicatorRule(BearEngulf)).and(new BooleanIndicatorRule(uptrend)))
				.or((new BooleanIndicatorRule(BearHarami)).and(new BooleanIndicatorRule(uptrend)))
				.or((new BooleanIndicatorRule(BeDarkClCo)).and(new BooleanIndicatorRule(uptrend))));
		// .and(new OverIndicatorRule(volumeIndicator, volumeSMA)));
	}
}
