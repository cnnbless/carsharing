# Carsharing Microservices — Helm Deployment

## Управління версіями (Chart.yaml)
* **version**: Версія самого Helm-чарта (інфраструктурного коду). Інкрементується при зміні структури шаблонів, додаванні нових маніфестів Kubernetes або модифікації логіки в `_helpers.tpl`.
* **appVersion**: Версія розгорнутого застосунку. Відповідає тегу Docker-образу мікросервісів. Змінюється при випуску нового релізу програмного коду (Spring Boot).

## Параметри конфігурації (values.yaml)

| Параметр | Опис | Значення за замовчуванням |
|---|---|---|
| `config.profile` | Активний профіль Spring Boot | `"prod"` |
| `config.defaultRole` | Роль користувача за замовчуванням | `"CUSTOMER"` |
| `config.logLevel` | Рівень логування системи | `"INFO"` |
| `ingress.enabled` | Увімкнення Ingress-контролера для маршрутизації | `true` |
| `postgres.persistence.enabled` | Увімкнення PersistentVolumeClaim для БД | `true` |
| `postgres.persistence.size` | Об'єм дискового простору для СУБД | `1Gi` |
| `userService.replicas` | Кількість реплік пода user-service | `2` |
| `userService.image.tag` | Версія Docker-образу user-service | `"1.0.0"` |
| `fleetService.replicas` | Кількість реплік пода fleet-service | `2` |
| `orderService.replicas` | Кількість реплік пода order-service | `2` |

## Команди встановлення

### Середовище розробки (dev)
Розгортання з 1 реплікою для кожного сервісу, вимкненим Ingress та без збереження стану бази даних на диск:
```bash
kubectl create namespace carsharing
helm install dev-release ./helm/carsharing -n carsharing -f ./helm/carsharing/values-dev.yaml
```
### Продуктове середовище (prod)
Розгортання з 3 репліками для кожного сервісу, увімкненим Ingress (хост prod.carsharing.local) та активованим PVC для бази даних:
```bash
kubectl create namespace carsharing
helm install prod-release ./helm/carsharing -n carsharing -f ./helm/carsharing/values-prod.yaml
```