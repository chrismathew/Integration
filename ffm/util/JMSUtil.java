package gov.hhs.cms.base.common.util;

import org.apache.log4j.Logger;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class intended to abstract out details related to sending and receiving JMS messages to/from a highly available (yet not in the
 * active-backup HornetQ sense) JMS cluster, a non-clustered JMS node or an in-VM local provider The same simple API is used throughout.
 *
 * The implementation takes into account HornetQ-specific idiosyncrasies, such as the fact that it does not allow for live-live failover
 * when the application would be fine with it, or the fact that it does not offer transparent failover. JMSUtil offers these features.
 *
 * All the application needs to do is to:
 *
 * 1) Create a JMSUtil instance for the target cluster or localhost, using one of JMSUtil.getLocalInstance(),
 * JMSUtil.getPointToPointInstance(<target-node-jndi-url>) or JMSUtil.getClusteredInstance(<jndi-url-comma-separated-list>) methods.
 * The method documentation contains more details on the semantics of each:
 *
 * @see JMSUtil#getLocalInstance()
 * @see JMSUtil#getPointToPointInstance(String)
 * @see JMSUtil#getClusteredInstance(String)
 *
 * 2) Invoke send(...)/receive(...) methods on the JMSUtil instance.
 *
 * The underlying JMS Connections are "lazy initialized", in that connections are created only when they are needed (when the first message
 * is being sent or a receive() invocation is fielded). This gives us the flexibility to delay the startup of the JMS clusters - the
 * absence of the JMS cluster won't stop the client boot process.
 *
 * "localhost" situation.
 *
 * We may find ourselves in the situation when we need consistent JMSUtil API access to the local JMS provider. In this case, we do not need
 * failover, load balancing, or cluster-related behavior in any way, and JMSUtil performs internal optimizations, such as using in-vm
 * connection factories. To get such an instance, use JMSUtil.getLocalInstance().
 *
 * DO NOT use JMSUtil.getPointToPointInstance("localhost:1099") or JMSUtil.getClusteredInstance("localhost:1099") to get a local instance,
 * because you will get an instance with more functionality than you need and that will behave non-optimally for your use case. It will
 * still work, though.
 *
 * Cluster Topology Discovery.
 *
 * Not *all* cluster nodes must be specified in the JNDI URL list the JMSUtil instance is created with. The implementation has built-in
 * mechanisms allowing it to dynamically learn about updated cluster topology. In order to activate dynamic discovery, set
 * "jmsutil.discovery.interval" to the value of the discovery interval (in ms) *BEFORE* the first getInstance(...) invocation. For a
 * localhost instance, discovery is permanently disabled, even if "jmsutil.discovery.interval" is found to have a positive integral value.
 *
 * Message Time-to-Live
 *
 * JMS semantics can be controlled via the 'properties' map: in order to specify the time-to-live for a specific message, set the
 * "JMS_MESSAGE_TIME_TO_LIVE" (JMSUtil.JMS_MESSAGE_TIME_TO_LIVE) property to a Long value (or a primitive long) representing the time to
 * live in milliseconds, before invoking send().
 *
 * Notes:
 *
 * 1. Any failure in attempting to create a session, producer or sending/receiving a message will invalidate the entire connection pool for
 *    the node in question and no further connection attempts will be performed for that node, unless the discovery mechanism is active
 *    and the discovery process retrieves a new view that includes the node.
 *
 * What JMSUtil does NOT DO:
 *
 * 1. receive(...) does not provide transparent fail-over yet, a node failure would propagate an exception to application. Transparent
 *    failover can be added if necessary.
 *
 * 2. IP addresses and host names are handled as different strings, even if a specific host name and IP address are designating the same
 *    host/interface.
 *
 * 3. Does not address sending/receiving messages in a transaction (JMS API or JTA transactions). This can be added though if necessary.
 *
 * 4. Cleanly shut down the active connections on JVM shutdown (lifecycle methods?)
 *
 * TODO:
 *
 * o Implement receive(...) transparent failover.
 *
 * o Implement tracing.
 *
 * o 04/22/13 Bruce's suggestion: "[...] it'd be awesome if the load balancing chose nodes based on server health (how many tasks were
 *   currently out on each node, e.g.?)".
 *
 * Testing
 *
 * mvn -Dmaven.surefire.debug="-Xrunjdwp:transport=dt_shmem,server=y,suspend=y,address=mvn" test -Dtest=JMSUtilJNDICachingTest
 *
 * @version - see VERSION.
 *
 * @author <a href="mailto:ovidiu@cgifederal.com">Ovidiu Feodorov</a>
 */
public class JMSUtil
{
    // Constants ---------------------------------------------------------------------------------------------------------------------------

    public static final String VERSION = "2.0.8";

    private static final Logger log = Logger.getLogger(JMSUtil.class);
    private static final boolean debug = log.isDebugEnabled();

    public static final String JMS_CONNECTION_FACTORY_JNDI_NAME_PROPERTY_NAME = "jmsutil.jms.connection.factory.jndi.name";
    public static final String DEFAULT_JMS_CONNECTION_FACTORY_JNDI_NAME = "/ConnectionFactory";
    public static final String DEFAULT_LOCAL_JMS_CONNECTION_FACTORY_JNDI_NAME = "java:/ConnectionFactory";

    public static final String CONNECTION_COUNT_PROPERTY_NAME = "jmsutil.connection.count";
    public static final int DEFAULT_CONNECTION_COUNT = 2;

    public static final String TOPOLOGY_DISCOVERY_INTERVAL_PROPERTY_NAME = "jmsutil.discovery.interval";

    // there is no default topology discovery interval, not enabled by default, it has to be specifically enabled with an integral value

    public static final String TOPOLOGY_JNDI_LOCATION_PROPERTY_NAME = "jmsutil.topology.jndi.location";
    public static final String DEFAULT_TOPOLOGY_JNDI_LOCATION = "/topology";

    public static final String JNDI_INITIAL_CONTEXT_FACTORY_PROPERTY_NAME = "jmsutil.jndi.initial.context.factory";
    public static final String DEFAULT_JNDI_INITIAL_CONTEXT_FACTORY = "org.jnp.interfaces.NamingContextFactory";

    public static final String JNDI_URL_PKG_PREFIXES_PROPERTY_NAME = "jmsutil.jndi.url.pkg.prefixes";
    public static final String DEFAULT_JNDI_URL_PKG_PREFIXES = "jboss.naming:org.jnp.interfaces";

    public static final String JMS_MESSAGE_TIME_TO_LIVE = "JMS_MESSAGE_TIME_TO_LIVE";

    private static final String LOCALHOST_LITERAL = "localhost";
    private static final String JNP_PROTOCOL_LITERAL = "jnp://";

    // Static ------------------------------------------------------------------------------------------------------------------------------

    private static final Map<Key, JMSUtil> instances = new HashMap<Key, JMSUtil>();

    /**
     * A local instance does not attempt any retry or failover whatsoever - and this is because if the co-located JMS provider fails,
     * then we assume anything running in that JVM is not to be trusted. Any failure will propagate to the client code and if the client
     * decides to continue, it will have to recreate the local instance. There is no dynamic discovery of any kind.
     */
    public static JMSUtil getLocalInstance()
    {
        return getInstance(Type.LOCAL, null);
    }

    /**
     * A point-to-point instance implies connection to one and only one, non-clustered JMS provider node. There is no failover to an
     * equivalent node, since the provider is assumed non-clustered. If connection failure occurs asynchronously, all connections in cache
     * are dropped, and the instance attempts to recreate them during the next messaging operation. If failure occurs during a messaging
     * operations, the connections will be dropped as well, and the exception will be propagated to the client code. However, a subsequent
     * messaging operation on the same instance operation will trigger a connection re-creation attempt. There is no dynamic discovery of
     * any kind.
     *
     * This semantics is most useful when we need to make sure that a message gets to a specific node, and that node only, and it is not
     * re-rerouted to equivalent nodes.
     *
     * @exception IllegalArgumentException if the jndiUrl does not designate a single, non-clustered provider.
     */
    public static JMSUtil getPointToPointInstance(String jndiUrl)
    {
        return getInstance(Type.POINT2POINT, jndiUrl);
    }

