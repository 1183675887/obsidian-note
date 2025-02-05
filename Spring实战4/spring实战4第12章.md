---
title: spring实战4第12章
date: 2020-07-08 23:00:25
tags: spring
---

## 第十二章，使用NoSQL数据库

此章讲解使用MongoDB持久化文档数据，使用Neo4j操作图数据。使用Redis操作key-value数据。

<!--more-->

Spring Data还提供了对多种NoSQL数据库的支持，包括MongoDB,Neo4j和Redis。它不仅支持自动化的Repository，还支持基于模板的数据访问和映射注解。在本章中，我们将会看到如何为非关系型的NoSQL数据库编写Repository。

### 12.0.各种注解

#### 1.@EnableMongoRepositories

@EnableMongoRepositories注解启动了Spring Data的自动化Repotiry生成功能。

```java
@Configuration
@EnableMongoRepositories(basePackages="orders.db")  //启动MongoDB的Repository功能。
public  class  MongoConfig{
```

#### 2.将java类型映射为MongoDB文档的注解。

1：@Document注解,标示到MongoDB文档上的领域对象。
2：@ID注解，标示某个域为ID域
3：@DbRef注解，标示某个域要引用其他的文档，这个文档有可能位于另一个数据库中
4：@Field注解，为文档域指定自定义的元数据
5：@Version注解，标示某个属性用作版本域

#### 3.@EnableNe04jRepositories

@EnableNe04jRepositories注解能够让Spring Data Neo4j自动生成Neo4j Repository实现。

```java
@Configuration
@EnableNeo4jRepositories(basePackages="orders.db")   //启动Repository自动生成功能
 public  class  Neo4jConfig extends Neo4jConfiguration{
```



### 12.1.使用MongoDB持久化文档数据

有一些数据的最佳表现形式是文档（doucument），也就是说，不要把这些数据分散到多个表，节点或实体中，将这些信息收集到一个非规范（也就是文档）的结构中会更有意义。尽管两个或两个以上的文档有可能会彼此产生关联，但是通常来讲，文档是独立的实体。能够按照这种方式优化并处理文档的数据库，我们称之为文档数据库。

##### MongoDB是最为流行的开源数据库之一。Spring Data MongoDB提供了三种方式在Spring应用中使用MongoDB。

1.通过注解实现对象-文档映射。

2.使用MongoTemplate实现基于模板的数据库访问

3.自动化的运行时Repository生成功能。

Spring Data MongoDB为基于MongoDB的数据访问提供了相同的功能。可以将java对象映射为文档的功能。除此之外，还为通用的文档操作任务提供了基于模板的数据访问方式。在使用这些特性前，我们首先要配置Spring Data MongoDB。

####  12.1.启动MongoDB

为了有效地使用Spring Data MongoDB，我们需要在Spring配置中添加几个必要的bean。首先，我们需要配置MongoClient，以便于访问MongoDB数据库。同时我们还需要一个MongoTemplate bean，实现基于模板的数据库访问。此外，强烈建议启动Spring Data MongoDB的自动化Repository。如下编写了简单的Spring Data MongoDB配置类。

```java
@Configuration
@EnableMongoRepositories(basePackages="orders.db")  //启动MongoDB的Repository功能。
public  class  MongoConfig{
     @Bean                                                                     
     public  MongoFactoryBean  mongo(){          //mongoClient bean
           MongoFactoryBean  mongo  =  new MongoFactoryBean(); 
           mongo.setHost("localhost");
               return  mongo;  }
     @Bean
     public MongoOperations  mongoTemplate(Mongo mongo){     //MongoTemplate bean
               return new  MongoTemplate(mongo, "OrdersDB");  }
       }
```

