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

![spring-boot-web-thymeleaf](https://raw.githubusercontent.com/tyronczt/spring-boot-learning/master/images/spring-boot-web-thymeleaf.png)

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

#### 4.7、添加员工

```java
/**
  * 添加页面
  *
  * @param model 页面显示参数
  * @return 添加页
  */
@GetMapping("/emp/add")
public String addPage(Model model) {
    // 获取部门列表
    Collection<Department> departments = departmentDao.getDepartments();
    model.addAttribute("depts", departments);
    return "emp/add";
}

/**
 * 添加请求
 *
 * @param employee 员工实体
 * @return 员工列表页
 */
@PostMapping("/emp")
public String addEmp(Employee employee) {
    System.out.println(employee);
    // 添加
    employeeDao.save(employee);
    return "redirect:/emps";
}
```

```html
添加页面

<!--引入 topbar-->
<div th:replace="commons/bar :: topbar"></div>

<div class="container-fluid">
   <div class="row">
      <!--引入侧边栏-->
      <div th:replace="commons/bar :: #sidebar(activeUri='emps')"></div>

      <main role="main" class="col-md-9 ml-sm-auto col-lg-10 pt-3 px-4">
         <form th:action="@{/emp}" method="post">
            <div class="form-group">
               <label>LastName</label>
               <input type="text" name="lastName" class="form-control" placeholder="zhangsan">
            </div>
            <div class="form-group">
               <label>Email</label>
               <input type="email" name="email" class="form-control" placeholder="zhangsan@atguigu.com">
            </div>
            <div class="form-group">
               <label>Gender</label><br/>
               <div class="form-check form-check-inline">
                  <input class="form-check-input" type="radio" name="gender"  value="1">
                  <label class="form-check-label">男</label>
               </div>
               <div class="form-check form-check-inline">
                  <input class="form-check-input" type="radio" name="gender"  value="0">
                  <label class="form-check-label">女</label>
               </div>
            </div>
            <div class="form-group">
               <label>department</label>
               <select class="form-control" name="department.id">
                  <option th:value="${dept.id}" th:each="dept : ${depts}" th:text="${dept.departmentName}">1</option>
               </select>
            </div>
            <div class="form-group">
               <label>Birth</label>
               <input type="text" name="birth" class="form-control" placeholder="zhangsan">
            </div>
            <button type="submit" class="btn btn-primary">添加</button>
         </form>
      </main>
   </div>
</div>
```

#### 4.8、修改员工

```java
/**
 * 修改页面（同添加页面）
 *
 * @param model 页面显示参数
 * @return 页面
 */
@GetMapping("/emp/{id}")
public String modifyPage(@PathVariable("id") Integer id, Model model) {
    // 获取部门列表
    Collection<Department> departments = departmentDao.getDepartments();
    model.addAttribute("depts", departments);
    // 获取原信息
    Employee employee = employeeDao.get(id);
    model.addAttribute("emp", employee);
    return "emp/edit";
}

/**
 * 修改操作
 *
 * @param employee 员工信息
 * @return 员工列表页
 */
@PutMapping("emp")
public String editEmp(Employee employee) {
    System.out.println(employee);
    employeeDao.save(employee);
    return "redirect:/emps";
}
```

添加页面和删除页面同用一个页面，在属性中增加非空判断

```html
<main role="main" class="col-md-9 ml-sm-auto col-lg-10 pt-3 px-4">
            <form th:action="@{/emp}" method="post">
                <!--发送put请求修改员工数据，由springboot配置的HiddenHttpMethodFilter处理
                    创建input项，name="_method"；值就是我们指定的请求方式-->
                <input type="hidden" name="_method" value="put" th:if="${emp!=null}"/>
                <input type="hidden" name="id" th:if="${emp!=null}" th:value="${emp.id}">
                <div class="form-group">
                    <label>LastName</label>
                    <input type="text" name="lastName" class="form-control" placeholder="zhangsan" th:value="${emp!=null}?${emp.lastName}">
                </div>
                <div class="form-group">
                    <label>Email</label>
                    <input type="email" name="email" class="form-control" placeholder="zhangsan@atguigu.com" th:value="${emp!=null}?${emp.email}">
                </div>
                <div class="form-group">
                    <label>Gender</label><br/>
                    <div class="form-check form-check-inline">
                        <input class="form-check-input" type="radio" name="gender" value="1" th:checked="${emp!=null}?${emp.gender==1}">
                        <label class="form-check-label">男</label>
                    </div>
                    <div class="form-check form-check-inline">
                        <input class="form-check-input" type="radio" name="gender" value="0" th:checked="${emp!=null}?${emp.gender==0}">
                        <label class="form-check-label">女</label>
                    </div>
                </div>
                <div class="form-group">
                    <label>department</label>
                    <select class="form-control" name="department.id">
                        <option th:value="${dept.id}" th:each="dept : ${depts}" th:text="${dept.departmentName}" th:selected="${emp!=null}?${dept.id == emp.department.id}">1
                        </option>
                    </select>
                </div>
                <div class="form-group">
                    <label>Birth</label>
                    <input name="birth" type="text" class="form-control" placeholder="zhangsan" th:value="${emp!=null}?${#dates.format(emp.birth, 'yyyy-MM-dd HH:mm')}">
                </div>
                <button type="submit" class="btn btn-primary" th:text="${emp!=null}?'修改':'添加'">添加</button>
            </form>
        </main>
    </div>
</div>
```

#### 4.9、员工删除

```java
/**
 * 删除员工
 *
 * @param id 员工id
 * @return 员工列表页
 */
@DeleteMapping("emp/{id}")
public String deleteEmp(@PathVariable("id") Integer id) {
    employeeDao.delete(id);
    return "redirect:/emps";
}
```

```html
<!--修改成一个表单，通过jquery提交-->
<button th:attr="del_uri=@{/emp/}+${emp.id}" class="btn btn-sm btn-danger deleteBtn">删除</button>

<form id="deleteEmpForm" method="post">
    <input type="hidden" name="_method" value="delete"/>
</form>

<script>
    $(".deleteBtn").click(function () {
        $("#deleteEmpForm").attr("action",$(this).attr("del_uri")).submit();
        return false;// 取消按钮的默认行为
    })
</script>
```

### 五、错误处理机制

#### 5.1、SpringBoot默认的错误处理机制

1）、浏览器访问，返回一个默认的错误页面：

![error page](https://raw.githubusercontent.com/tyronczt/spring-boot-learning/master/images/spring-boot-web-error-01.png)

2）、其他客户端访问，默认返回json数据：

```json
{
    "timestamp": "2019-03-07T14:15:38.625+0000",
    "status": 404,
    "error": "Not Found",
    "message": "No message available",
    "path": "/crud/aaa"
}
```

原理分析（步骤）：

系统出现4xx或5xx之类的错误后，**ErrorPageCustomizer**就会生效（定制错误的响应规则）；就会来到/error请求；被**BasicErrorController**处理，根据请求方式不同交由**ErrorViewResolver**（接口，实现类是**DefaultErrorViewResolver**），在有模板引擎的情况下访问error文件夹下的指定错误码页面，否则直接访问静态资源路径下的错误页面。

参照org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration，错误处理自动配置，并在容器中添加以下组件：

- ErrorPageCustomizer： **错误页面定制器**

```java
// bean注入
@Bean
public ErrorPageCustomizer errorPageCustomizer() {
		return new ErrorPageCustomizer(this.serverProperties, this.dispatcherServletPath);
}
// 主要方法
@Override
public void registerErrorPages(ErrorPageRegistry errorPageRegistry) {
    ErrorPage errorPage = new ErrorPage(this.dispatcherServletPath
                               .getRelativePath(this.properties.getError().getPath()));
    errorPageRegistry.addErrorPages(errorPage);
}

// 注册error页面，而页面的请求路径由getPath方法返回,所以当系统出现错误以后，会来到error请求进行处理。
public String getPath() {
    return this.path;
}
@Value("${error.path:/error}")
private String path = "/error";
```

- BasicErrorController：**错误控制器** 处理默认/error请求

```java
@Bean
	@ConditionalOnMissingBean(value = ErrorController.class, search = SearchStrategy.CURRENT)
	public BasicErrorController basicErrorController(ErrorAttributes errorAttributes) {
		return new BasicErrorController(errorAttributes, this.serverProperties.getError(),
				this.errorViewResolvers);
	}
```

```java
@Controller
@RequestMapping("${server.error.path:${error.path:/error}}")
public class BasicErrorController extends AbstractErrorController {

   @RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
   // public static final String TEXT_HTML_VALUE = "text/html";
   // 浏览器发送的请求头为："text/html"
   public ModelAndView errorHtml(HttpServletRequest request,
         HttpServletResponse response) {
      HttpStatus status = getStatus(request);
      //getErrorAttributes 根据错误信息来封装一些model数据，用于页面显示
      Map<String, Object> model = Collections.unmodifiableMap(getErrorAttributes(
            request, isIncludeStackTrace(request, MediaType.TEXT_HTML)));
      response.setStatus(status.value());
    
      //返回错误页面，包含页面地址和页面内容
      ModelAndView modelAndView = resolveErrorView(request, response, status, model);
      return (modelAndView != null) ? modelAndView : new ModelAndView("error", model);
   }

   // 其他客户端请求，由此方法处理
   @RequestMapping
   public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
      Map<String, Object> body = getErrorAttributes(request,
            isIncludeStackTrace(request, MediaType.ALL));
      HttpStatus status = getStatus(request);
      return new ResponseEntity<>(body, status);
   }
}

protected ModelAndView resolveErrorView(HttpServletRequest request,
			HttpServletResponse response, HttpStatus status, Map<String, Object> model) {
 //获取所有的视图解析器来处理这个错误信息，而这个 errorViewResolvers 对象其实就是DefaultErrorViewResolver对象的集合
		for (ErrorViewResolver resolver : this.errorViewResolvers) {
			ModelAndView modelAndView = resolver.resolveErrorView(request, status, model);
			if (modelAndView != null) {
				return modelAndView;
			}
		}
		return null;
	}
```

- DefaultErrorViewResolver：**默认错误视图处理器**

```java
@Bean
@ConditionalOnBean(DispatcherServlet.class)
@ConditionalOnMissingBean
public DefaultErrorViewResolver conventionErrorViewResolver() {
    return new DefaultErrorViewResolver(this.applicationContext,
                                        this.resourceProperties);
}
```

DefaultErrorViewResolver定义类

```java
public class DefaultErrorViewResolver implements ErrorViewResolver, Ordered {

   private static final Map<Series, String> SERIES_VIEWS;

   static {
      Map<Series, String> views = new EnumMap<>(Series.class);
      views.put(Series.CLIENT_ERROR, "4xx");
      views.put(Series.SERVER_ERROR, "5xx");
      SERIES_VIEWS = Collections.unmodifiableMap(views);
   }
    
    @Override
	public ModelAndView resolveErrorView(HttpServletRequest request, HttpStatus status,
			Map<String, Object> model) {
        // 先以错误状态码作为错误页面名
		ModelAndView modelAndView = resolve(String.valueOf(status.value()), model);
        // 如果无法处理，则使用4xx或者5xx作为错误页面名
		if (modelAndView == null && SERIES_VIEWS.containsKey(status.series())) {
			modelAndView = resolve(SERIES_VIEWS.get(status.series()), model);
		}
		return modelAndView;
	}
    
	private ModelAndView resolve(String viewName, Map<String, Object> model) {
        //错误页面：error/400，或者error/404，或者error/500...
		String errorViewName = "error/" + viewName;
        //模版引擎可以解析到这个页面地址就用模版引擎来解析
		TemplateAvailabilityProvider provider = this.templateAvailabilityProviders
				.getProvider(errorViewName, this.applicationContext);
		if (provider != null) {
            //模版引擎可用的情况下返回到errorViewName指定的视图
			return new ModelAndView(errorViewName, model);
		}
        //模版引擎不可用的情况下，在静态资源文件夹下查找errorViewName对应的页面
		return resolveResource(errorViewName, model);
	}

	private ModelAndView resolveResource(String viewName, Map<String, Object> model) {
		for (String location : this.resourceProperties.getStaticLocations()) {
			try {
                //从静态资源文件中查找errorViewName对应的页面
				Resource resource = this.applicationContext.getResource(location);
				resource = resource.createRelative(viewName + ".html");
				if (resource.exists()) {
					return new ModelAndView(new HtmlResourceView(resource), model);
				}
			}
			catch (Exception ex) {
			}
		}
		return null;
	}
```

- DefaultErrorAttributes：默认错误页面属性

```java
@Bean
@ConditionalOnMissingBean(value = ErrorAttributes.class, search = SearchStrategy.CURRENT)
public DefaultErrorAttributes errorAttributes() {
   return new DefaultErrorAttributes();
}

@Override
public Map<String, Object> getErrorAttributes(RequestAttributes requestAttributes,
                                              boolean includeStackTrace) {
    Map<String, Object> errorAttributes = new LinkedHashMap<String, Object>();
    errorAttributes.put("timestamp", new Date());
    addStatus(errorAttributes, requestAttributes);
    addErrorDetails(errorAttributes, requestAttributes, includeStackTrace);
    addPath(errorAttributes, requestAttributes);
    return errorAttributes;
}

页面设置属性：
timestamp： 时间戳
status：状态码
error：错误提示
exception：异常对象
message：异常消息
errors：JSR303数据校验的所有错误
```

#### 5.2、定制错误响应页面

1）、存在模板引擎，会在模板引擎文件夹下的error文件夹下查找指定状态码的视图；

可以使用4xx和5xx作为错误文件，匹配该类型的所有错误，精确匹配优先。

2）、未找到模板引擎（或模板引擎文件夹下未找到error文件夹），会在静态资源文件夹下找；

3）、模板引擎文件夹和静态资源文件夹下均没有错误页面时，会使用SpringBoot默认错误页面。

```java
// 默认错误页面bean
private final SpelView defaultErrorView = new SpelView(
      "<html><body><h1>Whitelabel Error Page</h1>"
            + "<p>This application has no explicit mapping for /error, so you are seeing this as a fallback.</p>"
            + "<div id='created'>${timestamp}</div>"
            + "<div>There was an unexpected error (type=${error}, status=${status}).</div>"
            + "<div>${message}</div></body></html>");

@Bean(name = "error")
@ConditionalOnMissingBean(name = "error")
public View defaultErrorView() {
   return this.defaultErrorView;
}
```

#### 5.3、定制错误json数据

```java
@ControllerAdvice
public class MyExceptionHandler {

    @ResponseBody
    @ExceptionHandler(UserNotExistException.class)
    public Map<String, Object> handException(Exception e) {
        Map<String,Object> map = new HashMap<>();
        map.put("code","user not exist");
        map.put("msg",e.getMessage());
        return  map;
    }
}
// 未实现自定义效果，浏览器和其他客户端访问都是json数据
```

实现自定义效果，需要将错误结果转发到默认的/error请求进行解析，同时还需要带上错误码（否则都是200）

```java
@ExceptionHandler(UserNotExistException.class)
    public String handException(Exception e, HttpServletRequest request) {
        
        request.setAttribute("javax.servlet.error.status_code",400);
        Map<String, Object> map = new HashMap<>();
        map.put("code", "user not exist");
        map.put("msg", e.getMessage());
        // 将错误提示设值到request中
        request.setAttribute("ext", map);
        // 转发到默认的BasicErrorController /error请求中
        return "forward:/error";
    }

protected HttpStatus getStatus(HttpServletRequest request) {
  	Integer statusCode = (Integer) request
         .getAttribute("javax.servlet.error.status_code");
```

实现自定义属性的功能：由于error方法设置属性时都调用DefaultErrorAttributes的getErrorAttributes()方法，所以只要继承DefaultErrorAttributes，重写getErrorAttributes方法，将自己的属性放入即可。

```java
@Component
public class MyErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
        Map<String, Object> map = super.getErrorAttributes(webRequest, includeStackTrace);
        map.put("author", "tyron");
        // 从request中获取自定义的错误提示
        Object ext = webRequest.getAttribute("ext", 0);
        map.put("ext", ext);
        return map;
    }
```

最终效果：自适应的响应和自定义的错误内容。

### 六、配置嵌入式Servlet容器

SpringBoot默认使用嵌入的Servlet容器---Tomcat

![spring-boot-web-tomcat](https://raw.githubusercontent.com/tyronczt/spring-boot-learning/master/images/spring-boot-web-tomcat-01.png)

#### 6.1、修改Servlet容器相关配置

1）、修改配置文件`application.properties`，设置ServerProperties属性

```properties
server.port=8081
server.context-path=/crud

server.tomcat.uri-encoding=UTF-8

//通用的Servlet容器设置
server.xxx
//Tomcat的设置
server.tomcat.xxx
```

2）编写WebServerFactoryCustomizer，设置Server属性

```java
//    SpringBoot1.5.x
//    @Bean
//    public EmbeddedServletContainerCustomizer embeddedServletContainerCustomizer() {
//        return new EmbeddedServletContainerCustomizer() {
//            @Override
//            public void customize(ConfigurableEmbeddedServletContainer container) {
//                container.setPort(8090);
//            }
//        };
//    }
    // SpringBoot2.x.x
    @Bean
    public WebServerFactoryCustomizer webServerFactoryCustomizer() {
        return new WebServerFactoryCustomizer<ConfigurableServletWebServerFactory>() {
            @Override
            public void customize(ConfigurableServletWebServerFactory factory) {
                factory.setPort(8090);
            }
        };
    }
    // 附上官方文档：https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-programmatic-embedded-container-customization
```

#### 6.2、注册Servlet三大组件【Servlet、Filter、Listener】

```java
// 注册Servlet组件------ServletRegistrationBean
@Bean
public ServletRegistrationBean myServlet() {
    return new ServletRegistrationBean(new MyServlet(), "/myservlet");
}

public class MyServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().write("Hello tyron MyServlet!");
    }
```

```java
// 注册Filter组件------FilterRegistrationBean
@Bean
public FilterRegistrationBean myFilter() {
    FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
    filterRegistrationBean.setFilter(new MyFilter());
    filterRegistrationBean.setUrlPatterns(Arrays.asList("/myfilter", "/myservlet"));
    return filterRegistrationBean;
}

public class MyFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("MyFilter process......");
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
```

```java
// 注册Listener------ServletListenerRegistrationBean
@Bean
public ServletListenerRegistrationBean myListener(){
    return  new ServletListenerRegistrationBean(new MyListener());
}

public class MyListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("contextInitialized...启动");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("contextDestroyed...销毁");
    }
}
```

SpringBoot自动配置SpringMVC前端控制器：DispatcherServlet

```java
@Bean(name = DEFAULT_DISPATCHER_SERVLET_REGISTRATION_BEAN_NAME)
		@ConditionalOnBean(value = DispatcherServlet.class, name = DEFAULT_DISPATCHER_SERVLET_BEAN_NAME)
		public DispatcherServletRegistrationBean dispatcherServletRegistration(
				DispatcherServlet dispatcherServlet) {
            // 默认拦截：/ 所有请求，包括静态资源，但不拦截jsp请求
            // 通过修改spring.mvc.servlet.path来修改SpringMVC默认拦截的请求路径
			DispatcherServletRegistrationBean registration = new DispatcherServletRegistrationBean(
					dispatcherServlet, this.webMvcProperties.getServlet().getPath());
			registration.setName(DEFAULT_DISPATCHER_SERVLET_BEAN_NAME);
			registration.setLoadOnStartup(
					this.webMvcProperties.getServlet().getLoadOnStartup());
			if (this.multipartConfig != null) {
				registration.setMultipartConfig(this.multipartConfig);
			}
			return registration;
		}
```

#### 6.3、替换其他嵌入式Servlet容器

默认支持：

Tomcat（默认）

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

Jetty

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <artifactId>spring-boot-starter-tomcat</artifactId>
            <groupId>org.springframework.boot</groupId>
        </exclusion>
    </exclusions>
</dependency>

<!-- 引入jetty -->
<dependency>
    <artifactId>spring-boot-starter-jetty</artifactId>
    <groupId>org.springframework.boot</groupId>
</dependency>
```

Undertow

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <artifactId>spring-boot-starter-tomcat</artifactId>
            <groupId>org.springframework.boot</groupId>
        </exclusion>
    </exclusions>
</dependency>

<!-- 引入undertow -->
<dependency>
    <artifactId>spring-boot-starter-undertow</artifactId>
    <groupId>org.springframework.boot</groupId>
</dependency>
```
