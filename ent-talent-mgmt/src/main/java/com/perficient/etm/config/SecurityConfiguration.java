package com.perficient.etm.config;

import com.perficient.etm.security.*;
import com.perficient.etm.security.ldap.CustomLdapUserDetailsMapper;
import com.perficient.etm.web.filter.CsrfCookieGeneratorFilter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.ldap.LdapAuthenticationProviderConfigurer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.csrf.CsrfFilter;

import javax.inject.Inject;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Inject
    private Environment env;

    @Inject
    private AjaxAuthenticationSuccessHandler ajaxAuthenticationSuccessHandler;

    @Inject
    private AjaxAuthenticationFailureHandler ajaxAuthenticationFailureHandler;

    @Inject
    private AjaxLogoutSuccessHandler ajaxLogoutSuccessHandler;

    @Inject
    private Http401UnauthorizedEntryPoint authenticationEntryPoint;

    @Inject
    private RememberMeServices rememberMeServices;
    
    @Inject
    private CustomLdapUserDetailsMapper ldapUserDetailsMapper;
    
    @Inject
    private LdapAuthenticatorPostProcessor authenticatorPostProcessor;
    
    @Value("${spring.ldap.domain}")
    private String ldapDomain;

    @Value("${spring.ldap.port}")
    private int ldapPort;
    
    @Value("${spring.ldap.root}")
    private String ldapRoot;
    
    @Value("${spring.ldap.accountdn}")
    private String ldapAccountDn;
    
    @Value("${spring.ldap.password}")
    private String ldapPassword;

    @Inject
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        LdapAuthenticationProviderConfigurer<AuthenticationManagerBuilder> ldapAuthentication = auth.ldapAuthentication();
        ldapAuthentication.userDetailsContextMapper(ldapUserDetailsMapper);
        ldapAuthentication.addObjectPostProcessor(authenticatorPostProcessor);
    	
        if (env.acceptsProfiles(Constants.SPRING_PROFILE_PRODUCTION)) {
            ldapAuthentication
                .userDnPatterns("cn={0},ou=Employees," + ldapRoot, "cn={0},ou=Test,ou=IT," + ldapRoot)
                .groupSearchBase("ou=Groups," + ldapRoot)
                .contextSource()
                    .url("ldaps://" + ldapDomain + ":" + ldapPort + "/")
                    .managerDn(ldapAccountDn + "," + ldapRoot)
                    .managerPassword(ldapPassword);
    	} else {
    	    ldapAuthentication
    	        .userDnPatterns("cn={0},ou=people")
    	        .groupSearchBase("ou=groups")
    	        .contextSource()
    	            .root(ldapRoot)
    	            .ldif("classpath:auth/users-dev.ldif");
    	}
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
            .antMatchers("/scripts/**/*.{js,html}")
            .antMatchers("/bower_components/**")
            .antMatchers("/i18n/**")
            .antMatchers("/assets/**")
            .antMatchers("/swagger-ui/**")
            .antMatchers("/test/**")
            .antMatchers("/console/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .addFilterAfter(new CsrfCookieGeneratorFilter(), CsrfFilter.class)
            .exceptionHandling()
            .authenticationEntryPoint(authenticationEntryPoint)
        .and()
            .rememberMe()
            .rememberMeServices(rememberMeServices)
            .key(env.getProperty("jhipster.security.rememberme.key"))
        .and()
            .formLogin()
            .loginProcessingUrl("/api/authentication")
            .successHandler(ajaxAuthenticationSuccessHandler)
            .failureHandler(ajaxAuthenticationFailureHandler)
            .usernameParameter("j_username")
            .passwordParameter("j_password")
            .permitAll()
        .and()
            .logout()
            .logoutUrl("/api/logout")
            .logoutSuccessHandler(ajaxLogoutSuccessHandler)
            .deleteCookies("JSESSIONID")
            .permitAll()
        .and()
            .headers()
            .frameOptions()
            .disable()
            .authorizeRequests()
                .antMatchers("/api/authenticate").permitAll()
                .antMatchers("/api/logs/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/api/**").authenticated()
                .antMatchers("/metrics/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/health/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/trace/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/dump/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/shutdown/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/beans/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/configprops/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/info/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/autoconfig/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/env/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/trace/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/api-docs/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/protected/**").authenticated();
    }

    @EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true)
    private static class GlobalSecurityConfiguration extends GlobalMethodSecurityConfiguration {
    }
}
