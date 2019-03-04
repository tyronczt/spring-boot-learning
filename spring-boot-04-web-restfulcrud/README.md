# Spring Boot 与Web开发

### 一、SpringBoot对静态资源的映射规则

org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration.java

```java
public void addResourceHandlers(ResourceHandlerRegistry registry) {
			if (!this.resourceProperties.isAddMappings()) {
				logger.debug("Default resource handling disabled");
				return;
			}
			Duration cachePeriod = this.resourceProperties.getCache().getPeriod();
			CacheControl cacheControl = this.resourceProperties.getCache()
					.getCachecontrol().toHttpCacheControl();
			if (!registry.hasMappingForPattern("/webjars/**")) {
				customizeResourceHandlerRegistration(registry
						.addResourceHandler("/webjars/**")
						.addResourceLocations("classpath:/META-INF/resources/webjars/")
						.setCachePeriod(getSeconds(cachePeriod))
						.setCacheControl(cacheControl));
			}
			String staticPathPattern = this.mvcProperties.getStaticPathPattern();
			if (!registry.hasMappingForPattern(staticPathPattern)) {
				customizeResourceHandlerRegistration(
						registry.addResourceHandler(staticPathPattern)
								.addResourceLocations(getResourceLocations(
										this.resourceProperties.getStaticLocations()))
								.setCachePeriod(getSeconds(cachePeriod))
								.setCacheControl(cacheControl));
			}
		}

	<!-- 欢迎页 -->
	@Bean
    public WelcomePageHandlerMapping welcomePageHandlerMapping(
        ApplicationContext applicationContext) {
        return new WelcomePageHandlerMapping(
            new TemplateAvailabilityProviders(applicationContext),
            applicationContext, getWelcomePage(),
            this.mvcProperties.getStaticPathPattern());
    }
	private Optional<Resource> getWelcomePage() {
        String[] locations = getResourceLocations(
            this.resourceProperties.getStaticLocations());
        return Arrays.stream(locations).map(this::getIndexHtml)
            .filter(this::isReadable).findFirst();
    }

    private Resource getIndexHtml(String location) {
        return this.resourceLoader.getResource(location + "index.html");
    }

<!-- 图标配置 -->
@Configuration
@ConditionalOnProperty(value = "spring.mvc.favicon.enabled", matchIfMissing = true)
public static class FaviconConfiguration implements ResourceLoaderAware {

    private final ResourceProperties resourceProperties;
    private ResourceLoader resourceLoader;
    public FaviconConfiguration(ResourceProperties resourceProperties) {
        this.resourceProperties = resourceProperties;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Bean
    public SimpleUrlHandlerMapping faviconHandlerMapping() {
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        mapping.setUrlMap(Collections.singletonMap("**/favicon.ico",
                                                   faviconRequestHandler()));
        return mapping;
    }

    @Bean
    public ResourceHttpRequestHandler faviconRequestHandler() {
        ResourceHttpRequestHandler requestHandler = new ResourceHttpRequestHandler();
        requestHandler.setLocations(resolveFaviconLocations());
        return requestHandler;
    }

    private List<Resource> resolveFaviconLocations() {
        String[] staticLocations = getResourceLocations(
            this.resourceProperties.getStaticLocations());
        List<Resource> locations = new ArrayList<>(staticLocations.length + 1);
        Arrays.stream(staticLocations).map(this.resourceLoader::getResource)
            .forEach(locations::add);
        locations.add(new ClassPathResource("/"));
        return Collections.unmodifiableList(locations);
    }
  }
}
```

#### 1.1、所有 /webjars/** 

在 classpath:/META-INF/resources/webjars/ 找资源；

webjars：以jar包的方式引入静态资源；http://www.webjars.org/

```xml
<!--引入webjars-jquery的静态文件-->
<dependency>
    <groupId>org.webjars</groupId>
    <artifactId>jquery</artifactId>
    <version>3.3.1-2</version>
</dependency>
```

启动项目后：http://localhost:8080/webjars/jquery/3.3.1-2/jquery.js  可访问

#### 1.2、访问静态资源

