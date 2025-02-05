---
title: spring实战4第4章
date: 2020-07-06 10:35:38
tags: spring
---

## 第四章，面向切面编程

此章讲解什么是切面编程，通过切点来选择连接点，使用注解创建切面，在XML中声明切面。

<!--more-->

### 4.0.各种注解

#### 1.@Aspect

类使用@Aspect注解进行标注，说明类不仅是一个POJO，还是一个切面。类中方法都都使用注解来定义切面行为。

```java
@Aspect
 public  class  Audience{
                  
 @Before("execution(--)")       //在目标方法之前就开始会调用silence方法。
 public   void  silence(){
       -println(" xxx ");  }
 @Before("execution(--)")       //在目标方法之前就开始会调用silence方法。
  public   void take(){
        -println(" xxx ");  }
        }
```

#### 2.@Pointcut

因为切点表达式经常重复，所以使用@Pointcut注解，能够在一个@AspectJ切面内定义可重用的切点。

```java
@Aspect
public  class  Audience{                  
@Pointcut（"execution(** concert.Performance.perform(..))"）
 public   void   performance() { }        //performance为切点表达式的方法         

   @Before （"performance()")       //括号中为定义切点表达式的performance方法
   public   void  silence(){
         ---(--);      }
   @Before("performance()")       //括号中为定义切点表达式的performance方法
   public   void take(){
        ---(--);      }
         }
```

#### 3.@EnableAspectJAutoproxy

在配置类的类级别上通过使用@EnableAspectJAutoproxy注解启动自动代理功能。

```java
@EnableAspectJAutoproxy        //启动Aspect代理
@componentScan
 public   class  ConcertConfig{
    @bean
    public   Audience   audience（）{       //声明Audience  bean
       return  new   Audience（）；
                                  }
          }
```



### 4.1.什么是面向切面编程（AOP）

#### 4.1.1.定义AOP术语

描述切面的常用术语有通知（Advice），切点（pointcut），和连接点（join point）等。



1.通知（Advice）:定义了切面是什么以及如何使用。除了描述切面要完成的工作，通知还解决了何时执行这个工作的问题。

spring切面可以应用5种类型的通知：

前置通知（Before）：在目标方法被调用之前调用通知功能。

后置通知（After）：在目标方法完成之后调用通知，此时不会关心方法的输出是什么。

返回通知（After-returning）：在目标方法成功执行之后调用通知。

异常通知（After-throwing）：在目标方法抛出异常后调用通知。

环绕通知（Around）：通知包裹了被通知的方法，在被通知的方法调用之前和调用之后执行自定义的行为。



2.连接点（join point）：连接点是在应用执行过程能够插入切面的一个点。这个点可以是调用方法时，抛出异常时，甚至修改一个字段时。切面代码可以利用这些点插入到应用的正常流程之中，并添加新的行为。

3.切点（Pointcut）：如果说通知定义了切面的“什么”和“何时”的话，那么切点就定义了“何处”，切点的定义会匹配通知所要织入的一个或多个连接点。

4.切面（Aspect）：切面是通知和切点的结合。通知和切点共同定义了切面的全部内容----它是什么，在何时和何处完成其功能。

5.引入（Introduction）：引入允许我们向现有的类添加新方法和属性。从而可以在无需修改这些现有类的情况下，让它们具有新的行为和状态。

6.织入（Weaving）：织入是把切面应用到目标对象并创建新的代理对象的过程。切面在指定的连接点被织入到目标对象中，在目标对象的生命周期里有多个点可以织入：

编译期：切面在目标类编译时被织入。这种方法需要特别的编译器。AspectJ的织入编译器就是以这种方式织入切面的。

类加载期：切面在目标类加载到JVM时被织入。这种方式需要特殊的类加载器（ClassLoader），它可以在目标类被引入应用之前增强该目标类的字节码。

运行期：切面在应用允许的某个时刻被织入。一般情况下，在织入切面时，AOP容器会为目标对象动态地创建一个代理对象。Spring AOP就是以这种方式织入切面的。

#### 4.1.2.Spring对AOP的支持

Spring提供了4种类型的AOP支持：

1.基于代理的经典Spring AOP

