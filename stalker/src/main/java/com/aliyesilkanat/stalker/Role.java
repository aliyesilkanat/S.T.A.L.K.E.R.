package com.aliyesilkanat.stalker;

import org.apache.log4j.Logger;

import com.aliyesilkanat.stalker.data.UnfinishedOperationException;
import com.aliyesilkanat.stalker.extractor.Extractor;
import com.aliyesilkanat.stalker.retriever.Retriever;

/**
 * @author Ali Yesilkanat.
 * 
 *         Any class other than {@link Extractor} and {@link Retriever} should
 *         inhereted from this class. This class controls session by handling
 *         action executions exceptions. With this handling system, any
 *         triggered session would have been terminated.
 *
 */
public abstract class Role {
	/**
	 * Logger instance
	 */
	private final Logger logger = Logger.getLogger(getClass());

	/**
	 * Wrapper method for action execution. If any
	 * {@link UnfinishedOperationException} thrown, then Role class handles it
	 * and terminates session..
	 */
	public void execute() {
		try {
			executeAction();
		} catch (Exception e) {
			getLogger().error(e);
		}
	}

	/**
	 * Main method for executing role's actions.
	 * 
	 * @throws UnfinishedOperationException
	 */
	protected abstract void executeAction() throws UnfinishedOperationException;

	public Logger getLogger() {
		return logger;
	}
}
