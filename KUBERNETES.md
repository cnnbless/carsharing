# Carsharing Service — Розгортання в Kubernetes

Цей репозиторій містить набір декларативних маніфестів для розгортання та керування життєвим циклом системи каршерінгу в локальному кластері Kubernetes (Minikube).

---

## Архітектура розгортання

Система розгортається у виділеному просторі імен `carsharing` і складається з таких компонентів:
* **user-service**: бекенд-сервіс керування користувачами (Spring Boot, внутрішній порт `8082`, 2 репліки).
* **fleet-service**: бекенд-сервіс автопарку (Spring Boot, внутрішній порт `8081`, 2 репліки).
* **order-service**: бекенд-сервіс замовлень (Spring Boot, внутрішній порт `8083`, 2 репліки).
* **postgres**: СУБД PostgreSQL зі збереженням стану через PersistentVolumeClaim (PVC).
* **Ingress**: точка входу NGINX Ingress Controller для маршрутизації HTTP-трафіку за шляхами.

---

## Передумови

Перед початком розгортання переконайтеся, що встановлено:
* [Docker Desktop](https://www.docker.com/products/docker-desktop/)
* [Minikube](https://minikube.sigs.k8s.io/docs/start/)
* [kubectl](https://kubernetes.io/docs/tasks/tools/)

---

## Порядок запуску кластера та розгортання

### 1. Запуск Minikube та увімкнення розширень
Запустіть локальний кластер із драйвером Docker та активуйте необхідні аддони:

```bash
minikube start --driver=docker
minikube addons enable ingress
minikube addons enable metrics-server

2. Налаштування локального DNSДодайте запис для домену carsharing.local у файл операційної системи:Windows: C:\Windows\System32\drivers\etc\hostsLinux / macOS: /etc/hostsPlaintext127.0.0.1 carsharing.local

3. Запуск мережевого тунелю (окремий термінал)Для маршрутизації трафіку на Ingress у середовищі Minikube на Windows запустіть тунель у терміналі від імені Адміністратора:Bashminikube tunnel
Залиште цей процес активним під час роботи з кластером.4. Збірка та завантаження Docker-образівЗберіть образи мікросервісів та імпортуйте їх у локальний реєстр Minikube:Bash# Збірка образів

docker build -t carsharing/user-service:1.0.0 ./user-service
docker build -t carsharing/user-service:1.1.0 ./user-service
docker build -t carsharing/fleet-service:1.0.0 ./fleet-service
docker build -t carsharing/order-service:1.0.0 ./order-service

# Завантаження в Minikube
minikube image load carsharing/user-service:1.0.0
minikube image load carsharing/user-service:1.1.0
minikube image load carsharing/fleet-service:1.0.0
minikube image load carsharing/order-service:1.0.0


5. Застосування маніфестів

Розгорніть усі обєкти кластера з каталогу k8s/:Bashkubectl apply -f k8s/
Перевірте статус запуску всіх компонентів:Bashkubectl get pods,svc,pvc,ingress -n carsharing
Усі поди повинні перебувати у статусі Running і мати стан готовності 1/1 Ready.Опис конфігураціїConfigMap (user-service-config)Зберігає нечутливі змінні конфігурації застосунку:ЗміннаЗначення за замовчуваннямОписSPRING_PROFILES_ACTIVEprodАктивний профіль конфігурації Spring BootSPRING_DATASOURCE_URLjdbc:postgresql://postgres-service:5432/user_dbJDBC URL для підключення до бази данихSPRING_DATASOURCE_USERNAMEpostgresКористувач СУБД PostgreSQLAPP_DEFAULT_ROLECUSTOMERРоль нового користувача за замовчуваннямLOGGING_LEVEL_ROOTINFOБазовий рівень логування системиSecret (user-service-secret)Зберігає конфіденційні параметри у форматі Opaque:КлючПризначенняDB_PASSWORDПароль суперкористувача СУБД PostgreSQLSPRING_DATASOURCE_PASSWORDПароль підключення до бази даних для сервісівJWT_SECRET_KEYСекретний ключ для підпису та валідації JWT-токенівМаршрутизація API (Ingress)Зовнішній доступ забезпечується через хост http://carsharing.local за такими префіксами:ШляхСервіс призначенняПортПризначення/api/usersuser-service8082Керування обліковими записами та водійськими правами/api/carsfleet-service8081Перегляд та керування доступним автопарком/api/ordersorder-service8083Створення та облік замовлень на оренду автоПеревірка працездатностіВиконання тестових HTTP-запитів до API через термінал:PowerShell# Створення користувача (POST)
Invoke-RestMethod -Uri "[http://127.0.0.1/api/users](http://127.0.0.1/api/users)" -Method Post -Headers @{ Host = "carsharing.local" } -ContentType "application/json" -Body '{"name":"Anton","driverLicenseNumber":"AB123456","isLicenseValid":true}'

# Отримання даних користувача за UUID (GET)
Invoke-RestMethod -Uri "[http://127.0.0.1/api/users/](http://127.0.0.1/api/users/)<USER_UUID>" -Method Get -Headers @{ Host = "carsharing.local" }

# Перевірка доступності автопарку
curl.exe -i --resolve carsharing.local:80:127.0.0.1 [http://carsharing.local/api/cars](http://carsharing.local/api/cars)

# Перевірка доступності замовлень
curl.exe -i --resolve carsharing.local:80:127.0.0.1 [http://carsharing.local/api/orders](http://carsharing.local/api/orders)