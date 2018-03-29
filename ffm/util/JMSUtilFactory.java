package gov.hhs.cms.base.common.util;


/**
 * This class is here to enable unit testing of classes that use JMSUtil. All it does is serve as
 * a pass-through to the various JMSUtil methods for getting a specific JMSUtil instance. 
 * One of these factory objects is instantiated when the a JMSUtil object is first needed by a class,
 * or when that class is constructed. Doing it this way means that these classes can be unit tested
 * by setting the actual JMSUtilFactory to be used to a mock version. 
 * @author jhayes
 *
 */
public class JMSUtilFactory  {
	public JMSUtil getJMSUtilClusteredInstance(String jndiUrl) {
		return JMSUtil.getClusteredInstance(jndiUrl);
	}
	public JMSUtil getJMSUtilLocalInstance() {
		return JMSUtil.getLocalInstance();
	}
	public JMSUtil getJMSUtilPointToPointInstance(String jndiUrl) {
		return JMSUtil.getPointToPointInstance(jndiUrl);
	}
}