    /**
     * An instance that assumes the existence of a cluster of equivalent JMS provider nodes. The instance provides load balancing among
     * existing nodes, transparent failover among nodes and automatic node discovery, when previously failed (or new nodes) come on-line.
     * The JNDI URL list argument should contain the JNDI URLs for *some* of the cluster nodes. JMSUtil will learn of additional nodes via
     * a discovery process. For details on how to enable the discovery process and how that works, see "Cluster Topology Discovery".
     *
     * @param jndiUrls - the comma-separated JNDI URL list of *some* of the cluster nodes (the "seed" URLs). Further nodes, if they exist
     *        or come back online after the JMSUtil instance initialization, may be discovered dynamically.
     *
     * @exception IllegalArgumentException if the jndiUrls has an invalid value.
     */
    public static JMSUtil getClusteredInstance(String jndiUrls)
    {
        return getInstance(Type.CLUSTERED, jndiUrls);
    }

    /**
     * @param commaSeparatedJndiUrls - a comma-separated list of JNDI URLs. This is also the format used to advertise the topology in JNDI
     * by a peer service.
     */
    public static List<String> jndiUrlsToList(String commaSeparatedJndiUrls)
    {
        if (commaSeparatedJndiUrls == null)
        {
            throw new IllegalArgumentException("null JNDI URL list");
        }

        List<String> result = new ArrayList<String>();

        for(StringTokenizer st = new StringTokenizer(commaSeparatedJndiUrls, ","); st.hasMoreTokens(); )
        {
            String s = st.nextToken().trim();

            if (s.startsWith(JNP_PROTOCOL_LITERAL))
            {
                s = s.substring(JNP_PROTOCOL_LITERAL.length());
            }

            if (s.length() > 0)
            {
                result.add(s);
            }
        }

        return result;
    }

    /**
     * This method has been added to provide backing for a command-line tool that can be used for operational testing. It exposes JMSUtil
     * behavior without the need to stand up a full SOA-P instance and deploy an application that uses JMSUtil. System.out.println() makes
     * the presence of a log4j.xml unnecessary, in the same context. The command line can be simply "built" by adding base-common-core.jar
     * to a JVM classpath.
     */
    public static void main(String[] args) throws Exception
    {
        if (args.length == 0)
        {
            System.out.println("usage: jmsutil jnp://node1:port1,node2:port2 [LOCAL|POINT2POINT|CLUSTERED");
            return;
        }

        // the second argument should specify type, otherwise we assume CLUSTERED

        Type t = Type.CLUSTERED;

        if (args.length >= 2)
        {
            t = Type.valueOf(args[1]);
        }

        JMSUtil jms = JMSUtil.getInstance(t, args[0]);
        new CommandLineLoop(jms).readStdin();
    }

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    private Type type;

    // the initial population - must not change for the duration of this instance's life; will be initialized as unmodifiable set
    private Set<String> initialNodes;

    private String jmsConnectionFactoryJndiName;

    /**
     * The number of JMS Connection instances that are maintained for a specific node.
     */
    private int connectionCount;

    private long topologyDiscoveryInterval;
    private String topologyJndiLocation;

    private String jndiInitialContextFactory;
    private String jndiUrlPkgPrefixes;

    private LoadBalancingPolicy loadBalancingPolicy;

    /**
     * A cache of JMS connections - we maintain a list of JMS connections for each cluster node keyed on its JNDI URL.
     *
     * TODO we can optimize for localhost, as there won't be other nodes to look up
     */
    private Map<String, List<ConnectionContext>> connections;

    private Timer clusterTopologyTimer;
    private ClusterTopologyDiscoveryTask clusterTopologyDiscoveryTask;

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    /**
     * No initialization (attempt to get a hold on external JMS resources takes place inside the constructor; the JMS resources are lazy
     * initialized.
     *
     * @param seed - the set of JNDI URLs of the initial nodes of the cluster we want to connect to or "localhost", in which case the
     *        instance will intermediate access to the local JMS provider.
     *
     * @param connectionCount the number of active JMS connections maintained per node (or for local access).
     *
     * @param topologyDiscoveryInterval - the interval (in milliseconds) between successive runs of the cluster topology discovery. If zero
     *        or negative value, the topology discovery is disabled.
     *
     * @param topologyJndiLocation - the JNDI name to look up to get the cluster topology. Ignored if topology discovery is not enabled.
     *
     * @param jndiInitialContextFactory - the initial context factory. Useful for testing, and for situations the code runs with a different
     *        provider than JBoss.
     *
     * @param jndiUrlPkgPrefixes - the JNDI URL package prefixes. Useful for testing, and for situations the code runs with a different
     *        provider than JBoss.
     *
     * @exception IllegalArgumentException unacceptable url (such as a null value, empty value, etc.)
     */
    private JMSUtil(Type type,
                    Set<String> seed,
                    String jmsConnectionFactoryJndiName,
                    int connectionCount,
                    long topologyDiscoveryInterval,
                    String topologyJndiLocation,
                    String jndiInitialContextFactory,
                    String jndiUrlPkgPrefixes)
    {

        this.type = type;

        this.jmsConnectionFactoryJndiName = jmsConnectionFactoryJndiName;
        this.connectionCount = connectionCount;
        this.topologyDiscoveryInterval = topologyDiscoveryInterval;
        this.topologyJndiLocation = type.isClustered() ? topologyJndiLocation : null;
        this.jndiInitialContextFactory = jndiInitialContextFactory;
        this.jndiUrlPkgPrefixes = jndiUrlPkgPrefixes;

        this.loadBalancingPolicy = new LoadBalancingPolicy();

        this.connections = new HashMap<String, List<ConnectionContext>>();

        // pre-fill the connection map with the nodes we know of

        if (type.isLocal())
        {
            seed = new HashSet<String>();
            seed.add("localhost");
        }
        else if (seed.size() == 0)
        {
            throw new IllegalArgumentException("attempt to initialize JMSUtil with an empty JNDI URL set");
        }

        this.initialNodes = Collections.unmodifiableSet(seed);

        for(String in: initialNodes)
        {
            connections.put(in, new ArrayList<ConnectionContext>());
        }

        if (topologyDiscoveryInterval <= 0)
        {
            log.debug(this + " cluster topology discovery disabled");
        }
        else if (type.isLocal() || type.isPointToPoint())
        {
            log.warn("cluster topology discovery interval (" + topologyDiscoveryInterval +
                     " ms) overridden for a " + type + " instance, cluster topology discovery forcibly disabled");

            this.topologyDiscoveryInterval = -2L;
        }
        else
        {
            // register a timer that periodically will check for new additions to the cluster

            clusterTopologyTimer = new Timer("JMSUtil Cluster Topology Discovery", true);
            clusterTopologyDiscoveryTask = new ClusterTopologyDiscoveryTask(this);
            clusterTopologyTimer.schedule(clusterTopologyDiscoveryTask,
                                          topologyDiscoveryInterval,
                                          topologyDiscoveryInterval);

            log.debug(this + " cluster topology discovery enabled, discovery interval " + topologyDiscoveryInterval + " ms");
        }

        log.debug(this + " (version " + VERSION + ") instance created, connection factory JNDI name: " + this.jmsConnectionFactoryJndiName);
    }

    // Public ------------------------------------------------------------------------------------------------------------------------------

