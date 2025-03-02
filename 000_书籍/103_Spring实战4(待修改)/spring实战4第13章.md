---
title: spring实战4第13章
date: 2020-07-09 14:15:20
tags: spring
---

## 第十三章，缓存数据

此章讲解启动对缓存的支持，为方法添加注解以支持缓存，使用XML声明缓存。

<!--more-->

缓存可以存储经常会用到的信息，这样每次需要的时候，这些信心都是立即可用的。在本章中，我们将会了解到Spring的缓存抽象。尽管Spring 自身并没有实现缓存解决方法。但是它对缓存功能提供了声明式的支持。能够与多种流行的缓存实现进行集成。

### 13.0.各种注解

#### 1.@EnableCaching

```java
@Configuration
@EnableCaching          //启动缓存
    public  class  CachingConfig{
```



### 13.1.启动对缓存的支持

Spring对缓存的支持有两种方式。

1：注解驱动的缓存
2：XML声明的缓存

使用Spring 的缓存抽象时，最为通用的方式就是在方法上添加@Cacheable和@CacheEvict注解。在往bean上添加缓存注解之前，必须要启动Spring对缓存驱动缓存的支持，如果，我们使用java配置的话，那么可以在其中的一个配置类上添加@EnableCaching，这样的话就能启动注解缓驱动的缓存。

```java
@Configuration
@EnableCaching          //启动缓存
    public  class  CachingConfig{
       @Bean
       public  CacheManager  cacheManager(){        //声明缓存管理器
            return  new  ConcurrentMapCacheManager(); } 
             }
```

如果以XML的方式配置应用的话，那么可以使用Spring cache命名空间中的<cache:annotation--driven>元素来启动注解驱动的缓存。

```xml
<beans---->
  <cache:annotation-driven />
     <bean id="CacheManager"     //启动缓存
           class="org.springframework.cache.concurrent.ConcurrentMapCacheManager" />//声明缓存管理器
</beans>
```

@EnableCaching和<cache:annotation--driven>的工作方式是相同的。它们都会创建一个切面并触发Spring缓存注解的切点。根据所使用的注解以及缓存的状态，这个切面会从缓存中获取数据，将数据添加到缓存之中或者从缓存中移除某个值。



缓存管理器是Spring缓存抽象的核心，它能够与多个流行的缓存实现进行集成。

#### 13.1.1.配置缓存管理器

Spring 3.1内置了五个缓存管理器实现。

1：SimpleCacheManager
2：NoOpCacheManager
3：ConcurrentMapCachManager
4：CompositeCacheManager
5：EhCacheCacheManager

Spring3.2引入了另外一个缓存管理器，这个管理器可以用用在基于JCahe的缓存提供商之中。除了核心的Spring框架，Spring Data又提供了两个缓存管理器。

①：RedisCacheManager(来自于Spring Data Redis项目)
②：GemfireCacheManager（来自于Spring Data GemFire项目）

我们必须选择一个缓存管理器，然后要在Spring应用上下文中，以bean的方式对其进行配置。我们已经看到了如何配置ConcurrentMapCachManager，并且知道它可能并不是实际应用的最佳选择。

##### 使用Ehcache缓存

Ehcache缓存是最为流行的缓存供应商之一。鉴于它的广泛，Spring提供集成Ehcache的缓存管理器是很有意义的。这个缓存管理器就是EhCacheCacheManager。

```java
@Configuration
@EnableCaching
public class  CachingConfig{
    @Bean
    public  EhCacheManager  cacheManager(CacheManager  cm){ //配置EhCacheManager
          return  new  EhCacheCacheManager(cm);      }
     @Bean
    public  EhCacheManagerFactoryBean  ehcache(){     //EhCacheManagerFactory Bean
       EhCacheManagerFactoryBean  ehCacheFactoryBean = new  EhCacheManagerFactoryBean();
          ehCacheFactoryBean.setConfigLocation(
                 new ClassPathResource("com/habuma/spittr/cache/ehcache.xml")); //指明XML配置文件位置
             return  ehCacheFactoryBean;    }
```

cacheManager()方法创建了一个EhCacheCacheManager的实例。这是通过传入EhCacheCacheManager实例实现的。这是因为Spring和EhCache都定义了Cachemanager类型。需要明确的是，EhCache的CacheManage要注入到Spring的EnCacheCachManager(Spring的CacheManager的实现)之中。

我们需要属于EnCache的cacheManager来进行注入，所有必须也要声明一个CacheManager bean。为了简化，Spring提供了EhCacheManagerFactoryBean来生成EhCache的CacheManager。方法ehcache()会创建并返回一个EnCacheManagerFactoryBean实例。因为它是一个工厂bean，(也就是说，它实现了Spring的FactoryBbean接口)，所以注册在Spring上下文的并不是EhCacheManagerFactoryBean的实例，而是CacheManager的一个实例，因此适合注入到EhCacheCacheManager之中。

