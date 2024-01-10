package api.multiverse.swecryptocupcakes.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Base64;

public class AuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {
        String authHeader = request.getHeader("Authorization");
        if ((authHeader != null) && (authHeader.startsWith("Basic"))) {
            authHeader = authHeader.substring(6).trim();
            byte[] decodedHeader = Base64.getDecoder().decode(authHeader);
            String[] creds = new String(decodedHeader).split(":");
            request.setAttribute("username", creds[0]);
            request.setAttribute("password", creds[1]);
        }
        return true;
    }
}
