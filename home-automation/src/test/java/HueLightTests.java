import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import is.hello.gaibu.homeauto.clients.HueLight;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by jnorgan on 10/23/16.
 */
public class HueLightTests {
  private static final Logger LOGGER = LoggerFactory.getLogger(HueLightTests.class);

  public HueLightTests(){}

  @Test
  public void testBrightnessValueConversion() throws Exception {
    final Integer convertedMaxBrightness = HueLight.convertDisplayedToRealBrightness(HueLight.HUE_MAX_BRIGHTNESS);
    final Integer convertedMinBrightness = HueLight.convertDisplayedToRealBrightness(HueLight.HUE_MIN_BRIGHTNESS);


    assertThat(convertedMaxBrightness, is(HueLight.HUE_API_MAX_BRIGHTNESS));
    assertThat(convertedMinBrightness, is(HueLight.HUE_API_MIN_BRIGHTNESS));

    //Check the midpoint
    final Double midBrightness = (HueLight.HUE_MAX_BRIGHTNESS - HueLight.HUE_MIN_BRIGHTNESS) / 2.0d;
    final Integer trueMidBrightness = (HueLight.HUE_API_MAX_BRIGHTNESS - HueLight.HUE_API_MIN_BRIGHTNESS) / 2;
    final Integer convertedMidBrightness = HueLight.convertDisplayedToRealBrightness(midBrightness);
    final float conversionError = Math.abs((float)(convertedMidBrightness - trueMidBrightness) / (HueLight.HUE_API_MAX_BRIGHTNESS - HueLight.HUE_API_MIN_BRIGHTNESS));
    //Allow for some conversion error due to precision loss
    assertThat((conversionError < 0.01f) , is(true));
  }



}
