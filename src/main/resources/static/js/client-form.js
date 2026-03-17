//=======================================================================*
//                  client-form.js (UNA ADREÇA MÍNIMA)
//=======================================================================*

document.addEventListener("DOMContentLoaded", () => {

    const BASE_URL = "http://localhost:8080";

    const form = document.getElementById("clientForm");
    const addAddressBtn = document.getElementById("addAddressBtn");
    const resetBtn = document.getElementById("resetFormBtn");
    const closeBtn = document.getElementById("closeFormBtn");
    const container = document.getElementById("addresses-container");

    // ============================
    // FUNCIONS AUXILIARS
    // ============================
    function createAddressBlock(street = "", city = "") {
        const div = document.createElement("div");
        div.className = "address address-block";

        div.innerHTML = `
            <div class="address-fields">
                <div class="form-group">
                    <label>Carrer</label>
                    <input type="text" class="street" value="${street}">
                </div>
                <div class="form-group">
                    <label>Ciutat</label>
                    <input type="text" class="city" value="${city}">
                </div>
            </div>
            <button type="button" class="remove-address-btn">Eliminar</button>
        `;
        return div;
    }

    function ensureAtLeastOneAddress() {
        if (container.querySelectorAll(".address-block").length === 0) {
            container.appendChild(createAddressBlock());
        }
    }

    // ============================
    // INICIALITZAR AMB UNA ADREÇA
    // ============================
    ensureAtLeastOneAddress();

    // ============================
    // AFEGIR ADREÇA
    // ============================
    addAddressBtn.addEventListener("click", () => {
        container.appendChild(createAddressBlock());
    });

    // ============================
    // ELIMINAR ADREÇA (delegació)
    // ============================
    container.addEventListener("click", (e) => {
        if (e.target.classList.contains("remove-address-btn")) {
            const total = container.querySelectorAll(".address-block").length;
            if (total > 1) {
                e.target.closest(".address-block").remove();
            } else {
                alert("Ha de quedar almenys una adreça.");
            }
        }
    });

    // ============================
    // RESET FORMULARI
    // ============================
    resetBtn.addEventListener("click", () => {
        if (confirm("Segur que vols reiniciar el formulari?")) {
            form.reset();
            container.innerHTML = "";
            ensureAtLeastOneAddress();
        }
    });

    // ============================
    // TANCAR FORMULARI
    // ============================
    closeBtn.addEventListener("click", () => {
        if (confirm("Segur que vols tancar la finestra?")) {
            window.close();
        }
    });

    // ============================
    // ENVIAR FORMULARI
    // ============================
    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        const client = {
            name: document.getElementById("name").value.trim(),
            surname: document.getElementById("surname").value.trim(),
            edat: parseInt(document.getElementById("edat").value) || null,
            dni: document.getElementById("dni").value.trim(),
            email: document.getElementById("email").value.trim(),
            addresses: []
        };

        if (!client.name || !client.email) {
            alert("Nom i email són obligatoris");
            return;
        }

        document.querySelectorAll(".address-block").forEach(block => {
            const street = block.querySelector(".street").value.trim();
            const city = block.querySelector(".city").value.trim();
            if (street && city) {
                client.addresses.push({ street, city });
            }
        });

        // Si per algun motiu no hi ha cap adreça amb dades
        if (client.addresses.length === 0) {
            alert("Ha de haver-hi almenys una adreça amb carrer i ciutat.");
            return;
        }

        try {
            const res = await fetch(`${BASE_URL}/clients`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(client)
            });

            if (!res.ok) throw new Error("Error creant client");

            await res.json();
            alert("Client creat correctament!");
            form.reset();
            container.innerHTML = "";
            ensureAtLeastOneAddress();

        } catch (err) {
            console.error(err);
            alert("Error al crear client: " + err.message);
        }
    });

});