package gov.hhs.cms.base.common.util;

import java.util.Properties;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.management.ObjectName;
import javax.management.ObjectInstance;
import javax.management.MBeanServerConnection;

import gov.hhs.cms.base.common.util.FFEConfig;

import org.jboss.security.SecurityAssociation;
import org.jboss.security.SimplePrincipal;

public class JMXQueryUtil	{
	public static boolean isServiceInVM(String serviceCategory, String serviceName)	{
		try {
			Properties ENV = new Properties();
			ENV.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
			ENV.setProperty(Context.PROVIDER_URL, "jnp://" + FFEConfig.getProperty("service.common.my_services_jndi"));
			ENV.setProperty(Context.URL_PKG_PREFIXES, "org.jnp.interfaces");
			SecurityAssociation.setPrincipal(new SimplePrincipal(FFEConfig.getProperty("utilities.jmx.user")));
			SecurityAssociation.setCredential(FFEConfig.getProperty("utilities.jmx.pwd"));
			Context ctx = new InitialContext(ENV);
            MBeanServerConnection c = (MBeanServerConnection) ctx.lookup("jmx/invoker/RMIAdaptor");

            ObjectName obj = new ObjectName("jboss.esb:category=MessageCounter,service-category=" + serviceCategory + ",*");
            Set<ObjectInstance> set = c.queryMBeans(obj, null);
            for(ObjectInstance oi : set)	{
				if (serviceName.equals(oi.getObjectName().getKeyProperty("service-name")))
                	return true;
            }
        } 	catch (Exception ex) {
            ex.printStackTrace();
        }

		return false;
	}
}