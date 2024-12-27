document.addEventListener("DOMContentLoaded", async function () {
    await loadAdminInfo();
    await loadUsers();
    await loadRoles();

    document.getElementById("createUserForm").addEventListener("submit", function (e) {
        e.preventDefault();
        clearErrors();
        createUser();
    });

    document.getElementById("editUserForm").addEventListener("submit", function (e) {
        e.preventDefault();
        clearErrors();
        updateUser();
    });

    document.getElementById("deleteUserForm").addEventListener("submit", function (e) {
        e.preventDefault();
        confirmDeleteUser();
    })
});

async function loadAdminInfo() {
    const response = await fetch("/api/admin");
    const admin = await response.json();

    document.getElementById("adminInfo").innerText = `${admin.username} with roles: ${admin.roles.map(role => role.name.substring(5)).join(", ")}`;

    const adminInfoRow = document.getElementById("adminInfoRow");
    adminInfoRow.innerHTML = `
        <td>${admin.id}</td>
        <td>${admin.username}</td>
        <td>${admin.age}</td>
        <td>${admin.email}</td>
        <td>${admin.roles.map(role => role.name.substring(5)).join(", ")}</td>
    `;
}

async function loadUsers() {
    const response = await fetch("/api/admin/users");
    const users = await response.json();
    const tbody = document.querySelector("#usersTable tbody");
    tbody.innerHTML = "";

    users.forEach(user => {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${user.id}</td>
            <td>${user.username}</td>
            <td>${user.age}</td>
            <td>${user.email}</td>
            <td>${user.roles.map(role => role.name.substring(5)).join(", ")}</td>
            <td><button class="btn btn-info" onclick="editUser(${user.id})">Edit</button></td>
            <td><button class="btn btn-danger" onclick="deleteUser(${user.id})">Delete</button></td>
        `;
        tbody.appendChild(row);
    });
}

async function loadRoles() {
    const response = await fetch("/api/admin/roles");
    const roles = await response.json();
    const rolesSelect = document.getElementById("roles");
    const editRolesSelect = document.getElementById("editroles");
    rolesSelect.innerHTML = "";
    editRolesSelect.innerHTML = "";

    roles.forEach(role => {
        const option = document.createElement("option");
        option.value = role.id;
        option.textContent = role.name.substring(5);
        rolesSelect.appendChild(option.cloneNode(true));
        editRolesSelect.appendChild(option.cloneNode(true));
    });
}

async function createUser() {
    const username = document.getElementById("username").value;
    const age = document.getElementById("age").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const roles = Array.from(document.getElementById("roles").selectedOptions).map(option => ({
        id: parseInt(option.value), name: option.text
    }));

    const user = {username, age, email, password, roles};

    const response = await fetch("/api/admin/users", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(user)
    });

    if (response.ok) {
        await loadUsers();
        document.getElementById("createUserForm").reset();
        document.getElementById("nav-users-tab").click();
    } else {
        const errors = await response.json();
        displayErrors(errors);
    }
}

function displayErrors(errors, formPrefix = "") {
    console.log("Errors:", errors); // Логируем ошибки
    console.log("Form prefix:", formPrefix); // Логируем префикс
    document.querySelectorAll(".error").forEach(span => span.textContent = '');
    for (const [field, message] of Object.entries(errors)) {
        const errorElement = document.getElementById(`${formPrefix}${field}Error`);
        console.log("Error element:", errorElement); // Логируем элемент для ошибки
        if (errorElement) {
            errorElement.textContent = message;
        }
    }
}

function clearErrors() {
    document.querySelectorAll('.error').forEach(span => span.textContent = '');
}

async function editUser(id) {
    const response = await fetch(`/api/admin/users/${id}`);
    const user = await response.json();

    document.getElementById("editid").value = user.id;
    document.getElementById("editusername").value = user.username;
    document.getElementById("editage").value = user.age;
    document.getElementById("editemail").value = user.email;
    document.getElementById("editpassword").value = user.password;

    const rolesSelect = document.getElementById("editroles");
    rolesSelect.innerHTML = "";
    const allRoles = await fetch("/api/admin/roles").then(res => res.json());
    allRoles.forEach(role => {
        const option = document.createElement("option");
        option.value = role.id;
        option.textContent = role.name.substring(5);
        option.selected = user.roles.some(userRole => userRole.id === role.id);
        rolesSelect.appendChild(option);
    });

    const modal = new bootstrap.Modal(document.getElementById("editUserModal"));
    modal.show();
}

async function updateUser() {
    const id = document.getElementById("editid").value;
    const username = document.getElementById("editusername").value;
    const age = document.getElementById("editage").value;
    const email = document.getElementById("editemail").value;
    const password = document.getElementById("editpassword").value;
    const roles = Array.from(document.getElementById("editroles").selectedOptions).map(option => ({
        id: parseInt(option.value), name: option.text
    }));

    const user = {username, age, email, password, roles};

    const response = await fetch(`/api/admin/users/${id}`, {
        method: "PUT",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(user)
    });

    if (response.ok) {
        await loadUsers();
        const modal = bootstrap.Modal.getInstance(document.getElementById("editUserModal"));
        modal.hide();
    } else {
        const errors = await response.json();
        console.log("Server response:", errors);
        displayErrors(errors, "edit");
    }
}

async function deleteUser(id) {
    const response = await fetch(`/api/admin/users/${id}`);
    const user = await response.json();
    document.getElementById("deleteId").value = user.id;
    document.getElementById("deleteUsername").value = user.username;
    document.getElementById("deleteAge").value = user.age;
    document.getElementById("deleteEmail").value = user.email;

    const rolesSelect = document.getElementById("deleteRoles");
    rolesSelect.innerHTML = "";
    const allRoles = await fetch("/api/admin/roles").then(res => res.json());
    allRoles.forEach(role => {
        const option = document.createElement("option");
        option.value = role.id;
        option.textContent = role.name.substring(5);
        option.selected = user.roles.some(userRole => userRole.id === role.id);
        rolesSelect.appendChild(option);
    });

    const modal = new bootstrap.Modal(document.getElementById("deleteUserModal"));
    modal.show();
}

async function confirmDeleteUser() {
    const id = document.getElementById("deleteId").value;
    const response = await fetch(`/api/admin/users/${id}`, {method: "DELETE"});

    if (response.ok) {
        await loadUsers();
        const modal = bootstrap.Modal.getInstance(document.getElementById("deleteUserModal"));
        modal.hide();
    }
}