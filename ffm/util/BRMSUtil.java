package gov.hhs.cms.base.common.util;

import gov.hhs.cms.base.common.Layer;
import gov.hhs.cms.base.common.cache.FEPSCacheBase;
import gov.hhs.cms.base.common.cache.FEPSCacheManagerFactory;
import gov.hhs.cms.base.common.exceptionframework.base.TransactionException;
import gov.hhs.cms.base.common.exceptionframework.util.ExceptionFrameworkUtil;
import gov.hhs.cms.base.vo.ReturnCodeVO;
import gov.hhs.cms.base.vo.RulePackageHeaderVO;
import gov.hhs.cms.base.vo.ServiceHeaderVO;

import org.apache.log4j.Logger;
import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.UrlResource;
import org.drools.runtime.StatefulKnowledgeSession;

/**
 * This Utility class allows a caller to pass the VOs as model object to the
 * BRMS rule engine and fire the rules defined in a changeset.
 * 
 * @author Birali Hakizumwami, Trevor Quinn
 * 
 */
public class BRMSUtil {
	// private static long executeTime = 0L;
	// private static long numExecutes = 0L;

	private static final Logger LOG = Logger.getLogger(BRMSUtil.class);
	private static final String RULES_CONTROLLER_PROPERTY_NAME = "rulesController";
	
	private static final String component = "BRMSUtil";

	
	/**
	 * 
	 * This method run a set of rules from a rulePackage given facts stored in a
	 * Value object containing the facts and return the result in the same VO.
	 * 
	 * @param rulePkg
	 *            The rule package
	 * @param modelObj
	 *            The VO containing the facts. Results will be added to this
	 *            same object model
	 */
	public static void execute(String rulePkg, Object modelObj, ServiceHeaderVO serviceHeaderVO) throws TransactionException {
		execute(rulePkg, null, modelObj, serviceHeaderVO);
	}	
	
	/**
	 * 
	 * This method run a set of rules from a rulePackage given facts stored in a
	 * Value object containing the facts and return the result in the same VO.
	 * 
	 * @param rulePkg
	 *            The rule package
	 * @param modelObj
	 *            The VO containing the facts. Results will be added to this
	 *            same object model
	 */
	@Deprecated //Use execute with service header vo
	public static void execute(String rulePkg, Object modelObj) {
		execute(rulePkg, null, modelObj);
	}

	/**
	 * 
	 * This method run a set of rules from a rulePackage given facts stored in a
	 * Value object containing the facts and return the result in the same VO.
	 * 
	 * @param rulePkg
	 *            The rule package
	 * @param agendaGroup
	 * 			  The agenda group to put focus on
	 * @param modelObj
	 *            The VO containing the facts. Results will be added to this
	 *            same object model
	 */
	public static void execute(String rulePkg, String agendaGroup, Object modelObj, ServiceHeaderVO serviceHeaderVO) throws TransactionException {
		try {
			createSessionAndFireAllRules(rulePkg, agendaGroup, modelObj);
		} catch (Exception ex) {
			String msg = String.format("exception in BRMSUtil.execute calling rule (%s) :: ",rulePkg);
			LOG.error(msg, ex);
			throw ExceptionFrameworkUtil.getInstance().createTransactionException(serviceHeaderVO, Layer.UNKNOWN,  component,  "execute", null,  msg,                                       
					ExceptionHandlingConstants.BRMS_UTIL_PREFIX+ExceptionHandlingConstants.GENERIC_BRMS_ERROR, ReturnCodeVO.INTERNAL_ERROR_500.value(), ex);	
		}
		
	}	
	
	/**
	 * 
	 * This method run a set of rules from a rulePackage given facts stored in a
	 * Value object containing the facts and return the result in the same VO.
	 * 
	 * @param rulePkg
	 *            The rule package
	 * @param agendaGroup
	 * 			  The agenda group to put focus on
	 * @param modelObj
	 *            The VO containing the facts. Results will be added to this
	 *            same object model
	 */
	@Deprecated //Use execute with service header vo
	public static void execute(String rulePkg, String agendaGroup, Object modelObj) {
		createSessionAndFireAllRules(rulePkg, agendaGroup, modelObj);
		
	}

