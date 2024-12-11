package com.berkay.kafka.producer;

import com.berkay.kafka.model.ConsumerCreateChargeStation;
import com.berkay.kafka.model.ConsumerLocations;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ChargeSquareProducer {

    private final KafkaTemplate<String, ConsumerCreateChargeStation> kafkaTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    private final String API_URL = "https://api.openchargemap.io/v3/poi/?output=json&countrycode=TR&key=b1f15906-8911-4bb7-abfb-b99cf39b98d2";
    private static final String TOPIC = "create-charging-station";
    private static final String REDIS_KEY_PREFIX = "charging_station:"; // Redis anahtar prefix

    public void sendCreate() {
        // RestTemplate ile API'den veri çek
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(API_URL, String.class);

        // JSON verisini işle
        JSONArray jsonArray = new JSONArray(response);
        int arrayLength = jsonArray.length();

        // Random sınıfını kullanarak rastgele bir başlangıç indeksi belirle
        Random random = new Random();
        int startIndex = random.nextInt(arrayLength); // 0 ile arrayLength-1 arasında rastgele bir değer

        // Döngüyü başlat, rastgele başlangıç indeksinden başlayarak 5 elemanı işleyelim
        int processedCount = 0; // İşlenen veri sayısı
        for (int i = startIndex; processedCount < 5 && i < arrayLength; i++) {
            JSONObject stationData = jsonArray.getJSONObject(i);

            // "ID" değerini alın ve String'e dönüştürün
            Object idObject = stationData.opt("ID"); // Türden emin olmadığımız için "opt" kullanıyoruz
            if (idObject == null) {
                System.out.println("ID alanı mevcut değil. Atlanıyor.");
                continue;
            }
            String stationId = idObject.toString(); // ID'yi String'e dönüştür

            // Redis kontrolü: Aynı ID varsa işlemi atla
            if (redisTemplate.opsForValue().get(REDIS_KEY_PREFIX + stationId) != null) {
                System.out.println("Veri zaten işlendi: " + stationId);
                continue;
            }

            // Yeni veri ise işle ve Redis'e kaydet
            ConsumerCreateChargeStation chargingStation = convertToChargingStationCreateRequestDTO(stationData);
            kafkaTemplate.send(TOPIC, "create", chargingStation);

            // Redis'e kaydet (ID'yi 24 saat boyunca sakla)
            redisTemplate.opsForValue().set(REDIS_KEY_PREFIX + stationId, true, Duration.ofHours(24));
            processedCount++;
        }

    }

    // API'den gelen veriyi DTO'ya dönüştüren metot
    private ConsumerCreateChargeStation convertToChargingStationCreateRequestDTO(JSONObject stationData) {
        // Şarj istasyonunun adı "AddressInfo" içinde yer alıyor
        String name = stationData.getJSONObject("AddressInfo").getString("Title");

        // Şarj hızı "Connections" içinde yer alıyor
        double chargeSpeed = stationData.getJSONArray("Connections")
                .getJSONObject(0)
                .getDouble("PowerKW"); // İlk bağlantının şarj gücü

        // Lokasyon verisini alalım: "AddressInfo" içindeki veriler
        JSONObject addressInfo = stationData.getJSONObject("AddressInfo");

        // AddressLine1 ve AddressLine2'yi alalım, varsa
        String addressLine1 = addressInfo.optString("AddressLine1", ""); // Eğer yoksa boş string döner
        String addressLine2 = addressInfo.optString("AddressLine2", ""); // Eğer yoksa boş string döner

        // İki adres satırını birleştirelim
        String address = addressLine1 + (addressLine2.isEmpty() ? "" : " " + addressLine2);

        // Şehir ve ülke bilgilerini alalım
        String city = addressInfo.optString("Town", ""); // "Town" verisini alalım
        String country = addressInfo.getJSONObject("Country").optString("Title", ""); // "Country" -> "Title" verisini alalım

        // Lokasyon verilerini listeye ekleyelim
        List<ConsumerLocations> locationDtos = new ArrayList<>();
        locationDtos.add(new ConsumerLocations(address, city, country)); // Tek bir lokasyon olduğu varsayılıyor

        // ConsumerCreateChargeStation nesnesi döndürülüyor
        return ConsumerCreateChargeStation.builder()
                .name(name)
                .chargeSpeed(chargeSpeed)
                .locationDtos(locationDtos)
                .build();
    }
}