    /**
     * Use this method to send JMS message to the JMS cluster this JMSUtil instance fronts. The* messages will be distributed across
     * available nodes.
     *
     * The method will always succeed as long there's at least one operational JMS node in the cluster: failure when sending message
     * to a node will be caught and send will retry in a loop.
     *
     * JMS semantics can be controlled via the 'properties' map: in order to specify the time-to-live for a specific message, set the
     * "JMS_MESSAGE_TIME_TO_LIVE" (JMSUtil.JMS_MESSAGE_TIME_TO_LIVE) property with a Long value representing the time to live in
     * milliseconds.
     *
     * @param destination - the JNDI name of the JMS destination to send to.
     * @param payload - the ObjectMessage payload
     * @param type - one of JMSUtil.MessageType values: BYTE, MAP, OBJECT, STREAM, TEXT
     * @param jmsCorrelationId - the JMSCorrelationID
     * @param properties - a map containing the properties to be set on the outgoing message. null is acceptable if there are no additional
     *        properties to set. Also see the comments on controlling JMS semantics, above.
     * @param persistent - whether the message is configured to be persistent or not.
     *
     * @throws NameNotFoundException in case the destination is not deployed on the node we're attempting to send the message to.
     *
     * @throws Exception there is no JMS cluster node to perform initialization on, or there is not JMS cluster node left to handle the
     *         submission.
     */
    public void send(String destination,
                     MessageType type,
                     Serializable payload,
                     String jmsCorrelationId,
                     Map<String, Object> properties,
                     boolean persistent)
        throws Exception
    {
        if (debug) { log.debug(this + " sending message (JMSCorrelationID " + jmsCorrelationId + ") to " + destination); }

        // loop as long there are non-excluded nodes to send to

        Set<String> excludedNodes = new HashSet<String>();

        while(true)
        {
            ConnectionContext cc = getConnectionContext(excludedNodes);

            if (cc == null)
            {
                if (isClustered())
                {
                    throw new WholeClusterDownException(this + " cannot establish a JMS connection to any node in the cluster, are all nodes down?");
                }
                else
                {
                    throw new TargetNodeDownException(this + " cannot establish a JMS connection to target, is the target down?" );
                }
            }

            // not a remote call, the initial context is cached, won't interrupt the send attempt
            Context ic = cc.getNodeJndiContext();

            Destination d = null;
            Connection c = null;
            Session s = null;

            try
            {
                // let the exception bubble up in case the destination is not found on the target node, this is not an error caused by an
                // invalid JMS provider state, but a configuration error we cannot fail over from
                d = (Destination)ic.lookup(destination);

                c = cc.getConnection();

                s = c.createSession(false, Session.AUTO_ACKNOWLEDGE);

                MessageProducer p = s.createProducer(d);

                Message m = createMessage(type, payload, s);

                // default spec-mandated behavior is to create PERSISTENT messages

                if (!persistent)
                {
                    m.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
                }

                if (jmsCorrelationId != null)
                {
                    m.setJMSCorrelationID(jmsCorrelationId);
                }

                long timeToLive = getTimeToLive(properties); // zero means "live forever"

                setMessageProperties(m, properties);

                p.send(m, p.getDeliveryMode(), p.getPriority(), timeToLive);

                if (debug)
                {
                    try
                    {
                        log.debug("message " + m + " (id=" + m.getJMSMessageID() + ", correlation id=" + m.getJMSCorrelationID() +
                                  ") sent over connection " + cc.getNode() + ":" + c);
                    }
                    catch(Exception e)
                    {
                        log.error("debug statement threw exception", e);
                    }
                }

                // message submission successful, exit the loop
                return;
            }
            catch(IllegalArgumentException e)
            {
                // let the exception bubble up, this is not a failover situation, but a programming error, it makes no sense to throw
                // away nodes and connections, just cleanly close the session
                throw e;
            }
            catch(NullPointerException e)
            {
                // let the exception bubble up, this is not a failover situation, but a programming error, it makes no sense to throw
                // away nodes and connections, just cleanly close the session
                throw e;
            }
            // ovidiu 06/14/13 - we encountered situations when naming exceptions are result of the node state degradation, not
            // misconfiguration so we need to handle this as any other failure we need to fail over from
//            catch(NameNotFoundException e)
//            {
//                // let the exception bubble up in case the destination is not found on the target node, this is not an error caused by an
//                // invalid JMS provider state, but a configuration error we cannot fail over from
//                throw e;
//            }
            catch(Exception e)
            {
                String node = cc.getNode();

                // anything that interferes with a message submission invalidates the connection and the node
                // (maybe this is a bit too drastic?)

                log.warn("message submission failed for " + node + ":" +  c + ", expelling " + node +
                         " from the list of those we maintain connections to. More details on failure below:", e);

                removeNode(node);
                excludedNodes.add(node);

                // we will retry with the another node, if any
            }
            finally
            {
                if (s != null)
                {
                    try
                    {
                        s.close();
                    }
                    catch(Exception e)
                    {
                        log.warn("failed to close session " + s, e);
                    }
                }
            }
        }
    }

    /**
     * @see JMSUtil#send(String, JMSUtil.MessageType, Serializable, String, Map, boolean)
     */
    @SuppressWarnings({"UnusedDeclaration"})
    public void send(String destination, MessageType type, Serializable payload, String jmsCorrelationId, Map<String, Object> properties)
        throws Exception
    {
        send(destination, type, payload, jmsCorrelationId, properties, true);
    }

    /**
     * @see JMSUtil#send(String, JMSUtil.MessageType, Serializable, String, Map, boolean)
     */
    public void send(String destination, JMSUtil.MessageType type, Serializable payload, String jmsCorrelationId) throws Exception
    {
        send(destination, type, payload, jmsCorrelationId, null, true);
    }

    /**
     * @see JMSUtil#send(String, JMSUtil.MessageType, Serializable, String, Map, boolean)
     */
    public void send(String destination, JMSUtil.MessageType type, Serializable payload) throws Exception
    {
        send(destination, type, payload, null, null, true);
    }

    public Message receive(String destination, String jmsCorrelationId, long timeout) throws Exception
    {
        if (debug) { log.debug(this + " receiving message from " + destination + ", timeout " + timeout +
                               ", correlation ID " + jmsCorrelationId); }

        ConnectionContext cc = getConnectionContext(null);

        if (cc == null)
        {
            throw new WholeClusterDownException(this + " cannot get a JMS connection, are all JMS cluster nodes down?");
        }

        Context ic = cc.getNodeJndiContext();

        // let the exception bubble up in case the destination is not found on the target node, this is not an error caused by an invalid
        // state of the JMS provider, but most likely a configuration error - the queue was not deployed
        Destination d = (Destination)ic.lookup(destination);

        Connection c = null;

        Session s = null;

        Message m = null;

        try
        {
            c = cc.getConnection();

            s = c.createSession(false, Session.AUTO_ACKNOWLEDGE);

            String selector = jmsCorrelationId == null ? null : "JMSCorrelationID='" + jmsCorrelationId + "'";

            MessageConsumer mc = s.createConsumer(d, selector);

            long t0 = System.currentTimeMillis();

            m = mc.receive(timeout);

            long elapsed = System.currentTimeMillis() - t0;

            if (debug) { log.debug("received " + m + " in " + elapsed + " ms"); }
        }
        catch(Exception e)
        {
            String node = cc.getNode();

            // anything that interferes with message reception invalidates the connection and the node (maybe this is a bit too drastic?)
            log.warn("reception failed for " + node + ":" +  c + ", expelling " + node +
                     " from the list of those we maintain connections to. More details on failure below:", e);

            this.removeNode(node);

            // bubble the exception up - we could retry (?) (TODO)
            throw e;
        }
        finally
        {
            if (s != null)
            {
                try
                {
                    s.close();
                }
                catch(Exception e)
                {
                    log.warn("failed to close session " + s, e);
                }
            }
        }

        return m;
    }

    public boolean isLocal()
    {
        return type.isLocal();
    }

    public boolean isPointToPoint()
    {
        return type.isPointToPoint();
    }

    public boolean isClustered()
    {
        return type.isClustered();
    }

    public int getConnectionCount()
    {
        return connectionCount;
    }

    public String getJmsConnectionFactoryJndiName()
    {
        return jmsConnectionFactoryJndiName;
    }

    public long getTopologyDiscoveryInterval()
    {
        return topologyDiscoveryInterval;
    }

    public String getTopologyJndiLocation()
    {
        return topologyJndiLocation;
    }

    public String getJndiInitialContextFactory()
    {
        return jndiInitialContextFactory;
    }

    public String getJndiUrlPkgPrefixes()
    {
        return jndiUrlPkgPrefixes;
    }

