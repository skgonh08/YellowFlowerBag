package ta4jexamplesBS;

import java.text.SimpleDateFormat;
import java.util.List;

import alphaVantage.InputDataType;
import alphaVantage.excelWrite.TicksXLSDatabase;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import ta4j.AnalysisCriterion;
import ta4j.Decimal;
import ta4j.Rule;
import ta4j.Strategy;
import ta4j.TimeSeries;
import ta4j.Trade;
import ta4j.TradingRecord;
import ta4j.analysis.CashFlow;
import ta4j.analysis.criteria.AverageProfitableTradesCriterion;
import ta4j.analysis.criteria.RewardRiskRatioCriterion;
import ta4j.analysis.criteria.TotalGrossProfit;
import ta4j.analysis.criteria.TotalProfitCriterion;
import ta4j.analysis.criteria.VersusBuyAndHoldCriterion;
import ta4j.candleSticks.CandleStickChart;
import ta4j.candleSticks.CandleStickGraph;
import ta4j.candleSticks.DecimalAxisFormatter;
import ta4j.indicators.candles.*;
import ta4j.indicators.simple.*;
import ta4j.indicators.trackers.EMAIndicator;
import ta4j.indicators.trackers.SMAIndicator;
import ta4j.trading.byingSellingRules.BuyingSellingRuleFromIndicators;
import ta4j.trading.rules.BooleanIndicatorRule;
import ta4j.trading.rules.CrossedDownIndicatorRule;
import ta4j.trading.rules.CrossedUpIndicatorRule;
import ta4j.trading.rules.OverIndicatorRule;
import ta4j.trading.rules.StopGainRule;
import ta4j.trading.rules.StopLossRule;
import ta4jexamplesBS.loaders.CsvTicksLoader;

public class SBITest1yr extends Application {

	private static String stockName = "TCS";

	private static TimeSeries series = null;

	public static void main(String[] args) {

		TicksXLSDatabase db = new TicksXLSDatabase(stockName, "Day");
		//db.updateDatabase();

		try {
			series = db.loadTicks(1, InputDataType.YEAR);
			System.out.println("row count:" + series.getTickCount());
			System.out.println("Last Update Date: " + series.getTick(series.getTickCount() - 1).getDateName());
		} catch (Exception e) {

			e.printStackTrace();
		}

		// Get Close price indicator
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
		System.out.println("Amount at index 2 is : " + amountIndicator.getValue(2));

		// Buying rules
		BuyingSellingRuleFromIndicators bsr = new BuyingSellingRuleFromIndicators(series);
		
		
		Rule buyingRule = bsr.buyingRule(); //inserted by bngonh on 22.02.2018 @8:50 pm
		
						/*  inserted by bngonh on 22.02.2018 @8:50 pm
						 
						 	(new CrossedUpIndicatorRule(shortEma, longEma))
							  //.or(new CrossedDownIndicatorRule(closePrice,Decimal.valueOf("900"))) 
							  //.or(new BooleanIndicatorRule(threeWhiteSo)) 
							  .or(new BooleanIndicatorRule(BuHarami))
							  .or(new BooleanIndicatorRule(BulEngulf))
							 .or(new BooleanIndicatorRule(BullPier))
							.or(new BooleanIndicatorRule(BullPaper))
							.or(new BooleanIndicatorRule(morningStar));
							// .and(new OverIndicatorRule(volumeIndicator, volumeSMA)));

		 				inserted by bngonh on 22.02.2018 @8:50 pm*/ 
		// Selling rules

		Rule sellingRule = new CrossedDownIndicatorRule(shortEma, longEma)
				.or(new StopLossRule(closePrice, Decimal.valueOf("1")))
				// .or(new StopGainRule(closePrice, Decimal.valueOf("3")))
				.or(new BooleanIndicatorRule(threeBlackCr))
				.or((new BooleanIndicatorRule(BearEngulf)).and(new BooleanIndicatorRule(uptrend)))
				.or((new BooleanIndicatorRule(BearHarami)).and(new BooleanIndicatorRule(uptrend)))
				.or((new BooleanIndicatorRule(BeDarkClCo)).and(new BooleanIndicatorRule(uptrend)));
		// .and(new OverIndicatorRule(volumeIndicator, volumeSMA)));

		// Running our juicy trading strategy...
		TradingRecord tradingRecord = series.run(new Strategy(buyingRule, sellingRule));
		System.out.println("Number of trades for our strategy: " + tradingRecord.getTradeCount());

		// System.out.println(tradingRecord.getTrades());
		List<Trade> trades = tradingRecord.getTrades();
		// Trade trade;
		double grossProfit = 0;
		for (Trade trade : trades) {
			System.out.println(trade.toString());
			AnalysisCriterion totalProfit = new TotalGrossProfit();

			// totalProfit.calculate(series, trade);
			System.out.println(totalProfit.calculate(series, trade));
			grossProfit += totalProfit.calculate(series, trade);
		}

		System.out.println("GrossProfit: " + grossProfit);

		// Analysis

		// Getting the cash flow of the resulting trades
		CashFlow cashFlow = new CashFlow(series, tradingRecord);

		// System.out.println(cashFlow);

		// Getting the profitable trades ratio
		AnalysisCriterion profitTradesRatio = new AverageProfitableTradesCriterion();
		System.out.println("Profitable trades ratio: " + profitTradesRatio.calculate(series, tradingRecord));
		// Getting the reward-risk ratio
		AnalysisCriterion rewardRiskRatio = new RewardRiskRatioCriterion();
		System.out.println("Reward-risk ratio: " + rewardRiskRatio.calculate(series, tradingRecord));

		// Total profit of our strategy
		// vs total profit of a buy-and-hold strategy
		AnalysisCriterion vsBuyAndHold = new VersusBuyAndHoldCriterion(new TotalProfitCriterion());
		System.out.println("Our profit vs buy-and-hold profit: " + vsBuyAndHold.calculate(series, tradingRecord));

		// CandleStickGraph graph = new CandleStickGraph();
		System.out.println(series.getTickCount());
		// graph.setTimeSeries(series);
		// graph.createGraph(args);

		// launching graph
		//launch(args);
	}

	protected double getNewValue(double previousValue) {
		int sign;

		if (Math.random() < 0.5) {
			sign = -1;
		} else {
			sign = 1;
		}
		return getRandom() * sign + previousValue;
	}

	protected double getRandom() {
		double newValue = 0;
		newValue = Math.random() * 10;
		return newValue;
	}

	/* ======================================== */
	/* Below methods are written for JAVA FX */
	/* ======================================== */
	@Override
	public void start(Stage stage) throws Exception {
		CandleStickGraph candleStickGraph = new CandleStickGraph(series, stockName);
		Scene scene = candleStickGraph.getScene();
		stage.setTitle(stockName);
		stage.setScene(scene);
		stage.show();

	}

}
