// Keep CSRF enabled: obtain the session token before each state-changing request.
async function apiFetch(url, options = {}) {
    const method = (options.method || "GET").toUpperCase();
    const headers = new Headers(options.headers);
    if (!["GET", "HEAD", "OPTIONS"].includes(method)) {
        const tokenResponse = await fetch("/api/csrf", { credentials: "same-origin" });
        if (tokenResponse.status === 401) {
            location.assign("/index.html");
            throw new Error("로그인이 필요합니다.");
        }
        if (!tokenResponse.ok) throw new Error("보안 토큰을 가져오지 못했습니다.");
        const csrf = await tokenResponse.json();
        headers.set(csrf.headerName, csrf.token);
    }
    const response = await fetch(url, { ...options, headers, credentials: "same-origin" });
    if (response.status === 401) {
        location.assign("/index.html");
        throw new Error("로그인이 필요합니다.");
    }
    return response;
}

document.addEventListener("DOMContentLoaded", () => {
    const button = document.getElementById("logoutButton");
    if (button) button.addEventListener("click", async () => {
        button.disabled = true;
        try {
            const response = await apiFetch("/logout", { method: "POST" });
            if (!response.ok) throw new Error("로그아웃에 실패했습니다.");
            location.assign("/index.html?logout");
        } catch (error) {
            alert(error.message);
            button.disabled = false;
        }
    });
});
