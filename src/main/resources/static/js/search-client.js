document.addEventListener("DOMContentLoaded", function () {

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

    const name = document.getElementById("searchName").value.trim();
    const surname = document.getElementById("searchSurname").value.trim();
    const edat = document.getElementById("searchEdat").value.trim();
    const dni = document.getElementById("searchDni").value.trim();
    const email = document.getElementById("searchEmail").value.trim();

    const params = new URLSearchParams();

    if (name) params.append("name", name);
    if (surname) params.append("surname", surname);
    if (edat) params.append("edat", edat);
    if (dni) params.append("dni", dni);
    if (email) params.append("email", email);

    //fetchClients("http://localhost:8080/clients/search?" + params.toString());
    alert("submit "+params.toString());
    fetchClients("http://localhost:8080/clients?" + params.toString());
});

    // ============================
    // VEURE TOTS
    // ============================
    showAllBtn.addEventListener("click", function () {
        document.getElementById("searchName").value = "";
        document.getElementById("searchSurname").value = "";
        document.getElementById("searchEdat").value = "";
        document.getElementById("searchDni").value = "";
        document.getElementById("searchEmail").value = "";
        fetchClients("http://localhost:8080/clients");
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
            row.innerHTML = "<td colspan='8' style='text-align:center;'>No s'han trobat resultats</td>";
            tbody.appendChild(row);
            return;
        }

        clients.forEach(client => {
            const row = document.createElement("tr");

            row.innerHTML = `
                <td>${client.id}</td>
                <td class="name">${client.name}</td>
                <td class="surname">${client.surname || ""}</td>
                <td class="edat">${client.edat != null ? client.edat : ""}</td>
                <td class="dni">${client.dni || ""}</td>
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
        const surnameCell = row.querySelector(".surname");
        const ageCell = row.querySelector(".edat");
        const dniCell = row.querySelector(".dni");
        const emailCell = row.querySelector(".email");

        const name = nameCell.textContent.trim();
        const surname = surnameCell.textContent.trim();
        const edat = ageCell.textContent.trim();
        const dni = dniCell.textContent.trim();
        const email = emailCell.textContent.trim();

        nameCell.innerHTML = `<input type="text" class="name-input" value="${name}">`;
        surnameCell.innerHTML = `<input type="text" class="surname-input" value="${surname}">`;
        ageCell.innerHTML = `<input type="number" class="edat-input" value="${edat}">`;
        dniCell.innerHTML = `<input type="text" class="dni-input" value="${dni}">`;
        emailCell.innerHTML = `<input type="text" class="email-input" value="${email}">`;

        button.textContent = "Guardar";
    }

    // ============================
    // SAVE UPDATE
    // ============================
    function saveEdit(button, row) {
        const clientId = row.querySelector("td:first-child").textContent;

        const name = row.querySelector(".name-input").value.trim();
        const surname = row.querySelector(".surname-input").value.trim();
        const ageValue = row.querySelector(".edat-input").value.trim();
        const edat = ageValue ? parseInt(ageValue) : null;
        const dni = row.querySelector(".dni-input").value.trim();
        const email = row.querySelector(".email-input").value.trim();

        if (!name || !email) {
            alert("Nom i email són obligatoris");
            return;
        }

        fetch(`http://localhost:8080/clients/${clientId}`)
            .then(res => {
                if (!res.ok) throw new Error("Error carregant client");
                return res.json();
            })
            .then(fullClient => {

                const updatedClient = {
                    id: fullClient.id,
                    name: name,
                    surname: surname,
                    edat: edat,
                    dni: dni,
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
                row.querySelector(".surname").textContent = surname;
                row.querySelector(".edat").textContent = edat != null ? edat : "";
                row.querySelector(".dni").textContent = dni;
                row.querySelector(".email").textContent = email;

                button.textContent = "Update";
            })
            .catch(err => {
                console.error(err);
                alert("No s'ha pogut actualitzar el client. Revisa que es proporcionin tots els elements");
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
    