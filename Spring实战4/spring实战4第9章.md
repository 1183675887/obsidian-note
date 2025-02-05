---
title: spring实战4第9章
date: 2020-07-07 17:15:32
tags: spring
---

## 第九章，保护Web应用

此章讲解拦截请求，认证用户，保护视图。

<!--more-->

### 9.0.各种注解

#### 1.@EnableWebSecurity

@EnableWebSecurity注解来启动Wen安全功能。

```java
@Configuration
@EnableWebSecurity        //启动Web安全性
public class SecurityConfig extends WebSecurityConfigurerAdapter{    
}
```

#### 2.@EnableWebMvcSecurity

如果碰巧使用Spring MVC开发的那么考虑使用@EnableWebMvcSecurity替代它。

```java
@Configuration
@EnableWebMvcSecurity        //启动Spring MVC安全性
public class SecurityConfig extends WebSecurityConfigurerAdapter{    
}
```



### 9.1.Spring Security简介

Spring Security是为基于Spring的应用程序提供声明式安全保护的安全性框架。它能够在Web请求和方法调用级别处理身份认证和授权。

#### 9.1.1.理解Spring Security的模块

Spring Security分为11个模块，一下之列出常用的几种

ACL：支持通过访问控制列表（ACL）为域对象提供安全性。

切面（Aspects）：一个很小的模块，当使用Spring Security注解时，会使用基于AspectJ的切面，而不是使用标准的Spring AOP。

配置（configuration）：包含通过XML和java配置Spring Security的功能支持。

核心（Core）：提供Spring Security基本库。

加密（Cryptography）：提供了加密和密码编码的功能。

LDAP：支持基于LDAP进行认证。

Web：提供了Spring Security基于Filter的Web安全性支持。

#### 9.1.2.过滤Web请求

Spring Security借助一系列Servlet Filter来提供各种安全性功能。我们需要在web.xml或WebApplicationInlizer中配置Fillter。

DelegatingFilterProxy是一个特殊的Servlet Filter，它将工作委托给一个javax.servlet.Filter实现类，这个实现类作为一个<bean>注册在Spring的上下文中。

在web.xml中配置Servlet和Filter的话，可以使用<filter>元素。

```xml
<filter>
    <filter-name>springSercurityFilterChain</filter-name>  //名为springSercurityFilterChain的Filter bean
    <filterclass>
        org.springframework.web.filter.DelegatingFilterProxy //DelegatingFilterProxy会过滤给它
    </filterclass>
</filter>
```

如果以java配置的话，我们需要借助WebApplicationInitializer来配置DelegatingFilterProxy

```java
public class SecurityWebInitializer extends AbstractSecurityWebApplicationInitializer{}
```

#### 9.1.3.编写简单的安全性配置

用@EnableWebSecurity注解来启动Wen安全功能。但它本身并没有什么用处，Spring Security必须配置在一个实现了WebSecurityConfigurer的bean中或者扩展WebSecurityConfigurer。

```java
@Configuration
@EnableWebSecurity        //启动Web安全性
public class SecurityConfig extends WebSecurityConfigurerAdapter{    
}
```

如果碰巧使用Spring MVC开发的那么考虑使用@EnableWebMvcSecurity替代它。

```java
@Configuration
@EnableWebMvcSecurity        //启动Spring MVC安全性
public class SecurityConfig extends WebSecurityConfigurerAdapter{    
}
```

@EnableWebMvcSecurity还配置了一个Spring MVC参数解析器，这样的话处理器就能够通过带有@AuthenticationPrincipal注解的参数获得认证用户的username等。

我们可以通过重载WebSecurityConfigurerAdapter的三个方法来配置Web安全性。

configure(WebSecurity)：通过重载，配置Spring Security的Filter链

configure(HttpSecurity)：通过重载，配置如何通过拦截器保护请求。

configure(AuthenticationManagerBuilder)：通过重载，配置user-detail服务。

```java
protected void configure(HttpSecurity http) throws Exception{
    http
        .authorizeRequests()              //authorizeRequests()与anyRequest().authenticated()
          .anyRequest().authenticated()    //就会要求所有进入应用的Http请求都要进行认证。
          .and()
        .formLogin().and()
        .httpBasic();
```

