package ta4jexamplesBS.candleSticks;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.input.ScrollEvent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import ta4j.TimeSeries;
import ta4j.candleSticks.CandleStickChart;
import ta4j.candleSticks.DecimalAxisFormatter;
import ta4jexamplesBS.loaders.CsvTicksLoader;
import javafx.event.EventHandler;
import javafx.geometry.Insets;

public class MainApp extends Application {

	private final int candleStickgrphHeight = 480;
	private final int volumeGrphHeight = 170;
	private final int graphWidthForOneFullScreen = 1300;
	private final int totalBarDisplyedOnScreen = 60;
	private int chartWidth = 0;
	private int inputDataCount = 0;
	final static String fileName = "13-08-2015-TO-11-08-2017ICICIBANKEQN.csv";

	@Override
	public void start(Stage stage) throws Exception {
		long strtTime = System.currentTimeMillis();
		TimeSeries series = CsvTicksLoader.loadDataFromCSV(fileName);
		
		CandleStickChart candleStickChart = new CandleStickChart("SBIN", series);
		ScrollPane scrollPane = new ScrollPane();
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
		final CategoryAxis xAxisVolBar = new CategoryAxis();
		final NumberAxis yAxisVolBar = new NumberAxis();
		yAxisVolBar.setTickLabelFormatter(new DecimalAxisFormatter("###0.##E0"));
		final BarChart<String, Number> volumeBarChart = new BarChart<>(xAxisVolBar, yAxisVolBar);
		XYChart.Series<String, Number> volBarSeriesData = new XYChart.Series<String, Number>();

		inputDataCount = series.getTickCount();
		chartWidth = (inputDataCount / totalBarDisplyedOnScreen == 0 ? 1 : inputDataCount / totalBarDisplyedOnScreen)
				* graphWidthForOneFullScreen;
		System.out.println("Chart Width: " + chartWidth);
		System.out.println("Input Data Count: " + inputDataCount);

		
		for(int i =0; i<inputDataCount;i++){
			String label = sdf.format(series.getTick(i).getEndTime().toDate());
			volBarSeriesData.getData().add(new XYChart.Data<String, Number>(label, series.getTick(i).getVolume().toDouble()));
		}
		
		
		volumeBarChart.getData().add(volBarSeriesData);
		volumeBarChart.setLegendVisible(false);
		volumeBarChart.setAnimated(false);
		volumeBarChart.setAlternativeRowFillVisible(false);
		volumeBarChart.setAlternativeColumnFillVisible(false);
		// bc.setHorizontalGridLinesVisible(false);
		volumeBarChart.setVerticalGridLinesVisible(false);
		volumeBarChart.getStylesheets().add(getClass().getResource("resources/VolBarGraphStyle.css").toExternalForm());
		
		

		// bc.setMaxHeight(150);
		// bc.setTranslateX(-15);
		// bc.setTranslateY(-50);
		// bc.getYAxis().setTickLabelsVisible(false);
		// bc.getYAxis().setOpacity(0);
		candleStickChart.getXAxis().setTickLabelsVisible(false);
		candleStickChart.getXAxis().setOpacity(0);
		FlowPane fPane = new FlowPane();
		// fPane.setVgap(5);
		candleStickChart.setPrefSize(chartWidth, candleStickgrphHeight);
		volumeBarChart.setPrefSize(chartWidth, volumeGrphHeight);
		fPane.getChildren().add(candleStickChart);
		fPane.getChildren().add(volumeBarChart);

		scrollPane.setContent(fPane);

		Scene scene = new Scene(scrollPane);
		scene.getStylesheets().add(getClass().getResource("resources/CandleStickChartStyles.css").toExternalForm());

		stage.setTitle("SBIN EQ last 2 Years");
		stage.setScene(scene);
		stage.show();

		candleStickChart.setYAxisFormatter(new DecimalAxisFormatter("#000.00"));
		long endTime = System.currentTimeMillis();
		System.out.println("Time: " + (endTime - strtTime) / 100);
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

	/**
	 * The main() method is ignored in correctly deployed JavaFX application.
	 * main() serves only as fallback in case the application can not be
	 * launched through deployment artifacts, e.g., in IDEs with limited FX
	 * support. NetBeans ignores main().
	 *
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}

}
