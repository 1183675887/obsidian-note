---
title: spring实战4第14章
date: 2020-07-09 20:15:45
tags: spring
---

## 第十四章，保护方法应用

此章讲解，使用注解保护方法，使用表达式实现方法级别的安全性

<!--more-->

我们可以同时保护应用的Web层以及场景后面的方法，这样就能保证如果用户不具备权限的话，就无法执行相应的逻辑。

### 14.1.使用注解保护方法

在Spring Security中实现方法级安全性的最常见办法是使用特点的注解，将这些注解应用到需要保护的方法上。
Spring Security提供了三种不同的安全注解。
1：Spring Security自带的@Secured注解。
2：JSR-250的@RolesAllowed注解。
3：表达式驱动的注解，包括@PreAuthorize，@PostAuthorize,@PreFilter和@PostFilter。

@Secured和@RolesAllowed方案非常类似，能够基于用户所授予的权限限制为对方的访问，当我们需要在方法上定义更灵活的安全规则时，Spring  Sercurity提供了@PreAuthorize和@PostAuthorize。而@PreFilter和@PostFilter能够过滤方法返回的以及传入方法的集合。

#### 14.1.1.使用@Secured注解限制方法调用

在Spring中，如果要启用基于注解的方法安全性，关键之处在于要在配置类上使用@EnableGlobalMethodSercurity。

```
@Configuration
@EnableGlobalMethodSercurity(securedEnabled=true)
public class MethodSecurityConfig  extends GlobalMethodSecurityConfiguration{

}
```

