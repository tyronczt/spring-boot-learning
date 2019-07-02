## 一、Docker

### 1、简介

官网：https://www.docker.com/

官方文档：https://docs.docker.com/get-started/

**Docker**是一个开源的应用容器引擎；是一个轻量级容器技术；

Docker支持将软件编译成一个镜像；然后在镜像中各种软件做好配置，将镜像发布出去，其他使用者可以直接使用这个镜像；

运行中的这个镜像称为容器，容器启动是非常快速的。

### 2、核心概念

docker主机(**Host**)：安装了Docker程序的机器（Docker直接安装在操作系统之上）；

docker客户端(**Client**)：连接docker主机进行操作；

docker仓库(**Registry**)：用来保存各种打包好的软件镜像；

docker镜像(**Images**)：软件打包好的镜像；放在docker仓库中；

docker容器(**Container**)：镜像启动后的实例称为一个容器；容器是独立运行的一个或一组应用

使用Docker的步骤：

1）、安装Docker

2）、去Docker仓库找到这个软件对应的镜像；

3）、使用Docker运行这个镜像，这个镜像就会生成一个Docker容器；

4）、对容器的启动停止就是对软件的启动停止；

### 3、安装Docker

#### 1）、在Linux虚拟机上安装Docker

1、检查内核版本，必须是3.10及以上

```shell
uname -r
```

2、安装docker

```shell
yum install docker
```

3、输入y确认安装
4、启动 docker

```shell
systemctl start docker
```

5、查看 docket 版本

```
docker -v
```

6、开机启动 docker

```shell
systemctl enable docker
```

7、停止docker

```shell
systemctl stop docker
```

附：[centos7 docker升级到最新稳定版本](https://www.cnblogs.com/wdliu/p/10194332.html)

![最新版本](https://raw.githubusercontent.com/tyronczt/spring-boot-learning/master/images/spring-boot-docker-%20version.png)