除了在Spring中配置的bean，还需要有针对EhChe的配置。EhCache为XML定义了自己的配置模式，我们需要在一个XML文件中配置缓存，该文件需要符合EhCache所定义的模式。在创建EhCacheManagerFactoryBean的过程中，需要告诉它EhCache配置文件在什么地方。在这里通过调用setConfiguration()方法，传入ClassPathResoure，用来指明EhCache XML配置文件相对于根路径(classpath)的位置

至于ehcache.xml文件的内容,不同应用会有差别。例如，EhCache配置声明为一个名为spittleCache的缓存，它最大的堆存储为50MB，存活的时间为100秒。

```java
<ehcache>
  <cache name="spittleCache"
    maxBytesLocalHeap="50m"
    timeToLiveSeconds="100">
    </cache>
</ehcache>
```

显然，这是一个基础的EhCache的配置。如果需要更丰富的。参考官方文档。

##### 使用Redis缓存

如果缓存的条目不过是一个键值对(key-value pair)，其中key描述了产生value的操作和参数。因此Redis作为key-value存储，非常适合于存储缓存。

Redis可以用来为Spring缓存抽象存储条目。Spring Data Redis提供了RedisCacheManager,这是CacheManager的一个实现。RedisCacheManager会与一个Redis服务器协作，并通过RedisTemplate将缓存条目存储到Redis中。

为了使用RedisCacheManager，我们需要RedisTemplate bean以及RedisConnectionFactory实现类(如JedisConnectionFactory)的一个bean。在十二章中，我们已经看到了这些bean该何如配置。在RedisTemplate就绪之后，配置RedisCacheManager就是非常容易的事情了。

```java
@Configuration
@EnableCaching
public  class  CachingConfig{
   @Bean                                                         //Redis缓存管理器bean
   public class CacheManager  cacheManager(RedisTemplate redisTemplate){
        return  new  RedisCacheManager(redisTemplate);                                      }
   @Bean                                                     //Redis连接工厂bean
   public JedisConnectionFactory redisConnectionFactory(){
        JedisConnectionFactory  jedisConnectionFactory = new JedisConnectionFactory ();
        jedisConnectionFactory.afterPropertiesSet(); 
            return  jedisConnectionFactory;                                    }
   @Bean                                                      //RedisTemplate  bean
   public  RedisTemplate<String, String> redisTemplate(RedisConnectionFactory redisCF){
         RedisTemplate<String, String> redisTemplate = new RedisTemplate<String, String>();
         redisTemplate.setConnectionFactory(redisCF);
         redisTemplate.afterPropertiesSet();
              return  redisTemplate;   }
      }   
```

我们可以看到，我么构建了一个RedisCacheManager，这是通过传递一个RedisTemplate实例作为其构造器的参数实现。

##### 使用多个缓存管理器

如果很难确定使用哪个的话，那么可以尝试使用Spring的CompositeCacheManager。CompositeCacheManager要通过一个或多个

的缓存管理器来进行配置，它会迭代这些缓存管理器，以查找之前所缓存的值。如下展示了如何创建CompositeCacheManager bean，它会迭代JCacheCacheManager，EhCacheCacheManager和RedisCacheManager。

```java
@Bean                                      
public  CacheManager  cacheManager( net.sf.ehcache.CacheManager cm,javax.cache.CacheManager jcm){
   CompositeCacheManager cacheManager = new CompositeCacheManager();//创建CompositeCacheManager
   List<CacheManager> managers = new ArrayList<CacheManager>();       
   managers.add(new JCacheCacheManager(jcm));
   managers.add(new EhCacheCacheManager(cm));
   managers.add(new RedisCacheManager(redisTemplate()));
   cacheManager.setCacheManagers(managers);  //添加单个缓存管理器
       return cacheManager;                                   
```

当缓存条目时，CompositeCacheManager首先会从JCacheCacheManager开始检查JCache实现，然后通过EhCacheManager检查Ehcache，最后会使用RedisCacheManager来检查Redis，完成缓存条目的查找。

在配置完缓存管理器并启用缓存后，就可以在bean方法上应用缓存规则了。让我们看一下如何使用Spring的缓存注解来定义缓存边界。

### 13.2.为方法添加注解以支持缓存

Spring的缓存抽象在很大程度上是围绕切面创建的。在Spring中启用缓存时，会创建一个切面，它触发一个或更多的Spring的缓存注解。