@EnableMongoRepositories注解启动了Spring Data的自动化Repotiry生成功能。第一个@Bean方法使用MonfactoryBean声明了一个mogo实例。这个bean将Spring Data MongoDB与数据库本身连接了起来。另外一一个@Bean方法声明了MongoTemplate bean，在它构造时，使用了其他@Bean方法所创建的Mogo实例的引用以及数据库的名称。

除了直接声明这些bean，我们还可以让配置类扩展AbstractMongoconfiguration并重载getDatabaseName()和mongo()方法。

```java
@Configuration
@EnableMongoRepositories("orders.db")  //启动MongoDB的Repository功能。
public  class  MongoConfig  extends  AbstractMongoConfiguration{
        @Override
        protected  String  getDatabaseName(){
             return  "OrdersDB"；}   //指定数据库名称
        @Override
        public  Mongo  mongo()  throws  Exception{     //创建Mongo客户端
             return   new   MongoClient();      }
            }
```

我们在这里重载了getDatabaseName()方法来提供数据库的名称。mongo()方法依然会创建一个MongoClient的实例。

上面都为Spring Data MongoDB提供了一个运行配置，也就是说，只要MongoDB服务器运行在本地即可。如果MongoDB服务器运行在其他的机器上，那么可以在创建MongoClient的时候进行指定。

```java
 public  Mongo  mongo()  throws  Exception{     //创建Mongo客户端
      return   new   MongoClient("mongodbserver");      }
            }
```

另外，MongoDB还可能监听的的端口并不是默认的27017。如果是这样的话，在创建MongoClient的时候，还需要指定端口。

```java
 public  Mongo  mongo()  throws  Exception{     //创建Mongo客户端
      return   new   MongoClient("mongodbserver", 37017);      }
            }
```

如果MongoDB服务器运行在生产配置上，我认为你可能还启动了认证功能。在这种情况下，为了访问数据库，我们还需要提供应用的凭证。访问需要认证的MongoDB服务器有些复杂。

```java
@Autowird
private   Environment   env;
@Override
public  Mongo  mongo()   throws  Exception{
     MongoCredential credential  = MongoCredential.createMongoCRCredential{  //创建MongoDB凭证
             env.getProperty("mongo.username"),
             "OrdersDB",
             env.getProperty("mongo.password").toCharArray());
       return  new  MongoClient{    //创建MongoClient
             new ServerAddress("localhost",  37017), 
           Arrays.asList(credential));
  }
```

为了访问需要认证的MongoDB服务器，MongoClient在实例化的时候必须要有一个MongoCredential的列表，为此创建了一个MongoCredential。为了将凭证信息的细节放在配置类外边，它们是通过注入的Environment对象解析得到的。

我们还可以使用XML来配置Spring Data MongoDB。

```xml
<beans---->
  <mongo:repositories base-package="orders.db" />  //启动Repository生成功能
  <mongo:mongo />    //声明Mongo Client
  <bean id ="mongoTemplate"      //创建MongoTemplate bean
        class="org.springframework.data.mongodb.core.MongoTemplate">
      <constructor-arg ref="mongo" />
      <constructor-arg value="OrdersDB" />
  </bean>
</beans>
```

现在Spring Data MongoDB以及配置完成了，我们很快就可以使用它来保存和查询文档了。但首先，需要使用Spring Data MongoDB的对象--文档映射注解为java领域对象建立到持久化文档的映射关系。

#### 12.1.2.为模型添加注解，实现MongoDB持久化。

当使用JPA的时候，我们需要将java实体类映射到关系型表和列上。JPA规范提供了一些支持对象--关系映射的注解。但是MongoDB并没有提供对象-文档映射的注解。Spring Data MongoDB填补了这一空白，提供了一些将java类型映射为MongoDB文档的注解。

1：@Document注解,标示到MongoDB文档上的领域对象。
2：@ID注解，标示某个域为ID域
3：@DbRef注解，标示某个域要引用其他的文档，这个文档有可能位于另一个数据库中
4：@Field注解，为文档域指定自定义的元数据
5：@Version注解，标示某个属性用作版本域

