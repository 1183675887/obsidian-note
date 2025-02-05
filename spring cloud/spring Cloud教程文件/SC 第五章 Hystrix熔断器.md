# 7 Hystrix 断路器/熔断器

## 7.1 基本介绍

Hystrix叫做断路器/熔断器。微服务系统中，整个系统出错的概率非常高，因为在微服务系统中，涉及到的模块太多了，每一个模块出错，都有可能导致整个服务出，当所有模块都稳定运行时，整个服务才算是稳定运行。

我们希望当整个系统中，某一个模块无法正常工作时，能够通过我们提前配置的一些东西，来使得整个系统正常运行，即单个模块出问题，不影响整个系统。

## 7.2 基本用法

首先创建一个新的 SpringBoot 模块，项目名为hystrix。 然后添加spring Web依赖，Eureka Discovery Client依赖，其中Hystrix依赖可能没有，因此需要手动在模块的pom.xml中注入。

[![f0VyDJ.png](https://z3.ax1x.com/2021/08/12/f0VyDJ.png)](https://imgtu.com/i/f0VyDJ)

```xml
<!--   引入hystrix，注意需要版本号，不然maven可能导入不进来   -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
            <version>2.1.1.RELEASE</version>
        </dependency>
```

项目创建成功后，在application.properties中添加如下配置，将 Hystrix 注册到 Eureka 上：

```properties
#当前服务的名称
spring.application.name=hystrix
#当前服务的端口
server.port=3000
#当前服务地址
eureka.client.service-url.defaultZone=http://localhost:1111/eureka
```

然后，在项目启动类上HystrixApplication添加@EnableCircuitBreaker注解，开启断路器，同时提供一个 RestTemplate 实例：

```java
@SpringBootApplication
@EnableCircuitBreaker  //开启断路器,两个注解等价于@SpringCloudApplication
public class HystrixApplication {

    public static void main(String[] args) {
        SpringApplication.run(HystrixApplication.class, args);
    }

    //提供一个 RestTemplate 实例
    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
       return new RestTemplate();
    }

}
```

HystrixApplication启动类上的注解，也可以使用 @SpringCloudApplication 代替：

```java
@SpringCloudApplication
public class HystrixApplication {

    public static void main(String[] args) {
        SpringApplication.run(HystrixApplication.class, args);
    }

    //提供一个 RestTemplate 实例
    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
       return new RestTemplate();
    }

}
```

这样，Hystrix 的配置就算完成了。接下来提供 Hystrix 的接口。在hystrix项目下创建service包，包下创建HelloService类。类中添加如下代码。

```java
@Service
public class HelloService {

    @Autowired
    RestTemplate restTemplate;

    /**
     * 在这个方法中，我们将发起一个远程调用，去调用 provider 中提供的 /hello 接口
     * 但是，这个调用可能会失败。1.provider连接不到。2.自己代码出现问题
     * 我们在这个方法上添加 @HystrixCommand 注解，配置 fallbackMethod 属性，这个属性表示该方法调用失败时的临时替代方法
     */
    @HystrixCommand(fallbackMethod = "error")
    public String hello() {
         return restTemplate.getForObject("http://provider/hello", String.class);
    }

    /**
     * 注意，这个方法名字要和 fallbackMethod 一致
     * 方法返回值也要和对应的方法一致
     */
    public String error(Throwable t) {
        return "error：" + t.getMessage();
    }

}
```

之后，在hystrix项目中创建controller包，在包下创建HelloController类。在类中添加如下方法。

```java
@RestController
public class HelloController {

    @Autowired
    HelloService helloService;

    /*这是常规的测试方法*/
    @GetMapping("/hello")
    public String hello() {
       return helloService.hello();
    }

}
```

这时，调用方法时会跳到service对应方法，如果请求provider失败，则会进入error方法。 

## 7.3 请求命令

在hystrix项目下的controller包的HelloService类。类中添加如下代码调用方法：

```java
    @GetMapping("/hello2")
    public void hello2() {
        HelloCommand helloCommand = new HelloCommand(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("javaboy")), restTemplate, "javaboy");
        String execute = helloCommand.execute();//直接执行
        System.out.println(execute);
        HelloCommand helloCommand2 = new HelloCommand(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("javaboy")), restTemplate, "javaboy");
        try {
            Future<String> queue = helloCommand2.queue();
            String s = queue.get();
            System.out.println(s);//先入队，后执行
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
```

注意：

- 一个实例只能执行一次

- 可以直接执行，也可以先入队，后执行

也通过注解实现请求异步调用。首先，定义如下方法，返回 Future<String> 。在hystrix项目下的controller包的HelloService类。类中添加如下代码调用方法

```java
    /*
    * 使用注解实现请求异步调用。
    * */
    @HystrixCommand(fallbackMethod = "error")
    public Future<String> hello2() {
        return new AsyncResult<String>() {
            @Override
            public String invoke() {
                return restTemplate.getForObject("http://provider/hello", String.class);
            }
        };
    }
```

然后，在hystrix项目下的controller包的HelloController类。类中添加如下代码调用方法调用该方法：

```java
    /*
    * 调用注解实现请求异步调用的方法
    * */
    @GetMapping("/hello3")
    public void hello3() {
        Future<String> hello2 = helloService.hello2();
        try {
            String s = hello2.get();
            System.out.println(s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
```

通过继承的方式使用 Hystrix，如何实现服务容错/降级？重写继承类的 getFallback 方法即可：在hystrix项目下的config包的HelloCommand类。类中添加如下代码调用方法

```java
//请求命令就是以继承类的方式来替代前面的注解方式
public class HelloCommand extends HystrixCommand<String> {

    RestTemplate restTemplate;

    public HelloCommand(Setter setter, RestTemplate restTemplate) {
        super(setter);
        this.restTemplate = restTemplate;
    }

    /*这是运行的方法*/
    @Override
    protected String run() throws Exception {
        return restTemplate.getForObject("http://provider/hello", String.class);
    }

    /*
    * 实现服务容错/降级,重写此方法
    *这个方法就是请求失败的回调
    *  */
    @Override
    protected String getFallback() {
        return "error-extends:" +getExecutionException().getMessage();
    }

}
```

## 7.4 异常处理

就是当发起服务调用时，如果不是 provider 的原因导致请求调用失败，而是 consumer 中本身代码有问题导致的请求失败，即 consumer 中抛出了异常，这个时候，也会自动进行服务降级，只不过这个时候降级，我们还需要知道到底是哪里出异常了。

如下示例代码，如果 hello 方法中，执行时抛出异常，那么一样也会进行服务降级，进入到 error 方法中，在 error 方法中，我们可以获取到异常的详细信息：在hystrix项目下的service包的HelloService类。类中添加如下代码调用方法

```java
@Service
public class HelloService {

    @Autowired
    RestTemplate restTemplate;

    /**
     * 在这个方法中，我们将发起一个远程调用，去调用 provider 中提供的 /hello 接口
     * 但是，这个调用可能会失败。1.provider连接不到。2.自己代码出现问题
     * 我们在这个方法上添加 @HystrixCommand 注解，配置 fallbackMethod 属性，这个属性表示该方法调用失败时的临时替代方法
     */
    @HystrixCommand(fallbackMethod = "error")
    public String hello() {
        int i = 1 / 0;       //这步就是自己的代码错误，用来测试情况2
         return restTemplate.getForObject("http://provider/hello", String.class);
    }
        /**
     * 注意，这个方法名字要和 fallbackMethod 一致
     * 方法返回值也要和对应的方法一致
     */
    public String error(Throwable t) {

        return "error：" + t.getMessage();
    }

}
```

这是注解的方式。也可以通过继承的方式：在hystrix项目下的config包的HelloCommand类。类中添加如下代码调用方法

```java
//请求命令就是以继承类的方式来替代前面的注解方式
public class HelloCommand extends HystrixCommand<String> {

    RestTemplate restTemplate;

    public HelloCommand(Setter setter, RestTemplate restTemplate) {
        super(setter);
        this.restTemplate = restTemplate;
    }

    /*这是运行的方法*/
    @Override
    protected String run() throws Exception {
         int i = 1 / 0;       //这是使用继承方法测试情况2.自己代码出现问题
        return restTemplate.getForObject("http://provider/hello", String.class);
    }

    /*
    * 实现服务容错/降级,重写此方法
    *这个方法就是请求失败的回调
    *  */
    @Override
    protected String getFallback() {
        return "error-extends:" +getExecutionException().getMessage();
    }

}
```

如果是通过继承的方式来做 Hystrix，在 getFallback 方法中，我们可以通过 getExecutionException 方法来获取执行的异常信息。

另一种可能性（作为了解）。如果抛异常了，我们希望异常直接抛出，不要服务降级，那么只需要配置  忽略某一个异常即可：

```java
@HystrixCommand(fallbackMethod = "error",ignoreExceptions = ArithmeticException.class)
public String hello() { 
    int i = 1 / 0;
    return restTemplate.getForObject("http://provider/hello", String.class);
}
```

这个配置表示当 hello 方法抛出 ArithmeticException 异常时，不要进行服务降级，直接将错误抛出。

## 7.5 请求缓存

请求缓存就是在 consumer 中调用同一个接口，如果参数相同，则可以使用之前缓存下来的数据。首先修改 provider 中的 hello2 接口，一会用来检测缓存配置是否生效：在provider项目下的controller包的HelloController类。类中添加如下代码调用方法

```java
    @GetMapping("/hello2")
    public String hello2(String name) {
        System.out.println(new Date() + ">>>" + name);      //这步打印是验证缓存问题。
        return "hello " + name;
    }
```

然后，在 hystrix 的请求方法中，添加如下注解：在hystrix 项目下的service包的HelloService类。类中添加如下代码调用方法

```java
    /*测试请求缓存*/
    @HystrixCommand(fallbackMethod = "error2")
    @CacheResult//这个注解表示该方法的请求结果会被缓存起来，默认情况下，缓存的 key 就是方法的参数，缓存的 value 就是方法的返回值。
    public String hello3(String name) {
        return restTemplate.getForObject("http://provider/hello2?name={1}", String.class, name);
    }
```

这个配置完成后，缓存并不会生效，一般来说，我们使用缓存，都有一个缓存生命周期这样一个概念。  这里也一样，我们需要初始化 HystrixRequestContext，初始化完成后，缓存开始生效， HystrixRequestContext close 之后，缓存失效。在hystrix 项目下的controller包的HelloController类。类中添加如下代码调用方法

```java
    /*测试请求缓存*/
    @GetMapping("/hello4")
    public void hello4() {
        HystrixRequestContext ctx = HystrixRequestContext.initializeContext();
        //第一请求完，数据已经缓存下来了.所以第二次调用时不会去使用provider的方法
        String javaboy = helloService.hello3("javaboy");
        javaboy = helloService.hello3("javaboy");
        ctx.close();
    }
```

在 ctx close 之前，缓存是有效的，close 之后，缓存就失效了。也就是说，访问一次 hello4 接口， provider 只会被调用一次（第二次使用的缓存），如果再次调用 hello4 接口，之前缓存的数据是失效的。

默认情况下，缓存的 key 就是所调用方法的参数，如果参数有多个，就是多个参数组合起来作为缓存的key。

例如如下方法：

```java
@HystrixCommand(fallbackMethod = "error2")
@CacheResult//这个注解表示该方法的请求结果会被缓存起来，默认情况下，缓存的 key 就是方法的参数，缓存的 value 就是方法的返回值。
public String hello3(String name,Integer age) {
return restTemplate.getForObject("http://provider/hello2?name={1}", String.class, name);
}
```

此时缓存的 key 就是 name+age，但是，如果有多个参数，但是又只想使用其中一个作为缓存的 key， 那么我们可以通过 @CacheKey 注解来解决。

```java
@HystrixCommand(fallbackMethod = "error2")
@CacheResult//这个注解表示该方法的请求结果会被缓存起来，默认情况下，缓存的 key 就是方法的参数，缓存的 value 就是方法的返回值。
public String hello3(@CacheKey String name, Integer age) {
return restTemplate.getForObject("http://provider/hello2?name={1}", String.class, name);
}
```

上面这个配置，虽然有两个参数，但是缓存时以 name 为准。也就是说，两次请求中，只要 name 一样，即使 age 不一样，第二次请求也可以使用第一次请求缓存的结果。

另外还有一个注解叫做 @CacheRemove()。在做数据缓存时，如果有一个数据删除的方法，我们一般除了删除数据库中的数据，还希望能够顺带删除缓存中的数据，这个时候 @CacheRemove() 就派上用场了。

@CacheRemove() 在使用时，必须指定 commandKey  属性，commandKey  其实就是缓存方法的名字，指定了 commandKey，@CacheRemove 才能找到数据缓存在哪里了，进而才能成功删除掉数据。

例如如下方法定义缓存与删除缓存：在hystrix 项目下的service包的HelloService类。类中添加如下代码调用方法

```java
    /*测试请求缓存*/
    @HystrixCommand(fallbackMethod = "error2")
    @CacheResult//这个注解表示该方法的请求结果会被缓存起来，默认情况下，缓存的 key 就是方法的参数，缓存的 value 就是方法的返回值。
    public String hello3(String name) {
        return restTemplate.getForObject("http://provider/hello2?name={1}", String.class, name);
    }

    /*定义删除数据时也删除缓存的方法.测试时缓存未删除。*/
    @HystrixCommand
    @CacheRemove(commandKey = "hello3")
    public String deleteUserByName(String name) {
        return "缓存已删除";
    }
```

再去调用：在hystrix 项目下的controller包的HelloController类。类中添加如下代码调用方法

```java
    /*测试删除数据时删除缓存*/
    @GetMapping("/hello4")
    public void hello4() {
        HystrixRequestContext ctx = HystrixRequestContext.initializeContext();
        //第一请求完，数据已经缓存下来了
        String javaboy = helloService.hello3("javaboy");
        //删除数据，同时缓存中的数据也会被删除
       String a =  helloService.deleteUserByName("javaboy");
        System.out.println(a);
        //第二次请求时，虽然参数还是 javaboy，但是缓存数据已经没了，所以这一次，provider 还是会收到请求
        javaboy = helloService.hello3("javaboy");
        ctx.close();
    }
```

如果是继承的方式使用 Hystrix ，只需要重写 getCacheKey 方法即可：在hystrix 项目下的config包的HelloCommand类。类中添加如下代码调用方法

```java
//请求命令就是以继承类的方式来替代前面的注解方式
public class HelloCommand extends HystrixCommand<String> {

    RestTemplate restTemplate;

    String name;

    public HelloCommand(Setter setter, RestTemplate restTemplate, String name) {
        super(setter);
        this.name = name;
        this.restTemplate = restTemplate;
    }

    /*这是运行的方法*/
//    @Override
//    protected String run() throws Exception {
//        int i = 1 / 0;       //这是使用继承方法测试情况2.自己代码出现问题
//        return restTemplate.getForObject("http://provider/hello", String.class);
//    }

    /*
    * 测试方法
    * */
    @Override
    protected String run() throws Exception {
        return restTemplate.getForObject("http://provider/hello2?name={1}", String.class, name);
    }

    /*
    * 实现服务容错/降级,重写此方法
    *这个方法就是请求失败的回调
    *  */
    @Override
    protected String getFallback() {
        return "error-extends:" +getExecutionException().getMessage();
    }

    /*这个是删除缓存*/
    @Override
    protected String getCacheKey() {
        return name;
    }

}
```

调用时候，一定记得初始化 HystrixRequestContext.在hystrix 项目下的controller包的HelloCommand类。类中添加如下代码调用方法

```java
    /*测试定义缓存删除*/
    @GetMapping("/hello2")
    public void hello2() {
        HystrixRequestContext ctx = HystrixRequestContext.initializeContext();
        HelloCommand helloCommand = new HelloCommand(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("javaboy")), restTemplate,"javaboy");
        String execute = helloCommand.execute();//直接执行
        System.out.println(execute);
        HelloCommand helloCommand2 = new HelloCommand(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("javaboy")), restTemplate,"javaboy");
        try {
            Future<String> queue = helloCommand2.queue();
            String s = queue.get();
            System.out.println(s);//先入队，后执行
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        ctx.close();
    }
```

## 7.6 请求同步

如果 consumer 中，频繁的调用 provider 中的同一个接口，在调用时，只是参数不一样，那么这样情况下，我们就可以将多个请求合并成一个，这样可以有效提高请求发送的效率。

首先我们在 provider 中提供一个请求合并的接口：在provider 项目下的controller包的UserController类。类中添加如下代码调用方法

```java
//这是测试请求合并的方法
@RestController
public class UserController {

    @GetMapping("/user/{ids}")//假设 consumer 传过来的多个 id 的格式是 1,2,3,4....
    public List<User> getUserByIds(@PathVariable String ids) {
        System.out.println(ids);
        String[] split = ids.split(",");
        List<User> users = new ArrayList<>();
        for (String s : split) {
            User u = new User();
            u.setId(Integer.parseInt(s));
            users.add(u);
        }
        return users;
    }
}
```

这个接口既可以处理合并之后的请求，也可以处理单个请求（单个请求的话，List  集合中就只有一项数据。）然后，在 Hystrix 中，定义 UserService：

在Hystrix 项目下的service包的UserService类。类中添加如下代码调用方法

```java
@Service
public class UserService {
    @Autowired
    RestTemplate restTemplate;

      /*常规的请求合并*/
    public List<User> getUsersByIds(List<Integer> ids) {
        User[] users = restTemplate.getForObject("http://provider/user/{1}", User[].class, StringUtils.join(ids, ","));
        return Arrays.asList(users);
    }
}
```

接下来定义 UserBatchCommand ，相当于我们之前的 HelloCommand：在Hystrix 项目下的config包的UserBatchCommand类。类中添加如下代码调用方法

```java
public class UserBatchCommand extends HystrixCommand<List<User>> {

    private List<Integer> ids;

    private UserService userService;

    public UserBatchCommand(List<Integer> ids, UserService userService) {
        super(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("batchCmd"))
              .andCommandKey(HystrixCommandKey.Factory.asKey("batchKey")));
        this.ids = ids;
        this.userService = userService;
    }

    @Override
    protected List<User> run() throws Exception {
        return userService.getUsersByIds(ids);
    }
}
```

最后，定义最最关键的请求合并方法：在Hystrix 项目下的config包的UserCollapseCommand类。类中添加如下代码调用方法

```java
//请求合并
public class UserCollapseCommand extends HystrixCollapser<List<User>, User, Integer> {

    private UserService userService;

    private Integer id;

    public UserCollapseCommand(UserService userService, Integer id) {
        super(HystrixCollapser.Setter.withCollapserKey(HystrixCollapserKey.Factory.asKey("UserCollapseCommand")).andCollapserPropertiesDefaults(HystrixCollapserProperties.Setter().withTimerDelayInMilliseconds(2000)));
        this.userService = userService;
        this.id = id;
    }

    /**
     * 请求参数
     */
    @Override
    public Integer getRequestArgument() {
        return id;
    }

    /**
     * 请求合并的方法
     */
    @Override
    protected HystrixCommand<List<User>> createCommand(Collection<CollapsedRequest<User, Integer>> collection) {
        List<Integer> ids = new ArrayList<>(collection.size());
        for (CollapsedRequest<User, Integer> userIntegerCollapsedRequest : collection) {
            ids.add(userIntegerCollapsedRequest.getArgument());
        }
        return new UserBatchCommand(ids, userService);
    }

    /**
     * 请求结果分发
     */
    @Override
    protected void mapResponseToRequests(List<User> users, Collection<CollapsedRequest<User, Integer>> collection) {
        int count = 0;
        for (CollapsedRequest<User, Integer> request : collection) {
            request.setResponse(users.get(count++));
        }
    }

}
```

最后就是测试调用：在Hystrix 项目下的controller包的HelloController类。类中添加如下代码调用方法

```java
    @Autowired
    UserService userService;

    /*测试请求合并的方法.请求时需要配置两个或以上的提供者*/
    @GetMapping("/hello5")
    public void hello5() throws ExecutionException, InterruptedException {
        HystrixRequestContext ctx = HystrixRequestContext.initializeContext();
        UserCollapseCommand cmd1 = new UserCollapseCommand(userService, 99);
        UserCollapseCommand cmd2 = new UserCollapseCommand(userService, 98);
        UserCollapseCommand cmd3 = new UserCollapseCommand(userService, 97);
        Future<User> q1 = cmd1.queue();
        Future<User> q2 = cmd2.queue();
        Future<User> q3 = cmd3.queue();
        User u1 = q1.get();
        User u2 = q2.get();
        User u3 = q3.get();
        System.out.println(u1);
        System.out.println(u2);
        System.out.println(u3);
//        Thread.sleep(2000);
        UserCollapseCommand cmd4 = new UserCollapseCommand(userService, 96);
        Future<User> q4 = cmd4.queue();
        User u4 = q4.get();
        System.out.println(u4);
        ctx.close();
    }
```

通过注解实现请求合并：在Hystrix 项目下的service包的UserService类。类中添加如下代码调用方法

```java
//测试请求合并
@Service
public class UserService {
    @Autowired
    RestTemplate restTemplate;

    /*使用注解方式来请求合并*/
    @HystrixCollapser(batchMethod = "getUsersByIds",collapserProperties = {@HystrixProperty(name = "timerDelayInMilliseconds",value = "200")})
    public Future<User> getUserById(Integer id) {
        return null;
    }

    @HystrixCommand
    public List<User> getUsersByIds(List<Integer> ids) {
        User[] users = restTemplate.getForObject("http://provider/user/{1}", User[].class, StringUtils.join(ids, ","));
        return Arrays.asList(users);
    }


}
```

这里的核心是 @HystrixCollapser 注解。在这个注解中，指定批处理的方法即可。测试代码如下：

在Hystrix 项目下的controller包的HelloController类。类中添加如下代码调用方法

```java
    /*
    * 测试注解实现请求合并
    * */
    @GetMapping("/hello6")
    public void hello6() throws ExecutionException, InterruptedException {
        HystrixRequestContext ctx = HystrixRequestContext.initializeContext();
        Future<User> q1 = userService.getUserById(99);
        Future<User> q2 = userService.getUserById(98);
        Future<User> q3 = userService.getUserById(97);
        User u1 = q1.get();
        User u2 = q2.get();
        User u3 = q3.get();
        System.out.println(u1);
        System.out.println(u2);
        System.out.println(u3);
        Thread.sleep(2000);
        Future<User> q4 = userService.getUserById(96);
        User u4 = q4.get();
        System.out.println(u4);
        ctx.close();
    }
```

