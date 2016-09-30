package is.hello.gaibu.core.stores;


import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

import is.hello.gaibu.core.db.ExpansionsDAO;
import is.hello.gaibu.core.models.Expansion;

public class PersistentExpansionStore implements ExpansionStore<Expansion> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersistentExpansionStore.class);

    private final ExpansionsDAO applicationsDAO;
    final LoadingCache<String, Optional<Expansion>> nameCache;
    final LoadingCache<Long, Optional<Expansion>> idCache;

    // This is called by the cache when it doesn't contain the key
    final CacheLoader nameCacheLoader = new CacheLoader<String, Optional<Expansion>>() {
        public Optional<Expansion> load(final String applicationName) {
            LOGGER.debug("Application '{}' not in cache, fetching from DB", applicationName);
            return getApplicationByNameFromDB(applicationName);
        }
    };

    final CacheLoader idCacheLoader = new CacheLoader<Long, Optional<Expansion>>() {
        public Optional<Expansion> load(final Long applicationId) {
            LOGGER.debug("Application ID '{}' not in cache, fetching from DB", applicationId);
            return getApplicationByIdFromDB(applicationId);
        }
    };

    public PersistentExpansionStore(final ExpansionsDAO applicationsDAO) {
         this.applicationsDAO = applicationsDAO;

        this.nameCache = CacheBuilder.newBuilder()
            .expireAfterWrite(60, TimeUnit.MINUTES)
            .build(nameCacheLoader);
        this.idCache = CacheBuilder.newBuilder()
            .expireAfterWrite(60, TimeUnit.MINUTES)
            .build(idCacheLoader);
    }

    public Optional<Expansion> getApplicationByIdFromDB(final Long applicationId) {
        return applicationsDAO.getById(applicationId);
    }

    @Override
    public Optional<Expansion> getApplicationById(final Long applicationId) {
        return idCache.getUnchecked(applicationId);
    }

    @Override
    public Optional<Expansion> getApplicationByClientId(final String clientId) {
        return applicationsDAO.getByClientId(clientId);
    }

    public Optional<Expansion> getApplicationByName(final String applicationName){
        return nameCache.getUnchecked(applicationName);
    }

    public Optional<Expansion> getApplicationByNameFromDB(final String applicationName) {
        return applicationsDAO.getByServiceName(applicationName);
    }

    @Override
    public List<Expansion> getAll() {
        return applicationsDAO.getAll();
    }
}
