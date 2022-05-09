# CIE/CNS Liferay Authentication Token-based SSO

[![SMC Tech Blog](https://img.shields.io/badge/Mainteiner-SMC%20Tech%20Blog-blue)](https://techblog.smc.it)
[![Twitter Follow](https://img.shields.io/twitter/follow/SMCpartner.svg?style=social&label=%40SMCpartner%20on%20Twitter&style=plastic)](https://twitter.com/SMCpartner)

This project was carried out on the occasion of [Liferay BootCamp 2022](https://liferaybootcamp.smc.it/)
organized by [SMC Treviso](https://www.smc.it/). The speech to which it refers
è: ** Liferay Authentication: How to create a Token-based SSO system **.

The project is organized as [Liferay Workspace](https://help.liferay.com/hc/en-us/articles/360018164651-Liferay-Workspace)
and the reference Liferay version is [7.4.3.19 CE GA19](https://github.com/liferay/liferay-portal/releases/tag/7.4.3.19-ga19).

The project consists of two OSGi modules which are inside the directory
[modules/security](modules/security).

1. CIE-CNS Auto Login Token API (it.smc.labs.bootcamp.liferay.security.auto.login.token.api)
2. CIE-CNS Auto Login Token API Implementation (it.smc.labs.bootcamp.liferay.security.auto.login.token.impl)

The two modules implement the necessary components to make it possible
the solution shown in the diagram below.

![](docs/architecture/images/portal_access_scenario_2.png)

Figure 1 - Architecture of the Liferay portal access solution via Smart Card

The architectural element external to Liferay, the **Reverse Proxy and/or IDP** is
fundamentally responsible for identifying the user who requests to
access the Liferay portals via Smart Card (in this case TS-CNS and CIE) e
to switch to Liferay, after successful identification, on the header
http the user's token.

The value of the token received on the http header (whose name can be configured)
corresponds to the user's tax code, both if the latter has
logged in with the TS-CNS and if logged in with the CIE

The Auto Login module will then be responsible for authentication on the portal
Liferay and the possible import of the user from an external source.

For the demo of the solution implemented with this project, for the component
architectural Reverse Proxy (IDP), the project was used
[Apache HTTP 2.4 for TS-CNS Smart Card (Health Card - National Services Card) and CIE (Electronic Identity Card)](https://github.com/italia/cie-cns-apache-docker/)
by Developer Italia suitably extended and available on this [cie-cns-apache-docker-extended-for-liferay-bootcamp-2022](https://github.com/amusarra/cie-cns-apache-docker-extended-for -liferay-bootcamp-2022)

## 1. Portal Configuration
The basic configuration of the Liferay portal is available on the configuration file
portal-ext.properties inside the directory [configs/docker/portal-ext.properties](configs/docker/portal-ext.properties)

To comply with the solution shown in figure 1, two must be set
Portal instances for the cns.portal.local and cie.portal.local virtual hosts.
The virtual host admin.portal.local is the default one configured on the file
configuration previously indicated and created during the start-up phase of the
portal (on first start).

![](docs/liferay/images/virtual_instance_configuration.png)

Figura 2 - Configuration of the Virtual Instances

You can see the complete configuration of the Liferay Portal on [docker](configs/docker)
and [portal-ext.properties](configs/docker/portal-ext.properties).

## 2. Configuring the Token-Based SSO module
The Token SSO module expects a series of configuration parameters which are
fully described in the table below.



| Name                     | Description                                                  |
| ------------------------ | ------------------------------------------------------------ |
| enabled                  | Enable or disable the Token SSO auto login module. The default is **false**.|
| importFromExternalSource | Enable or disable the ability to import users from an external system. At the code level there may therefore be different implementations and the latter can be used by the auto login module. |
| userTokenName            | Defines the name of the Token that will contain the user's username. The default is **X-AUTH-REMOTE-USER**. |
| originHttpHeaderName     | Defines the name of the HTTP header that will contain the source of the request. The default is **X-AUTH-ORIGIN**. |
| originHttpHeaderValues   | It defines the values that are allowed by the HTTP header defined by the **originHttpHeaderName** configuration. If values other than those configured were received, access would not be allowed. The default values are: **RP_IDP_TEST_CIE** and **RP_IDP_TEST_CNS** which in this case respectively indicate the Reverse Proxy/IDP dedicated to the CNS and Reverse Proxy/IDP dedicated to the CIE.|
| whitelist                | It defines a list of IP addresses (of the Reverse Proxy) that are allowed by the auto login module, therefore, if the access request comes from a Reverse Proxy or machine whose IP address is not registered, this will be denied.|
| xForwarded               | Enable or disable the use of forward HTTP headers. The default is **false**. |
| tokenLocation            | Defines where to find the token of the user's username. The default is **REQUEST_HEADER**. |

Table 1 - Configuration parameters of the Auto Login module defined on the interface [CieCnsTokenAutoLoginConfiguration](modules/security/token-header-auto-login/token-header-auto-login-api/src/main/java/it/smc/labs/bootcamp/liferay/security/auto/login/token/configuration/CieCnsTokenAutoLoginConfiguration.java).

Remember that the values for the **X-AUTH-REMOTE-USER** and **X-AUTH-ORIGIN** HTTP headers
they are set by the Reverse Proxy/IDP, which is responsible for the identification process
via Smart Card. For more information, refer to
project [amusarra/cie-cns-apache-docker-extended-for-liferay-bootcamp-2022](https://github.com/amusarra/cie-cns-apache-docker-extended-for-liferay-bootcamp-2022)

## 3. Scenario in action
So that you can try the scenario proposed and described by this project,
within this there is a [Docker Compose](docker-compose.yml) al
within which the following main services are defined:

1. **Liferay**: Liferay portal instance version 7.4.19 GA19
2. **PostgreSQL**: PostgreSQL 12.8 database instance hosting the Liferay portal database
3. **CNS Proxy**: proxy dedicated to the portal which can be accessed via the TS-CNS
4. **CIE Proxy**: proxy dedicated to the portal which can be accessed through the CIE
5. **Proxy Admin**: proxy dedicated to the administration of the Liferay portal instance

The CNS proxy is configured to respond to the `cns.portal.local` FQDN and header
HTTP `X-AUTH-ORIGIN` is set to `RP_IDP_TEST_CNS`.

The CIE proxy is configured to respond to the `cie.portal.local` FQDN and header
HTTP `X-AUTH-ORIGIN` is set to `RP_IDP_TEST_CIE`.

The Admin proxy is configured to respond to the `admin.portal.local` FQDN and header
HTTP `X-AUTH-ORIGIN` is set to `RP_IDP_TEST_ADMIN`. This service is
configured to make authentication via Smart Card optional (see
environment `APACHE_SSL_VERIFY_CLIENT=optional`).

The accesses for each portal are therefore:
1. Access to the CNS portal - https: //cns.portal.local: 8443
2. Access to the CIE portal - https: //cie.portal.local: 9443
3. Access to the Admin Portal - https: //admin.portal.local: 7443

Below is the detail of the proxy services configuration extracted from Docker
Compose file.

```yaml
  proxy-cns:
      image: amusarra/cie-cns-apache-docker-extended-for-liferay-bootcamp-2022:2.2.0-rp-liferay
      environment:
        - APACHE_SERVER_NAME=cns.portal.local
        - APPLICATION_BACKEND_BASE_URL=http://liferay:8080/
        - HEADER_X_AUTH_ORIGIN=RP_IDP_TEST_CNS
        - APACHE_SSL_CERTS=cns.portal.local_crt.pem
        - APACHE_SSL_PRIVATE=cns.portal.local_key.pem
      ports:
        - 8443:10443
  proxy-cie:
    image: amusarra/cie-cns-apache-docker-extended-for-liferay-bootcamp-2022:2.2.0-rp-liferay
    environment:
      - APACHE_SERVER_NAME=cie.portal.local
      - APPLICATION_BACKEND_BASE_URL=http://liferay:8080/
      - HEADER_X_AUTH_ORIGIN=RP_IDP_TEST_CIE
      - APACHE_SSL_CERTS=cie.portal.local_crt.pem
      - APACHE_SSL_PRIVATE=cie.portal.local_key.pem
    ports:
      - 9443:10443
  proxy-cie-cns-admin:
    image: amusarra/cie-cns-apache-docker-extended-for-liferay-bootcamp-2022:2.2.0-rp-liferay
    environment:
      - APACHE_SERVER_NAME=admin.portal.local
      - APPLICATION_BACKEND_BASE_URL=http://liferay:8080/
      - HEADER_X_AUTH_ORIGIN=RP_IDP_TEST_ADMIN
      - APACHE_SSL_VERIFY_CLIENT=optional
      - APACHE_SSL_CERTS=admin.portal.local_crt.pem
      - APACHE_SSL_PRIVATE=admin.portal.local_key.pem
    ports:
      - 7443:10443
```
Source 1 - Extract from the configuration of the Proxy Server services

### 3.1 Requirements
To run the entire service stack on your development machines, it is
your hardware must not be obsolete; is strongly recommended
have an Intel processor at least Core i7 dual-core or AMD series
Ryzen 5 or Apple M1 processor.

As for the RAM memory it is strongly recommended to have at least
16 GByte.

There are no restrictions on the operating system. The execution of this scenario is
been tested on: macOS Monterey 12.3.1.

As for Docker, this scenario was tested with version 4.8.0
of [Docker Desktop](https://www.docker.com/products/docker-desktop/) for macOS consisting of the following components:
1. Docker Engine 20.10.14
2. Compose 2.5.0
3. Kubernetes 1.24.0
4. Snyk 1.827.0

In order for you to access the portals via TS-CNS or CIE, I remember that you have to
have the appropriate Smart Card reader and your browser must be
appropriately configured. The recommended readers are those produced by Bit4Id.

1. [miniLector CIE](https://shop.bit4id.com/prodotto/minilector-cie-lettore-cie-offerta/?gclid=CjwKCAjw9-KTBhBcEiwAr19ig_Q9ojOjX9TBA2w09MXQAlLmRy9t3QI3ZFteTijTn6PzkrizZA1_uhoCoXgQAvD_BwE)
2. [miniLector EVO](https://shop.bit4id.com/prodotto/minilector-evo/)

### 3.2 Execution of the scenario via Docker Compose
The execution of the scenario consists of making the entire stack of services go up
defined on the Docker Compose file. The steps to be performed are therefore:

1. Clone of the Repository
2. Start of services
3. Verify that the services are operating properly
4. Deploy the application modules on Liferay

```bash
$ git clone https://github.com/smclab/cie-cns-auth-token-sso.git
$ cd cie-cns-auth-token-sso
```
Console 1 - Clone of the project repository

```bash
# Start all service
$ docker-compose up -d

# Get the service log to monitoring start-up
$ docker-compose logs -f

# Check Services up
$ docker-compose ps
```
Console 2 - Start of the stack of services defined on the Docker Compose

```bash
# Deploy using the Gradle Wrapper
$ ./gradlew clean deploy

# Deploy using the blade tool
$ blade gw deploy
```
Console 3 - Deploy bundle CIE/CNS Token SSO module

In order to reach the portals via FQDN from your local machine, you should
add entries to your hosts file (in the case of unix-like systems) the
file is /etc/hosts.

```bash
##
# Host per demo Liferay BootCamp 2022
##
127.0.0.1   cie.portal.local
127.0.0.1   cns.portal.local
127.0.0.1   admin.portal.local
```
Console 4 - Added host for Liferay Virtual Hosts