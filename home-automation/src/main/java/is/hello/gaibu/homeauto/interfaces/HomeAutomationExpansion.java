package is.hello.gaibu.homeauto.interfaces;

import com.google.common.base.Optional;

import com.hello.suripu.core.models.ValueRange;

import java.util.List;

import is.hello.gaibu.core.models.Configuration;
import is.hello.gaibu.core.models.ExpansionData;

/**
 * Created by jnorgan on 9/28/16.
 */
public interface HomeAutomationExpansion {
  List<Configuration> getConfigurations();
  Optional<Configuration> getSelectedConfiguration(ExpansionData expansionData);
  Integer getDefaultBufferTimeSeconds();
  Boolean runDefaultAlarmAction();
  Boolean runAlarmAction(ValueRange valueRange);
}
