---
title: spring实战4第15章
date: 2020-07-11 23:05:24
tags: spring
---

## 第十五章，使用远程服务

此章讲解Spring远程调用概览，使用RMI，使用Hessian和Burlap发布远程服务。

<!--more-->

作为一个java开发者，我们有多种可以使用的远程调用技术。

1.远程放大调用（RMI）。

2.Caucho的Hessian和Burlap。

3.Spring基于HTTP的远程服务。

4.使用JAX-RPC和JAX-WS的Web Service。

<!--more-->

### 15.1.Spring远程调度概览

远程调用是客户端应用和微服务之间的会话。在客户端，它所需要的一些功能并不在该应用远程服务暴露这些功能。我们将应用提供桌面应用或移动端应用。为了实现这功能，需要把接口的基本功能能发布为远程服务。

其他应用与此应用之间的会话开始于客户端应用的一个远程过程调用（RPC）。RPC调用就是执行流从一个应用传递给另一个应用，理论上另一个应用部署在跨网络的一台远程机器上。Spring支持多种不同的RPC模型。

1.远程方法调用（RMI）：不考虑网络限制时(如防火墙)，访问/发布基于java的服务。

2.Hessian或Burlap：考虑网络限制时，通过HTTP访问/发布基于java的服务。Hession是二进制协议，而Burlap是基于XML的。

3.HTTP invoker：考虑网络限制，并希望使用基于XML或专有的序列化机制实现java序列化时，访问/发布基于Spring的服务。

4.JAX-RPC和JAX-WS：访问/发布平台独立的，基于SOAP的Web服务。

不管选择哪种远程调用模型，我们会发现Spring都提供了风格一样的支持。在所有的模型中，服务都作为你Spring所管理的bean配置到我们的应用中。这是通过一个代理工厂bean实现的。这个bean能将远程服务想本地对象一样装配到其他bean的属性中去。

### 15.2.使用RMI

Spring简化了RMI模型，它提供了一个代理工厂bean，能让我们把RMI服务像本地javaBean那样装配到我们的Spring应用中。Spring还提供了一个远程导出器，用来简化把Spring管理的bean转化为RMI服务的工作。

#### 15.2.1.导出RMI服务

如果你创建过RMI服务，应该会知道这会涉及如下几个步骤：
1.编写一个服务实现类，类中的方法必须抛出java.rmi.RemoteException异常。
2：创建一个继承于java.rmi.Remote的服务接口。
3：运行RMI编译器（rmic），创建客户端stub类和服务类skeleton类。
4：启动一个RMI注册表，以便持有这些服务。
5：在RMI注册表中注册服务。

##### 在Spring中配置RMI服务

Spring提供了更简单的方式来发布RMI服务，只需要简单地编写实现服务功能的POJO就可以了，Spring会处理剩下的其他事项。

我们将要创建的RMI服务需要发布SpitterService接口的方法。如下展示了该接口定义。

```
public interface SpitterService{
           ------}

```

RmiServiceExporter可以把任意Spring管理的bean发布为RMI服务。它把bean包装在一个适配器类中，然后通过适配器被绑定到RMI注册表中，并且代理到服务类的请求-----在本例中服务类也就是SpitterServiceImpl。如下使用@Bean方法进行配置。

```java
@Bean
public  RmiServiceExporter  rmiExporter(SpitterService){
     RmiServiceExporter  rmiExporter = new RmiServiceExporter();
          rmiExporter.setService(spitterService);     //将该bean发布为一个RMI服务
          rmiExporter.setServiceName("SpitterService");  //命名了RMI服务
          rmiExporter.setServiceInterface(SpitterService.class);     //指定此服务所实现的接口
             return  rmiExporter; }
```

把spitterService bean设置到service属性中，表明RmiServiceExporter要把该bean发布为一个RMI服务。ServiceName属性命名了RMI服务。ServiceInterface属性指定了此服务所实现的接口。

默认情况下，RmiServiceExporter会绑定到本地机器1099端口的RMI注册表，不存在则会启动一个。如果希望绑定到别的端口或主机上的RMI注册表，那么可以通过registryPort和regisrtHost属性来指定。

```java
@Bean
public RmiServiceExporter rmiExporter(SpitterService spitterService){
    RmiServiceExporter rmiExporter = new  RmiServiceExporter();
          rmiExporter.setService(spitterService);
          rmiExporter.setServiceName("SpitterService");
          rmiExporter.setServiceInterface(SpitterService.class);
          rmiExporter.setregisrtHost("rmi.spitter.com");  //rmi.spitter.com主机
          rmiExporter.setregistryPort(1199);                  //1199端口
               return rmiExporter;    }
```

