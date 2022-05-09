# CIE/CNS Liferay Authentication Token-based SSO

[![SMC Tech Blog](https://img.shields.io/badge/Mainteiner-SMC%20Tech%20Blog-blue)](https://techblog.smc.it) 
[![Twitter Follow](https://img.shields.io/twitter/follow/SMCpartner.svg?style=social&label=%40SMCpartner%20on%20Twitter&style=plastic)](https://twitter.com/SMCpartner) 

[Versione inglese del README](README_EN.markdown)

Questo progetto è stato realizzato in occasione del [Liferay BootCamp 2022](https://liferaybootcamp.smc.it/)
organizzato da [SMC Treviso](https://www.smc.it/). Lo speech a cui fa riferimento
è: **Liferay Authentication: How to create a Token-based SSO system**.

Il progetto è organizzato come [Liferay Workspace](https://help.liferay.com/hc/en-us/articles/360018164651-Liferay-Workspace) 
e la versione Liferay di riferimento è la [7.4.3.19 CE GA19](https://github.com/liferay/liferay-portal/releases/tag/7.4.3.19-ga19).

Il progetto è costituito da due moduli OSGi che sono all'interno della directory
[modules/security](modules/security).

1. CIE-CNS Auto Login Token API (it.smc.labs.bootcamp.liferay.security.auto.login.token.api)
2. CIE-CNS Auto Login Token API Implementation (it.smc.labs.bootcamp.liferay.security.auto.login.token.impl)

I due moduli implementano i componenti necessari affinché sia possibile realizzare
la soluzione mostrata dal diagramma a seguire.

![](docs/architecture/images/portal_access_scenario_2.png)

Figura 1 - Architettura della soluzione di accesso al portale Liferay tramite Smart Card

L'elemento architetturale esterno a Liferay, il **Reverse Proxy e/o IDP** è 
fondamentalmente responsabile dell'identificazione dell'utente che richiede di
accedere ai portali Liferay tramite Smart Card (in questo caso TS-CNS e CIE) e 
di passare a Liferay, dopo l'identificazione avvenuta con successo, sull'header 
http il token dell'utente.

Il valore del token ricevuto sull'header http (il cui nome può essere configurato)
corrisponde al codice fiscale dell'utente, sia nel caso quest'ultimo abbia 
eseguito l'accesso con la TS-CNS sia nel caso abbia eseguito l'accesso con la 
CIE.

Il modulo di Auto Login sarà responsabile quindi dell'autenticazione sul portale
Liferay e dell'eventuale importazione dell'utente da una fonte esterna.

Per la demo della soluzione implementata con questo progetto, per il componente
architetturale Reverse Proxy (IDP), è stato utilizzato il progetto 
[Apache HTTP 2.4 per Smart Card TS-CNS (Tessera Sanitaria - Carta Nazionale Servizi) e CIE (Carta d'Identità Elettronica)](https://github.com/italia/cie-cns-apache-docker/) 
di Developer Italia opportunamente esteso e disponibile su questo [cie-cns-apache-docker-extended-for-liferay-bootcamp-2022](https://github.com/amusarra/cie-cns-apache-docker-extended-for-liferay-bootcamp-2022)

## 1. Configurazione del portale
La configurazione base del portale Liferay è disponibile sul file di configurazione
portal-ext.properties all'interno della directory [configs/docker/portal-ext.properties](configs/docker/portal-ext.properties)

Per essere conformi alla soluzione mostrata in figura 1, occorre impostare due 
istanze di portale per gli virtual host cns.portal.local e cie.portal.local. 
Il virtual host admin.portal.local è quello di default configurato sul file
di configurazione indicato in precedenza e creato in fase di start-up del 
portale (al primo avvio).

![](docs/liferay/images/virtual_instance_configuration.png)

Figura 2 - Configurazione Virtual Instances

La configurazione completa del portale è possibile vederla all'interno della
directory del profilo [docker](configs/docker) e in particolare sul file
[portal-ext.properties](configs/docker/portal-ext.properties).

## 2. Configurazione del modulo Token-Based SSO
Il modulo Token SSO prevede una serie di parametri di configurazione che sono
ampiamente descritti dalla tabella a seguire.



| Name                     | Description                                                  |
| ------------------------ | ------------------------------------------------------------ |
| enabled                  | Abilita o disabilita il modulo di auto login Token SSO. Il valore di default è **false**. |
| importFromExternalSource | Abilita o disabilita la possibilità d'importare gli utenti da un sistema esterno. A livello di codice potranno esserci quindi diverse implemetazioni e quest'ultime possono essere usate dal modulo di auto login. |
| userTokenName            | Definisce il nome del Token che conterrà la username dell'utente. Il valore di default è **X-AUTH-REMOTE-USER**. |
| originHttpHeaderName     | Definisce il nome dell'HTTP header che conterrà l'origine della richiesta. Il valore di default è **X-AUTH-ORIGIN**. |
| originHttpHeaderValues   | Definisce i valori che sono ammessi dall'header HTTP definito dalla configurazione **originHttpHeaderName**. Nel caso venissero ricevuti valori diversi da quelli configurati, l'accesso non sarebbe consentito. I valori di default sono: **RP_IDP_TEST_CIE** e **RP_IDP_TEST_CNS** che in questo caso rispettivamente indicano, il Reverse Proxy/IDP dedicato alla CNS e Reverse Proxy/IDP dedicato alla CIE. |
| whitelist                | Definisce la una lista d'indirizzi IP (del Reverse Proxy) che sono ammessi dal modulo di auto login, quindi, se la richiesta di accesso provenisse da un Reverse Proxy o macchina il cui indirizzo IP non è censito, questa sarabbe negata. |
| xForwarded               | Abilita o disabilita l'uso degli HTTP header di forward. Il valore di default è **false**. |
| tokenLocation            | Definisce dove reperire il Token dell'username dell'utente. Il valore di default è **REQUEST_HEADER**. |

Tabella 1 - Parametri di configurazione del modulo di Auto Login definiti sull'interfaccia [CieCnsTokenAutoLoginConfiguration](modules/security/token-header-auto-login/token-header-auto-login-api/src/main/java/it/smc/labs/bootcamp/liferay/security/auto/login/token/configuration/CieCnsTokenAutoLoginConfiguration.java).

Ricordo che i valori per gli header HTTP **X-AUTH-REMOTE-USER** e **X-AUTH-ORIGIN** 
sono impostati dal Reverse Proxy/IDP, responsabile del processo d'identificazione
tramite Smart Card. Per maggiori informazioni a riguardo fare riferimento al 
progetto [amusarra/cie-cns-apache-docker-extended-for-liferay-bootcamp-2022](https://github.com/amusarra/cie-cns-apache-docker-extended-for-liferay-bootcamp-2022)

## 3. Scenario in action
Affinché possiate provare lo scenario proposto e descritto da questo progetto,
all'interno di questo è disponibile un [Docker Compose](docker-compose.yml) al 
cui interno sono definiti i seguenti principali servizi:

1. **Liferay**: istanza del portale Liferay versione 7.4.19 GA19
2. **PostgreSQL**: istanza del database PostgreSQL 12.8 che ospita il database del portale Liferay
3. **Proxy CNS**: proxy dedicato al portale il cui accesso è possibile tramite la TS-CNS
4. **Proxy CIE**: proxy dedicato al portale il cui accesso è possibile tramite la CIE
5. **Proxy Admin**: proxy dedicato all'amministrazione dell'istanza di portale Liferay

Il proxy CNS è configurato per rispondere all'FQDN `cns.portal.local` e l'header
HTTP `X-AUTH-ORIGIN` è impostato a `RP_IDP_TEST_CNS`.

Il proxy CIE è configurato per rispondere all'FQDN `cie.portal.local` e l'header
HTTP `X-AUTH-ORIGIN` è impostato a `RP_IDP_TEST_CIE`.

Il proxy Admin è configurato per rispondere all'FQDN `admin.portal.local` e l'header
HTTP `X-AUTH-ORIGIN` è impostato a `RP_IDP_TEST_ADMIN`. Questo servizio è 
configurato per rendere l'autenticazione via Smart Card opzionale (vedi
environment `APACHE_SSL_VERIFY_CLIENT=optional`).

Gli accessi per ogni portale sono quindi:
1. Accesso al portale CNS - https://cns.portal.local:8443
2. Accesso al portale CIE - https://cie.portal.local:9443
3. Accesso al portale Admin - https://admin.portal.local:7443

A seguire il dettaglio della configurazione dei servizi proxy estratta dal Docker
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
Source 1 - Estratto della configurazione dei servizi Proxy Server

### 3.1 Requisiti
Per eseguire l'intero stack dei servizi sulle vostre macchine di sviluppo, è 
necessario che il vostro hardware non sia obsoleto; è fortemente consigliato 
disporre di un processore Intel almeno Core i7 dual-core o AMD della serie 
Ryzen 5 o per finire processore Apple M1. 

Per quanto riguarda la memoria RAM è fortemente consigliato disporre di almeno
16 GByte.

Non ci sono vincoli sul sistema operativo. L'esecuzione di questo scenario è 
stata testata su: macOS Monterey 12.3.1.

Per quanto riguarda Docker, questo scenario è stato testato con la versione 4.8.0
di [Docker Desktop](https://www.docker.com/products/docker-desktop/) per macOS costituito dai seguenti componenti:
1. Docker Engine 20.10.14
2. Compose 2.5.0
3. Kubernetes 1.24.0
4. Snyk 1.827.0

Affinché possiate accedere ai portali via TS-CNS o CIE, ricordo che dovete 
disporre dell'apposito lettore di Smart Card e il vostro browser deve essere 
opportunamente configurato. Il lettori consigliati sono quelli prodotti da Bit4Id
e sono:

1. [miniLector CIE](https://shop.bit4id.com/prodotto/minilector-cie-lettore-cie-offerta/?gclid=CjwKCAjw9-KTBhBcEiwAr19ig_Q9ojOjX9TBA2w09MXQAlLmRy9t3QI3ZFteTijTn6PzkrizZA1_uhoCoXgQAvD_BwE)
2. [miniLector EVO](https://shop.bit4id.com/prodotto/minilector-evo/)

### 3.2 Esecuzione dello scenario via Docker Compose
L'esecuzione dello scenario consiste nel fare salire tutto lo stack dei servizi
definiti sul Docker Compose file. Gli step da eseguire sono quindi:

1. Clone del Repository
2. Start dei servizi
3. Verifica che i servizi siano correttamente operativi
4. Deploy dei moduli applicativi su Liferay

```bash
$ git clone https://github.com/smclab/cie-cns-auth-token-sso.git
$ cd cie-cns-auth-token-sso
```
Console 1 - Clone del repository del progetto

```bash
# Start all service
$ docker-compose up -d

# Get the service log to monitoring start-up
$ docker-compose logs -f

# Check Services up
$ docker-compose ps
```
Console 2 - Start dello stack dei servizi definiti sul Docker Compose

```bash
# Deploy using the Gradle Wrapper
$ ./gradlew clean deploy

# Deploy using the blade tool
$ blade gw deploy
```
Console 3 - Deploy bundle CIE/CNS Token SSO module

Per poter raggiungere i portali via FQDN dalla vostra macchina locale, dovreste
aggiungere le entry sul vostro file di hosts (nel caso di sistemi unix-like) il 
file è /etc/hosts.

```bash
##
# Host per demo Liferay BootCamp 2022
##
127.0.0.1   cie.portal.local
127.0.0.1   cns.portal.local
127.0.0.1   admin.portal.local
```
Console 4 - Aggiunta host per i Virtual Host Liferay 