```java
org.springframework.boot.autoconfigure.web.ResourceProperties
/**
  * Path pattern used for static resources.
  */
private String staticPathPattern = "/**";

private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
      "classpath:/META-INF/resources/", "classpath:/resources/",
      "classpath:/static/", "classpath:/public/" };

/**
 * Locations of static resources. Defaults to classpath:[/META-INF/resources/,
 * /resources/, /static/, /public/].
 */
private String[] staticLocations = CLASSPATH_RESOURCE_LOCATIONS;
```

项目启动后：http://localhost:8080/asserts/css/signin.css 可访问

#### 1.3、欢迎页

 静态资源文件夹下的所有index.html页面，同1.2的静态资源路径一样

项目启动后：http://localhost:8080 可访问

#### 1.4、 配置图标

**/favicon.ico 在静态资源文件中查找

### 二、模板引擎

JSP、Velocity、Freemarker、Thymeleaf



#### 2.1、SpringBoot 推荐模板引擎：[Thymeleaf](https://www.thymeleaf.org/)

引入依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

spring-boot-starters/2.1.3.RELEASE版本

```xml
<thymeleaf.version>3.0.11.RELEASE</thymeleaf.version>
<thymeleaf-extras-data-attribute.version>2.0.1</thymeleaf-extras-data-attribute.version>
<thymeleaf-extras-java8time.version>3.0.3.RELEASE</thymeleaf-extras-java8time.version>
<thymeleaf-extras-springsecurity.version>3.0.4.RELEASE</thymeleaf-extras-springsecurity.version>
<thymeleaf-layout-dialect.version>2.3.0</thymeleaf-layout-dialect.version>

如果切换thymeleaf版本，需要layout版本也要同步更改 
thymeleaf3主程序 需要layout2以上版本
```

#### 2.2、Thymeleaf的使用

ThymeleafAutoConfiguration 对应的配置规则 --> ThymeleafProperties：

```java
@ConfigurationProperties(prefix = "spring.thymeleaf")
public class ThymeleafProperties {

   private static final Charset DEFAULT_ENCODING = Charset.forName("UTF-8");

   private static final MimeType DEFAULT_CONTENT_TYPE = MimeType.valueOf("text/html");

   public static final String DEFAULT_PREFIX = "classpath:/templates/";

   public static final String DEFAULT_SUFFIX = ".html";
```

只要把HTML页面放在classpath:/templates/，thymeleaf就能自动渲染；

#### 2.3、语法规则

th:text；改变当前元素里面的文本内容；

th：任意html属性；来替换原生属性的值

| Order | Feature                         | Attributes                         |
| ----- | ------------------------------- | ---------------------------------- |
| 1     | Fragment inclusion              | th:insert
th:replace                |
| 2     | Fragment iteration              | th:each                            |
| 3     | Conditional evaluation          | th:if
th:unless
th:switch
th:case     |
| 4     | Local variable definition       | th:object
th:with                   |
| 5     | General attribute modification  | th:attr
th:attrprepend
th:attrappend |
| 6     | Specific attribute modification | th:value
th:href
th:src ···          |
| 7     | Text (tag body modification)    | th:text
th:utext                    |
| 8     | Fragment specification 声明片段 | th:fragment                        |
| 9     | Fragment removal                | th:remove                          |

Standard Expression features:

