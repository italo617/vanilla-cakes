import {loadCart} from "./cartCommons.js";

const checkoutInformationElementId = "checkout-information";
const checkoutOrderElementId = "checkout-order";
const cartItemsElementId = "order-items";
const orderTotalElementId = "order-total";
const totalCakePriceClassName = "total_price";
const errorMessageDivId = "error-message-div";
const errorMessageParagraphId = "error-message-paragraph";

function showError(message) {
    document.getElementById(checkoutInformationElementId).hidden = true;
    document.getElementById(errorMessageDivId).hidden = false;
    document.getElementById(errorMessageParagraphId).textContent = message;
}

async function loadCartTable() {
    const cart = loadCart();

    if (Object.keys(cart).length === 0) {
        window.location.href = "cakes.html";
    }

    document.getElementById(cartItemsElementId).innerHTML = "";
    const orderItems = [];
    for (const [cakeId, quantity] of Object.entries(cart)) {
        const cake = await getCake(cakeId);
        if (!cake) {
            continue;
        }
        createCakeRow(cake, quantity);
        orderItems.push(createOrderEntry(cake, quantity));
    }
    updateCartTotal();
    registerPlaceOrderHandler(orderItems);
}

async function getCake(cakeId) {
    const response = await fetch(`/api/cakes/${cakeId}`)
    if (!response.ok) {
        showError("Could not load checkout page.");
        return;
    }

    return await response.json();
}

function updateCartTotal() {
    let cartTotal = 0.0;
    const totalPriceElements = Array.from(document.getElementsByClassName(totalCakePriceClassName));
    for (const totalPriceElement of totalPriceElements) {
        cartTotal += Number(totalPriceElement.dataset.totalPrice);
    }
    document.getElementById(orderTotalElementId).innerText = `$ ${cartTotal.toFixed(2)}`;
}

function createCakeRow(cake, quantity) {
    const tableRowElement = document.createElement("tr");

    const tableDataNameElement = document.createElement("td");
    tableDataNameElement.textContent = cake.name;
    tableRowElement.appendChild(tableDataNameElement);

    const tableDataUnitPriceElement = document.createElement("td");
    const cakeUnitPrice = Number(cake.price)
    tableDataUnitPriceElement.textContent = `$ ${cakeUnitPrice.toFixed(2)}`;
    tableRowElement.appendChild(tableDataUnitPriceElement);

    const tableDataQuantityElement = document.createElement("td");
    tableDataQuantityElement.textContent = quantity;
    tableRowElement.appendChild(tableDataQuantityElement);

    const tableDataTotalPriceElement = document.createElement("td");
    tableDataTotalPriceElement.classList.add(totalCakePriceClassName);
    tableDataTotalPriceElement.dataset.totalPrice = String(cakeUnitPrice * quantity);
    tableDataTotalPriceElement.innerText = `$ ${(Number(tableDataTotalPriceElement.dataset.totalPrice)).toFixed(2)}`;
    tableRowElement.appendChild(tableDataTotalPriceElement);

    document.getElementById(cartItemsElementId).append(tableRowElement)
}

function createOrderEntry(cake, quantity) {
    return {
        cakeId: cake.id,
        quantity: quantity,
        unitPrice: cake.price
    }
}

function registerPlaceOrderHandler(orderItems) {
    const form = document.getElementById(checkoutOrderElementId);

    form.addEventListener("submit", async (event) => {
        event.preventDefault();

        const formData = new FormData(event.target);

        const order = {
            orderItems: orderItems,
            clientName: formData.get('client-name'),
            fullAddress: formData.get('full-address'),
            paymentMethod: formData.get('payment-method')
        };

        const response = await fetch("/api/orders", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(order)
        });

        if (!response.ok) {
            alert("Could not place order.");
            return;
        }

        const createdOrder = await response.json();

        localStorage.removeItem("cart");
        sessionStorage.setItem("last-order", JSON.stringify(createdOrder));

        window.location.href = `orderSuccess.html`;
    });

}

await loadCartTable();