由于没有重载configure(AuthenticationManagerBuilder)方法，所有没有用户存储认证过程。

为了让Spring Security满足我们应用的需求，还需要再添加一点配置。

1.匹配用户存储。

2.指定哪些请求需要认证，哪些请求不需要认证，以及所需要的权限。

3.提供一个自定义的邓肯页面，替换原来简单的默认登录页。

### 9.2.选择查询用户详情信息的服务

#### 9.2.1.使用基于内存的用户存储

配置用户存储的最简单的方法就是重载configure(AuthenticationManagerBuilder)，AuthenticationManagerBuilder有多个方法可以用来配置Spring Security对认证的支持。

```java
@Configuration
@EnableWebMvcSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
        auth
            .inMemoryAuthentication()     //启用内存用户存储
            //withUser()方法为内存用户添加新的用户。
              .withUser("user").password("password").roles("USER").and()  
            .withUser("admin").password("password").roles("USER","ADMIN"); 
                                                    // roles（）方法授予某个永辉一个或多个角色//
    }
}
```

对于调试和开发人员来讲，基于内存的用户存储是很有用的，但是对于生产级别的应用来说，最好将用户数据存储在某种类型的数据库之中。

#### 9.2.2.基于数据库表进行认证

用户数据通常会存储在关系型数据库中，并通过JDBC访问。Spring Security 使用以JDBC为支撑的用户存储，我们可以使用

jdbcAuthentication（）方法。

```java
@Autowired
DataSource dataSource;
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception{
    auth
        .jdbcAuthentication()
          .dataSource(dataSource);
}
```

##### 重写默认的用户查询功能

Spring Security内部有默认的SQL查询语句。

```java
public static final String DEF__USER__BY__USERNAME__QUERY=
    "select username,password,enabled"+
    "freom users"+
    "where username=?"
```

我们的数据库可能和默认的不一样，所以我们可以按照如下的方式来配置自己的查询。

```java
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception{
    auth
        .jdbcAuthentication()
          .dataSource(dataSource);
          .uesesByUsernameQuery(    //调用方法重写
            "select username,password,true"+
            "from Spitter where username=?")
          .authoritiesByUsernameQuery(
          "select username,'POLE_USER' from Spitter where username=?");
}
```

所有查询都会将用户名作为唯一的参数。认证查询会选取用户名，密码，以及启动状态信息。权限查询会选取若干行包含该用户以及权限信息的数据。

##### 使用转码后的密码

因为密码明文存储的话，很容易遭受到黑客的攻击，所有我们需要借助passwordEncoder()方法指定一个密码转换器（encoder）。

```java
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception{
    auth
        .jdbcAuthentication()
          .dataSource(dataSource);
          .uesesByUsernameQuery(    //调用方法重写
            "select username,password,true"+
            "from Spitter where username=?")
          .authoritiesByUsernameQuery(
          "select username,'POLE_USER' from Spitter where username=?")
          .passwordEncoder(new StandarPasswordEncoder("53cr3t"));
}
```

SPring Secutiry的加密模块包括了3个实现：BCryptPasswordEncoder,NoOpPasswordEncoder,和StandarPasswordEncoder。

#### 9.2.3.基于LDAP进行认证

为了让Spring Security使用基于LDAP的认证，我们可以使用ldapAuthentication()方法。这个方法类似于jdbcAuthentication()，只不过是LDAP版本。如下的configure()方法展现了LDAP认证的简单配置。

```java
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception{
    auth
        .ldapAuthentication()
           //这两个方法用来过滤条件
          .userSearchFilter("(uid={0})")     //搜索用户
          .groupSearchFilter("number={0}")   //搜索组
}
```

默认情况下，用户和组的基础查询都是空的，也就是会从根目录查起，但是我们可以指定查询基础来改变这个默认。

```java
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception{
    auth
        .ldapAuthentication()
        
          .userSearchBase("ou=people")  //会在名为people的组织单元下搜索
          .userSearchFilter("(uid={0})")
          .userSearchBase("ou=group")   //会在名为group的组织单元下搜索
          .groupSearchFilter("number={0}")   
}
```

##### 配置密码对比

