package com.speno.xedm.gui.common.client.util;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.gwt.logging.client.TextLogFormatter;

/**
 * A Handler that prints logs to the window.console - this is used by things
 * like FirebugLite in IE, and Safari debug mode.
 * Note we are consciously using 'window' rather than '$wnd' to avoid issues
 * similar to http://code.google.com/p/fbug/issues/detail?id=2914
 */
public class ConsoleLogHandlerWithIEFix extends Handler {

  public ConsoleLogHandlerWithIEFix() {
    setFormatter(new TextLogFormatter(true));
    setLevel(Level.ALL);
  }
  
  @Override
  public void close() {
    // No action needed
  }

  @Override
  public void flush() {
    // No action needed
  }

  @Override
  public void publish(LogRecord record) {
    if (!isSupported() || !isLoggable(record)) {
      return;
    }
    String msg = getFormatter().format(record);
    log(msg);
  }

//  private native boolean isSupported() /*-{
//    return ((window.console != null) &&
//            (window.console.firebug == null) && 
//            (window.console.log != null) &&
//            (typeof(window.console.log) == 'function'));
//  }-*/;

  private native boolean isSupported() /*-{
  return ((window.console != null) &&
          (window.console.firebug == null) && 
          (window.console.log != null) &&
          ((typeof(window.console.log) == 'function') 
            || (typeof(window.console.log) == 'object')));
  }-*/;
  
  private native void log(String message) /*-{
    window.console.log(message);
  }-*/;

}
