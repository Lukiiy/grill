package me.lukiiy.grill;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class DiscordHook {
    private static final String WEBHOOK_BASE = "https://discord.com/api/webhooks/";

    public final URI webhookURL;

    public DiscordHook(String id, String token) {
        this.webhookURL = URI.create(WEBHOOK_BASE + id + "/" + token);
    }

    public void sendMessage(String message) {
        HttpRequest request = HttpRequest.newBuilder().uri(webhookURL)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"content\":\"" + message + "\"}"))
                .build();

        HttpResponse<String> response = null;

        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            Grill.getInstance().getLogger().severe("Discord Webhook error: " + e.getMessage());
        }

        Grill.getInstance().getLogger().info("[Discord Webhook (" + response.statusCode() + ")] " + message);
    }
}