	/**
	 * 
	 * This method run a set of rules from a rule package given facts stored in
	 * an array of Value object containing the facts and return the result in
	 * the same array of VOs.
	 * 
	 * @param rulePkg
	 *            The rule package
	 * @param modelObjs
	 *            An array of VOs containing the facts. Results will be added to
	 *            this same array of VOs
	 */
	public static void executeMultiple(String rulePkg, Object[] modelObjs, ServiceHeaderVO serviceHeaderVO) throws TransactionException {
			executeMultiple(rulePkg, null, modelObjs, serviceHeaderVO);

	}	
	
	/**
	 * 
	 * This method run a set of rules from a rule package given facts stored in
	 * an array of Value object containing the facts and return the result in
	 * the same array of VOs.
	 * 
	 * @param rulePkg
	 *            The rule package
	 * @param modelObjs
	 *            An array of VOs containing the facts. Results will be added to
	 *            this same array of VOs
	 */
	@Deprecated //Use execute with service header vo
	public static void executeMultiple(String rulePkg, Object[] modelObjs) {
		executeMultiple(rulePkg, null, modelObjs);
	}
	
	/**
	 * 
	 * This method run a set of rules from a rule package given facts stored in
	 * an array of Value object containing the facts and return the result in
	 * the same array of VOs.
	 * 
	 * @param rulePkg
	 *            The rule package
	 * @param agendaGroup
	 * 			  Agenda group to put focus on
	 * @param modelObjs
	 *            An array of VOs containing the facts. Results will be added to
	 *            this same array of VOs
	 */
	public static void executeMultiple(String rulePkg, String agendaGroup, Object[] modelObjs, ServiceHeaderVO serviceHeaderVO) 
		throws TransactionException {
		try {
			createSessionAndFireAllRules(rulePkg, agendaGroup, modelObjs);
		} catch (Exception ex) {
			String msg = String.format("exception in BRMSUtil.executeMultiple calling rule (%s) :: ",rulePkg);
			LOG.error(msg, ex);
			throw ExceptionFrameworkUtil.getInstance().createTransactionException(serviceHeaderVO, Layer.UNKNOWN,  component,  "executeMultiple", null,  msg,                                       
					ExceptionHandlingConstants.BRMS_UTIL_PREFIX+ExceptionHandlingConstants.GENERIC_BRMS_ERROR, ReturnCodeVO.INTERNAL_ERROR_500.value(), ex);
		}
	}
	
	/**
	 * 
	 * This method run a set of rules from a rule package given facts stored in
	 * an array of Value object containing the facts and return the result in
	 * the same array of VOs.
	 * 
	 * @param rulePkg
	 *            The rule package
	 * @param agendaGroup
	 * 			  Agenda group to put focus on
	 * @param modelObjs
	 *            An array of VOs containing the facts. Results will be added to
	 *            this same array of VOs
	 */
	@Deprecated //Use execute with service header vo
	public static void executeMultiple(String rulePkg, String agendaGroup, Object[] modelObjs) {
		createSessionAndFireAllRules(rulePkg, agendaGroup, modelObjs);
	}

	/**
	 * This method calls a decision table to determine based on the tenant and
	 * effective period passed in the ServiceHeaderVO which version of the same
	 * rule to use.
	 * 
	 * 
	 * @param facts
	 *            : An array of facts needed for the changeset
	 * @param rulePackageheader
	 *            : contains information about the tenant Id and effective dates
	 */
	public static void executeWithRulesController(RulePackageHeaderVO rulePackageheader, Object... facts) {
		executeWithRulesController(rulePackageheader, null, facts);
	}

