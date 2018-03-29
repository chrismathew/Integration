package org.cms.hios.common.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class PartitionUtil {
	
	private static Logger logger = Logger.getLogger(PartitionUtil.class);
	public static int MAX_PARTITION_SIZE = 2000;

	public static <T> List<List<T>> partition(List<T> bigList) {
		return PartitionUtil.partition(bigList, MAX_PARTITION_SIZE);
	}
	
	public static <T> List<List<T>> partition(List<T> bigList, int partitionSize) {
		List<List<T>> result = new ArrayList<List<T>>();
		// nothing to do
		if (bigList == null || bigList.size() <= partitionSize) {
			if (logger.isDebugEnabled())
				logger.debug("-> nothing to do");
			result.add(bigList);
		}
		// partition
		else {
			int startIndex = 0;
			while (startIndex < bigList.size() - partitionSize) {
				result.add(bigList.subList(startIndex, startIndex + partitionSize));
				if (logger.isDebugEnabled())
					logger.debug("-> start: " + startIndex);
				startIndex += partitionSize;
			}
			if (startIndex < bigList.size()) {
				if (logger.isDebugEnabled())
					logger.debug("-> remainder: " + startIndex);
				result.add(bigList.subList(startIndex, bigList.size()));
			}
		}
		return result;
	}
	
}
