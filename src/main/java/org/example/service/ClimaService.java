package org.example.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.Cidade;
import org.example.dto.ClimaDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class ClimaService {

    private static final Logger log = LoggerFactory.getLogger(ClimaService.class);

    private final SimpMessagingTemplate messagingTemplate;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Random random = new Random();

    private static final List<Cidade> CIDADES = List.of(
            new Cidade("Sao Paulo",       -23.5505, -46.6333),
            new Cidade("Rio de Janeiro",  -22.9068, -43.1729),
            new Cidade("Brasilia",        -15.7801, -47.9292),
            new Cidade("Salvador",        -12.9714, -38.5014),
            new Cidade("Fortaleza",        -3.7172, -38.5433),
            new Cidade("Belo Horizonte",  -19.9191, -43.9386),
            new Cidade("Manaus",           -3.1190, -60.0217),
            new Cidade("Curitiba",        -25.4284, -49.2733),
            new Cidade("Recife",           -8.0476, -34.8770),
            new Cidade("Porto Alegre",    -30.0346, -51.2177)
    );

    private static final Map<Integer, String> WMO_DESCRICAO = Map.ofEntries(
            Map.entry(0,  "Ceu Limpo"),
            Map.entry(1,  "Predominantemente Limpo"),
            Map.entry(2,  "Parcialmente Nublado"),
            Map.entry(3,  "Nublado"),
            Map.entry(45, "Nevoeiro"),
            Map.entry(51, "Garoa Leve"),
            Map.entry(61, "Chuva Fraca"),
            Map.entry(63, "Chuva Moderada"),
            Map.entry(65, "Chuva Forte"),
            Map.entry(80, "Pancadas de Chuva"),
            Map.entry(95, "Trovoada")
    );

    public ClimaService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Scheduled(fixedRate = 5000)
    public void enviarClima() {
        Cidade cidade = CIDADES.get(random.nextInt(CIDADES.size()));

        try {
            String url = "https://api.open-meteo.com/v1/forecast"
                    + "?latitude=" + cidade.getLatitude()
                    + "&longitude=" + cidade.getLongitude()
                    + "&current=temperature_2m,weather_code"
                    + "&timezone=America%2FSao_Paulo";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString()
            );

            // Log da resposta bruta para diagnóstico
            log.info("Resposta da API: {}", response.body());

            JsonNode root    = objectMapper.readTree(response.body());
            JsonNode current = root.path("current");

            double temperatura = current.path("temperature_2m").asDouble();
            int wmoCode        = current.path("weather_code").asInt();
            String descricao   = WMO_DESCRICAO.getOrDefault(wmoCode, "Sem informacao");
            String horario     = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

            ClimaDTO payload = new ClimaDTO(cidade.getNome(), temperatura, descricao, horario);
            messagingTemplate.convertAndSend("/topic/clima", payload);

            log.info("Broadcast: {} - {}C - {}", cidade.getNome(), temperatura, descricao);

        } catch (Exception ex) {
            log.error("Erro ao buscar clima para {}: {}", cidade.getNome(), ex.getMessage());
        }
    }
}