# 三步构建SpringBoot对单表的增删改查

## Spring Boot API Project Seed Plus

>  来源自：https://github.com/lihengming/spring-boot-api-project-seed

### 第一步 ：下载源码

https://github.com/tyronczt/spring-boot-learning/tree/master/spring-boot-00-seed-plus

### 第二步：配置数据库

#### 导入数据表

```sql
DROP TABLE IF EXISTS `t_user_info`;
CREATE TABLE `t_user_info`  (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'id',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '姓名',
  `sex` varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '性别',
  `age` int(11) NULL DEFAULT NULL COMMENT '年龄',
  `job` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '工作',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '新增时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_user_info
-- ----------------------------
INSERT INTO `t_user_info` VALUES ('1', '张三', '男', 32, '教师', '2020-04-18 16:00:55', NULL);
INSERT INTO `t_user_info` VALUES ('2', '李四', '女', 22, '学生', '2020-04-18 16:01:40', NULL);
INSERT INTO `t_user_info` VALUES ('3', '张莎莎', '女', 35, '律师', '2020-04-18 16:02:15', NULL);
INSERT INTO `t_user_info` VALUES ('4', '陈锋', '男', 33, '建筑师', '2020-04-18 16:03:01', NULL);

SET FOREIGN_KEY_CHECKS = 1;
```

#### 修改项目中的数据源及表名

application-dev.yml

```yaml
spring:
  datasource:
    username: root
    password: 123456
    url: jdbc:mysql://localhost:3306/user?useUnicode=true&characterEncoding=utf-8&useSSL=true&serverTimezone=UTC
    driver-class-name: com.mysql.cj.jdbc.Driver
```

spring-boot-00-seed-plus\src\main\java\com\tyron\core\Constant.java

```java
//JDBC配置，请修改为你项目的实际配置
public static final String JDBC_URL = "jdbc:mysql://localhost:3306/user?useUnicode=true&characterEncoding=utf-8&useSSL=true&serverTimezone=UTC";
public static final String JDBC_USERNAME = "root";
public static final String JDBC_PASSWORD = "123456";
public static final String JDBC_DIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
```

## 第三步：执行main方法，测试

#### 执行main方法

spring-boot-00-seed-plus\src\test\java\com\tyron\project\CodeGenerator.java

```java
public static void main(String[] args) {
	genCodeByCustomModelName("t_user_info", "TUserInfo", "用户信息");
}

提示信息
TUserInfo.java 生成成功
TUserInfoMapper.java 生成成功
TUserInfoMapper.xml 生成成功
TUserInfoService.java 生成成功
TUserInfoServiceImpl.java 生成成功
TUserInfoController.java 生成成功
```

#### 测试CRUD

1、启动项目

2、查看swagger

http://localhost:7080/swagger-ui.html#

3、Mybatis-Plus测试

```java
 @Resource
 private TUserInfoMapper tUserInfoMapper;

@GetMapping("mybatisplustest")
    public Result mybatisPlusTest() {
        List<TUserInfo> users = tUserInfoMapper.selectList(new QueryWrapper<TUserInfo>().lambda().like(TUserInfo::getName, "陈").lt(TUserInfo::getAge, 40));
        return ResultGenerator.genSuccessResult(users);
    }
```

注：测试原因，直接在controller里写Mapper，正常需要写在service中，或者直接调用Mybatis-Plus自带的service方法

## 改动说明

- 增加Mybatis-Plus插件，详细使用介绍可参考：[MyBatis-Plus 快速入门](https://blog.csdn.net/tian330726/article/details/106087857) 文末还有升级版
- 增加swagger的支持
- main方法中增加注释字段

```java
public static void main(String[] args) {
	genCodeByCustomModelName("t_user_info", "TUserInfo", "用户信息");
}
```

- 增加模块字段

```java
public static final String MODEL_PACKAGE = ".user";// 模块名称，可不填
```