```properties
Simple expressions:
    Variable Expressions: ${...}：获取变量值；OGNL表达式；
    		1）、获取对象的属性、调用方法
    		2）、使用内置的基本对象：
    		#ctx : the context object.
            #vars: the context variables.
            #locale : the context locale.
            #request : (only in Web Contexts) the HttpServletRequest object.
            #response : (only in Web Contexts) the HttpServletResponse object.
            #session : (only in Web Contexts) the HttpSession object.
            #servletContext : (only in Web Contexts) the ServletContext object.
            3）、使用内置的工具对象：
            #execInfo : information about the template being processed.
#messages : methods for obtaining externalized messages inside variables expressions, in the same way as they would be obtained using #{…} syntax.
#uris : methods for escaping parts of URLs/URIs
#conversions : methods for executing the configured conversion service (if any).
#dates : methods for java.util.Date objects: formatting, component extraction, etc.
#calendars : analogous to #dates , but for java.util.Calendar objects.
#numbers : methods for formatting numeric objects.
#strings : methods for String objects: contains, startsWith, prepending/appending, etc.
#objects : methods for objects in general.
#bools : methods for boolean evaluation.
#arrays : methods for arrays.
#lists : methods for lists.
#sets : methods for sets.
#maps : methods for maps.
#aggregates : methods for creating aggregates on arrays or collections.
#ids : methods for dealing with id attributes that might be repeated (for example, as a result of an iteration).
    Selection Variable Expressions: *{...} 选择表达式，和${...}类似，配合th:object使用
    Message Expressions: #{...} 获取国际化内容
    Link URL Expressions: @{...} 定义URL；
    	@{/order/process(execId=${execId},execType='FAST')}
    Fragment Expressions: ~{...} 片段引用表达式
        <div th:insert="~{commons :: main}">...</div>
Literals：（字面量）
    Text literals: 'one text' , 'Another one!' ,…
    Number literals: 0 , 34 , 3.0 , 12.3 ,…
    Boolean literals: true , false
    Null literal: null
    Literal tokens: one , sometext , main ,…
Text operations:（文本操作）
    String concatenation: +
    Literal substitutions: |The name is ${name}|
    Arithmetic operations:
    Binary operators: + , - , * , / , %
    Minus sign (unary operator): -
Boolean operations:（布尔运算）
    Binary operators: and , or
    Boolean negation (unary operator): ! , not
    Comparisons and equality:
    Comparators: > , < , >= , <= ( gt , lt , ge , le )
    Equality operators: == , != ( eq , ne )
Conditional operators: （条件运算）
    If-then: (if) ? (then)
    If-then-else: (if) ? (then) : (else)
    Default: (value) ?: (defaultvalue)
Special tokens:  （特殊操作）
	No-Operation: _
```

### 三、SpringMVC自动配置

官方地址：https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-developing-web-applications.html#boot-features-spring-mvc-auto-configuration

29.1.1 Spring MVC Auto-configuration

Spring Boot provides auto-configuration for Spring MVC that works well with most applications.

The auto-configuration adds the following features on top of Spring’s defaults:

- Inclusion of `ContentNegotiatingViewResolver` and `BeanNameViewResolver` beans.
  - ContentNegotiatingViewResolver：组合所有的视图解析器，得到最合适的；
