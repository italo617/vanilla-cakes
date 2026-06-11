import {loadCart, removeFromCart, setToCart, clearCart} from "./cartCommons.js";
import {createQuantityComponent} from "./quantityComponent.js";

const cartInformationElementId = "cartInformation";
const cartItemsElementId = "cartItems";
const cartTotalElementId = "cartTotal";
const totalCakePriceClassName = "total_price";
const clearCartTdElementId = "clearCartTd";
const errorMessageDivId = "errorMessageDiv";
const errorMessageParagraphId = "errorMessageParagraph";

function showError(message) {
    document.getElementById(cartInformationElementId).innerHTML = "";
    document.getElementById(cartInformationElementId).hidden = true;
    document.getElementById(errorMessageDivId).hidden = false;
    document.getElementById(errorMessageParagraphId).textContent = message;
}

async function loadCartTable() {
    const cart = loadCart();

    document.getElementById(cartItemsElementId).innerHTML = "";
    document.getElementById(clearCartTdElementId).innerHTML = "";
    for (const [cakeId, quantity] of Object.entries(cart)) {
        const cake = await getCake(cakeId);
        if (!cake) {
            continue;
        }
        createCakeRow(cake, quantity);
    }
    updateCartTotal();
    if (Object.entries(cart).length > 0) {
        createClearCartButton();
    }
}

async function getCake(cakeId) {
    const response = await fetch(`/api/cakes/${cakeId}`)
    if (!response.ok) {
        showError("Could not load cart");
        return;
    }

    return await response.json();
}

function getCakeTotalPriceElementId(cakeId) {
    return "total_price_cake_" + cakeId;
}

function updateTotalCakePrice(quantity, unitPrice, cakeId) {
    const totalCakePrice = unitPrice * quantity;
    const cakeTotalPriceElement = document.getElementById(getCakeTotalPriceElementId(cakeId));
    cakeTotalPriceElement.dataset.totalPrice = String(totalCakePrice);
    cakeTotalPriceElement.textContent = `$ ${totalCakePrice.toFixed(2)}`;
    setToCart(cakeId, quantity);
    updateCartTotal();
}

async function removeCake(cakeId) {
    removeFromCart(cakeId);
    await loadCartTable();
}

function updateCartTotal() {
    let cartTotal = 0.0;
    const totalPriceElements = Array.from(document.getElementsByClassName(totalCakePriceClassName));
    for (const totalPriceElement of totalPriceElements) {
        cartTotal += Number(totalPriceElement.dataset.totalPrice);
    }
    document.getElementById(cartTotalElementId).innerText = `$ ${cartTotal.toFixed(2)}`;
}

function createClearCartButton() {
    const clearCartTdElement = document.getElementById(clearCartTdElementId);

    const clearCartButton = document.createElement("button");
    clearCartButton.innerText = 'Clear Cart';
    clearCartButton.addEventListener("click", async () => {
        clearCart();
        await loadCartTable();
        clearCartButton.remove();
    })
    clearCartTdElement.appendChild(clearCartButton);
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
    const quantityComponent = createQuantityComponent(quantity, quantity => {
        updateTotalCakePrice(quantity, cake.price, cake.id);
    });
    tableDataQuantityElement.appendChild(quantityComponent.element);
    tableRowElement.appendChild(tableDataQuantityElement);

    const tableDataTotalPriceElement = document.createElement("td");
    tableDataTotalPriceElement.id = getCakeTotalPriceElementId(cake.id);
    tableDataTotalPriceElement.classList.add(totalCakePriceClassName);
    tableDataTotalPriceElement.dataset.totalPrice = String(cakeUnitPrice * quantity);
    tableDataTotalPriceElement.innerText = `$ ${(Number(tableDataTotalPriceElement.dataset.totalPrice)).toFixed(2)}`;
    tableRowElement.appendChild(tableDataTotalPriceElement);

    const tableDataRemoveCakeElement = document.createElement("td");
    const removeCakeElement = document.createElement("button");
    removeCakeElement.innerText = "Remove";
    removeCakeElement.addEventListener('click', () => removeCake(cake.id));
    tableDataRemoveCakeElement.appendChild(removeCakeElement);
    tableRowElement.appendChild(tableDataRemoveCakeElement);

    document.getElementById(cartItemsElementId).append(tableRowElement)
}

await loadCartTable();