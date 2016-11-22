package is.hello.gaibu.core.models;

import java.util.List;

/**
 * Created by jnorgan on 9/27/16.
 */
public interface ExpansionDeviceData {
  void setId(final String id);
  String getId();
  void setName(final String name);
  String getName();
  List<Capability> capabilities();
  void setCapabilities(List<Capability> capabilities);
}
