# CI pipeline für price-range-service

## Dependencies

* leiningen installieren (https://leiningen.org)
* der CI Server verwendet MongoDB für die Persistenz der Buildhistory
* zum lokalen Testen kann MongoDB in Docker gestartet werden: `docker run --name some-mongo -p 27017:27017 -d mongo`

## Pipeline lokal starten

* `lein run` startet den CI Server lokal an port 8090

## CCTray support (buildnotify under linux)

http://localhost:8090/cctray/pipeline.xml

## CI Server

### Dependencies

* CI Server provisionieren (Verzeichnis ci)

### CI Server aktualisieren

`./upgrade-pipeline.sh`

im Detail:

* `lein uberjar` baut das CI Server JAR
* mittels Ansible den CI Server aktualsieren:
	`ansible-playbook -i ansible-host.txt deploy-lambdacd.yml`

### Log output von LambdaCD

* `sudo journalctl -u lambdacd`