如果希望通过密码进行对比认证，可以声明passwordCompare()方法来实现。

```java
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception{
    auth
        .ldapAuthentication()
      
          .userSearchBase("ou=people")  
          .userSearchFilter("(uid={0})")
          .userSearchBase("ou=group") 
          .groupSearchFilter("number={0}")  
          .passwordCompare();
}
```

默认情况下，登录表单中提供的密码将会与用户的LDAP条目的userPassword属性来进行对比。如果密码保存在别的属性中，可以通过

passwordAttribute()方法来声明密码属性的名称。

```java
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception{
    auth
        .ldapAuthentication()
      
          .userSearchBase("ou=people")  
          .userSearchFilter("(uid={0})")
          .userSearchBase("ou=group") 
          .groupSearchFilter("number={0}")  
          .passwordAttribute("passcode");  //指定要与给定密码进行对比的是"passcode"属性
}
```

##### 引用远程的LDAP服务器

默认情况下，Spring Security 的LDAP认证假设LDAP服务器监听本机的33389端口，但是，如果你的LDAP服务器在另一台机器上，那么可以使用contextSource()方法来配置这个地址。

```java
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception{
    auth
        .ldapAuthentication()
      
          .userSearchBase("ou=people")  
          .userSearchFilter("(uid={0})")
          .userSearchBase("ou=group") 
          .groupSearchFilter("number={0}")  
           //会返回一个ContextSourceBuilder对象，还提供了url()方式用来指定LDAP服务器的地址
          .contextSource()
             .url("ldap://habuma.com:389/dc=habuma,dc=com")
}
```

##### 配置嵌入式的LDAP服务器

如果没有现成的LDAP服务器供认证使用，我们可以通过root()方法指定嵌入式服务器的根前缀就可以了。

```java
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception{
    auth
        .ldapAuthentication()
      
          .userSearchBase("ou=people")  
          .userSearchFilter("(uid={0})")
          .userSearchBase("ou=group") 
          .groupSearchFilter("number={0}")  
          .contextSource()
        
             .root("dc=habuma,dc=com")
}
```

当LDAP服务启动时，它会尝试在根目录下查找LDIF文件来加载数据。如果你希望指定加载某个LDIF文件，通过ldif()方法。

```java
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception{
    auth
        .ldapAuthentication()
      
          .userSearchBase("ou=people")  
          .userSearchFilter("(uid={0})")
          .userSearchBase("ou=group") 
          .groupSearchFilter("number={0}")  
          .contextSource()
             .root("dc=habuma,dc=com")
        
             .ldif("classpath:users.ldif")  //会从users.ldif文件中加载内容
}
```

#### 9.2.4.配置自定义的用户服务

假设我们需要认证的用户存储在非关系型数据库中，如Mongo或Neo4j，我们需要提供一个自定义的UserDetailsService接口实现。

```java
public interface UserDetailService{
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException；
}
```

我们需要做的就是实现loadUserByUsername()方法。loadUserByUsername()方法会返回代表给定用户的UserDetails对象。

### 9.3.拦截请求

在任何应用中，不是所有的请求都需要同等程度的保护。对每个请求进行细粒度安全性控制的关键在于重载configure(HttpSecurity)方法。

```java
@Override
protected void configure(HttpSecurity http) throws Exception{
    http
        .authorizeRequest()
          .antMatchers("/spitters/me").authenticated()   //对"/spitters/me"路径的请求进行认证
          .antMatchers(HttpMethod.POST,"/spittles").authenticated() //对"/spittles"路径的HTTp POST进行认证
          .anyRRequest().permitAll();   //说明其他的请求都是允许的，不需要认证。
}
```

如下有几种常用的方法。

1.authenticated()：允许认证过的用户访问。

2.permitAll()：无条件允许访问。

3.hasAuthority()：如果用户具备给定权限的话，就允许访问。

4.hasRole(String)：如果用户具备给定角色的话，就允许访问。

5.hasIpAddress(String)：如果请求来自于给定IP地址的话，就允许访问。

修改之前的方案，要求用户不仅需要认证，还要具备ROLE_SPITTER权限。

