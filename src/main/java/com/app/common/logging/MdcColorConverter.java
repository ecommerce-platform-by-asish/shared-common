package com.app.common.logging;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;

/** Custom Logback converter that applies ANSI colors to MDC values for improved log readability. */
public class MdcColorConverter extends ClassicConverter {

  /** Colors values Green when present, and Red when anonymous or missing. */
  @Override
  public String convert(ILoggingEvent event) {
    // Force ANSI to be always enabled for this converter
    AnsiOutput.setEnabled(AnsiOutput.Enabled.ALWAYS);

    String key = getFirstOption();
    if (key == null) return "";

    String value = event.getMDCPropertyMap().get(key);

    // Check for 'none' or 'anonymous' or empty values
    if (value == null
        || value.isEmpty()
        || "none".equalsIgnoreCase(value)
        || "anonymous".equalsIgnoreCase(value)) {
      String display =
          (value == null || value.isEmpty())
              ? (key.equals("userId") ? "anonymous" : "none")
              : value;
      return AnsiOutput.toString(AnsiColor.RED, display);
    }

    // Use Green when data is available
    return AnsiOutput.toString(AnsiColor.GREEN, value);
  }
}