2.纯POJO切面

3.@AspectJ注解驱动的切面

4.注入式AspectJ切面（适用于Spring各版本）

前三种都是Spring AOP实现的变体，Spring AOP构建在动态代理基础之上，因此，spring对AOP的支持局限于方法拦截。

spring经典的AOP看起来笨重和过于复杂，借助spring的aop命名空间，可以将纯POJO转换为切面。

**如果AOP需求超过了简单的方法调用（如构造器或属性拦截），那么你需要考虑使用AspectJ来实现切面。**

spring AOP框架的一些关键知识：

spring通知是java编写的。

spring在运行时通知对象。

spring只支持方法级别的连接点。

### 4.2.通过切点来选择连接点

在spring AOP中，要使用AspectJ的切点表达式语言来定义切点。spring仅支持AspectJ切点指示器的一个子集。

主要是使用exexution()切点指示器，用于匹配是连接点的执行方法。

#### 4.2.1.编写切点

```java
//exexution指在方法执行时调用  //*指返回类型   //包.类，为方法所属的类     //方法名       //（...）指使用任意参数
 execution                 ( *            concert.Performance.    perform        (....) )
         //其中execution（）大括号中指定方法。

 限定匹配某个包
 execution(*concert.Performance.perform(....) )  
              && within(concert. *)        //&&与and等价，within（）说明限制包（concert）。

```

#### 4.2.2.在切点中选择bean

spring引入了一个新的bean（）指示器，可以在切点表达式中使用bean的ID来标识bean。使用bean ID或者bean名称作为参数

```java
execution( *  concert.Performance.perform(....) )  
               and   bean('wordstock')        //表明切点只匹配特定的bean(wordstock) 
    
```

### 4.3.使用注解创建切面

#### 4.3.1.定义切面

类使用@Aspect注解进行标注，说明类不仅是一个POJO，还是一个切面。类中方法都都使用注解来定义切面行为。

```java
@Aspect              //说明类是一个切面
 public  class  Audience{                

  @Before("execution(** concert.Performance.perform(..))")       //在目标方法之前就开始会调用silence方法。
  public   void  silence(){
       ---(--);  }
  @Before("execution(** concert.Performance.perform(..))")       //在目标方法之前就开始会调用silence方法。
  public   void take(){
        ---(--);  }
        }
```

spring使用AspectJ注解来声明通知方法：

@After：通知方法会在目标方法返回或抛出异常后调用。

@AfterReturning：通知方法会在目标方法返回后调用。

@AfterThrowing：通知方法会在目标方法抛出异常后调用。

@Around：通知方法会将目标方法封装起来。

@Before：通知方法会在目标方法调用之前执行。

因为切点表达式经常重复，所以使用@Pointcut注解，能够在一个@AspectJ切面内定义可重用的切点。

```java
@Aspect
public  class  Audience{                  
@Pointcut（"execution(** concert.Performance.perform(..))"）
 public   void   performance() { }        //performance为切点表达式的方法         

   @Before （"performance()")       //括号中为定义切点表达式的performance方法
   public   void  silence(){
         ---(--);      }
   @Before("performance()")       //括号中为定义切点表达式的performance方法
   public   void take(){
        ---(--);      }
         }
```

单一的使用AspectJ注解，它不会被视为切面，这些注解不会解析，也不会创建将其转换为切面的代理。

如果使用javaConfig的话，可以在配置类的类级别上通过使用@EnableAspectJAutoproxy注解启动自动代理功能。

```java
@EnableAspectJAutoproxy        //启动Aspect代理
@componentScan
 public   class  ConcertConfig{
    @bean
    public   Audience   audience（）{       //声明Audience  bean
       return  new   Audience（）；
                                  }
          }
```

还可以使用XML来实现。需要使用spring aop命名空间的<aop:aspectj-autoproxy>元素

```xml
<beans----->
   <aop:aspectj-autoproxy/>                //启动AspectJ自动代理
   <bean  class="concert.Audience">     //声明Audience  bean
</beans>
```

不管是使用javaConfig还是XML，AspectJ自动代理都会使用@Aspect注解的bean创建一个代理，这个代理会围绕着所有该切面的切点所匹配的bean。我们需要记住的是，切面依然是基于代理的。本质上是spring基于代理的切面。

