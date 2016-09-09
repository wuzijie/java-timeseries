package timeseries.models;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import static data.DoubleFunctions.newArray;
import java.util.Arrays;

import org.junit.Test;

import data.TestData;
import timeseries.TimePeriod;
import timeseries.TimeSeries;
import timeseries.TimeUnit;

import timeseries.models.Arima.ModelCoefficients;

public class ArimaSpec {
  
  @Test
  public void arSarCoeffsMakeSense() {
    double[] arCoeffs = new double[] {0.5, -0.3};
    double[] sarCoeffs = new double[] {0.4, -0.2};
    Arima.ModelCoefficients modelCoeffs = new Arima.ModelCoefficients(arCoeffs, new double[] {}, sarCoeffs,
        new double[] {}, 0, 0, 0.0);
    Arima model = new Arima(TestData.internetTraffic(), modelCoeffs, new TimePeriod(TimeUnit.MILLISECOND, 100));
    System.out.println(Arrays.toString(model.expandArCoefficients()));
  }
  
  @Test
  public void whenArimaModelFitKnownCoefficientsInterceptCorrect() {
    TimeSeries series = TestData.ukcars();
    double[] arCoeffs = newArray(0.6041);
    double[] sarCoeffs = newArray(0.7753);
    double mean = 341.7788;
    ModelCoefficients.Builder builder = ModelCoefficients.newBuilder();
    ModelCoefficients modelCoeffs = builder.setArCoeffs(arCoeffs).setSarCoeffs(sarCoeffs).setMean(mean).build();
    Arima model = new Arima(series, modelCoeffs, TimePeriod.oneYear());
    assertThat(model.intercept(), is(closeTo(30.40146, 1E-1)));
  }

}