```
@Override
protected void configure(HttpSecurity http) throws Exception{
    http
        .authorizeRequest()
          .antMatchers("/spitters/me").hasAuthority("ROLE_SPITTER")  //需要具备ROLE_SPITTER权限
          .antMatchers(HttpMethod.POST,"/spittles").hasAuthority("ROLE_SPITTER")
          .anyRRequest().permitAll();   //说明其他的请求都是允许的，不需要认证。
}
```

很重要的一点就是将最为具体的请求路径放在前面，而最不具体的路径(如anyRequest())放在最后面。如果不这样做的话，那不具体的路径配置将会覆盖更为具体的路径配置。

#### 9.3.2.强制通道的安全性

使用HTTP提交数据是一件具有风险的事情，所以有时候需要HTTPS通道传输。传递到configure()方法中的HttpSecurity对象，除了具有authorizeRequests()方法外，还有一个requiresChannel()方法。这个方法能够为各种URL模式声明所要求的通道。

```java
@Override
protected void configure(HttpSecurity http) throws Exception{
    http
        .authorizeRequest()
          .antMatchers("/spitters/me").hasRole("SPITTER")  
          .antMatchers(HttpMethod.POST,"/spittles").hasRole("SPITTER")
          .anyRRequest().permitAll(); 
        .and()
        .requiresChannel()
            .antMatchers("/spitter/form").requiresSecure() // requiresSecure()方法为HTTPS传送
}
```

有些页面并不需要HTTPS传送，可以把它改为HTTP。

```java
@Override
protected void configure(HttpSecurity http) throws Exception{
    http
        .authorizeRequest()
          .antMatchers("/spitters/me").hasRole("SPITTER")  
          .antMatchers(HttpMethod.POST,"/spittles").hasRole("SPITTER")
          .anyRRequest().permitAll(); 
        .and()
        .requiresChannel()
            .antMatchers("/").requiresInecure() // requiresInecure()方法为HTTP传送
}
```

#### 9.3.3.防止跨域请求伪造

从Spring Security 3.2开始，默认就会启动CRRF防护。

### 9.4.认证用户

#### 9.4.2.启动HTTP Basic认证

基于表单的认证是比较理想的。但是如果将Web应用的页面转化为RESTful API。就需要另一种方式了。

HTTP Basic认证会直接通过HTTP请求本身，对要访问应用的用户进行认证。在RESTful客户端向它使用的服务进行认证的情景中，这种方式比较适合。

如果要启动HTTP Basic认证的话，只需要在configure()方法所传入的HttpSecurity对象上调用httpBasic()。另外，还可以调用realmName()方法指定域。

```java
@Override
protected void configure(HttpSecurity http) throws Exception{
    http
        .formLogin()
          .loginPage("/login")
        .and()
        .httpBasic()    //启动HTTP Basci认证
          .realmName("/Spitter")   //指定域
        .and()
}
```

#### 9.4.3.启动Remember-me功能

对于应用程序来说，能够对用户认证是非常重要的。你需要登录过一次，应用就会记住你，当你再次回到应用就不要登录了。

Spring Security使得为应用添加Remember-me的功能变得非常容易。为了启动这项功能，只需在configure()方法传入的HttpSecurity对象上调用rememberMe()即可。

```java
@Override
protected void configure(HttpSecurity http) throws Exception{
    http
        .formLogin()
          .loginPage("/login")
        .and()
        .rememberMe()
          .tokenValiditySeconds(2419200)   //指定这个token最多4周(24199200秒)有效
        .key("spittrKey")          //私钥
}
```

#### 9.4.4.退出

如果你希望用户被重定向到其他的页面，如应用的首页，那么可以在configure()中进行如下的配置。

```java
@Override
protected void configure(HttpSecurity http) throws Exception{
    http
        .formLogin()
          .loginPage("/login")
        .and()
        .logout()       //logout()提供了配置退出的行为的方法
        .logoutSuccessUrl("/")       //调用logoutSuccessUrl()表明在退出成功后，浏览器需要重定向到"/"
}
```

除了logoutSuccessUrl()方法之外，你可能还希望重写默认的LogoutFilter拦截路径。我们可以通过logoutUrl()方法实现这一功能

```java
.logout()     
  .logoutSuccessUrl("/") 
  .logoutUrl("/signout")
```

