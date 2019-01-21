## Spring Boot 配置

### 1、配置文件

Spring Boot使用一个全局的配置文件

- application.properties
- application.yml

配置文件放在`src/main/resources`目录或者类路径 /config 下

**yml** 是 **YAML**（YAML Ain't Markup Language）语言的文件，以数据为中
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

### 2、YAML语法

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