@Document和@Id注解类似于JPA的@Entity和@Id注解。我们将会经常使用者两个注解，对于要以文档形式保存到MongoDB数据库的每个java类型都会使用者两个注解。如下展现了如何为Order类添加注解，它会被持久化到MongoDB中。

```java
@Document      //这是一个文档
public class   Order{            
      @ID                             
      private String id;     //指定ID
      @Field("client")
      private  String  customer;     //覆盖默认的域名
      private  String  type;
      private  Collection<Item> items = new LinkedHashSet<Item>();
      public  String  getCustomer(){ 
          return getCustomer;}
    -----后面都是get与set方法。
```

我们可以看到，Order类添加了@Document注解。这样它就能够借助MongoTemplate或自动生成的Repository进行持久化。其id属性使用了@Id注解，用来指定它作为文档的ID。除此之外，customer属性使用了@Field注解，这样的话，当文档持久化的时候customer属性会将映射为名为client的域。

现在我们已经为java对象添加量MongoDB持久化的注解。接下来，看一下如何使用MongoTemplate来存储它们。

#### 12.1.3.使用MongoTemplate访问MongoDB

我们已经在配置类中配置了MongoTemplate bean。不管是显示声明还是扩展AbstractMongoconfiguration都能实现相同的效果。接下来，需要做的就是将其注入到使用它的地方。

```java
@Autowird  
MongoOperations  mongo;  //MongoOperations是MongoTemplate所实现的接口

```

在这里我们将将MongoTemplate注入到一个类型为MongoOperations的属性中。 MongoOperations是MongoTemplate所实现的接口。不使用具体实现是一个好的做法，尤其是注入的时候。

MongoOperations暴露了多个使用MongoDB文档数据库的方法。我们看一下常用的几个操作。

比如计算文档集合中有多少条文档。使用注入的MongoOperations,我们可以得到Order集合并调用count（）得到数量。

```java
 long orderCount = mongo.getCollection("order").count();
```

假设保存一个新的Order。可以调用save()方法。

```java
Order  order = new Order();
    ..... //set properties  and  add  line items
       mongo.save(order,"order");        //save()方法的第一个参数是新创建的order,第二个是要保存的文档存储的名称
```

调用findById()方法来根据ID查找订单。

```java
String  orderId =......;
         Order  order  = mongo.findById(order, Order.class);
```

移除某一个文档，使用remove（）方法。

```java
 mongo.remove(order)；
```

建议查看一下其javaDoc文档，以了解通过MongoOperations都能完成什么功能。

#### 12.1.4.编写MongoDB Repository

我们已经通过@EnableMongoRepositories注解启动了Spring Data MongoDB的Repository功能。接下来需要做的就是创建一个接口，Repository实现基于这个接口来生成。在这里我们需要扩展MongoRepository。如下的OrderRepository扩展了MongoRepository，为Order文档提供了基本的CRUD操作。

```java
public interface OrderRepository extends MongoRepository<Order, String>{
}
```

MongoRepository接口有两个参数，第一个是带有@Document注解的对象类型，也就是该Repository要处理的类型。第二个参数就是带有@Id注解的属性类型。尽管OrderRepository本身并没有定义任何方法，但是它会继承多个方法。

### 12.2.使用Neo4j操作图数据

文档型数据库会将数据存储到粗细度的文档中，而图数据库会将数据存储到多个细粒度的节点中，这些节点之间通过关系建立关联。图数据库中的一个节点通常会对应数据库中的一个概念，它会具备描述节点状态的属性。链接两个节点的关联关系可能也会带有属性。

因为数据的结构是图，所有可以遍历关联关系以查找数据中你所关心的内容，这在很多其他数据库中是很难甚至无法实现的。

