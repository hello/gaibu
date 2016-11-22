package is.hello.gaibu.homeauto.interfaces;

import com.google.common.base.Optional;
import com.hello.suripu.core.models.ValueRange;
import is.hello.gaibu.core.models.Configuration;
import is.hello.gaibu.core.models.ExpansionData;
import is.hello.gaibu.homeauto.models.AlarmActionStatus;

import java.util.List;

/**
 * Created by jnorgan on 9/28/16.
 */
public interface HomeAutomationExpansion {
  List<Configuration> getConfigurations();
  Optional<Configuration> getSelectedConfiguration(ExpansionData expansionData);
  Integer getDefaultBufferTimeSeconds();
  AlarmActionStatus runDefaultAlarmAction();
  AlarmActionStatus runAlarmAction(ValueRange valueRange);
}
