document.addEventListener("DOMContentLoaded", async function () {
    await loadUserInfo();

    async function loadUserInfo() {
        const response = await fetch("/api/user");
        const user = await response.json();

        document.getElementById("userInfo").innerText = `${user.username} with roles: ${user.roles.map(role => role.name.substring(5)).join(", ")}`;

        const userInfoRow = document.getElementById("userInfoRow");
        userInfoRow.innerHTML = `
        <td>${user.id}</td>
        <td>${user.username}</td>
        <td>${user.age}</td>
        <td>${user.email}</td>
        <td>${user.roles.map(role => role.name.substring(5)).join(", ")}</td>
    `;
    }
});