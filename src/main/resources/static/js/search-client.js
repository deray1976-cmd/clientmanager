document.addEventListener("DOMContentLoaded", function () {

    const searchForm = document.getElementById("searchForm");
    const searchInput = document.getElementById("searchInput");
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
            .then(response => {
                if (!response.ok) return [];
                return response.json();
            })
            .then(data => renderTable(data))
            .catch(error => {
                console.error("Error:", error);
                renderTable([]);
            });
    }

    // ============================
    // CERCA
    // ============================
    searchForm.addEventListener("submit", function (e) {
        e.preventDefault();
        const query = searchInput.value.trim();
        if (!query) return;

        fetchClients("http://localhost:8080/clients?query=" + encodeURIComponent(query));
    });

    // ============================
    // VEURE TOTS
    // ============================
    showAllBtn.addEventListener("click", function () {
        searchInput.value = "";
        fetchClients("http://localhost:8080/clients");
    });

    // ============================
    // AFEGIR CLIENT
    // ============================
    //addClientBtn.addEventListener("click", function () {
    //    window.location.href = "/client-form.html";
    //});

    // ============================
    // AFEGIR CLIENT - obrir popup
    // ============================
    addClientBtn.addEventListener("click", function () {
        window.open("/client-form.html", "_blank", "width=800,height=800");
    });

    // ============================
    // RESET
    // ============================
    resetBtn.addEventListener("click", function () {
        window.location.href = "http://localhost:8080/";
    });

    // ============================
    // RENDER TAULA
    // ============================
    function renderTable(clients) {

        tbody.innerHTML = "";
        table.style.display = "table";

        if (!clients || clients.length === 0) {
            const row = document.createElement("tr");
            row.innerHTML = "<td colspan='5' style='text-align:center;'>No s'han trobat resultats</td>";
            tbody.appendChild(row);
            return;
        }

        clients.forEach(client => {
            const row = document.createElement("tr");

            row.innerHTML = `
                <td>${client.id}</td>
                <td class="name">${client.name}</td>
                <td class="email">${client.email}</td>
                <td>
                    <button class="show-address-btn" data-id="${client.id}">Mostra adreça</button>
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
        document.querySelectorAll(".delete-btn").forEach(button => {
            button.addEventListener("click", function () {
                const row = this.closest("tr");
                const clientId = this.getAttribute("data-id");

                if (confirm("Segur que vols eliminar aquest client?")) {
                    deleteClient(clientId, row);
                }
            });
        });

        // UPDATE / GUARDAR
        document.querySelectorAll(".update-btn").forEach(button => {
            button.addEventListener("click", function () {

                const row = this.closest("tr");

                if (this.textContent === "Update") {
                    enableEdit(this);
                } else {
                    saveEdit(this, row);
                }
            });
        });

        // MOSTRA ADREÇA
        document.querySelectorAll(".show-address-btn").forEach(button => {
            button.addEventListener("click", function () {
                const clientId = this.getAttribute("data-id");
                showAddresses(clientId);
            });
        });
    }

    // ============================
    // ENABLE EDIT
    // ============================
    function enableEdit(button) {
        const row = button.closest("tr");
        const nameCell = row.querySelector(".name");
        const emailCell = row.querySelector(".email");

        const name = nameCell.textContent.trim();
        const email = emailCell.textContent.trim();

        nameCell.innerHTML = `<input type="text" class="name-input" value="${name}">`;
        emailCell.innerHTML = `<input type="text" class="email-input" value="${email}">`;

        button.textContent = "Guardar";
    }

    // ============================
    // SAVE UPDATE
    // ============================
    function saveEdit(button, row) {

        const clientId = row.querySelector("td:first-child").textContent;
        const name = row.querySelector(".name-input").value.trim();
        const email = row.querySelector(".email-input").value.trim();

        if (!name || !email) {
            alert("Nom i email són obligatoris");
            return;
        }

        // 1️⃣ Obtenir client complet
        fetch(`http://localhost:8080/clients/${clientId}`)
            .then(res => {
                if (!res.ok) throw new Error("Error carregant client");
                return res.json();
            })
            .then(fullClient => {

                const updatedClient = {
                    id: fullClient.id,
                    name: name,
                    email: email,
                    addresses: fullClient.addresses || []
                };

                return fetch(`http://localhost:8080/clients/${clientId}`, {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(updatedClient)
                });
            })
            .then(res => {
                if (!res.ok) throw new Error("Error actualitzant client");

                row.querySelector(".name").textContent = name;
                row.querySelector(".email").textContent = email;

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
        fetch("http://localhost:8080/clients/" + id, { method: "DELETE" })
            .then(response => {
                if (!response.ok) throw new Error("Error eliminant client");
                row.remove();
            })
            .catch(error => {
                console.error(error);
                alert("No s'ha pogut eliminar el client.");
            });
    }

    // ============================
    // SHOW ADDRESSES
    // ============================
    function showAddresses(clientId) {
        fetch(`http://localhost:8080/clients/${clientId}`)
            .then(res => res.json())
            .then(client => {

                const addressWindow = window.open("", "_blank", "width=1000,height=450");
                const clientJson = JSON.stringify(client);

                addressWindow.document.write(`
                    <!DOCTYPE html>
                    <html lang="ca">
                    <head>
                        <meta charset="UTF-8">
                        <title>Adreces de ${client.name}</title>
                        <link rel="stylesheet" href="/css/style.css">
                    </head>
                    <body>
                        <h2>Adreces de ${client.name}</h2>
                        <div id="addressContainer"></div>

                        <script>
                            window.clientData = ${clientJson};
                        </script>

                        <script src="/js/address-popup.js"></script>
                    </body>
                    </html>
                `);

                addressWindow.document.close();
            })
            .catch(err => {
                console.error(err);
                alert("No s'ha pogut carregar les adreces.");
            });
    }

});
