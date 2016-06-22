package com.huotu.agento2o.agent.config.security.filter;

import com.huotu.agento2o.agent.common.CookieHelper;
import com.huotu.agento2o.agent.common.UserNameAndPasswordNullException;
import com.huotu.agento2o.agent.config.security.AuthenticationToken;
import com.huotu.agento2o.common.SysConstant;
import com.huotu.agento2o.service.entity.author.Author;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by helloztt on 2016/5/9.
 */
public class AuthenticationProcessingFilter extends UsernamePasswordAuthenticationFilter {

    public static final String SPRING_SECURITY_FORM_ROLE_TYPE_KEY = "roleType";

    public String roleTypeParameter = SPRING_SECURITY_FORM_ROLE_TYPE_KEY;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }
        String username = obtainUsername(request);
        String password = obtainPassword(request);
        int roleType = obtainRoleType(request);
        if (username == null) {
            username = "";
        }
        if (password == null) {
            password = "";
        }
        username = username.trim();
        if (username == "") {
            throw new UserNameAndPasswordNullException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.nullUsername",
                    "username can't be null"));
        } else if (password == "") {
            throw new UserNameAndPasswordNullException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.nullPassword",
                    "password can't be null"));
        }
        AuthenticationToken supAuthenticationToken = new AuthenticationToken(username, password, roleType);

        setDetails(request, supAuthenticationToken);
        Authentication authentication = this.getAuthenticationManager().authenticate(supAuthenticationToken);
        if (authentication != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            long authorId = 0;
            if (userDetails instanceof Author) {
                authorId = ((Author) userDetails).getId();
            }
            CookieHelper.setCookie(response, "authorId", String.valueOf(authorId), SysConstant.COOKIE_DOMAIN);
        }


        return authentication;
    }

    public void setRoleTypeParameter(String roleTypeParameter) {
        this.roleTypeParameter = roleTypeParameter;
    }

    protected int obtainRoleType(HttpServletRequest request) {
        String param = request.getParameter(roleTypeParameter);
        if (StringUtils.isEmpty(param)) {
            return 0;
        }
        return Integer.parseInt(param);
    }
}