    /**
     * Attempts to close all opened ConnectionFactory and clean resources, shut down the topology
     * discovery pinger, etc. A JMSUtil instance is not intended to be re-used again after closure.
     * Failures are reported as warnings in logs, the method doesn't throw any exception.
     */
    public void close()
    {
        if (connections == null)
        {
            return;
        }

        try
        {
            synchronized (connections)
            {
                for(Iterator<String> i = connections.keySet().iterator(); i.hasNext(); )
                {
                    String n = i.next();
                    List<ConnectionContext> ccs = connections.get(n);
                    i.remove();

                    for(Iterator<ConnectionContext> j = ccs.iterator(); j.hasNext(); )
                    {
                        ConnectionContext cc = j.next();

                        j.remove();

                        try
                        {
                            cc.close();
                        }
                        catch(Throwable t)
                        {
                            log.warn("failed to close " + cc, t);
                        }
                    }
                }
            }

            stopTopologyDiscovery();

            connections = null;

            log.debug(this + " closed");
        }
        catch(Throwable t)
        {
            log.warn("failed to close " + this, t);
        }
    }

    public String statusToString()
    {
        StringBuffer sb = new StringBuffer();

        synchronized (connections)
        {
            if (connections.isEmpty())
            {
                sb.append("no nodes").append("\n");
            }
            else
            {
                for(String s: connections.keySet())
                {
                    sb.append(s).append("\n");

                    List<ConnectionContext> ccs = connections.get(s);

                    if (ccs.isEmpty())
                    {
                        sb.append("        ").
                            append("no connections").append("\n");
                    }
                    else
                    {
                        for(ConnectionContext cc: ccs)
                        {
                            sb.append("        ").
                                append(cc.getId()).append(" ").
                                append(cc.getConnection()).append("\n");
                        }
                    }
                }
            }
        }

        sb.append("\n");

        sb.append(clusterTopologyTimer == null ?
                  "topology discovery disabled" :
                  "topology discovery enabled, interval " + topologyDiscoveryInterval + " ms").
            append("\n");

        return sb.toString();
    }

    @Override
    public String toString()
    {
        return type.toString() + "-JMSUtil" +
               (type.isLocal() ?
                "[" + Integer.toHexString(System.identityHashCode(this)) + "]" :
                initialNodes + "-" + (connections == null ? "[]" : connections.keySet()));
    }

    // Package protected -------------------------------------------------------------------------------------------------------------------

    /**
     * Cleans up the instance map; useful for testing.
     */
    static synchronized void cleanup()
    {
        if (instances != null)
        {
            for(JMSUtil jms: instances.values())
            {
                jms.close();
            }
        }

        instances.clear();
    }

    /**
     * Creates connections to the specified node and loads them into the given list instance.
     *
     * DOES NOT throw exception - if it cannot create connections as expected (node down, no such
     * connection factory, authentication problems, etc, log the issues in as many details as
     * possible and return an empty list
     *
     * @param connectionContexts - must be an empty list - if not, throw an IllegalStateException,
     *        to signal that someone else did not do their job - closing and cleaning dangling
     *        connections.
     *
     * @param count - the number of connection instances to create.
     *
     * @throws IllegalStateException if the connection list is not empty.
     *
     */
    static void connect(Type type, final String nodeJndiUrl, String initialContextFactory,
                        String urlPkgPrefixes, String connectionFactoryJndiName,
                        List<ConnectionContext> connectionContexts, int count,
                        final JMSUtil util)
    {
        if (connectionContexts == null)
        {
            throw new IllegalArgumentException("null connection context list");
        }

        if (!connectionContexts.isEmpty())
        {
            throw new IllegalStateException("passed connection context list is supposed to be empty, but it's not: " + connectionContexts);
        }

        log.debug("creating " + count + " JMS connection(s) " + (type.isLocal() ? " locally" : "to node " + nodeJndiUrl) +
                  " using connection factory " + connectionFactoryJndiName);

        try
        {
            InitialContext ic = null;

            if (type.isLocal())
            {
                // use whatever InitialContext configuration is found in the environment - we're local
                ic = new InitialContext();
            }
            else
            {
                Properties p = new Properties();
                p.put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
                p.put(Context.URL_PKG_PREFIXES, urlPkgPrefixes);
                p.put(Context.PROVIDER_URL, nodeJndiUrl);

                ic = new InitialContext(p);
            }

            CachingContext cachingNamingContext = new CachingContext(ic);

            ConnectionFactory cf = (ConnectionFactory)cachingNamingContext.lookup(connectionFactoryJndiName);

            for(int i = 0; i < count; i ++)
            {
                Connection c = cf.createConnection();
                log.debug("created connection " + c + (type.isLocal() ? " locally" : " to " + nodeJndiUrl));

                // this is the listener that will kick in on server troubles and clean up the
                // our cache of invalid connections
                c.setExceptionListener(new ExceptionListener()
                {
                    private String node = nodeJndiUrl;
                    private JMSUtil jmsUtil = util;

                    //@Override
                    public void onException(JMSException e)
                    {
                        log.warn(
                            "node " + node +
                            " seems to be experiencing problems, we're discarding all JMS cached connections to it. More details in the exception stack trace, below:", e);

                        try
                        {
                            jmsUtil.removeNode(node);
                        }
                        catch(Throwable t)
                        {
                            log.error("failed to cleanly discard cached JMS connections", t);
                        }
                    }
                });

                c.start();

                log.debug("started connection " + c);

                connectionContexts.add(new ConnectionContext(i, c, cachingNamingContext, nodeJndiUrl));
            }

            log.debug(connectionContexts.size() + " JMS connections successfully created to " + nodeJndiUrl);
        }
        catch(Throwable t)
        {
            log.warn(
                "failed to create JMS connection(s) to " + nodeJndiUrl + ", the cause is reflected by the embedded exception, below:", t);

            // clean up

            for(ConnectionContext cc: connectionContexts)
            {
                try
                {
                    cc.close();
                }
                catch(Exception e)
                {
                    log.debug("failed to close connection context " + cc, e);
                }
            }

            connectionContexts.clear();
        }
        finally
        {
            // noop - DO NOT close the Context, as it is still referred to from ConnectionContexts - will be closed when those contexts are
            // closed.
        }
    }

    /**
     * Put on hold because we need jmx-console credentials in order to make the call and those
     * won't be available on the client side. Aside from that, we need JBoss-specific API to
     * compile it (SecurityAssociation).
     *
     * We may resume work on this in the future.
     *
     * @throws Exception
     */
//    static List<String> getTopologyViaJmxRmiCall(InitialContext ic) throws Exception
//    {
//        Object rmiInvoker = ic.lookup("/jmx/invoker/RMIAdaptor");
//
//        log.debug(rmiInvoker);
//
//        MBeanServerConnection mbs = (MBeanServerConnection)rmiInvoker;
//
////        SecurityAssociation.setPrincipal(new SimplePrincipal("admin"));
////        SecurityAssociation.setCredential("blahblah");
//
//        ObjectName haPartitionON =
//            new ObjectName("jboss:service=HAPartition,partition=PreProd_Data_Cluster");
//
//        Object o = mbs.getAttribute(haPartitionON, "CurrentView");
//
//        log.debug(o);
//
//        // etc, etc
//
//        throw new Exception("NOT YET IMPLEMENTED");
//    }

    /**
     * We rely on a server side service that periodically publishes the most up-to-date cluster view, in the format "jndiUrl1,jndiUrl2, ..."
     *
     * @param log - makes possible logging with the Logger of the instance that made the call, and allows for finer filtering. If null
     *              it will use JMSUtil log.
     *
     * @see JMSUtil#jndiUrlsToList(String)
     */
    static List<String> getTopologyFromJndi(Context ic, Logger log) throws Exception
    {
        String topology = (String)ic.lookup(DEFAULT_TOPOLOGY_JNDI_LOCATION);

        (log == null ? JMSUtil.log : log).debug("topology received from remote node: " + topology);

        return jndiUrlsToList(topology);
    }

