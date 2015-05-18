#HTTP Resource Adaptor [![Build Status](http://ec2-52-5-86-189.compute-1.amazonaws.com/job/http-jca/badge/icon?style=plastic)](http://ec2-52-5-86-189.compute-1.amazonaws.com/job/http-jca/) [![Coverage Status](https://coveralls.io/repos/ozoli/http-jca-adaptor/badge.svg?branch=develop)](https://coveralls.io/r/ozoli/http-jca-adaptor?branch=develop)
This HTTP Resource Adaptor is an implementation of the [Java EE Java Connector Architecture 1.6](http://en.wikipedia.org/wiki/Java_EE_Connector_Architecture) using the [Apache HTTP client](https://hc.apache.org/httpcomponents-client-ga/) as the underlying HTTP implementation. 

The [Arquillian](http://arquillian.org/) test framework from [JBoss](http://www.jboss.org/) is used to execute the unit tests against an embedded [WildFly](http://wildfly.org/) 8 instance locally and a remote [WildFly](http://wildfly.org/) 8 instance on  [OpenShift](http://openshift.redhat.com) from [Jenkins](https://jenkins-ci.org/).

#Building
[JDK 8](http://www.oracle.com/technetwork/java/javase/overview/index.html) and [Apache Maven 3.2.2](https://maven.apache.org/) has been used in development. 
To run the tests against a remote WildFly or JBoss instance on [OpenShift](http://openshift.redhat.com) create a file called `wildfly-dev.properties` in `~/config/` (eg. `/users/bloke/config`) with content similar to:

```bash
wildfly.namespace=aussieollie
wildfly.application=wildfly8
wildfly.libraDomain=rhcloud.com
wildfly.sshUserName=52aa84455222446d5aa33eedd
wildfly.login=someBloke@gmail.com
wildfly.identityFile=/Users/bloke/.ssh/jenkins
wildfly.deploymentTimeoutInSeconds=300
wildfly.disableStrictHostChecking=true

undertow.http.host=localhost
undertow.http.port=8180
```

The `wildfly.identityFile` is the SSH private key of the [WildFly](http://wildfly.org/) instance on [OpenShift](http://openshift.redhat.com).

##Examples
Coming soon! At least a simple HTTP example for WildFly / JBoss and another using a Jersey client.

##Support
Please submit any **HTTP-Resource-Adaptor** bugs, issues, and feature requests to [ozoli/http-jca-adaptor](//github.com/ozoli/http-jca-adaptor/issues).

##License
Copyright (c) 2014-2015 [Luminis Groep BV](http://lumins.eu)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/ozoli/http-jca-adaptor/trend.png)](https://bitdeli.com/free "Bitdeli Badge")

