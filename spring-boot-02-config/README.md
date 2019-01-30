## Spring Boot 配置

### 一、配置文件

Spring Boot使用一个全局的配置文件

- application.properties
- application.yml

配置文件放在`src/main/resources`目录或者类路径 /config 下

**yml **是 **YAML**（YAML Ain't Markup Language）语言的文件，以数据为中
心，比 json、xml 等更适合做配置文件

http://www.yaml.org/ 参考语法规范

全局配置文件的可以对一些默认配置值进行修改

YAML ： 配置例子

```yaml
server:
  port: 8081
```

XML ：配置例子

```xml
<server>
	<port>8081<port>
</server>
```

### 二、YAML语法

#### 1、基本语法

- 使用缩进表示层级关系
- 缩进时不允许使用Tab键，只允许使用**空格**
- 缩进的空格数目不重要，只要相同层级的元素左侧对齐即可
- 大小写敏感

#### 2、YAML 支持的三种数据结构

- 字面量：单个的、不可再分的值
- 对象：键值对的集合
- 数组：一组按次序排列的值

##### 2.1、字面量

- 数字、字符串、布尔、日期
- 字符串
  - 默认不使用引号
  - 可以使用单引号或者双引号，单引号会转义特殊字符，双引号不会转义
  - 字符串可以写成多行，从第二行开始，必须有一个单空格缩进，换行符会被转为空格。

name:   "zhangsan \n lisi"：输出；zhangsan 换行  lisi

name:   ‘zhangsan \n lisi’：输出；zhangsan \n  lisi

##### 2.2、对象（Map）

- 对象的一组键值对，使用冒号分隔。如：username: admin
- 冒号后面跟**空格**来分开键值
- {k: v}是行内写法

行内写法：

```yaml
friends: {name: xiaoming,age: 18}
```

##### 2.2、数组（List、Set）：

一组连词线（-）开头的行，构成一个数组，[]为行内写法

```yaml
pets: [cat,dog,pig]
```

#### 3、配置文件值注入

注入类：

```java
/** 
 * 将配置文件中配置的每一个属性的值，映射到这个组件中
 * @ConfigurationProperties：告诉SpringBoot将本类中的所有属性和配置文件中相关的配置进行绑定；
 *       prefix = "person"：配置文件中哪个下面的所有属性进行一一映射
 * 
 * 只有这个组件是容器中的组件，才能容器提供的@ConfigurationProperties功能；
 */
@Component
@ConfigurationProperties(prefix = "person")
public class Person {

    private String name;
    private Integer age;
    private Boolean boss;
    private Date birth;

    private Map<String, Object> maps;
    private List<Object> lists;
    private Animal animal;
    
    getter/setter
}
```

yaml配置文件：

```yaml
person:
  name: xiaoming
  age: 18
  boss: true
  birth: 2010/01/01
  maps:
    hello: world
    say: hello
  lists: [dog,cat,pig]
  animal:
    name: xiaohuang
    age: 3
```

打印：

```java
Person{name='xiaoming', age=18, boss=true, birth=Fri Jan 01 00:00:00 CST 2010, maps={hello=world, say=hello}, lists=[dog, cat, pig], animal=Animal{name='xiaohuang', age=3}}
```

##### 3.1、properties配置文件在idea中默认utf-8可能会乱码

File --> Editor --> File Encodings

![](C:\Users\Administrator\Desktop\java-learn\images\spring-boot-config-01.png)

##### 3.2、@Value获取值和@ConfigurationProperties获取值比较

|                      | @ConfigurationProperties | @Value     |
| -------------------- | ------------------------ | ---------- |
| 功能                 | 批量注入配置文件中的属性 | 一个个指定 |
| 松散绑定（松散语法） | 支持                     | 不支持     |
| SpEL                 | 不支持                   | 支持       |
| JSR303数据校验       | 支持                     | 不支持     |
| 复杂类型封装         | 支持                     | 不支持     |

配置文件yml还是properties都能获取到值；

如果说，我们只是在某个业务逻辑中需要获取一下配置文件中的某项值，使用@Value；

如果说，我们专门编写了一个javaBean来和配置文件进行映射，我们就直接使用@ConfigurationProperties；

##### 3.3、配置文件注入值数据校验

```java
@Component
@ConfigurationProperties(prefix = "person")
@Validated
public class Person {

    /**
     * <bean class="Person">
     *      <property name="lastName" value="字面量/${key}从环境变量、配置文件中获取值/#{SpEL}"></property>
     * <bean/>
     */

//    @Value("${person.name}")
//    @Email
    private String name;
//    @Value("#{11*3}")
    private Integer age;
//    @Value("false")
    private Boolean boss;
//    @Value("2010/01/01")
    private Date birth;

    // 通过@Value的测试：Person{name='小马', age=33, boss=false, birth=Fri Jan 01 00:00:00 CST 2010, maps=null, lists=null, animal=null}

    //    @Value("${person.maps}")---不支持这种写法（IllegalArgumentException）
    private Map<String, Object> maps;
    private List<Object> lists;
    private Animal animal;
```

