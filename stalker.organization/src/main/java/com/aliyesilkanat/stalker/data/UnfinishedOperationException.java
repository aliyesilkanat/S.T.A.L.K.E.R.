package com.aliyesilkanat.stalker.data;

public class UnfinishedOperationException extends Exception {
	/**
	 * Serial UID
	 */
	private static final long serialVersionUID = -8408879853415397047L;

	public UnfinishedOperationException() {
		super("Operation unfinished, terminating session");
	}
}
