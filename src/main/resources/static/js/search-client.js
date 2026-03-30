//=======================================================================*
//                  search-clients.js (VERSIÓ AMB ADRECES)
//=======================================================================*

document.addEventListener("DOMContentLoaded", () => {

    const searchForm = document.getElementById("searchForm");
    const table = document.getElementById("resultsTable");
    const tbody = table.querySelector("tbody");
    const addClientBtn = document.getElementById("addClientBtn");
    const showAllBtn = document.getElementById("showAllBtn");
    const resetBtn = document.getElementById("resetBtn");

    const BASE_URL = "http://localhost:8080";
    table.style.display = "none";

    // ============================
    // FETCH CLIENTS
    // ============================
    function fetchClients(url) {
        fetch(url)
            .then(res => res.ok ? res.json() : [])
            .then(data => renderTable(data))
            .catch(err => {
                console.error("Error fetching clients:", err);
                renderTable([]);
            });
    }

    // ============================
    // CERCA
    // ============================
    searchForm.addEventListener("submit", e => {
        e.preventDefault();
        const params = new URLSearchParams();
        ["Name","Surname","Edat","Dni","Email"].forEach(id => {
            const val = document.getElementById("search"+id).value.trim();
            if(val) params.append(id.toLowerCase(), val);
        });
        fetchClients(`${BASE_URL}/clients?${params.toString()}`);
    });

    showAllBtn.addEventListener("click", () => {
        ["Name","Surname","Edat","Dni","Email"].forEach(id => document.getElementById("search"+id).value = "");
        fetchClients(`${BASE_URL}/clients`);
    });

    addClientBtn.addEventListener("click", () => {
        window.open("/client-form.html", "_blank", "width=800,height=1000");
    });

    resetBtn.addEventListener("click", () => window.location.href = `${BASE_URL}/`);

    // ============================
    // RENDER TAULA
    // ============================
    function renderTable(clients) {
        tbody.innerHTML = "";
        table.style.display = "table";

        if(!clients || clients.length === 0){
            tbody.innerHTML = `<tr><td colspan="8" style="text-align:center;">No s'han trobat resultats</td></tr>`;
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
                <td><button class="show-address-btn" data-id="${client.id}">Mostra adreça</button></td>
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
        document.querySelectorAll(".delete-btn").forEach(btn => {
            btn.onclick = () => {
                const row = btn.closest("tr");
                const clientId = btn.dataset.id;
                if(confirm("Segur que vols eliminar aquest client?")) deleteClient(clientId,row);
            };
        });

        document.querySelectorAll(".update-btn").forEach(btn => {
            btn.onclick = () => {
                const row = btn.closest("tr");
                btn.textContent === "Update" ? enableEdit(btn) : saveEdit(btn,row);
            };
        });

        document.querySelectorAll(".show-address-btn").forEach(btn => {
            btn.onclick = () => showAddresses(btn.dataset.id);
        });
    }

    // ============================
    // ENABLE / SAVE EDIT
    // ============================
    function enableEdit(btn){
        const row = btn.closest("tr");
        ["name","surname","edat","dni","email"].forEach(cls=>{
            const cell = row.querySelector(`.${cls}`);
            const val = cell.textContent.trim();
            cell.innerHTML = `<input type="${cls==="edat"?"number":"text"}" class="${cls}-input" value="${val}">`;
        });
        btn.textContent="Guardar";
    }

    function saveEdit(btn,row){
        const clientId = row.querySelector("td:first-child").textContent;

        const updated = {
            id: clientId,
            name: row.querySelector(".name-input").value.trim(),
            surname: row.querySelector(".surname-input").value.trim(),
            edat: parseInt(row.querySelector(".edat-input").value.trim()) || 0,
            dni: row.querySelector(".dni-input").value.trim(),
            email: row.querySelector(".email-input").value.trim()
            // Les adreces NO s'inclouen aquí - es manejen per separat
        };

        // Validar SOLO nom i email (els camps del client)
        if(!updated.name || !updated.email){
            alert("Nom i email són obligatoris");
            return;
        }

        fetch(`${BASE_URL}/clients/${clientId}`,{
            method:"PUT",
            headers:{"Content-Type":"application/json"},
            body:JSON.stringify(updated)
        })
        .then(res=>{
            if(!res.ok) throw new Error("Error actualitzant client");
            ["name","surname","edat","dni","email"].forEach(cls=>row.querySelector(`.${cls}`).textContent = updated[cls] ?? "");
            btn.textContent="Update";
            alert("Client actualitzat correctament!");
        })
        .catch(err=>{ console.error(err); alert("No s'ha pogut actualitzar el client."); });
    }

    // ============================
    // DELETE CLIENT
    // ============================
    function deleteClient(id,row){
        fetch(`${BASE_URL}/clients/${id}`,{method:"DELETE"})
            .then(res=>{
                if(!res.ok) throw new Error("Error eliminant client");
                row.remove();
            })
            .catch(err=>{console.error(err); alert("No s'ha pogut eliminar el client.");});
    }

    // ============================
    // SHOW ADDRESSES POPUP
    // ============================
    function showAddresses(clientId){
        fetch(`${BASE_URL}/clients/${clientId}?withAddresses=true`)
            .then(res=>res.json())
            .then(client=>{
                // Guardem adreces globals per a PUT
                window.clientAddresses = client.addresses || [];

                const addressWindow = window.open("", "_blank","width=1000,height=450");
                const clientData = {
                    id: client.id,
                    name: client.name,
                    surname: client.surname || "",
                    addresses: window.clientAddresses
                };
                addressWindow.document.write(`
                    <!DOCTYPE html>
                    <html lang="ca">
                    <head>
                        <meta charset="UTF-8">
                        <title>Adreces de ${client.name}</title>
                        <link rel="stylesheet" href="/css/style.css">
                    </head>
                    <body>
                        <div id="addressContainer"></div>
                        <script>window.clientData=${JSON.stringify(clientData)};</script>
                        <script src="/js/address-popup.js"></script>
                    </body>
                    </html>
                `);
                addressWindow.document.close();
            })
            .catch(err=>{console.error(err); alert("No s'han pogut carregar les adreces.");});
    }

});