- Support for serving static resources, including support for WebJars (covered [later in this document](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-developing-web-applications.html#boot-features-spring-mvc-static-content))). 静态资源
- Automatic registration of `Converter`, `GenericConverter`, and `Formatter` beans.
  - Converter 转换器
  - Formatter 格式化器
- Support for `HttpMessageConverters` (covered [later in this document](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-developing-web-applications.html#boot-features-spring-mvc-message-converters)).
  - HttpMessageConverters：SpringMVC中用来转换HTTP请求和响应的
- Automatic registration of `MessageCodesResolver` (covered [later in this document](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-developing-web-applications.html#boot-features-spring-message-codes)).定义错误代码生成规则
- Static `index.html` support.静态首页访问
- Custom `Favicon` support (covered [later in this document](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-developing-web-applications.html#boot-features-spring-mvc-favicon)). 图标
- Automatic use of a `ConfigurableWebBindingInitializer` bean (covered [later in this document](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-developing-web-applications.html#boot-features-spring-mvc-web-binding-initializer)). initializing all  WebDataBinder instances.

If you want to keep Spring Boot MVC features and you want to add additional [MVC configuration](https://docs.spring.io/spring/docs/5.1.5.RELEASE/spring-framework-reference/web.html#mvc) (interceptors, formatters, view controllers, and other features), you can add your own `@Configuration` class of type `WebMvcConfigurer` but **without** `@EnableWebMvc`. If you wish to provide custom instances of `RequestMappingHandlerMapping`, `RequestMappingHandlerAdapter`, or `ExceptionHandlerExceptionResolver`, you can declare a `WebMvcRegistrationsAdapter` instance to provide such components.

If you want to take complete control of Spring MVC, you can add your own `@Configuration` annotated with `@EnableWebMvc`.

#### 3.1、扩展SpringMVC

编写一个配置类（@Configuration），是WebMvcConfigurerAdapter类型；不能标注@EnableWebMvc

```java
* WebMvcConfigurerAdapter @deprecated as of 5.0 {@link WebMvcConfigurer} has default methods (made
* possible by a Java 8 baseline) and can be implemented directly without the
* need for this adapter
```

```java
@Configuration
public class MyMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 浏览器发送 /tyron 请求，来到hello页面
        registry.addViewController("/tyron").setViewName("hello");
    }
}
```

原理：

​	1）、WebMvcAutoConfiguration是SpringMVC的自动配置类

​	2）、在做其他自动配置时会导入；@Import(**EnableWebMvcConfiguration**.class)

```java
 @Configuration
	public static class EnableWebMvcConfiguration extends DelegatingWebMvcConfiguration {
      private final WebMvcConfigurerComposite configurers = new WebMvcConfigurerComposite();

	 //从容器中获取所有的WebMvcConfigurer
      @Autowired(required = false)
      public void setConfigurers(List<WebMvcConfigurer> configurers) {
          if (!CollectionUtils.isEmpty(configurers)) {
              this.configurers.addWebMvcConfigurers(configurers);
            	//一个参考实现；将所有的WebMvcConfigurer相关配置都来一起调用；  
            	@Override
             // public void addViewControllers(ViewControllerRegistry registry) {
             //    for (WebMvcConfigurer delegate : this.delegates) {
             //       delegate.addViewControllers(registry);
             //   }
              }
          }
	}
```

​	3）、容器中所有的WebMvcConfigurer都会一起起作用；

​	4）、我们的配置类也会被调用；

​	效果：SpringMVC的自动配置和我们的扩展配置都会起作用；

#### 3.2、全面接管SpringMVC

在配置类中添加`@EnableWebMvc`，即可全面接管SpringMVC，所有的SpringMVC的自动配置都失效。

原理：

​	1）、DelegatingWebMvcConfiguration

```java
@Import(DelegatingWebMvcConfiguration.class)
public @interface EnableWebMvc {
}
```

​	2）、WebMvcConfigurationSupport

```java
@Configuration
public class DelegatingWebMvcConfiguration extends WebMvcConfigurationSupport {
```

​	3）、@ConditionalOnMissingBean(WebMvcConfigurationSupport.class)

```java
@Configuration
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnClass({ Servlet.class, DispatcherServlet.class, WebMvcConfigurer.class })
// 容器中没有这个组件的时候，这个自动配置类才生效
@ConditionalOnMissingBean(WebMvcConfigurationSupport.class)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 10)
@AutoConfigureAfter({ DispatcherServletAutoConfiguration.class,
      TaskExecutionAutoConfiguration.class, ValidationAutoConfiguration.class })
public class WebMvcAutoConfiguration {
```

​	4）WebMvcConfigurationSupport 中只有 SpringMVC 最基本的功能。

#### 3.3、修改SpringBoot的默认配置

模式：

​	1）、SpringBoot在自动配置很多组件的时候，先看容器中有没有用户自己配置的（@Bean、@Component）如果有就用用户配置的，如果没有，才自动配置；如果有些组件可以有多个（ViewResolver）将用户配置的和自己默认的组合起来；

​	2）、在SpringBoot中会有非常多的xxxConfigurer帮助我们进行扩展配置

​	3）、在SpringBoot中会有很多的xxxCustomizer帮助我们进行定制配置

### 四、RestfulCRUD

#### 4.1、默认访问首页

```java
@Override
public void addViewControllers(ViewControllerRegistry registry) {
    // 浏览器发送 /tyron 请求，来到hello页面
    registry.addViewController("/tyron").setViewName("hello");
    // 设置浏览器默认页面为login.html
    registry.addViewController("/").setViewName("login");
    registry.addViewController("index.html").setViewName("login");
}
```

#### 4.2、国际化

步骤：

​	1）、编写国际化配置文件，抽取页面需要显示的国际化消息；

​	2）、SpringBoot自动配置好了管理国际化资源文件的组件；

```properties
spring.messages.basename=i18n.login
```

```java
@Configuration
@ConditionalOnMissingBean(value = MessageSource.class, search = SearchStrategy.CURRENT)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@Conditional(ResourceBundleCondition.class)
@EnableConfigurationProperties
public class MessageSourceAutoConfiguration {

   private static final Resource[] NO_RESOURCES = {};

   @Bean
   @ConfigurationProperties(prefix = "spring.messages")
   public MessageSourceProperties messageSourceProperties() {
      return new MessageSourceProperties();
   }

   @Bean
   public MessageSource messageSource(MessageSourceProperties properties) {
      ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
      if (StringUtils.hasText(properties.getBasename())) {
         // 设置国际化资源文件的基础名
         messageSource.setBasenames(StringUtils.commaDelimitedListToStringArray(
               StringUtils.trimAllWhitespace(properties.getBasename())));
      }
      if (properties.getEncoding() != null) {
         messageSource.setDefaultEncoding(properties.getEncoding().name());
      }
      messageSource.setFallbackToSystemLocale(properties.isFallbackToSystemLocale());
      Duration cacheDuration = properties.getCacheDuration();
      if (cacheDuration != null) {
         messageSource.setCacheMillis(cacheDuration.toMillis());
      }
      messageSource.setAlwaysUseMessageFormat(properties.isAlwaysUseMessageFormat());
      messageSource.setUseCodeAsDefaultMessage(properties.isUseCodeAsDefaultMessage());
      return messageSource;
   }
```

3）、去页面获取国际化信息

```html
<form class="form-signin" action="dashboard.html">
    <img class="mb-4" src="asserts/img/bootstrap-solid.svg" th:src="@{/asserts/img/bootstrap-solid.svg}" alt=""
         width="72" height="72">
    <h1 class="h3 mb-3 font-weight-normal" th:text="#{login.tip}">Please sign in</h1>
    <label class="sr-only" th:text="#{login.username}">Username</label>
    <input type="text" class="form-control" placeholder="Username" required="" autofocus=""
           th:placeholder="#{login.username}">
    <label class="sr-only" th:text="#{login.password}">Password</label>
    <input type="password" class="form-control" placeholder="Password" required="" th:placeholder="#{login.password}">
    <div class="checkbox mb-3">
        <label>
            <input type="checkbox" value="remember-me"> [[#{login.remember}]]
        </label>
    </div>
    <button class="btn btn-lg btn-primary btn-block" type="submit" th:text="#{login.btn}">Sign in</button>
    <p class="mt-5 mb-3 text-muted">© 2017-2018</p>
    <a class="btn btn-sm">中文</a>
    <a class="btn btn-sm">English</a>
</form>
```

原理：国际化Locale（区域信息对象）；LocaleResolver（获取区域信息对象）；

系统默认地区解析器

```java
@Bean
@ConditionalOnMissingBean
@ConditionalOnProperty(prefix = "spring.mvc", name = "locale")
public LocaleResolver localeResolver() {
   if (this.mvcProperties
         .getLocaleResolver() == WebMvcProperties.LocaleResolver.FIXED) {
      return new FixedLocaleResolver(this.mvcProperties.getLocale());
   }
   AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
   localeResolver.setDefaultLocale(this.mvcProperties.getLocale());
   return localeResolver;
}
默认的就是根据请求头带来的区域信息获取Locale进行国际化
```

4）、点击链接切换国际化

```html
<!-- 设置点击链接 -->
<a class="btn btn-sm" th:href="@{/index.html(l=zh_CN)}">中文</a>
<a class="btn btn-sm" th:href="@{/index.html(l=en_US)}">English</a>
```

```java
/**
* 自定义区域信息解析器
*/
public class MyLocaleResolver implements LocaleResolver {
    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        String parameter = request.getParameter("l");
        // 初始化区域信息，默认系统区域
        Locale locale = Locale.getDefault();
        if (!StringUtils.isEmpty(parameter)) {
            String[] strings = parameter.split("_");
            locale = new Locale(strings[0], strings[1]);
        }
        return locale;
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {

    }
}

// 注册自己的LocaleResolver
@Bean
public LocaleResolver localeResolver() {
    return new MyLocaleResolver();
}
```

#### 4.3、登录

```java
@Controller
public class LoginController {

    @PostMapping("/user/login")
    public String login(@RequestParam("username") String username, @RequestParam("password") String password, Map<String, Object> map) {
        if (!StringUtils.isEmpty(username) && "123456".equals(password)) {
            // 登录成功
            return "dashboard";
        } else {
            map.put("errMsg", "用户名或密码错误");
            return "login";
        }
    }
}
```

页面：

```html
<form class="form-signin" action="dashboard.html" th:action="@{/user/login}" method="post">
    <!--不为空时，提示错误-->
    <p style="color:red" th:if="${not #strings.isEmpty(errMsg)}" th:text="${errMsg}"></p>
    <label class="sr-only" th:text="#{login.username}">Username</label>
    <input type="text" class="form-control" name="username" placeholder="Username" required="" autofocus=""
           th:placeholder="#{login.username}">
    <label class="sr-only" th:text="#{login.password}">Password</label>
    <input type="password" class="form-control" name="password" placeholder="Password" required="" th:placeholder="#{login.password}">
    ···
</form>
```

禁用缓存

```properties
spring.thymeleaf.cache=false
```

重定向请求主页，防止表单重复提交

```java
@Configuration
public class MyMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 设置登录后跳转的主页
        registry.addViewController("main.html").setViewName("dashboard");
    }
}

LoginController
 // 登录成功，重定向
return "redirect:/main.html";
```

#### 4.4、登录拦截

步骤：

1）、配置自己的拦截器

```java
public class LoginHandlerInterceptor implements HandlerInterceptor {

    /**
     * 目标方法执行前
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object user = request.getSession().getAttribute("loginUser");
        if (user == null) {
            // 未登录，返回登录页面
            request.setAttribute("errMsg","没有权限访问，请登录");
            request.getRequestDispatcher("/index.html").forward(request, response);
            return false;
        } else {
            // 已登录
            return true;
        }
    }
}
```

2）、将拦截器注入，并设置相应规则

注意添加：`"/asserts/**"`,`"/webjars/**"` 

**SpringBoot 2.x的如果自定义HandlerInterceptor拦截器时访问静态资源就会被同步拦截，需要手动去除拦截**

```java
@Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginHandlerInterceptor()).addPathPatterns("/**").excludePathPatterns("/index.html","/","/user/login","/asserts/**","/webjars/**");
    }
```

#### 4.5、CRUD-员工列表

实验要求：

1）、RestfulCRUD：CRUD满足Rest风格；

URI：  /资源名称/资源标识       HTTP请求方式区分对资源CRUD操作

|      | 普通CRUD（uri来区分操作） | RestfulCRUD       |
| ---- | ------------------------- | ----------------- |
| 查询 | getEmp                    | emp---GET         |
| 添加 | addEmp?xxx                | emp---POST        |
| 修改 | updateEmp?id=xxx&xxx=xx   | emp/{id}---PUT    |
| 删除 | deleteEmp?id=1            | emp/{id}---DELETE |

2）、实验的请求架构;

| 实验功能     | 请求URI  | 请求方式 |
| ------------ | -------- | -------- |
| 查询所有员工 | emps     | GET      |
| 查询某个员工 | emp/{id} | GET      |
| 访问添加页面 | emp      | GET      |
| 添加员工     | emp      | POS      |
| 修改员工     | emp      | PUT      |
| 删除员工     | emp/{id} | DELETE   |

#### 4.6、公共页提取

1）、抽取公共片段

```html
<div th:fragment="copy">    
&copy; 2011 The Good Thymes Virtual Grocery
</div>
```

2）、引入公共片段

```html
<div th:insert="~{footer :: copy}"></div>
~{templatename::fragmentname} 模板名::片段名 like in the ~{footer :: copy} above
~{templatename::selector} 模板名:: 选择器
```

3）、显示效果

insert的功能片段显示在div内

如果使用th:insert等属性进行引入，可以不用写~{}：

行内写法要加上：[[~{}]]; [(~{})]；

4）、三种引入方式

- **th:insert** is the simplest: it will simply insert the specified fragment as the body of its host tag.
- **th:replace** actually replaces its host tag with the specified fragment.
- **th:include** is similar to th:insert , but instead of inserting the fragment it only inserts the contents of this fragment.

```html
<footer th:fragment="copy">
&copy; 2011 The Good Thymes Virtual Grocery
</footer>

引入方式
<div th:insert="footer :: copy"></div>
<div th:replace="footer :: copy"></div>
<div th:include="footer :: copy"></div>

效果
<div>
    <footer>
    &copy; 2011 The Good Thymes Virtual Grocery
    </footer>
</div>

<footer>
&copy; 2011 The Good Thymes Virtual Grocery
</footer>

<div>
&copy; 2011 The Good Thymes Virtual Grocery
</div>
```

