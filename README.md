# ChargeSquare

# ChargeSquare - API Gateway ve Redis Konfigürasyonu

Bu yazıda, **API Gateway** servisini ve **Redis** yapılandırmasını Docker üzerinde nasıl çalıştıracağınızı, ayrıca **Spring Boot** ile Redis'i nasıl entegre edebileceğinizi adım adım anlatacağız.

## Docker Üzerinde Redis Single Node Oluşturma

Redis'i Docker üzerinde çalıştırmak için aşağıdaki komutları kullanabilirsiniz:

### 1. Redis Container'ını Çalıştırma

Redis'in tekli bir node olarak çalışması için aşağıdaki Docker komutunu kullanabilirsiniz:

```bash
docker run --name microservice-redis -p 6379:6379 -d redis

```
Bu komut, Redis'i arka planda çalıştırır ve port 6379 üzerinden erişilebilir hale getirir.

### 2. RedisInsight UI (Redis GUI) Kurulumu

Redis'i görsel olarak yönetmek için RedisInsight GUI'yi çalıştırabilirsiniz:

```bash
docker run --name redis-gui -d -p 8001:8001 -d redislabs/redisinsight:1.14.0
```
Bu komut, RedisInsight'i başlatacak ve port 8001 üzerinden erişilebilir hale getirecektir.
 
## Redis Spring Boot Configuration
    İlgili bağımlılıklar eklenir.

    DİKKAT!!!
    Redis repository olarak kullanılabileceği gibi, Cache olarakta kullanılabilir, Bu nedenle Spring te Cache i
    ve Redis Repository aktif etmek için gerekli anatasyonları config e eklememiz uygun olacaktır.

## Spring Cloud Gateway Yapılandırması
   Spring Cloud Gateway, mikro servislere gelen istekleri yönlendirmek için kullanılan 
   güçlü bir çözüm sunar. Bu kısımda, gelen isteklerin hangi mikro servislere
   yönlendirileceğini belirleyen "routes" (yönlendirmeler) yapılandırması yer alıyor.

### `server.port: 80`
**Port Ayarı**: Spring Boot uygulamanızın çalışacağı portu belirtir. 
Bu durumda, API Gateway servisi **port 80**'de çalışacak.

- Path=/stations/**  Bu yönlendirme, /stations/** yolundaki tüm istekleri http://localhost:9090 adresine yönlendirir.
- Path=/dev/v1/stations/get-all-stations Bu yönlendirme, /dev/v1/stations/get-all-stations yolundaki istekleri yine http://localhost:9090 adresine yönlendirir.
- Path=/dev/v1/stations/get-station-by-id/{stationId} Bu yönlendirme, /dev/v1/stations/get-station-by-id/{stationId} yolundaki istekleri http://localhost:9090 
 adresine yönlendirir. Ayrıca, stationId parametresini URL yolunda veya query parametresi olarak alır.
- Path=/dev/v1/stations/create-stations     Bu yönlendirme, /dev/v1/stations/create-stations yolundaki istekleri http://localhost:9090 adresine yönlendirir.
- Path=/dev/v1/locations/get-all-locations  Bu yönlendirme, /dev/v1/locations/get-all-locations yolundaki istekleri yine http://localhost:9090 adresine yönlendirir.

# ChargeSquare - PostgreSql Yapılandırması
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://localhost:5432/charging_stationsDB
    username: postgres
    password: 1234
# KAFKA Yapılandırması
Konteynerleri Başlatın : Terminalden dosyayı kaydettiğiniz dizine gidin ve şunu çalıştırın:
```bash
  docker-compose up -d
```
Hizmetleri Kontrol Edin : Hizmetlerin çalışıp çalışmadığını şu şekilde doğrulayabilirsiniz:
```bash
 docker-compose ps
```





