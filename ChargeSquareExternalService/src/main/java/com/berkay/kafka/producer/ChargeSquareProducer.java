package com.berkay.kafka.producer;

import com.berkay.kafka.model.ConsumerCreateChargeStation;
import com.berkay.kafka.model.ConsumerLocations;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChargeSquareProducer {

    private final KafkaTemplate<String, ConsumerCreateChargeStation> kafkaTemplate;

    private final String API_URL = "https://api.openchargemap.io/v3/poi/?output=json&countrycode=TR&key=b1f15906-8911-4bb7-abfb-b99cf39b98d2";
    private static final String TOPIC = "create-charging-station";


    public void sendCreate() {
        // RestTemplate ile API'den veri çek
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(API_URL, String.class);

        // JSON verisini işle
        JSONArray jsonArray = new JSONArray(response);

        // JSON dizisi en fazla 5 elemanla sınırlı işleniyor
        int limit = Math.min(jsonArray.length(), 5); // Eğer 5'ten az veri varsa o kadarını işleyelim.
        for (int i = 0; i < limit; i++) {
            JSONObject stationData = jsonArray.getJSONObject(i);
            ConsumerCreateChargeStation chargingStation = convertToChargingStationCreateRequestDTO(stationData);

            // Kafka'ya gönder
            kafkaTemplate.send(TOPIC, "create", chargingStation);
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
