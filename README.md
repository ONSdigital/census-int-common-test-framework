# Common Service
This repository contains common framework code for the Response Management [Spring Boot](http://projects.spring.io/spring-boot/) applications, as well as reusable test framework code for the same.

[![Build Status](https://travis-ci.org/ONSdigital/rm-common-service.svg?branch=master)](https://travis-ci.org/ONSdigital/rm-common-service)

## framework
This project contains ONS Jersey/JAX-RS base classes for the Spring Boot applications.

## standards
This project contains project Java coding standards in the form of Checkstyle configuration and an Eclipse formatter.

## test-framework
This project provides the base class for the ONS Spring Boot/Jersey unit tests, which provides a Domain-Specific Language (DSL) for unit testing RESTful endpoints.

## Building Common Service

```
mvn --update-snapshots
```
## Copyright
Copyright (C) 2016 Crown Copyright (Office for National Statistics)