    /**
     * @throws JMSException if session reacts badly
     * @throws IllegalArgumentException mismatched parameters.
     */
    static Message createMessage(MessageType type, Serializable payload, Session s) throws JMSException
    {
        if (MessageType.OBJECT.equals(type))
        {
            return s.createObjectMessage(payload);
        }
        else if (MessageType.TEXT.equals(type))
        {
            if (!(payload instanceof String))
            {
                throw new IllegalArgumentException(
                    "cannot create a TEXT message while being provided a " + (payload == null ? null : payload.getClass().getName()));
            }

            return s.createTextMessage((String)payload);
        }
        else if (MessageType.BYTE.equals(type))
        {
            throw new IllegalArgumentException("unknown message tyoe " + type);
        }
        else if (MessageType.MAP.equals(type))
        {
            throw new IllegalArgumentException("unknown message tyoe " + type);
        }
        else if (MessageType.STREAM.equals(type))
        {
            throw new IllegalArgumentException("unknown message tyoe " + type);
        }
        else
        {
            throw new IllegalArgumentException("unknown message tyoe " + type);
        }
    }

    /**
     * @param excluded - null is a valid value.
     *
     * May return null if everything is excluded.
     */
    static Set<String> exclude(Set<String> base, Set<String> excluded)
    {
        if (excluded == null)
        {
            return base;
        }

        if (base == null)
        {
            return null;
        }

        Set<String> result = new HashSet<String>();

        for(String s: base)
        {
            if (!excluded.contains(s))
            {
                result.add(s);
            }
        }

        return result;
    }

    /**
     * The method has the side effect to remove the property from the map, if it exists. This is done to prevent propagation as "regular"
     * property, so the order in which this method and setMessageProperties() is important: getTimeToLive() must be called *before*
     * setMessageProperties().
     *
     * @return the JMS_MESSAGE_TIME_TO_LIVE set by the caller or 0. The JMS_MESSAGE_TIME_TO_LIVE can be a Long, Integer or String
     *
     * @see JMSUtil#setMessageProperties(javax.jms.Message, java.util.Map)
     *
     * @exception NumberFormatException
     * @exception IllegalArgumentException
     */
    static long getTimeToLive(Map<String, Object> properties) throws Exception
    {
        if (properties == null)
        {
            return 0L;
        }

        Object o = properties.remove(JMSUtil.JMS_MESSAGE_TIME_TO_LIVE);

        if (o == null)
        {
            return 0L;
        }

        if (o instanceof Long)
        {
            return ((Long)o).longValue();
        }
        else if (o instanceof Integer)
        {
            return ((Integer)o).longValue();
        }
        else if (o instanceof String)
        {
            return Long.parseLong((String)o);
        }
        else
        {
            throw new IllegalArgumentException("'" + JMSUtil.JMS_MESSAGE_TIME_TO_LIVE + "' value is invalid: " + o);
        }
    }

    /**
     * @see JMSUtil#getTimeToLive(java.util.Map)
     */
    static void setMessageProperties(Message m, Map<String, Object> properties) throws JMSException
    {
        if (properties == null)
        {
            return;
        }

        for(String key: properties.keySet())
        {
            Object value = properties.get(key);

            if (value instanceof String)
            {
                m.setStringProperty(key, (String)value);

            }
            else if (value instanceof Boolean)
            {
                m.setBooleanProperty(key, ((Boolean)value).booleanValue());
            }
            else if (value instanceof Byte)
            {
                m.setByteProperty(key, ((Byte)value).byteValue());
            }
            else if (value instanceof Short)
            {
                m.setShortProperty(key, ((Short)value).shortValue());
            }
            else if (value instanceof Integer)
            {
                m.setIntProperty(key, ((Integer)value).intValue());
            }
            else if (value instanceof Long)
            {
                m.setLongProperty(key, ((Long)value).longValue());
            }
            else if (value instanceof Float)
            {
                m.setFloatProperty(key, ((Float)value).floatValue());
            }
            else if (value instanceof Double)
            {
                m.setDoubleProperty(key, ((Double)value).doubleValue());
            }
            else
            {
                m.setObjectProperty(key, value);
            }
        }
    }

    /**
     * @param node - the node's JNDI URL.
     *
     * @return the ConnectionContext list or null if the node is unknown.
     */
    List<ConnectionContext> getContextsForNode(String node)
    {
        if (connections == null)
        {
            return null;
        }

        synchronized (connections)
        {
            return connections.get(node);
        }
    }

    /**
     * @return all nodes the JMSUtil instance is currently maintaining active JMS connection for.
     *
     * @see gov.hhs.cms.base.common.util.JMSUtil#getNodes()
     */
    List<String> getActiveNodes()
    {
        List<String> nodes = new ArrayList<String>();

        synchronized (connections)
        {
            for(String n: connections.keySet())
            {
                if (!connections.get(n).isEmpty())
                {
                    nodes.add(n);
                }
            }
        }

        return nodes;
    }

    /**
     * @return all nodes the JMSUtil instance knows about at the moment of the invocation, regardless whether the instance maintains active
     *         JMS connections to them or not.
     *
     * @see gov.hhs.cms.base.common.util.JMSUtil#getActiveNodes()
     */
    Set<String> getNodes()
    {
        Set<String> nodes = new HashSet<String>();

        synchronized (connections)
        {
            for(String n: connections.keySet())
            {
                nodes.add(n);
            }
        }

        return nodes;
    }

    /**
     * @return true if the node was added, or false if the node was already in the list
     */
    boolean addNode(String node)
    {
        synchronized (connections)
        {
            if (connections.keySet().contains(node))
            {
                return false;
            }

            log.debug(this + " added new node '" + node + "', current node set: " + getNodes());
            connections.put(node, new ArrayList<ConnectionContext>());
            return true;
        }
    }

    /**
     * Removes the node's connections from the cache (after attempting to cleanly close() them) and then removes the node itself. Typically
     * called to clean the cache when a connection failure is detected.
     */
    boolean removeNode(String node)
    {
        List<ConnectionContext> ccs = null;
        synchronized (connections)
        {
            ccs = connections.remove(node);
        }

        if (ccs == null)
        {
            return false;
        }

        synchronized (ccs)
        {
            for(ConnectionContext cc: ccs)
            {
                try
                {
                    cc.close();
                }
                catch (Exception e)
                {
                    log.warn("failed to close " + cc, e);
                }
            }
        }

        log.debug("node " + node + " connection cache cleared and node removed from those we will attempt sending messages to");

        return true;
    }

    ClusterTopologyDiscoveryTask getClusterTopologyDiscoveryTask()
    {
        return clusterTopologyDiscoveryTask;
    }

    void stopTopologyDiscovery()
    {
        if (clusterTopologyTimer != null)
        {
            clusterTopologyTimer.cancel();
            clusterTopologyTimer = null;
            log.debug(this + " stopped topology discovery");
        }
    }

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    // Private -----------------------------------------------------------------------------------------------------------------------------

