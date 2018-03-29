package gov.hhs.cms.base.common.util;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.pool.BaseKeyedPoolableObjectFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.apache.log4j.Logger;

/**
 * Implementation which uses pooling for JAXBContext, Marshaller and Unmarshaller instances.
 * It is thread-safe and re-entrant.
 */
public class PooledJaxbHelper implements JaxbHelper {
	
	private static final Logger LOG = Logger.getLogger(PooledJaxbHelper.class);

    private static class MarshallerFactory extends BaseKeyedPoolableObjectFactory {
        @SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
        public Marshaller makeObject(Object object) throws Exception {
        	//JAXBContext jaxbContext = (JAXBContext)jaxbContextPool.borrowObject(object);
            JAXBContext jaxbContext = JaxbContextFactory.createContext((Class)object);

            Marshaller marshaller = jaxbContext.createMarshaller();
            //jaxbContextPool.returnObject(object, jaxbContext);
            
            if(LOG.isDebugEnabled()){
            	LOG.debug("Created a new marshaller for: " + ((Class)object).getName());
            }

            return marshaller;
        }
    }

    private static class UnmarshallerFactory extends BaseKeyedPoolableObjectFactory {
        @SuppressWarnings("unchecked")
		@Override
        public Unmarshaller makeObject(Object object) throws Exception {
            JAXBContext jaxbContext = JaxbContextFactory.createContext((Class)object);

            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            if(LOG.isDebugEnabled()){
            	LOG.debug("Created a new unmarshaller for: " + ((Class)object).getName());
            }
            return unmarshaller;
        }
    }

    private static class JaxbContextFactory /* extends BaseKeyedPoolableObjectFactory */ {
    	//Temporary commented out block. We need to use ehcache. For now, using a Concurrent HashMap
    	//protected static FEPSCacheBase jaxbContextCache = FEPSCacheManagerFactory.getInstance().getCache("JAXBContextCache");
    	private static ConcurrentHashMap<String,JAXBContext> jaxbContextCache = new ConcurrentHashMap<String, JAXBContext>();
    	private static final AtomicLong cacheHits = new AtomicLong();
        private static final AtomicLong cacheMisses = new AtomicLong();
    	
    	/**
         * Creates a new JAXBContext for a given package name. It does the following things:
         * 1. Get the cache key based on the classObject's class loader and the package name
         * 2. Tries to find the context in the cache
         * 3. If not available in cache, create a new context and add it to the cache.
         * 
         * JAXBContext is thread safe. There is no need to synchronize around it.
         * @param clazz Class object
         * 
         * @return JAXBContext
         * @throws JAXBException
         */
    	static <T> JAXBContext createContext(Class<T> clazz) throws JAXBException{
    		
    		if(clazz == null){
    			return null;
    		}
    		
    		//get Cache key
    		String cacheKey = getCacheKey(clazz);
    		
    		//check the cache if the context was previously created
    		JAXBContext jaxbContext = (JAXBContext)jaxbContextCache.get(cacheKey);
    		
    		if(jaxbContext == null){
    			//cache Miss. increment for stats
    			cacheMisses.incrementAndGet();
    			
    			//now create a new jaxbContext
    			jaxbContext = JAXBContext.newInstance(clazz.getPackage().getName());
    			
    			//add to Cache
    			jaxbContextCache.putIfAbsent(cacheKey, jaxbContext);
    			
    			if(LOG.isDebugEnabled()){
    				LOG.debug("Added a new entry in the JAXBContextCache (key=" + cacheKey + ")");
    			}
    		}else {
    			//found in the cache. increment for stats
    			cacheHits.incrementAndGet();
    		}
    		
    		if (LOG.isDebugEnabled()) {
                long hits = cacheHits.get();
                long misses = cacheMisses.get();
                double total = hits + misses;
                double hitPercentage = (hits / total) * 100.0;
                LOG.debug("Cache hits: " + hits + ", cache misses: " + misses + ", hit percentage: " + hitPercentage + "%");
            }
    		
    		return jaxbContext;
    	}
    	
    	/**
    	 * Create a new jaxb context cache key
    	 * 
    	 * @param clazz
    	 * @param packageName
    	 * @return cacheKey
    	 */
    	private static <T> String getCacheKey(Class<T> clazz){
    		String prefix = clazz.getClassLoader().toString();
    		return prefix + ":" + clazz.getPackage().getName();
    	}
    }
    
