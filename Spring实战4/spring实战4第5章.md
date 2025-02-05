---
title: spring实战第5章
date: 2020-07-06 18:45:13
tags: spring
---

## 第五章，构建Spring Web应用程序

此章讲解Spring MVC起步，编写基本的控制器，接受请求的输入，处理表单。

<!--more-->

### 5.0.各种注解

#### 1.@EnableWebMvc

@EnableWebMvc注解，会启动Spring MVC。

```java
@EnableWebMvc       //启动Spring MVC
public class WebConfig{
}
```

#### 2.@Controller

@Controller注解说明它是一个控制器。

#### 3.@RequestMapping

方法上添加了@RequestMapping注解的类，这个注解声明了它们所要处理的请求。

```java
@Controller          //声明为一个控制器
public class HomeController {
    @RequestMapping(value="/",method=GET)     //处理对“/”的GET请求
    public String home(){
        return "home";          //返回的视图名为home
    }
} 
```

### 5.1.Spring MVC起步

spring将请求在调度Servlet，处理器映射（handler mapping），控制器以及视图解析器（view resolver）之间移动。

请求是从客户端出发，经过Spring MVC中的组件，最终再返回到客户端的。

#### 5.1.1.跟踪Spring MVC的请求

请求离开浏览器时，会带有用户请求内容的信息，至少包含请求的URL。过程分为7步。

1.请求旅程的第一站是spring的DispatcherServlet（程序调度Servlet，前端控制器），spring MVC所有请求都会通过一个前段控制器（front controller）Servlet，前段控制器是常用的Web应用程序模式。。

2.DispatcherServlet的任务是将请求发送给Spring MVC控制器（controller）,控制器是一个用于处理请求的spring组件，因为应用程序有可能会有多个控制器，因此DispatcherServlet会查询一个或多个处理器映射（handler mapping）来确定请求的下一站是哪里。处理器映射会根据请求所携带的URL信息来进行决策。

3.一旦选择了合适的处理器，DispatcherServlet会将请求发送给选择的控制器，到了控制器，请求会卸下负载（用户提交的信息）并耐心等待控制器处理处理这些信息。

4.控制器在完成逻辑处理后，通常会产生一些信息，这些信息需要返回给用户并在浏览器上显示。这些信息被称为模型（modle）。这些信息会以用户友好的方式进行格式化，所以，信息需要发送给一个视图（view）比如JSP。

5.控制器所做的最后一件事就是将模型打包，并且标示出用于渲染输出的视图名。它接下来会将请求连同模型和视图名发送回DispatcherServlet。

6.DispatcherServlet将会使用视图解析器（view resolver）来将逻辑视图名匹配为一个特定的视图实现。比如JSP。在这里，它交付模型数据，请求的任务就完成了。视图将使用模型数据渲染输出，这个输出会通过响应对象传递给客户端。

#### 5.1.2.搭建Spring MVC

##### 配置DispatcherServlet

DispatcherServlet是Spring MVC的核心，负责将请求路由到其他的组件中。传统的方式是将servlet配置到web.xml中，这是方法之一，

但是我们使用java来配置在Servlet容器中。

```java
//扩展AbstractAnnotationConfigDispatcherServletInitializer的任意类都会自动的配置DispatcherServlet和spring应用上下   文。spring应用的上下文会位于应用程序的Servlet上下文之中。
public class SpittrWebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    @Override
    protected String[] getServletMapping(){
        return new String[] { "/" };         //将一个或多个路径映射到DispatcherServlet上。"/"是应用的默认Servlet
    }                                        //根目录，它会处理进入应用的所有请求。
    @Override
    protected Class<?>[] getRootConfigClasses(){
        return new Class<?>[] {RootConfig.class};      //指定配置类
    } 
@Override
    protected Class<?>[] getServletConfigClasses(){
        return new Class<?>[] {WebConfig.class};      //指定配置类
    }
}
```

##### 启动Spring MVC

启动Spring MVC组件的方法不止一种。可以通过XML进行配置的，但我们优先使用java配置。

最简单的Spring MVC配置就是一个带有@EnableWebMvc注解的类。

```java
@EnableWebMvc //启动Spring MVC
public class WebConfig{
}
```

### 5.2.编写基本的控制器

在Spring MVC中，控制器只是方法上添加了@RequestMapping注解的类，这个注解声明了它们所要处理的请求。可以在类上添加@Controller注解说明它是一个控制器。

```java
@Controller          //声明为一个控制器
public class HomeController {
    @RequestMapping(value="/",method=GET)     //处理对“/”的GET请求
    public String home(){
        return "home";          //返回的视图名为home
    }
} 
```

还可以将@RequestMapping拆分，将其路径映射部分放在类级别上。当控制器在类级别上添加@RequestMapping注解时，这个注解会应用到控制器的所有处理方法上。

```java
@Controller //声明为一个控制器
@RequestMapping("/")           //将控制器映射到"/"
public class HomeController {
    @RequestMapping(method=GET)     //处理GET请求
    public String home(){
        return "home";          //返回的视图名为home
    }
} 
```

### 5.3.接受参数的输入

Spring MVC允许以多种方式将客户端中的数据传送到控制器的处理器方法中。

查询参数（Query Parameter）

表单参数（Form Parameter）

路径参数（Path Variable）

### 5.4.校验表单

java校验API定义了多个注解，这些注解可以放在属性上。具体上网搜索。

```java
public class Spitter{
    private Long id;
     
    @NotNull        //非空
    @Size(min=5,max=16)  //5-16个字符
    private Sring username;
    
}
```

启动校验用@Valid注解。

```java
@RequestMapping(value="register",method=POST)
public String processRegistration(
    @Valid Spitter spitter,Errors errors) {      //校验Spitter输入
        if(errors.hasErrors()){
            return "registerForm";       //如果校验出现错误，则重新返回表单
        }
    spitterRepository.save(spitter);
    return "redirect:/spitter/" + spitter.getUsername();
}
```