Spring Data Neo4j提供了很多与Spring Data JPA和Spring Data MongoDB相同的功能，当然针对的是Neo4j图数据库。它提供了将java对象映射到节点和关联关系的注解，面向模板的Neo4j访问方式以及Repository实现的自动化生成功能。

#### 12.2.1.配置Spring Data Neo4j

配置Spring Data Neo4j的关键在于声明GraphDatabaseService bean和启动Neo4j Repository自动生成功能。

```java
@Configuration
@EnableNeo4jRepositories(basePackages="orders.db")   //启动Repository自动生成功能
 public  class  Neo4jConfig extends Neo4jConfiguration{
       public  Neo4jConfig(){                 
          setBasePackage("orders");   }//设置模型的基础包  
       @Bean(destroyMethod="shutdowm")
       public  GraphDatabaseSerivce graphDatabaseSerice（）{
           return  new  GraphDatabase()
                                     .newEmbeddedDatabase("/tmp/graphdb"); }  //配置嵌入式数据库
                    }
```

@EnableNe04jRepositories注解能够让Spring Data Neo4j自动生成Neo4j Repository实现。它的basePackage属性设置为ordrs.db包，这样它就会扫描这个包来查找扩展Repository标记接口的其他接口。

Neo4jConfig扩展自Neo4jConfiguration.后者提供了多个遍历的方法来配置Spring Data Neo4j。setBasePackage()方法就是其中一个。

它会在Neo4jConfig的构造器中调用，用来告诉Spring Data Neo4j要在orders包中查找模型。

GraphDatabaseSerivce bean中的graphDatabaseSerice方法使用GraphDatabaseFactory来创建嵌入式的Neo4j数据库。在Neo4j中，"嵌入式"指的是数据库引擎与应用运行在同一个JVM中。作为应用的一部分，而不是独立的服务器。数据依然会持久化到文件系统中。

你可能会希望配置GraphDatabaseSerivce连接远程的Neo4j服务器。如果spring-data-neo4j-rest库在应用的类路径下，那么我们就可以配置SpringRestGraphDatabase,它会通过RESTful API来访问远程的Neo4j数据库。

```java
@Bean(destroyMethod="shutdowm")
public  GraphDatabaseSerivce graphDatabaseSerice（）{
     return  new  SpringRestGraphDatabase(
            "http://graphdserver:7474/db/data/");
```

如果在生产环境的配置中，需要应用的凭证。

```java
@Bean(destroyMethod="shutdowm")
public  GraphDatabaseSerivce graphDatabaseSerice（Enviroment  env）{  //凭证通过注入的Enviroment
    return  new  SpringRestGraphDatabase(         //获取到的
                       "http://graphdserver:7474/db/data/",
                         env.getProperty("db.username"),env.getProperty("db.password"));
                         }
```

在这里，凭证是通过Enviroment获取到的，避免了在配置类中的硬编码。

还可通过XML配置Spring Data Neo4j。

```java
<beans--->
    <neo4j:config
        storeDirectory="/tmp/graphdb"
        base-package="orders"  />
    <neo4j:repositories base-package="order.db" />  //启动Repository生成功能
</beans>
```

storeDirectory属性指定了数据要持久化到那个文件系统路径中。base-package属性设置了模型类定义在哪个包。

如果配置远程的Neo4j服务器。我们所需要的就是声明SpringRestGraphDatabase bean。并设置graphDatabaseService属性。

```xml
<neo4j:config base-package="orders"
              graphDatabaseService="graphDatabaseService" />
<bean id="graphDatabaseService" class="org.springframework.data.neo4j.rest.SpringRestGraphDatabase">
    <constructor-arg value="http://graphdbserver:7474/db/data/" />
    <constructor-arg value="db.username">
    <constructor-arg value="db.password">    
```

 不管是通过java还是XML来配置Spring Data Neo4j，我们都需要确保模型类位于基础包所指定的包中。它们都需要使用注解将其标记为节点实体或关联实体。这就是我们接下来的任务。

