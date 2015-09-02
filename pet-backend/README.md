# Ruby on Rails backend

## RVM with Ruby and Rails
```bash
gpg --keyserver hkp://keys.gnupg.net --recv-keys 409B6B1796C275462A1703113804BB82D39DC0E3
\curl -sSL https://get.rvm.io | bash -s stable --rails
/bin/bash --login
rvm use 2.2.1
gem install rubygems-bundler
gem regenerate_binstubs
gem install bundler
```

## Databases
```bash
sudo apt-get install postgresql-9.3 libpq-dev mysql-server
```
### Creating user with password

Open the file pg_hba.conf for ubuntu it will be in /etc/postgresql/9.x/main and change the this line:
```text
local   all             postgres                                peer
```
to
```
local   all             postgres                                trust
```
Restart the server
```bash
sudo service postgresql restart
```
Login into psql and set your passowrd
```postgres
psql -U postgres
```
```postgres
ALTER USER postgres with password 'password';
```

Finally change the ```pg_hba.conf``` from
```text
local   all             postgres                                trust
```
to
```text
local   all             postgres                                md5
```
After restarting the postgresql server, set the postgresql password in config/database.yml

## Heroku toolbelt
```bash
wget -O- https://toolbelt.heroku.com/install-ubuntu.sh | sh
heroku login
```

## Creating Rails app (already created)
```bash
rails new <app-name> --database=postgresql
```
### Setup the database
Configure the ```database.yml``` file, create database, run migrations and seed the database.

```bash
cp config/database.yml.example config/database.yml
bin/rake db:create
bin/rake db:migrate
bin/rake db:setup
```
### Running the server
```bash
bin/rails server -p 3000
```
### Creating a scaffolded model (Pet model already created, try another one)
```bash
bin/rails generate scaffold Pet type:string:index name:string description:text
bin/rake db:migrate
```