    /**
     * @param url - the JMSUtil instance identifier - it could be a comma-separated list of JNDI URLs designating nodes of the cluster we
     *        want to connect to (the values will be used to "seed" the connection cache), or "localhost", in which case the instance will
     *        intermediate access to the local JMS provider.
     */
    private static JMSUtil getInstance(Type type, String url)
    {
        if (url == null)
        {
            if (type.isLocal())
            {
                url = "localhost";
            }
            else
            {
                throw new IllegalArgumentException("attempt to initialize JMSUtil with a null URL/URL list");
            }
        }

        if ("localhost".equals(url) && !type.isLocal())
        {
            throw new IllegalArgumentException("cannot initialize a " + type + " instance with a 'localhost' URL");
        }

        JMSUtil result = null;

        Key k = new Key(url);

        if (type.isPointToPoint() && k.getUrls().size() > 1)
        {
            throw new IllegalArgumentException("cannot initialize a " + type + " instance with a list containing more than one URL");
        }

        synchronized (instances)
        {
            result = instances.get(k);

            if (result == null)
            {
                // look up all necessary system properties, invent defaults, configure the instance

                // JMS ConnectionFactory - cannot configure at this time different
                //                         ConnectionFactories per cluster

                String jmsConnectionFactoryJndiName = null;

                if (type.isLocal())
                {
                    jmsConnectionFactoryJndiName = DEFAULT_LOCAL_JMS_CONNECTION_FACTORY_JNDI_NAME;
                }
                else
                {
                    jmsConnectionFactoryJndiName =
                        System.getProperty(JMS_CONNECTION_FACTORY_JNDI_NAME_PROPERTY_NAME);

                    if (jmsConnectionFactoryJndiName == null)
                    {
                        jmsConnectionFactoryJndiName = DEFAULT_JMS_CONNECTION_FACTORY_JNDI_NAME;
                    }
                }

                int connectionCount = DEFAULT_CONNECTION_COUNT;

                String s = System.getProperty(CONNECTION_COUNT_PROPERTY_NAME);

                if (s != null)
                {
                    try
                    {
                        connectionCount = Integer.parseInt(s);
                    }
                    catch(Exception e)
                    {
                        throw new IllegalArgumentException(
                            "invalid '" + CONNECTION_COUNT_PROPERTY_NAME +
                            "' value: \"" + s + "\", it must be an integer");
                    }
                }

                long topologyDiscoveryInterval = -1L;

                s = System.getProperty(TOPOLOGY_DISCOVERY_INTERVAL_PROPERTY_NAME);

                if (s != null)
                {
                    try
                    {
                        topologyDiscoveryInterval = Long.parseLong(s);
                    }
                    catch(Exception e)
                    {
                        throw new IllegalArgumentException(
                            "invalid '" + TOPOLOGY_DISCOVERY_INTERVAL_PROPERTY_NAME +
                            "' value: \"" + s + "\", it must be a long");
                    }
                }

                String topologyJndiLocation = System.getProperty(TOPOLOGY_JNDI_LOCATION_PROPERTY_NAME);

                if (topologyJndiLocation == null)
                {
                    topologyJndiLocation = DEFAULT_TOPOLOGY_JNDI_LOCATION;
                }

                String jndiInitialContextFactory = System.getProperty(JNDI_INITIAL_CONTEXT_FACTORY_PROPERTY_NAME);;

                if (jndiInitialContextFactory == null)
                {
                    jndiInitialContextFactory = DEFAULT_JNDI_INITIAL_CONTEXT_FACTORY;
                }

                String jndiUrlPkgPrefixes = System.getProperty(JNDI_URL_PKG_PREFIXES_PROPERTY_NAME);;

                if (jndiUrlPkgPrefixes == null)
                {
                    jndiUrlPkgPrefixes = DEFAULT_JNDI_URL_PKG_PREFIXES;
                }

                result = new JMSUtil(type,
                                     k.getUrls(),
                                     jmsConnectionFactoryJndiName,
                                     connectionCount,
                                     topologyDiscoveryInterval,
                                     topologyJndiLocation,
                                     jndiInitialContextFactory,
                                     jndiUrlPkgPrefixes);

                instances.put(k, result);
            }
        }

        return result;
    }

    /**
     * Returns a connection after consulting the load balancing policy, possibly initializing some connections and re-populating one time
     *         if necessary.
     *
     * @param excludedFromRePopulation - if re-population is attempted, exclude these nodes from re-population, as they were just
     *        invalidated by a previous failure during the current send() or receive() attempt. Null is a valid value.
     *
     * @return a valid Connection or null if no connection can be obtained after one re-population attempt - which usually means the
     *         point-to-point target or the whole JMS cluster is down.
     */
    private ConnectionContext getConnectionContext(Set<String> excludedFromRePopulation)
    {
        ConnectionContext c = null;

        boolean firstAttempt = true;

        while(c == null)
        {
            String node = null;
            List<ConnectionContext> contexts = null;

            synchronized (connections)
            {
                Set<String> nodes = connections.keySet();

                if (nodes.isEmpty())
                {
                    Set<String> rePopulation = null;

                    if (firstAttempt)
                    {
                        firstAttempt = false;

                        // there are no nodes to choose from because previous connection failures removed all nodes from the connection
                        // map; however this is the first attempt to get connections after a while, so in order to avoid being stuck with
                        // a stale instance forever, re-populate the node set with the initial node population, after excluding the nodes
                        // specifically excluded from re-population

                        rePopulation = exclude(initialNodes, excludedFromRePopulation);
                    }

                    if (rePopulation != null && !rePopulation.isEmpty())
                    {
                        log.debug(this + " being re-populated with " + rePopulation + " after found completely empty");
                        for(String in: rePopulation)
                        {
                            connections.put(in, new ArrayList<ConnectionContext>());
                        }
                    }
                    else
                    {
                        // no nodes to choose from after re-population, it means our targets are really broken ... let the above layer
                        // throw the appropriate exception
                        log.error(this + " contains no more nodes to connect/load balance to");
                        return null;
                    }
                }

                node = loadBalancingPolicy.chooseNode(nodes);

                contexts = getContextsForNode(node);

                if (contexts == null)
                {
                    contexts = new ArrayList<ConnectionContext>();
                    connections.put(node, contexts);
                }

                synchronized (contexts)
                {
                    if (contexts.isEmpty())
                    {
                        // the node connection list could be empty because we never created connections to it, the node went down in
                        // the past and we cleared up the cache

                        connect(type, node, jndiInitialContextFactory, jndiUrlPkgPrefixes,
                                jmsConnectionFactoryJndiName, contexts, connectionCount, this);

                        if (contexts.isEmpty())
                        {
                            // unsuccessful initialization attempt, update load balancing policy so it won't offer it next time
                            connections.remove(node);
                            continue;
                        }
                    }

                    return loadBalancingPolicy.chooseContext(contexts);
                }
            }
        }

        log.debug(this + " has chosen connection " + c);

        return c;
    }

    // Inner classes -----------------------------------------------------------------------------------------------------------------------

    /**
     * Instances of this class are used as keys in the top level static JMSUtil instance map. Two keys are equal if they contain at least
     * one common jndi URL - they designate the same cluster.
     */
    public static class Key
    {
        public static final Key LOCALHOST = new Key(LOCALHOST_LITERAL);

        private Set<String> urls;

        /**
         * @exception IllegalArgumentException on an empty URL list.
         */
        public Key(String jndiUrls)
        {
            urls = new HashSet<String>();

            if (LOCALHOST_LITERAL.equals(jndiUrls))
            {
                urls.add(LOCALHOST_LITERAL);
            }
            else
            {
                for(StringTokenizer st = new StringTokenizer(jndiUrls, ","); st.hasMoreTokens(); )
                {
                    String tok = st.nextToken().trim();

                    if (tok.startsWith(JNP_PROTOCOL_LITERAL))
                    {
                        tok = tok.substring(JNP_PROTOCOL_LITERAL.length());
                    }

                    if (tok.length() == 0)
                    {
                        continue;
                    }

                    urls.add(tok);
                }
            }

            if (urls.isEmpty())
            {
                throw new IllegalArgumentException("empty URL list");
            }
        }

        public Set<String> getUrls()
        {
            return urls;
        }

        @Override
        public boolean equals(Object k)
        {
            if (this == k)
            {
                return true;
            }

            if (!(k instanceof Key))
            {
                return false;
            }

            Key that = (Key)k;

            if (urls.size() == 1 && urls.contains(LOCALHOST_LITERAL))
            {
                return that.urls.size() == 1 && that.urls.contains(LOCALHOST_LITERAL);
            }

            // we're equal if we have one url in common

            for(String s: urls)
            {
                if (that.urls.contains(s))
                {
                    return true;
                }
            }

            return false;
        }

        @Override
        public int hashCode()
        {
            int result = 7;

            for(String s: urls)
            {
                result += 17 * s.hashCode();
            }

            return result;
        }

        @Override
        public String toString()
        {
            if (urls == null)
            {
                return "UNINITIALIZED Key";
            }

            if (urls.size() == 1 && urls.contains(LOCALHOST_LITERAL))
            {
                return "LOCALHOST";
            }

            return urls.toString();
        }
    }

    public static class ClusterTopologyDiscoveryTask extends TimerTask
    {
        private static final Logger log = Logger.getLogger(ClusterTopologyDiscoveryTask.class);

        private JMSUtil cluster;

        public ClusterTopologyDiscoveryTask(JMSUtil cluster)
        {
            this.cluster = cluster;
        }