	/**
	 * This method calls a decision table to determine based on the tenant and
	 * effective period passed in the ServiceHeaderVO which version of the same
	 * rule to use.
	 * 
	 * 
	 * @param facts
	 *            : An array of facts needed for the changeset
	 * @param agendaGroup
	 *            : An agenda group to set focus on
	 * @param rulePackageheader
	 *            : contains information about the tenant Id and effective dates
	 */
	public static void executeWithRulesController(RulePackageHeaderVO rulePackageheader, String agendaGroup, Object... facts) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Entered executeWithRulesController");
		}

		KnowledgeBase rulesControllerKBase = null;
		StatefulKnowledgeSession rulesControllerSession = null;

		try {
			rulesControllerKBase = createKnowledgeBase(FFEConfig.getProperty(RULES_CONTROLLER_PROPERTY_NAME));
			rulesControllerSession = rulesControllerKBase.newStatefulKnowledgeSession();
			rulesControllerSession.insert(rulePackageheader);
			rulesControllerSession.fireAllRules();
			if (LOG.isDebugEnabled()) {
				LOG.debug("Rules controller returned rule package name result: "
						+ rulePackageheader.getRulePackageNameResult());
			}
			if (rulePackageheader.getRulePackageNameResult() == null) {
				// Use default rule from system properties
				createSessionAndFireAllRules(FFEConfig.getProperty(rulePackageheader.getRulePackageType()), agendaGroup, facts);
			} else {
				// Use the package name the rules controller returned
				createSessionAndFireAllRules(rulePackageheader.getRulePackageNameResult(), agendaGroup, facts);
			}
		} catch (Exception ex) {
			LOG.error("Exception in executeWithRulesController", ex);
			throw new RuntimeException(ex);
		} finally {
			rulesControllerSession.dispose();
		}
	}
	
	private static KnowledgeBase createKnowledgeBase(String pkgName) {

		String brmsUrl = FFEConfig.getProperty("brms.url",
				"http://localhost:8180/jboss-brms/org.drools.guvnor.Guvnor/package/");

		if (LOG.isDebugEnabled()) {
			LOG.debug("brmsUrl=" + brmsUrl);
		}

		String brmsUsername = FFEConfig.getProperty("brms.username", "admin");
		String brmsPassword = FFEConfig.getProperty("brms.password", "admin");

		// Load the knowledge base from the cache
		KnowledgeBase kbase = null;
		FEPSCacheBase knowledgeBaseCache = FEPSCacheManagerFactory.getInstance().getCache("knowledgeBases");
		if (knowledgeBaseCache != null) {
			kbase = (KnowledgeBase) knowledgeBaseCache.get(pkgName);
		}

		if (kbase == null) {
			// TODO Configure expiration to allow runtime rules updates. Current
			// implementation requires cache clearance to retrieve rule updates.

			String pkgUrl = brmsUrl + pkgName;

			if (LOG.isDebugEnabled()) {
				LOG.debug("pkgUrl=" + pkgUrl);
			}

			KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

			UrlResource pkgUrlResource = (UrlResource) ResourceFactory.newUrlResource(pkgUrl);
			pkgUrlResource.setBasicAuthentication("enabled");
			pkgUrlResource.setUsername(brmsUsername);
			pkgUrlResource.setPassword(brmsPassword);

			kbuilder.add(pkgUrlResource, ResourceType.PKG);

			kbase = kbuilder.newKnowledgeBase();

			// knowledgeBaseMap.put(pkgName, kbase);
			if (knowledgeBaseCache != null) {
				knowledgeBaseCache.put(pkgName, kbase);
			}
		}

		return kbase;
	}

	private static void createSessionAndFireAllRules(String rulePkg, String agendaGroup, Object... modelObjs) {
		// long start = System.currentTimeMillis();
		KnowledgeBase knowledgeBase = createKnowledgeBase(rulePkg);
		StatefulKnowledgeSession session = knowledgeBase.newStatefulKnowledgeSession();

		try {
			if (LOG.isDebugEnabled()) {
				LOG.debug("rule package: " + rulePkg + " - before rule firing - start facts listing:");
				LogUtils.dumpObjectsToLog(modelObjs, LOG);
				LOG.debug("rule package: " + rulePkg + " - before rule firing - end facts listing");
			}

			for (int i = 0; i < modelObjs.length; i++) {
				if (modelObjs[i] != null) {
					session.insert(modelObjs[i]);
				}
			}

			if (agendaGroup != null) {
				session.getAgenda().getAgendaGroup(agendaGroup).setFocus();
			}
			LOG.info("start firing all rules for rule package: " + rulePkg);
			session.fireAllRules();
			LOG.info("done firing all rules for rule package: " + rulePkg);

			if (LOG.isDebugEnabled()) {
				LOG.debug("rule package: " + rulePkg + " - after rule firing - start result objects listing:");
				LogUtils.dumpObjectsToLog(modelObjs, LOG);
				LOG.debug("rule package: " + rulePkg + " - after rule firing - end result objects listing");
			}

		} finally {
			session.dispose();
			// long time = System.currentTimeMillis() - start;
			// executeTime += time;
			// if (LOG.isDebugEnabled()) {
			// LOG.debug("*** BRMS execute took " + time + " ms. Avg="
			// + (executeTime / ++numExecutes));
			// }
		}
	}

}