package com.rengu.machinereadingcomprehension.Configuration;

import com.rengu.machinereadingcomprehension.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
//@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;

    @Autowired
    public WebSecurityConfig(UserService userService) {
        this.userService = userService;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 关闭CSRF
        http.csrf().disable();
        // 启用CORS
        http.cors();
        // 关闭Session鉴权
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // 启用http basic验证
        http.httpBasic();
        // 请求全部需要鉴权认证
        http.authorizeRequests().antMatchers(HttpMethod.POST, "/users").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.POST, "/users/forgetpasswordcheck").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.POST, "/users/forgetpassword").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.GET, "/users/ranking").permitAll();
        http.authorizeRequests().anyRequest().authenticated();
    }
}