##### 3.4、@PropertySource & @ImportResource & @Bean

**@PropertySource**：加载指定的配置文件；

person.properties

```properties
# 配置person的属性
person.name=小李
person.age=15
person.birth=2011/01/01
person.boss=false
person.animal.name=小灰
person.animal.age=1
person.maps.h1=11
person.maps.jj=11
person.lists=1,2,3
```

Person.java 部分

```java
@PropertySource(value = {"classpath:person.properties"})
@Component
@ConfigurationProperties(prefix = "person")
//@Validated
public class Person {
    ...
}
```

test测试数据

```java
Person{name='小李', age=15, boss=false, birth=Sat Jan 01 00:00:00 CST 2011, 
maps={h1=11, jj=11}, lists=[1, 2, 3], animal=Animal{name='小灰', age=1}}
```

**@ImportResource**：导入Spring的配置文件，让配置文件里面的内容生效；

bean的配置文件：beans.xml

```xml
<bean id="helloService" class="com.tyron.springboot.service.HelloService"></bean>
```

启动类：SpringBoot02ConfigApplication.java

```java
@ImportResource(value = "classpath:beans.xml")
@SpringBootApplication
public class SpringBoot02ConfigApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBoot02ConfigApplication.class, args);
    }
}
```

测试方法：

```java
@Autowired
ApplicationContext ioc;

@Test
public void testService() {
    boolean containsBean = ioc.containsBean("helloService");
    System.out.println("containsBean：" + containsBean);
}

// 输出结果：
containsBean：true
```

SpringBoot推荐给容器中添加组件的方式；**推荐使用全注解的方式**

配置类 MyAppConfig：

```java
/**
 * @Configuration 指明当前类是配置类，代替之前的xml配置文件
 * 在Spring配置文件中使用<bean></bean>，在配置文件中使用@Bean 注解
 */
@Configuration
public class MyAppConfig {

    /**
     * @Bean 注解的作用是将方法的返回值添加到容器中；
     * 容器中该组件默认的id就是方法名
     */
    @Bean
    public HelloService helloService02() {
        System.out.println("通过配置类的方式注入Bean");
        return new HelloService();
    }
}
```

测试方法：

```java
@Test
public void testService() {
    boolean containsBean = ioc.containsBean("helloService02");
    System.out.println("containsBean：" + containsBean);
}

// 输出结果
通过配置类的方式注入Bean
containsBean：true
```

#### 4、配置文件占位符

##### 4.1 随机数

```properties
${random.value}、${random.int}、${random.long}、${random.uuid}
${random.int(10)}、${random.int[1024,65536]}
```

##### 4.2、占位符获取之前配置的值，如果没有可以是用:指定默认值

```properties
${person.hello:hello}_h1
```

例子：

```properties
# 配置person的属性
person.name=小李${random.uuid}
person.age=${random.int}
person.birth=2011/01/01
person.boss=false
person.animal.name=${person.name}_animal
person.animal.age=${random.int(10)}
person.maps.h1=${person.hello:hello}_h1
person.maps.jj=${random.value}
person.lists=1,2,3

# 打印结果
Person{name='小马33922644-860e-4655-b1cd-0f4da7862169', age=-1366299504, boss=false, birth=Sat Jan 01 00:00:00 CST 2011, maps={h1=hello_h1, jj=f4903021ea950ac7529dbcd9e300fc26}, lists=[1, 2, 3], animal=Animal{name='小马b3c42bef-e6a1-4bbe-98fd-e1a7ed771238_animal', age=7}}
```

#### 5、Profile

##### 5.1、多Profile

在主配置文件编写的时，文件名可以是   application-{profile}.properties/yml

默认使用 **application.properties** 的配置；

##### 5.2、yml支持多文档块方式

```yaml
server:
  port: 8081
spring:
  profiles:
    active: dev
---
server:
  port: 8082
spring:
  profiles: dev

---
server:
  port: 8083
spring:
  profiles: prod
```

##### 5.3、激活指定profile

- 在**配置文件**中指定  spring.profiles.active=dev [properties的格式]，yml的激活方式如上；
- **启动参数**[Program arguments]：--spring.profiles.active=dev
- **命令行**：mvn-package 打成jar包后，java -jar spring-boot-02-config-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
- **虚拟机参数**[VM options]：-Dspring.profiles.active=dev

注：properties文件的优先级高于yml文件

#### 6、配置文件加载位置

springboot 启动会扫描以下位置的application.properties或者application.yml文件作为Spring boot的默认配置文件

–file:./config/   

–file:./

–classpath:/config/

–classpath:/

注：[-file ：当前项目的文件路径；–classpath：类路径]

优先级由高到底，高优先级的配置会覆盖低优先级的配置；

SpringBoot会从这四个位置全部加载主配置文件；**互补配置**；



