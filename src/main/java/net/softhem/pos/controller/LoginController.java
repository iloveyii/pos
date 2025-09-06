package net.softhem.pos.controller;

import net.softhem.pos.model.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

import org.springframework.web.bind.annotation.*;
import java.security.SecureRandom;
import java.util.*;
import java.util.Base64;

@Controller
@RequestMapping("/auth")
public class LoginController {
    private final SecureRandom random = new SecureRandom();
    // In-memory store for demo (replace with DB in real use)
    private final Map<String, UserRecord> users = new HashMap<>();

    static class UserRecord {
        String id;
        String challenge;
        String credentialId;
    }

    // 🔹 Generate random Base64URL string
    private String randomBase64URL(int length) {
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    // 📌 Registration challenge
    @ResponseBody
    @PostMapping("/register-challenge")
    public Map<String, Object> registerChallenge(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        if (username == null || username.isBlank()) throw new RuntimeException("Username required");

        UserRecord user = new UserRecord();
        user.id = randomBase64URL(16);
        user.challenge = randomBase64URL(32);
        users.put(username, user);

        Map<String, Object> options = new HashMap<>();
        options.put("challenge", user.challenge);
        options.put("rp", Map.of("name", "My Demo App", "id", "pos.softhem.net"));
        options.put("user", Map.of(
                "id", user.id,
                "name", username,
                "displayName", username
        ));
        options.put("pubKeyCredParams", List.of(Map.of("alg", -7, "type", "public-key")));
        options.put("authenticatorSelection", Map.of("authenticatorAttachment", "platform"));

        return options;
    }

    // 📌 Store credential after registration
    @ResponseBody
    @PostMapping("/register-complete")
    public String registerComplete(@RequestBody Map<String, Object> body) {
        String username = (String) body.get("username");
        Map<String, Object> credential = (Map<String, Object>) body.get("credential");
        if (!users.containsKey(username)) throw new RuntimeException("User not found");

        UserRecord user = users.get(username);
        user.credentialId = (String) credential.get("id");
        return "Registered successfully, now click fingerprint again to login";
    }

    // 📌 Login challenge
    @ResponseBody
    @PostMapping("/login-challenge")
    public Map<String, Object> loginChallenge(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        if (!users.containsKey(username)) throw new RuntimeException("User not found");

        UserRecord user = users.get(username);
        user.challenge = randomBase64URL(32);

        Map<String, Object> options = new HashMap<>();
        options.put("challenge", user.challenge);
        options.put("rpId", "pos.softhem.net");
        options.put("allowCredentials", List.of(Map.of(
                "id", user.credentialId,
                "type", "public-key",
                "transports", List.of("internal") // 👈 forces local biometrics
        )));
        options.put("userVerification", "preferred");

        return options;
    }

    // 📌 Complete login (⚠️ signature verification skipped for demo)
    @ResponseBody
    @PostMapping("/login-complete")
    public ResponseEntity<Map<String, Object>> loginComplete(@RequestBody Map<String, Object> body) {
        String username = body.get("username").toString();

        // Load user roles (from DB or repo)
        Set<String> roles = Set.of("USER"); // Example

        // Generate JWT
        String jwt = JwtUtil.generateToken(username, roles);

        return ResponseEntity.ok(Map.of(
                "token", jwt,
                "username", username,
                "roles", roles
        ));
        // return "Login success ✅ (signature verification skipped in demo)";
    }

    // 🔹 Serve login page with Thymeleaf
    @GetMapping("/login")
    public String index(Model model) {
        model.addAttribute("message", "Hello Thymeleaf!");
        return "login";
    }
}
