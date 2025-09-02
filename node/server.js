import express from "express";
import bodyParser from "body-parser";
import crypto from "crypto";

const app = express();
app.use(bodyParser.json());
app.use(express.static("public")); // serve frontend

// In-memory DB for demo
const users = {}; // { username: { id, credential } }

function randomBase64URLBuffer(len = 32) {
  return crypto.randomBytes(len).toString("base64url");
}

// 📌 Registration challenge
app.post("/register-challenge", (req, res) => {
  const { username } = req.body;
  if (!username) return res.status(400).send("Username required");

  const userId = randomBase64URLBuffer(16);
  const challenge = randomBase64URLBuffer(32);

  const options = {
    challenge: Buffer.from(challenge, "base64url"),
    rp: { name: "My Demo App", id: "biometric.softhem.net" },
    user: {
      id: Buffer.from(userId, "base64url"),
      name: username,
      displayName: username,
    },
    pubKeyCredParams: [{ alg: -7, type: "public-key" }],
    authenticatorSelection: { authenticatorAttachment: "platform" },
  };

  users[username] = { id: userId, challenge };

  res.json(options);
});

// 📌 Login challenge
app.post("/login-challenge", (req, res) => {
  const { username } = req.body;
  const user = users[username];
  if (!user || !user.credential) return res.status(400).send("User not found");

  const challenge = randomBase64URLBuffer(32);
  user.challenge = challenge;

  const options = {
    challenge: Buffer.from(challenge, "base64url"),
    rpId: "biometric.softhem.net",
    allowCredentials: [
      {
        id: Buffer.from(user.credential.id, "base64url"),
        type: "public-key",
      },
    ],
    userVerification: "preferred",
  };

  res.json(options);
});

// 📌 Store credential after registration
app.post("/register-complete", (req, res) => {
  const { username, credential } = req.body;
  if (!users[username]) return res.status(400).send("User not found");
  users[username].credential = credential;
  res.send("Registered successfully");
});

// Dummy login verify
app.post("/login-complete", (req, res) => {
  res.send("Login success ✅ (signature verification skipped in demo)");
});

app.listen(3000, () =>
  console.log("Server running on https://biometric.softhem.net:3000")
);
