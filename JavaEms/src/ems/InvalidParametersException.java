package ems;

class InvalidParametersException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1111116704148110753L;

	public InvalidParametersException(String errorMessage) {
		super(errorMessage);
	}
	
}
