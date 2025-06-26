# LREAS
node version 22

### Install pm2
```
npm install -g serve
npm install -g pm2
```

### Start Client App
```
cd ./Client/lreas
cp .env.example .env
nvm use 22
npm install
npm run build && pm2 serve build/ 3000 --name "lreas-app" --spa 
```

### Start Kong API Gateway
```
#make sure that mirgration container has done running and kong container starts successfully
docker-compose up -d 

#Back up Kong's database for configurations
#Chạy lệnh này để lưu lại những config về service, route,... để có thể chạy trên máy khác
docker exec -t lreas-database pg_dump -U lreas -d kong > ./Server/database/backup/lreas_backup.sql

#Restore Kong's database
#Bình thường postgres sẽ tự động restore, nhưng nếu nó không tự động thì mới chạy lệnh dưới
docker exec -i lreas-database psql -U lreas -d kong < ./Server/database/backup/lreas_backup.sql
```

### Backup LREAS data
```
#Back up LREAS's database
#Chạy lệnh này để lưu lại data
docker exec -t lreas-database pg_dump -U lreas -d lreas > ./Server/database/backup/lreas_data_backup.sql

#Restore Kong's database
#Bình thường postgres sẽ tự động restore, nhưng nếu nó không tự động thì mới chạy lệnh dưới
docker exec -i lreas-database psql -U lreas -d lreas < ./Server/database/backup/lreas_data_backup.sql
```

### Start Server
1. Run docker containers:
```
docker-compose up -d
```
2. Import module project folder into IntelliJ IDEA (eg: Server/authentication)
3. Install JDK 20.0.1
4. Begin coding
