package org.andresoviedo.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.andresoviedo.util.cache.SpringTTLCache;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Classe de test de la classe IhmDao
 * 
 * @author sBoulay
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class SpringTTLCacheTest {

	@Autowired
	private IhmDao ihmDao;

	@Autowired
	private CacheManager cacheManager;

	@Configuration
	@EnableCaching
	static class SpringConfiguration {

		@Bean
		public IhmDao ihmDao() {
			return new IhmDao();
		}

		// this bean will be injected into the OrderServiceTest class
		@Bean
		public CacheManager cacheManager() {
			SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
			List<Cache> caches = new ArrayList<Cache>();
			caches.add(new SpringTTLCache("junit", 1));
			simpleCacheManager.setCaches(caches);
			return simpleCacheManager;
		}
	}

	@Before
	public void setUpDatabase() throws Exception {
		Map<String, Ihm> store = new HashMap<String, Ihm>();
		Ihm ihm1 = new Ihm();
		ihm1.setId(1L);
		ihm1.setDomaine("priv");
		ihm1.setNom("ihm1");
		Map<String, IhmProperty> properties = new HashMap<>();
		properties.put("prop1", new IhmProperty(1L, "prop1", "valeur1"));
		properties.put("prop2", new IhmProperty(2L, "prop2", "valeur2"));
		ihm1.setProperties(properties);
		store.put("priv.ihm1", ihm1);
		ihmDao.setStore(store);
	}

	/**
	 * Test de la mÃ©thode permettant de rÃ©cupÃ©rer les ihm par cle
	 * fonctionnelle en testant l'estructure du entitÃ©
	 */
	@Test
	public void testIhmEntity() {

		// Appel de la mÃ©thode
		final Ihm ihm1 = ihmDao.getIhm("priv", "ihm1");

		// VÃ©rifications
		Assert.assertNotNull(ihm1);
		Assert.assertEquals((Long) 1L, ihm1.getId());
		Assert.assertEquals("priv", ihm1.getDomaine());
		Assert.assertEquals("ihm1", ihm1.getNom());
		Assert.assertNotNull(ihm1.getProperties());
		Assert.assertEquals(2, ihm1.getProperties().size());
		final Map<String, IhmProperty> proprietes = ihm1.getProperties();
		final IhmProperty prop1 = proprietes.get("prop1");
		final IhmProperty prop2 = proprietes.get("prop2");
		Assert.assertEquals((Long) 1L, prop1.getId());
		Assert.assertEquals("prop1", prop1.getNom());
		Assert.assertEquals("valeur1", prop1.getValeur());
		Assert.assertEquals((Long) 2L, prop2.getId());
		Assert.assertEquals("prop2", prop2.getNom());
		Assert.assertEquals("valeur2", prop2.getValeur());
	}

	/**
	 * Test cache hibernate L2
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void testCacheL2() {

		// validate we have cache configured for dao.ihm
		Assert.assertNull(cacheManager.getCache("default"));
		Assert.assertNotNull(cacheManager.getCache("junit"));

		// get current status
		final long initialFetchCount = ihmDao.getQueryExecutionCount();

		// test cache
		ihmDao.getIhm("junit", "ihm2");

		// validate we have accessed to database
		Assert.assertEquals(initialFetchCount + 1, ihmDao.getQueryExecutionCount());

		// query cache
		ihmDao.getIhm("junit", "ihm2");
		ihmDao.getIhm("junit", "ihm2");
		ihmDao.getIhm("junit", "ihm2");

		// assert pas de requetes sql
		Assert.assertEquals(initialFetchCount + 1, ihmDao.getQueryExecutionCount());
	}

	@Test
	public void testCacheExpiration() throws InterruptedException {

		// get non-existent entity
		Ihm ihm = ihmDao.getIhm("cache", "new_ihm");
		Assert.assertNull(ihm);

		ihm = new Ihm();
		ihm.setId(888L);
		ihm.setDomaine("cache");
		ihm.setNom("new_ihm");
		ihmDao.update(ihm);

		ihm = ihmDao.getIhm("cache", "new_ihm");
		Assert.assertNull(ihm);

		// test expiration
		Thread.sleep(1111);

		ihm = ihmDao.getIhm("cache", "new_ihm");
		Assert.assertNotNull(ihm);
	}

}