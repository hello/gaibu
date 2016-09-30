package is.hello.gaibu.core.stores;

import com.google.common.base.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import is.hello.gaibu.core.db.ExpansionDataDAO;
import is.hello.gaibu.core.models.ExpansionData;

/**
 * Created by jnorgan on 9/9/16.
 */
public class PersistentExpansionDataStore implements ExternalApplicationDataStore<ExpansionData> {
  private static final Logger LOGGER = LoggerFactory.getLogger(PersistentExpansionDataStore.class);

  private final ExpansionDataDAO externalAppDataDAO;

  public PersistentExpansionDataStore(final ExpansionDataDAO externalAppDataDAO) {
    this.externalAppDataDAO = externalAppDataDAO;

  }

  @Override
  public Optional<ExpansionData> getAppData(Long applicationId, String deviceId) {
    return externalAppDataDAO.getAppData(applicationId, deviceId);
  }

  @Override
  public void insertAppData(ExpansionData appData) {
    externalAppDataDAO.insertAppData(appData);
  }

  @Override
  public void updateAppData(ExpansionData appData) {
    externalAppDataDAO.updateAppData(appData);
  }


}