#### 12.2.2.使用注解标注图实体

Neo4j定义了两种类型的实体：节点和关联关系。一般来讲，节点反映了应用中的事物，而关联关系定义了这些事物是如何联系在一起的。Spring Data Neo4j提供了多个注解。以下为常用的。

1：@NodeEntiry注解，将java类型声明为节点实体。
2：@RelationshipEntity注解，将java类型声明为关联关系实体。
3：@StarNode注解，将某个属性声明为关联关系实体的开始节点。
4：@EndNode注解，将某个属性声明为关联关系实体的结束节点。
5：@GraphId注解，将某个属性设置为实体的ID域（这个域的类型必须是long）。
6：@RelatedTo注解，通过某个属性，声明当前@NodeEntity与另一个@NodeEntity之间的关联关系。

为了了解如何使用其中注解，我们会将其应用到订单、条目样例中。在该样例中，数据模型的一种方式就是将订单设置为一个节点，它会与一个或多个条目关联。

```java
@NodeEntity
public class Order{     //Order类是节点
    @GraphId
    private Long id;      //GraphId ID
    private String customer;
    private String type;
    @RelatedTo(type="HAS_ITEMS")  //与条目之间的关联关系
    private Ser<Item> items = new LinkedHashSet<Item>();
    ----
}
```

Neo4j上的所有实体必须要有一个图ID。所有id属性用来@GraphId注解。items属性上使用了@RelatedTo注解，这表明Order与一个Item的Set关系存在关联关系。type属性实际上就是为关联建立了一个文本标记。

就item本身来说，下面展现了如何为其添加注解实现图的持久化。

```java
@NodeEntity
public class Item{
    @GraphId
    private Long id;
    private String product;
    private double price;
    private int quantity;
------
}

```

#### 12.2.3.使用Neo4jTemplate

Spring Data Neo4j提供了Neo4jTemplate来操作Neo4j图数据库中的节点和关联关系。如果按照前面的方式配置了Spring Data Neo4j，在Spring应用上下文中就以及具备了一个Neo4jTemplate bean。接下来需要做的就是将其注入到任意想使用它的地方。例如，我们可以直接将其自动装配到某个bean的属性上。

```java
@Autowired
private  Neo4jOperations neo4j；
```

Neo4jTemplate定义了很多方法，包括保存节点，删除节点以及创建节点间的关联关系。我们提供常见的几种方式。

将某个对象保存为节点，假设对象已经使用@NodeEntity注解，那么我也使用save()方法。

```java
Order order = ....;
     Order savedOrder = neo4j.save(order);

```

知道对象的图ID，那么可以通过findOne()方法来获取它。

```java
Order  order = neo4j.findOne(42, Order.class);
```

如果你想获取给点类型的所以对象，那么可以使用findAll()方法。

```java
EndResult<Order>  allOrders = neo4j.findAll(Order.class);
```

如果想知道Neo4j数据库中指定类型的对象数量，那么就可以调用count()方法。

```java
long  orderCount = count(Order.class);
```

delete（）方法可以删除对象。

```java
 neo4j.delete(order);
```

为两个节点创建关系，用createRelationshipBetween（）方法。

```java
Order order = ....;                //在order节点和prod节点之间创建LineItem关联关系
Product  prod = ....;
LineItem lineItem = neo4j.createRelationshipBetween(
     order,prod,LineItem.class,"HAS_LINE_ITEM_FOR"，false);
lineItem.setQuantity(5);
neo4j.save(lineItem);
```

#### 12.2.4.创建自动化的Neo4j Repository

我们已经将@EnableNeo4jRepository添加到了配置中，所有所需要的就是编写接口。如下的OrderRepository就是很好的起点。

```java
public interface OrderRepisitory extends GraphRepository<Order>{}
```

与上面一样，也有很多方法，下面介绍常见的几种方法。

保存Order实体。

```
Order savedOrder = orderRepository.save(order);
```

