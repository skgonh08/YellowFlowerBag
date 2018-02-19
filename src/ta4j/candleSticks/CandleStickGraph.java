package ta4j.candleSticks;

import java.text.SimpleDateFormat;

import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import ta4j.ProjectConstants;
import ta4j.TimeSeries;

public class CandleStickGraph {

	private String fileName = "";
	private TimeSeries timeSeries;

	public CandleStickGraph(TimeSeries timeSeries, String fileName) {
		this.timeSeries = timeSeries;
		this.fileName = fileName;
	}

	public Scene getScene() throws Exception {
		
		int inputDataCount = 0;
		int chartWidth = 0;
		long strtTime = System.currentTimeMillis();

		CandleStickChart candleStickChart = new CandleStickChart(fileName, timeSeries);
		ScrollPane scrollPane = new ScrollPane();

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
		final CategoryAxis xAxisVolBar = new CategoryAxis();
		final NumberAxis yAxisVolBar = new NumberAxis();
		yAxisVolBar.setTickLabelFormatter(new DecimalAxisFormatter("###0.##E0"));
		final BarChart<String, Number> volumeBarChart = new BarChart<>(xAxisVolBar, yAxisVolBar);
		XYChart.Series<String, Number> volBarSeriesData = new XYChart.Series<String, Number>();

		inputDataCount = timeSeries.getTickCount();
		chartWidth = (inputDataCount / ProjectConstants.totalBarDisplyedOnScreen == 0 ? 1
				: inputDataCount / ProjectConstants.totalBarDisplyedOnScreen)
				* ProjectConstants.graphWidthForOneFullScreen;
		// System.out.println("Chart Width: " + chartWidth);
		// System.out.println("Input Data Count: " + inputDataCount);

		for (int i = 0; i < inputDataCount; i++) {
			String label = sdf.format(timeSeries.getTick(i).getEndTime().toDate());
			volBarSeriesData.getData()
					.add(new XYChart.Data<String, Number>(label, timeSeries.getTick(i).getVolume().toDouble()));
		}

		volumeBarChart.getData().add(volBarSeriesData);
		volumeBarChart.setLegendVisible(false);
		volumeBarChart.setAnimated(false);
		volumeBarChart.setAlternativeRowFillVisible(false);
		volumeBarChart.setAlternativeColumnFillVisible(false);
		// bc.setHorizontalGridLinesVisible(false);
		volumeBarChart.setVerticalGridLinesVisible(false);
		volumeBarChart.getStylesheets().add(getClass().getResource("resources/VolBarGraphStyle.css").toExternalForm());

		candleStickChart.getXAxis().setTickLabelsVisible(false);
		candleStickChart.getXAxis().setOpacity(0);
		FlowPane fPane = new FlowPane();
		// fPane.setVgap(5);
		candleStickChart.setPrefSize(chartWidth, ProjectConstants.candleStickgrphHeight);
		volumeBarChart.setPrefSize(chartWidth, ProjectConstants.volumeGrphHeight);
		fPane.getChildren().add(candleStickChart);
		fPane.getChildren().add(volumeBarChart);
		candleStickChart.setYAxisFormatter(new DecimalAxisFormatter("#000.00"));

		scrollPane.setContent(fPane);

		Scene scene = new Scene(scrollPane);
		scene.getStylesheets().add(getClass().getResource("resources/CandleStickChartStyles.css").toExternalForm());

		// stage.setTitle("SBIN EQ last 2 Years");
		// stage.setScene(scene);
		// stage.show();

		long endTime = System.currentTimeMillis();
		//System.out.println("Time: " + (endTime - strtTime) / 100);

		return scene;
	}

}
