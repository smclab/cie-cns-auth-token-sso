# CIE/CNS Liferay Authentication Token-based SSO

[![SMC Tech Blog](https://img.shields.io/badge/Mainteiner-SMC%20Tech%20Blog-blue)](https://techblog.smc.it) 
[![Twitter Follow](https://img.shields.io/twitter/follow/SMCpartner.svg?style=social&label=%40SMCpartner%20on%20Twitter&style=plastic)](https://twitter.com/SMCpartner) 

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

Per la demo della soluzione implemntata con questo progetto, per il componente
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

## 2. Configurazione del modulo Token-Based SSO
TODO: Da completare

## 3. Scenario in action
TODO: Da completare