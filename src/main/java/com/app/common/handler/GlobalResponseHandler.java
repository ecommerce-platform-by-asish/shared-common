package com.app.common.handler;

import com.app.common.dto.ApiResponse;
import java.util.Collection;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * Automates the wrapping of controller return values into a standardized {@link ApiResponse}.
 * This eliminates the need for manual wrapping in every controller method.
 */
@RestControllerAdvice(basePackages = "com.app")
@ConditionalOnWebApplication(type = Type.SERVLET)
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {

  @Override
  public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
    // Avoid wrapping if already wrapped or if it's a special type
    Class<?> type = returnType.getParameterType();
    return !type.equals(ApiResponse.class) 
        && !type.equals(ResponseEntity.class)
        && !type.equals(void.class)
        && !Void.class.equals(type);
  }

  @Override
  public Object beforeBodyWrite(
      Object body,
      MethodParameter returnType,
      MediaType selectedContentType,
      Class<? extends HttpMessageConverter<?>> selectedConverterType,
      ServerHttpRequest request,
      ServerHttpResponse response) {
    
    // If the body is already an ApiResponse or error-like map, don't wrap
    if (body instanceof ApiResponse || body instanceof Map || body instanceof String) {
      return body;
    }

    return ApiResponse.ok(body);
  }
}