        @Override
        public void run()
        {
            // use one of the active node as source of cluster topology; if no active nodes found just noop, as no one seems to be needing
            // JMS right now

            List<String> activeNodes = cluster.getActiveNodes();

            if(activeNodes.isEmpty())
            {
                log.debug("no active nodes to read topology information from");
                return;
            }

            // iterate until I find an active context
            ConnectionContext cc = null;
            for(String node: activeNodes)
            {
                List<ConnectionContext> contexts = cluster.getContextsForNode(node);

                if (contexts == null)
                {
                    continue;
                }

                for(ConnectionContext c: contexts)
                {
                    cc = c;
                    break;
                }
            }

            if (cc == null)
            {
                // no connections created yet to any nodes - no one is using the connection cache so simply noop
                log.debug("no JMS connections established to the target yet, looping ...");
                return;
            }

            List<String> topology = null;

            try
            {
                Context ic = cc.getNodeJndiContext();
                topology = getTopologyFromJndi(ic, ClusterTopologyDiscoveryTask.log);
            }
            catch(Exception e)
            {
                log.warn(this + " failed to get an updated topology from the JMS cluster " + cluster.getNodes() + ", for details see the DEBUG log");
                log.debug(this + " failed to get an updated topology from the JMS cluster " + cluster.getNodes(), e);
                return;
            }

            if (topology != null)
            {
                for (String node: topology)
                {
                    // if the node is already known, it'll be a noop
                    cluster.addNode(node);
                }
            }
        }
    }

    static class LoadBalancingPolicy
    {
        private int nextNode = 0;

        private Map<String, Integer> nextContext;

        public LoadBalancingPolicy()
        {
            nextContext = new HashMap<String, Integer>();
        }

        /**
         * @return null on an empty set
         */
        public String chooseNode(Set<String> nodes)
        {
            if (nodes.isEmpty())
            {
                return null;
            }

            List<String> sortable = new ArrayList<String>(nodes);
            Collections.sort(sortable);

            synchronized (this)
            {
                if (nextNode >= sortable.size())
                {
                    nextNode = 0;
                }

                return sortable.get(nextNode ++);
            }
        }

        /**
         * It assumes all contexts are from the same node (it would not make sense otherwise).
         *
         * However, there are no checks (yet).
         *
         * TODO this class has a small leak, in that node clusters that leave and never return will leak, but the practical implications of
         *      this are so minuscule that we will fix this in a future release.
         *
         * @return null on an empty list.
         */
        public ConnectionContext chooseContext(List<ConnectionContext> contexts)
        {
            if (contexts.isEmpty())
            {
                return null;
            }

            // first context gives the node
            String node = contexts.get(0).getNode();

            List<ConnectionContext> sortable = new ArrayList<ConnectionContext>(contexts);
            Collections.sort(sortable);

            ConnectionContext result = null;

            synchronized (nextContext)
            {
                Integer next = nextContext.get(node);

                if (next == null)
                {
                    next = 0;
                }

                if (next >= sortable.size())
                {
                    next = 0;
                }

                result = sortable.get(next);

                nextContext.put(node, ++ next);
            }

            return result;
        }

    }

    /**
     * A live connection and a reference to an open InitialContext to the cluster node the connection
     * is established to. Useful when looking up Destinations.
     */
    static class ConnectionContext implements Comparable<ConnectionContext>
    {
        private int id;
        private String jndiUrl;
        private Connection c;
        private CachingContext cachingNamingContext;

        public ConnectionContext(int id, Connection c, CachingContext cachingNamingContext, String nodeJndiUrl)
        {
            this.id = id;
            this.c = c;
            this.cachingNamingContext = cachingNamingContext;
            this.jndiUrl = nodeJndiUrl;
        }

        public int getId()
        {
            return id;
        }

        public Connection getConnection()
        {
            return c;
        }

        public Context getNodeJndiContext()
        {
            return cachingNamingContext;
        }

        /**
         * @return the node JNDI URL
         */
        public String getNode()
        {
            return jndiUrl;
        }

        public void close() throws Exception
        {
            if (cachingNamingContext != null)
            {
                cachingNamingContext.close();
                cachingNamingContext = null;
            }

            if (c != null)
            {
                c.close();
                c = null;
            }
        }

        @Override
        public String toString()
        {
            return "" + id + ", " + jndiUrl + ":" + c + ", " + cachingNamingContext;
        }

        /**
         * Compares this object with the specified object for order.  Returns a negative integer,
         * zero, or a positive integer as this object is less than, equal to, or greater than the
         * specified object.
         */
        //@Override
        public int compareTo(ConnectionContext o)
        {
            if (o == null)
            {
                return 1;
            }

            return id - o.id;
        }
    }

    /**
     * A context that selectively caches instances we know won't change in the remote JNDI space to speed up access
     */
    static class CachingContext implements Context
    {
        private InitialContext delegate;
        private Map<String, Object> namingCache;

        CachingContext(InitialContext delegate)
        {
            this.delegate = delegate;
            this.namingCache = new ConcurrentHashMap<String, Object>();
        }

        //@Override
        public Object lookup(String name) throws NamingException
        {
            Object result = namingCache.get(name);

            if (result != null)
            {
                return result;
            }

            result = delegate.lookup(name);

            // redundant precaution checking for null, lookup() is supposed to throw exception if the name is not found
            if (result != null && (name.startsWith("/queue/") || name.startsWith("queue/")))
            {
                namingCache.put(name, result);
            }

            return result;
        }

        //@Override
        public void close() throws NamingException
        {
            if (namingCache != null)
            {
                namingCache.clear();
                namingCache = null;
            }

            delegate.close();
        }

        //@Override
        public Object lookup(Name name) throws NamingException { throw new RuntimeException("NOT YET IMPLEMENTED"); }

        //@Override
        public void bind(Name name, Object obj) throws NamingException { throw new RuntimeException("NOT YET IMPLEMENTED"); }

        //@Override
        public void bind(String name, Object obj) throws NamingException
        {
            delegate.bind(name, obj);
        }

        //@Override
        public void rebind(Name name, Object obj) throws NamingException { throw new RuntimeException("NOT YET IMPLEMENTED"); }

        //@Override
        public void rebind(String name, Object obj) throws NamingException { throw new RuntimeException("NOT YET IMPLEMENTED"); }

        //@Override
        public void unbind(Name name) throws NamingException { throw new RuntimeException("NOT YET IMPLEMENTED"); }

        //@Override
        public void unbind(String name) throws NamingException { throw new RuntimeException("NOT YET IMPLEMENTED"); }

        //@Override
        public void rename(Name oldName, Name newName) throws NamingException { throw new RuntimeException("NOT YET IMPLEMENTED"); }

        //@Override
        public void rename(String oldName, String newName) throws NamingException { throw new RuntimeException("NOT YET IMPLEMENTED"); }

        //@Override
        public NamingEnumeration<NameClassPair> list(Name name) throws NamingException { throw new RuntimeException("NOT YET IMPLEMENTED"); }

        //@Override
        public NamingEnumeration<NameClassPair> list(String name) throws NamingException { throw new RuntimeException("NOT YET IMPLEMENTED"); }

        //@Override
        public NamingEnumeration<Binding> listBindings(Name name) throws NamingException { throw new RuntimeException("NOT YET IMPLEMENTED"); }

        //@Override
        public NamingEnumeration<Binding> listBindings(String name) throws NamingException { throw new RuntimeException("NOT YET IMPLEMENTED"); }

        //@Override
        public void destroySubcontext(Name name) throws NamingException { throw new RuntimeException("NOT YET IMPLEMENTED"); }

        //@Override
        public void destroySubcontext(String name) throws NamingException { throw new RuntimeException("NOT YET IMPLEMENTED"); }

        //@Override
        public Context createSubcontext(Name name) throws NamingException { throw new RuntimeException("NOT YET IMPLEMENTED"); }

        //@Override
        public Context createSubcontext(String name) throws NamingException { throw new RuntimeException("NOT YET IMPLEMENTED"); }

        //@Override
        public Object lookupLink(Name name) throws NamingException { throw new RuntimeException("NOT YET IMPLEMENTED"); }

