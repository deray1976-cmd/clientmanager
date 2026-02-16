document.addEventListener("DOMContentLoaded", function () {

    const searchForm = document.getElementById("searchForm");
    const searchInput = document.getElementById("searchInput");
    const table = document.getElementById("resultsTable");
    const tbody = table.querySelector("tbody");
    const addClientBtn = document.getElementById("addClientBtn");
    const showAllBtn = document.getElementById("showAllBtn");

    // Amaguem la taula inicialment
    table.style.display = "none";

    // ============================
    // FUNCIÓ FETCH
    // ============================
    function fetchClients(url) {
        fetch(url)
            .then(response => {
                if (!response.ok) {
                    return [];
                }
                return response.json();
            })
            .then(data => {
                renderTable(data);
            })
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

        //fetchClients("http://localhost:8080/clients/search?query=" + encodeURIComponent(query));
        fetchClients("http://localhost:8080/clients?query=" + encodeURIComponent(query));

    });

    // ============================
    // VEURE TOTS
    // ============================
    showAllBtn.addEventListener("click", function () {
        searchInput.value = "";
        //fetchClients("http://localhost:8080/clients/search");
        fetchClients("http://localhost:8080/clients");

    });

    // ============================
    // AFEGIR CLIENT
    // ============================
    addClientBtn.addEventListener("click", function () {
        window.location.href = "/client-form.html";
    });

    // ============================
    // RENDER TAULA
    // ============================
    function renderTable(clients) {

        tbody.innerHTML = "";
        table.style.display = "table";

        if (!clients || clients.length === 0) {
            const row = document.createElement("tr");
            row.innerHTML = "<td colspan='4' style='text-align:center;'>No s'han trobat resultats</td>";
            tbody.appendChild(row);
            return;
        }

        clients.forEach(client => {
            const row = document.createElement("tr");

            const addressesText = (client.addresses || [])
                .map(a => (a.street || "") + " (" + (a.city || "") + ")")
                .join("<br>");

            row.innerHTML =
                "<td><button class='delete-btn' data-id='" + client.id + "'>Suprimir</button></td>" +
                "<td>" + client.id + "</td>" +
                "<td>" + client.name + "</td>" +
                "<td>" + client.email + "</td>" +
                "<td>" + addressesText + "</td>";

            tbody.appendChild(row);
        });

        // Afegim els listeners als botons de suprimir
    document.querySelectorAll(".delete-btn").forEach(button => {
        button.addEventListener("click", function () {
            const clientId = this.getAttribute("data-id");

            if (confirm("Segur que vols eliminar aquest client?")) {
                deleteClient(clientId, this.closest("tr"));
            }
        });
    });
    }

});