Spring所提供的的缓存注解都能运用在方法或类上。当将其放在单个方法上时，注解所描述的缓存行为只会运用到这个方法上。如果注解放在类级别的话，那就缓存行为就会应用到这个类的所有方法上。

Spring提供了四个注解来声明缓存规则。

1：@Cacheable注解，表明Spring在调用方法之前，首先应该在缓存中查找方法的返回值。如果这个值就能够找到，就会返回缓存的值，否则的话，这个方法就会被调用，返回值就会放到缓存之中。
2：@CachePut注解，表明Spring应该将方法的返回值放到缓存中。在方法的调用前并不会检查缓存，方法始终都会被调用。
3：@CacheEvict注解，表明Spring应该在缓存中清除一个或多个条目。
4：@Caching注解，这是一个分组的注解，能够同时应用多个其他的缓存注解。

#### 13.2.1.填充缓存

我们可以看到，@Cacheable和@CachePut注解都可以填充缓存，但是它们的工作方式略有差异。

@Cacheable首先在缓存中查找条目，如果找到了匹配的条目，那么久不会对方法进行调研了。如果没有找到匹配的条目，方法会被调用并且放在缓存之中。而@CachePut并不会在缓存中检查匹配的值，目标方法总是会被调用，并将返回值添加到缓存之中。

两者有一些共有的属性。

| 属性      | 类型     | 描述                                                         |
| --------- | -------- | ------------------------------------------------------------ |
| value     | String[] | 要使用的缓存名称                                             |
| condition | String   | spEL表达式，如果得到的值是false的话，不会将缓存应用到方法调用上。 |
| key       | String   | spEL表达式，用来计算自定义的缓存key。                        |
| unless    | String   | 如果得到的值是true的话，返回值不会放到缓存之中。             |

在最简单的情况下，在@Cacheable和@CachePut的这些属性中，只需要使用value属性指定一个或多个缓存即可。例如考虑SpittleRepository的findOne()方法。在初始保存之后，Spittle就不会发生变化了。如果有的Spittle比较热门并且会被频繁请求，反复地在数据库中获取是对时间和资源的浪费。通过在findOne()方法上添加@Cacheable注解，如下面所示，将Spitter保存在缓存中，从而避免对数据库的不必要访问。

```java
@Cacheable("spittleCache")     //缓存这个方法的结构
public Spittle findOne(long id){
    try{
        return jdbcTemplate.queryForObject(
            SELECT_SPITTLE_BY_ID,
            new SpittleRowMapper(),
            id);
    }catch(emptyResultDataAccessException e){
        return null;
  }
}
```

当findOne()被调用时，缓存切面会拦截调用并在缓存中查找之前以名spittleCache存储的返回值。缓存的key是传递到findOne()方法中的id参数。如果按照这个key能找到值的话，就会返回找到的值，方法不会再被调用。如果没有找到值的话，那么就会调用这个方法，并将返回值放到缓存之中，为下一次调用findOne()方法做好准备。

@Cacheable注解放在jdbcSpittleRepository的findOne()方法实现中。SpittleRepository的其他实现并没有缓存功能，除非也为其添加上@Cacheable注解。因此可以将注解添加到SpittleRepository的方法声明上，而不是放在实现类中。

```java
@Cacheable("spittleCache")
Spittle findOne(long id)；
```

当为接口方法添加注解后，@Cacheable注解会被SpittleRepository的所有实现继承。这些实现类都会应用相同的缓存规则。

### 13.3.使用XML声明缓存

为什么要使用XML的方式，有两个原因。

1.你可能会觉得自己的源码中添加Spring的注解有点不太舒服。

2.你需要在没有源码的bean上应用缓存功能。

如下等同之前使用缓存注解的方式

```xml
<beans---->
   <aop:config>
       <aop:advisor advice-ref="cacheAdvice"
               pointcut="execution(* com.habuma.spitter.db.SpittleRepository.*(..))" />
    </aop:config>       //将缓存通知绑定到一个切点上
    <cache:advice id="cacheAdvice">
     <cache:caching>
         <cache:cacheable         //配置为支持缓存
          cache="spittleCache"  method="findRecent" />
         <cache:cacheable        //配置为支持缓存
               cache="spitttleCache"  method="findOne" />
         <cache:cacheable         //配置为支持缓存
               cache="spitttleCache"  method="finBySpitterId" />
         <cache:cache-put        //在save时填充缓存
               cache="spitttleCache"  method="save" key="#result.id" />
         <cache:cache-evict      //从缓存中移除
               cache="spitttleCache"  method="remove" />
        </cache:caching>
    </cache:advice>
        <bean id="cacheManager"  class="org.springframework.concurrent.ConcurrentMapCacheManager" />
</beans>
```

