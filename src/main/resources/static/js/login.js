async function registerPasskey() {
    const username = document.getElementById("username").value;
    if(!username) {
        alert('Please enter username');
        return false;
    }
    const options = await fetch("/auth/register-challenge", {
        method: "POST", headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username })
    }).then(res => res.json());

    options.challenge = base64urlToUint8Array(options.challenge);
    options.user.id = base64urlToUint8Array(options.user.id);

    const cred = await navigator.credentials.create({ publicKey: options });

    await fetch("/auth/register-complete", {
        method: "POST", headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, credential: { id: cred.id } })
    });

    localStorage.setItem(username, true);
    document.getElementById("status").innerText = "✅ Passkey registered!";
    return true;
}

async function loginPasskey() {
    const username = document.getElementById("username").value;
    const options = await fetch("/auth/login-challenge", {
        method: "POST", headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username })
    }).then(res => res.json());

    options.challenge = base64urlToUint8Array(options.challenge);
    options.allowCredentials = options.allowCredentials.map(c => ({
        ...c, id: base64urlToUint8Array(c.id)
    }));

    const assertion = await navigator.credentials.get({ publicKey: options });

    const res = await fetch("/auth/login-complete", {
        method: "POST", headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username })
    });
    const { token } = await res.json();
    // Store in localStorage (or HttpOnly cookie)
    localStorage.setItem("jwt", token);
    document.getElementById("status").innerText = "🎉 Logged in successfully!";
    return true;
}