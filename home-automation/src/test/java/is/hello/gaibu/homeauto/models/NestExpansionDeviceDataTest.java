package is.hello.gaibu.homeauto.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import is.hello.gaibu.core.models.Capability;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class NestExpansionDeviceDataTest {
    final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testDeSerialization() throws IOException {

        String data = "{\"thermostat_id\":\"kw60N-mpIIjh6aUNJuiPzeSQtze7Bojs\",\"name\":\"Hello Home (Backyard (11110000000000000000000000000))\",\"id\":\"kw60N-mpIIjh6aUNJuiPzeSQtze7Bojs\"}";
        final NestExpansionDeviceData nestExpansionDeviceData = mapper.readValue(data, NestExpansionDeviceData.class);
        assertTrue("thermostat_id", nestExpansionDeviceData.getId().equals("kw60N-mpIIjh6aUNJuiPzeSQtze7Bojs"));
        assertTrue("list capabilities", nestExpansionDeviceData.capabilities().size() == 0);
    }

    @Test
    public void testSerialization() throws IOException {
        final NestExpansionDeviceData deviceData = new NestExpansionDeviceData(
                "thermostat_id",
                "name",
                Lists.newArrayList(Capability.COOL)
        );

        final String jsonValue = mapper.writeValueAsString(deviceData);
        final NestExpansionDeviceData deser = mapper.readValue(jsonValue, NestExpansionDeviceData.class);
        assertTrue("thermostat_id", deviceData.getId().equals(deser.getId()));
        assertTrue("capabilities", deviceData.capabilities().size() == 1);
        assertTrue("heat", deviceData.capabilities().get(0) == Capability.COOL);
    }
}
