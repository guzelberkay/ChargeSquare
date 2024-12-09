package com.berkay.service;

import com.berkay.dto.ChargingStationCreateRequestDTO;
import com.berkay.dto.LocationSaveRequestDTO;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ExternalApiService {

    private final String API_URL = "https://api.openchargemap.io/v3/poi/?output=json&countrycode=TR&key=b1f15906-8911-4bb7-abfb-b99cf39b98d2";
    private final RestTemplate restTemplate;

    private final KafkaTemplate<String, String> kafkaTemplate;  // KafkaTemplate<String, String> kullanıyoruz

    private static final String TOPIC = "create-charging-station";

    // Dışarıdan gelen veriyi alıp Kafka'ya gönderir

    @KafkaListener(topics = TOPIC,groupId = "group1")
    public void fetchAndSendChargingStations() {
        String response = restTemplate.getForObject(API_URL, String.class);
        JSONArray stations = new JSONArray(response);

        // API'den alınan her istasyon için veriyi dönüştürüp Kafka'ya gönderiyoruz
        for (int i = 0; i < stations.length(); i++) {
            JSONObject stationData = stations.getJSONObject(i);

            // ChargingStationCreateRequestDTO'yu oluşturuyoruz
            ChargingStationCreateRequestDTO dto = convertToChargingStationCreateRequestDTO(stationData);

            System.out.println(dto);

            kafkaTemplate.send(TOPIC, dto.toString());

        }
    }

    // DTO'yu JSON string formatına dönüştüren metot
    private String convertDtoToJson(ChargingStationCreateRequestDTO dto) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", dto.name());
        jsonObject.put("chargeSpeed", dto.chargeSpeed());

        // LocationSaveRequestDTO nesnesini JSON formatına dönüştürme
        JSONArray locationDtosJson = new JSONArray();
        for (LocationSaveRequestDTO location : dto.locationDtos()) {
            JSONObject locationJson = new JSONObject();
            locationJson.put("address", location.address());
            locationJson.put("city", location.city());
            locationJson.put("country", location.country());
            locationDtosJson.put(locationJson);
        }

        jsonObject.put("locationDtos", locationDtosJson);

        return jsonObject.toString();  // JSON formatındaki string döndürülüyor
    }

    // API'den gelen veriyi DTO'ya dönüştüren metot
    private ChargingStationCreateRequestDTO convertToChargingStationCreateRequestDTO(JSONObject stationData) {
        // Şarj istasyonunun adı "AddressInfo" içinde yer alıyor
        String name = stationData.getJSONObject("AddressInfo").getString("Title");

        // Şarj hızı "Connections" içinde yer alıyor
        double chargeSpeed = stationData.getJSONArray("Connections")
                .getJSONObject(0)
                .getDouble("PowerKW");  // İlk bağlantının şarj gücü

        // Lokasyon verisini alalım: "AddressInfo" içindeki veriler
        JSONObject addressInfo = stationData.getJSONObject("AddressInfo");

        // AddressLine1 ve AddressLine2'yi alalım, varsa
        String addressLine1 = addressInfo.optString("AddressLine1", "");  // Eğer yoksa boş string döner
        String addressLine2 = addressInfo.optString("AddressLine2", "");  // Eğer yoksa boş string döner

        // İki adres satırını birleştirelim
        String address = addressLine1 + (addressLine2.isEmpty() ? "" : " " + addressLine2);

        String city = addressInfo.optString("Town", ""); // "Town" verisini alalım
        String country = addressInfo.getJSONObject("Country").optString("Title", ""); // "Country" -> "Title" verisini alalım

        // LocationSaveRequestDTO nesnesi oluşturuluyor
        List<LocationSaveRequestDTO> locationDtos = new ArrayList<>();
        locationDtos.add(new LocationSaveRequestDTO(address, city, country));  // Tek bir lokasyon olduğu varsayılıyor

        // ChargingStationCreateRequestDTO nesnesi döndürülüyor
        return new ChargingStationCreateRequestDTO(name, chargeSpeed, locationDtos);
    }
}
