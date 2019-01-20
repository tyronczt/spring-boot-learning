## 使用idea快速构建Spring Boot项目

### Spring Initializr

idea --> File --> New --> Project --> Spring Initializr 

默认生成的Spring Boot项目；

- 主程序已经生成好，我们只需要实现自己的逻辑
- resources文件夹中目录结构
  - static：保存所有的静态资源； js  css  images；
  - templates：保存所有的模板页面；（Spring Boot默认jar包使用嵌入式的Tomcat，默认不支持JSP页面）；可以使用模板引擎（freemarker、thymeleaf）；
  - application.properties：Spring Boot应用的配置文件；可以修改一些默认设置；

