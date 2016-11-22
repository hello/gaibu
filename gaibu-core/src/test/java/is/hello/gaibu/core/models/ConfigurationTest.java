package is.hello.gaibu.core.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ConfigurationTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testSerDer() throws IOException {

        final Configuration configuration = new Configuration(
                "id",
                "name",
                false,
                Lists.newArrayList(Capability.HEAT)
        );

        final String data = mapper.writeValueAsString(configuration);
        final Configuration deser = mapper.readValue(data, Configuration.class);
        assertEquals("name", configuration.getName(), deser.getName());
        assertTrue(configuration.capabilities().size() == 1);
    }
}
