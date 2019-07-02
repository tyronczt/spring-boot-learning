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

#### 2）、测试 Docker 运行

1、通过运行简单的Docker映像测试安装是否工作, [hello-world](https://hub.docker.com/_/hello-world/):

```shell
# docker run hello-world

Hello from Docker!
This message shows that your installation appears to be working correctly.

To generate this message, Docker took the following steps:
 1. The Docker client contacted the Docker daemon.
 2. The Docker daemon pulled the "hello-world" image from the Docker Hub.
    (amd64)
 3. The Docker daemon created a new container from that image which runs the
    executable that produces the output you are currently reading.
 4. The Docker daemon streamed that output to the Docker client, which sent it
    to your terminal.

To try something more ambitious, you can run an Ubuntu container with:
 $ docker run -it ubuntu bash

Share images, automate workflows, and more with a free Docker ID:
 https://hub.docker.com/

For more examples and ideas, visit:
 https://docs.docker.com/get-started/

```

2、列出下载到您的机器上的hello-world镜像:

```shell
# docker image ls

REPOSITORY      TAG      IMAGE ID       CREATED       SIZE
hello-world     latest    fce289e99eb9  6 months ago  1.84kB
```

3、列出hello-world容器(由镜像生成)，该容器在显示其消息后退出。如果它还在运行，您就不需要—all选项:

```shell
# docker container ls --all
CONTAINER ID     IMAGE           COMMAND      CREATED            STATUS
54f4984ed6a8     hello-world     "/hello"     20 seconds ago     Exited (0) 19 seconds ago
```

## 4、Docker常用命令&操作

### 1）镜像操作

| 操作 | 命令                                            | 说明                                                     |
| ---- | ----------------------------------------------- | -------------------------------------------------------- |
| 检索 | docker  search 关键字  eg：docker  search redis | 我们经常去docker  hub上检索镜像的详细信息，如镜像的TAG。 |
| 拉取 | docker pull 镜像名:tag                          | :tag是可选的，tag表示标签，多为软件的版本，默认是latest  |
| 列表 | docker images                                   | 查看所有本地镜像                                         |
| 删除 | docker rmi image-id                             | 删除指定的本地镜像                                       |

https://hub.docker.com/

### 2）容器操作

软件镜像（QQ安装程序）----运行镜像----产生一个容器（正在运行的软件，运行的QQ）；

【此处以安装  **rabbitmq** 为例】步骤：

> 注：获取镜像的时候要获取management版本的，不要获取last版本的，management版本的才带有管理界面。

```shell
1、搜索镜像
# docker search rabbitmq:management

NAME     DESCRIPTION              STARS               OFFICIAL            AUTOMATED
macintoshplus/rabbitmq-management   Based on rabbitmq:management    5           [OK]
transmitsms/rabbitmq-sharded        Fork of rabbitmq:management with sharded_exc…   0   
xiaochunping/rabbitmq               xiaochunping/rabbitmq:management   2018-06-30   0   

2、拉取镜像
# docker pull rabbitmq:management

management: Pulling from library/rabbitmq
5b7339215d1d: Pull complete 
14ca88e9f672: Pull complete 
a31c3b1caad4: Pull complete 
b054a26005b7: Pull complete 
eef17c6cb6cf: Pull complete 
1b1c29c0085b: Pull complete 
f8dfa8d24f5a: Pull complete 
eb8ab9a01cdc: Pull complete 
ab779a040984: Pull complete 
5662cbdafc1c: Pull complete 
7f07e4815b29: Pull complete 
b056c3deadc8: Pull complete 
Digest: sha256:5d61d789788cc4ee4396b693f99b1763086d43fd2ee215ca6f2725784131d0f7
Status: Downloaded newer image for rabbitmq:management

3、运行镜像
# docker run -d --hostname my-rabbit --name rabbit -p 5672:5672 -p 15672:15672 rabbitmq:management

1aeb5bd4bb70a8cda409ef8ff911980705319c31407943eccd4e917687e4ba25

--d： 后台进程运行
--hostname：指定容器主机名称
--name：指定容器名称
--p：将mq端口号映射到本地
--p：15672:15672 http访问端口
--p：5672:5672 amqp访问端口

4、查看运行中的容器
# docker ps

CONTAINER ID        IMAGE                 COMMAND                  CREATED             STATUS              PORTS                 NAMES
9820f1cd68d4        rabbitmq:management   "docker-entrypoint.s…"   12 minutes ago      Up 12 minutes       4369/tcp, 5671/tcp, 0.0.0.0:5672->5672/tcp, 15671/tcp, 25672/tcp, 0.0.0.0:15672->15672/tcp   rabbit

5、 停止运行中的容器
# docker stop  容器的id

6、查看所有的容器
# docker ps -a

7、启动容器
# docker start 容器id

8、删除一个容器
# docker rm 容器id

更多命令参看
https://docs.docker.com/engine/reference/commandline/docker/
```

此时就可以登录RabbitMQ 的 WEB界面了，访问地址是[ip:15672]默认用户名和密码都是`guest`

![rabbitmq](https://raw.githubusercontent.com/tyronczt/spring-boot-learning/master/images/spring-boot-docker-rabbitmq.png)

> 注：此处是部署在阿里云上的，如果要访问15672端口，需要设置安全组，详情查看：[添加安全组规则](<https://help.aliyun.com/document_detail/25471.html?spm=5176.2020520101.0.0.68c44df5ylEX19>)

> 如果是本地虚拟机运行的话，关闭防火墙：
>
> ```shell
> #关闭防火墙设置开机不启动
> [root@docker ~]# systemctl stop firewalld
> [root@docker ~]# systemctl disable firewalld
> ```

参考文章：[[RabbitMQ系列（五）使用Docker部署RabbitMQ集群](https://www.cnblogs.com/vipstone/p/9362388.html)]
