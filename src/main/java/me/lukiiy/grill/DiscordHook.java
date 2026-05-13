package me.lukiiy.grill;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

// O mini sistema que gerencia o webhook do Discord.
public class DiscordHook {
    private static final String WEBHOOK_BASE = "https://discord.com/api/webhooks/";

    private final HttpClient client = HttpClient.newHttpClient();
    public final URI webhookURL;

    public DiscordHook(String id, String token) {
        this.webhookURL = URI.create(WEBHOOK_BASE + id + "/" + token);

        // Check de validação deles
        try {
            HttpResponse<String> response = client.send(HttpRequest.newBuilder().uri(webhookURL).GET().build(), HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) throw new IllegalArgumentException("Invalid webhook (" + response.statusCode() + ")");
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to validate webhook", e);
        }
    }

    // Enviar mensagens simples
    public void sendMessage(String message) {
        if (message == null || message.isBlank()) return;

        sendWebhook("{\"content\":\"" + escapeJson(message) + "\"}", message);
    }

    // Enviar embeds
    public void sendSimpleEmbed(String title, String message) {
        if (title == null || title.isBlank() || message == null || message.isBlank()) return;

        // triple quoted because i hate formatting this
        String json = """
            {"embeds": [{"title": "%s", "description": "%s"}]}
        """.formatted(escapeJson(title), escapeJson(message));

        sendWebhook(json, title + ": " + message);
    }

    // Manda qualquer dado ao webhook
    private void sendWebhook(String json, String logMsg) {
        HttpRequest request = HttpRequest.newBuilder().uri(webhookURL)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            Grill.getInstance().getLogger().info("[Discord Webhook (" + response.statusCode() + ")] " + logMsg);
        } catch (Exception e) {
            Grill.getInstance().getLogger().severe("Discord Webhook error: " + e.getMessage());
        }
    }

    // Escapa caracteres especiais para o formato JSON
    private String escapeJson(String input) {
        return input.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}
