//=======================================================================*
//                  client-form.js (versió depurada)
//=======================================================================*

document.addEventListener("DOMContentLoaded", function () {

    const BASE_URL = "http://localhost:8080";

    const searchForm = document.getElementById("searchForm");
    const table = document.getElementById("resultsTable");
    const tbody = table.querySelector("tbody");
    const addClientBtn = document.getElementById("addClientBtn");
    const showAllBtn = document.getElementById("showAllBtn");
    const resetBtn = document.getElementById("resetBtn");

    table.style.display = "none";

    // ============================
    // FETCH CLIENTS
    // ============================
    function fetchClients(url) {
        fetch(url)
            .then(res => res.ok ? res.json() : [])
            .then(data => renderTable(data))
            .catch(err => {
                console.error("Error:", err);
                renderTable([]);
            });
    }

    // ============================
    // CERCA AVANÇADA
    // ============================
    searchForm.addEventListener("submit", function (e) {
        e.preventDefault();
        const params = new URLSearchParams();
        ["Name", "Surname", "Edat", "Dni", "Email"].forEach(id => {
            const val = document.getElementById("search" + id).value.trim();
            if (val) params.append(id.toLowerCase(), val);
        });

        fetchClients(`${BASE_URL}/clients${params.toString() ? "?" + params.toString() : ""}`);
    });

    // ============================
    // VEURE TOTS
    // ============================
    showAllBtn.addEventListener("click", function () {
        ["Name", "Surname", "Edat", "Dni", "Email"].forEach(id =>
            document.getElementById("search" + id).value = ""
        );
        fetchClients(`${BASE_URL}/clients`);
    });

    // ============================
    // AFEGIR CLIENT - popup
    // ============================
    addClientBtn.addEventListener("click", function () {
        window.open("/client-form.html", "_blank", "width=800,height=800");
    });

    // ============================
    // RESET
    // ============================
    resetBtn.addEventListener("click", () => window.location.href = `${BASE_URL}/`);

    // ============================
    // RENDER TAULA
    // ============================
    function renderTable(clients) {
        tbody.innerHTML = "";
        table.style.display = "table";

        if (!clients || clients.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="8" style="text-align:center;">
                        No s'han trobat resultats
                    </td>
                </tr>
            `;
            return;
        }

        clients.forEach(client => {
            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${client.id}</td>
                <td class="name">${client.name}</td>
                <td class="surname">${client.surname || ""}</td>
                <td class="edat">${client.edat ?? ""}</td>
                <td class="dni">${client.dni || ""}</td>
                <td class="email">${client.email}</td>
                <td>
                    <button class="show-address-btn" data-id="${client.id}">
                        Mostra adreça
                    </button>
                </td>
                <td>
                    <button class="update-btn" data-id="${client.id}">Update</button>
                    <button class="delete-btn" data-id="${client.id}">Delete</button>
                </td>
            `;
            tbody.appendChild(row);
        });

        attachActionListeners();
    }

    // ============================
    // ACTION LISTENERS
    // ============================
    function attachActionListeners() {

        // DELETE
        document.querySelectorAll(".delete-btn").forEach(btn => {
            btn.addEventListener("click", function () {
                const row = this.closest("tr");
                const clientId = this.dataset.id;
                if (confirm("Segur que vols eliminar aquest client?")) {
                    deleteClient(clientId, row);
                }
            });
        });

        // UPDATE
        document.querySelectorAll(".update-btn").forEach(btn => {
            btn.addEventListener("click", function () {
                const row = this.closest("tr");
                if (this.textContent === "Update") {
                    enableEdit(this);
                } else {
                    saveEdit(this, row);
                }
            });
        });

        // MOSTRA ADREÇA
        document.querySelectorAll(".show-address-btn").forEach(btn => {
            btn.addEventListener("click", function () {
                const clientId = this.dataset.id;
                showAddresses(clientId);
            });
        });
    }

    // ============================
    // ENABLE EDIT
    // ============================
    function enableEdit(button) {
        const row = button.closest("tr");
        ["name","surname","edat","dni","email"].forEach(cls => {
            const cell = row.querySelector(`.${cls}`);
            const val = cell.textContent.trim();
            cell.innerHTML = `<input type="${cls==="edat"?"number":"text"}" class="${cls}-input" value="${val}">`;
        });
        button.textContent = "Guardar";
    }

    // ============================
    // SAVE UPDATE
    // ============================
    function saveEdit(button, row) {
        const clientId = row.querySelector("td:first-child").textContent;
        const updatedClient = {
            id: clientId,
            name: row.querySelector(".name-input").value.trim(),
            surname: row.querySelector(".surname-input").value.trim(),
            edat: parseInt(row.querySelector(".edat-input").value.trim()) || null,
            dni: row.querySelector(".dni-input").value.trim(),
            email: row.querySelector(".email-input").value.trim()
        };

        if (!updatedClient.name || !updatedClient.email) {
            alert("Nom i email són obligatoris");
            return;
        }

        fetch(`${BASE_URL}/clients/${clientId}`, {
            method: "PUT",
            headers: {"Content-Type":"application/json"},
            body: JSON.stringify(updatedClient)
        })
        .then(res => {
            if (!res.ok) throw new Error("Error actualitzant client");
            ["name","surname","edat","dni","email"].forEach(cls => {
                row.querySelector(`.${cls}`).textContent = updatedClient[cls] ?? "";
            });
            button.textContent = "Update";
        })
        .catch(err => {
            console.error(err);
            alert("No s'ha pogut actualitzar el client.");
        });
    }

    // ============================
    // DELETE CLIENT
    // ============================
    function deleteClient(id, row) {
        fetch(`${BASE_URL}/clients/${id}`, { method:"DELETE" })
            .then(res => {
                if (!res.ok) throw new Error("Error eliminant client");
                row.remove();
            })
            .catch(err => {
                console.error(err);
                alert("No s'ha pogut eliminar el client.");
            });
    }

    // ============================
    // SHOW ADDRESSES
    // ============================
    function showAddresses(clientId) {
        Promise.all([
            fetch(`${BASE_URL}/clients/${clientId}`).then(r => r.json()),
            fetch(`${BASE_URL}/addresses/client/${clientId}`).then(r => r.json())
        ])
        .then(([client, addresses]) => {

            const clientData = {
                id: client.id || client.clientId,
                name: client.name || client.nom || "",
                surname: client.surname || client.cognom || "",
                addresses: addresses || []
            };

            const clientJson = JSON.stringify(clientData);
            const addressWindow = window.open("", "_blank", "width=1000,height=450");

            addressWindow.document.write(`
                <!DOCTYPE html>
                <html lang="ca">
                <head>
                    <meta charset="UTF-8">
                    <title>Adreces de ${clientData.name}</title>
                    <link rel="stylesheet" href="/css/style.css">
                </head>
                <body>
                    <div id="addressContainer"></div>
                    <script>window.clientData = ${clientJson};</script>
                    <script src="/js/address-popup.js"></script>
                </body>
                </html>
            `);

            addressWindow.document.close();
        })
        .catch(err => {
            console.error(err);
            alert("No s'han pogut carregar les adreces.");
        });
    }

});