#### 4.3.2.创建环绕通知

@Around注解会作为切点的环绕通知，它需要接受proceedingJoinPoint作为参数。这个对象是必须的，因为要在通知中通过它来调用它来调用被通知的方法。

```java
@Aspect
public  class  Audience{                  
@Pointcut（"execution(** concert.Performance.perform(..))"）
 public   void   performance() { }        //performance为切点表达式的方法         
 
    @Around（"performance()"）
    public  void watchPerformance(ProceedingJoinPoint jp){
        try{
            ----;        //调用方法前的输出    
            jp.proceed();      //执行被通知的方法
            ---;          //调用方法后的输出
        }
        catch(throwable e){
            ----;         //调用方法异常时的输出
        }
```

#### 4.3.3.处理通知中的参数

之前的方法中都没有参数，当方法中有参数的时候需要修改切点。

```java
@Aspect
public  class  TrackCounter{ 
    
    private Map<Interger,Integer> trackCounts = new HashMap<Interger,Integer>();
                                                           //指定参数
@Pointcut（"execution(* sound.compact.playTrack(int))" +   "&& args(trackNumber)"）
 public   void   trcakPlayed(int trackNumber) { }        //performance为切点表达式的方法         

   @Before （"trcakPlayed(trackNumber)")       //括号中为定义切点表达式的trcakPlayed方法
   public   void  countTrack(int trackNumber){
         ---(--);      }
  
```

#### 4.3.4.通过注解引入新功能

可以通过@DeclareParents注解来引入。由三部分组成：

 value属性指定了哪种类型的bean要引入接口；
 defaultImpl属性指定了为引入功能提供实现的类；
 @DeclareParents注解所标记的静态属性指明了要引入接口。

```java
@Aspect
public   class  EncorableInteroducer{                     
 @DeclareParents(value="concenrt.Performance+",             //所有实现per-的类型，+代表子类型。
                 defaultImpl=DefaultEncoreable.class )    //指定DefaultEncoreable提供实现
    public    static  Encoreable    encoreable;            //引入Encoreable接口

```

### 4 .4.在XML中声明切面

如果你需要声明切面，但是又不能为通知类添加注解的时候，那么就必须转向XML配置。

<aop:advistor>：定义AOP通知器。

aop:---：---与java类似。

#### 4.4.1.声明前置和后置通知

把bean声明为一个切面时，我们总是从<aop:config>元素中开始配置的。

```xml
<aop:config>
   <aop:aspect  ref="audiencr">            //声明切面，引用audience bean
       <aop:Before
         pointcut="execution(** concert.Performance.perform(..))"      //切点
                   method="--"   />             //输出的值
    </aop:aspect>
<aop:config>
```

当切点重复时，也可以设置为一个切点声明。

```xml
<aop:config>
   <aop:aspect  ref="audiencr">            //声明切面，引用audience bean
       <aop:pointcut                               //定义切点
           id="performance"
            expression="execution(** concert.Performance.perform(..))" />
       
       
       <aop:Before
         pointcut-ref="performance"      //引用切点
                   method="--"   />             //输出的值
    </aop:aspect>
<aop:config>
```

#### 4.4.2.声明环绕通知

```java
@Aspect
public  class  Audience{                  
    public  void watchPerformance(ProceedingJoinPoint jp){
        try{
            ----;        //调用方法前的输出    
            jp.proceed();      //执行被通知的方法
            ---;          //调用方法后的输出
        }
        catch(throwable e){
            ----;         //调用方法异常时的输出
        }
```

使用<sop:around>元素来声明环绕通知。

```xml
<aop:config>
   <aop:aspect  ref="audiencr">            //声明切面，引用audience bean
       <aop:pointcut                               //定义切点
           id="performance"
            expression="execution(** concert.Performance.perform(..))" />      
       
       <aop:around           //声明环绕通知
         pointcut-ref="performance"     
                   method=" watchPerformance"   />             
    </aop:aspect>
<aop:config>
```

#### 4.4.3.为通知传递参数

与上面类似。

