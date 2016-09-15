package is.hello.gaibu.core.stores;

import com.google.common.base.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import is.hello.gaibu.core.db.ExternalApplicationDataDAO;
import is.hello.gaibu.core.models.ExternalApplicationData;

/**
 * Created by jnorgan on 9/9/16.
 */
public class PersistentExternalAppDataStore implements ExternalApplicationDataStore<ExternalApplicationData> {
  private static final Logger LOGGER = LoggerFactory.getLogger(PersistentExternalAppDataStore.class);

  private final ExternalApplicationDataDAO externalAppDataDAO;

  public PersistentExternalAppDataStore(final ExternalApplicationDataDAO externalAppDataDAO) {
    this.externalAppDataDAO = externalAppDataDAO;

  }

  @Override
  public Optional<ExternalApplicationData> getAppData(Long applicationId, String deviceId) {
    return externalAppDataDAO.getAppData(applicationId, deviceId);
  }

  @Override
  public void insertAppData(ExternalApplicationData appData) {
    externalAppDataDAO.insertAppData(appData);
  }

  @Override
  public void updateAppData(ExternalApplicationData appData) {
    externalAppDataDAO.updateAppData(appData);
  }


}