使用findOne()方法查询某一个实体。

```
Order order = orderRepository.findOne(4L);
```

还可以查询所有的Order。

```
EndResult<Order> allOrders = orderRepository.findAll();
```

还可以删除某一个实体。

```
delect(order)；
```

### 12.3使用Redis操作Key-value数据

Redis是一种特殊类型的数据库，它被称之为key-value存储。保存的是键值对。实际上，key-value存储与哈希Map有很大的相似性。可以不夸张的说，它们就是持久化的哈希Map。

对于哈希Map或者key-value存储来说，其实并没有太多的操作。我们可以将某个value存储到特定的key上，并且能够根据特定key，获取value。因此，Spring Data的自动Repository生成功能并没有应用到Redis上。

Spring Data Redis包含了多个模板实现，用来完成Redis数据库的数据存取功能。为了创建模板，首先需要有一个Redis链接工厂。

#### 12.3.1.连接到Redis

Redis连接工厂会生成到Redis数据库服务器的连接。Spring Data Redis为四种Redis客户端实现提供了连接工厂。
具体选择哪个看需求。

1：JedisConnectionFactory
2：JredisConnectionFactory
3：LettuceConnectionFactory
4：SrpConnectionFactory

具体怎么选择看客户端和工厂的适配性。在做成决策后，我们就可以将连接工厂配置为Spring中的bean。例如，如下展示了如何配置JedisConnectionFactory bean。

```java
@Bean
public  RedisConnectionFactory redisCF(){
      return  new  JedisConnectionFactory();
                                                                               }
```

通过默认构造器创建的连接工厂会向localhost上的6379端口创建连接，并且没有密码。如果你的Redis服务器运行在其他的主机或端口上，在创建连接工厂的时候，可以设置这些属性。

```java
@Bean
 public  RedisConnectionFactory redisCF(){
    JedisConnectionFactory cf = new JedisConnectionFactory();
         cf.setHostName("redis-server");
               return cf;
                                    
```

如果需要客户端认证的话，可以通过调用setPassword()方法来设置密码。

```java
@Bean
public  RedisConnectionFactory redisCF(){
    JedisConnectionFactory cf = new JedisConnectionFactory();
         cf.setHostName("redis-server");
         cf.setPort(7379);
         cf.setPassword("foobared");
            return cf;
```

所有的Redis连接工厂都有setHostName(),setPost(),cf.setPassword()方法。所以可以替换连接工厂。   

#### 12.3.2.使用RedisTemplate

Redis连接工厂会生成Redis key-value存储的连接(以RedisConnection的形式)。借助RedisConnection，可以存储和读取数据。例如，可以获取连接并使用它来保存一个问候信息。

```java
RedisConnectionFactory cf  ...;
RedisConnection conn = cf.getConnection();
conn.set("greeting".getBytes(),"Hello World".getBytes());
```

与之类似，我们还可以使用RedisConnection来获取之前存储的问候信息。

```java
byte[] greetingBytes = conn.get("greeting".getBytes());
String greeting = new String(greetingBytes);
```

Spring Data Redis提供了两个模板,以模板的形式提供了较高等级的数据访问方案。

1：RedisTemplate
2：StringRedisTemplate

RedisTemplate可以极大的简化Redis数据访问，能够让我们持久化各种类型的key和value，并不局限于字节数组。在认识到key和value通常是String类型之后，SpringRedisTemplate扩展了RedisTemplate。只关注String类型。

假设我们已经有了RedisConnectionFactory，那么可以按照如下的方式构建RedisTemplate。

```java
RedisConnectionFactory cf = ...;
RedisTemplate<String,product> redis = new RedisTemplate<String,product>（）;
     reids.setConnectionFactory(cf);
```

RedisTemplate使用了两个类型进行参数化。第一个是key类型，第二个是value的类型。在这个中，将会保存
Product对象作为value，并赋予一个String类型的key。

