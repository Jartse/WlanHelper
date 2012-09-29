package com.example.wlanhelper;

public class WlanHelperExceptions {
	
	/**
	 * WlanHelperExceptions
	 * Class for wrapping up the proprietary exceptions used in the WlanHelper application.
	 * 
	 * @author jarias
	 */
	
	// Exceptions that may occur when writing into a NFC tag.
	
	public static class NoNdefMessagesExtraInTagException extends Exception {
		private static final long serialVersionUID = 42L; //for serialization
		
		public NoNdefMessagesExtraInTagException() { }
		public NoNdefMessagesExtraInTagException(String details) {
			super(details);
		}
		public NoNdefMessagesExtraInTagException(String details, Throwable throwable) {
			super(details, throwable);
		}
	}
	
	public static class TagNotWritableException extends Exception {
		private static final long serialVersionUID = 42L; //for serialization
		
		public TagNotWritableException() { }
		public TagNotWritableException(String details) {
			super(details);
		}
		public TagNotWritableException(String details, Throwable throwable) {
			super(details, throwable);
		}
	}
	
	public static class TagMemoryTooSmallException extends Exception {
		private static final long serialVersionUID = 42L; //for serialization
		
		public TagMemoryTooSmallException() { }
		public TagMemoryTooSmallException(String details) {
			super(details);
		}
		public TagMemoryTooSmallException(String details, Throwable throwable) {
			super(details, throwable);
		}
	}
	
	public static class TagNotFormattableException extends Exception {
		private static final long serialVersionUID = 42L; //for serialization
		
		public TagNotFormattableException() { }
		public TagNotFormattableException(String details) {
			super(details);
		}
		public TagNotFormattableException(String details, Throwable throwable) {
			super(details, throwable);
		}
	}
	
	public static class TagFormatNotSupportedException extends Exception {
		private static final long serialVersionUID = 42L; //for serialization
		
		public TagFormatNotSupportedException() { }
		public TagFormatNotSupportedException(String details) {
			super(details);
		}
		public TagFormatNotSupportedException(String details, Throwable throwable) {
			super(details, throwable);
		}
	}
	
	// Exceptions that may occur when reading from a NFC tag.
	
	public static class NoTagExtraException extends Exception {
		private static final long serialVersionUID = 42L; //for serialization
		
		public NoTagExtraException() { }
		public NoTagExtraException(String details) {
			super(details);
		}
		public NoTagExtraException(String details, Throwable throwable) {
			super(details, throwable);
		}
	}
	
	public static class DataFormatNotSupportedException extends Exception {
		private static final long serialVersionUID = 42L; //for serialization
		
		public DataFormatNotSupportedException() { }
		public DataFormatNotSupportedException(String details) {
			super(details);
		}
		public DataFormatNotSupportedException(String details, Throwable throwable) {
			super(details, throwable);
		}
	}
	
	public static class WifiNetworkAdditionException extends Exception {
		private static final long serialVersionUID = 42L; //for serialization
		
		public WifiNetworkAdditionException() { }
		public WifiNetworkAdditionException(String details) {
			super(details);
		}
		public WifiNetworkAdditionException(String details, Throwable throwable) {
			super(details, throwable);
		}
	}
	
	public static class WifiNetworkEnablingException extends Exception {
		private static final long serialVersionUID = 42L; //for serialization
		
		public WifiNetworkEnablingException() { }
		public WifiNetworkEnablingException(String details) {
			super(details);
		}
		public WifiNetworkEnablingException(String details, Throwable throwable) {
			super(details, throwable);
		}
	}
	
}
