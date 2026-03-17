//=======================================================================*
//                  address-popup-clean.js
//=======================================================================*
document.addEventListener("DOMContentLoaded", () => {

    const BASE_URL = "http://localhost:8080";

    if (!window.clientData) {
        document.getElementById("addressContainer").innerHTML = "<p>No hi ha dades disponibles.</p>";
        return;
    }

    let client = { ...window.clientData };
    const clientId = client.id;

    initializeAddressTable(client.addresses);

    // ===================== FUNCIONS =====================
    function initializeAddressTable(addresses) {
        const container = document.getElementById("addressContainer");
        container.innerHTML = "";

        // Info client
        const clientInfo = document.createElement("div");
        clientInfo.classList.add("client-info");
        clientInfo.innerHTML = `
            <h2>Adreces del client</h2>
            <p><strong>ID:</strong> ${clientId}</p>
            <p><strong>Nom:</strong> ${client.name || ""} ${client.surname || ""}</p>
            <hr>
        `;
        container.appendChild(clientInfo);

        // Botons
        const btnContainer = document.createElement("div");
        btnContainer.classList.add("btn-container","mb-3");
        container.appendChild(btnContainer);

        const addBtn = document.createElement("button");
        addBtn.textContent = "Afegir adreça";
        addBtn.classList.add("btn","btn-add","btn-primary","me-2");
        addBtn.onclick = addNewRow;
        btnContainer.appendChild(addBtn);

        const closeBtn = document.createElement("button");
        closeBtn.textContent = "Tancar";
        closeBtn.classList.add("btn","btn-close","btn-secondary");
        closeBtn.onclick = () => window.close();
        btnContainer.appendChild(closeBtn);

        // Taula adreces
        const table = document.createElement("table");
        table.id = "resultsTable";
        table.classList.add("table","table-striped");
        table.style.width = "100%";
        table.innerHTML = `
            <thead>
                <tr>
                    <th>Carrer</th>
                    <th>Ciutat</th>
                    <th>Accions</th>
                </tr>
            </thead>
            <tbody>
                ${addresses.map(a => `
                    <tr data-id="${a.id || ''}">
                        <td class="street">${a.street || ""}</td>
                        <td class="city">${a.city || ""}</td>
                        <td class="actions">
                            <button class="edit-btn btn btn-success">Modificar</button>
                            <button class="delete-btn btn btn-danger">Suprimir</button>
                        </td>
                    </tr>
                `).join('')}
            </tbody>
        `;
        container.appendChild(table);

        attachListeners();
        updateDeleteButtons(); // Assegurar botons correctes en iniciar
    }

    function attachListeners() {
        const tbody = document.querySelector("#resultsTable tbody");

        tbody.querySelectorAll(".edit-btn").forEach(btn => btn.onclick = () => enableEdit(btn));
        tbody.querySelectorAll(".delete-btn").forEach(btn => btn.onclick = (e) => deleteRow(btn));
    }

    function enableEdit(btn) {
        const row = btn.closest("tr");
        row.querySelector(".street").innerHTML = `<input type="text" class="street-input form-control" value="${row.querySelector(".street").textContent.trim()}">`;
        row.querySelector(".city").innerHTML = `<input type="text" class="city-input form-control" value="${row.querySelector(".city").textContent.trim()}">`;

        btn.textContent = "Guardar";
        btn.classList.replace("btn-success","btn-primary");
        btn.onclick = () => saveRow(row);
    }

    function saveRow(row) {
        const street = row.querySelector(".street-input").value.trim();
        const city = row.querySelector(".city-input").value.trim();

        if (!street || !city) {
            alert("Els camps Carrer i Ciutat són obligatoris.");
            return;
        }

        const addressId = row.dataset.id || null;
        const payload = [{
            id: addressId ? Number(addressId) : null,
            street,
            city
        }];

        fetch(`${BASE_URL}/clients/${clientId}/addresses`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        })
        .then(res => {
            if (!res.ok) throw new Error("Error desant adreces");
            return res.json();
        })
        .then(updatedClient => {
            client = updatedClient;

            const updatedAddress = client.addresses.find(a => !addressId || a.id == addressId);
            if (!updatedAddress) return;

            row.dataset.id = updatedAddress.id;
            row.querySelector(".street").textContent = updatedAddress.street;
            row.querySelector(".city").textContent = updatedAddress.city;

            const editBtn = row.querySelector(".edit-btn");
            editBtn.textContent = "Modificar";
            editBtn.classList.replace("btn-primary","btn-success");
            editBtn.onclick = () => enableEdit(editBtn);

            updateDeleteButtons();
        })
        .catch(err => alert(err));
    }

    function syncAddressesToClient() {
        const tbody = document.querySelector("#resultsTable tbody");
        const updatedAddresses = [];

        tbody.querySelectorAll("tr").forEach(row => {
            const street = row.querySelector(".street").textContent.trim();
            const city = row.querySelector(".city").textContent.trim();
            const id = row.dataset.id ? Number(row.dataset.id) : null;

            if (street && city) {
                updatedAddresses.push({ id, street, city });
            }
        });

        client.addresses = updatedAddresses;
    }

    function updateDeleteButtons() {
        const tbody = document.querySelector("#resultsTable tbody");
        const rows = tbody.querySelectorAll("tr");
        const disable = rows.length <= 1;

        rows.forEach(row => {
            const deleteBtn = row.querySelector(".delete-btn");
            deleteBtn.disabled = disable;

            // Missatge si intenta eliminar l'única adreça
            deleteBtn.onclick = () => {
                if (disable) {
                    showAlert("No es pot eliminar l'única adreça del client.");
                    return;
                } else {
                    deleteRow(deleteBtn);
                }
            };
        });
    }

    function showAlert(message) {
        let alertDiv = document.createElement("div");
        alertDiv.className = "alert alert-warning mt-2";
        alertDiv.textContent = message;

        const container = document.getElementById("addressContainer");
        container.prepend(alertDiv);

        setTimeout(() => alertDiv.remove(), 3000); // desapareix després de 3s
    }

    function addNewRow() {
        const tbody = document.querySelector("#resultsTable tbody");
        const row = document.createElement("tr");
        row.dataset.id = "";
        row.innerHTML = `
            <td class="street"></td>
            <td class="city"></td>
            <td class="actions">
                <button class="edit-btn btn btn-primary">Guardar</button>
                <button class="delete-btn btn btn-danger">Suprimir</button>
            </td>
        `;
        tbody.appendChild(row);

        enableEdit(row.querySelector(".edit-btn"));
        updateDeleteButtons();
    }

    function deleteRow(btn) {
        const row = btn.closest("tr");
        const addressId = row.dataset.id;

        if (addressId) {
            if (!confirm("Segur que vols eliminar aquesta adreça?")) return;

            fetch(`${BASE_URL}/clients/${clientId}/addresses/${addressId}`, { method: "DELETE" })
            .then(res => {
                if (!res.ok) throw new Error("Error eliminant adreça");
                client.addresses = client.addresses.filter(a => a.id != addressId);
                row.remove();
                updateDeleteButtons();
            })
            .catch(err => showAlert(err));
        } else {
            row.remove();
            updateDeleteButtons();
        }
    }

    // ===================== Guardar client principal =====================
    document.getElementById("saveClientBtn").onclick = () => {
        syncAddressesToClient();

        const payload = {
            id: client.id,
            name: document.getElementById("nameInput").value,
            surname: document.getElementById("surnameInput").value,
            dni: document.getElementById("dniInput").value,
            edat: Number(document.getElementById("ageInput").value),
            email: document.getElementById("emailInput").value,
            addresses: client.addresses
        };

        fetch(`${BASE_URL}/clients/${client.id}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        })
        .then(res => {
            if (!res.ok) throw new Error("Error guardant client");
            return res.json();
        })
        .then(updatedClient => {
            client = updatedClient;
            showAlert("Client desat correctament!");
            updateDeleteButtons();
        })
        .catch(err => showAlert(err));
    };

});