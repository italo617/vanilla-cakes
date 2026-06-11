import { addToCart } from './cartCommons.js'
import { createQuantityComponent } from "./quantityComponent.js";

const cakeInformationElementId = "cakeInformation"
const cakeNameElementId = "cakeName";
const cakeDescriptionElementId = "cakeDescription";
const cakeUnitPriceElementId = "cakeUnitPrice";
const quantityContainerElementId = "quantityContainer";
const totalPriceElementId = "totalPrice";
const addToCartButtonElementId = "addToCartButton";
const errorMessageDivId = "errorMessageDiv";
const errorMessageParagraphId = "errorMessageParagraph";

function showError(message) {
    document.getElementById(cakeInformationElementId).hidden = true;
    document.getElementById(errorMessageDivId).hidden = false;
    document.getElementById(errorMessageParagraphId).textContent = message;
}

function updateTotalPrice(quantity, unitPrice) {
    const totalPrice = unitPrice * quantity;
    document.getElementById(totalPriceElementId).textContent = `$ ${totalPrice.toFixed(2)}`;
}

function handleAddToCart(cakeId, quantity) {
    addToCart(cakeId, quantity);
    window.location.href = "cart.html";
}

(async function loadCake() {

    const params = new URLSearchParams(window.location.search);

    const cakeIdString = params.get("id");

    const cakeId = Number(cakeIdString);

    if (!cakeIdString || Number.isNaN(cakeId)) {
        showError("Missing valid cake id");
        return;
    }

    try {
        const response = await fetch(`/api/cakes/${cakeId}`);
        if (!response.ok) {
            showError("Could not load cake");
            return;
        }

        const cake = await response.json();
        document.getElementById(cakeNameElementId).textContent = cake.name;
        document.getElementById(cakeDescriptionElementId).textContent = cake.description;
        document.getElementById(cakeUnitPriceElementId).textContent = `$ ${cake.price.toFixed(2)}`;

        const quantityComponent = createQuantityComponent(1, quantity => {
            updateTotalPrice(quantity, cake.price);
        });
        document.getElementById(quantityContainerElementId).appendChild(quantityComponent.element);

        updateTotalPrice(quantityComponent.getQuantity(), cake.price);

        document.getElementById(addToCartButtonElementId).addEventListener('click', () => {
            handleAddToCart(cakeId, quantityComponent.getQuantity());
        })
    } catch (error) {
        showError("Unexpected error");
        console.error(error);
    }
})();