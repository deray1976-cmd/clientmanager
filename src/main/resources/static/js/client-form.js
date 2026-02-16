

function addAddress() {
    const div = document.createElement("div");
    div.className = "address address-block";
    div.innerHTML = `
        <div class="address-fields">
            <label>Carrer: <input type="text" class="street"></label>
            <label>Ciutat: <input type="text" class="city"></label>
        </div>
        <button type="button" class="remove-address-btn" onclick="removeAddress(this)">Eliminar</button>
    `;
    document.getElementById("addresses-container").appendChild(div);
}

function removeAddress(button) {
    const addresses = document.querySelectorAll(".address");
    if (addresses.length <= 1) {
        alert("Cal que hi hagi almenys una adreça");
        return;
    }
    button.parentElement.remove();
}

document.getElementById("clientForm").addEventListener("submit", function(e) {
    e.preventDefault();

     // Netejar errors anteriors
    document.querySelectorAll(".error").forEach(div => div.textContent = "");

     const addressDivs = document.querySelectorAll(".address");

    const addresses = [];

    addressDivs.forEach(div => {
        const street = div.querySelector(".street").value.trim();
        const city = div.querySelector(".city").value.trim();

        if (street && city) {
            addresses.push({ street, city });
        }
    });

    const client = {
        name: document.getElementById("name").value.trim(),
        email: document.getElementById("email").value.trim(),
        addresses: addresses
    };

    fetch("http://localhost:8080/clients", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(client)
    })
    .then(async res => {
        if (!res.ok) {
            const errors = await res.json();
            // Mostrar errors
            Object.keys(errors).forEach(key => {
    if (key === "name") {
        document.getElementById("name-error").textContent = errors[key];
    } else if (key === "email") {
        document.getElementById("email-error").textContent = errors[key];
    } else if (key.startsWith("addresses")) {
        const match = key.match(/addresses\[(\d+)\]\.(\w+)/);
        if (match) {
            const index = parseInt(match[1]);
            const field = match[2];
            const div = document.querySelectorAll(".address")[index];
            if (field === "street") {
                let span = div.querySelector(".street-error");
                if(!span) {
                    span = document.createElement("div");
                    span.className = "error street-error";
                    div.querySelector(".address-fields").appendChild(span);
                }
                span.textContent = errors[key];
            } else if (field === "city") {
                let span = div.querySelector(".city-error");
                if(!span) {
                    span = document.createElement("div");
                    span.className = "error city-error";
                    div.querySelector(".address-fields").appendChild(span);
                }
                span.textContent = errors[key];
            }
        }
    }
});
        } else {
            const data = await res.json();
            document.getElementById("result").textContent = JSON.stringify(data, null, 2);
        }
    })
});