这就是我们使用Spring把某个bean转变为RMI服务所需要做的全部工作。现在Spitter服务已经导出为RMI服务。我们可以为应用创建其他的用户界面或邀请第三方使用此RMI服务创建新的客户端。如果使用Spring，客户端开发访问应用的RMI服务会非常容易。让我们转换一下视角来看看如何编写Spring RMI服务的客户端。

#### 15.2.2.装配RMI服务

传统上，RMI客户端必须使用RMI  API的Naming类从RMI注册表中查找服务，但是问题很大。Spring的RmiProxyFactoryBean是一个工厂bean，该bean可以为RMI服务创建代理。使用RmiProxyFactoryBean引用SpitterService的RMI服务是非常简单的，只需要在客户端的Spring配置中增加如下的@Bean方法。

```java
@Bean
public  RmiProxyFactoryBean spitterService(){
    RmiProxyFactoryBean  rmiProxy = new RmiProxyFactoryBean();
          rmiProxy.setServiceUrl("rmi://localhost/SpitterService"); //本地服务名
          rmiProxy.setServiceInterface(SpitterService.class);
              return  rmiProxy;      }
```

ServiceUrl属性设置服务的URL，在这里，服务名被设置为SpitterService。ServiceInterface属性设置服务提供的接口。

现在已经把RMI服务声明为Spring管理的bean，我们就可以把它作为依赖装配进另一个bean中。假设客户端需要使用Spitter服务为指定的用户获取Spittle列表，我们可以使用@Autowired注解把服务代理装配进客户端中。

```java
@Autowired 
SpitterService spitterService;
```

我们还可以像本地bean一样调用它的方法。

```java
 public  List<Spittle> getSpittles(String userName){
      Spitter spitter = spitterService.getSpitter(userName);
           return  spitterService.getSpittlesForSpitter(spitter); }
```

RMI存在某些限制，首先很难穿越防火墙。其次，RMI是基于java的，这意味着通过网络传输的对象类型必须要保证在调用两端的java运行时中是完全相同的版本。

为了解决这些问题，我们可以在Spring中使用Hession和Burlap处理远程服务。

### 15.3.使用Hession和Burlap发布远程服务

Hession和Burlap是基于HTTP的轻量级远程服务解决方案。你可能想知道如何在Hession和Burlap之间做出选择，很多程度上，它们是一样的，唯一的区别在于Hession的消息是二进制的，而Burlap的消息是XML。Hession在宽带山更有优势，但是如果我们更注重可读性（出于调试的目的）或者我们的应用需要与没有Hession实现的语言交互，那么Burlap的XML消息回事更好的选择。

#### 15.3.1.使用Hessian和Burlap导出bean的功能

像之前一样，我们希望把SpitterServiceImpl类的功能发布为远程服务-这次是一个Hession服务。

##### 导出Hession服务

与之前一样，把Spitter服务发布为Hession服务，先配置一个RmiServiceExporter，之后需要配置另一个导出bean，这不过这次是HessianServiceExporter。HessianServiceExporter它把POJO的public方法发布为Hession服务的方法。

```java
 @Bean
 public HessianServiceExporter  hessianExportedSpitterService(SpitterService  service){
     HessianServiceExporter  exporter = new HessianServiceExporter();
            exporter.setService(service);               //实现了这个服务的bean引用
            exporter.setServiceInterface(SpitterService.class); //实现了SpitterService接口
                return  exporter;  }


```

##### 配置Hession控制器

由于Hesson是基于HTTP的，所以HessianServiceExporter实现为一个Spring MVC控制器。这意味着为了使用导出的Hessian服务，我们需要执行两个额外的配置步骤。

1：在web.xml中配置Spring的DispatcherServlet，并把我们的应用部署为Web应用。
2：在Spring的配置文件中配置一个URL处理器，把Hessian服务的URL分类给对应的Hessian服务bean。

我们已经在第5张学习了如何配置Spring的DispatcherServlet。我们只需要在DispatcherServlet配置一个Servlet映射来拦截后缀为"*.service"的URL。

```xml
<servlet-mapping>
   <servlet-name>Spitter</servlet-name>
   <url-pattern>*.service</url-pattern>
</servlet-mapping>
```

如果你在java中通过实现WebApplicationInitializer来配置DispatcherServlet的话，那么需要将URL模式作为映射添加到ServletRegistration.Dynamic中，在将DispatcherServlet添加到容器的时候，我们能够得到ServletRegistration.Dynamic对象。

```java
ServletRegistration.Dynamic dispatcher = container.addServlet(
   "appServlet",new DispatcherServlet(dispatcherServletContext));
       dispatcher.setLoadOnStertup(1);
       dispatcher.addMapping("/");
       dispatcher.addMapping("*.service");
```

