//package uz.greenstar.jolybell.config.security.filter;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//import org.springframework.web.filter.GenericFilterBean;
//import uz.greenstar.jolybell.api.jwt.JwtDTO;
//import uz.greenstar.jolybell.utils.JwtUtils;
//import uz.greenstar.jolybell.utils.MutableHttpServletRequest;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.http.HttpServletRequest;
//import java.io.IOException;
//
//@Component
//public class JwtParserFilter extends GenericFilterBean {
//    @Autowired
//    private JwtUtils jwtUtils;
//
//    @Override
//    public void doFilter(
//            ServletRequest request,
//            ServletResponse response,
//            FilterChain chain) throws IOException, ServletException {
//        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
//        MutableHttpServletRequest mutableRequest = new MutableHttpServletRequest(httpServletRequest);
//        String authorization = httpServletRequest.getHeader("Authorization");
//        if (StringUtils.hasText(authorization)) {
//            JwtDTO jwtDTO = jwtUtils.decode(authorization.split(" ")[1]);
//            mutableRequest.putHeader("userId", jwtDTO.getUserId());
//            mutableRequest.putHeader("username", jwtDTO.getUsername());
//        }
//        chain.doFilter(mutableRequest, response);
//    }
//}
