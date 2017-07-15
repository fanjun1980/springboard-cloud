# Springboard-Cloud

## 简介
springboard-cloud 项目是一个微服务系统的示例，项目目标是将dubbo与Spring Cloud技术栈融合，同时集成基于Spring Security的应用安全体系，实现一个支持OAuth2安全认证的RESTful API服务系统。

项目提供如下功能： **OAuth2认证、RBAC权限体系、配置管理 、服务发现、熔断、动态路由、分布式跟踪、应用监控**

## 系统架构
![](http://i.imgur.com/saTYAgm.png)

## 特性
1. 实现基于OAuth2的API权限认证与RBAC权限体系，提供基于角色的Url细粒度授权；账户、授权与认证数据均保存在数据库中
2. 将Dubbo融入到Spring Cloud体系，作为一种可选的rpc方式
   - 基于java config，在Spring boot中集成Dubbo
   - Dubbo支持Hystrix的断路器功能
   - Dubbo支持Sleuth的分布式跟踪 //TODO
3. 提供对外统一的服务网关（Zuul），实现安全认证、动态路由、限流等功能
4. 注册中心（Eureka）提供服务注册、发现功能
5. 所有服务的配置文件通过Spring Cloud Config来管理 //TODO
6. 通过Spring Sleuth实现微服务间调用的分布式追踪，通过ZipKin展示 //TODO
7. 应用监控
   - 服务调用过程通过聚合器（Turbine）统一所有断路信息；
   - 微服务状态通过Spring Boot Admin统一收集与展示；
8. 使用Swagger生成API文档
9. 使用Docker部署

## 技术栈
- Spring Cloud Netflix
- Spring Security OAuth2
- Spring Cloud Sleuth
- Spring Cloud Config
- Spring Boot Admin
- Spring Boot
- ZipKin
- Swagger
- Dubbo
- Docker

## 快速开始
//TODO

## 文档与资料
//TODO

## 截图
//TODO