    /**
     * This is the marshaller and unmarshaller pool config.
     */
    private static class MarshallerUnmarshallerPoolConfig extends GenericKeyedObjectPool.Config {
	    {
	    	maxIdle = Integer.parseInt(FFEConfig.getProperty("PooledJaxbHelper.marshallerUnmarshaller.pool.max.idle.size", "3"));
            maxActive = Integer.parseInt(FFEConfig.getProperty("PooledJaxbHelper.marshallerUnmarshaller.pool.max.active.size", "5"));
            //no max limit
            maxTotal = Integer.parseInt(FFEConfig.getProperty("PooledJaxbHelper.marshallerUnmarshaller.pool.max.total.size", "50"));
            minIdle = Integer.parseInt(FFEConfig.getProperty("PooledJaxbHelper.marshallerUnmarshaller.pool.min.idle.size", "3"));
            whenExhaustedAction = GenericKeyedObjectPool.WHEN_EXHAUSTED_GROW;
            //indicates how long the eviction thread should sleep before "runs" of examining idle objects. When non-positive, no eviction thread will be launched.
            //currently set to 100 min, this can be set to -1, if no eviction is preferred.
            timeBetweenEvictionRunsMillis = Long.parseLong(FFEConfig.getProperty("PooledJaxbHelper.marshallerUnmarshaller.pool.time.between.eviction.runs.millis","600000"));
            numTestsPerEvictionRun = Integer.parseInt(FFEConfig.getProperty("PooledJaxbHelper.marshallerUnmarshaller.pool.num.tests.per.eviction.run", "50"));
            //specifies the minimum amount of time that an object may sit idle in the pool before it is eligable for eviction due to idle time. 
            //When non-positive, no object will be dropped from the pool due to idle time alone.
            //currently set to 50 min
            minEvictableIdleTimeMillis = Long.parseLong(FFEConfig.getProperty("PooledJaxbHelper.marshallerUnmarshaller.pool.min.evictable.idle.time.millis", "300000"));
	     }
    }

    /** pool of marshallers */
    private static GenericKeyedObjectPool marshallerPool = new GenericKeyedObjectPool(new MarshallerFactory(), new MarshallerUnmarshallerPoolConfig());
    /** pool of unmarshallers */
    private static GenericKeyedObjectPool unmarshallerPool = new GenericKeyedObjectPool(new UnmarshallerFactory(), new MarshallerUnmarshallerPoolConfig());

    /**
     * @see gov.hhs.cms.base.common.util.JaxbHelper#marshal(Object, boolean)
     */
    @SuppressWarnings("rawtypes")
	@Override
    public <T> String marshal(T instance, boolean includeDeclaration) throws Exception {
    	StringWriter result = new StringWriter();
    	Class clazz = (instance instanceof JAXBElement) ? ((JAXBElement)instance).getDeclaredType() : instance.getClass();
    	Marshaller marshaller = null;
    	try {
        	marshaller = (Marshaller) marshallerPool.borrowObject(clazz);
        	marshaller.setProperty("jaxb.fragment", includeDeclaration ? Boolean.FALSE : Boolean.TRUE);
            marshaller.marshal(instance, result);
            if(LOG.isDebugEnabled()){
            	LOG.debug("Marshalled instance of class : " + clazz.getName());
            }
            return result.toString();
        } catch (Exception e) {
            marshallerPool.invalidateObject(clazz, marshaller);
            throw new RuntimeException(e);
        } finally {
        	if(null != marshaller){
        		marshallerPool.returnObject(clazz, marshaller);
        		if(LOG.isDebugEnabled()){
                	LOG.debug("Returned marshaller back to the pool : " + clazz.getName());
                }
        	}
        }
    }

    /**
     * @see gov.hhs.cms.base.common.util.JaxbHelper#unmarshal(String, Class)
     */
    @SuppressWarnings("unchecked")
	@Override
    public <T> T unmarshal(String xml, Class<T> clazz) throws Exception {
        T result;
        Unmarshaller unmarshaller = null;
        try {
        	unmarshaller = (Unmarshaller) unmarshallerPool.borrowObject(clazz);
            //noinspection unchecked
            result = (T) unmarshaller.unmarshal(new StringReader(xml));
            
            if(LOG.isDebugEnabled()){
            	LOG.debug("Unmarshalled instance of class : " + clazz.getName());
            }
            
            return result;
        } catch (Exception e) {
            unmarshallerPool.invalidateObject(clazz, unmarshaller);
            throw new RuntimeException(e);
        } finally{
        	if(null != unmarshaller){
        		unmarshallerPool.returnObject(clazz, unmarshaller);
        		
        		if(LOG.isDebugEnabled()){
                	LOG.debug("Returned unmarshaller back to the pool : " + clazz.getName());
                }
        	}
        }
    }
}
