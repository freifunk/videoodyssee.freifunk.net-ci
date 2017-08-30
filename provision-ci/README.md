# Basiskonfiguration unseres CI Servers

installiert:
* JDK
* MongoDB


## benötigte Ansible Galaxy Rollen

sudo ansible-galaxy install manala.mongo-express
sudo ansible-galaxy install manala.apt
sudo ansible-galaxy install geerlingguy.nodejs

## Provisionieren des CI Server

* Dry Run: `ansible-playbook playbook.yml -i ansible-hosts.txt --check`
* Provision: `ansible-playbook playbook.yml --user <username> --ask-sudo-pass -i ansible-hosts.txt`

## MongoDB UI

* MongoDB Express UI: http://localhost:8081
