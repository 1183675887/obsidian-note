---
title: spring实战4第6章
date: 2020-07-07 10:20:15
tags: spring
---

## 第六章，渲染Web视图

此章讲解理解视图解析，使用Thymeleaf。

<!--more-->

### 6.1.理解视图解析

控制器方法和视图的实现会在模型内容上达成一致，这是两者的最大关联。

Spring提供了一个名为VieResolver的接口。

```java
public interface ViewResolver{
    View resolveViewName(String viewName, Locale locale)   throws Exception;
}

```

当给resolviewName()方法传入一个视图名和Locale对象时，它会返回一个View实例。View是另外一个接口。

```java
public interface View{
    String getContentType();
    void render(Map<String, ?> model,
                HttpServletRequest request,
                HttpServletResponse reponse)   throws Exception
}
```

View接口的任务就是接受模型以及Servlet的request和response对象，并将结果渲染到response中，进而展现到浏览器中。

### 6.2.创建JSP视图

Spring提供了两种支持JSP视图的方式：

1.InternalResourceViewResolver会将视图名解析为JSP文件，

2.Spring提供了两个JSP标签库，一个用于表单到模型的绑定，另一个提供了通用的工具类特性。

#### 6.2.1.配置适用于JSP的视图解析器

InternalResourceViewResolver会遵循一个约定，会在视图名上添加前缀和后缀，进而确定一个Web应用中视图资源的物理路径。

```java
假设逻辑视图名为home.把JSP文件放在Web应用的"/WEB-INF/views"目录下。并且home页JSP名为home.jsp. 

  //前缀         //视图名   //后缀
/WEB-INF/views/  home     .jsp
    
```

在使用@Bean的注解的时候，可以如下的方式配置InternalResourceViewResolver，使其在解析视图时，遵循上述的约定。

```java
@Bean
public ViewResolver viewResolver(){
    InternalResourceViewResolver resolver = new InternalResourceViewResolver();
    resolver.setPrefix("/WEB-INF/views/");      //前缀
    resolver.setSuffix(".jsp");                 //后缀
    return resolver;                           
}
```

还可以使用XML的spring配置，那么按照如下的方式配置InternalResourceViewResolver。

```xml
<bean id="viewResolver"
      class"org.springframework.web.servlet.view.InternalResourceViewResolver"
      p:prefix="/WEB-INF/views/"       //前缀
      p:suffix=".jsp" / >              //后缀
```

未完待续。