        //@Override
        public Object lookupLink(String name) throws NamingException { throw new RuntimeException("NOT YET IMPLEMENTED"); }

        //@Override
        public NameParser getNameParser(Name name) throws NamingException { throw new RuntimeException("NOT YET IMPLEMENTED"); }

        //@Override
        public NameParser getNameParser(String name) throws NamingException { throw new RuntimeException("NOT YET IMPLEMENTED"); }

        //@Override
        public Name composeName(Name name, Name prefix) throws NamingException { throw new RuntimeException("NOT YET IMPLEMENTED"); }

        //@Override
        public String composeName(String name, String prefix) throws NamingException { throw new RuntimeException("NOT YET IMPLEMENTED"); }

        //@Override
        public Object addToEnvironment(String propName, Object propVal) throws NamingException { throw new RuntimeException("NOT YET IMPLEMENTED"); }

        //@Override
        public Object removeFromEnvironment(String propName) throws NamingException { throw new RuntimeException("NOT YET IMPLEMENTED"); }

        //@Override
        public Hashtable<?, ?> getEnvironment() throws NamingException { throw new RuntimeException("NOT YET IMPLEMENTED"); }

        //@Override
        public String getNameInNamespace() throws NamingException { throw new RuntimeException("NOT YET IMPLEMENTED"); }

        @Override
        public String toString()
        {
            return "CachingContext[" + delegate + "](" + (namingCache == null ? null : namingCache.size()) + ")";
        }

        Map<String, Object> getCache()
        {
            return namingCache;
        }
    }

    public class WholeClusterDownException extends Exception
    {
        public WholeClusterDownException(String message)
        {
            super(message);
        }
    }

    public class TargetNodeDownException extends Exception
    {
        public TargetNodeDownException(String message)
        {
            super(message);
        }
    }

    public static class CommandLineLoop
    {
        private JMSUtil jms;
        private BufferedReader br;
        private volatile boolean stopSending;

        public CommandLineLoop(JMSUtil jms)
        {
            br = new BufferedReader(new InputStreamReader(System.in));
            this.jms = jms;
        }

        public void readStdin() throws Exception
        {
            System.out.print("> ");
            boolean keepGoing = true;
            String line;
            while(keepGoing && (line = br.readLine()) != null)
            {

                try
                {
                    keepGoing = executeCommand(line);
                }
                catch(Exception e)
                {
                    System.out.println("> [error] " + e.getMessage());
                    log.error("failure", e);
                }
            }

            System.out.println("closing JMSUtil instance ...");
            jms.close();
            System.out.println("done");
        }

        private boolean executeCommand(String line) throws Exception
        {
            if (line.trim().length() == 0)
            {
                System.out.print("> ");
                return true;
            }
            else if (line.startsWith("send"))
            {
                return executeSend(line.substring("send".length()).trim());
            }
            else if (line.startsWith("exit"))
            {
                return executeExit();
            }
            else if (line.startsWith("status") || line.startsWith("info"))
            {
                return executeStatus();
            }
            else if (line.startsWith("test"))
            {
                return executeTest();
            }
            else if (line.startsWith("stop"))
            {
                return executeStop();
            }
            else
            {
                throw new Exception("unknown command: " + line);
            }
        }

        private boolean executeSend(String args) throws Exception
        {
            StringTokenizer st = new StringTokenizer(args);

            int mc = -1;
            int sv = -1;
            int threadCount = 1;
            String sp = null;
            boolean p = true;
            String qjn = "/queue/TestQueue";

            while(st.hasMoreTokens())
            {
                String tok = st.nextToken();

                if ("--sleep".equals(tok))
                {
                    try
                    {
                        sv = Integer.parseInt(st.nextToken());
                    }
                    catch(Exception e)
                    {
                        throw new Exception("expecting an integer to follow --sleep");
                    }
                }
                else if ("--threads".equals(tok))
                {
                    try
                    {
                        threadCount = Integer.parseInt(st.nextToken());
                    }
                    catch(Exception e)
                    {
                        throw new Exception("expecting an integer to follow --threads");
                    }
                }
                else if ("--non-persistent".equals(tok))
                {
                    p = false;
                }
                else if ("--queue".equals(tok))
                {
                    qjn = st.nextToken();
                }
                else if (mc == -1)
                {
                    try
                    {
                        mc = Integer.parseInt(tok);
                    }
                    catch(Exception e)
                    {
                        throw new Exception("expecting message count, got '" + tok + "'");
                    }
                }
                else
                {
                    sp = tok;
                }
            }

            if (mc == -1)
            {
                mc = Integer.MAX_VALUE;
            }

            if (sp == null)
            {
                sp = createSyntheticBody(10240);
            }

            final String payload = sp;
            final AtomicInteger remaining = new AtomicInteger(mc);
            final AtomicInteger actuallySent = new AtomicInteger(0);
            final int sleep = sv;
            final int messageCount = mc;
            final boolean persistent = p;
            final String queueJndiName = qjn;

            final CyclicBarrier barrier = new CyclicBarrier(threadCount, new Runnable()
            {
                public void run()
                {
                    System.out.print("> " + actuallySent.get() + " messages sent\n> ");
                    stopSending = false;
                }
            });

            for(int t = 0; t < threadCount; t ++)
            {
                new Thread(new Runnable()
                {
                    public void run()
                    {
                        boolean error = false;

                        while(true)
                        {
                            int i = remaining.decrementAndGet();

                            if (i < 0 || error || stopSending)
                            {
                                // no more messages to send or we encountered error and we're useless, or we have been told to stop
                                // sending messages; await at the barrier and then exit
                                try
                                {
                                    barrier.await();
                                }
                                catch(Exception e)
                                {
                                    log.error("barrier error", e);
                                }

                                return;
                            }

                            // there are still messages, and we did not previously fail so send

                            try
                            {
                                jms.send(queueJndiName, MessageType.OBJECT, payload, UUID.randomUUID().toString(), null, persistent);

                                int index = actuallySent.incrementAndGet();

                                reportToStdout(index, messageCount, sleep, persistent);

                                if (sleep > 0)
                                {
                                    Thread.sleep(sleep);
                                }
                            }
                            catch(Exception e)
                            {
                                log.error("sending thread failed", e);
                                error = true;
                            }
                        }
                    }
                }, "Sender " + t).start();
            }

            return true;
        }

        private boolean executeStop() throws Exception
        {
            stopSending = true;
            return true;
        }

        private boolean executeExit() throws Exception
        {
            System.out.println("> exiting ...");
            return false;
        }

        private boolean executeStatus() throws Exception
        {
            System.out.println(jms.statusToString());
            return true;
        }

        private boolean executeTest() throws Exception
        {
            JMSUtil.ClusterTopologyDiscoveryTask r = new JMSUtil.ClusterTopologyDiscoveryTask(jms);

            r.run();

            return true;
        }

        private String createSyntheticBody(int sizeInBytes)
        {
            StringBuffer sb = new StringBuffer();
            int remaining = sizeInBytes;
            while(remaining > 0)
            {
                String line = "TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST\n";
                sb.append(line);
                remaining -= line.length();

            }
            return sb.toString();
        }

        private void reportToStdout(int index, int totalCount, long sleep, boolean persistent)
        {
            // if there is a sleep between messages, report messages individually, otherwise report every thousandth one

            if (sleep > 0 || (index % 1000 == 0))
            {
                System.out.println("> sent " + index + " " + (persistent ? "PERSISTENT" : "NON_PERSISTENT") +
                                   " messages" + (totalCount == Integer.MAX_VALUE ? "" : " out of " + totalCount) +
                                   " on thread " + Thread.currentThread().getName());

            }
        }
    }

    public static enum MessageType
    {
        BYTE,
        MAP,
        OBJECT,
        STREAM,
        TEXT
    }

    public static enum Type
    {
        LOCAL,
        POINT2POINT,
        CLUSTERED;

        public boolean isLocal()
        {
            return this.equals(LOCAL);
        }

        public boolean isPointToPoint()
        {
            return this.equals(POINT2POINT);
        }

        public boolean isClustered()
        {
            return this.equals(CLUSTERED);
        }
    }
}