如果你通过扩展AbstractDispatcherServletInitalizer或AbstractAnnotationConfigDispatcherServletInitializer的方式配置DispatcherServlet，那么在重载getServletMappings()的时候，需要包含该映射。

```java
@Override
 protected String[] getServletMapping(){
     return new String[] {"/","*.service"};
 }
```

这样配置后，任何以".service"结束的URL请求都将由DispatcherServlet处理，它会把请求传递给匹配这个URL的控制器。我们还需配置一个URL映射来确保DispatcherService把请求转给hessianSpitterService。如下的SimpleUrlHandleMapping bean可以做到这一点。

```java
@Bean
public  HandlerMapping  hessianMapping(){
   SimpleUrlHandlerMapping mapping = new  SimpleUrlHandlerMapping();
          properties  mappings = new Properties();
          mappings.setProperty("/Spitter.service","hessianExportedSpitterService");
          mapping.setMappings(mappings);
               return  mapping;     }                                                            
```

如果不喜欢Hessian的二进制协议，我们还可以选择使用Burlap基于XML的协议。

##### 导出Burlap服务

BurlapServiceExporter与HessianServiceExporter实际上都是相同的。下面的bean定义展示了如何使用BurlapServiceExporter把Spitter服务导出为一个Burlap服务。

```java
@Bean
public  BurlapServiceExporter  burlapExportedSpitterService(SpitterService service){
   BurlapServiceExporter  exporter = new BurlapServiceExporter();
         exporter.setService(service);
         exporter.setServiceInterface(SpitterService.class);
            return  exporter;       }   
```

#### 15.3.2.访问Hession/Burlap服务

在客户端代码中，基于RMI的服务于Hession的服务之间唯一的差别在于要使用Spring的HessianproxyFactoryBean来代替RmiProxyFactoryBean。客户端调用基于Hessian的Spitter服务可以用如下的配置声明。

```java
@Bean
public  HessianproxyFactoryBean spitterService(){
    HessianproxyFactoryBean  rmiProxy = new HessianproxyFactoryBean();
          rmiProxy.setServiceUrl("rmi://localhost/SpitterService"); //本地服务名
          rmiProxy.setServiceInterface(SpitterService.class);
              return  rmiProxy;      }
```

Burlap也一样，使用BurlapProxyFactoryBean来代替HessianProxyFactoryBean。

```java
@Bean
public  BurlapProxyFactoryBean spitterService(){
    BurlapProxyFactoryBean  rmiProxy = new BurlapProxyFactoryBean();
          rmiProxy.setServiceUrl("rmi://localhost/SpitterService"); //本地服务名
          rmiProxy.setServiceInterface(SpitterService.class);
              return  rmiProxy;      }
```

因为Hessian和Burlap都是基于HTTP的，当传递过来的RPC消息中包含序列化对象时会有一些问题。我们还有一个两全其美的解决方案。让我们看一下Spring的HTTP invoker，它基于HTTP提供了RPC(与Hession/Burlap一样)，同时又使用了java的对象化序列化机制（像RMI一样）。

### 15.4.使用Spring的HttpInvoker

#### 15.4.1.将bean导出为HTTP服务

为了把应用服务导出为一个基于Http invoker的服务，我们需要像下面的配置一样声明HttpInvokerServiceExporter bean。

```java
@Bean
public  HttpInvokerServiceExporter httpExportedSpitterService(SpitterService  service){
      HttpInvokerServiceExporter  exporter = new HttpInvokerServiceExporter();
              exporter.setService(service);
              exporter.serServiceInterface(SpitterService.class);
                return  exporter;       }
```

因为HttpInvokerServiceExporter是一个Spring MVC控制器，我们需要建立一个URL处理器，映射HTTP URL到对应的服务上，就像Hessian和Burlap导出器做的一样。

```java
@Bean
public HandleMapping httpInvokerMapping(){
    SimpleUrlHandlerMapping mapping = newSimpleUrlHandlerMapping();
    properties mapping = new Properties();
    mapping.setProperty("/spitter.service","httpExportedSpitterService");
    mapping.setMappings(mappings);
      return mapping;  }
```

#### 15.4.2.通过HTTP访问服务

通过Http访问服务类似之前的服务代理。将HttpInvokerProxyFactoryBean配置为bean。

```java
@Bean
public  HttpInvokerProxyFactoryBean spitterService(){
    HttpInvokerProxyFactoryBean  Proxy = new HttpInvokerProxyFactoryBean();
           Proxy.setServiceUrl("http://localhost:8080/Spitter/spitter.service"); 
           Proxy.setServiceInterface(SpitterService.class);
               return  Proxy;                                             }
```

要记住HTTP invoker有一个重大的限制：它只是一个Spring框架所提供的的远程调用解决方案，这意味着客户端和服务器端必须是Spring应用。

未完待续。