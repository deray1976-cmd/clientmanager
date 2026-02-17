// =======================
// address-popup.js - Versió neta i optimitzada
// =======================

window.onload = () => {
    if (window.clientData) {
        window.clientId = window.clientData.id;
        initializeAddressTable(window.clientData.addresses || []);
    } else {
        document.getElementById("addressContainer").innerHTML = "<p>No hi ha dades disponibles.</p>";
    }
};

function initializeAddressTable(addresses) {
    const container = document.getElementById("addressContainer");
    container.innerHTML = "";

    // Contenidor de botons Afegir + Tancar
    const btnContainer = document.createElement("div");
    btnContainer.classList.add("btn-container");
    container.appendChild(btnContainer);

    const addBtn = document.createElement("button");
    addBtn.textContent = "Afegir adreça";
    addBtn.classList.add("btn", "btn-add");
    addBtn.onclick = addNewRow;
    btnContainer.appendChild(addBtn);

    const closeBtn = document.createElement("button");
    closeBtn.textContent = "Tancar";
    closeBtn.classList.add("btn", "btn-close");
    closeBtn.onclick = () => window.close();
    btnContainer.appendChild(closeBtn);

    // Taula de resultats
    const table = document.createElement("table");
    table.id = "resultsTable";
    table.classList.add("table", "table-striped");
    table.style.width = "100%";
    table.style.borderCollapse = "collapse";

    addresses = addresses || [];

    table.innerHTML = `
        <thead>
            <tr>
                <th>Carrer</th>
                <th>Ciutat</th>
                <th>Accions</th>
            </tr>
        </thead>
        <tbody>
            ${addresses.map((a, index) => `
                <tr>
                    <td class="street">${a.street || ""}</td>
                    <td class="city">${a.city || ""}</td>
                    <td class="actions">
                        <button class="edit-btn btn btn-success" data-index="${index}">Modificar</button>
                        <button class="delete-btn btn btn-danger" data-index="${index}">Suprimir</button>
                    </td>
                </tr>
            `).join('')}
        </tbody>
    `;

    container.appendChild(table);
    attachListeners();
}

function attachListeners() {
    const tbody = document.querySelector("#resultsTable tbody");

    tbody.querySelectorAll(".delete-btn").forEach(btn => {
        btn.onclick = () => {
            if (tbody.querySelectorAll("tr").length <= 1) {
                alert("Almenys una adreça ha de quedar.");
                return;
            }
            btn.closest("tr").remove();
            saveAllAddresses();
        };
    });

    tbody.querySelectorAll(".edit-btn").forEach(btn => btn.onclick = () => enableEdit(btn));
}

function enableEdit(button) {
    const row = button.closest("tr");
    const streetCell = row.querySelector(".street");
    const cityCell = row.querySelector(".city");

    streetCell.innerHTML = `<input type="text" class="street-input" value="${streetCell.textContent.trim()}">`;
    cityCell.innerHTML = `<input type="text" class="city-input" value="${cityCell.textContent.trim()}">`;

    button.textContent = "Guardar";
    button.classList.replace("btn-success", "btn-primary");
    button.onclick = () => saveEdit(button, row);
}

function saveEdit(button, row) {
    row.querySelector(".street").textContent = row.querySelector(".street-input").value.trim();
    row.querySelector(".city").textContent = row.querySelector(".city-input").value.trim();

    button.textContent = "Modificar";
    button.classList.replace("btn-primary", "btn-success");
    button.onclick = () => enableEdit(button);

    saveAllAddresses();
}

function saveAllAddresses() {
    const clientId = window.clientId;
    if (!clientId) {
        alert("No s'ha pogut determinar l'ID del client.");
        return;
    }

    const addresses = Array.from(document.querySelectorAll("#resultsTable tbody tr"))
        .map(r => ({
            street: r.querySelector(".street").textContent.trim(),
            city: r.querySelector(".city").textContent.trim()
        }));

    fetch(`http://localhost:8080/clients/${clientId}/addresses`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(addresses)
    })
    .then(res => {
        if (!res.ok) throw new Error("No s'ha pogut actualitzar les adreces");
        console.log("Adreces actualitzades correctament");
    })
    .catch(err => {
        console.error(err);
        alert("Error en actualitzar les adreces.");
    });
}

function addNewRow() {
    const tbody = document.querySelector("#resultsTable tbody");
    const row = document.createElement("tr");

    row.innerHTML = `
        <td class="street"><input type="text" class="street-input" placeholder="Carrer"></td>
        <td class="city"><input type="text" class="city-input" placeholder="Ciutat"></td>
        <td class="actions">
            <button class="edit-btn btn btn-primary">Guardar</button>
            <button class="delete-btn btn btn-danger">Suprimir</button>
        </td>
    `;

    tbody.appendChild(row);

    row.querySelector(".edit-btn").onclick = () => saveEdit(row.querySelector(".edit-btn"), row);
    row.querySelector(".delete-btn").onclick = () => {
        row.remove();
        saveAllAddresses();
    };
}
