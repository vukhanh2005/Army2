# MobiArmy2 Web Forum

Web forum dung chung database `army` voi game server.

## Chay nhanh

1. Import database web:
   ```sql
   SOURCE MobiArmy2-Web/database.sql;
   ```
2. Cau hinh database bang bien moi truong neu can:
   - `MOBIARMY_DB_HOST`
   - `MOBIARMY_DB_NAME`
   - `MOBIARMY_DB_USER`
   - `MOBIARMY_DB_PASSWORD`
3. Chay local:
   ```bash
   php -S 127.0.0.1:8080 -t MobiArmy2-Web
   ```

## API cho game server

Endpoint:

```text
POST /api/register.php
api_secret=NguyenVuKhanhEni
username=<account>
password=<password>
```

Response:

```json
{"success":true,"message":"Đăng ký thành công.","user_id":123}
```

Server co the cau hinh:

```properties
web.apiBaseUrl=http://127.0.0.1:8080/api
web.apiSecret=NguyenVuKhanhEni
```
