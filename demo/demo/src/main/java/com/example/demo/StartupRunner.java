package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.Map;

@Component
public class StartupRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {

        RestTemplate restTemplate = new RestTemplate();

        String url = "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";

        Map<String, String> body = new HashMap<>();
        body.put("name", "John Doe");
        body.put("regNo", "REG12347");
        body.put("email", "john@example.com");

        ResponseEntity<Map> response =
                restTemplate.postForEntity(url, body, Map.class);

        String webhook = (String) response.getBody().get("webhook");
        String accessToken = (String) response.getBody().get("accessToken");

        System.out.println("Webhook: " + webhook);
        System.out.println("Token: " + accessToken);

        // STEP 2: SQL QUERY 🔥
        String finalQuery = "SELECT e1.EMP_ID, e1.FIRST_NAME, e1.LAST_NAME, d.DEPARTMENT_NAME, COUNT(e2.EMP_ID) AS YOUNGER_EMPLOYEES_COUNT FROM EMPLOYEE e1 JOIN DEPARTMENT d ON e1.DEPARTMENT = d.DEPARTMENT_ID LEFT JOIN EMPLOYEE e2 ON e1.DEPARTMENT = e2.DEPARTMENT AND e2.DOB > e1.DOB GROUP BY e1.EMP_ID, e1.FIRST_NAME, e1.LAST_NAME, d.DEPARTMENT_NAME ORDER BY e1.EMP_ID DESC;";

        // STEP 3: Send to webhook
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> finalBody = new HashMap<>();
        finalBody.put("finalQuery", finalQuery);

        HttpEntity<Map<String, String>> entity =
                new HttpEntity<>(finalBody, headers);

        ResponseEntity<String> submitResponse =
                restTemplate.postForEntity(webhook, entity, String.class);

        System.out.println("Response: " + submitResponse.getBody());
        System.out.println("✅ DONE!");
    }
}
