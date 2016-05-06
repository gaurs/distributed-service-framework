# Distributed Services Framework

The project represents a multi-tier distributed services framework that can be used to scale current applications. Following are the main modules:

  - Service layer (deployed on any servlet container)
  - Framework (shared between client and service layer)
  - Client

The framework is the core engine that is used to route the requests as generted from the client layer to the appropriate tier. The framework is actually based on [Remote Proxy Design Pattern] which is modified to suite current requirements

> **Remote Proxy** – Represents an object locally which belongs to a different address space. Think of an ATM implementation, it will hold proxy objects for bank information that exists in the remote server.

The detailed architecture and detils can be found at the project [documentation] page.

### Version
- 0.01

### How to Install
```sh
$ git clone https://github.com/gaurs/DistributedServiceFramework.git
```
```sh
$ cd DistributedServiceFramework
```
```sh
$ mvn clean install
```
### Whom to contact
Please raise any concern or report issue directly. You can contact us at:
- sumit.gaur@optimumalgorithms.com
- pankaj.agrawal@optimumalgorithms.com






[Remote Proxy Design Pattern]:http://en.wikipedia.org/wiki/Proxy_pattern
[documentation]:#