如果你所使用的value和key都是String类型，那么可以考虑使用StringRedisTemplate来代替RedisTemplate。

```java
RedisConnectionFactory cf = ...;
StringRedisTemplate redis = new StringRedisTemplate(cf);
```

注意，StringRedisTemplat有一个接受RedisConnectionFactory的构造齐全，因此没有必要再构建后再调用setConnectionFactory。

如果经常经常使用RedisTemplate和StringRedisTemplate的话，就可以配置为bean，注入到需要的地方。如下就是一个声明RedisTemplate的简单@Bean方法。

```java
@Bean
public RedisTemplate<String,product>  redisTemplate(RedisConnectionFactory cf){
       RedisTemplate<String,Product>  redis = new RedisTemplate<String,product>（）;
              reids.setConnectionFactory(cf);
                       return redis;
```

```java
@Bean
public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory cf){
            return new  StringRedisTemplate(cf）;         
         }
```

有了RedisTemplate之后，我们就可以开始保存，获取以及删除key-value条目了。与之前一样，也有很多方法。

##### 使用简单的值

假设我们想通过RedisTemplate<String,Product>保存Product，其中key是sku属性的值，使用opsForValue()方法

```java
redis.opsForValue().set(product.getSku(), product);
```

你希望获取sku属性为123456的产品，那么可以使用opsForValue()方法。

```java
Product  product = redis.opsForValue().get("123456");
```

##### 使用List类型的值

使用List类型的value与之类似，只需要使用opsForValue()方法。我们可以条目尾部添加一个值。

```java
redis.opsForValue().rightPush("cart",product);
通过这个方式，我们向列表的尾部添加了一个Product，所使用的这个列表在存储时key为cart。
```

rightPush()会在列表的尾部添加一个元素，而leftPush()则会在列表的头部添加一个值。

```java
redis.opsForValue().leftPush("cart",product);
```

我们有很多方式从列表中获取元素，可以通过leftPop()或rightPop()方法从列表中弹出一个元素。

```java
Product first = redis.opsForList().leftPop("cart");
Product last = redis.opsForList().rightPop("cart");
这两方法有一个副作用就是从列表中移除所弹出的元素。
```

如果只是想获得值的话（可能在列表中间获取），那么可以使用range()方法。

```java
List<Product> produce = redis.opsForList().range("cart",2,12);
range()方法不会冲列表中移除任何元素，但是它会根据指定的key和索引范围，获取范围内的一个或多个值。
```

##### 在Set上执行操作

除了操作列表以外，我们还可以使用opsForSet()操作Set。最为常用的操作就是想Set中添加一个元素。

```java
redis.opsForSet().add("cart",product);
```

在我们有多个Set并填充值之后，就可以对这些Set进行一些有意思的操作，如获取其差异，求交集和求并集。

```java
List<Product> diff = redis.opsForSet().differerence("cart1","cart2");
List<Product> union = redis.opsForSet().union("cart1","cart2");
List<Product> isect = redis.opsForSet().isect("cart1","cart2");
```

当然，我们还可以移除它的元素。

```java
redis.opsForSet().remove(product);
```

我们还可以随机获取Set中的一个元素。

```java
product random = redis.opsForSet().randomMember("cart");
```

##### 绑定到某个key上

假设将Product对象保存到一个list中，并且key为cart。在这种场景下，假设我们想从list的右侧弹出一个元素，然后在list的尾部新增三个元素。我们可以使用boundListOps()方法所返回的BoundListOperations。

```java
BoundListOperations<String, product> cart = redis.boundListOps("cart");
product popped = cart.rightPop();
cart.rightpush(product1);
cart.rightpush(product2);
cart.rightpush(product3);
```

注意，我们只在一个地方使用了条目的key，也就是调用boundListOps()的时候，对返回的BoundListOperations执行的所有操作都会应用到这个key上。

